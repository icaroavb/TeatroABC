package com.teatroabc.infraestrutura.persistencia.interfaces;

import com.teatroabc.dominio.modelos.Cliente;
import java.util.Optional; // Importa a classe Optional

/**
 * Interface (Porta de Saída) para o repositório de Clientes.
 * Define o contrato para operações de acesso a dados relacionadas aos clientes.
 */
public interface IClienteRepositorio {

    /**
     * Salva um novo cliente ou atualiza um existente na fonte de dados.
     * @param cliente O objeto Cliente a ser salvo.
     */
    void salvar(Cliente cliente);

    /**
     * Busca um cliente pelo seu número de CPF.
     *
     * @param cpf O CPF do cliente (já normalizado).
     * @return Um Optional contendo o Cliente se encontrado, ou um Optional vazio caso contrário.
     */
    Optional<Cliente> buscarPorCpf(String cpf); // MUDANÇA: O tipo de retorno agora é Optional<Cliente>

    /**
     * Verifica se um cliente com o CPF especificado existe.
     * @param cpf O CPF do cliente (já normalizado).
     * @return true se o cliente existe, false caso contrário.
     */
    boolean existe(String cpf);
}