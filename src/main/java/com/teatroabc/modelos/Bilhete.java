package com.teatroabc.modelos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Bilhete {
    private final String id;
    private final String codigoBarras;
    private final Peca peca;
    private final Cliente cliente;
    private final List<Assento> assentos;
    private final double valorTotal;
    private final LocalDateTime dataHoraCompra;

    public Bilhete(Peca peca, Cliente cliente, List<Assento> assentos) {
        this.id = UUID.randomUUID().toString();
        this.codigoBarras = gerarCodigoBarras();
        this.peca = peca;
        this.cliente = cliente;
        this.assentos = assentos;
        this.valorTotal = calcularValorTotal();
        this.dataHoraCompra = LocalDateTime.now();
    }

    private String gerarCodigoBarras() {
        return String.format("%012d", System.currentTimeMillis() % 1000000000000L);
    }

    private double calcularValorTotal() {
        return assentos.stream()
                .mapToDouble(Assento::getPreco)
                .sum();
    }

    // Getters
    public String getId() { return id; }
    public String getCodigoBarras() { return codigoBarras; }
    public Peca getPeca() { return peca; }
    public Cliente getCliente() { return cliente; }
    public List<Assento> getAssentos() { return assentos; }
    public double getValorTotal() { return valorTotal; }
    public LocalDateTime getDataHoraCompra() { return dataHoraCompra; }
}
