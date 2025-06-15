package com.teatroabc.infraestrutura.persistencia.implementacao_mysql;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.infraestrutura.persistencia.interfaces.IClienteRepositorio;
import com.teatroabc.infraestrutura.persistencia.util.ConexaoMySQL;
import com.teatroabc.infraestrutura.persistencia.util.GerenciadorArquivos;

/**
 * Implementação (Adaptador Secundário) do repositório de Clientes.
 * Responsável por traduzir objetos Cliente para o formato de persistência
 * em arquivo de texto e vice-versa.
 */
public class ClienteRepositorio_mySql implements IClienteRepositorio {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * {@inheritDoc}
     */
    @Override
    public void salvar(Cliente cliente) {
        String sql = "INSERT INTO cliente (cpf, nome, telefone, email, dataNasc, abcGold) VALUES (?, ?, ?, ?, ?, ?)";
        

        try (Connection conn = ConexaoMySQL.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getCpf());
            stmt.setString(2, cliente.getNome());
            stmt.setString(3, cliente.getTelefone());
            stmt.setString(4, cliente.getEmail());
            stmt.setDate(5, java.sql.Date.valueOf(cliente.getDataNascimento()));
            stmt.setString(6, cliente.getPlanoFidelidade().getIdentificadorPlano());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir cliente: " + e.getMessage(), e);
        }
    }
    
    
    @Override
    public Optional<Cliente> buscarPorCpf(String cpf) {
        String sql = "SELECT * FROM cliente WHERE cpf = ?";

        try (Connection conn = ConexaoMySQL.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Cliente cliente = mapearResultSetParaCliente(rs);
                return Optional.of(cliente);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cliente por CPF: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public boolean existe(String cpf) {
        return GerenciadorArquivos.buscarClientePorCpf(cpf) != null;
    }

    // --- Método Auxiliar ---
    private Cliente mapearResultSetParaCliente(ResultSet rs) throws SQLException {
        String cpf = rs.getString("cpf");
        String nome = rs.getString("nome");
        String telefone = rs.getString("telefone");
        String email = rs.getString("email");
        LocalDate dataNascimento = rs.getDate("dataNasc").toLocalDate();
        String identificadorPlano = rs.getString("abcGold");

        return new Cliente(cpf, nome, dataNascimento, telefone, email, identificadorPlano);
    }

}