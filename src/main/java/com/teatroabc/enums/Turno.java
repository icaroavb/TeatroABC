package com.teatroabc.enums;

public enum Turno {
    MANHA("Manhã", "10:00"),
    TARDE("Tarde", "17:00"),
    NOITE("Noite", "20:00");

    private final String nome;
    private final String horario;

    Turno(String nome, String horario) {
        this.nome = nome;
        this.horario = horario;
    }

    public String getNome() {
        return nome;
    }

    public String getHorario() {
        return horario;
    }

    @Override
    public String toString() {
        return nome + " - " + horario;
    }
}
