package com.teatroabc.modelos;

import java.time.LocalDate;
import java.util.Objects;

public class Cliente {
    private final String cpf;
    private final String nome;
    private final LocalDate dataNascimento;
    private final String telefone;

    public Cliente(String cpf, String nome, LocalDate dataNascimento2, String telefone) {
        this.cpf = cpf;
        this.nome = nome;
        this.dataNascimento = dataNascimento2;
        this.telefone = telefone;
    }

    // Getters
    public String getCpf() { return cpf; }
    public String getNome() { return nome; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public String getTelefone() { return telefone; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(cpf, cliente.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }
}