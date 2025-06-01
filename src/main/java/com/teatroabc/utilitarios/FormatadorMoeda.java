package com.teatroabc.utilitarios;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatadorMoeda {
    public static String formatar(double valor) {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(valor);
    }
}
