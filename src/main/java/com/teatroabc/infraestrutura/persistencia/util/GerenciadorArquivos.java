package com.teatroabc.infraestrutura.persistencia.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Utilitário de infraestrutura responsável pelo gerenciamento de leitura e escrita
 * de dados em arquivos de texto. Atua como a camada de acesso a dados de baixo nível
 * para os adaptadores de repositório.
 * Na arquitetura hexagonal, esta classe seria parte de um Adaptador de Saída (Driven Adapter)
 * para a persistência em arquivos.
 */
public class GerenciadorArquivos {
    private static final String DIRETORIO_DADOS = "dados";
    private static final String ARQUIVO_CLIENTES = "clientes.txt";
    private static final String ARQUIVO_BILHETES = "bilhetes.txt";
    // private static final String ARQUIVO_ASSENTOS = "assentos.txt"; // Parece não estar mais em uso ativo para status
    private static final String ARQUIVO_ASSENTOS_TURNOS = "assentos_turnos.txt"; // Para persistir assentos ocupados por turno

    static {
        try {
            Path diretorio = Paths.get(DIRETORIO_DADOS);
            if (!Files.exists(diretorio)) {
                Files.createDirectories(diretorio);
                // System.out.println("Diretório 'dados' criado: " + diretorio.toAbsolutePath());
            } else {
                // System.out.println("Diretório 'dados' existe: " + diretorio.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Erro crítico ao inicializar GerenciadorArquivos (criar diretório de dados):");
            e.printStackTrace();
            // Considerar lançar uma RuntimeException aqui se a criação do diretório é vital
        }
    }

    // --- Métodos para Clientes ---
    public static void salvarCliente(String linhaCliente) {
        salvarLinha(ARQUIVO_CLIENTES, linhaCliente);
    }

    public static List<String> lerClientes() {
        return lerArquivo(ARQUIVO_CLIENTES);
    }

    public static String buscarClientePorCpf(String cpf) { // CPF deve vir normalizado (só números)
        List<String> clientes = lerClientes();
        for (String linha : clientes) {
            // Assumindo que o CPF é o primeiro campo e está normalizado no arquivo também.
            if (linha.startsWith(cpf + "|")) {
                return linha;
            }
        }
        return null;
    }

    // --- Métodos para Bilhetes ---
    public static void salvarBilhete(String linhaBilhete) {
        salvarLinha(ARQUIVO_BILHETES, linhaBilhete);
    }

    public static List<String> lerBilhetes() {
        return lerArquivo(ARQUIVO_BILHETES);
    }

    public static List<String> buscarBilhetesPorCpf(String cpfCliente) { // CPF deve vir normalizado
        List<String> todosBilhetes = lerBilhetes();
        List<String> bilhetesDoCliente = new ArrayList<>();
        for (String linha : todosBilhetes) {
            String[] partes = linha.split("\\|");
            // Formato esperado: ID|CODIGO_BARRAS|CPF_CLIENTE|... (índice 2 para CPF)
            if (partes.length > 2 && partes[2].equals(cpfCliente)) {
                bilhetesDoCliente.add(linha);
            }
        }
        return bilhetesDoCliente;
    }

    // --- Métodos para Assentos Ocupados por Turno ---
    /**
     * Registra que um assento específico para uma peça e turno foi ocupado.
     * Salva uma linha no formato "idPeca|nomeTurno|codigoAssento".
     */
    public static void marcarAssentoOcupado(String idPeca, String nomeTurno, String codigoAssento) {
        String chaveOcupacao = idPeca + "|" + nomeTurno + "|" + codigoAssento;
        // Evitar duplicatas se já existir (embora append não verifique)
        // Para um sistema real, seria melhor ler, verificar e depois escrever ou usar um Set em memória se o arquivo for pequeno.
        // Por simplicidade, apenas adicionamos. A lógica de verificação de disponibilidade deve ser robusta.
        salvarLinha(ARQUIVO_ASSENTOS_TURNOS, chaveOcupacao);
    }

    /**
     * Busca todos os códigos de assentos que estão registrados como ocupados
     * para uma determinada peça e turno.
     * @return Um Set contendo os códigos dos assentos ocupados.
     */
    public static Set<String> buscarAssentosOcupados(String idPeca, String nomeTurno) {
        Set<String> assentosOcupados = new HashSet<>();
        List<String> linhas = lerArquivo(ARQUIVO_ASSENTOS_TURNOS);
        String prefixoBusca = idPeca + "|" + nomeTurno + "|";

        for (String linha : linhas) {
            if (linha.startsWith(prefixoBusca)) {
                String codigoAssento = linha.substring(prefixoBusca.length());
                assentosOcupados.add(codigoAssento);
            }
        }
        return assentosOcupados;
    }

    // --- Métodos Auxiliares Genéricos de Manipulação de Arquivo ---
    private static boolean salvarLinha(String nomeArquivo, String linha) {
        Path caminho = Paths.get(DIRETORIO_DADOS, nomeArquivo);
        try {
            // Cria o arquivo se não existir
            if (!Files.exists(caminho)) {
                Files.createFile(caminho);
            }
            // Adiciona a linha ao final do arquivo (append)
            Files.write(caminho,
                    (linha + System.lineSeparator()).getBytes(StandardCharsets.UTF_8), // Especificar Charset
                    StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao salvar linha no arquivo " + nomeArquivo + ": " + linha);
            e.printStackTrace();
            return false;
        }
    }

    private static List<String> lerArquivo(String nomeArquivo) {
        Path caminho = Paths.get(DIRETORIO_DADOS, nomeArquivo);
        try {
            if (Files.exists(caminho)) {
                return Files.readAllLines(caminho, StandardCharsets.UTF_8); // Especificar Charset
            } else {
                // System.out.println("Arquivo " + nomeArquivo + " não existe, retornando lista vazia.");
                // Criar o arquivo vazio para evitar problemas na primeira execução se ele for necessário para append
                try {
                    Files.createFile(caminho);
                } catch (IOException exCreate) {
                    System.err.println("Não foi possível criar o arquivo " + nomeArquivo + " que não existia.");
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo " + nomeArquivo + ":");
            e.printStackTrace();
        }
        return Collections.emptyList(); // Retorna lista imutável vazia em caso de erro ou arquivo não existente
    }

    // ... (método limparDados e debugListarArquivos podem ser mantidos para desenvolvimento/teste)
}