package com.teatroabc.infraestrutura.persistencia.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConexaoMySQL {
    private static final String URL = "jdbc:mysql://localhost:3306/teatro";
    private static final String USUARIO = "root";
    private static final String SENHA = "Pato903859#";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
