package com.teatroabc.modelos;

import java.time.LocalDate;
import java.util.Objects;

public class Cliente {
    private final String cpf;
    private final String nome;
    private final LocalDate dataNascimento;
    private final String telefone;
    private final String email;
    private final boolean membroABC;

    // Construtor completo
    public Cliente(String cpf, String nome, LocalDate dataNascimento, String telefone, String email, boolean membroABC) {
        this.cpf = cpf;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.email = email;
        this.membroABC = membroABC;
    }

    // Construtor para compatibilidade (cliente não ABC)
    public Cliente(String cpf, String nome, LocalDate dataNascimento, String telefone) {
        this(cpf, nome, dataNascimento, telefone, "", false);
    }

    // Getters
    public String getCpf() { return cpf; }
    public String getNome() { return nome; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }
    public boolean isMembroABC() { return membroABC; }

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