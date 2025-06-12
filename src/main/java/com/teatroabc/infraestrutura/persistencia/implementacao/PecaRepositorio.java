package com.teatroabc.infraestrutura.persistencia.implementacao;

import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.infraestrutura.persistencia.interfaces.IPecaRepositorio;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementação (Adaptador Secundário) do repositório de Peças.
 * Atualmente, os dados das peças são hardcoded para fins de demonstração.
 * Esta classe é responsável por fornecer os objetos Peca que representam
 * as obras artísticas disponíveis no teatro.
 */
public class PecaRepositorio implements IPecaRepositorio {

    private static final List<Peca> pecasDB = new ArrayList<>();

    // Bloco estático para popular o banco de dados em memória na inicialização.
    static {
        // IDs agora são fixos para corresponder aos dados de exemplo dos arquivos.        
        
        pecasDB.add(new Peca(
                "ee4ef3e1-38d8-4456-9773-3160b7f70479",
                "WICKEDONIA",
                "A PARÓDIA MUSICAL",
                "Uma paródia hilária do famoso musical",
                "#3CB371",
                "imagens/wickedonia.png"
                // O argumento LocalDateTime foi removido daqui.
        ));
        
        pecasDB.add(new Peca(
                "0c22d34c-bf3b-4250-b536-53cbf276114d",
                "HERMANOTEU",
                "NA TERRA DE GODAH",
                "Comédia com os personagens mais queridos do humor",
                "#EF7D00",
                "imagens/hermanoteu.png"
                // O argumento LocalDateTime foi removido daqui.
        ));
        
        pecasDB.add(new Peca(
                "27460ed2-aa6d-4bb4-b68a-46938b6b242e",
                "MORTE E VIDA SEVERINA",
                "O Auto do Natal Pernambucano",
                "Clássico de João Cabral de Melo Neto",
                "#E53935",
                "imagens/morte_vida_severina.jpg"
                // O argumento LocalDateTime foi removido daqui.
        ));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Peca> listarTodas() {
        // Retorna uma cópia da lista para evitar modificações externas.
        return new ArrayList<>(pecasDB);
    }

    /**
     * {@inheritDoc}
     */
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