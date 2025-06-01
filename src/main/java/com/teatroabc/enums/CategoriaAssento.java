package com.teatroabc.enums;

public enum CategoriaAssento {
    FRISAS("Frisas", 25.00),
    BALCAO_NOBRE("Balcão Nobre", 20.00),
    BALCAO("Balcão", 15.00);

    private final String nome;
    private final double preco;

    CategoriaAssento(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
    }

    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }
}