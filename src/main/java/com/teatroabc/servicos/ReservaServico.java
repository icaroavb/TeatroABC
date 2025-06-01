package com.teatroabc.servicos;

import com.teatroabc.modelos.*;
import com.teatroabc.repositorios.BilheteRepositorio;
import com.teatroabc.servicos.interfaces.IReservaServico;
import java.util.List;

public class ReservaServico implements IReservaServico {
    private final BilheteRepositorio bilheteRepo = new BilheteRepositorio();

    @Override
    public Bilhete criarReserva(Peca peca, Cliente cliente, List<Assento> assentos) {
        Bilhete bilhete = new Bilhete(peca, cliente, assentos);
        bilheteRepo.salvar(bilhete);
        return bilhete;
    }

    @Override
    public List<Bilhete> buscarBilhetesCliente(String cpf) {
        return bilheteRepo.listarPorCpf(cpf);
    }

    @Override
    public Bilhete buscarBilhetePorId(String idBilhete) {
        return bilheteRepo.buscarPorId(idBilhete);
    }
}
