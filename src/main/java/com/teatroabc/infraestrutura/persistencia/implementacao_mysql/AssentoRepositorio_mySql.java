package com.teatroabc.infraestrutura.persistencia.implementacao_mysql;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.teatroabc.dominio.enums.CategoriaAssento;
import com.teatroabc.dominio.enums.StatusAssento;
import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.infraestrutura.config.ConfiguracaoPlantaTeatro;
import com.teatroabc.infraestrutura.persistencia.interfaces.IAssentoRepositorio;
import com.teatroabc.infraestrutura.persistencia.util.ConexaoMySQL;

/**
 * Implementação do repositório de assentos que interage com a persistência em arquivos.
 * Esta classe é responsável por gerar a planta completa de assentos do teatro e verificar sua disponibilidade.
 * 
 * REFATORAÇÃO: Esta classe não contém mais lógica de layout hardcoded. Ela obtém a
 * estrutura do teatro (seções, fileiras, assentos por fileira) da classe
 * ConfiguracaoPlantaTeatro, agindo como um consumidor de configuração.
 * Isso desacopla a lógica de persistência da lógica de layout físico do teatro,
 * aderindo ao princípio de Fonte Única de Verdade (Single Source of Truth).
 */
public class AssentoRepositorio_mySql implements IAssentoRepositorio {

    /**
     * {@inheritDoc}
     * Gera dinamicamente a lista de todos os assentos para uma peça e turno,
     * baseando-se na configuração centralizada em {@link ConfiguracaoPlantaTeatro}.
     * O status de cada assento (Disponível/Ocupado) é definido consultando os
     * dados de persistência.
     */
    @Override
    public List<Assento> buscarTodosAssentosPorPecaETurno(String idPeca, Turno turno) {
         List<Assento> assentos = new ArrayList<>();
        String sql = "SELECT * FROM assento";

        try (Connection conn = ConexaoMySQL.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Assento assento = mapearResultSetParaAssento(rs);
                assentos.add(assento);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return assentos;
    }
    /**
     * {@inheritDoc}
     * Esta implementação não precisa ser alterada, pois sua lógica é independente
     * da geração da planta do teatro. Ela apenas verifica uma lista de códigos
     * contra os dados de persistência.
     */
    @Override
    public boolean verificarDisponibilidade(String idPeca, Turno turno, List<String> codigosAssentos) {
        if (codigosAssentos == null || codigosAssentos.isEmpty()) return true;

        String placeholders = String.join(",", Collections.nCopies(codigosAssentos.size(), "?"));
        String sql = "SELECT codigo FROM assento WHERE codigo IN (" + placeholders + ") AND status = 'OCUPADO'";

        try (Connection conn = ConexaoMySQL.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < codigosAssentos.size(); i++) {
                ps.setString(i + 1, codigosAssentos.get(i));
            }

            ResultSet rs = ps.executeQuery();
            return !rs.next(); // Se não encontrar nenhum ocupado, está disponível.

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }




    public void inserir(Assento assento) {
        String sql = "INSERT INTO assento (codigo, fileira, numero, categoria, preco, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoMySQL.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, assento.getCodigo());
            ps.setInt(2, assento.getFileira());
            ps.setInt(3, assento.getNumero());
            ps.setString(4, assento.getCategoria().name());
            ps.setBigDecimal(5, assento.getPreco());
            ps.setString(6, assento.getStatus().name());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Atualiza o status de um assento (ex: DISPONIVEL, OCUPADO).
     */
    public void atualizarStatus(String codigo, StatusAssento status) {
        String sql = "UPDATE assento SET status = ? WHERE codigo = ?";

        try (Connection conn = ConexaoMySQL.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setString(2, codigo);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove um assento do banco (caso necessário).
     */
    public void remover(String codigo) {
        String sql = "DELETE FROM assento WHERE codigo = ?";

        try (Connection conn = ConexaoMySQL.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, codigo);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Busca um assento pelo código.
     */
    public Optional<Assento> buscarPorCodigo(String codigo) {
        String sql = "SELECT * FROM assento WHERE codigo = ?";

        try (Connection conn = ConexaoMySQL.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapearResultSetParaAssento(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Mapeia um ResultSet para um objeto Assento.
     */
    private Assento mapearResultSetParaAssento(ResultSet rs) throws SQLException {
        String codigo = rs.getString("codigo");
        int fileira = rs.getInt("fileira");
        int numero = rs.getInt("numero");
        CategoriaAssento categoria = CategoriaAssento.valueOf(rs.getString("categoria"));
        BigDecimal preco = rs.getBigDecimal("preco");
        StatusAssento status = StatusAssento.valueOf(rs.getString("status"));

        Assento assento = new Assento(codigo, fileira, numero, categoria, preco);
        assento.setStatus(status);

        return assento;
    }
}
