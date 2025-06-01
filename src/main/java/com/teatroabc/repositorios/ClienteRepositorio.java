package com.teatroabc.repositorios;

import com.teatroabc.modelos.Cliente;
import com.teatroabc.utilitarios.GerenciadorArquivos;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ClienteRepositorio {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public void salvar(Cliente cliente) {
        // Formato: CPF|NOME|DATA_NASCIMENTO|TELEFONE
        String linha = String.format("%s|%s|%s|%s",
            cliente.getCpf(),
            cliente.getNome(),
            cliente.getDataNascimento().format(DATE_FORMATTER),
            cliente.getTelefone()
        );
        
        GerenciadorArquivos.salvarCliente(linha);
    }
    
    public Cliente buscarPorCpf(String cpf) {
        String linha = GerenciadorArquivos.buscarClientePorCpf(cpf);
        
        if (linha != null) {
            String[] partes = linha.split("\\|");
            if (partes.length == 4) {
                return new Cliente(
                    partes[0], // CPF
                    partes[1], // Nome
                    LocalDate.parse(partes[2], DATE_FORMATTER), // Data Nascimento
                    partes[3]  // Telefone
                );
            }
        }
        
        return null;
    }
    
    public boolean existe(String cpf) {
        return buscarPorCpf(cpf) != null;
    }
}