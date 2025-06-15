package com.teatroabc.infraestrutura.persistencia.implementacao_mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.dominio.modelos.Sessao;
import com.teatroabc.infraestrutura.persistencia.interfaces.IPecaRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.ISessaoRepositorio;
import com.teatroabc.infraestrutura.persistencia.util.ConexaoMySQL;

public class SessaoRepositorio_mysql implements ISessaoRepositorio {

    private final IPecaRepositorio pecaRepositorio;

    public SessaoRepositorio_mysql(IPecaRepositorio pecaRepositorio) {
        this.pecaRepositorio = pecaRepositorio;
    }

    @Override
    public List<Sessao> buscarSessoesPorPeca(String idPeca) {
        List<Sessao> sessoes = new ArrayList<>();

        String sql = "SELECT * FROM sessao WHERE id_peca = ?";

        try (Connection conn = ConexaoMySQL.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idPeca);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Sessao sessao = mapearResultSetParaSessao(rs);
                    sessoes.add(sessao);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar sessões por peça: " + e.getMessage());
        }

        return sessoes;
    }

    @Override
    public Optional<Sessao> buscarPorId(String idSessao) {
        String sql = "SELECT * FROM sessao WHERE id = ?";

        try (Connection conn = ConexaoMySQL.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idSessao);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Sessao sessao = mapearResultSetParaSessao(rs);
                    return Optional.of(sessao);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar sessão por ID: " + e.getMessage());
        }

        return Optional.empty();
    }

    /**
     * Mapeia uma linha do ResultSet para um objeto Sessao
     */
    private Sessao mapearResultSetParaSessao(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String idPeca = rs.getString("id_peca");
        LocalDateTime dataHora = rs.getTimestamp("data_hora").toLocalDateTime();
        Turno turno = Turno.valueOf(rs.getString("turno"));

        // Busca a peça associada
        Optional<Peca> pecaOpt = pecaRepositorio.buscarPorId(idPeca);
        if (pecaOpt.isEmpty()) {
            throw new RuntimeException("Peça com ID " + idPeca + " não encontrada.");
        }

        return new Sessao(id, pecaOpt.get(), dataHora, turno);
    }
}
