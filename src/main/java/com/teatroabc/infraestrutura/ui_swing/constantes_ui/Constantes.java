package com.teatroabc.infraestrutura.ui_swing.constantes_ui;

import java.awt.Color;
import java.awt.Font;

public class Constantes {
    // Cores da UI
    public static final Color AZUL_ESCURO = new Color(23, 42, 58);
    public static final Color LARANJA = new Color(239, 125, 0);
    public static final Color AMARELO = new Color(255, 193, 7);
    public static final Color AZUL_CLARO = new Color(52, 152, 219); // Usada para ASSENTO_DISPONIVEL
    public static final Color VERDE = new Color(139, 195, 74);      // Usada para ASSENTO_SELECIONADO
    public static final Color VERMELHO = new Color(229, 57, 53);
    public static final Color BEGE = new Color(222, 184, 135);        // Usada para ASSENTO_OCUPADO
    public static final Color CINZA_ESCURO = new Color(52, 73, 94);

    // REMOVIDO - Cores dos Assentos (UI)
    // public static final Color ASSENTO_DISPONIVEL = new Color(52, 152, 219);
    // public static final Color ASSENTO_OCUPADO = new Color(222, 184, 135);
    // public static final Color ASSENTO_SELECIONADO = new Color(139, 195, 74);

    // Preços (REMOVIDOS)

    // Fontes da UI
    public static final Font FONTE_TITULO = new Font("Arial", Font.BOLD, 56);
    // ... (outras fontes)

    // Formato de Data (REAVALIAR)
    // public static final String FORMATO_DATA_EXIBICAO_SIMPLES = "dd/MM/yyyy";

    private Constantes() {
        throw new IllegalStateException("Classe de constantes não deve ser instanciada.");
    }
}