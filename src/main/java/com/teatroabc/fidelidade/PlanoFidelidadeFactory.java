package com.teatroabc.fidelidade;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PlanoFidelidadeFactory {
    private static final Map<String, Supplier<PlanoFidelidade>> planos = new HashMap<>();

    static {
        planos.put(SemFidelidade.IDENTIFICADOR, SemFidelidade::new);
        planos.put(MembroABCGold.IDENTIFICADOR, MembroABCGold::new);
        // Adicionar novos planos aqui
    }

    public static PlanoFidelidade criar(String identificadorPlano) {
        if (identificadorPlano == null || !planos.containsKey(identificadorPlano)) {
            // Retorna o plano padrão se o identificador for desconhecido ou nulo
            return new SemFidelidade();
        }
        return planos.get(identificadorPlano).get();
    }

    // Método para obter todos os planos disponíveis (útil para um ComboBox na UI de cadastro)
    public static Map<String, String> getPlanosDisponiveis() {
        Map<String, String> nomesPlanos = new HashMap<>();
        planos.forEach((id, supplier) -> nomesPlanos.put(id, supplier.get().getNomePlano()));
        return nomesPlanos;
    }
}
