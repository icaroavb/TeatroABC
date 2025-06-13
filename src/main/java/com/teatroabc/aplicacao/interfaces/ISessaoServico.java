// Arquivo: aplicacao/interfaces/ISessaoServico.java
package com.teatroabc.aplicacao.interfaces;

import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.modelos.Sessao;
import java.util.List;

/**
 * Interface (Porta de Entrada) para o serviço de aplicação responsável pelas
 * operações de negócio relacionadas às Sessões.
 * REFATORADO: Agora inclui a responsabilidade de buscar os assentos para uma sessão.
 */
public interface ISessaoServico {
    /**
     * Busca todas as sessões disponíveis para uma peça específica.
     *
     * @param idPeca O ID da peça para a qual as sessões devem ser buscadas.
     * @return Uma lista de objetos {@link Sessao}.
     */
    List<Sessao> buscarSessoesPorPeca(String idPeca);

    /**
     * Busca a planta de assentos para uma sessão específica, com seus status atualizados.
     *
     * @param sessao A sessão para a qual os assentos serão buscados.
     * @return Uma lista de objetos Assento.
     */
    List<Assento> buscarAssentosPorSessao(Sessao sessao);
}
