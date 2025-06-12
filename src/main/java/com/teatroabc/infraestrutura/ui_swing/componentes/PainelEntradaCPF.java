package com.teatroabc.infraestrutura.ui_swing.componentes;

import com.teatroabc.dominio.validadores.ValidadorCPF;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.ParseException;

/**
 * Componente que encapsula o formulário de entrada de CPF, incluindo
 * o título, o campo de texto formatado e o botão de ação.
 */
public class PainelEntradaCPF extends JPanel {

    private JFormattedTextField txtCPF;

    /**
     * Construtor do PainelEntradaCPF.
     * @param tituloDaTela O texto a ser exibido como título principal.
     * @param continuarAction O ActionListener a ser executado quando o botão "Continuar" for clicado.
     */
    public PainelEntradaCPF(String tituloDaTela, ActionListener continuarAction) {
        configurarPainel();
        adicionarComponentes(tituloDaTela, continuarAction);
    }

    /**
     * Retorna o CPF digitado pelo usuário, já normalizado (apenas dígitos).
     * @return Uma string contendo os 11 dígitos do CPF, ou uma string vazia se nada foi digitado.
     */
    public String getCPF() {
        String texto = txtCPF.getText();
        if (texto == null || texto.contains("_")) {
            return "";
        }
        return ValidadorCPF.normalizar(texto);
    }

    private void configurarPainel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void adicionarComponentes(String tituloDaTela, ActionListener continuarAction) {
        JLabel titulo = new JLabel(tituloDaTela);
        titulo.setFont(Constantes.FONTE_TITULO.deriveFont(48f));
        titulo.setForeground(Constantes.AMARELO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        configurarCampoCPF();
        
        BotaoAnimado btnContinuar = new BotaoAnimado("CONTINUAR",
                Constantes.LARANJA, Constantes.AMARELO.darker(), new Dimension(380, 65));
        btnContinuar.setFont(Constantes.FONTE_BOTAO);
        btnContinuar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnContinuar.addActionListener(continuarAction);

        add(titulo);
        add(Box.createRigidArea(new Dimension(0, 40)));
        add(txtCPF);
        add(Box.createRigidArea(new Dimension(0, 40)));
        add(btnContinuar);
    }

    private void configurarCampoCPF() {
        try {
            MaskFormatter maskCPF = new MaskFormatter("###.###.###-##");
            maskCPF.setPlaceholderCharacter('_');
            txtCPF = new JFormattedTextField(maskCPF);
        } catch (ParseException e) {
            txtCPF = new JFormattedTextField();
            System.err.println("Erro crítico ao criar máscara de CPF: " + e.getMessage());
        }
        
        txtCPF.setPreferredSize(new Dimension(380, 55));
        txtCPF.setMaximumSize(new Dimension(380, 55));
        txtCPF.setFont(new Font("Arial", Font.BOLD, 22));
        txtCPF.setHorizontalAlignment(JTextField.CENTER);
        txtCPF.setBackground(new Color(52, 73, 94));
        txtCPF.setForeground(Color.GRAY);
        txtCPF.setCaretColor(Constantes.AMARELO);
        txtCPF.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constantes.AZUL_CLARO, 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        txtCPF.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                if (String.valueOf(txtCPF.getValue()).equals("null")) {
                    txtCPF.setForeground(Color.WHITE);
                }
            }

            @Override
            public void focusLost(FocusEvent evt) {
                String content = String.valueOf(txtCPF.getValue()).trim();
                if (content.equals("null") || content.isEmpty()) {
                    txtCPF.setValue(null);
                    txtCPF.setForeground(Color.GRAY);
                }
            }
        });
    }
}