package com.teatroabc.infraestrutura.config;

import com.teatroabc.dominio.enums.CategoriaAssento;
import java.util.List;
import java.util.ArrayList;

/**
 * Fonte Única de Verdade para a configuração da planta do Teatro ABC.
 * Esta classe encapsula a lógica de layout, garantindo que toda a aplicação
 * (backend e frontend) trabalhe com a mesma estrutura de assentos,
 * conforme definido nos requisitos do projeto.
 */
public class ConfiguracaoPlantaTeatro {

    private static final TeatroLayoutConfig LAYOUT_PADRAO;

    static {
        List<SecaoConfig> secoes = new ArrayList<>();

        // REQUISITOS DO PDF:
        // Plateia A: 25 -> Modelado como 5 fileiras de 5 assentos
        secoes.add(new SecaoConfig("Plateia A", CategoriaAssento.PLATEIA_A, 5, 5));

        // Plateia B: 100 -> Modelado como 10 fileiras de 10 assentos
        secoes.add(new SecaoConfig("Plateia B", CategoriaAssento.PLATEIA_B, 10, 10));

        // Frisa: 5 por frisa. Assumindo 8 frisas no total (baseado na imagem de exemplo).
        // Modelado como 8 "fileiras" (unidades) de 5 assentos cada.
        secoes.add(new SecaoConfig("Frisa", CategoriaAssento.FRISA, 8, 5));

        // Camarote: 10 por camarote. Assumindo 4 camarotes no total.
        // Modelado como 4 "fileiras" (unidades) de 10 assentos cada.
        secoes.add(new SecaoConfig("Camarote", CategoriaAssento.CAMAROTE, 4, 10));
        
        // Balcão Nobre: 50 -> Modelado como 5 fileiras de 10 assentos
        secoes.add(new SecaoConfig("Balcão Nobre", CategoriaAssento.BALCAO_NOBRE, 5, 10));

        // OBS: A seção "Balcão" do código antigo foi removida pois não consta nos requisitos.

        LAYOUT_PADRAO = new TeatroLayoutConfig(List.of(
            // Definindo a ordem visual de renderização
            new SecaoConfig("Plateia A", CategoriaAssento.PLATEIA_A, 5, 5),
            new SecaoConfig("Plateia B", CategoriaAssento.PLATEIA_B, 10, 10),
            new SecaoConfig("Frisa", CategoriaAssento.FRISA, 8, 5),
            new SecaoConfig("Camarote", CategoriaAssento.CAMAROTE, 4, 10),
            new SecaoConfig("Balcão Nobre", CategoriaAssento.BALCAO_NOBRE, 5, 10)
        ));
    }

    /**
     * Retorna a configuração de layout padrão do teatro.
     * @return um objeto TeatroLayoutConfig com a planta do teatro.
     */
    public static TeatroLayoutConfig getLayout() {
        return LAYOUT_PADRAO;
    }
}