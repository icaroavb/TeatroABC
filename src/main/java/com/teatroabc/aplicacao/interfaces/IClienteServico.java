package com.teatroabc.aplicacao.interfaces; // Conforme sua estrutura de pacotes

import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.aplicacao.dto.DadosCadastroClienteDTO;
import com.teatroabc.aplicacao.excecoes.ClienteJaCadastradoException;

import java.util.Optional;

/**
 * Interface para o serviço de aplicação responsável pelas operações de negócio
 * relacionadas à entidade Cliente.
 * Define o contrato para cadastro, busca e verificação de existência de clientes.
 * Atua como uma Porta de Entrada na arquitetura hexagonal.
 */
public interface IClienteServico {

    /**
     * Cadastra um novo cliente no sistema com base nos dados fornecidos.
     *
     * @param dadosCadastro DTO contendo todas as informações necessárias para o cadastro do cliente.
     *                      Não pode ser nulo.
     * @return O objeto {@link Cliente} que foi cadastrado e persistido.
     * @throws IllegalArgumentException Se os {@code dadosCadastro} forem nulos, ou se campos
     *                                  obrigatórios dentro do DTO (como CPF, nome, data de nascimento)
     *                                  estiverem ausentes, vazios ou em formato inválido.
     * @throws ClienteJaCadastradoException Se já existir um cliente cadastrado no sistema
     *                                      com o CPF fornecido nos {@code dadosCadastro}.
     */
    Cliente cadastrar(DadosCadastroClienteDTO dadosCadastro) throws ClienteJaCadastradoException, IllegalArgumentException;

    /**
     * Busca um cliente pelo seu número de CPF.
     * O CPF fornecido será normalizado (removendo caracteres não numéricos) antes da consulta.
     *
     * @param cpf O número de CPF do cliente a ser buscado. Pode conter formatação.
     * @return Um {@link Optional} contendo o objeto {@link Cliente} se um cliente com o CPF
     *         especificado for encontrado; caso contrário, um {@link Optional#empty()}.
     *         Retorna {@link Optional#empty()} também se o CPF fornecido for nulo ou vazio.
     */
    Optional<Cliente> buscarPorCpf(String cpf);

    /**
     * Verifica se um cliente com o número de CPF especificado já existe no sistema.
     * O CPF fornecido será normalizado (removendo caracteres não numéricos) antes da verificação.
     *
     * @param cpf O número de CPF a ser verificado. Pode conter formatação.
     * @return {@code true} se um cliente com o CPF especificado existir, {@code false} caso contrário.
     *         Retorna {@code false} também se o CPF fornecido for nulo ou vazio.
     */
    boolean existe(String cpf);
}