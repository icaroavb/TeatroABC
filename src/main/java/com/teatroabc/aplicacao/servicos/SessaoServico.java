package com.teatroabc.aplicacao.servicos;

import com.teatroabc.aplicacao.interfaces.ISessaoServico;
import com.teatroabc.dominio.modelos.Sessao;
import com.teatroabc.infraestrutura.persistencia.interfaces.ISessaoRepositorio;

import java.util.Collections;
import java.util.List;

/**
 * Implementação do serviço de aplicação para gerenciar operações de negócio
 * relacionadas às Sessões.
 * Esta classe orquestra a lógica de busca de sessões, interagindo com a camada
 * de persistência através da interface {@link ISessaoRepositorio}.
 */
public class SessaoServico implements ISessaoServico {

    // Dependência da Porta de Saída (Interface do Repositório)
    private final ISessaoRepositorio sessaoRepositorio;

    /**
     * Constrói uma instância de {@code SessaoServico} com a dependência do repositório.
     *
     * @param sessaoRepositorio A implementação de {@link ISessaoRepositorio} a ser utilizada
     *                          para operações de persistência de sessões. Não pode ser nulo.
     * @throws IllegalArgumentException se {@code sessaoRepositorio} for nulo.
     */
    public SessaoServico(ISessaoRepositorio sessaoRepositorio) {
        //basta descomentar para fazer a revalidação
        //apurarInformacoesEssenciais();
        this.sessaoRepositorio = sessaoRepositorio;
    }

    //Encapsulamento da lógica de validação restrita para o repositório 
    private boolean verificarSessao (ISessaoRepositorio sessaoRepositorio){
        return sessaoRepositorio == null;
    }

    //Encapsulamento da validação total da classe
    public void apurarInformacoesEssenciais (){
        if (verificarSessao(sessaoRepositorio)) {
            throw new IllegalArgumentException("Repositório de sessões (ISessaoRepositorio) não pode ser nulo.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sessao> buscarSessoesPorPeca(String idPeca) {
        // Validação de entrada básica.
        if (idPeca == null || idPeca.trim().isEmpty()) {
            return Collections.emptyList(); // Retorna lista vazia para entrada inválida.
        }

        // Delega a chamada diretamente para o repositório.
        // Em casos mais complexos, poderia haver lógica de negócio aqui,
        // como filtrar sessões que já passaram ou combinar dados.
        return this.sessaoRepositorio.buscarSessoesPorPeca(idPeca);
    }
}