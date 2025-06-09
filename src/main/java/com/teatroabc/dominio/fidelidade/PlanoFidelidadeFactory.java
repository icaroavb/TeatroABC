package com.teatroabc.dominio.fidelidade;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Factory para criar instâncias de PlanoFidelidade com base em um identificador.
 * Ajuda a desacoplar a criação das estratégias concretas.
 */
public class PlanoFidelidadeFactory {

    // Mapa para registrar os construtores (suppliers) das implementações de PlanoFidelidade
    private static final Map<String, Supplier<PlanoFidelidade>> REGISTRADOS = new HashMap<>();

    // Bloco estático para registrar os planos conhecidos
    static {
        registrarPlano(SemFidelidade.IDENTIFICADOR, SemFidelidade::new);
        registrarPlano(MembroABCGold.IDENTIFICADOR, MembroABCGold::new);
        // Para adicionar um novo plano:
        // registrarPlano(NovoPlano.IDENTIFICADOR, NovoPlano::new);
    }

    /**
     * Registra um novo tipo de plano de fidelidade na factory.
     * @param identificador O identificador único do plano.
     * @param supplier O Supplier que cria uma nova instância do plano.
     */
    public static void registrarPlano(String identificador, Supplier<PlanoFidelidade> supplier) {
        REGISTRADOS.put(identificador, supplier);
    }

    /**
     * Cria uma instância de PlanoFidelidade com base no identificador fornecido.
     * Se o identificador for nulo ou não reconhecido, retorna uma instância de SemFidelidade (plano padrão).
     * @param identificadorPlano O identificador do plano a ser criado.
     * @return Uma instância de PlanoFidelidade.
     */
    public static PlanoFidelidade criar(String identificadorPlano) {
        if (identificadorPlano != null && REGISTRADOS.containsKey(identificadorPlano)) {
            return REGISTRADOS.get(identificadorPlano).get();
        }
        // Log ou tratamento de erro se o identificador não for encontrado e não houver padrão
        System.err.println("Atenção: Identificador de plano não reconhecido '" + identificadorPlano + "'. Usando plano padrão.");
        return new SemFidelidade(); // Retorna o plano padrão como fallback
    }

    /**
     * Retorna um mapa com os identificadores e nomes dos planos de fidelidade disponíveis.
     * Útil para popular interfaces de usuário, como ComboBoxes.
     * O mapa retornado é não modificável.
     * @return Um mapa de Identificador -> Nome do Plano.
     */
    public static Map<String, String> getPlanosDisponiveis() {
        Map<String, String> nomesPlanos = new HashMap<>();
        REGISTRADOS.forEach((id, supplier) -> {
            // Cria uma instância temporária para obter o nome do plano
            // Isso pode ser otimizado se o nome do plano for também estático na implementação
            PlanoFidelidade plano = supplier.get();
            if (plano != null) {
                nomesPlanos.put(id, plano.getNomePlano());
            }
        });
        return Collections.unmodifiableMap(nomesPlanos);
    }

    /**
     * Verifica se um identificador de plano é reconhecido pela factory.
     * @param identificadorPlano O identificador a ser verificado.
     * @return true se o plano é reconhecido, false caso contrário.
     */
    public static boolean isPlanoReconhecido(String identificadorPlano) {
        return identificadorPlano != null && REGISTRADOS.containsKey(identificadorPlano);
    }
}