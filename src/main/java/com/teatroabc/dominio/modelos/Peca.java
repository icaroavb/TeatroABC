package com.teatroabc.dominio.modelos;

import java.time.LocalDateTime;
import java.util.Objects;
// UUID não é mais gerado aqui, mas a classe Peca ainda precisa de um ID.
// A geração foi movida para quem cria a Peca (ex: PecaRepositorio usando GeradorIdUtil)

public class Peca {
    private final String id;
    private final String titulo;
    private final String subtitulo;
    private final String descricao;
    private final String corFundoHex; // Alterado de java.awt.Color para String
    private final String caminhoImagem;
    private final LocalDateTime dataHora;

    // Construtor principal - SEMPRE recebe o ID
    public Peca(String id, String titulo, String subtitulo, String descricao,
                String corFundoHex, String caminhoImagem, LocalDateTime dataHora) {

        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID da peça não pode ser nulo ou vazio.");
        }
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("Título da peça não pode ser nulo ou vazio.");
        }
        // Validação simples para formato hexadecimal. Pode ser mais robusta.
        if (corFundoHex == null || !corFundoHex.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
            throw new IllegalArgumentException("Cor de fundo em formato hexadecimal inválido ou ausente. Ex: #RRGGBB");
        }
        if (dataHora == null) {
            throw new IllegalArgumentException("Data e hora da peça não podem ser nulos.");
        }

        this.id = id;
        this.titulo = titulo;
        this.subtitulo = Objects.requireNonNullElse(subtitulo, ""); // Evita nulo
        this.descricao = Objects.requireNonNullElse(descricao, ""); // Evita nulo
        this.corFundoHex = corFundoHex;
        this.caminhoImagem = caminhoImagem; // Pode ser nulo/vazio
        this.dataHora = dataHora;
    }

    // Getters
    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getSubtitulo() { return subtitulo; }
    public String getDescricao() { return descricao; }
    public String getCorFundoHex() { return corFundoHex; }
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

    @Override
    public String toString() {
        return "Peca{" +
                "id='" + id + '\'' +
                ", titulo='" + titulo + '\'' +
                ", corFundoHex='" + corFundoHex + '\'' +
                ", dataHora=" + dataHora +
                '}';
    }
}