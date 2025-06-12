package com.teatroabc.infraestrutura.persistencia.interfaces;

import com.teatroabc.dominio.modelos.Bilhete;
import java.util.List;
import java.util.Optional;

/**
 * Interface (Porta de Saída) para o repositório de Bilhetes.
 * Define o contrato para operações de acesso a dados relacionadas aos bilhetes.
 */
public interface IBilheteRepositorio {

    /**
     * Salva um bilhete na fonte de dados.
     * O método agora recebe apenas o objeto Bilhete, pois toda a informação
     * necessária (incluindo o turno para marcar os assentos) pode ser
     * extraída dele.
     *
     * @param bilhete O objeto Bilhete a ser salvo.
     */
    void salvar(Bilhete bilhete);

    /**
     * Busca todos os bilhetes de um cliente específico pelo CPF.
     * @param cpf O CPF do cliente.
     * @return Uma lista de Bilhetes. Pode ser vazia.
     */
    List<Bilhete> listarPorCpfCliente(String cpf);

    /**
     * Busca um bilhete específico pelo seu ID.
     * @param id O ID do bilhete.
     * @return Um Optional contendo o Bilhete se encontrado.
     */
    Optional<Bilhete> buscarPorId(String id);
}