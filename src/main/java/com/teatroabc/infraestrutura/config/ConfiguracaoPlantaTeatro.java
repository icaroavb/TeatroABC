package com.teatroabc.infraestrutura.config;

import com.teatroabc.dominio.enums.CategoriaAssento;
import java.util.List;

public class ConfiguracaoPlantaTeatro {

    private static final TeatroLayoutConfig LAYOUT_PADRAO;

    static {
        // A lista de seções de nível superior define a ordem e o posicionamento geral.
        LAYOUT_PADRAO = new TeatroLayoutConfig(List.of(
            /*
             * CORREÇÃO: Defina a categoria FRISA uma única vez.
             * O AssentoRepositorio irá gerar os assentos para esta categoria uma vez.
             * A TelaSelecionarAssento irá então dividir os assentos gerados
             * para exibição nos lados esquerdo e direito.
             * Total de fileiras de frisa: 6 (3 de cada lado). Total de assentos por fileira: 5.
             */
            new SecaoConfig("Frisa", CategoriaAssento.FRISA, 6, 5, Alinhamento.CENTRO), // Ajustado para representar o total
            
            // Seções centrais permanecem as mesmas
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