package com.teatroabc.infraestrutura.persistencia.implementacao;

import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.enums.CategoriaAssento;
import com.teatroabc.dominio.enums.StatusAssento;
import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.infraestrutura.config.ConfiguracaoPlantaTeatro;
import com.teatroabc.infraestrutura.config.SecaoConfig;
import com.teatroabc.infraestrutura.config.TeatroLayoutConfig;
import com.teatroabc.infraestrutura.persistencia.interfaces.IAssentoRepositorio; // Implementar
import com.teatroabc.infraestrutura.persistencia.util.GerenciadorArquivos;

import java.util.*;

public class AssentoRepositorio implements IAssentoRepositorio {

     @Override
    public List<Assento> buscarTodosAssentosPorPecaETurno(String idPeca, Turno turno) {
        if (idPeca == null || turno == null) {
            return Collections.emptyList();
        }

        List<Assento> todosAssentos = new ArrayList<>();
        Set<String> codigosOcupados = GerenciadorArquivos.buscarAssentosOcupados(idPeca, turno.name());

        // Busca a configuração da planta do teatro da nossa fonte única de verdade
        TeatroLayoutConfig layout = ConfiguracaoPlantaTeatro.getLayout();

        // Itera sobre a configuração para gerar os assentos
        for (SecaoConfig secao : layout.getSecoes()) {
            char prefixo = secao.getNomeDaSecao().charAt(0); // Ex: 'F' para Frisa, 'P' para Plateia
            
            for (int f = 1; f <= secao.getNumeroDeFileiras(); f++) {
                for (int a = 1; a <= secao.getAssentosPorFileira(); a++) {
                    String codigo = String.format("%c%d-%d", prefixo, f, a);
                    CategoriaAssento categoria = secao.getCategoria();
                    
                    Assento assento = new Assento(codigo, f, a, categoria, categoria.getPrecoBase());
                    
                    if (codigosOcupados.contains(codigo)) {
                        assento.setStatus(StatusAssento.OCUPADO);
                    }
                    todosAssentos.add(assento);
                }
            }
        }
        return todosAssentos;
    }

    @Override
    public boolean verificarDisponibilidade(String idPeca, Turno turno, List<String> codigosAssentos) {
        if (idPeca == null || turno == null) return false; // Ou lançar exceção
        if (codigosAssentos == null || codigosAssentos.isEmpty()) {
            return true; // Nenhum assento para verificar, considera disponível
        }
        Set<String> codigosOcupadosRegistrados = GerenciadorArquivos.buscarAssentosOcupados(idPeca, turno.name());

        for (String codigo : codigosAssentos) {
            if (codigosOcupadosRegistrados.contains(codigo) || isAssentoOcupadoSimulacao(codigo, idPeca, turno)) {
                // Log de depuração
                // System.out.println("AssentoRepositorio: Verificação falhou para " + codigo +
                //                    " (Peça: " + idPeca + ", Turno: " + turno.name() + ")");
                return false; // Encontrou um ocupado
            }
        }
        return true; // Todos os assentos verificados estão disponíveis
    }

    // Lógica de simulação para assentos que não estão explicitamente marcados como ocupados no arquivo.
    // Mantida para dar alguma variação na disponibilidade se o arquivo `assentos_turnos.txt`
    // não estiver completo para todos os cenários de ocupação "natural".
    // Idealmente, toda ocupação viria da persistência (arquivo `assentos_turnos.txt`).
    private boolean isAssentoOcupadoSimulacao(String codigoAssento, String idPeca, Turno turno) {
        int hash = (codigoAssento + idPeca + turno.name()).hashCode();
        double probabilidade;
        switch (turno) {
            case MANHA: probabilidade = 0.10; break;
            case TARDE: probabilidade = 0.20; break;
            case NOITE: probabilidade = 0.30; break;
            default: probabilidade = 0.15;
        }
        return (Math.abs(hash) % 100) < (probabilidade * 100);
    }
}