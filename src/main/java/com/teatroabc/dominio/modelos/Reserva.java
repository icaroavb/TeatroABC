package com.teatroabc.dominio.modelos;

import com.teatroabc.dominio.enums.Turno;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data Transfer Object (DTO) que encapsula os dados necessários
 * para iniciar o processo de criação de uma reserva/bilhete.
 * Esta classe é imutável após a criação.
 */
public class Reserva {
    private final Cliente cliente;
    private final Peca peca;
    private final List<Assento> assentos; // Lista de assentos da reserva
    private final Turno turno;

    /**
     * Construtor para criar uma instância de Reserva.
     *
     * @param cliente O cliente que está fazendo a reserva. Não pode ser nulo.
     * @param peca A peça selecionada para a reserva. Não pode ser nula.
     * @param assentos A lista de assentos selecionados. Não pode ser nula ou vazia.
     * @param turno O turno da apresentação selecionado. Não pode ser nulo.
     * @throws IllegalArgumentException Se qualquer parâmetro essencial for nulo ou inválido.
     */
    public Reserva(Cliente cliente, Peca peca, List<Assento> assentos, Turno turno) {

        if (cliente == null) throw new IllegalArgumentException("Cliente não pode ser nulo para uma reserva.");
        if (peca == null) throw new IllegalArgumentException("Peça não pode ser nula para uma reserva.");
        if (assentos == null || assentos.isEmpty()) throw new IllegalArgumentException("A lista de assentos não pode ser nula ou vazia para uma reserva.");
        if (turno == null) throw new IllegalArgumentException("Turno não pode ser nulo para uma reserva.");

        this.cliente = cliente;
        this.peca = peca;
        // Cria uma cópia defensiva e garante que a lista interna não seja modificável externamente
        this.assentos = Collections.unmodifiableList(new ArrayList<>(assentos));
        this.turno = turno;
    }

    //encapsulamento das validacoe

    // --- Getters ---
    public Cliente getCliente() { return cliente; }
    public Peca getPeca() { return peca; }
    /**
     * Retorna uma visão não modificável da lista de assentos.
     * @return Lista de assentos da reserva.
     */
    public List<Assento> getAssentos() { return assentos; } // Já é imutável pela construção
    public Turno getTurno() { return turno; }
}