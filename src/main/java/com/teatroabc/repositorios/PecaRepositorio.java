package com.teatroabc.repositorios;

import com.teatroabc.modelos.Peca;
import java.util.*;
import java.time.LocalDateTime;
import java.awt.Color;

public class PecaRepositorio {
    private static final List<Peca> pecas = new ArrayList<>();
    
    static {
        // Inicializar com as peças padrão
        pecas.add(new Peca(
            "WICKEDONIA",
            "A PARÓDIA MUSICAL",
            "Uma paródia hilária do famoso musical",
            new Color(60, 179, 113),
            "imagens/wickedonia.png",
            LocalDateTime.of(2024, 4, 25, 20, 0)
        ));
        
        pecas.add(new Peca(
            "HERMANOTEU",
            "",
            "Comédia com os personagens mais queridos do humor",
            new Color(239, 125, 0),
            "imagens/hermanoteu.png",
            LocalDateTime.of(2024, 5, 2, 21, 0)
        ));
        
        pecas.add(new Peca(
            "MORTE E VIDA SEVERINA",
            "",
            "Clássico de João Cabral de Melo Neto",
            new Color(229, 57, 53),
            "imagens/morte_vida_severina.jpg",
            LocalDateTime.of(2024, 5, 10, 19, 30)
        ));
    }
    
    public void salvar(Peca peca) {
        pecas.add(peca);
    }
    
    public List<Peca> listarTodas() {
        return new ArrayList<>(pecas);
    }
    
    public Peca buscarPorId(String id) {
        for (Peca peca : pecas) {
            if (peca.getId().equals(id)) {
                return peca;
            }
        }
        return null;
    }
}