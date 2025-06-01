package com.teatroabc.repositorios;

import com.teatroabc.modelos.Peca;
import java.util.*;
import java.time.LocalDateTime;

public class PecaRepositorio {
    private static final List<Peca> pecas = new ArrayList<>();

    static {
        pecas.add(new Peca("Wickedonia", "A paródia musical", "Uma paródia hilária do famoso musical", java.awt.Color.GREEN, "C:\\Users\\Icaro\\OneDrive\\Documentos\\CODIGOS\\PROJETO INTEGRADOR\\TeatroABC\\src\\main\\resources\\imagenswickedonia.png", LocalDateTime.of(2024, 4, 25, 20, 0)));
        pecas.add(new Peca("Morte e Vida Severina", "", "Clássico de João Cabral de Melo Neto", java.awt.Color.RED, "imagens/morte_vida_severina.jpg", LocalDateTime.of(2024, 5, 10, 19, 30)));
        pecas.add(new Peca("Hermanoteu", "", "Comédia com os personagens mais queridos do humor", java.awt.Color.ORANGE, "C:\\Users\\Icaro\\OneDrive\\Documentos\\CODIGOS\\PROJETO INTEGRADOR\\TeatroABC\\src\\main\\resources\\imagens\\hermanoteu.png", LocalDateTime.of(2024, 5, 2, 21, 0)));
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
