package com.teatroabc.modelos;

import com.teatroabc.fidelidade.MembroABCGold;
import com.teatroabc.fidelidade.PlanoFidelidade;
import com.teatroabc.fidelidade.PlanoFidelidadeFactory; // Para o construtor com string
import com.teatroabc.fidelidade.SemFidelidade;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Cliente {
    private final String cpf;
    private final String nome;
    private final LocalDate dataNascimento;
    private final String telefone;
    private final String email;
    private PlanoFidelidade planoFidelidade; // Imutável após construção se não houver setPlanoFidelidade

    // Construtor principal, recebe a instância do PlanoFidelidade
    public Cliente(String cpf, String nome, LocalDate dataNascimento,
                   String telefone, String email, PlanoFidelidade planoFidelidade) {
        validarCamposObrigatorios(cpf, nome, dataNascimento); // Adicionar validações
        this.cpf = cpf;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone; // Pode ser nulo se não obrigatório
        this.email = email;       // Pode ser nulo se não obrigatório
        this.planoFidelidade = Objects.requireNonNullElseGet(planoFidelidade, SemFidelidade::new);
    }

    // Construtor para ser usado ao carregar da persistência (usando identificador do plano)
    public Cliente(String cpf, String nome, LocalDate dataNascimento,
                   String telefone, String email, String identificadorPlanoFidelidade) {
        this(cpf, nome, dataNascimento, telefone, email, PlanoFidelidadeFactory.criar(identificadorPlanoFidelidade));
    }


    private void validarCamposObrigatorios(String cpf, String nome, LocalDate dataNascimento) {
        if (cpf == null || cpf.trim().isEmpty() /*|| !ValidadorCPF.isValid(cpf) // Descomentar se ValidadorCPF for usado aqui */) {
            throw new IllegalArgumentException("CPF inválido ou ausente.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio.");
        }
        if (dataNascimento == null) { // Adicionar mais validações de data se necessário
            throw new IllegalArgumentException("Data de nascimento não pode ser nula.");
        }
    }

    // Getters
    public String getCpf() { return cpf; }
    public String getNome() { return nome; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }
    public PlanoFidelidade getPlanoFidelidade() { return planoFidelidade; }

    // Método para obter o desconto, delegando para a estratégia
    public BigDecimal obterDescontoParaCompra(List<Assento> assentos) {
        return this.planoFidelidade.calcularDesconto(assentos);
    }

    // Conveniência para verificar se é um tipo específico de membro (se necessário)
    public boolean isMembroGold() {
        return this.planoFidelidade instanceof MembroABCGold;
    }

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

    @Override
    public String toString() {
        return "Cliente{" +
               "cpf='" + cpf + '\'' +
               ", nome='" + nome + '\'' +
               ", dataNascimento=" + dataNascimento +
               ", telefone='" + (telefone == null ? "N/A" : telefone) + '\'' +
               ", email='" + (email == null ? "N/A" : email) + '\'' +
               ", planoFidelidade=" + (planoFidelidade == null ? "N/A" : planoFidelidade.getNomePlano()) +
               '}';
    }

}