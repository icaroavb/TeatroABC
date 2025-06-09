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

        //logica de validacao encapsulada, caso não seja necessário validar os dados, basta apenas comentar esta linha
        apurarInformacoesEssenciais(id, titulo, corFundoHex, dataHora);

        this.id = id;
        this.titulo = titulo;
        this.subtitulo = Objects.requireNonNullElse(subtitulo, ""); // Evita nulo
        this.descricao = Objects.requireNonNullElse(descricao, ""); // Evita nulo
        this.corFundoHex = corFundoHex;
        this.caminhoImagem = caminhoImagem; // Pode ser nulo/vazio
        this.dataHora = dataHora;
    }
    
    //Encapsulamento das lógicas de validação     
    /**
     * Encapsulamento da lógica para apurar se o Id é válido
     * @param id
     * @return
     */
    private boolean verificarId (String id){
        return id == null || id.trim().isEmpty();
    }
    /**
     * Encapsualmento da lógica para apurar se o título é válido
     * @param titulo
     * @return
     */
    private boolean verificarTitulo (String titulo){
        return titulo == null || titulo.trim().isEmpty();
    }
    /**
     * Encapsulamento da lógica para apurar se a corFundo é válida - Comentários: Pode ser mais robusta
     * @param corFundoHex
     * @return
     */
    private boolean verificarCorFundoHex (String corFundoHex){
        return corFundoHex == null || !corFundoHex.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
    }
    /**
     * Encapsulamento da lógica para apurar se hora registrarada é nula
     * @param dateTime
     * @return
     */
    private boolean verificarHora (LocalDateTime dateTime){
        return dataHora == null;
    }

    //Encapsulamento sobre as verificações das informações
    /**
     * Encapsulamento da lógica de negócio - Facilta a modificação caso seja necessário acrescentar ou modificar algo
     */
    public void apurarInformacoesEssenciais(String id, String titulo, String corFundoHex, LocalDateTime dataHora){
        if (verificarId(id)) {
            throw new IllegalArgumentException("ID da peça não pode ser nulo ou vazio.");
        }
        if (verificarTitulo(titulo)) {
            throw new IllegalArgumentException("Título da peça não pode ser nulo ou vazio.");
        }
        // Validação simples para formato hexadecimal. Pode ser mais robusta.
        if (verificarCorFundoHex(corFundoHex)) {
            throw new IllegalArgumentException("Cor de fundo em formato hexadecimal inválido ou ausente. Ex: #RRGGBB");
        }
        if (verificarHora(dataHora)) {
            throw new IllegalArgumentException("Data e hora da peça não podem ser nulos.");
        }
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