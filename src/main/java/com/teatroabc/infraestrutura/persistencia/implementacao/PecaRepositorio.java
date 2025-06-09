package com.teatroabc.infraestrutura.persistencia.implementacao;

import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.infraestrutura.persistencia.interfaces.IPecaRepositorio; // Implementar
import com.teatroabc.infraestrutura.utilitarios_comuns.GeradorIdUtil;
import java.util.*;
import java.time.LocalDateTime;

public class PecaRepositorio implements IPecaRepositorio {
    private static final List<Peca> pecasDB = new ArrayList<>(); // Renomeado para clareza

    static {
        // Inicialização como antes, usando GeradorIdUtil e cores Hex.
        // O Peca construtor agora espera o ID como primeiro argumento.
        pecasDB.add(new Peca(
                GeradorIdUtil.gerarNovoId(),
                "WICKEDONIA", "A PARÓDIA MUSICAL", "Uma paródia hilária do famoso musical",
                "#3CB371", "imagens/wickedonia.png", LocalDateTime.of(2024, 4, 25, 20, 0)
        ));
        pecasDB.add(new Peca(
                GeradorIdUtil.gerarNovoId(),
                "HERMANOTEU", "", "Comédia com os personagens mais queridos do humor",
                "#EF7D00", "imagens/hermanoteu.png", LocalDateTime.of(2024, 5, 2, 21, 0)
        ));
        pecasDB.add(new Peca(
                GeradorIdUtil.gerarNovoId(),
                "MORTE E VIDA SEVERINA", "", "Clássico de João Cabral de Melo Neto",
                "#E53935", "imagens/morte_vida_severina.jpg", LocalDateTime.of(2024, 5, 10, 19, 30)
        ));
    }

    @Override
    public List<Peca> listarTodas() {
        // Retorna uma nova lista (cópia superficial) para proteger a lista interna de modificações externas.
        return new ArrayList<>(pecasDB);
    }

    @Override
    public Optional<Peca> buscarPorId(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return pecasDB.stream()
                .filter(peca -> id.equals(peca.getId()))
                .findFirst();
    }
}