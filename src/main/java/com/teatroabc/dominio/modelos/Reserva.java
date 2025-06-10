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

        //validacao dos dados essencial encapsulada - comentar caso não seja mais interessante fazer a validacao
        //apurarInformacoesEssenciais(cliente, peca, assentos, turno);

        this.cliente = cliente;
        this.peca = peca;        
        this.assentos = Collections.unmodifiableList(new ArrayList<>(assentos)); // Cria uma cópia defensiva e garante que a lista interna não seja modificável externamente
        this.turno = turno;
    }

    //encapsulamento das validações
    /**
     * Apurar se o campo privado cliente está nulo
     * @param cliente
     * @return true se estiver nulo
     */
    private boolean verificarCliente (Cliente cliente){
        return cliente == null;
    }
    /**
     * Apurar se o campo privado peca está nulo
     * @param peca
     * @return true se estiver nulo
     */
    private boolean verificarPeca (Peca peca){
        return peca == null;
    }
    /**
     * Apurar se o campo privado da lista de Assento está nula
     * @param assentos
     * @return true se estiver null
     */
    private boolean verificarAssento (List <Assento> assentos){
        return assentos == null || assentos.isEmpty();
    }
    /**
     * Encapsulamento para apurar se o turno está null
     * @param turno
     * @return true se o turno estiver com o valor null
     */
    private boolean verificarTurno (Turno turno){
        return turno == null;
    }

    //Encapsular validação de dados essenciais
    /**
     * Encapsulamento da validação de dados essenciais
     * @param cliente
     * @param peca
     * @param assentos
     * @param turno
     */
    public void apurarInformacoesEssenciais(Cliente cliente, Peca peca, List <Assento> assentos, Turno turno){
        if (verificarCliente(cliente)) throw new IllegalArgumentException("Cliente não pode ser nulo para uma reserva.");
        if (verificarPeca(peca)) throw new IllegalArgumentException("Peça não pode ser nula para uma reserva.");
        if (verificarAssento(assentos)) throw new IllegalArgumentException("A lista de assentos não pode ser nula ou vazia para uma reserva.");
        if (verificarTurno(turno)) throw new IllegalArgumentException("Turno não pode ser nulo para uma reserva.");
    }

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