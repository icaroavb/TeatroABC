package com.teatroabc.infraestrutura.ui_swing.componentes;

import javax.swing.*;
import java.awt.*;

/**
 * Um componente JPanel customizado que desenha uma representação visual simples
 * de um código de barras a partir de uma string.
 *
 * Este é um componente de UI puramente visual, usado para adicionar um elemento
 * temático aos detalhes do bilhete, sem qualquer funcionalidade de escaneamento real.
 */
public class PainelCodigoBarras extends JPanel {
    private final String codigo; // O código a ser representado visualmente.

    /**
     * Construtor do PainelCodigoBarras.
     * @param codigo A string do código que será desenhada e representada. Não pode ser nulo.
     */
    public PainelCodigoBarras(String codigo) {
        if (codigo == null) {
            throw new IllegalArgumentException("O código não pode ser nulo.");
        }
        this.codigo = codigo;
        
        // Define um tamanho padrão e torna o fundo branco para simular uma etiqueta.
        setPreferredSize(new Dimension(280, 70));
        setBackground(Color.WHITE);
        setOpaque(true);
    }

    /**
     * Sobrescreve o método de pintura para desenhar as barras e o texto do código.
     * A "aleatoriedade" das barras é baseada nos caracteres da string do código,
     * criando um padrão único para cada código.
     * @param g O contexto gráfico.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Limpa o painel com a cor de fundo (branco).
        
        int x = 20; // Posição inicial das barras
        int larguraBarra = 2;
        int espacamento = 2;
        
        g.setColor(Color.BLACK); // Cor das barras

        // Itera sobre os caracteres do código para desenhar as barras.
        for (char c : codigo.toCharArray()) {
            // A altura da barra é pseudo-aleatória, baseada no valor do caractere.
            // Isso garante que o mesmo código sempre produza o mesmo padrão de barras.
            int altura = 30 + (c % 25); 
            g.fillRect(x, 10, larguraBarra, altura); // Desenha uma barra retangular.
            x += larguraBarra + espacamento;
        }
        
        // Desenha o texto do código abaixo das barras.
        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        FontMetrics fm = g.getFontMetrics();
        int xTexto = (getWidth() - fm.stringWidth(codigo)) / 2; // Centraliza o texto
        g.drawString(codigo, xTexto, 60);
    }
}