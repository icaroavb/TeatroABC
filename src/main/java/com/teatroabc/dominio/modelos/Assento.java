package com.teatroabc.dominio.modelos;

import com.teatroabc.dominio.enums.CategoriaAssento;
import com.teatroabc.dominio.enums.StatusAssento;
import java.math.BigDecimal;
import java.util.Objects;

public class Assento {
    private final String codigo;
    private final int fileira;
    private final int numero;
    private final CategoriaAssento categoria;
    private final BigDecimal preco; // Preço é agora um atributo final do Assento
    private StatusAssento status;

    public Assento(String codigo, int fileira, int numero, CategoriaAssento categoria, BigDecimal preco) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("Código do assento não pode ser nulo ou vazio.");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria do assento não pode ser nula.");
        }
        if (preco == null || preco.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço do assento não pode ser nulo ou negativo.");
        }

        this.codigo = codigo;
        this.fileira = fileira;
        this.numero = numero;
        this.categoria = categoria; 
        this.preco = preco.setScale(2, BigDecimal.ROUND_HALF_UP); // Armazena com 2 casas decimais
        this.status = StatusAssento.DISPONIVEL;
    }

    // Getters
    public String getCodigo() { return codigo; }
    public int getFileira() { return fileira; }
    public int getNumero() { return numero; }
    public CategoriaAssento getCategoria() { return categoria; }
    public BigDecimal getPreco() { return preco; } // Retorna o preço armazenado
    public StatusAssento getStatus() { return status; }
    
    public void setStatus(StatusAssento status) {
        if (status == null) {
            throw new IllegalArgumentException("Status do assento não pode ser nulo.");
        }
        this.status = status;
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

    @Override
    public String toString() {
        return "Assento{" +
               "codigo='" + codigo + '\'' +
               ", categoria=" + categoria.getNome() +
               ", preco=" + preco.toString() +
               ", status=" + status +
               '}';
    }
}