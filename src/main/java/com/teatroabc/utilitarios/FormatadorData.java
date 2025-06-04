package com.teatroabc.utilitarios;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Checklist SOLID 
 * S - Responsabilidade Única OK
 * O - Aberto para Extensões, Fechado para Modificações OK (é necessário agora implementar uma interface)
 * L - Princípio da Substitição de Liskov - OK
 * I - Interface Segretation (É necessário fazer )
 */
public class FormatadorData {
    public static String formatar(LocalDateTime data) {
        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
