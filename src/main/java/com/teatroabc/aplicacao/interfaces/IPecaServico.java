package com.teatroabc.aplicacao.interfaces;


import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.modelos.Peca;
import java.util.List; // Novo import
import java.util.Optional; 

/**
 * Interface para o serviço de aplicação relacionado à entidade Peça.
 * Define as operações de negócio para buscar informações sobre peças.
 * Atua como uma Porta de Entrada na arquitetura hexagonal.
 */
public interface IPecaServico {

    /**
     * Busca todas as peças cadastradas no sistema.
     * @return Uma lista de objetos Peca. A lista pode ser vazia se não houver peças.
     */
    List<Peca> buscarTodasPecas();

    /**
     * Busca uma peça específica pelo seu ID.
     * @param id O ID único da peça a ser buscada.
     * @return Um Optional contendo o objeto Peca se encontrado, ou Optional.empty() caso contrário.
     *         Retorna Optional.empty() também se o ID fornecido for nulo ou vazio.
     */
    Optional<Peca> buscarPecaPorId(String id);

    /**
     * Busca todos os assentos para uma determinada peça e turno,
     * com seus status de disponibilidade atualizados.
     * @param idPeca O ID da peça.
     * @param turno O turno da apresentação.
     * @return Uma lista de objetos Assento.
     */
    List<Assento> buscarAssentosDaPecaPorTurno(String idPeca, Turno turno);    
}