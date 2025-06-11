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
        // A lista de seções agora será construída com os valores corretos.
        List<SecaoConfig> secoes = new ArrayList<>();

        // REQUISITOS DO PDF:
        // Plateia A: 25 -> Modelado como 5 fileiras de 5 assentos
        secoes.add(new SecaoConfig("Plateia A", CategoriaAssento.PLATEIA_A, 5, 5));

        // Plateia B: 100 -> Modelado como 10 fileiras de 10 assentos
        secoes.add(new SecaoConfig("Plateia B", CategoriaAssento.PLATEIA_B, 10, 10));

        // Frisa: 5 por frisa. Total de 6 frisas (3 de cada lado).
        // Modelado como 6 "fileiras" (unidades) de 5 assentos cada.
        secoes.add(new SecaoConfig("Frisa", CategoriaAssento.FRISA, 6, 5)); // <-- MUDANÇA AQUI de 8 para 6

        // Camarote: 10 por camarote. Assumindo 4 camarotes no total (baseado na imagem).
        // Modelado como 4 "fileiras" (unidades) de 10 assentos cada.
        secoes.add(new SecaoConfig("Camarote", CategoriaAssento.CAMAROTE, 4, 10));
        
        // Balcão Nobre: 50 -> Modelado como 5 fileiras de 10 assentos
        secoes.add(new SecaoConfig("Balcão Nobre", CategoriaAssento.BALCAO_NOBRE, 5, 10));

        // A lista final é criada com os objetos já configurados.
        // O `List.of` aqui apenas garante a imutabilidade, a configuração em si já foi feita.
        LAYOUT_PADRAO = new TeatroLayoutConfig(secoes);
    }

    /**
     * Retorna a configuração de layout padrão do teatro.
     * @return um objeto TeatroLayoutConfig com a planta do teatro.
     */
    public static TeatroLayoutConfig getLayout() {
        return LAYOUT_PADRAO;
    }
}