package com.teatroabc.infraestrutura.persistencia.implementacao;

import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.enums.CategoriaAssento;
import com.teatroabc.dominio.enums.StatusAssento;
import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.infraestrutura.persistencia.interfaces.IAssentoRepositorio; // Implementar
import com.teatroabc.infraestrutura.persistencia.util.GerenciadorArquivos;

import java.util.*;

public class AssentoRepositorio implements IAssentoRepositorio {

    @Override
    public List<Assento> buscarTodosAssentosPorPecaETurno(String idPeca, Turno turno) {
        if (idPeca == null || turno == null) {
            // Ou lançar IllegalArgumentException
            return Collections.emptyList();
        }

        List<Assento> todosAssentos = new ArrayList<>();
        // Usa o GerenciadorArquivos para saber quais códigos de assento estão ocupados
        // para a peça e turno específicos.
        Set<String> codigosOcupadosRegistrados = GerenciadorArquivos.buscarAssentosOcupados(idPeca, turno.name());

        // Frisas
        for (int f = 1; f <= 3; f++) {
            for (int a = 1; a <= 8; a++) {
                String codigo = "F" + f + "-" + a;
                CategoriaAssento cat = CategoriaAssento.FRISAS;
                // O preço é o precoBase da categoria, conforme definido no Assento construtor
                Assento assento = new Assento(codigo, f, a, cat, cat.getPrecoBase());
                if (codigosOcupadosRegistrados.contains(codigo) || isAssentoOcupadoSimulacao(codigo, idPeca, turno)) {
                    assento.setStatus(StatusAssento.OCUPADO);
                }
                todosAssentos.add(assento);
            }
        }
        // Balcão Nobre
        for (int f = 1; f <= 4; f++) {
            for (int a = 1; a <= 10; a++) {
                String codigo = "B" + f + "-" + a;
                CategoriaAssento cat = CategoriaAssento.BALCAO_NOBRE;
                Assento assento = new Assento(codigo, f, a, cat, cat.getPrecoBase());
                if (codigosOcupadosRegistrados.contains(codigo) || isAssentoOcupadoSimulacao(codigo, idPeca, turno)) {
                    assento.setStatus(StatusAssento.OCUPADO);
                }
                todosAssentos.add(assento);
            }
        }
        // Balcão
        for (int f = 1; f <= 4; f++) {
            for (int a = 1; a <= 10; a++) {
                String codigo = "C" + f + "-" + a;
                CategoriaAssento cat = CategoriaAssento.BALCAO;
                Assento assento = new Assento(codigo, f, a, cat, cat.getPrecoBase());
                if (codigosOcupadosRegistrados.contains(codigo) || isAssentoOcupadoSimulacao(codigo, idPeca, turno)) {
                    assento.setStatus(StatusAssento.OCUPADO);
                }
                todosAssentos.add(assento);
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