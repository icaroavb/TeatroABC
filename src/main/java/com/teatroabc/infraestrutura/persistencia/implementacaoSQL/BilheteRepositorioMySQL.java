package com.teatroabc.infraestrutura.persistencia.implementacaoSQL;

import com.teatroabc.dominio.enums.CategoriaAssento;
import com.teatroabc.dominio.modelos.*;
import com.teatroabc.infraestrutura.config.ConfiguracaoPlantaTeatro;
import com.teatroabc.infraestrutura.persistencia.interfaces.IBilheteRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.IClienteRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.ISessaoRepositorio;
import com.teatroabc.infraestrutura.persistencia.util.ConexaoMySQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementação (Adaptador Secundário) do repositório de Bilhetes para MySQL.
 * Esta classe gerencia a persistência de objetos Bilhete, lidando com a complexidade
 * de salvar dados em múltiplas tabelas de forma atômica (usando transações)
 * e reconstruir objetos de domínio a partir de consultas ao banco.
 */
public class BilheteRepositorioMySQL implements IBilheteRepositorio {

    // Injeção de dependências de outros repositórios, necessária para reconstruir o objeto Bilhete.
    private final IClienteRepositorio clienteRepositorio;
    private final ISessaoRepositorio sessaoRepositorio;

    public BilheteRepositorioMySQL(IClienteRepositorio clienteRepositorio, ISessaoRepositorio sessaoRepositorio) {
        if (clienteRepositorio == null || sessaoRepositorio == null) {
            throw new IllegalArgumentException("Repositórios de Cliente e Sessão não podem ser nulos.");
        }
        this.clienteRepositorio = clienteRepositorio;
        this.sessaoRepositorio = sessaoRepositorio;
    }

    /**
     * {@inheritDoc}
     * Salva um Bilhete e seus assentos associados de forma transacional.
     * Garante que ou todas as inserções (bilhete e assentos) são bem-sucedidas, ou nenhuma é.
     */
    @Override
    public void salvar(Bilhete bilhete) {
        String sqlBilhete = "INSERT INTO bilhetes (id, codigo_barras, cliente_cpf, sessao_id, " +
                            "subtotal, valor_desconto, valor_total, data_hora_compra) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlAssento = "INSERT INTO bilhete_assentos (bilhete_id, assento_codigo) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = ConexaoMySQL.getConexao();
            // Desabilita o auto-commit para iniciar a transação.
            conn.setAutoCommit(false);

            // 1. Inserir o registro principal na tabela 'bilhetes'.
            try (PreparedStatement stmtBilhete = conn.prepareStatement(sqlBilhete)) {
                stmtBilhete.setString(1, bilhete.getId());
                stmtBilhete.setString(2, bilhete.getCodigoBarras());
                stmtBilhete.setString(3, bilhete.getCliente().getCpf());
                stmtBilhete.setString(4, bilhete.getSessao().getId());
                stmtBilhete.setBigDecimal(5, bilhete.getSubtotal());
                stmtBilhete.setBigDecimal(6, bilhete.getValorDesconto());
                stmtBilhete.setBigDecimal(7, bilhete.getValorTotal());
                stmtBilhete.setTimestamp(8, Timestamp.valueOf(bilhete.getDataHoraCompra()));
                stmtBilhete.executeUpdate();
            }

            // 2. Inserir os assentos na tabela de junção 'bilhete_assentos' em lote.
            try (PreparedStatement stmtAssento = conn.prepareStatement(sqlAssento)) {
                for (Assento assento : bilhete.getAssentos()) {
                    stmtAssento.setString(1, bilhete.getId());
                    stmtAssento.setString(2, assento.getCodigo());
                    stmtAssento.addBatch();
                }
                stmtAssento.executeBatch();
            }

            // Se todas as operações foram bem-sucedidas, efetiva a transação.
            conn.commit();

        } catch (SQLException e) {
            System.err.println("Erro na transação ao salvar bilhete. Realizando rollback.");
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erro crítico ao tentar realizar rollback da transação.");
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Falha ao salvar bilhete no banco de dados.", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Restaura o modo de auto-commit padrão.
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     * Busca todos os bilhetes de um cliente e os reconstrói.
     */
    @Override
    public List<Bilhete> listarPorCpfCliente(String cpf) {
        List<Bilhete> bilhetes = new ArrayList<>();
        // Primeiro, obtemos apenas os IDs dos bilhetes para evitar consultas complexas.
        String sql = "SELECT id FROM bilhetes WHERE cliente_cpf = ? ORDER BY data_hora_compra DESC";

        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String bilheteId = rs.getString("id");
                    // Reutilizamos a lógica de buscarPorId para montar cada bilhete.
                    // Nota: Esta é a abordagem N+1. É clara e funcional, mas para um volume
                    // muito alto de bilhetes por cliente, uma otimização com um JOIN complexo seria necessária.
                    buscarPorId(bilheteId).ifPresent(bilhetes::add);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao listar bilhetes por CPF.", e);
        }
        return bilhetes;
    }

    /**
     * {@inheritDoc}
     * Busca um bilhete pelo seu ID e reconstrói o objeto de domínio completo,
     * buscando suas dependências (Cliente, Sessao, Assentos).
     */
    @Override
    public Optional<Bilhete> buscarPorId(String id) {
        String sql = "SELECT * FROM bilhetes WHERE id = ?";
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToBilhete(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar bilhete por ID.", e);
        }
        return Optional.empty();
    }

    /**
     * Mapeador principal que converte uma linha do ResultSet da tabela 'bilhetes'
     * em um objeto de domínio 'Bilhete' completo.
     * @param rs O ResultSet posicionado na linha correta.
     * @return O objeto Bilhete reconstruído.
     * @throws SQLException Se ocorrer um erro de acesso ao ResultSet.
     */
    private Bilhete mapRowToBilhete(ResultSet rs) throws SQLException {
        String bilheteId = rs.getString("id");
        String clienteCpf = rs.getString("cliente_cpf");
        String sessaoId = rs.getString("sessao_id");

        // Busca as entidades relacionadas usando seus repositórios.
        Cliente cliente = clienteRepositorio.buscarPorCpf(clienteCpf)
                .orElseThrow(() -> new IllegalStateException("Inconsistência de dados: Cliente não encontrado para o Bilhete ID: " + bilheteId));
        Sessao sessao = sessaoRepositorio.buscarPorId(sessaoId)
                .orElseThrow(() -> new IllegalStateException("Inconsistência de dados: Sessão não encontrada para o Bilhete ID: " + bilheteId));

        // Busca a lista de assentos associados a este bilhete.
        List<Assento> assentos = buscarAssentosDoBilhete(bilheteId);

        // Constrói e retorna o objeto Bilhete.
        return new Bilhete(
                bilheteId,
                rs.getString("codigo_barras"),
                sessao,
                cliente,
                assentos,
                rs.getBigDecimal("subtotal"),
                rs.getBigDecimal("valor_desconto"),
                rs.getBigDecimal("valor_total"),
                rs.getTimestamp("data_hora_compra").toLocalDateTime()
        );
    }

    /**
     * Método auxiliar para buscar os códigos dos assentos de um bilhete na
     * tabela de junção e reconstruí-los como objetos de domínio.
     * @param bilheteId O ID do bilhete.
     * @return Uma lista de objetos Assento.
     * @throws SQLException Se ocorrer um erro na consulta.
     */
    private List<Assento> buscarAssentosDoBilhete(String bilheteId) throws SQLException {
        List<Assento> assentos = new ArrayList<>();
        String sql = "SELECT assento_codigo FROM bilhete_assentos WHERE bilhete_id = ?";
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bilheteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    assentos.add(reconstruirAssentoPorCodigo(rs.getString("assento_codigo")));
                }
            }
        }
        return assentos;
    }

    /**
     * Reconstrói um objeto Assento a partir de seu código (ex: 'C4-5').
     * Usa a configuração da planta do teatro para determinar a categoria e o preço.
     * Este método não acessa o banco de dados.
     * @param codigo O código do assento.
     * @return O objeto Assento reconstruído.
     */
    private Assento reconstruirAssentoPorCodigo(String codigo) {
        char prefixo = codigo.charAt(0);
        String[] partesNum = codigo.substring(1).split("-");
        int fileira = Integer.parseInt(partesNum[0]);
        int numero = Integer.parseInt(partesNum[1]);

        CategoriaAssento categoria = ConfiguracaoPlantaTeatro.getLayout().getSecoes()
                .stream()
                .filter(secao -> secao.getNomeDaSecao().charAt(0) == prefixo)
                .map(secao -> secao.getCategoria())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Configuração de planta de teatro inconsistente. Não foi possível encontrar a categoria para o código: " + codigo));
        
        return new Assento(codigo, fileira, numero, categoria, categoria.getPrecoBase());
    }
}