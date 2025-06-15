package com.teatroabc.dominio.enums;

/**
 * Enumeração que define os possíveis estados de um pagamento associado a uma transação
 * (por exemplo, a compra de um bilhete).
 * Esta enumeração faz parte do vocabulário do domínio para o ciclo de vida
 * de uma transação financeira.
 */
public enum StatusPagamento {
    /**
     * O pagamento foi iniciado ou está aguardando processamento/confirmação.
     */
    PENDENTE("Pendente"),

    /**
     * O pagamento foi processado e confirmado com sucesso.
     */
    PAGO("Pago"),

    /**
     * O pagamento foi cancelado antes da conclusão, seja pelo usuário,
     * sistema ou por expiração.
     */
    CANCELADO("Cancelado");

    private final String descricao;

    StatusPagamento(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Retorna a descrição textual amigável do status do pagamento.
     * @return A descrição do status.
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Retorna a descrição do status.
     * @return A descrição do status.
     */
    @Override
    public String toString() {
        return descricao;
    }
}