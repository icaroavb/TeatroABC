package com.teatroabc.repositorios;

import com.teatroabc.modelos.Cliente;
import com.teatroabc.utilitarios.GerenciadorArquivos;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ClienteRepositorio {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public void salvar(Cliente cliente) {
        // Verificar se o cliente já existe
        if (existe(cliente.getCpf())) {
            System.out.println("Cliente já existe, não será salvo novamente: " + cliente.getCpf());
            return;
        }
        
        // Formato: CPF|NOME|DATA_NASCIMENTO|TELEFONE|EMAIL|MEMBRO_ABC
        String linha = String.format("%s|%s|%s|%s|%s|%s",
            cliente.getCpf(),
            cliente.getNome(),
            cliente.getDataNascimento().format(DATE_FORMATTER),
            cliente.getTelefone() != null ? cliente.getTelefone() : "",
            cliente.getEmail() != null ? cliente.getEmail() : "",
            cliente.isMembroABC() ? "true" : "false"
        );
        
        GerenciadorArquivos.salvarCliente(linha);
        System.out.println("Cliente salvo: " + cliente.getNome() + " - ABC GOLD: " + cliente.isMembroABC());
    }
    
    public Cliente buscarPorCpf(String cpf) {
        String linha = GerenciadorArquivos.buscarClientePorCpf(cpf);
        
        if (linha != null) {
            String[] partes = linha.split("\\|");
            
            // Compatibilidade com formato antigo (4 partes) e novo (6 partes)
            if (partes.length >= 4) {
                String telefone = partes.length > 3 ? partes[3] : "";
                String email = partes.length > 4 ? partes[4] : "";
                boolean membroABC = partes.length > 5 ? "true".equals(partes[5]) : false;
                
                return new Cliente(
                    partes[0], // CPF
                    partes[1], // Nome
                    LocalDate.parse(partes[2], DATE_FORMATTER), // Data Nascimento
                    telefone,  // Telefone
                    email,     // Email
                    membroABC  // Membro ABC
                );
            }
        }
        
        return null;
    }
    
    public boolean existe(String cpf) {
        return buscarPorCpf(cpf) != null;
    }
}