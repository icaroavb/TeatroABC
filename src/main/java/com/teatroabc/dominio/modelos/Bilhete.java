package com.teatroabc.dominio.modelos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Representa um bilhete emitido para um cliente, para uma sessão específica e um
 * conjunto de assentos. Esta entidade é o registro final de uma compra.
 * 
 * A classe é imutável após a criação. Os valores financeiros e identificadores
 * são calculados e gerados pela camada de serviço e passados via construtor.
 * A informação da peça, data e turno agora está encapsulada no objeto {@link Sessao}.
 */
public class Bilhete {
    private final String id;
    private final String codigoBarras;
    private final Sessao sessao; // MUDANÇA: Substitui os campos 'peca' e 'turno'
    private final Cliente cliente;
    private final List<Assento> assentos;
    private final BigDecimal subtotal;
    private final BigDecimal valorDesconto;
    private final BigDecimal valorTotal;
    private final LocalDateTime dataHoraCompra;

    /**
     * Construtor principal refatorado para criar uma instância de Bilhete.
     * Recebe um objeto {@link Sessao} que contém as informações da apresentação.
     *
     * @param id O ID único do bilhete.
     * @param codigoBarras O código de barras do bilhete.
     * @param sessao A sessão (peça, data, turno) para a qual o bilhete é válido.
     * @param cliente O cliente que comprou o bilhete.
     * @param assentos A lista de assentos reservados por este bilhete.
     * @param subtotal O valor bruto total dos assentos.
     * @param valorDesconto O valor do desconto aplicado.
     * @param valorTotal O valor final pago pelo bilhete.
     * @param dataHoraCompra A data e hora em que o bilhete foi emitido.
     * @throws IllegalArgumentException Se algum parâmetro essencial for nulo ou inválido.
     */
    public Bilhete(String id, String codigoBarras, Sessao sessao, Cliente cliente, 
                   List<Assento> assentos, BigDecimal subtotal, BigDecimal valorDesconto, 
                   BigDecimal valorTotal, LocalDateTime dataHoraCompra) {

        // A lógica de validação foi encapsulada para maior clareza.
        //apurarInformacoesEssenciais(id, codigoBarras, sessao, cliente, assentos, 
                                     //subtotal, valorDesconto, valorTotal, dataHoraCompra);

        // Validação de consistência financeira.
        if (subtotal.subtract(valorDesconto).compareTo(valorTotal) != 0) {
            throw new IllegalArgumentException(String.format(
                    "Inconsistência financeira: Subtotal (%.2f) - Desconto (%.2f) != Total (%.2f)",
                    subtotal, valorDesconto, valorTotal
            ));
        }

        this.id = id;
        this.codigoBarras = codigoBarras;
        this.sessao = sessao;
        this.cliente = cliente;
        this.assentos = Collections.unmodifiableList(new ArrayList<>(assentos)); // Cópia defensiva
        this.subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);
        this.valorDesconto = valorDesconto.setScale(2, RoundingMode.HALF_UP);
        this.valorTotal = valorTotal.setScale(2, RoundingMode.HALF_UP);
        this.dataHoraCompra = dataHoraCompra;
    }
    
    /**
     * Encapsula a verificação de dados essenciais para a criação de um Bilhete.
     * @throws IllegalArgumentException se algum dado obrigatório for inválido.
     */
    private void apurarInformacoesEssenciais(String id, String codigoBarras, Sessao sessao, 
                                              Cliente cliente, List<Assento> assentos, 
                                              BigDecimal subtotal, BigDecimal valorDesconto, 
                                              BigDecimal valorTotal, LocalDateTime dataHoraCompra) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("ID do bilhete não pode ser nulo ou vazio.");
        if (codigoBarras == null || codigoBarras.trim().isEmpty()) throw new IllegalArgumentException("Código de barras não pode ser nulo ou vazio.");
        if (sessao == null) throw new IllegalArgumentException("Sessão não pode ser nula para o bilhete.");
        if (cliente == null) throw new IllegalArgumentException("Cliente não pode ser nulo para o bilhete.");
        if (assentos == null || assentos.isEmpty()) throw new IllegalArgumentException("Lista de assentos não pode ser nula ou vazia.");
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Subtotal não pode ser nulo ou negativo.");
        if (valorDesconto == null || valorDesconto.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Valor de desconto não pode ser nulo ou negativo.");
        if (valorTotal == null || valorTotal.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Valor total não pode ser nulo ou negativo.");
        if (dataHoraCompra == null) throw new IllegalArgumentException("Data e hora da compra não podem ser nulos.");
    }

    // --- Getters ---

    public String getId() { return id; }
    public String getCodigoBarras() { return codigoBarras; }
    public Sessao getSessao() { return sessao; }
    public Cliente getCliente() { return cliente; }
    public List<Assento> getAssentos() { return assentos; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getValorDesconto() { return valorDesconto; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public LocalDateTime getDataHoraCompra() { return dataHoraCompra; }

    // --- Métodos Padrão (equals, hashCode, toString) ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bilhete bilhete = (Bilhete) o;
        return Objects.equals(id, bilhete.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Bilhete{" +
                "id='" + id + '\'' +
                ", sessao=" + (sessao != null ? sessao.getPeca().getTitulo() : "N/A") +
                ", cliente=" + (cliente != null ? cliente.getNome() : "N/A") +
                ", nAssentos=" + (assentos != null ? assentos.size() : 0) +
                ", valorTotal=" + valorTotal +
                '}';
    }
}