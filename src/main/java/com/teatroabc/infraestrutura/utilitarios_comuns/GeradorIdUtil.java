package com.teatroabc.infraestrutura.utilitarios_comuns;

import java.util.UUID;

/**
 * Utilitário para a geração de identificadores únicos.
 * Pode ser usado por serviços de aplicação ou adaptadores para criar IDs
 * para novas entidades de domínio ou outros artefatos que necessitem de
 * um identificador único.
 * Na arquitetura hexagonal, a geração de IDs é frequentemente uma preocupação
 * de infraestrutura ou de um serviço de aplicação que orquestra a criação de entidades.
 */
public class GeradorIdUtil {

    /**
     * Gera um ID único universal (UUID) como String.
     * Este método é comumente usado para gerar IDs para entidades de domínio.
     * @return Uma String representando o UUID.
     */
    public static String gerarNovoId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Gera um novo código de barras.
     * A lógica atual é um exemplo simples e pode ser adaptada conforme a necessidade
     * de formato ou unicidade do código de barras.
     * @return Uma String representando o código de barras.
     */
    public static String gerarNovoCodigoBarras() {
        // Exemplo: Prefixo "TB" + parte do timestamp em nanossegundos para alguma variação.
        // Em um sistema real, a lógica poderia ser mais robusta para garantir unicidade
        // ou seguir um padrão específico de código de barras.
        return String.format("TB%010d", System.nanoTime() % 10000000000L);
    }

    // Futuramente, outros métodos para gerar diferentes tipos de IDs
    // poderiam ser adicionados aqui, por exemplo:
    // public static long gerarIdNumericoSequencial() { /* ... */ }
}