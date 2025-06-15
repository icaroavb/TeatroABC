package com.teatroabc.infraestrutura.persistencia.implementacaoSQL;

import com.teatroabc.dominio.enums.CategoriaAssento;
import com.teatroabc.dominio.enums.StatusAssento;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.modelos.Sessao;
import com.teatroabc.infraestrutura.config.ConfiguracaoPlantaTeatro;
import com.teatroabc.infraestrutura.config.SecaoConfig;
import com.teatroabc.infraestrutura.config.TeatroLayoutConfig;
import com.teatroabc.infraestrutura.persistencia.interfaces.IAssentoRepositorio;
import com.teatroabc.infraestrutura.persistencia.util.ConexaoMySQL;

import java.sql.*;
import java.util.*;

public class AssentoRepositorioMySQL implements IAssentoRepositorio {

    @Override
    public List<Assento> buscarAssentosPorSessao(Sessao sessao) {
        if (sessao == null) {
            return Collections.emptyList();
        }

        List<Assento> todosAssentos = new ArrayList<>();
        // 1. Busca os códigos de assentos já ocupados para esta SESSÃO específica no banco.
        Set<String> codigosOcupados = buscarAssentosOcupados(sessao.getId());

        // 2. Obtém a configuração da planta do teatro (in-memory).
        TeatroLayoutConfig layout = ConfiguracaoPlantaTeatro.getLayout();

        // 3. Itera sobre a CONFIGURAÇÃO para gerar a planta de assentos.
        for (SecaoConfig secaoConfig : layout.getSecoes()) {
            char prefixoCodigo = secaoConfig.getNomeDaSecao().charAt(0);
            CategoriaAssento categoria = secaoConfig.getCategoria();

            for (int numFileira = 1; numFileira <= secaoConfig.getNumeroDeFileiras(); numFileira++) {
                for (int numAssento = 1; numAssento <= secaoConfig.getAssentosPorFileira(); numAssento++) {
                    String codigoAssento = String.format("%c%d-%d", prefixoCodigo, numFileira, numAssento);
                    Assento assento = new Assento(codigoAssento, numFileira, numAssento, categoria, categoria.getPrecoBase());
                    
                    // 4. Define o status do assento: Ocupado se o código estiver no set de ocupados.
                    if (codigosOcupados.contains(codigoAssento)) {
                        assento.setStatus(StatusAssento.OCUPADO);
                    }
                    todosAssentos.add(assento);
                }
            }
        }
        return todosAssentos;
    }

    @Override
    public boolean verificarDisponibilidade(Sessao sessao, List<String> codigosAssentos) {
        if (sessao == null || codigosAssentos == null || codigosAssentos.isEmpty()) {
            return true; // Nenhum assento a verificar, então estão "disponíveis".
        }
        Set<String> codigosOcupados = buscarAssentosOcupados(sessao.getId());
        for (String codigo : codigosAssentos) {
            if (codigosOcupados.contains(codigo)) {
                return false; // Encontrou um assento que já está ocupado.
            }
        }
        return true;
    }
    
    /**
     * Método auxiliar que consulta o banco para obter os códigos dos assentos
     * ocupados para uma sessão.
     */
    private Set<String> buscarAssentosOcupados(String idSessao) {
        Set<String> assentosOcupados = new HashSet<>();
        String sql = "SELECT ba.assento_codigo " +
                     "FROM bilhete_assentos ba " +
                     "JOIN bilhetes b ON ba.bilhete_id = b.id " +
                     "WHERE b.sessao_id = ?";

        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idSessao);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    assentosOcupados.add(rs.getString("assento_codigo"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar assentos ocupados para a sessão.", e);
        }
        return assentosOcupados;
    }
}