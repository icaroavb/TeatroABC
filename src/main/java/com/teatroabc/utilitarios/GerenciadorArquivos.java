package com.teatroabc.utilitarios;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class GerenciadorArquivos {
    private static final String DIRETORIO_DADOS = "dados";
    private static final String ARQUIVO_CLIENTES = "clientes.txt";
    private static final String ARQUIVO_BILHETES = "bilhetes.txt";
    private static final String ARQUIVO_ASSENTOS = "assentos.txt";
    
    static {
        // Criar diretório de dados se não existir
        try {
            Files.createDirectories(Paths.get(DIRETORIO_DADOS));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Métodos para Clientes
    public static void salvarCliente(String linha) {
        salvarLinha(ARQUIVO_CLIENTES, linha);
    }
    
    public static List<String> lerClientes() {
        return lerArquivo(ARQUIVO_CLIENTES);
    }
    
    public static String buscarClientePorCpf(String cpf) {
        List<String> clientes = lerClientes();
        for (String linha : clientes) {
            if (linha.startsWith(cpf + "|")) {
                return linha;
            }
        }
        return null;
    }
    
    // Métodos para Bilhetes
    public static void salvarBilhete(String linha) {
        salvarLinha(ARQUIVO_BILHETES, linha);
    }
    
    public static List<String> lerBilhetes() {
        return lerArquivo(ARQUIVO_BILHETES);
    }
    
    public static List<String> buscarBilhetesPorCpf(String cpf) {
        List<String> bilhetes = lerBilhetes();
        List<String> bilhetesCliente = new ArrayList<>();
        
        for (String linha : bilhetes) {
            String[] partes = linha.split("\\|");
            if (partes.length > 2 && partes[2].equals(cpf)) {
                bilhetesCliente.add(linha);
            }
        }
        
        return bilhetesCliente;
    }
    
    // Métodos para Assentos
    public static void salvarAssento(String linha) {
        salvarLinha(ARQUIVO_ASSENTOS, linha);
    }
    
    public static List<String> lerAssentos() {
        return lerArquivo(ARQUIVO_ASSENTOS);
    }
    
    public static void atualizarStatusAssento(String pecaId, String codigoAssento, String novoStatus) {
        List<String> assentos = lerAssentos();
        List<String> assentosAtualizados = new ArrayList<>();
        
        for (String linha : assentos) {
            String[] partes = linha.split("\\|");
            if (partes.length >= 3 && partes[0].equals(pecaId) && partes[1].equals(codigoAssento)) {
                // Atualiza o status
                assentosAtualizados.add(pecaId + "|" + codigoAssento + "|" + novoStatus);
            } else {
                assentosAtualizados.add(linha);
            }
        }
        
        // Reescreve o arquivo
        Path caminho = Paths.get(DIRETORIO_DADOS, ARQUIVO_ASSENTOS);
        try {
            Files.write(caminho, assentosAtualizados);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Métodos auxiliares
    private static void salvarLinha(String arquivo, String linha) {
        Path caminho = Paths.get(DIRETORIO_DADOS, arquivo);
        try {
            if (!Files.exists(caminho)) {
                Files.createFile(caminho);
            }
            Files.write(caminho, 
                       (linha + System.lineSeparator()).getBytes(), 
                       StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static List<String> lerArquivo(String arquivo) {
        Path caminho = Paths.get(DIRETORIO_DADOS, arquivo);
        try {
            if (Files.exists(caminho)) {
                return Files.readAllLines(caminho);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    
    public static void limparDados() {
        // Método para limpar todos os dados (útil para testes)
        try {
            Path clientes = Paths.get(DIRETORIO_DADOS, ARQUIVO_CLIENTES);
            Path bilhetes = Paths.get(DIRETORIO_DADOS, ARQUIVO_BILHETES);
            Path assentos = Paths.get(DIRETORIO_DADOS, ARQUIVO_ASSENTOS);
            
            if (Files.exists(clientes)) Files.delete(clientes);
            if (Files.exists(bilhetes)) Files.delete(bilhetes);
            if (Files.exists(assentos)) Files.delete(assentos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}