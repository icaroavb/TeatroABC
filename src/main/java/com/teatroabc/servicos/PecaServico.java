package com.teatroabc.servicos;

import com.teatroabc.modelos.Peca;
import com.teatroabc.repositorios.PecaRepositorio;
import java.util.List;

import com.teatroabc.servicos.interfaces.IPecaServico;
import com.teatroabc.modelos.Assento;

public class PecaServico implements IPecaServico {
    private PecaRepositorio repo = new PecaRepositorio();

    @Override
    public List<Peca> buscarTodasPecas() {
        return repo.listarTodas();
    }

    @Override
    public Peca buscarPecaPorId(String id) {
        return repo.buscarPorId(id);
    }

    @Override
    public List<Assento> buscarAssentosDisponiveis(String idPeca) {
        // Implement logic to get available seats for the piece
        return null;
    }

    @Override
    public void atualizarStatusAssentos(String idPeca, List<Assento> assentos) {
        // Implement logic to update seat status
    }
}
