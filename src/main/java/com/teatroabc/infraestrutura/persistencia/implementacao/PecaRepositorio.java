package com.teatroabc.infraestrutura.persistencia.implementacao;

import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.infraestrutura.persistencia.interfaces.IPecaRepositorio;
import java.util.*;
import java.time.LocalDateTime;

public class PecaRepositorio implements IPecaRepositorio {
    private static final List<Peca> pecasDB = new ArrayList<>();

    static {
        // IDs agora são fixos para corresponder aos dados dos arquivos.
        // Use os IDs corretos do seu ambiente. Estes são exemplos.
        pecasDB.add(new Peca(
                "ee4ef3e1-38d8-4456-9773-3160b7f70479", // ID exemplo, use os seus
                "WICKEDONIA", "A PARÓDIA MUSICAL", "Uma paródia hilária do famoso musical",
                "#3CB371", "imagens/wickedonia.png", LocalDateTime.of(2024, 4, 25, 20, 0)
        ));
        pecasDB.add(new Peca(
                "0c22d34c-bf3b-4250-b536-53cbf276114d", // ID exemplo, use os seus
                "HERMANOTEU", "NA TERRA DE GODAH", "Comédia com os personagens mais queridos do humor",
                "#EF7D00", "imagens/hermanoteu.png", LocalDateTime.of(2025, 6, 5, 20, 0)
        ));
        pecasDB.add(new Peca(
                "27460ed2-aa6d-4bb4-b68a-46938b6b242e", // ID exemplo, use os seus
                "MORTE E VIDA SEVERINA", "", "Clássico de João Cabral de Melo Neto",
                "#E53935", "imagens/morte_vida_severina.jpg", LocalDateTime.of(2025, 6, 6, 19, 30)
        ));
    }

    @Override
    public List<Peca> listarTodas() {
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