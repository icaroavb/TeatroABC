package com.teatroabc.infraestrutura.persistencia.interfaces;

import com.teatroabc.dominio.modelos.Sessao;
import java.util.List;

/**
 * Interface (Porta de Saída) para o repositório de Sessões.
 * Define o contrato para operações de acesso a dados relacionadas às sessões,
 * como buscar todas as sessões disponíveis para uma determinada peça.
 */
public interface ISessaoRepositorio {

    /**
     * Busca todas as sessões disponíveis para uma peça específica.
     * @param idPeca O ID da peça para a qual as sessões devem ser buscadas.
     * @return Uma lista de objetos {@link Sessao}. A lista pode ser vazia se não
     *         houver sessões cadastradas para a peça.
     */
    List<Sessao> buscarSessoesPorPeca(String idPeca);
    
    // /**
    //  * Busca uma sessão específica pelo seu ID único.
    //  * @param idSessao O ID da sessão a ser buscada.
    //  * @return Um {@link Optional} contendo a {@link Sessao} se encontrada,
    //  *         ou um Optional vazio caso contrário.
    //  */
    // Optional<Sessao> buscarPorId(String idSessao);

    // // No futuro, poderíamos ter métodos como:
    // // List<Sessao> buscarSessoesPorData(LocalDate data);
}