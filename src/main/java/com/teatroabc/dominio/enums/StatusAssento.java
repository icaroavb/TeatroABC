package com.teatroabc.dominio.enums;

/**
 * Enumeração que define os possíveis estados de um assento no sistema de bilheteria.
 * Faz parte do vocabulário do domínio para descrever a disponibilidade de um assento.
 */
public enum StatusAssento {
    DISPONIVEL("Disponível"),
    OCUPADO("Ocupado"),
    SELECIONADO("Selecionado"); // Usado durante o processo de compra pela UI

    private final String descricao;

    /**
     * Construtor para StatusAssento.
     * @param descricao A descrição textual amigável do status.
     */
    StatusAssento(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Retorna a descrição textual amigável do status do assento.
     * @return A descrição do status.
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Retorna a descrição do status.
     * Útil para exibição em interfaces de usuário ou logs.
     * @return A descrição do status.
     */
    @Override
    public String toString() {
        return descricao;
    }
}