package com.teatroabc.componentes;

import com.teatroabc.constantes.Constantes;
import com.teatroabc.enums.StatusAssento;
import com.teatroabc.modelos.Assento;
import javax.swing.*;
import java.awt.*;

public class BotaoAssento extends JButton {
    private Assento assento;
    private static final int TAMANHO = 30;
    
    public BotaoAssento(Assento assento) {
        this.assento = assento;
        setPreferredSize(new Dimension(TAMANHO, TAMANHO));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        
        if (assento.getStatus() != StatusAssento.OCUPADO) {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        } else {
            setEnabled(false);
        }
        
        atualizarAparencia();
    }
    
    private void atualizarAparencia() {
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        Color cor = null;
        switch (assento.getStatus()) {
            case DISPONIVEL:
                cor = Constantes.ASSENTO_DISPONIVEL;
                break;
            case OCUPADO:
                cor = Constantes.ASSENTO_OCUPADO;
                break;
            case SELECIONADO:
                cor = Constantes.ASSENTO_SELECIONADO;
                break;
        }
        
        // Desenhar círculo
        g2d.setColor(cor);
        g2d.fillOval(2, 2, getWidth() - 4, getHeight() - 4);
        
        // Borda
        g2d.setColor(cor.darker());
        g2d.setStroke(new BasicStroke(1));
        g2d.drawOval(2, 2, getWidth() - 4, getHeight() - 4);
        
        g2d.dispose();
    }
}
