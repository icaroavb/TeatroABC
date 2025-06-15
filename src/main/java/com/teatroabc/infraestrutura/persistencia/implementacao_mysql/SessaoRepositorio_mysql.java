package com.teatroabc.infraestrutura.persistencia.implementacao_mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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

/**
 * Implementação do repositório de Sessão usando MySQL.
 */
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
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, idPeca);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Sessao sessao = mapearResultSetParaSessao(rs);
                sessoes.add(sessao);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sessoes;
    }

    @Override
    public Optional<Sessao> buscarPorId(String idSessao) {
        String sql = "SELECT * FROM sessao WHERE id_sessao = ?";

        try (Connection conn = ConexaoMySQL.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, idSessao);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapearResultSetParaSessao(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Insere uma nova sessão no banco.
     */
    public void inserir(Sessao sessao) {
        String sql = "INSERT INTO sessao (id_sessao, id_peca, data_hora, turno) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexaoMySQL.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sessao.getId());
            ps.setString(2, sessao.getPeca().getId());
            ps.setTimestamp(3, Timestamp.valueOf(sessao.getDataHora()));
            ps.setString(4, sessao.getTurno().name());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Atualiza uma sessão existente.
     */
    public void atualizar(Sessao sessao) {
        String sql = "UPDATE sessao SET id_peca = ?, data_hora = ?, turno = ? WHERE id_sessao = ?";

        try (Connection conn = ConexaoMySQL.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sessao.getPeca().getId());
            ps.setTimestamp(2, Timestamp.valueOf(sessao.getDataHora()));
            ps.setString(3, sessao.getTurno().name());
            ps.setString(4, sessao.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove uma sessão pelo ID.
     */
    public void remover(String idSessao) {
        String sql = "DELETE FROM sessao WHERE id_sessao = ?";

        try (Connection conn = ConexaoMySQL.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, idSessao);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mapeia um ResultSet para um objeto Sessao.
     */
    private Sessao mapearResultSetParaSessao(ResultSet rs) throws SQLException {
        String id = rs.getString("id_sessao");
        String idPeca = rs.getString("id_peca");
        Timestamp ts = rs.getTimestamp("data_hora");
        String turnoStr = rs.getString("turno");

        // Busca a peça para esta sessão
        Optional<Peca> pecaOpt = pecaRepositorio.buscarPorId(idPeca);
        Peca peca = pecaOpt.orElseThrow(() -> new SQLException("Peça não encontrada para sessão: " + id));

        LocalDateTime dataHora = ts.toLocalDateTime();
        Turno turno = Turno.valueOf(turnoStr);

        return new Sessao(id, peca, dataHora, turno);
    }
}
