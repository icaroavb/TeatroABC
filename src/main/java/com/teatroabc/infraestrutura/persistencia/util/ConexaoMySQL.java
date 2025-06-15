package com.teatroabc.infraestrutura.persistencia.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoMySQL {

    // --- CONFIGURE AQUI OS DADOS DO SEU BANCO ---
    private static final String DB_URL = "jdbc:mysql://localhost:3306/teatro_abc?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root"; // ou seu usuário
    private static final String DB_PASSWORD = "root"; // ou sua senha

    // Bloco estático para garantir que o driver JDBC seja carregado apenas uma vez.
    static {
        try {
            // Carrega o driver JDBC do MySQL.
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC do MySQL não encontrado. Verifique suas dependências.");
            throw new RuntimeException("Falha ao carregar o driver JDBC.", e);
        }
    }

    /**
     * Obtém uma nova conexão com o banco de dados.
     * O chamador é responsável por fechar a conexão.
     *
     * @return um objeto Connection.
     * @throws SQLException se ocorrer um erro ao tentar se conectar.
     */
    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}