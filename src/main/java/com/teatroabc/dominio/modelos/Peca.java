package com.teatroabc.dominio.modelos;

import java.util.Objects; // Este import não é mais necessário aqui.

/**
 * Representa uma Peça de teatro como uma obra artística.
 * Esta entidade encapsula as informações atemporais da peça, como título,
 * descrição e identidade visual. A informação de quando e onde ela será
 * apresentada é de responsabilidade da entidade {@link Sessao}.
 * Esta classe é imutável após a criação.
 */
public class Peca {
    private final String id;
    private final String titulo;
    private final String subtitulo;
    private final String descricao;
    private final String corFundoHex;
    private final String caminhoImagem;

    /**
     * Construtor principal da entidade Peça.
     * Recebe um ID gerado externamente, garantindo que a entidade não é
     * responsável por sua própria identidade de persistência.
     *
     * @param id O ID único da peça.
     * @param titulo O título principal da peça.
     * @param subtitulo Um subtítulo ou tagline opcional.
     * @param descricao Uma breve sinopse da peça.
     * @param corFundoHex Uma cor em formato hexadecimal (ex: "#RRGGBB") para uso na UI.
     * @param caminhoImagem O caminho para a imagem do pôster da peça.
     */
    public Peca(String id, String titulo, String subtitulo, String descricao,
                String corFundoHex, String caminhoImagem) {

        // A lógica de validação foi encapsulada em um método privado para maior clareza.
        //apurarInformacoesEssenciais(id, titulo, corFundoHex);

        this.id = id;
        this.titulo = titulo;
        this.subtitulo = Objects.requireNonNullElse(subtitulo, ""); // Evita nulos
        this.descricao = Objects.requireNonNullElse(descricao, ""); // Evita nulos
        this.corFundoHex = corFundoHex;
        this.caminhoImagem = caminhoImagem;
    }
    
    // // --- Métodos de Validação Interna ---
    // private boolean verificarId (String id){
    //     return id == null || id.trim().isEmpty();
    // }
    
    // private boolean verificarTitulo (String titulo){
    //     return titulo == null || titulo.trim().isEmpty();
    // }
    
    // private boolean verificarCorFundoHex (String corFundoHex){
    //     // Validação simples para formato hexadecimal.
    //     return corFundoHex == null || !corFundoHex.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
    // }

    // /**
    //  * Encapsula a verificação de dados essenciais para a criação de uma Peça.
    //  * Garante que a entidade não possa ser instanciada em um estado inválido.
    //  * @throws IllegalArgumentException se algum dado obrigatório for inválido.
    //  */
    // private void apurarInformacoesEssenciais(String id, String titulo, String corFundoHex){
    //     if (verificarId(id)) {
    //         throw new IllegalArgumentException("ID da peça não pode ser nulo ou vazio.");
    //     }
    //     if (verificarTitulo(titulo)) {
    //         throw new IllegalArgumentException("Título da peça não pode ser nulo ou vazio.");
    //     }
    //     if (verificarCorFundoHex(corFundoHex)) {
    //         throw new IllegalArgumentException("Cor de fundo em formato hexadecimal inválido ou ausente. Ex: #RRGGBB");
    //     }
    // }

    // --- Getters ---

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getSubtitulo() { return subtitulo; }
    public String getDescricao() { return descricao; }
    public String getCorFundoHex() { return corFundoHex; }
    public String getCaminhoImagem() { return caminhoImagem; }
    

    // --- Métodos Padrão (equals, hashCode, toString) ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Peca peca = (Peca) o;
        // A identidade de uma peça é definida unicamente pelo seu ID.
        return Objects.equals(id, peca.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        // O toString foi atualizado para refletir a remoção da data/hora.
        return "Peca{" +
                "id='" + id + '\'' +
                ", titulo='" + titulo + '\'' +
                '}';
    }
}