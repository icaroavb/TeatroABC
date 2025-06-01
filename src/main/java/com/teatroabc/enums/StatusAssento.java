package com.teatroabc.enums;

public enum StatusAssento {
    DISPONIVEL("Disponível"),
    OCUPADO("Ocupado"),
    SELECIONADO("Selecionado");

    private final String descricao;

    StatusAssento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
