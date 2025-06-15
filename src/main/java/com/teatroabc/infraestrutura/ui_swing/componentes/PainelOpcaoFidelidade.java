package com.teatroabc.infraestrutura.ui_swing.componentes;

import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Componente que encapsula a checkbox para a opção do plano de fidelidade "ABC GOLD".
 * Isola a lógica de criação e estilização da checkbox e notifica um listener
 * externo quando seu estado é alterado.
 */
public class PainelOpcaoFidelidade extends JPanel {
    
    private JCheckBox chkMembroABC;

    /**
     * Construtor do PainelOpcaoFidelidade.
     * @param onCheckboxChange O ActionListener a ser executado quando a checkbox é marcada ou desmarcada.
     */
    public PainelOpcaoFidelidade(ActionListener onCheckboxChange) {
        configurarPainel();
        
        // O container interno é usado para aplicar a borda e o fundo semi-transparente.
        JPanel containerCheckbox = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        containerCheckbox.setBackground(new Color(255, 193, 7, 30)); // Amarelo com transparência
        containerCheckbox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Constantes.AMARELO, 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        chkMembroABC = new JCheckBox("Desejo ser membro ABC GOLD");
        
        // Estilização da checkbox
        chkMembroABC.setFont(new Font("Arial", Font.BOLD, 16));
        chkMembroABC.setForeground(Constantes.AMARELO);
        chkMembroABC.setOpaque(false);
        chkMembroABC.setFocusPainted(false);
        chkMembroABC.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chkMembroABC.setIconTextGap(10);
        
        // Associa a ação recebida ao listener da checkbox.
        chkMembroABC.addActionListener(onCheckboxChange);
        
        containerCheckbox.add(chkMembroABC);
        add(containerCheckbox);
    }
    
    /**
     * Configura as propriedades visuais do painel principal.
     */
    private void configurarPainel() {
        setLayout(new FlowLayout(FlowLayout.CENTER));
        setOpaque(false); // O painel principal é transparente
        setMaximumSize(new Dimension(500, 70));
    }
}