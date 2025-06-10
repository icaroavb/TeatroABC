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

    // Cores dos Assentos (REMOVIDAS - a lógica de mapeamento de StatusAssento para Color
    // deve residir nos componentes da UI que exibem os assentos,
    // possivelmente usando as cores genéricas acima ou definindo cores específicas localmente)
    // public static final Color ASSENTO_DISPONIVEL = AZUL_CLARO; // Exemplo de como era
    // public static final Color ASSENTO_OCUPADO = BEGE;
    // public static final Color ASSENTO_SELECIONADO = VERDE;

    // Preços (REMOVIDOS - gerenciados pelo enum CategoriaAssento no domínio)

    // Fontes da UI
    public static final Font FONTE_TITULO = new Font("Arial", Font.BOLD, 56);
    public static final Font FONTE_SUBTITULO = new Font("Arial", Font.BOLD, 32);
    public static final Font FONTE_BOTAO = new Font("Arial", Font.BOLD, 24);
    public static final Font FONTE_TEXTO = new Font("Arial", Font.PLAIN, 18);
    public static final Font FONTE_LABEL = new Font("Arial", Font.PLAIN, 16);

    // Formato de Data (REAVALIAR UTILIDADE OU NOMEAR MELHOR)
    // Se for usado, deve ser para um formato específico da UI,
    // e o FormatadorData.java já possui um padrão default.
    // public static final String FORMATO_DATA_EXIBICAO_SIMPLES = "dd/MM/yyyy";

    private Constantes() {
        throw new IllegalStateException("Classe de constantes não deve ser instanciada.");
    }
}