package com.teatroabc.infraestrutura.persistencia.implementacaoSQL;

import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.dominio.modelos.Sessao;
import com.teatroabc.infraestrutura.persistencia.interfaces.ISessaoRepositorio;
import com.teatroabc.infraestrutura.persistencia.util.ConexaoMySQL;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SessaoRepositorioMySQL implements ISessaoRepositorio {

    @Override
    public List<Sessao> buscarSessoesPorPeca(String idPeca) {
        List<Sessao> sessoes = new ArrayList<>();
        // Usamos JOIN para buscar dados da peça e da sessão em uma única consulta
        String sql = "SELECT s.id AS sessao_id, s.data_hora, s.turno, " +
                     "p.id AS peca_id, p.titulo, p.subtitulo, p.descricao, p.cor_fundo_hex, p.caminho_imagem " +
                     "FROM sessoes s " +
                     "JOIN pecas p ON s.peca_id = p.id " +
                     "WHERE s.peca_id = ?";

        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idPeca);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // 1. Mapeia a parte da Peça
                    Peca peca = new Peca(
                            rs.getString("peca_id"),
                            rs.getString("titulo"),
                            rs.getString("subtitulo"),
                            rs.getString("descricao"),
                            rs.getString("cor_fundo_hex"),
                            rs.getString("caminho_imagem")
                    );
                    // 2. Mapeia a Sessão usando a Peça criada
                    Sessao sessao = new Sessao(
                            rs.getString("sessao_id"),
                            peca,
                            rs.getObject("data_hora", LocalDateTime.class),
                            Turno.valueOf(rs.getString("turno"))
                    );
                    sessoes.add(sessao);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar sessões por peça.", e);
        }
        return sessoes;
    }

     @Override
    public Optional<Sessao> buscarPorId(String idSessao) {
        if (idSessao == null || idSessao.trim().isEmpty()) {
            return Optional.empty();
        }

        String sql = "SELECT s.id AS sessao_id, s.data_hora, s.turno, " +
                     "p.id AS peca_id, p.titulo, p.subtitulo, p.descricao, p.cor_fundo_hex, p.caminho_imagem " +
                     "FROM sessoes s " +
                     "JOIN pecas p ON s.peca_id = p.id " +
                     "WHERE s.id = ?";

        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idSessao);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Peca peca = new Peca(
                            rs.getString("peca_id"),
                            rs.getString("titulo"),
                            rs.getString("subtitulo"),
                            rs.getString("descricao"),
                            rs.getString("cor_fundo_hex"),
                            rs.getString("caminho_imagem")
                    );
                    Sessao sessao = new Sessao(
                            rs.getString("sessao_id"),
                            peca,
                            rs.getObject("data_hora", LocalDateTime.class),
                            Turno.valueOf(rs.getString("turno"))
                    );
                    return Optional.of(sessao);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar sessão por ID.", e);
        }
        return Optional.empty();
    }
}