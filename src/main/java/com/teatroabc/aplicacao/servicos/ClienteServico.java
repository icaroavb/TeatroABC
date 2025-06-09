package com.teatroabc.aplicacao.servicos;

import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.infraestrutura.persistencia.interfaces.IClienteRepositorio; // Dependência da INTERFACE
import com.teatroabc.aplicacao.dto.DadosCadastroClienteDTO; // USAR O DTO
import com.teatroabc.aplicacao.excecoes.ClienteJaCadastradoException;
import com.teatroabc.aplicacao.interfaces.IClienteServico;
// import com.teatroabc.dominio.validadores.ValidadorCPF;

import java.util.Optional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Implementação do serviço de aplicação para operações relacionadas a Clientes.
 * Orquestra a lógica de negócio e interage com o repositório de clientes.
 */
public class ClienteServico implements IClienteServico {

    private final IClienteRepositorio clienteRepositorio;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Construtor que recebe a dependência do repositório de clientes.
     * @param clienteRepositorio Implementação da interface IClienteRepositorio.
     */
    public ClienteServico(IClienteRepositorio clienteRepositorio) {
        if (clienteRepositorio == null) {
            throw new IllegalArgumentException("Repositório de clientes não pode ser nulo.");
        }
        this.clienteRepositorio = clienteRepositorio;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cliente cadastrar(DadosCadastroClienteDTO dadosCadastro) throws ClienteJaCadastradoException, IllegalArgumentException { // PARÂMETRO CORRIGIDO
        if (dadosCadastro == null) {
            throw new IllegalArgumentException("Dados de cadastro não podem ser nulos.");
        }

        // Validação e Normalização do CPF a partir do DTO
        String cpf = dadosCadastro.getCpf();
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF não pode ser nulo ou vazio nos dados de cadastro.");
        }
        String cpfNormalizado = normalizarCpf(cpf);

        // if (!ValidadorCPF.isValid(cpfNormalizado)) {
        //     throw new IllegalArgumentException("Formato de CPF inválido.");
        // }

        if (clienteRepositorio.existe(cpfNormalizado)) {
            throw new ClienteJaCadastradoException("Já existe um cliente cadastrado com o CPF: " + dadosCadastro.getCpf());
        }

        LocalDate dataNascimentoLocalDate;
        try {
            String dataNascimentoStr = dadosCadastro.getDataNascimentoStr();
            if (dataNascimentoStr == null || dataNascimentoStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Data de nascimento não pode ser nula ou vazia nos dados de cadastro.");
            }
            dataNascimentoLocalDate = LocalDate.parse(dataNascimentoStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de data de nascimento inválido. Use dd/MM/yyyy.", e);
        }

        String nomeCliente = dadosCadastro.getNome();
        if (nomeCliente == null || nomeCliente.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do cliente não pode ser nulo ou vazio nos dados de cadastro.");
        }

        // Criação da entidade Cliente usando dados do DTO
        Cliente novoCliente = new Cliente(
                cpfNormalizado,
                nomeCliente, // Usando dados do DTO
                dataNascimentoLocalDate,
                dadosCadastro.getTelefone(), // Usando dados do DTO
                dadosCadastro.getEmail(),    // Usando dados do DTO
                dadosCadastro.getIdentificadorPlanoFidelidade() // Usando dados do DTO
        );

        clienteRepositorio.salvar(novoCliente);
        return novoCliente;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Cliente> buscarPorCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return Optional.empty();
        }
        String cpfNormalizado = normalizarCpf(cpf);
        return Optional.ofNullable(clienteRepositorio.buscarPorCpf(cpfNormalizado));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existe(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return false;
        }
        String cpfNormalizado = normalizarCpf(cpf);
        return clienteRepositorio.existe(cpfNormalizado);
    }

    private String normalizarCpf(String cpf) {
        if (cpf == null) return null;
        return cpf.replaceAll("[^0-9]", "");
    }
}