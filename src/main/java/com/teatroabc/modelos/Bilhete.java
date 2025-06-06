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
    private final double valorDesconto;
    private final LocalDateTime dataHoraCompra;

    // Construtor original (sem desconto)
    public Bilhete(Peca peca, Cliente cliente, List<Assento> assentos) {
        this(peca, cliente, assentos, 0.0);
    }

    // Construtor com desconto
    public Bilhete(Peca peca, Cliente cliente, List<Assento> assentos, double valorDesconto) {
        this.id = UUID.randomUUID().toString();
        this.codigoBarras = gerarCodigoBarras();
        this.peca = peca;
        this.cliente = cliente;
        this.assentos = assentos;
        this.valorDesconto = valorDesconto;
        this.valorTotal = calcularValorTotal();
        this.dataHoraCompra = LocalDateTime.now();
        
        // Debug para acompanhar a criação do bilhete
        System.out.println("=== CRIANDO BILHETE ===");
        System.out.println("Cliente: " + cliente.getNome() + " (ABC: " + cliente.isMembroABC() + ")");
        System.out.println("Subtotal: " + getSubtotal());
        System.out.println("Desconto: " + valorDesconto);
        System.out.println("Total: " + valorTotal);
        System.out.println("=======================");
    }

    private String gerarCodigoBarras() {
        return String.format("%012d", System.currentTimeMillis() % 1000000000000L);
    }

    private double calcularValorTotal() {
        double subtotal = assentos.stream()
                .mapToDouble(Assento::getPreco)
                .sum();
        return subtotal - valorDesconto;
    }

    public double getSubtotal() {
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
    public double getValorDesconto() { return valorDesconto; }
    public LocalDateTime getDataHoraCompra() { return dataHoraCompra; }
}