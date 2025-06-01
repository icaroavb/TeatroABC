package com.teatroabc.utilitarios;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormatadorData {
    public static String formatar(LocalDateTime data) {
        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
