package com.teatroabc.infraestrutura.persistencia.implementacaoSQL;

import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.infraestrutura.persistencia.interfaces.IPecaRepositorio;
import com.teatroabc.infraestrutura.persistencia.util.ConexaoMySQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PecaRepositorioMySQL implements IPecaRepositorio {

    @Override
    public List<Peca> listarTodas() {
        List<Peca> pecas = new ArrayList<>();
        String sql = "SELECT * FROM pecas";

        try (Connection conn = ConexaoMySQL.getConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pecas.add(mapRowToPeca(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao listar todas as peças.", e);
        }
        return pecas;
    }

    @Override
    public Optional<Peca> buscarPorId(String id) {
        String sql = "SELECT * FROM pecas WHERE id = ?";
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToPeca(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar peça por ID.", e);
        }
        return Optional.empty();
    }

    /**
     * Mapeia uma linha do ResultSet para um objeto Peca.
     */
    private Peca mapRowToPeca(ResultSet rs) throws SQLException {
        return new Peca(
                rs.getString("id"),
                rs.getString("titulo"),
                rs.getString("subtitulo"),
                rs.getString("descricao"),
                rs.getString("cor_fundo_hex"),
                rs.getString("caminho_imagem")
        );
    }
}