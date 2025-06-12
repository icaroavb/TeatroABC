package com.teatroabc.dominio.modelos;

import com.teatroabc.dominio.enums.Turno;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa uma Sessão ou Apresentação específica de uma Peça.
 * Esta entidade de domínio vincula uma obra (Peca) a uma data, hora e turno
 * concretos, formando um evento único no calendário do teatro para o qual
 * os bilhetes podem ser vendidos.
 * 
 * Esta classe é imutável após sua criação.
 */
public class Sessao {
    private final String id; // Identificador único da sessão.
    private final Peca peca; // Composição: A sessão TEM UMA peça.
    private final LocalDateTime dataHora; // A data e hora exatas de início da apresentação.
    private final Turno turno; // O turno (Manhã, Tarde, Noite) da apresentação.

    /**
     * Construtor principal da entidade Sessao.
     *
     * @param id O ID único da sessão, gerado externamente.
     * @param peca O objeto Peca associado a esta sessão. Não pode ser nulo.
     * @param dataHora A data e hora exatas da apresentação. Não pode ser nula.
     * @param turno O Turno da apresentação. Não pode ser nulo.
     * @throws IllegalArgumentException se algum dos parâmetros essenciais for nulo.
     */
    public Sessao(String id, Peca peca, LocalDateTime dataHora, Turno turno) {
        // Validação para garantir que a sessão não seja criada em um estado inválido.
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID da sessão não pode ser nulo ou vazio.");
        }
        if (peca == null) {
            throw new IllegalArgumentException("A Peça não pode ser nula para uma Sessão.");
        }
        if (dataHora == null) {
            throw new IllegalArgumentException("A data e hora não podem ser nulas para uma Sessão.");
        }
        if (turno == null) {
            throw new IllegalArgumentException("O Turno não pode ser nulo para uma Sessão.");
        }
        
        this.id = id;
        this.peca = peca;
        this.dataHora = dataHora;
        this.turno = turno;
    }

    // --- Getters ---

    /**
     * @return O ID único desta sessão.
     */
    public String getId() { 
        return id; 
    }

    /**
     * @return O objeto Peca que será apresentado nesta sessão.
     */
    public Peca getPeca() { 
        return peca; 
    }

    /**
     * @return A data e hora de início de uma sessão específica
     */
    public LocalDateTime getDataHora() { 
        return dataHora; 
    }

    /**
     * @return O Turno (Manhã, Tarde, Noite) desta sessão.
     */
    public Turno getTurno() { 
        return turno; 
    }

    // --- Métodos Padrão (equals, hashCode, toString) ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sessao sessao = (Sessao) o;
        // A identidade de uma sessão é definida unicamente pelo seu ID.
        return Objects.equals(id, sessao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Sessao{" +
                "id='" + id + '\'' +
                ", peca=" + peca.getTitulo() +
                ", dataHora=" + dataHora +
                ", turno=" + turno +
                '}';
    }
}