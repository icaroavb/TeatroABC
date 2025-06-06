package com.teatroabc.utilitarios;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class GerenciadorArquivos {
    private static final String DIRETORIO_DADOS = "dados";
    private static final String ARQUIVO_CLIENTES = "clientes.txt";
    private static final String ARQUIVO_BILHETES = "bilhetes.txt";
    private static final String ARQUIVO_ASSENTOS = "assentos.txt";
    private static final String ARQUIVO_ASSENTOS_TURNOS = "assentos_turnos.txt";
    
    static {
        // Criar diretório de dados se não existir
        try {
            Path diretorio = Paths.get(DIRETORIO_DADOS);
            if (!Files.exists(diretorio)) {
                Files.createDirectories(diretorio);
                System.out.println("Diretório 'dados' criado: " + diretorio.toAbsolutePath());
            } else {
                System.out.println("Diretório 'dados' existe: " + diretorio.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Erro ao criar diretório de dados:");
            e.printStackTrace();
        }
    }
    
    // Métodos para Clientes
    public static void salvarCliente(String linha) {
        if (salvarLinha(ARQUIVO_CLIENTES, linha)) {
            System.out.println("Cliente salvo: " + linha);
        }
    }
    
    public static List<String> lerClientes() {
        List<String> clientes = lerArquivo(ARQUIVO_CLIENTES);
        System.out.println("Lidos " + clientes.size() + " clientes do arquivo");
        return clientes;
    }
    
    public static String buscarClientePorCpf(String cpf) {
        List<String> clientes = lerClientes();
        for (String linha : clientes) {
            if (linha.startsWith(cpf + "|")) {
                System.out.println("Cliente encontrado para CPF " + cpf + ": " + linha);
                return linha;
            }
        }
        System.out.println("Cliente não encontrado para CPF: " + cpf);
        return null;
    }
    
    // Métodos para Bilhetes
    public static void salvarBilhete(String linha) {
        if (salvarLinha(ARQUIVO_BILHETES, linha)) {
            System.out.println("Bilhete salvo: " + linha);
        }
    }
    
    public static List<String> lerBilhetes() {
        List<String> bilhetes = lerArquivo(ARQUIVO_BILHETES);
        System.out.println("Lidos " + bilhetes.size() + " bilhetes do arquivo");
        return bilhetes;
    }
    
    public static List<String> buscarBilhetesPorCpf(String cpf) {
        List<String> bilhetes = lerBilhetes();
        List<String> bilhetesCliente = new ArrayList<>();
        
        System.out.println("Buscando bilhetes para CPF: " + cpf);
        
        for (String linha : bilhetes) {
            String[] partes = linha.split("\\|");
            if (partes.length > 2 && partes[2].equals(cpf)) {
                bilhetesCliente.add(linha);
                System.out.println("Bilhete encontrado: " + linha);
            }
        }
        
        System.out.println("Total de bilhetes encontrados para CPF " + cpf + ": " + bilhetesCliente.size());
        return bilhetesCliente;
    }
    
    // Métodos para Assentos com Turnos
    public static void marcarAssentoOcupado(String pecaId, String turno, String codigoAssento) {
        String chave = pecaId + "|" + turno + "|" + codigoAssento;
        salvarLinha(ARQUIVO_ASSENTOS_TURNOS, chave);
        System.out.println("Assento marcado como ocupado: " + chave);
    }
    
    public static boolean isAssentoOcupado(String pecaId, String turno, String codigoAssento) {
        String chave = pecaId + "|" + turno + "|" + codigoAssento;
        List<String> assentosOcupados = lerArquivo(ARQUIVO_ASSENTOS_TURNOS);
        boolean ocupado = assentosOcupados.contains(chave);
        System.out.println("Verificando ocupação: " + chave + " = " + ocupado);
        return ocupado;
    }
    
    public static Set<String> buscarAssentosOcupados(String pecaId, String turno) {
        Set<String> assentosOcupados = new HashSet<>();
        List<String> linhas = lerArquivo(ARQUIVO_ASSENTOS_TURNOS);
        
        String prefixo = pecaId + "|" + turno + "|";
        for (String linha : linhas) {
            if (linha.startsWith(prefixo)) {
                String codigoAssento = linha.substring(prefixo.length());
                assentosOcupados.add(codigoAssento);
            }
        }
        
        System.out.println("Assentos ocupados para " + pecaId + " no turno " + turno + ": " + assentosOcupados);
        return assentosOcupados;
    }
    
    // Métodos para Assentos (mantido para compatibilidade)
    public static void salvarAssento(String linha) {
        if (salvarLinha(ARQUIVO_ASSENTOS, linha)) {
            System.out.println("Assento salvo: " + linha);
        }
    }
    
    public static List<String> lerAssentos() {
        return lerArquivo(ARQUIVO_ASSENTOS);
    }
    
    public static void atualizarStatusAssento(String pecaId, String codigoAssento, String novoStatus) {
        System.out.println("Atualizando assento: " + pecaId + "|" + codigoAssento + "|" + novoStatus);
        // Implementação mantida para compatibilidade, mas não é mais usada
    }
    
    // Métodos auxiliares
    private static boolean salvarLinha(String arquivo, String linha) {
        Path caminho = Paths.get(DIRETORIO_DADOS, arquivo);
        try {
            if (!Files.exists(caminho)) {
                Files.createFile(caminho);
                System.out.println("Arquivo criado: " + caminho.toAbsolutePath());
            }
            Files.write(caminho, 
                       (linha + System.lineSeparator()).getBytes(), 
                       StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao salvar no arquivo " + arquivo + ":");
            e.printStackTrace();
            return false;
        }
    }
    
    private static List<String> lerArquivo(String arquivo) {
        Path caminho = Paths.get(DIRETORIO_DADOS, arquivo);
        try {
            if (Files.exists(caminho)) {
                List<String> linhas = Files.readAllLines(caminho);
                System.out.println("Arquivo " + arquivo + " lido com " + linhas.size() + " linhas");
                return linhas;
            } else {
                System.out.println("Arquivo " + arquivo + " não existe: " + caminho.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo " + arquivo + ":");
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
            Path assentosTurnos = Paths.get(DIRETORIO_DADOS, ARQUIVO_ASSENTOS_TURNOS);
            
            if (Files.exists(clientes)) {
                Files.delete(clientes);
                System.out.println("Arquivo de clientes deletado");
            }
            if (Files.exists(bilhetes)) {
                Files.delete(bilhetes);
                System.out.println("Arquivo de bilhetes deletado");
            }
            if (Files.exists(assentos)) {
                Files.delete(assentos);
                System.out.println("Arquivo de assentos deletado");
            }
            if (Files.exists(assentosTurnos)) {
                Files.delete(assentosTurnos);
                System.out.println("Arquivo de assentos por turno deletado");
            }
        } catch (IOException e) {
            System.err.println("Erro ao limpar dados:");
            e.printStackTrace();
        }
    }
    
    // Método para debug - listar conteúdo dos arquivos
    public static void debugListarArquivos() {
        System.out.println("\n=== DEBUG - CONTEÚDO DOS ARQUIVOS ===");
        
        System.out.println("\n--- CLIENTES ---");
        List<String> clientes = lerClientes();
        clientes.forEach(System.out::println);
        
        System.out.println("\n--- BILHETES ---");
        List<String> bilhetes = lerBilhetes();
        bilhetes.forEach(System.out::println);
        
        System.out.println("\n--- ASSENTOS ---");
        List<String> assentos = lerAssentos();
        assentos.forEach(System.out::println);
        
        System.out.println("\n--- ASSENTOS POR TURNO ---");
        List<String> assentosTurnos = lerArquivo(ARQUIVO_ASSENTOS_TURNOS);
        assentosTurnos.forEach(System.out::println);
        
        System.out.println("\n=== FIM DEBUG ===\n");
    }
}