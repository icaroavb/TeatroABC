// Arquivo: infraestrutura/persistencia/interfaces/IAssentoRepositorio.java
package com.teatroabc.infraestrutura.persistencia.interfaces;

import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.modelos.Sessao;
import java.util.List;

/**
 * Interface (Porta de Saída) para o repositório de Assentos.
 * Define o contrato para operações de acesso a dados relacionadas aos assentos,
 * agora centradas no conceito de Sessao.
 */
public interface IAssentoRepositorio {
    /**
     * Retorna a planta de assentos completa para uma sessão específica, com o status
     * de cada assento (Disponível, Ocupado) devidamente atualizado.
     *
     * @param sessao A sessão para a qual a planta de assentos será buscada.
     * @return Uma lista de todos os Assentos para a sessão.
     */
    List<Assento> buscarAssentosPorSessao(Sessao sessao);

    /**
     * Verifica se um conjunto específico de assentos está disponível para uma dada sessão.
     *
     * @param sessao A sessão na qual a verificação será feita.
     * @param codigosAssentos A lista de códigos de assento a serem verificados.
     * @return true se todos os assentos estiverem disponíveis, false caso contrário.
     */
    boolean verificarDisponibilidade(Sessao sessao, List<String> codigosAssentos);
}
