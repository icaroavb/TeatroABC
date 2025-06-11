/**
 * Esta classe será 
 */
package com.teatroabc.infraestrutura.config;

import com.teatroabc.dominio.enums.CategoriaAssento;

public class SecaoConfig {
    private final String nomeDaSecao;
    private final CategoriaAssento categoria;
    private final int numeroDeFileiras;
    private final int assentosPorFileira;

    public SecaoConfig(String nome, CategoriaAssento categoria, int fileiras, int assentosPorFileira) {
        this.nomeDaSecao = nome;
        this.categoria = categoria;
        this.numeroDeFileiras = fileiras;
        this.assentosPorFileira = assentosPorFileira;
    }

    // Getters para todos os campos
    public String getNomeDaSecao() { return nomeDaSecao; }
    public CategoriaAssento getCategoria() { return categoria; }
    public int getNumeroDeFileiras() { return numeroDeFileiras; }
    public int getAssentosPorFileira() { return assentosPorFileira; }
}