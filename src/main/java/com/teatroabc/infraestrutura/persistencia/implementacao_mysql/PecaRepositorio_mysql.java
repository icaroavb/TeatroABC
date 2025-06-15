package com.teatroabc.infraestrutura.persistencia.implementacao_mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.infraestrutura.persistencia.interfaces.IPecaRepositorio;
import com.teatroabc.infraestrutura.persistencia.util.ConexaoMySQL;

public class PecaRepositorio_mysql implements IPecaRepositorio {

    @Override
    public List<Peca> listarTodas() {
        List<Peca> pecas = new ArrayList<>();
        String sql = "SELECT * FROM peca";

        try (Connection conn = ConexaoMySQL.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                pecas.add(mapearResultSetParaPeca(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pecas;
    }

    @Override
    public Optional<Peca> buscarPorId(String id) {
        String sql = "SELECT * FROM peca WHERE id_peca = ?";

        try (Connection conn = ConexaoMySQL.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapearResultSetParaPeca(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }


    private Peca mapearResultSetParaPeca(ResultSet rs) throws SQLException {
        String id = rs.getString("id_peca");
        String titulo = rs.getString("Titulo");
        String subtitulo = rs.getString("Subtitulo");
        String descricao = rs.getString("Descricao");
        String cor = rs.getString("CorFundoHex");
        String caminhoImg = rs.getString("CaminhoImg");

        return new Peca(id, titulo, subtitulo, descricao, cor, caminhoImg);
    }
}
