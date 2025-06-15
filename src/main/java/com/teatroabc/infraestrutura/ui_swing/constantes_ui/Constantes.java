package com.teatroabc.infraestrutura.ui_swing.constantes_ui;

import java.awt.Color;
import java.awt.Font;

/**
 * Classe utilitária para armazenar constantes globais utilizadas na aplicação,
 * principalmente relacionadas à interface gráfica (cores, fontes).
 * Seu uso deve ser restrito à camada de apresentação (UI) para manter
 * o núcleo do domínio e serviços desacoplado de detalhes de UI.
 */
public class Constantes {

    // Cores da UI
    public static final Color AZUL_ESCURO = new Color(23, 42, 58);
    public static final Color LARANJA = new Color(239, 125, 0);
    public static final Color AMARELO = new Color(255, 193, 7);
    public static final Color AZUL_CLARO = new Color(52, 152, 219);
    public static final Color VERDE = new Color(139, 195, 74);
    public static final Color VERMELHO = new Color(229, 57, 53);
    public static final Color BEGE = new Color(222, 184, 135);
    public static final Color CINZA_ESCURO = new Color(52, 73, 94);

    // Fontes da UI
    public static final Font FONTE_TITULO = new Font("Arial", Font.BOLD, 56);
    public static final Font FONTE_SUBTITULO = new Font("Arial", Font.BOLD, 32);
    public static final Font FONTE_BOTAO = new Font("Arial", Font.BOLD, 24);
    public static final Font FONTE_TEXTO = new Font("Arial", Font.PLAIN, 18);
    public static final Font FONTE_LABEL = new Font("Arial", Font.PLAIN, 16);

    private Constantes() {
        throw new IllegalStateException("Classe de constantes não deve ser instanciada.");
    }
}