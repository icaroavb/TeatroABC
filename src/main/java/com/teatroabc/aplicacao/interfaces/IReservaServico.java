package com.teatroabc.aplicacao.interfaces;

import com.teatroabc.dominio.modelos.Bilhete;
import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.aplicacao.excecoes.ReservaInvalidaException; // Exceção de negócio

import java.util.List;
import java.util.Optional;

/**
 * Interface para o serviço de aplicação responsável pela lógica de criação de reservas (bilhetes)
 * e consulta de bilhetes existentes.
 * Atua como uma Porta de Entrada na arquitetura hexagonal.
 */
public interface IReservaServico {

    /**
     * Tenta criar uma reserva e, se bem-sucedida, gera um Bilhete.
     * Esta operação envolve verificar a disponibilidade dos assentos, calcular o valor total
     * (considerando descontos do cliente) e persistir o bilhete.
     *
     * @param peca A peça selecionada para a reserva.
     * @param cliente O cliente que está fazendo a reserva.
     * @param assentosSelecionados A lista de objetos Assento que o cliente selecionou.
     * @param turno O turno da apresentação para a qual a reserva está sendo feita.
     * @return O objeto Bilhete criado e persistido.
     * @throws ReservaInvalidaException Se a reserva não puder ser completada devido a regras de negócio
     *                                  (ex: assentos indisponíveis, dados de entrada inválidos que não
     *                                  foram pegos por validações mais básicas).
     * @throws IllegalArgumentException Se os parâmetros de entrada forem fundamentalmente inválidos (ex: nulos).
     */
    Bilhete criarReserva(Peca peca, Cliente cliente, List<Assento> assentosSelecionados, Turno turno)
            throws ReservaInvalidaException, IllegalArgumentException;

    /**
     * Busca todos os bilhetes associados a um CPF de cliente.
     *
     * @param cpf O CPF do cliente (será normalizado internamente).
     * @return Uma lista de objetos Bilhete. A lista pode ser vazia se nenhum bilhete for encontrado.
     */
    List<Bilhete> buscarBilhetesCliente(String cpf);

    /**
     * Busca um bilhete específico pelo seu ID único.
     *
     * @param idBilhete O ID do bilhete a ser buscado.
     * @return Um Optional contendo o Bilhete se encontrado, ou Optional.empty() caso contrário.
     */
    Optional<Bilhete> buscarBilhetePorId(String idBilhete);
}