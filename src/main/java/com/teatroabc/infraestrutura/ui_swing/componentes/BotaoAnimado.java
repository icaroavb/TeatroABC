package com.teatroabc.infraestrutura.ui_swing.componentes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Um componente JButton customizado que apresenta animações de hover (passagem do mouse)
 * e uma aparência arredondada com sombra, proporcionando uma experiência de usuário mais rica.
 * 
 * Este componente é puramente visual e de infraestrutura de UI, não tendo conhecimento
 * sobre o domínio da aplicação.
 */
public class BotaoAnimado extends JButton {
    private final Color corOriginal; // Cor do botão em seu estado normal.
    private final Color corHover;    // Cor do botão quando o mouse está sobre ele.
    private float escala = 1.0f;     // Fator de escala para animação de tamanho.
    private Timer animador;          // Timer para controlar a animação suave.

    /**
     * Construtor do BotaoAnimado.
     *
     * @param texto O texto a ser exibido no botão. Pode conter quebras de linha (\n).
     * @param corFundo A cor de fundo do botão em seu estado normal.
     * @param corHover A cor de fundo do botão no estado de hover.
     * @param tamanho A dimensão (largura e altura) preferencial do botão.
     */
    public BotaoAnimado(String texto, Color corFundo, Color corHover, Dimension tamanho) {
        super(texto);
        this.corOriginal = corFundo;
        this.corHover = corHover;
        
        // Configurações visuais e de comportamento padrão.
        setPreferredSize(tamanho);
        setBackground(corFundo);
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false); // O fundo é desenhado manualmente.
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Adiciona listeners de mouse para controlar as animações e aparência.
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Inicia a animação de "crescimento" e mudança de cor.
                animar(true);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                // Inicia a animação para retornar ao estado original.
                animar(false);
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                // Efeito de "pressionar", diminuindo a escala do botão.
                escala = 0.95f;
                repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                // Retorna à escala de hover normal ao soltar o clique.
                escala = 1.05f; // Volta para o estado de hover, não para o original.
                repaint();
            }
        });
    }
    
    /**
     * Inicia ou para o Timer que controla a animação de escala.
     * @param hover {@code true} para animar para o estado de hover, {@code false} para o estado normal.
     */
    private void animar(boolean hover) {
        // Para qualquer animação anterior para evitar conflitos.
        if (animador != null && animador.isRunning()) {
            animador.stop();
        }
        
        // Cria um novo timer para a transição suave.
        animador = new Timer(10, e -> {
            boolean animacaoConcluida = false;
            if (hover) {
                if (escala < 1.05f) {
                    escala += 0.01f;
                } else {
                    escala = 1.05f;
                    animacaoConcluida = true;
                }
            } else { // !hover
                if (escala > 1.0f) {
                    escala -= 0.01f;
                } else {
                    escala = 1.0f;
                    animacaoConcluida = true;
                }
            }
            
            repaint(); // Repinta o botão a cada passo da animação.
            
            if (animacaoConcluida) {
                ((Timer)e.getSource()).stop();
            }
        });
        animador.start();
    }
    
    /**
     * Sobrescreve o método de pintura para desenhar a aparência customizada do botão.
     * @param g O contexto gráfico.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int largura = getWidth();
        int altura = getHeight();
        
        // Calcula as dimensões e posição do botão com base no fator de escala atual.
        int larguraEscalada = (int)(largura * escala);
        int alturaEscalada = (int)(altura * escala);
        int x = (largura - larguraEscalada) / 2;
        int y = (altura - alturaEscalada) / 2;
        
        // Desenha uma sombra sutil sob o botão.
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillRoundRect(x + 3, y + 3, larguraEscalada, alturaEscalada, 20, 20);
        
        // Interpola a cor entre a original e a de hover para uma transição suave.
        Color corAtual = misturarCores(corOriginal, corHover, (escala - 1.0f) * 20);
        g2d.setColor(corAtual);
        g2d.fillRoundRect(x, y, larguraEscalada, alturaEscalada, 20, 20);
        
        // Desenha o texto, centralizado e com suporte a múltiplas linhas.
        g2d.setColor(getForeground());
        g2d.setFont(getFont());
        FontMetrics fm = g2d.getFontMetrics();
        
        String[] linhas = getText().split("\n");
        int alturaTotalTexto = fm.getHeight() * linhas.length + fm.getLeading() * (linhas.length - 1);
        int yTexto = (altura - alturaTotalTexto) / 2 + fm.getAscent();
        
        for (String linha : linhas) {
            int xTexto = (largura - fm.stringWidth(linha)) / 2;
            g2d.drawString(linha, xTexto, yTexto);
            yTexto += fm.getHeight();
        }
        
        g2d.dispose();
    }
    
    /**
     * Interpola linearmente entre duas cores.
     * @param c1 A cor inicial.
     * @param c2 A cor final.
     * @param proporcao A proporção da mistura (0.0f = c1, 1.0f = c2).
     * @return A cor resultante da mistura.
     */
    private Color misturarCores(Color c1, Color c2, float proporcao) {
        proporcao = Math.max(0, Math.min(1, proporcao)); // Garante que a proporção esteja entre 0 e 1.
        int r = (int)(c1.getRed() * (1 - proporcao) + c2.getRed() * proporcao);
        int g = (int)(c1.getGreen() * (1 - proporcao) + c2.getGreen() * proporcao);
        int b = (int)(c1.getBlue() * (1 - proporcao) + c2.getBlue() * proporcao);
        return new Color(r, g, b);
    }
}