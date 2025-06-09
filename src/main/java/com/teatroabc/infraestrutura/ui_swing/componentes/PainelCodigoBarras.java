package com.teatroabc.infraestrutura.ui_swing.componentes;

import javax.swing.*;
import java.awt.*;

public class PainelCodigoBarras extends JPanel {
    private String codigo;

    public PainelCodigoBarras(String codigo) {
        this.codigo = codigo;
        setPreferredSize(new Dimension(220, 60));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Desenho simples para ilustrar um código de barras
        int x = 10;
        for (char c : codigo.toCharArray()) {
            int altura = 40 + (c % 20);
            g.fillRect(x, 10, 2, altura);
            x += 4;
        }
        g.drawString(codigo, 10, 55);
    }
}
