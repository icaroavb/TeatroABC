package com.teatroabc.dominio.modelos;

import com.teatroabc.dominio.fidelidade.PlanoFidelidade;
import com.teatroabc.dominio.fidelidade.PlanoFidelidadeFactory; // Para o construtor com string
import com.teatroabc.dominio.fidelidade.SemFidelidade;       // Para o valor padrão
import com.teatroabc.dominio.fidelidade.MembroABCGold;     // Para o método isMembroGold (opcional)

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Representa um cliente do teatro.
 * Um cliente possui dados pessoais e pode estar associado a um plano de fidelidade.
 */
public class Cliente {
    private final String cpf; // Identificador único da entidade Cliente
    private final String nome;
    private final LocalDate dataNascimento;
    private final String telefone; // Pode ser opcional ou obrigatório dependendo do PlanoFidelidade
    private final String email;    // Pode ser opcional ou obrigatório dependendo do PlanoFidelidade
    private final PlanoFidelidade planoFidelidade; // Composição com a estratégia de fidelidade

    /**
     * Construtor principal para criar uma instância de Cliente.
     * Recebe uma instância concreta de PlanoFidelidade.
     *
     * @param cpf O CPF do cliente (deve ser único e válido).
     * @param nome O nome completo do cliente.
     * @param dataNascimento A data de nascimento do cliente.
     * @param telefone O número de telefone do cliente (pode ser nulo/vazio).
     * @param email O endereço de e-mail do cliente (pode ser nulo/vazio).
     * @param planoFidelidade A estratégia de plano de fidelidade associada a este cliente.
     *                        Se nulo, um plano padrão (SemFidelidade) será atribuído.
     * @throws IllegalArgumentException Se cpf, nome ou dataNascimento forem inválidos.
     */
    public Cliente(String cpf, String nome, LocalDate dataNascimento,
                   String telefone, String email, PlanoFidelidade planoFidelidade) {

        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF do cliente não pode ser nulo, vazio ou inválido.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do cliente não pode ser nulo ou vazio.");
        }
        if (dataNascimento == null) {
            throw new IllegalArgumentException("Data de nascimento do cliente não pode ser nula.");
        }

        this.cpf = cpf.replaceAll("[^0-9]", ""); // Normaliza CPF para apenas dígitos
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.email = email;
        this.planoFidelidade = Objects.requireNonNullElseGet(planoFidelidade, SemFidelidade::new);
    }

    /**
     * Construtor de conveniência para criar um Cliente usando o identificador do plano de fidelidade.
     * Útil ao carregar da persistência ou quando a camada de aplicação/serviço
     * determina o plano por um identificador string.
     */
    public Cliente(String cpf, String nome, LocalDate dataNascimento,
                   String telefone, String email, String identificadorPlanoFidelidade) {
        this(cpf, nome, dataNascimento, telefone, email, PlanoFidelidadeFactory.criar(identificadorPlanoFidelidade));
    }

    // --- Getters ---
    public String getCpf() { return cpf; }
    public String getNome() { return nome; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }
    public PlanoFidelidade getPlanoFidelidade() { return planoFidelidade; }

    // --- Comportamentos Delegados à Estratégia de Fidelidade ---
    public BigDecimal obterDescontoParaCompra(List<Assento> assentos) {
        return this.planoFidelidade.calcularDesconto(assentos);
    }

    public String getNomePlanoFidelidade() {
        return this.planoFidelidade.getNomePlano();
    }

    public String getDescricaoBeneficiosPlano() {
        return this.planoFidelidade.getDescricaoBeneficios();
    }

    // --- Métodos Utilitários/Conveniência (Opcionais) ---
    public boolean isMembroGold() {
        return MembroABCGold.IDENTIFICADOR.equals(this.planoFidelidade.getIdentificadorPlano());
    }

    // --- Métodos Padrão (equals, hashCode, toString) ---
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
                ", planoFidelidade=" + planoFidelidade.getNomePlano() +
                '}';
    }
}