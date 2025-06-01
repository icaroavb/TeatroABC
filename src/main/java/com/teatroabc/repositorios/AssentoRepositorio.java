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
        
        // Verificar status dos assentos no arquivo
        List<String> statusAssentos = GerenciadorArquivos.lerAssentos();
        for (String linha : statusAssentos) {
            String[] partes = linha.split("\\|");
            if (partes.length >= 3 && partes[0].equals(pecaId)) {
                // Encontrar o assento e atualizar seu status
                for (Assento assento : todosAssentos) {
                    if (assento.getCodigo().equals(partes[1])) {
                        try {
                            assento.setStatus(StatusAssento.valueOf(partes[2]));
                        } catch (IllegalArgumentException e) {
                            // Status inválido, manter como disponível
                        }
                        break;
                    }
                }
            }
        }
        
        return todosAssentos;
    }
    
    public void atualizarStatusAssentos(String pecaId, List<Assento> assentos) {
        for (Assento assento : assentos) {
            GerenciadorArquivos.salvarAssento(
                pecaId + "|" + assento.getCodigo() + "|" + assento.getStatus().name()
            );
        }
    }
}