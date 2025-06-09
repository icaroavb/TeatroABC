package com.teatroabc.dominio.enums;

/**
 * Enumeração que define os possíveis turnos para as apresentações das peças.
 * Cada turno possui um nome descritivo e um horário associado.
 * Esta enumeração faz parte do vocabulário do domínio.
 */
public enum Turno {
    MANHA("Manhã", "10:00"),
    TARDE("Tarde", "17:00"),
    NOITE("Noite", "20:00");

    private final String nome;    // Nome amigável do turno (ex: "Manhã")
    private final String horario; // Representação textual do horário de início do turno (ex: "10:00")

    /**
     * Construtor para o enum Turno.
     * @param nome O nome descritivo do turno.
     * @param horario A string representando o horário de início do turno.
     */
    Turno(String nome, String horario) {
        this.nome = nome;
        this.horario = horario;
    }

    /**
     * Retorna o nome amigável do turno.
     * @return O nome do turno (ex: "Manhã").
     */
    public String getNome() {
        return nome;
    }

    /**
     * Retorna a string que representa o horário de início do turno.
     * @return O horário do turno (ex: "10:00").
     */
    public String getHorario() {
        return horario;
    }

    /**
     * Retorna uma representação em string do turno, combinando nome e horário.
     * Útil para exibição em interfaces de usuário ou logs.
     * @return String formatada como "Nome - Horário" (ex: "Manhã - 10:00").
     */
    @Override
    public String toString() {
        return nome + " - " + horario;
    }
}