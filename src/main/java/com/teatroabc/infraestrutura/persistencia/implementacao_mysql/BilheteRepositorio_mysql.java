package com.teatroabc.infraestrutura.persistencia.implementacao_mysql;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.teatroabc.dominio.enums.CategoriaAssento;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.modelos.Bilhete;
import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.dominio.modelos.Sessao;
import com.teatroabc.infraestrutura.persistencia.interfaces.IBilheteRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.IClienteRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.ISessaoRepositorio;
import com.teatroabc.infraestrutura.persistencia.util.ConexaoMySQL;

public class BilheteRepositorio_mysql implements IBilheteRepositorio {

    private final IClienteRepositorio clienteRepositorio;
    private final ISessaoRepositorio sessaoRepositorio;

    public BilheteRepositorio_mysql(IClienteRepositorio clienteRepositorio, ISessaoRepositorio sessaoRepositorio) {
        this.clienteRepositorio = clienteRepositorio;
        this.sessaoRepositorio = sessaoRepositorio;
    }

    @Override
    public void salvar(Bilhete bilhete) {
        String sqlBilhete = "INSERT INTO bilhete (id, codigo_barras, cpf_cliente, id_sessao, subtotal, valor_desconto, valor_total, data_hora_compra) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlBilheteAssento = "INSERT INTO bilhete_assento (id_bilhete, codigo_assento) VALUES (?, ?)";

        try (Connection conn = ConexaoMySQL.conectar()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtBilhete = conn.prepareStatement(sqlBilhete);
                 PreparedStatement stmtAssento = conn.prepareStatement(sqlBilheteAssento)) {

                // Inserir bilhete
                stmtBilhete.setString(1, bilhete.getId());
                stmtBilhete.setString(2, bilhete.getCodigoBarras());
                stmtBilhete.setString(3, bilhete.getCliente().getCpf());
                stmtBilhete.setString(4, bilhete.getSessao().getId());
                stmtBilhete.setBigDecimal(5, bilhete.getSubtotal());
                stmtBilhete.setBigDecimal(6, bilhete.getValorDesconto());
                stmtBilhete.setBigDecimal(7, bilhete.getValorTotal());
                stmtBilhete.setTimestamp(8, Timestamp.valueOf(bilhete.getDataHoraCompra()));

                stmtBilhete.executeUpdate();

                // Inserir assentos
                for (Assento assento : bilhete.getAssentos()) {
                    stmtAssento.setString(1, bilhete.getId());
                    stmtAssento.setString(2, assento.getCodigo());
                    stmtAssento.addBatch();
                }
                stmtAssento.executeBatch();

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Erro ao salvar bilhete: " + e.getMessage(), e);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro de conexão ao salvar bilhete: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Bilhete> listarPorCpfCliente(String cpf) {
        List<Bilhete> bilhetes = new ArrayList<>();
        String sql = "SELECT * FROM bilhete WHERE cpf_cliente = ?";

        try (Connection conn = ConexaoMySQL.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Bilhete bilhete = mapearBilhete(rs, conn);
                bilhetes.add(bilhete);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar bilhetes: " + e.getMessage(), e);
        }

        return bilhetes;
    }

    @Override
    public Optional<Bilhete> buscarPorId(String id) {
        String sql = "SELECT * FROM bilhete WHERE id = ?";

        try (Connection conn = ConexaoMySQL.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Bilhete bilhete = mapearBilhete(rs, conn);
                return Optional.of(bilhete);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar bilhete por ID: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    // 🔧 Método auxiliar para mapear o bilhete
    private Bilhete mapearBilhete(ResultSet rs, Connection conn) throws SQLException {
        String id = rs.getString("id");
        String codigoBarras = rs.getString("codigo_barras");
        String cpfCliente = rs.getString("cpf_cliente");
        String idSessao = rs.getString("id_sessao");
        BigDecimal subtotal = rs.getBigDecimal("subtotal");
        BigDecimal valorDesconto = rs.getBigDecimal("valor_desconto");
        BigDecimal valorTotal = rs.getBigDecimal("valor_total");
        LocalDateTime dataHoraCompra = rs.getTimestamp("data_hora_compra").toLocalDateTime();

        Cliente cliente = clienteRepositorio.buscarPorCpf(cpfCliente)
                .orElseThrow(() -> new SQLException("Cliente não encontrado: " + cpfCliente));

        Sessao sessao = sessaoRepositorio.buscarPorId(idSessao)
                .orElseThrow(() -> new SQLException("Sessão não encontrada: " + idSessao));

        // Buscar assentos
        List<Assento> assentos = buscarAssentosPorBilhete(id, conn);

        return new Bilhete(id, codigoBarras, sessao, cliente, assentos,
                subtotal, valorDesconto, valorTotal, dataHoraCompra);
    }

    private List<Assento> buscarAssentosPorBilhete(String idBilhete, Connection conn) throws SQLException {
        List<Assento> assentos = new ArrayList<>();
        String sql = "SELECT codigo_assento FROM bilhete_assento WHERE id_bilhete = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idBilhete);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String codigo = rs.getString("codigo_assento");
                // Mapear o código para Assento (regras simplificadas)
                CategoriaAssento categoria = determinarCategoriaPeloCodigo(codigo);
                String[] partes = codigo.substring(1).split("-");
                int fileira = Integer.parseInt(partes[0]);
                int numero = Integer.parseInt(partes[1]);

                Assento assento = new Assento(codigo, fileira, numero, categoria, categoria.getPrecoBase());
                assentos.add(assento);
            }
        }

        return assentos;
    }

    private CategoriaAssento determinarCategoriaPeloCodigo(String codigo) {
        char prefixo = codigo.charAt(0);
        switch (prefixo) {
            case 'F':
                return CategoriaAssento.FRISA;
            case 'B':
                return CategoriaAssento.BALCAO_NOBRE;
            case 'P':
                return CategoriaAssento.PLATEIA_B;
            case 'C':
                return CategoriaAssento.CAMAROTE;
            default:
                return CategoriaAssento.PLATEIA_B;
        }
        
    }
}
