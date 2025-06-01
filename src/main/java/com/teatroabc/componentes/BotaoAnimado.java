package com.teatroabc.componentes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BotaoAnimado extends JButton {
    private Color corOriginal;
    private Color corHover;
    private float escala = 1.0f;
    private Timer animador;
    
    public BotaoAnimado(String texto, Color corFundo, Color corHover, Dimension tamanho) {
        super(texto);
        this.corOriginal = corFundo;
        this.corHover = corHover;
        
        setPreferredSize(tamanho);
        setBackground(corFundo);
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                animar(true);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                animar(false);
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                escala = 0.95f;
                repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                escala = 1.0f;
                repaint();
            }
        });
    }
    
    private void animar(boolean hover) {
        if (animador != null && animador.isRunning()) {
            animador.stop();
        }
        
        animador = new Timer(10, e -> {
            if (hover && escala < 1.05f) {
                escala += 0.01f;
            } else if (!hover && escala > 1.0f) {
                escala -= 0.01f;
            } else {
                ((Timer)e.getSource()).stop();
            }
            repaint();
        });
        animador.start();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int largura = getWidth();
        int altura = getHeight();
        int larguraEscalada = (int)(largura * escala);
        int alturaEscalada = (int)(altura * escala);
        int x = (largura - larguraEscalada) / 2;
        int y = (altura - alturaEscalada) / 2;
        
        // Sombra
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillRoundRect(x + 3, y + 3, larguraEscalada, alturaEscalada, 15, 15);
        
        // Botão
        Color corAtual = escala > 1.0f ? misturarCores(corOriginal, corHover, (escala - 1.0f) * 20) : corOriginal;
        g2d.setColor(corAtual);
        g2d.fillRoundRect(x, y, larguraEscalada, alturaEscalada, 15, 15);
        
        // Texto
        g2d.setColor(getForeground());
        g2d.setFont(getFont());
        FontMetrics fm = g2d.getFontMetrics();
        
        String[] linhas = getText().split("\n");
        int alturaTexto = fm.getHeight() * linhas.length;
        int yTexto = (altura - alturaTexto) / 2 + fm.getAscent();
        
        for (String linha : linhas) {
            int xTexto = (largura - fm.stringWidth(linha)) / 2;
            g2d.drawString(linha, xTexto, yTexto);
            yTexto += fm.getHeight();
        }
        
        g2d.dispose();
    }
    
    private Color misturarCores(Color c1, Color c2, float proporcao) {
        proporcao = Math.max(0, Math.min(1, proporcao));
        int r = (int)(c1.getRed() * (1 - proporcao) + c2.getRed() * proporcao);
        int g = (int)(c1.getGreen() * (1 - proporcao) + c2.getGreen() * proporcao);
        int b = (int)(c1.getBlue() * (1 - proporcao) + c2.getBlue() * proporcao);
        return new Color(r, g, b);
    }
}
