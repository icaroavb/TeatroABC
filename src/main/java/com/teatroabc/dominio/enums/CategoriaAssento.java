package com.teatroabc.dominio.enums;

import java.math.BigDecimal;

public enum CategoriaAssento {
    PLATEIA_A("Plateia A", new BigDecimal("40.00")),
    PLATEIA_B("Plateia B", new BigDecimal("60.00")),
    CAMAROTE("Camarote", new BigDecimal("80.00")),
    FRISA("Frisa", new BigDecimal("120.00")), // Note que o documento diz "Frisa 5 por frisa", o preço aqui é por assento individual na frisa.
    BALCAO_NOBRE("Balcão Nobre", new BigDecimal("250.00"));

    private final String nome;
    private final BigDecimal precoBase;

    CategoriaAssento(String nome, BigDecimal precoBase) {
        this.nome = nome;
        this.precoBase = precoBase;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getPrecoBase() {
        return precoBase;
    }

    @Override
    public String toString() {
        return nome;
    }
}