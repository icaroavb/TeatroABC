package com.teatroabc.aplicacao.servicos;

import com.teatroabc.aplicacao.dto.DadosCadastroClienteDTO;
import com.teatroabc.aplicacao.excecoes.ClienteJaCadastradoException;
import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.infraestrutura.persistencia.interfaces.IClienteRepositorio;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * Implementação da interface {@link IClienteServico} para gerenciar operações
 * de negócio relacionadas a clientes.
 * Esta classe orquestra a lógica de cadastro e consulta, validando dados e
 * interagindo com a camada de persistência através da interface {@link IClienteRepositorio}.
 */
public class ClienteServico implements IClienteServico {

    private final IClienteRepositorio clienteRepositorio;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ClienteServico(IClienteRepositorio clienteRepositorio) {
        if (clienteRepositorio == null) {
            throw new IllegalArgumentException("Repositório de clientes (IClienteRepositorio) não pode ser nulo.");
        }
        this.clienteRepositorio = clienteRepositorio;
    }    

    @Override
    public Cliente cadastrar(DadosCadastroClienteDTO dadosCadastro) throws ClienteJaCadastradoException, IllegalArgumentException {
        if (dadosCadastro == null) {
            throw new IllegalArgumentException("Dados de cadastro (DadosCadastroClienteDTO) não podem ser nulos.");
        }

        String cpf = dadosCadastro.getCpf();
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF não pode ser nulo ou vazio nos dados de cadastro.");
        }
        String cpfNormalizado = normalizarCpf(cpf);

        if (clienteRepositorio.existe(cpfNormalizado)) {
            throw new ClienteJaCadastradoException("Já existe um cliente cadastrado com o CPF: " + dadosCadastro.getCpf());
        }

        String nomeCliente = dadosCadastro.getNome();
        if (nomeCliente == null || nomeCliente.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do cliente não pode ser nulo ou vazio nos dados de cadastro.");
        }

        String dataNascimentoStr = dadosCadastro.getDataNascimentoStr();
        if (dataNascimentoStr == null || dataNascimentoStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Data de nascimento não pode ser nula ou vazia nos dados de cadastro.");
        }
        LocalDate dataNascimentoLocalDate;
        try {
            dataNascimentoLocalDate = LocalDate.parse(dataNascimentoStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de data de nascimento inválido. Use o formato dd/MM/yyyy.", e);
        }
        
        Cliente novoCliente = new Cliente(
                cpfNormalizado,
                nomeCliente,
                dataNascimentoLocalDate,
                dadosCadastro.getTelefone(),
                dadosCadastro.getEmail(),
                dadosCadastro.getIdentificadorPlanoFidelidade()
        );
        
        clienteRepositorio.salvar(novoCliente);
        return novoCliente;
    }

    /**
     * {@inheritDoc}
     * A chamada ao repositório foi simplificada, pois o repositório agora retorna
     * diretamente um Optional, eliminando a necessidade de Optional.ofNullable.
     */
    @Override
    public Optional<Cliente> buscarPorCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return Optional.empty();
        }
        String cpfNormalizado = normalizarCpf(cpf);
        // MUDANÇA: A chamada agora é direta.
        return clienteRepositorio.buscarPorCpf(cpfNormalizado);
    }

    @Override
    public boolean existe(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return false;
        }
        String cpfNormalizado = normalizarCpf(cpf);
        return clienteRepositorio.existe(cpfNormalizado);
    }

    private String normalizarCpf(String cpf) {
        if (cpf == null) {
            return null;
        }
        return cpf.replaceAll("[^0-9]", "");
    }
}