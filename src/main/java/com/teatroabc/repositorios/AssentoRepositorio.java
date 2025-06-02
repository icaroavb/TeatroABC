package com.teatroabc.repositorios;

import com.teatroabc.modelos.Assento;
import com.teatroabc.enums.*;
import com.teatroabc.utilitarios.GerenciadorArquivos;
import java.util.*;

public class AssentoRepositorio {
    
    public List<Assento> buscarAssentosPorPeca(String pecaId) {
        List<Assento> todosAssentos = new ArrayList<>();
        
        // Criar todos os assentos possíveis
        // Frisas - 3 fileiras x 8 assentos
        for (int f = 1; f <= 3; f++) {
            for (int a = 1; a <= 8; a++) {
                String codigo = "F" + f + "-" + a;
                Assento assento = new Assento(codigo, f, a, CategoriaAssento.FRISAS);
                todosAssentos.add(assento);
            }
        }
        
        // Balcão Nobre - 4 fileiras x 10 assentos
        for (int f = 1; f <= 4; f++) {
            for (int a = 1; a <= 10; a++) {
                String codigo = "B" + f + "-" + a;
                Assento assento = new Assento(codigo, f, a, CategoriaAssento.BALCAO_NOBRE);
                todosAssentos.add(assento);
            }
        }
        
        // Balcão - 4 fileiras x 10 assentos
        for (int f = 1; f <= 4; f++) {
            for (int a = 1; a <= 10; a++) {
                String codigo = "C" + f + "-" + a;
                Assento assento = new Assento(codigo, f, a, CategoriaAssento.BALCAO);
                todosAssentos.add(assento);
            }
        }
        
        // Verificar status dos assentos ocupados nos bilhetes vendidos
        List<String> bilhetes = GerenciadorArquivos.lerBilhetes();
        Set<String> assentosOcupados = new HashSet<>();
        
        for (String linha : bilhetes) {
            String[] partes = linha.split("\\|");
            if (partes.length >= 5 && partes[3].equals(pecaId)) {
                // Formato: ID|CODIGO_BARRAS|CPF_CLIENTE|ID_PECA|ASSENTOS|VALOR_TOTAL|DATA_HORA_COMPRA
                String[] codigosAssentos = partes[4].split(",");
                for (String codigo : codigosAssentos) {
                    assentosOcupados.add(codigo.trim());
                }
            }
        }
        
        // Marcar assentos como ocupados
        for (Assento assento : todosAssentos) {
            if (assentosOcupados.contains(assento.getCodigo())) {
                assento.setStatus(StatusAssento.OCUPADO);
            }
        }
        
        System.out.println("Carregados " + todosAssentos.size() + " assentos para peça " + pecaId);
        System.out.println("Assentos ocupados: " + assentosOcupados.size());
        
        return todosAssentos;
    }
    
    public void atualizarStatusAssentos(String pecaId, List<Assento> assentos) {
        // Este método não é mais necessário pois a persistência é feita através dos bilhetes
        // Mantido para compatibilidade
        System.out.println("Status dos assentos será atualizado através da venda de bilhetes");
    }
}