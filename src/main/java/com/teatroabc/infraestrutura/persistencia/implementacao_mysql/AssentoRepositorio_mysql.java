package com.teatroabc.infraestrutura.persistencia.implementacao_mysql;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.teatroabc.dominio.enums.CategoriaAssento;
import com.teatroabc.dominio.enums.StatusAssento;
import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.infraestrutura.config.ConfiguracaoPlantaTeatro;
import com.teatroabc.infraestrutura.config.SecaoConfig;
import com.teatroabc.infraestrutura.config.TeatroLayoutConfig;
import com.teatroabc.infraestrutura.persistencia.interfaces.IAssentoRepositorio;
import com.teatroabc.infraestrutura.persistencia.util.ConexaoMySQL;

public class AssentoRepositorio_mysql implements IAssentoRepositorio {

    @Override
    public List<Assento> buscarTodosAssentosPorPecaETurno(String idPeca, Turno turno) {
        if (idPeca == null || turno == null) {
            return Collections.emptyList();
        }

        List<Assento> todosAssentos = new ArrayList<>();
        Set<String> ocupados = buscarAssentosOcupados(idPeca, turno);

        TeatroLayoutConfig layout = ConfiguracaoPlantaTeatro.getLayout();

        for (SecaoConfig secao : layout.getSecoes()) {
            char prefixo = secao.getNomeDaSecao().charAt(0);
            CategoriaAssento categoria = secao.getCategoria();
            BigDecimal preco = categoria.getPrecoBase();

            for (int fileira = 1; fileira <= secao.getNumeroDeFileiras(); fileira++) {
                for (int numero = 1; numero <= secao.getAssentosPorFileira(); numero++) {

                    String codigo = String.format("%c%d-%d", prefixo, fileira, numero);

                    Assento assento = new Assento(codigo, fileira, numero, categoria, preco);

                    if (ocupados.contains(codigo)) {
                        assento.setStatus(StatusAssento.OCUPADO);
                    } else {
                        assento.setStatus(StatusAssento.DISPONIVEL);
                    }

                    todosAssentos.add(assento);
                }
            }
        }

        return todosAssentos;
    }

    @Override
    public boolean verificarDisponibilidade(String idPeca, Turno turno, List<String> codigosAssentos) {
        if (idPeca == null || turno == null) return false;
        if (codigosAssentos == null || codigosAssentos.isEmpty()) return true;

        Set<String> ocupados = buscarAssentosOcupados(idPeca, turno);

        for (String codigo : codigosAssentos) {
            if (ocupados.contains(codigo)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Reserva os assentos, persistindo no banco.
     */
    public void reservarAssentos(String idPeca, Turno turno, List<String> codigosAssentos) {
        String sql = "INSERT INTO ocupacao_assento (id_peca, turno, codigo_assento) VALUES (?, ?, ?)";

        try (Connection conn = ConexaoMySQL.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (String codigo : codigosAssentos) {
                stmt.setString(1, idPeca);
                stmt.setString(2, turno.name());
                stmt.setString(3, codigo);
                stmt.addBatch();
            }

            stmt.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao reservar assentos: " + e.getMessage());
        }
    }

    /**
     * Libera os assentos, removendo a ocupação do banco.
     */
    public void liberarAssentos(String idPeca, Turno turno, List<String> codigosAssentos) {
        String sql = "DELETE FROM ocupacao_assento WHERE id_peca = ? AND turno = ? AND codigo_assento = ?";

        try (Connection conn = ConexaoMySQL.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (String codigo : codigosAssentos) {
                stmt.setString(1, idPeca);
                stmt.setString(2, turno.name());
                stmt.setString(3, codigo);
                stmt.addBatch();
            }

            stmt.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao liberar assentos: " + e.getMessage());
        }
    }

    /**
     * Busca no banco os códigos dos assentos ocupados para uma peça e turno.
     */
    private Set<String> buscarAssentosOcupados(String idPeca, Turno turno) {
        Set<String> ocupados = new HashSet<>();

        String sql = "SELECT codigo_assento FROM ocupacao_assento WHERE id_peca = ? AND turno = ?";

        try (Connection conn = ConexaoMySQL.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idPeca);
            stmt.setString(2, turno.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ocupados.add(rs.getString("codigo_assento"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar assentos ocupados: " + e.getMessage());
        }

        return ocupados;
    }
}
