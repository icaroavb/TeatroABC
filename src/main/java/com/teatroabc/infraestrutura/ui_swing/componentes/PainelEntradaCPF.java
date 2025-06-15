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

public class PainelEntradaCPF extends JPanel {

    private JFormattedTextField txtCPF;

    public PainelEntradaCPF(String tituloDaTela, ActionListener continuarAction) {
        configurarPainel();
        adicionarComponentes(tituloDaTela, continuarAction);
    }

    /**
     * Retorna o CPF digitado pelo usuário, já normalizado (apenas dígitos).
     * @return Uma string contendo apenas os dígitos do CPF, ou uma string vazia.
     */
    public String getCPF() {
        String textoFormatado = txtCPF.getText();
        if (apurarTxtFormatado(textoFormatado)) {
            return "";
        }
        // ValidadorCPF.normalizar já remove pontos, traços e underscores.
        return ValidadorCPF.normalizar(textoFormatado);
    }

    //Encapsulameno da lógica do string que receberá o cpf
    private boolean apurarTxtFormatado (String txt){
        return txt == null;
    }

    //Encapsular

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
        
        // Listener de foco para simular placeholder
        txtCPF.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                if (txtCPF.getText().contains("_")) {
                    // Move o cursor para o início para uma melhor experiência de usuário
                    SwingUtilities.invokeLater(() -> txtCPF.setCaretPosition(0));
                    txtCPF.setForeground(Color.WHITE);
                }
            }

            @Override
            public void focusLost(FocusEvent evt) {
                if (getCPF().isEmpty()) {
                    // Para redefinir o placeholder, é mais seguro limpar o valor e
                    // deixar o MaskFormatter fazer seu trabalho.
                    txtCPF.setValue(null);
                    txtCPF.setForeground(Color.GRAY);
                }
            }
        });
    }
}