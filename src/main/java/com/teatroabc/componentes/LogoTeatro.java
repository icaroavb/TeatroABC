package com.teatroabc.componentes;

import com.teatroabc.constantes.Constantes;
import javax.swing.*;
import java.awt.*;

public class LogoTeatro extends JPanel {
    
    public LogoTeatro() {
        setPreferredSize(new Dimension(280, 100));
        setBackground(Constantes.AZUL_ESCURO);
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int x = 20;
        int y = getHeight() / 2;
        
        // Máscara feliz (laranja)
        g2d.setColor(Constantes.LARANJA);
        g2d.fillOval(x, y - 35, 60, 60);
        
        // Detalhes da máscara feliz
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(3));
        // Olhos
        g2d.fillOval(x + 15, y - 20, 8, 8);
        g2d.fillOval(x + 37, y - 20, 8, 8);
        // Sorriso
        g2d.drawArc(x + 15, y - 10, 30, 20, 0, -180);
        
        // Máscara triste (azul)
        x += 70;
        g2d.setColor(Constantes.AZUL_CLARO);
        g2d.fillOval(x, y - 35, 60, 60);
        
        // Detalhes da máscara triste
        g2d.setColor(Color.BLACK);
        // Olhos
        g2d.fillOval(x + 15, y - 20, 8, 8);
        g2d.fillOval(x + 37, y - 20, 8, 8);
        // Tristeza
        g2d.drawArc(x + 15, y, 30, 20, 0, 180);
        
        // Texto TEATRO ABC
        x += 80;
        g2d.setFont(new Font("Arial", Font.BOLD, 40));
        g2d.setColor(Constantes.AZUL_CLARO);
        g2d.drawString("TEATRO", x, y - 5);
        g2d.setColor(Constantes.LARANJA);
        g2d.drawString("ABC", x, y + 30);
        
        g2d.dispose();
    }
}