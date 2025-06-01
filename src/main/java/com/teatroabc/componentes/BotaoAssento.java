package com.teatroabc.componentes;

import com.teatroabc.enums.StatusAssento;
import com.teatroabc.modelos.Assento;
import javax.swing.*;
import java.awt.*;

public class BotaoAssento extends JButton {
    private static final int TAMANHO = 30;

    public BotaoAssento(Assento assento) {
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

        }