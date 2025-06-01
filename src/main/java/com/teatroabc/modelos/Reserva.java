package com.teatroabc.modelos;

import java.util.List;

public class Reserva {
    private Cliente cliente;
    private Peca peca;
    private List<Assento> assentos;

    public Reserva(Cliente cliente, Peca peca, List<Assento> assentos) {
        this.cliente = cliente;
        this.peca = peca;
        this.assentos = assentos;
    }

    public Cliente getCliente() { return cliente; }
    public Peca getPeca() { return peca; }
    public List<Assento> getAssentos() { return assentos; }
}
