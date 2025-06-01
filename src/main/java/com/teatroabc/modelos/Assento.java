package com.teatroabc.modelos;

import com.teatroabc.enums.CategoriaAssento;
import com.teatroabc.enums.StatusAssento;
import java.util.Objects;

public class Assento {
    private final String codigo;
    private final int fileira;
    private final int numero;
    private final CategoriaAssento categoria;
    private StatusAssento status;

    public Assento(String codigo, int fileira, int numero, CategoriaAssento categoria) {
        this.codigo = codigo;
        this.fileira = fileira;
        this.numero = numero;
        this.categoria = categoria;
        this.status = StatusAssento.DISPONIVEL;
    }

    // Getters and Setters
    public String getCodigo() { return codigo; }
    public int getFileira() { return fileira; }
    public int getNumero() { return numero; }
    public CategoriaAssento getCategoria() { return categoria; }
    public StatusAssento getStatus() { return status; }
    public void setStatus(StatusAssento status) { this.status = status; }

    public double getPreco() {
        return categoria.getPreco();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assento assento = (Assento) o;
        return Objects.equals(codigo, assento.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}
