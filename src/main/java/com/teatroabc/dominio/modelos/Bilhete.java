package com.teatroabc.dominio.modelos;

import com.teatroabc.dominio.enums.Turno;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Representa um bilhete emitido para uma peça, cliente, assentos específicos e um turno.
 * Esta entidade é imutável após a criação. Os valores financeiros (subtotal, desconto, total)
 * são calculados pela camada de serviço (ReservaServico) e passados para o construtor,
 * assim como os identificadores (id, codigoBarras).
 */
public class Bilhete {
    private final String id; // Gerado externamente
    private final String codigoBarras; // Gerado externamente
    private final Peca peca;
    private final Cliente cliente;
    private final List<Assento> assentos; // Lista imutável de assentos
    private final Turno turno;
    private final BigDecimal subtotal; // Valor bruto dos assentos
    private final BigDecimal valorDesconto; // Desconto aplicado (via PlanoFidelidade do Cliente)
    private final BigDecimal valorTotal; // Valor final pago (subtotal - valorDesconto)
    private final LocalDateTime dataHoraCompra;

    /**
     * Construtor principal para criar uma instância de Bilhete.
     * Todos os valores, incluindo financeiros, identificadores e turno, são fornecidos externamente.
     *
     * @param id O ID único do bilhete.
     * @param codigoBarras O código de barras do bilhete.
     * @param peca A peça para a qual o bilhete é válido.
     * @param cliente O cliente que comprou o bilhete.
     * @param assentos A lista de assentos reservados por este bilhete.
     * @param turno O turno da apresentação.
     * @param subtotal O valor bruto total dos assentos antes de qualquer desconto.
     * @param valorDesconto O valor do desconto aplicado.
     * @param valorTotal O valor final pago pelo bilhete (subtotal - desconto).
     * @param dataHoraCompra A data e hora em que o bilhete foi comprado/emitido.
     * @throws IllegalArgumentException Se algum parâmetro essencial for nulo ou inválido, ou se os valores financeiros forem inconsistentes.
     */
    public Bilhete( String id, 
                    String codigoBarras, 
                    Peca peca, 
                    Cliente cliente, 
                    List<Assento> assentos, 
                    Turno turno,
                    BigDecimal subtotal, 
                    BigDecimal valorDesconto, 
                    BigDecimal valorTotal,
                    LocalDateTime dataHoraCompra) {

        // Encanspulamento das validações foi agora extraído para outro método - caso seja necessário não validar mais, basta comentar esta linha
        //apurarInformacoesEssenciais(id, codigoBarras, peca, cliente, assentos, turno, subtotal, valorDesconto, valorTotal, dataHoraCompra);                    

        // Validação de consistência financeira
        if (subtotal.subtract(valorDesconto).compareTo(valorTotal) != 0) {
            throw new IllegalArgumentException(String.format(
                    "Inconsistência nos valores financeiros do bilhete: Subtotal (%.2f) - Desconto (%.2f) != Total (%.2f)",
                    subtotal, valorDesconto, valorTotal
            ));
        }

        this.id = id;
        this.codigoBarras = codigoBarras;
        this.peca = peca;
        this.cliente = cliente;
        this.assentos = Collections.unmodifiableList(new ArrayList<>(assentos)); // Cópia defensiva imutável
        this.turno = turno;
        this.subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);
        this.valorDesconto = valorDesconto.setScale(2, RoundingMode.HALF_UP);
        this.valorTotal = valorTotal.setScale(2, RoundingMode.HALF_UP);
        this.dataHoraCompra = dataHoraCompra;
    }

    //Encapsulamento das lógicas de validação
    /**
     * Validação do campo privado id para verificar se ele é nulo ou vazio
     * @param id
     * @return
     */
    private boolean verificarId (String id){
        return id == null || id.trim().isEmpty();
    }    
    /**
     * Validacao do campo de código de barras para verificar se ele é null ou estiver vazio
     * @param codigoBarras
     * @return
     */
    private boolean verificarCodigoBarras (String codigoBarras){
        return codigoBarras == null || codigoBarras.trim().isEmpty();
    }
    /**
     * Validacao do campo privado peca para verificar se ele está null
     * @param peca
     * @return true se estiver null
     */
    private boolean verificarPeca (String peca){
        return peca == null;
    }
    /**
     * Validacao do campo privado cliente para verificar se ele está null
     * @param cliente
     * @return true se estiver null
     */
    private boolean verificarCliente (Cliente cliente){
        return cliente == null;
    }
    /**
     * Validacao do campo privado da lista de assentos -> verificar se está null ou se está vazio
     * @param assento
     * @return true se o valor for null ou se estiver vazio
     */
    private boolean verificarAssentos ( List <Assento> assento){
        return assentos == null || assentos.isEmpty();
    }
    /**
     * Validacao do campo privado turno
     * @param turno
     * @return true se ele for null;
     */
    private boolean verificarTurno (Turno turno){
        return turno == null;
    }
    /**
     * Validacao do campo subtotal 
     * @param subtotal
     * @return true se o valor for null ou se o valor for negativo
     */
    private boolean verificarSubtotal(BigDecimal subtotal){
        return subtotal == null || subtotal.compareTo(BigDecimal.ZERO) < 0;
    }
    /**
     * Validacao do campo desconto 
     * @param desconto
     * @return true se o valor for null ou se o valor for negativo
     */
    private boolean verificarDesconto (BigDecimal desconto){
        return desconto == null || desconto.compareTo(BigDecimal.ZERO) < 0;
    }
    /**
     * Validacao do campo subtotal 
     * @param valorTotal
     * @return true se o valor for null ou se o valor for negativo
     */
    private boolean verificarValorTotal (BigDecimal valorTotal){
        return valorTotal == null || valorTotal.compareTo(BigDecimal.ZERO) < 0;
    }
    /**
     * Validacao da hora e data de compra
     * @param dateTime
     * @return true se o valor for null
     */
    private boolean verificarDataHoraCompra (LocalDateTime dateTime){
        return dataHoraCompra == null;
    }

    //encapsulamento das validações - se for necessário acrescentar ou modificar uma validação, basta apenas acrescentar aqui
    public void apurarInformacoesEssenciais (String id, 
                                            String codigoBarras, 
                                            Peca peca, 
                                            Cliente cliente, 
                                            List<Assento> assentos, 
                                            Turno turno,
                                            BigDecimal subtotal, 
                                            BigDecimal valorDesconto, 
                                            BigDecimal valorTotal,
                                            LocalDateTime dataHoraCompra){
        
        if (verificarId(id)) throw new IllegalArgumentException("ID do bilhete não pode ser nulo ou vazio.");
        if (verificarCodigoBarras(codigoBarras)) throw new IllegalArgumentException("Código de barras do bilhete não pode ser nulo ou vazio.");
        if (verificarPeca(codigoBarras)) throw new IllegalArgumentException("Peça não pode ser nula para o bilhete.");
        if (verificarCliente(cliente)) throw new IllegalArgumentException("Cliente não pode ser nulo para o bilhete.");
        if (verificarAssentos(assentos)) throw new IllegalArgumentException("Lista de assentos não pode ser nula ou vazia para o bilhete.");
        if (verificarTurno(turno)) throw new IllegalArgumentException("Turno não pode ser nulo para o bilhete.");
        if (verificarSubtotal(subtotal)) throw new IllegalArgumentException("Subtotal do bilhete não pode ser nulo ou negativo.");
        if (verificarDesconto(valorDesconto)) throw new IllegalArgumentException("Valor de desconto do bilhete não pode ser nulo ou negativo.");
        if (verificarValorTotal(valorTotal)) throw new IllegalArgumentException("Valor total do bilhete não pode ser nulo ou negativo.");
        if (verificarDataHoraCompra(dataHoraCompra)) throw new IllegalArgumentException("Data e hora da compra do bilhete não podem ser nulos.");
    }

    // --- Getters ---
    public String getId() { return id; }
    public String getCodigoBarras() { return codigoBarras; }
    public Peca getPeca() { return peca; }
    public Cliente getCliente() { return cliente; }
    public List<Assento> getAssentos() { return Collections.unmodifiableList(assentos); } // Garante imutabilidade na exposição
    public Turno getTurno() { return turno; }
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
        return Objects.equals(id, bilhete.id); // Comparação pelo ID único
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Bilhete{" +
                "id='" + id + '\'' +
                ", codigoBarras='" + codigoBarras + '\'' +
                ", peca=" + (peca != null ? peca.getTitulo() : "N/A") +
                ", cliente=" + (cliente != null ? cliente.getNome() : "N/A") +
                ", turno=" + (turno != null ? turno.getNome() : "N/A") +
                ", nAssentos=" + (assentos != null ? assentos.size() : 0) +
                ", valorTotal=" + valorTotal +
                ", dataHoraCompra=" + dataHoraCompra +
                '}';
    }
}