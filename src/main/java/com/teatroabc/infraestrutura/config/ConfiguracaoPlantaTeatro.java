package com.teatroabc.infraestrutura.config;

import com.teatroabc.dominio.enums.CategoriaAssento;
import java.util.List;

public class ConfiguracaoPlantaTeatro {

    private static final TeatroLayoutConfig LAYOUT_PADRAO;

    static {
        // A lista de seções de nível superior define a ordem e o posicionamento geral.
        // As seções serão renderizadas na ordem em que aparecem aqui.
        LAYOUT_PADRAO = new TeatroLayoutConfig(List.of(
            // Primeiro, definimos as seções que ficarão lado a lado.
            // Para renderizar 3 blocos de frisa por lado (total 6),
            // a forma mais limpa é tratar cada lado como uma seção única de 15 assentos (3x5).
            new SecaoConfig("Frisas Ímpares", CategoriaAssento.FRISA, 3, 5, Alinhamento.ESQUERDA),
            new SecaoConfig("Frisas Pares",   CategoriaAssento.FRISA, 3, 5, Alinhamento.DIREITA),
            
            // Em seguida, as seções centrais que serão empilhadas.
            new SecaoConfig("Plateia A",    CategoriaAssento.PLATEIA_A,    5, 5, Alinhamento.CENTRO),
            new SecaoConfig("Plateia B",    CategoriaAssento.PLATEIA_B,    10, 10, Alinhamento.CENTRO),
            new SecaoConfig("Camarote",     CategoriaAssento.CAMAROTE,     5, 10, Alinhamento.CENTRO),
            new SecaoConfig("Balcão Nobre", CategoriaAssento.BALCAO_NOBRE, 5, 10, Alinhamento.CENTRO)
        ));
    }

    public static TeatroLayoutConfig getLayout() {
        return LAYOUT_PADRAO;
    }
}