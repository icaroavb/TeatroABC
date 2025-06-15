package com.teatroabc.infraestrutura.config;

import com.teatroabc.dominio.enums.CategoriaAssento;
import java.util.Collections;
import java.util.List;

public class SecaoConfig {
    private final String nomeDaSecao;
    private final CategoriaAssento categoria;
    private final int numeroDeFileiras;
    private final int assentosPorFileira;
    private final Alinhamento alinhamento;
    private final List<SecaoConfig> subSecoes; // NOVO CAMPO para hierarquia

    // Construtor para seções que podem ter sub-seções
    public SecaoConfig(String nome, CategoriaAssento categoria, int fileiras, int assentosPorFileira, Alinhamento alinhamento, List<SecaoConfig> subSecoes) {
        this.nomeDaSecao = nome;
        this.categoria = categoria;
        this.numeroDeFileiras = fileiras;
        this.assentosPorFileira = assentosPorFileira;
        this.alinhamento = alinhamento;
        this.subSecoes = subSecoes != null ? subSecoes : Collections.emptyList();
    }
    
    // Construtor de conveniência para seções simples (folhas da árvore)
    public SecaoConfig(String nome, CategoriaAssento categoria, int fileiras, int assentosPorFileira, Alinhamento alinhamento) {
        this(nome, categoria, fileiras, assentosPorFileira, alinhamento, null);
    }

    // Getters
    public String getNomeDaSecao() { return nomeDaSecao; }
    public CategoriaAssento getCategoria() { return categoria; }
    public int getNumeroDeFileiras() { return numeroDeFileiras; }
    public int getAssentosPorFileira() { return assentosPorFileira; }
    public Alinhamento getAlinhamento() { return alinhamento; }
    public List<SecaoConfig> getSubSecoes() { return subSecoes; }
}