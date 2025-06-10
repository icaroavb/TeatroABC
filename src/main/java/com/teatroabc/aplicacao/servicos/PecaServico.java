package com.teatroabc.aplicacao.servicos;

import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.infraestrutura.persistencia.interfaces.IAssentoRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.IPecaRepositorio;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementação do serviço de aplicação para operações relacionadas à entidade Peça.
 * Orquestra a busca de informações sobre peças através do repositório de peças.
 */
public class PecaServico implements IPecaServico {

    private final IPecaRepositorio pecaRepositorio; // Dependência da Porta de Saída (Interface)
    private final IAssentoRepositorio assentoRepositorio; // NOVA DEPENDÊNCIA


    /**
     * Construtor que recebe as dependências dos repositórios.
     */
    public PecaServico(IPecaRepositorio pecaRepositorio, IAssentoRepositorio assentoRepositorio) { // CONSTRUTOR ATUALIZADO
        if (pecaRepositorio == null) {
            throw new IllegalArgumentException("Repositório de peças não pode ser nulo.");
        }
        if (assentoRepositorio == null) {
            throw new IllegalArgumentException("Repositório de assentos não pode ser nulo.");
        }
        this.pecaRepositorio = pecaRepositorio;
        this.assentoRepositorio = assentoRepositorio; 
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Peca> buscarTodasPecas() {
        List<Peca> pecas = pecaRepositorio.listarTodas();
        // Retorna uma lista vazia imutável se o repositório retornar nulo, por segurança,
        // embora a interface IPecaRepositorio deva garantir o retorno de uma lista.
        return pecas != null ? pecas : Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Peca> buscarPecaPorId(String id) {
        if (id == null || id.trim().isEmpty()) {
            // O serviço de aplicação pode decidir se lança exceção ou retorna Optional.empty
            // para IDs inválidos antes de consultar o repositório.
            // Lançar IllegalArgumentException seria mais explícito sobre a entrada inválida.
            // throw new IllegalArgumentException("ID da peça não pode ser nulo ou vazio.");
            return Optional.empty(); // Mantendo consistência com a interface
        }
        return pecaRepositorio.buscarPorId(id); // Delega para o repositório que já retorna Optional
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Assento> buscarAssentosDaPecaPorTurno(String idPeca, Turno turno) { // IMPLEMENTAÇÃO DO NOVO MÉTODO
        if (idPeca == null || idPeca.trim().isEmpty() || turno == null) {
            // Ou lançar IllegalArgumentException
            return Collections.emptyList();
        }
        // Delega a busca para o repositório de assentos
        return assentoRepositorio.buscarTodosAssentosPorPecaETurno(idPeca, turno);
    }
}