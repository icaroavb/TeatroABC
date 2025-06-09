package com.teatroabc;

import com.teatroabc.infraestrutura.ui_swing.telas.TelaPrincipal;
import javax.swing.*;

public class TeatroABCApplication {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Teatro ABC - Sistema de Bilheteria");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 900);
            frame.setLocationRelativeTo(null);
            frame.setResizable(true);
            frame.setContentPane(new TelaPrincipal());
            frame.setVisible(true);
        });
    }
}


