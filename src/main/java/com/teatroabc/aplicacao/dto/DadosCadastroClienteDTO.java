package com.teatroabc.aplicacao.dto;

/**
 * Data Transfer Object (DTO) para encapsular os dados necessários
 * para o cadastro de um novo cliente.
 * Esta classe é imutável, com todos os campos finais, garantindo a integridade
 * dos dados durante a transferência entre camadas.
 */
public class DadosCadastroClienteDTO {
    private final String cpf;
    private final String nome;
    private final String dataNascimentoStr; // Mantida como String para ser parseada e validada no serviço
    private final String telefone;
    private final String email;
    private final String identificadorPlanoFidelidade; // Ex: "PADRAO", "GOLD"

    /**
     * Construtor para DadosCadastroClienteDTO.
     *
     * @param cpf O CPF do cliente.
     * @param nome O nome completo do cliente.
     * @param dataNascimentoStr A data de nascimento do cliente como String (formato esperado: "dd/MM/yyyy").
     * @param telefone O número de telefone do cliente (pode ser nulo ou vazio).
     * @param email O endereço de e-mail do cliente (pode ser nulo ou vazio).
     * @param identificadorPlanoFidelidade O identificador textual do plano de fidelidade desejado.
     */
    public DadosCadastroClienteDTO(String cpf, String nome, String dataNascimentoStr,
                                   String telefone, String email, String identificadorPlanoFidelidade) {
        // Validações básicas de nulidade podem ser adicionadas aqui se desejado,
        // embora o serviço de aplicação também deva validar os dados recebidos.
        // Por exemplo:
        // if (cpf == null || nome == null || dataNascimentoStr == null) {
        //     throw new IllegalArgumentException("Campos obrigatórios (CPF, Nome, Data de Nascimento) não podem ser nulos.");
        // }
        this.cpf = cpf;
        this.nome = nome;
        this.dataNascimentoStr = dataNascimentoStr;
        this.telefone = telefone;
        this.email = email;
        this.identificadorPlanoFidelidade = identificadorPlanoFidelidade;
    }

    // --- Getters para acesso aos dados ---
    public String getCpf() { return cpf; }
    public String getNome() { return nome; }
    public String getDataNascimentoStr() { return dataNascimentoStr; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }
    public String getIdentificadorPlanoFidelidade() { return identificadorPlanoFidelidade; }
}