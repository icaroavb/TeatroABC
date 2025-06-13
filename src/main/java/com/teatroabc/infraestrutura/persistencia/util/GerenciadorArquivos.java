// Arquivo: infraestrutura/persistencia/util/GerenciadorArquivos.java
package com.teatroabc.infraestrutura.persistencia.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Utilitário de infraestrutura responsável pelo gerenciamento de leitura e escrita
 * de dados em arquivos de texto. Atua como a camada de acesso a dados de baixo nível
 * para os adaptadores de repositório.
 * REFATORADO: A persistência de assentos ocupados agora é baseada no ID da sessão.
 */
public class GerenciadorArquivos {
    private static final String DIRETORIO_DADOS = "dados";
    private static final String ARQUIVO_CLIENTES = "clientes.txt";
    private static final String ARQUIVO_BILHETES = "bilhetes.txt";
    // O arquivo para assentos ocupados foi renomeado para refletir a nova lógica.
    private static final String ARQUIVO_ASSENTOS_OCUPADOS = "assentos_ocupados.txt";

    // O bloco estático garante que o diretório de dados exista ao iniciar a aplicação.
    static {
        try {
            Path diretorio = Paths.get(DIRETORIO_DADOS);
            if (!Files.exists(diretorio)) {
                Files.createDirectories(diretorio);
            }
        } catch (IOException e) {
            System.err.println("Erro crítico ao inicializar GerenciadorArquivos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Métodos para Clientes (sem alterações) ---
    public static void salvarCliente(String linhaCliente) {
        salvarLinha(ARQUIVO_CLIENTES, linhaCliente);
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

    // --- Métodos para Bilhetes (sem alterações na leitura/escrita) ---
    public static void salvarBilhete(String linhaBilhete) {
        salvarLinha(ARQUIVO_BILHETES, linhaBilhete);
    }

    public static List<String> lerBilhetes() {
        return lerArquivo(ARQUIVO_BILHETES);
    }

    public static List<String> buscarBilhetesPorCpf(String cpfCliente) {
        List<String> todosBilhetes = lerBilhetes();
        List<String> bilhetesDoCliente = new ArrayList<>();
        for (String linha : todosBilhetes) {
            String[] partes = linha.split("\\|");
            if (partes.length > 2 && partes[2].equals(cpfCliente)) {
                bilhetesDoCliente.add(linha);
            }
        }
        return bilhetesDoCliente;
    }

    // --- MÉTODOS DE ASSENTOS OCUPADOS REFATORADOS ---

    /**
     * Registra que um assento foi ocupado para uma sessão específica.
     * Salva uma linha no formato "idSessao|codigoAssento".
     *
     * @param idSessao O ID da sessão para a qual o assento foi ocupado.
     * @param codigoAssento O código do assento ocupado.
     */
    public static void marcarAssentoOcupado(String idSessao, String codigoAssento) {
        String linhaOcupacao = idSessao + "|" + codigoAssento;
        salvarLinha(ARQUIVO_ASSENTOS_OCUPADOS, linhaOcupacao);
    }

    /**
     * Busca todos os códigos de assentos que estão registrados como ocupados
     * para uma determinada sessão.
     *
     * @param idSessao O ID da sessão a ser consultada.
     * @return Um Set contendo os códigos dos assentos ocupados.
     */
    public static Set<String> buscarAssentosOcupados(String idSessao) {
        Set<String> assentosOcupados = new HashSet<>();
        List<String> linhas = lerArquivo(ARQUIVO_ASSENTOS_OCUPADOS);
        String prefixoBusca = idSessao + "|";

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
            Files.write(caminho,
                    (linha + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
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
                return Files.readAllLines(caminho, StandardCharsets.UTF_8);
            } else {
                Files.createFile(caminho);
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler/criar arquivo " + nomeArquivo + ":");
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
