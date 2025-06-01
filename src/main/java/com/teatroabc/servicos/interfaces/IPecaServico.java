package com.teatroabc.servicos.interfaces;

import com.teatroabc.modelos.Peca;
import com.teatroabc.modelos.Assento;
import java.util.List;

public interface IPecaServico {
    List<Peca> buscarTodasPecas();
    Peca buscarPecaPorId(String id);
    List<Assento> buscarAssentosDisponiveis(String idPeca);
    void atualizarStatusAssentos(String idPeca, List<Assento> assentos);
}