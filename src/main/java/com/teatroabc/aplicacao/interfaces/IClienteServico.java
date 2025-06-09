package com.teatroabc.aplicacao.interfaces;

import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.aplicacao.dto.DadosCadastroClienteDTO;
import com.teatroabc.aplicacao.excecoes.ClienteJaCadastradoException;
import java.util.Optional;

public interface IClienteServico {

    /**
     * Cadastra um novo cliente no sistema.
     * @param dadosCadastro DTO contendo todas as informações necessárias para o cadastro.
     * @return O objeto Cliente cadastrado.
     * @throws IllegalArgumentException Se os dados de cadastro forem inválidos (ex: formato de data).
     * @throws ClienteJaCadastradoException Se já existir um cliente com o CPF fornecido.
     */
    Cliente cadastrar(DadosCadastroClienteDTO dadosCadastro) throws ClienteJaCadastradoException, IllegalArgumentException;

    /**
     * Busca um cliente pelo seu CPF.
     * @param cpf O CPF do cliente a ser buscado.
     * @return Um Optional contendo o Cliente se encontrado, ou Optional.empty() caso contrário.
     */
    Optional<Cliente> buscarPorCpf(String cpf);

    /**
     * Verifica se um cliente com o CPF especificado já existe no sistema.
     * @param cpf O CPF a ser verificado.
     * @return true se o cliente existe, false caso contrário.
     */
    boolean existe(String cpf);
}