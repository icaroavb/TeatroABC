package com.teatroabc.repositorios;

import com.teatroabc.modelos.Assento;
import com.teatroabc.enums.*;
import com.teatroabc.utilitarios.GerenciadorArquivos;
import java.util.*;

public class AssentoRepositorio {
    
    public List<Assento> buscarAssentosPorPeca(String pecaId) {
        return buscarAssentosPorPecaETurno(pecaId, Turno.NOITE);
    }
    
    public List<Assento> buscarAssentosPorPecaETurno(String pecaId, Turno turno) {
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
        
        // Verificar status dos assentos ocupados nos bilhetes vendidos para esse turno específico
        List<String> bilhetes = GerenciadorArquivos.lerBilhetes();
        Set<String> assentosOcupados = new HashSet<>();
        
        for (String linha : bilhetes) {
            String[] partes = linha.split("\\|");
            if (partes.length >= 9) { // Novo formato com turno
                // Formato: ID|CODIGO_BARRAS|CPF_CLIENTE|ID_PECA|ASSENTOS|VALOR_TOTAL|VALOR_DESCONTO|DATA_HORA_COMPRA|TURNO
                if (partes[3].equals(pecaId)) {
                    String turnoVendido = partes.length > 8 ? partes[8] : "NOITE";
                    
                    // Só marcar como ocupado se for o mesmo turno
                    if (turnoVendido.equals(turno.name())) {
                        String[] codigosAssentos = partes[4].split(",");
                        for (String codigo : codigosAssentos) {
                            assentosOcupados.add(codigo.trim());
                        }
                    }
                }
            } else if (partes.length >= 7) { // Formato antigo sem turno
                // Compatibilidade com dados antigos - assumir turno NOITE
                if (partes[3].equals(pecaId) && turno == Turno.NOITE) {
                    String[] codigosAssentos = partes[4].split(",");
                    for (String codigo : codigosAssentos) {
                        assentosOcupados.add(codigo.trim());
                    }
                }
            }
        }
        
        // Marcar assentos como ocupados
        for (Assento assento : todosAssentos) {
            if (assentosOcupados.contains(assento.getCodigo())) {
                assento.setStatus(StatusAssento.OCUPADO);
            }
        }
        
        System.out.println("Carregados " + todosAssentos.size() + " assentos para peça " + pecaId + " turno " + turno);
        System.out.println("Assentos ocupados para este turno: " + assentosOcupados.size());
        
        return todosAssentos;
    }
    
    public void atualizarStatusAssentos(String pecaId, List<Assento> assentos) {
        // Este método não é mais necessário pois a persistência é feita através dos bilhetes
        // Mantido para compatibilidade
        System.out.println("Status dos assentos será atualizado através da venda de bilhetes");
    }
}