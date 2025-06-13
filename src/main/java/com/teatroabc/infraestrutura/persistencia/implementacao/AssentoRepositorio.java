// Arquivo: infraestrutura/persistencia/implementacao/AssentoRepositorio.java
package com.teatroabc.infraestrutura.persistencia.implementacao;

import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.modelos.Sessao;
import com.teatroabc.dominio.enums.CategoriaAssento;
import com.teatroabc.dominio.enums.StatusAssento;
import com.teatroabc.infraestrutura.persistencia.interfaces.IAssentoRepositorio;
import com.teatroabc.infraestrutura.persistencia.util.GerenciadorArquivos;
import com.teatroabc.infraestrutura.config.ConfiguracaoPlantaTeatro;
import com.teatroabc.infraestrutura.config.SecaoConfig;
import com.teatroabc.infraestrutura.config.TeatroLayoutConfig;

import java.util.*;

/**
 * Implementação do repositório de assentos que interage com a persistência em arquivos.
 * REFATORADO: A lógica agora é centrada na entidade Sessao. A disponibilidade de
 * assentos é consultada usando o ID da sessão, o que torna o modelo mais consistente.
 */
public class AssentoRepositorio implements IAssentoRepositorio {

    /**
     * {@inheritDoc}
     * Gera dinamicamente a lista de todos os assentos para uma sessão,
     * baseando-se na configuração centralizada e marcando como ocupados
     * aqueles registrados para o ID da sessão específica.
     */
    @Override
    public List<Assento> buscarAssentosPorSessao(Sessao sessao) {
        if (sessao == null) {
            return Collections.emptyList();
        }

        List<Assento> todosAssentos = new ArrayList<>();
        // 1. Busca os códigos de assentos já ocupados para esta SESSÃO específica.
        Set<String> codigosOcupados = GerenciadorArquivos.buscarAssentosOcupados(sessao.getId());

        // 2. Obtém a configuração da planta do teatro.
        TeatroLayoutConfig layout = ConfiguracaoPlantaTeatro.getLayout();

        // 3. Itera sobre a CONFIGURAÇÃO para gerar a planta de assentos.
        for (SecaoConfig secaoConfig : layout.getSecoes()) {
            char prefixoCodigo = secaoConfig.getNomeDaSecao().charAt(0);
            CategoriaAssento categoria = secaoConfig.getCategoria();

            for (int numFileira = 1; numFileira <= secaoConfig.getNumeroDeFileiras(); numFileira++) {
                for (int numAssento = 1; numAssento <= secaoConfig.getAssentosPorFileira(); numAssento++) {
                    
                    String codigoAssento = String.format("%c%d-%d", prefixoCodigo, numFileira, numAssento);
                    
                    Assento assento = new Assento(codigoAssento, numFileira, numAssento, categoria, categoria.getPrecoBase());
                    
                    // 4. Define o status do assento: Ocupado se estiver na lista da sessão ou na simulação.
                    if (codigosOcupados.contains(codigoAssento) || isAssentoOcupadoSimulacao(sessao, codigoAssento)) {
                        assento.setStatus(StatusAssento.OCUPADO);
                    }
                    
                    todosAssentos.add(assento);
                }
            }
        }
        
        return todosAssentos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean verificarDisponibilidade(Sessao sessao, List<String> codigosAssentos) {
        if (sessao == null) return false;
        if (codigosAssentos == null || codigosAssentos.isEmpty()) {
            return true;
        }
        
        Set<String> codigosOcupados = GerenciadorArquivos.buscarAssentosOcupados(sessao.getId());

        for (String codigo : codigosAssentos) {
            if (codigosOcupados.contains(codigo) || isAssentoOcupadoSimulacao(sessao, codigo)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Lógica de simulação para marcar aleatoriamente alguns assentos como ocupados.
     * Em um sistema real, a ocupação viria apenas da persistência.
     */
    private boolean isAssentoOcupadoSimulacao(Sessao sessao, String codigoAssento) {
        // Usa o hash do ID da sessão e do código do assento para gerar um número pseudo-aleatório consistente.
        int hash = (sessao.getId() + codigoAssento).hashCode();
        double probabilidade;
        
        switch (sessao.getTurno()) {
            case MANHA: probabilidade = 0.10; break;
            case TARDE: probabilidade = 0.20; break;
            case NOITE: probabilidade = 0.30; break;
            default:    probabilidade = 0.15;
        }
        
        return (Math.abs(hash) % 100) < (probabilidade * 100);
    }
}
