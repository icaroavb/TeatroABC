// Arquivo: aplicacao/servicos/PecaServico.java
package com.teatroabc.aplicacao.servicos;

import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.infraestrutura.persistencia.interfaces.IPecaRepositorio;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementação do serviço de aplicação para operações relacionadas à entidade Peça.
 * REFATORADO: Não possui mais dependência ou conhecimento sobre repositórios de assentos,
 * focando exclusivamente na orquestração das buscas de Peças.
 */
public class PecaServico implements IPecaServico {

    private final IPecaRepositorio pecaRepositorio; // A única dependência é a porta de saída para peças.

    /**
     * Construtor que recebe a dependência do repositório de peças.
     * @param pecaRepositorio A implementação de IPecaRepositorio a ser usada.
     */
    public PecaServico(IPecaRepositorio pecaRepositorio) {
        if (pecaRepositorio == null) {
            throw new IllegalArgumentException("Repositório de peças não pode ser nulo.");
        }
        this.pecaRepositorio = pecaRepositorio;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Peca> buscarTodasPecas() {
        // Delega a chamada diretamente para o repositório.
        List<Peca> pecas = pecaRepositorio.listarTodas();
        // Garante que nunca retorne nulo, por segurança.
        return pecas != null ? pecas : Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Peca> buscarPecaPorId(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }
        // Delega a chamada diretamente para o repositório.
        return pecaRepositorio.buscarPorId(id);
    }
}
