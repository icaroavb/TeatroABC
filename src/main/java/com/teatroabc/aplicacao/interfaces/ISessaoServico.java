package com.teatroabc.aplicacao.interfaces;

import com.teatroabc.dominio.modelos.Sessao;
import java.util.List;

/**
 * Interface (Porta de Entrada) para o serviço de aplicação responsável pelas
 * operações de negócio relacionadas às Sessões.
 * Define o contrato para buscar informações sobre as sessões disponíveis.
 */
public interface ISessaoServico {

    /**
     * Busca todas as sessões disponíveis para uma peça específica,
     * prontas para serem exibidas na UI.
     *
     * @param idPeca O ID da peça para a qual as sessões devem ser buscadas.
     * @return Uma lista de objetos {@link Sessao}. A lista pode ser vazia se não
     *         houver sessões para a peça ou se o ID for inválido.
     */
    List<Sessao> buscarSessoesPorPeca(String idPeca);
    
    // Futuramente, poderíamos adicionar outros métodos de negócio, como:
    // void cancelarSessao(String idSessao);
    // Sessao agendarNovaSessao(DadosNovaSessaoDTO dados);
}