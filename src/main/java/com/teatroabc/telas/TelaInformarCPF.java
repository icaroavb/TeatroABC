package com.teatroabc.telas;

import com.teatroabc.componentes.*;
import com.teatroabc.constantes.Constantes;
import com.teatroabc.modelos.*;
import com.teatroabc.servicos.ClienteServico;
import com.teatroabc.servicos.interfaces.IClienteServico;
import com.teatroabc.utilitarios.ValidadorCPF;
import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.util.List;

public class TelaInformarCPF extends JPanel {
    private boolean modoConsulta;
    private Peca peca;
    private List<Assento> assentosSelecionados;
    private JFormattedTextField txtCPF;
    private IClienteServico clienteServico;

    public TelaInformarCPF(boolean modoConsulta) {
        this(modoConsulta, null, null);
    }

    public TelaInformarCPF(boolean modoConsulta, Peca peca, List<Assento> assentosSelecionados) {
        this.modoConsulta = modoConsulta;
        this.peca = peca;
        this.assentosSelecionados = assentosSelecionados;
        this.clienteServico = new ClienteServico();
        configurarTela();
    }

    private void configurarTela() {
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        // Container principal
        JPanel containerPrincipal = new JPanel();
        containerPrincipal.setLayout(new BoxLayout(containerPrincipal, BoxLayout.Y_AXIS));
        containerPrincipal.setBackground(Constantes.AZUL_ESCURO);

        // Logo centralizado
        JPanel painelLogo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelLogo.setBackground(Constantes.AZUL_ESCURO);
        painelLogo.add(new LogoTeatro());
        containerPrincipal.add(Box.createVerticalStrut(50));
        containerPrincipal.add(painelLogo);

        // Espaço
        containerPrincipal.add(Box.createVerticalStrut(80));

        // Título
        JLabel titulo = new JLabel("INFORME O CPF");
        titulo.setFont(Constantes.FONTE_TITULO);
        titulo.setForeground(Constantes.AMARELO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        containerPrincipal.add(titulo);

        // Espaço
        containerPrincipal.add(Box.createVerticalStrut(50));

        // Campo CPF
        JPanel painelCPF = new JPanel();
        painelCPF.setBackground(Constantes.AZUL_ESCURO);
        painelCPF.setLayout(new FlowLayout(FlowLayout.CENTER));

        try {
            MaskFormatter maskCPF = new MaskFormatter("###.###.###-##");
            maskCPF.setPlaceholderCharacter('_');
            txtCPF = new JFormattedTextField(maskCPF);
        } catch (ParseException e) {
            txtCPF = new JFormattedTextField();
        }

        txtCPF.setPreferredSize(new Dimension(400, 50));
        txtCPF.setFont(new Font("Arial", Font.PLAIN, 24));
        txtCPF.setHorizontalAlignment(JTextField.CENTER);
        txtCPF.setBackground(new Color(52, 73, 94));
        txtCPF.setForeground(Color.WHITE);
        txtCPF.setCaretColor(Color.WHITE);
        txtCPF.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constantes.AZUL_CLARO, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Placeholder
        txtCPF.setText("CPF");
        txtCPF.setForeground(Color.GRAY);

        txtCPF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtCPF.getText().equals("CPF")) {
                    txtCPF.setText("");
                    txtCPF.setForeground(Color.WHITE);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtCPF.getText().trim().equals("___.___.___-__") || txtCPF.getText().trim().isEmpty()) {
                    txtCPF.setText("CPF");
                    txtCPF.setForeground(Color.GRAY);
                }
            }
        });

        painelCPF.add(txtCPF);
        containerPrincipal.add(painelCPF);

        // Espaço
        containerPrincipal.add(Box.createVerticalStrut(50));

        // Botão Continuar
        BotaoAnimado btnContinuar = new BotaoAnimado("CONTINUAR",
                Constantes.LARANJA, Constantes.AMARELO, new Dimension(400, 70));
        btnContinuar.setFont(new Font("Arial", Font.BOLD, 28));
        btnContinuar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnContinuar.addActionListener(e -> continuar());

        containerPrincipal.add(btnContinuar);

        add(containerPrincipal, BorderLayout.CENTER);

        // Botão Voltar
        JPanel painelVoltar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelVoltar.setBackground(Constantes.AZUL_ESCURO);

        JButton btnVoltar = new JButton("VOLTAR");
        btnVoltar.setFont(new Font("Arial", Font.PLAIN, 18));
        btnVoltar.setForeground(Constantes.AZUL_CLARO);
        btnVoltar.setBackground(Constantes.AZUL_ESCURO);
        btnVoltar.setBorderPainted(false);
        btnVoltar.setFocusPainted(false);
        btnVoltar.setContentAreaFilled(false);
        btnVoltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltar.addActionListener(e -> voltar());

        painelVoltar.add(btnVoltar);
        add(painelVoltar, BorderLayout.SOUTH);
    }

    private void continuar() {
        String cpf = txtCPF.getText().replaceAll("[^0-9]", "");

        if (cpf.isEmpty() || cpf.length() != 11) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, informe um CPF válido!",
                    "CPF Inválido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!ValidadorCPF.isValid(cpf)) {
            JOptionPane.showMessageDialog(this,
                    "CPF inválido! Por favor, verifique os dados.",
                    "CPF Inválido",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        if (modoConsulta) {
            // Modo consulta - ir para lista de bilhetes
            frame.setContentPane(new TelaListaBilhetes(cpf));
        } else {
            // Modo compra - verificar se cliente existe
            if (clienteServico.existe(cpf)) {
                // Cliente existe - ir para confirmação
                Cliente cliente = clienteServico.buscarPorCpf(cpf).orElse(null);
                frame.setContentPane(new TelaConfirmarPedido(peca, cliente, assentosSelecionados));
            } else {
                // Cliente não existe - ir para cadastro
                frame.setContentPane(new TelaCadastrar(cpf, peca, assentosSelecionados));
            }
        }

        frame.revalidate();
        frame.repaint();
    }

    private void voltar() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        if (modoConsulta) {
            frame.setContentPane(new TelaPrincipal());
        } else {
            frame.setContentPane(new TelaSelecionarAssento(peca));
        }

        frame.revalidate();
        frame.repaint();
    }
}