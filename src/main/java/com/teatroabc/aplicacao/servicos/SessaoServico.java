// Arquivo: aplicacao/servicos/SessaoServico.java
package com.teatroabc.aplicacao.servicos;

import com.teatroabc.aplicacao.interfaces.ISessaoServico;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.modelos.Sessao;
import com.teatroabc.infraestrutura.persistencia.interfaces.IAssentoRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.ISessaoRepositorio;

import java.util.Collections;
import java.util.List;

/**
 * Implementação do serviço de aplicação para gerenciar operações de Sessões.
 * REFATORADO: Agora também é responsável por orquestrar a busca de assentos
 * para uma sessão, dependendo do IAssentoRepositorio.
 */
public class SessaoServico implements ISessaoServico {

    private final ISessaoRepositorio sessaoRepositorio;
    private final IAssentoRepositorio assentoRepositorio; // Dependência adicionada

    /**
     * Constrói uma instância de {@code SessaoServico} com suas dependências.
     *
     * @param sessaoRepositorio O repositório para operações de persistência de sessões.
     * @param assentoRepositorio O repositório para operações de acesso aos assentos.
     */
    public SessaoServico(ISessaoRepositorio sessaoRepositorio, IAssentoRepositorio assentoRepositorio) {
        if (sessaoRepositorio == null) {
            throw new IllegalArgumentException("Repositório de sessões não pode ser nulo.");
        }
        if (assentoRepositorio == null) {
            throw new IllegalArgumentException("Repositório de assentos não pode ser nulo.");
        }
        this.sessaoRepositorio = sessaoRepositorio;
        this.assentoRepositorio = assentoRepositorio;
    }

    @Override
    public List<Sessao> buscarSessoesPorPeca(String idPeca) {
        if (idPeca == null || idPeca.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return this.sessaoRepositorio.buscarSessoesPorPeca(idPeca);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Assento> buscarAssentosPorSessao(Sessao sessao) {
        if (sessao == null) {
            return Collections.emptyList();
        }
        // Delega a busca de assentos para o repositório de assentos,
        // passando a sessão como contexto.
        return this.assentoRepositorio.buscarAssentosPorSessao(sessao);
    }
}
