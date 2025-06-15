package com.teatroabc.infraestrutura.ui_swing.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilitário de infraestrutura/apresentação para formatar objetos LocalDateTime
 * em uma representação String padronizada.
 * Esta classe é destinada a ser usada por adaptadores de UI ou outras camadas
 * que necessitam apresentar datas de forma legível para o usuário.
 */
public class FormatadorData {

    private static final DateTimeFormatter FORMATADOR_PADRAO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Formata um objeto {@link LocalDateTime} para uma String no padrão "dd/MM/yyyy HH:mm".
     *
     * @param data O objeto LocalDateTime a ser formatado. Não pode ser nulo.
     * @return A representação em String da data e hora formatada.
     * @throws IllegalArgumentException Se o parâmetro {@code data} for nulo.
     */
    public static String formatar(LocalDateTime data) {
        if (data == null) {
            throw new IllegalArgumentException("O objeto LocalDateTime fornecido para formatação não pode ser nulo.");
        }
        return data.format(FORMATADOR_PADRAO);
    }

    /**
     * Formata um objeto {@link LocalDateTime} usando um padrão de formatação customizado.
     *
     * @param data O objeto LocalDateTime a ser formatado. Não pode ser nulo.
     * @param padraoSDF O padrão de formatação (ex: "yyyy-MM-dd"). Não pode ser nulo ou vazio.
     * @return A representação em String da data e hora formatada.
     * @throws IllegalArgumentException Se o parâmetro {@code data} ou {@code padraoSDF} forem nulos/inválidos.
     */
    public static String formatar(LocalDateTime data, String padraoSDF) {
        if (data == null) {
            throw new IllegalArgumentException("O objeto LocalDateTime fornecido para formatação não pode ser nulo.");
        }
        if (padraoSDF == null || padraoSDF.trim().isEmpty()) {
            throw new IllegalArgumentException("O padrão de formatação (padraoSDF) não pode ser nulo ou vazio.");
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(padraoSDF);
            return data.format(formatter);
        } catch (IllegalArgumentException e) { // Captura exceção de padrão inválido do DateTimeFormatter
            throw new IllegalArgumentException("Padrão de formatação fornecido é inválido: " + padraoSDF, e);
        }
    }
}