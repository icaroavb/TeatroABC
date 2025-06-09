package com.teatroabc.aplicacao.servicos; // Conforme sua estrutura de pacotes

import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.infraestrutura.persistencia.interfaces.IClienteRepositorio; // Interface do Repositório
import com.teatroabc.aplicacao.dto.DadosCadastroClienteDTO;
import com.teatroabc.aplicacao.excecoes.ClienteJaCadastradoException;
import com.teatroabc.aplicacao.interfaces.IClienteServico;
// import com.teatroabc.dominio.validadores.ValidadorCPF; // Opcional, para validação de formato de CPF

import java.util.Optional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Implementação da interface {@link IClienteServico} para gerenciar operações
 * de negócio relacionadas a clientes.
 * Esta classe orquestra a lógica de cadastro e consulta, validando dados e
 * interagindo com a camada de persistência através da interface {@link IClienteRepositorio}.
 */
public class ClienteServico implements IClienteServico {

    private final IClienteRepositorio clienteRepositorio;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Constrói uma instância de {@code ClienteServico} com a dependência do repositório.
     *
     * @param clienteRepositorio A implementação de {@link IClienteRepositorio} a ser utilizada
     *                           para operações de persistência de clientes. Não pode ser nulo.
     * @throws IllegalArgumentException se {@code clienteRepositorio} for nulo.
     */
    public ClienteServico(IClienteRepositorio clienteRepositorio) {
        if (clienteRepositorio == null) {
            throw new IllegalArgumentException("Repositório de clientes (IClienteRepositorio) não pode ser nulo.");
        }
        this.clienteRepositorio = clienteRepositorio;
    }

    /**
     * {@inheritDoc}
     */
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

        // Se ValidadorCPF.isValid for usado, este seria o local:
        // if (!ValidadorCPF.isValid(cpfNormalizado)) {
        //     throw new IllegalArgumentException("Formato de CPF fornecido é inválido.");
        // }

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
            throw new IllegalArgumentException("Formato de data de nascimento inválido. Use o formato dd/MM/yyyy. Detalhe: " + e.getMessage(), e);
        }
        
        // Validação de obrigatoriedade de telefone/email com base no plano pode ser feita aqui,
        // se essa lógica não estiver encapsulada na criação da entidade Cliente ou
        // se não for uma pré-condição verificada pela camada de UI/adaptador de entrada.

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
     */
    @Override
    public Optional<Cliente> buscarPorCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return Optional.empty();
        }
        String cpfNormalizado = normalizarCpf(cpf);
        // Corrigido: clienteRepositorio.buscarPorCpf já retorna Optional<Cliente>
        return clienteRepositorio.buscarPorCpf(cpfNormalizado); // será coadunado com a implementação concreta do repositório
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

    /**
     * Normaliza uma string de CPF, removendo todos os caracteres não numéricos.
     * Se a string de entrada for nula, retorna {@code null}, o que será tratado
     * pelas validações nos métodos públicos.
     *
     * @param cpf A string de CPF a ser normalizada.
     * @return O CPF contendo apenas dígitos, ou {@code null} se a entrada for {@code null}.
     */
    private String normalizarCpf(String cpf) {
        if (cpf == null) {
            return null;
        }
        return cpf.replaceAll("[^0-9]", "");
    }
}