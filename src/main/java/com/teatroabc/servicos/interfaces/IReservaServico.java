package com.teatroabc.servicos.interfaces;

import com.teatroabc.modelos.*;
import java.util.List;

public interface IReservaServico {
    Bilhete criarReserva(Peca peca, Cliente cliente, List<Assento> assentos);
    List<Bilhete> buscarBilhetesCliente(String cpf);
    Bilhete buscarBilhetePorId(String idBilhete);
}
