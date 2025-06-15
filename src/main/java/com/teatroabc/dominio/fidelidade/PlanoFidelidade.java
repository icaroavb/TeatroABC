package com.teatroabc.dominio.fidelidade;

import java.math.BigDecimal;

/**
 * Interface que define o contrato para diferentes planos de fidelidade.
 * Cada plano pode oferecer diferentes lógicas de desconto e benefícios.
 */
public interface PlanoFidelidade {

    /**
     * Retorna o nome amigável do plano de fidelidade.
     * Ex: "Padrão", "ABC GOLD".
     * @return O nome do plano.
     */
    String getNomePlano();

    /**
     * Retorna um identificador único em string para o plano.
     * Usado principalmente para persistência e na factory para recriar o plano.
     * Ex: "PADRAO", "GOLD".
     * @return O identificador do plano.
     */
    String getIdentificadorPlano();

    /**
     * Retorna o fator de Desconto.
     * @return O valor do desconto como BigDecimal. Nunca nulo, pode ser BigDecimal.ZERO.
     */
    BigDecimal getFatorDesconto();

    /**
     * Retorna uma descrição textual dos benefícios oferecidos por este plano.
     * @return A descrição dos benefícios.
     */
    String getDescricaoBeneficios();

    /**
     * Indica se este plano de fidelidade requer que o cliente forneça um número de telefone.
     * (Exemplo de como o plano pode ditar regras para os dados do cliente)
     * @return true se o telefone for obrigatório, false caso contrário.
     */
    // boolean requerTelefone(); // Descomentar e implementar se necessário

    /**
     * Indica se este plano de fidelidade requer que o cliente forneça um endereço de e-mail.
     * (Exemplo de como o plano pode ditar regras para os dados do cliente)
     * @return true se o e-mail for obrigatório, false caso contrário.
     */
    // boolean requerEmail(); // Descomentar e implementar se necessário
}