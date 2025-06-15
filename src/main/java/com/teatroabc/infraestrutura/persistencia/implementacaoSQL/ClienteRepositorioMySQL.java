package com.teatroabc.infraestrutura.persistencia.implementacaoSQL;

import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.infraestrutura.persistencia.interfaces.IClienteRepositorio;
import com.teatroabc.infraestrutura.persistencia.util.ConexaoMySQL; // Nossa nova classe de conexão

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

public class ClienteRepositorioMySQL implements IClienteRepositorio {

    @Override
    public void salvar(Cliente cliente) {
        // SQL para inserir ou atualizar um cliente (ON DUPLICATE KEY UPDATE)
        String sql = "INSERT INTO clientes (cpf, nome, data_nascimento, telefone, email, plano_fidelidade) " +
                     "VALUES (?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE nome=?, data_nascimento=?, telefone=?, email=?, plano_fidelidade=?";

        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getCpf());
            stmt.setString(2, cliente.getNome());
            stmt.setDate(3, Date.valueOf(cliente.getDataNascimento()));
            stmt.setString(4, cliente.getTelefone());
            stmt.setString(5, cliente.getEmail());
            stmt.setString(6, cliente.getPlanoFidelidade().getIdentificadorPlano());
            
            // Parâmetros para a parte de UPDATE
            stmt.setString(7, cliente.getNome());
            stmt.setDate(8, Date.valueOf(cliente.getDataNascimento()));
            stmt.setString(9, cliente.getTelefone());
            stmt.setString(10, cliente.getEmail());
            stmt.setString(11, cliente.getPlanoFidelidade().getIdentificadorPlano());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            // Lançar uma exceção de infraestrutura customizada seria o ideal
            throw new RuntimeException("Erro ao salvar cliente no banco de dados.", e);
        }
    }

    @Override
    public Optional<Cliente> buscarPorCpf(String cpf) {
        String sql = "SELECT * FROM clientes WHERE cpf = ?";
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new Cliente(
                            rs.getString("cpf"),
                            rs.getString("nome"),
                            rs.getObject("data_nascimento", LocalDate.class),
                            rs.getString("telefone"),
                            rs.getString("email"),
                            rs.getString("plano_fidelidade")
                    );
                    return Optional.of(cliente);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar cliente por CPF.", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean existe(String cpf) {
        // É mais eficiente apenas verificar a existência do que buscar o objeto inteiro.
        return buscarPorCpf(cpf).isPresent();
    }
}