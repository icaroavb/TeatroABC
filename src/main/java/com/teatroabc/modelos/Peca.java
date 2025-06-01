package com.teatroabc.modelos;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Peca {
    private final String id;
    private final String titulo;
    private final String subtitulo;
    private final String descricao;
    private final Color corFundo;
    private final String caminhoImagem;
    private final LocalDateTime dataHora;

    public Peca(String titulo, String subtitulo, String descricao,
                Color corFundo, String caminhoImagem, LocalDateTime dataHora) {
        this.id = UUID.randomUUID().toString();
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.descricao = descricao;
        this.corFundo = corFundo;
        this.caminhoImagem = caminhoImagem;
        this.dataHora = dataHora;
    }

    // Getters
    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getSubtitulo() { return subtitulo; }
    public String getDescricao() { return descricao; }
    public Color getCorFundo() { return corFundo; }
    public String getCaminhoImagem() { return caminhoImagem; }
    public LocalDateTime getDataHora() { return dataHora; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Peca peca = (Peca) o;
        return Objects.equals(id, peca.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}