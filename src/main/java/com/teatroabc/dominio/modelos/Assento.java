package com.teatroabc.dominio.modelos;

import com.teatroabc.dominio.enums.CategoriaAssento;
import com.teatroabc.dominio.enums.StatusAssento;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Assento {
    private final String codigo;
    private final int fileira;
    private final int numero;
    private final CategoriaAssento categoria;
    private final BigDecimal preco; 
    private StatusAssento status;

    public Assento(
                String codigo, 
                int fileira, 
                int numero, 
                CategoriaAssento categoria, 
                BigDecimal preco) {
        
        //encapsulamento das validaçoes - caso não seja mais necessario fazer validacao qualquer, basta comentar esta linha
        //apurarInformacoesEssenciais(codigo, categoria, preco); 

        this.codigo = codigo;
        this.fileira = fileira;
        this.numero = numero;
        this.categoria = categoria; 
        this.preco = preco.setScale(2, RoundingMode.HALF_UP); // Armazena com 2 casas decimais
        this.status = StatusAssento.DISPONIVEL;
    }

    // //encapsualmento individual das validacoes do construtor 
    // /**
    //  * Encapsular validacao do código
    //  * @param codigo
    //  * @return true se o valor for null ou se estiver vazio
    //  */
    // private boolean verificarCodigo (String codigo){
    //     return codigo == null || codigo.trim().isEmpty();
    // }
    // /**
    //  * Encapsular validação da categoria de Assento;
    //  * @param categoria
    //  * @return true se o assento for null
    //  */
    // private boolean verificarCategoria(CategoriaAssento categoria){
    //     return categoria == null;
    // }
    // /**
    //  * Encapsular validacoes quanto ao preço do Assento
    //  * @param preco
    //  * @return true se o Assento estiver com preco negativo ou null
    //  */
    // private boolean verificarPreco (BigDecimal preco){
    //     return preco == null || preco.compareTo(BigDecimal.ZERO) < 0;
    // }

    //encapsulamento das validacoes - facilita caso haja modificação nas regras de negócio
    // private void apurarInformacoesEssenciais(
    //                                         String codigo, 
    //                                         CategoriaAssento assento, 
    //                                         BigDecimal preco)
    //                                         {
    //     if (verificarCodigo(codigo)) {
    //         throw new IllegalArgumentException("Código do assento não pode ser nulo ou vazio.");
    //     }
    //     if (verificarCategoria(categoria)) {
    //         throw new IllegalArgumentException("Categoria do assento não pode ser nula.");
    //     }
    //     if (verificarPreco(preco)) {
    //         throw new IllegalArgumentException("Preço do assento não pode ser nulo ou negativo.");
    //     }
    // }

    // Getters
    public String getCodigo() { return codigo; }
    public int getFileira() { return fileira; }
    public int getNumero() { return numero; }
    public CategoriaAssento getCategoria() { return categoria; }
    public BigDecimal getPreco() { return preco; } // Retorna o preço armazenado
    public StatusAssento getStatus() { return status; }
    
    /**
     * Composição recebendo o enumerador
     * @param status
     */
    public void setStatus(StatusAssento status) {
        if (verificarAssentoNull(status)) {
            throw new IllegalArgumentException("Status do assento não pode ser nulo.");
        }
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assento assento = (Assento) o;
        return Objects.equals(codigo, assento.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return "Assento{" +
               "codigo='" + codigo + '\'' +
               ", categoria=" + categoria.getNome() +
               ", preco=" + preco.toString() +
               ", status=" + status +
               '}';
    }

    //extracao de métodos
    /**
     * Apura se o status do assento não referencia nada no heap (AKA, null) - private porque só faz sentido dentro 
     * do contexto da classe Assento
     * @param status
     * @return
     */
    private boolean verificarAssentoNull (StatusAssento status){
        return status == null;
    }
}