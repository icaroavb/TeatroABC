package com.teatroabc.infraestrutura.ui_swing.componentes;

import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import javax.swing.*;
import java.awt.*;

public class LogoTeatro extends JPanel {
    
    public LogoTeatro() {
        setPreferredSize(new Dimension(400, 100)); // Aumentado significativamente
        setBackground(Constantes.AZUL_ESCURO);
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int x = 10; // Começar mais à esquerda
        int y = getHeight() / 2;
        
        // Máscara feliz (laranja)
        g2d.setColor(Constantes.LARANJA);
        g2d.fillOval(x, y - 30, 50, 50); // Máscaras menores
        
        // Detalhes da máscara feliz
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        // Olhos
        g2d.fillOval(x + 12, y - 18, 6, 6);
        g2d.fillOval(x + 32, y - 18, 6, 6);
        // Sorriso
        g2d.drawArc(x + 12, y - 8, 26, 16, 0, -180);
        
        // Máscara triste (azul)
        x += 60; // Espaço menor entre máscaras
        g2d.setColor(Constantes.AZUL_CLARO);
        g2d.fillOval(x, y - 30, 50, 50);
        
        // Detalhes da máscara triste
        g2d.setColor(Color.BLACK);
        // Olhos
        g2d.fillOval(x + 12, y - 18, 6, 6);
        g2d.fillOval(x + 32, y - 18, 6, 6);
        // Tristeza
        g2d.drawArc(x + 12, y + 2, 26, 16, 0, 180);
        
        // Texto TEATRO ABC com espaço garantido
        x += 80; // Espaço adequado após as máscaras
        
        // TEATRO
        g2d.setFont(new Font("Arial", Font.BOLD, 32)); // Fonte menor para caber
        g2d.setColor(Constantes.AZUL_CLARO);
        g2d.drawString("TEATRO", x, y - 5);
        
        // ABC
        g2d.setColor(Constantes.LARANJA);
        g2d.drawString("ABC", x + 20, y + 25); // Posicionamento ajustado
        
        g2d.dispose();
    }
}