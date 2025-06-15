package com.teatroabc.aplicacao.interfaces;

import com.teatroabc.aplicacao.excecoes.ReservaInvalidaException;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.modelos.Bilhete; // MUDANÇA: Usa a nova entidade Sessao
import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.dominio.modelos.Sessao;
import java.util.List;
import java.util.Optional;

/**
 * Interface (Porta de Entrada) para o serviço de aplicação responsável pela lógica
 * de criação de reservas (bilhetes) e consulta de bilhetes existentes.
 * 
 * Esta interface define a API pública para os casos de uso de reserva.
 */
public interface IReservaServico {

    /**
     * Tenta criar uma reserva para uma sessão específica e, se bem-sucedida, gera um Bilhete.
     * Esta operação envolve verificar a disponibilidade dos assentos, calcular o valor total
     * (considerando descontos do cliente) e persistir o bilhete e a ocupação dos assentos.
     * 
     * A assinatura deste método foi refatorada para receber um objeto {@link Sessao},
     * tornando o contrato mais coeso e alinhado com o domínio.
     *
     * @param sessao A sessão (peça, data, turno) para a qual a reserva está sendo feita.
     * @param cliente O cliente que está fazendo a reserva.
     * @param assentosSelecionados A lista de objetos Assento que o cliente selecionou.
     * @return O objeto {@link Bilhete} criado e persistido.
     * @throws ReservaInvalidaException Se a reserva não puder ser completada devido a regras de negócio
     *                                  (ex: assentos indisponíveis, violações de regras).
     * @throws IllegalArgumentException Se os parâmetros de entrada forem fundamentalmente inválidos (ex: nulos).
     */
    Bilhete criarReserva(Sessao sessao, Cliente cliente, List<Assento> assentosSelecionados)
            throws ReservaInvalidaException, IllegalArgumentException;

    /**
     * Busca todos os bilhetes associados a um CPF de cliente.
     *
     * @param cpf O CPF do cliente (será normalizado internamente).
     * @return Uma lista de objetos {@link Bilhete}. A lista pode ser vazia se nenhum bilhete for encontrado.
     */
    List<Bilhete> buscarBilhetesCliente(String cpf);

    /**
     * Busca um bilhete específico pelo seu ID único.
     *
     * @param idBilhete O ID do bilhete a ser buscado.
     * @return Um {@link Optional} contendo o Bilhete se encontrado, ou um Optional vazio caso contrário.
     */
    Optional<Bilhete> buscarBilhetePorId(String idBilhete);
}