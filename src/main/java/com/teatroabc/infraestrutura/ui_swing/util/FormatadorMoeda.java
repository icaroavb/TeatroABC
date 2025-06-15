package com.teatroabc.infraestrutura.ui_swing.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
// Sugestão: importar BigDecimal se quisermos adicionar um método para formatar BigDecimal
// import java.math.BigDecimal;

public class FormatadorMoeda {

    // Mantém o Locale.forLanguageTag("pt-BR") para consistência com a implementação original
    // Poderia ser configurável ou usar Locale.getDefault() se a localização dinâmica fosse um requisito.
    private static final Locale LOCALE_BRASIL = Locale.forLanguageTag("pt-BR");

    /**
     * Formata um valor {@code double} como uma string de moeda no formato brasileiro (R$).
     *
     * ATENÇÃO: Para cálculos financeiros precisos, {@link java.math.BigDecimal} é preferível
     * a {@code double}. Este método é fornecido para compatibilidade ou para casos onde
     * a precisão de {@code double} é aceitável para a formatação final.
     *
     * @param valor O valor numérico (double) a ser formatado.
     * @return A representação em String do valor formatado como moeda (ex: "R$ 1.234,56").
     *         Retorna uma string indicativa como "Valor Inválido" se ocorrer um erro na formatação,
     *         mas idealmente deveria lançar uma exceção para valores não numéricos se NumberFormat falhar.
     *         (NumberFormat lida bem com NaN e Infinito, retornando símbolos específicos)
     */
    public static String formatar(double valor) {
        // NumberFormat.getCurrencyInstance já lida com NaN e Infinito retornando "�" ou similar.
        // Não há necessidade de checagem de nulo para tipo primitivo double.
        try {
            NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(LOCALE_BRASIL);
            return formatadorMoeda.format(valor);
        } catch (Exception e) {
            // Esta exceção é improvável com a implementação atual de NumberFormat para double,
            // mas é uma salvaguarda.
            System.err.println("Erro ao formatar valor monetário (double): " + valor + " - " + e.getMessage());
            return "Valor Inválido"; // Ou poderia relançar uma exceção customizada
        }
    }

    /**
     * Formata um valor {@link java.math.BigDecimal} como uma string de moeda no formato brasileiro (R$).
     * Este é o método preferencial para formatação de valores monetários devido à precisão do BigDecimal.
     *
     * @param valor O objeto BigDecimal a ser formatado. Não pode ser nulo.
     * @return A representação em String do valor formatado como moeda (ex: "R$ 1.234,56").
     * @throws IllegalArgumentException Se o parâmetro {@code valor} for nulo.
     */
    public static String formatar(BigDecimal valor) {
        if (valor == null) {
            throw new IllegalArgumentException("O objeto BigDecimal fornecido para formatação não pode ser nulo.");
        }
        try {
            NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(LOCALE_BRASIL);
            return formatadorMoeda.format(valor);
        } catch (Exception e) {
            // Improvável com BigDecimal válido, mas como salvaguarda.
            System.err.println("Erro ao formatar valor monetário (BigDecimal): " + valor.toString() + " - " + e.getMessage());
            // Lançar uma exceção aqui seria mais apropriado do que retornar "Valor Inválido"
            // pois a entrada BigDecimal nula já foi tratada.
            throw new RuntimeException("Erro inesperado ao formatar BigDecimal para moeda.", e);
        }
    }
}