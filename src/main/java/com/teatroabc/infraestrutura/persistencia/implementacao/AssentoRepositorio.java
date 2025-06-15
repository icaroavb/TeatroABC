package com.teatroabc.infraestrutura.persistencia.implementacao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.teatroabc.dominio.enums.CategoriaAssento;
import com.teatroabc.dominio.enums.StatusAssento;
import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.infraestrutura.config.ConfiguracaoPlantaTeatro;
import com.teatroabc.infraestrutura.config.SecaoConfig;
import com.teatroabc.infraestrutura.config.TeatroLayoutConfig;
import com.teatroabc.infraestrutura.persistencia.interfaces.IAssentoRepositorio;
import com.teatroabc.infraestrutura.persistencia.util.GerenciadorArquivos;

/**
 * Implementação do repositório de assentos que interage com a persistência em arquivos.
 * Esta classe é responsável por gerar a planta completa de assentos do teatro e verificar sua disponibilidade.
 * 
 * REFATORAÇÃO: Esta classe não contém mais lógica de layout hardcoded. Ela obtém a
 * estrutura do teatro (seções, fileiras, assentos por fileira) da classe
 * ConfiguracaoPlantaTeatro, agindo como um consumidor de configuração.
 * Isso desacopla a lógica de persistência da lógica de layout físico do teatro,
 * aderindo ao princípio de Fonte Única de Verdade (Single Source of Truth).
 */
public class AssentoRepositorio implements IAssentoRepositorio {

    /**
     * {@inheritDoc}
     * Gera dinamicamente a lista de todos os assentos para uma peça e turno,
     * baseando-se na configuração centralizada em {@link ConfiguracaoPlantaTeatro}.
     * O status de cada assento (Disponível/Ocupado) é definido consultando os
     * dados de persistência.
     */
    @Override
    public List<Assento> buscarTodosAssentosPorPecaETurno(String idPeca, Turno turno) {
        if (idPeca == null || turno == null) {
            // Lançar exceção ou retornar lista vazia é uma decisão de design.
            // Retornar lista vazia é mais seguro para a UI não quebrar com null.
            return Collections.emptyList();
        }

        List<Assento> todosAssentos = new ArrayList<>();
        // 1. Busca os códigos de assentos já ocupados da camada de persistência.
        Set<String> codigosOcupadosRegistrados = GerenciadorArquivos.buscarAssentosOcupados(idPeca, turno.name());

        // 2. Obtém a configuração da planta do teatro da fonte única de verdade.
        TeatroLayoutConfig layout = ConfiguracaoPlantaTeatro.getLayout();

        // 3. Itera sobre a CONFIGURAÇÃO para gerar os assentos, em vez de usar loops hardcoded.
        for (SecaoConfig secao : layout.getSecoes()) {
            // Usa a primeira letra do nome da seção para o prefixo do código do assento.
            // Ex: "Frisa" -> 'F', "Plateia A" -> 'P'
            char prefixoCodigo = secao.getNomeDaSecao().charAt(0);
            CategoriaAssento categoria = secao.getCategoria();

            // Itera com base no número de fileiras e assentos por fileira definidos na configuração.
            for (int numFileira = 1; numFileira <= secao.getNumeroDeFileiras(); numFileira++) {
                for (int numAssento = 1; numAssento <= secao.getAssentosPorFileira(); numAssento++) {
                    
                    String codigoAssento = String.format("%c%d-%d", prefixoCodigo, numFileira, numAssento);
                    
                    // Cria a instância do objeto de domínio Assento.
                    Assento assento = new Assento(codigoAssento, numFileira, numAssento, categoria, categoria.getPrecoBase());
                    
                    // 4. Define o status do assento.
                    // Um assento é ocupado se estiver no arquivo de persistência OU na simulação.
                    if (codigosOcupadosRegistrados.contains(codigoAssento) || isAssentoOcupadoSimulacao(codigoAssento, idPeca, turno)) {
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
     * Esta implementação não precisa ser alterada, pois sua lógica é independente
     * da geração da planta do teatro. Ela apenas verifica uma lista de códigos
     * contra os dados de persistência.
     */
    @Override
    public boolean verificarDisponibilidade(String idPeca, Turno turno, List<String> codigosAssentos) {
        if (idPeca == null || turno == null) return false;
        if (codigosAssentos == null || codigosAssentos.isEmpty()) {
            return true; // Nenhum assento para verificar, logo, todos "disponíveis".
        }
        
        Set<String> codigosOcupadosRegistrados = GerenciadorArquivos.buscarAssentosOcupados(idPeca, turno.name());

        for (String codigo : codigosAssentos) {
            if (codigosOcupadosRegistrados.contains(codigo) || isAssentoOcupadoSimulacao(codigo, idPeca, turno)) {
                // Se qualquer um dos assentos solicitados estiver ocupado, a verificação falha.
                return false;
            }
        }
        
        // Se o loop terminar, todos os assentos verificados estão disponíveis.
        return true;
    }

    /**
     * Lógica de simulação para marcar aleatoriamente alguns assentos como ocupados.
     * Isso adiciona variação na disponibilidade, útil para fins de demonstração
     * e teste, já que o arquivo assentos_turnos.txt pode não estar completo.
     * Em um sistema de produção, toda a ocupação viria exclusivamente da persistência.
     * 
     * @param codigoAssento O código do assento a ser verificado.
     * @param idPeca O ID da peça.
     * @param turno O turno da apresentação.
     * @return true se o assento for considerado ocupado pela simulação, false caso contrário.
     */
    private boolean isAssentoOcupadoSimulacao(String codigoAssento, String idPeca, Turno turno) {
        // Usa o hash do código, peça e turno para gerar um número pseudo-aleatório consistente.
        int hash = (codigoAssento + idPeca + turno.name()).hashCode();
        double probabilidade;
        
        switch (turno) {
            case MANHA: probabilidade = 0.10; break; // 10% de ocupação simulada de manhã
            case TARDE: probabilidade = 0.20; break; // 20% à tarde
            case NOITE: probabilidade = 0.30; break; // 30% à noite
            default:    probabilidade = 0.15;
        }
        
        // Retorna true se o resultado do hash estiver dentro da faixa de probabilidade.
        return (Math.abs(hash) % 100) < (probabilidade * 100);
    }
}