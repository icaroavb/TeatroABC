package com.teatroabc.telas;

import com.teatroabc.componentes.*;
import com.teatroabc.constantes.Constantes;
import com.teatroabc.modelos.*;
import com.teatroabc.servicos.ClienteServico;
import com.teatroabc.servicos.interfaces.IClienteServico;
import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TelaCadastrar extends JPanel {
    private String cpfInformado;
    private Peca peca;
    private List<Assento> assentosSelecionados;
    private JFormattedTextField txtCPF;
    private JTextField txtNome;
    private JFormattedTextField txtDataNascimento;
    private JFormattedTextField txtTelefone;
    private IClienteServico clienteServico;

    public TelaCadastrar(String cpf) {
        this(cpf, null, null);
    }

    public TelaCadastrar(String cpf, Peca peca, List<Assento> assentosSelecionados) {
        this.cpfInformado = cpf;
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

        // Logo
        JPanel painelLogo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelLogo.setBackground(Constantes.AZUL_ESCURO);
        painelLogo.add(new LogoTeatro());
        containerPrincipal.add(Box.createVerticalStrut(30));
        containerPrincipal.add(painelLogo);

        // Título
        JLabel titulo = new JLabel("CADASTRAR");
        titulo.setFont(Constantes.FONTE_TITULO);
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        containerPrincipal.add(Box.createVerticalStrut(40));
        containerPrincipal.add(titulo);

        // Formulário
        JPanel formulario = criarFormulario();
        containerPrincipal.add(Box.createVerticalStrut(40));
        containerPrincipal.add(formulario);

        // Botão Cadastrar
        BotaoAnimado btnCadastrar = new BotaoAnimado("CADASTRAR",
                Constantes.LARANJA, Constantes.AMARELO, new Dimension(400, 70));
        btnCadastrar.setFont(new Font("Arial", Font.BOLD, 28));
        btnCadastrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCadastrar.addActionListener(e -> cadastrar());

        containerPrincipal.add(Box.createVerticalStrut(40));
        containerPrincipal.add(btnCadastrar);

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

    private JPanel criarFormulario() {
        JPanel formulario = new JPanel();
        formulario.setLayout(new GridBagLayout());
        formulario.setBackground(Constantes.AZUL_ESCURO);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        // CPF
        gbc.gridx = 0;
        gbc.gridy = 0;
        try {
            MaskFormatter maskCPF = new MaskFormatter("###.###.###-##");
            txtCPF = new JFormattedTextField(maskCPF);
            if (cpfInformado != null) {
                txtCPF.setText(cpfInformado);
                txtCPF.setEditable(false);
            }
        } catch (ParseException e) {
            txtCPF = new JFormattedTextField();
        }
        configurarCampo(txtCPF, "CPF");
        formulario.add(txtCPF, gbc);

        // Nome
        gbc.gridy = 1;
        txtNome = new JTextField();
        configurarCampo(txtNome, "NOME");
        formulario.add(txtNome, gbc);

        // Data de Nascimento
        gbc.gridy = 2;
        try {
            MaskFormatter maskData = new MaskFormatter("##/##/####");
            txtDataNascimento = new JFormattedTextField(maskData);
        } catch (ParseException e) {
            txtDataNascimento = new JFormattedTextField();
        }
        configurarCampo(txtDataNascimento, "DATA DE NASCIMENTO");
        formulario.add(txtDataNascimento, gbc);

        // Telefone
        gbc.gridy = 3;
        try {
            MaskFormatter maskTelefone = new MaskFormatter("(##) #####-####");
            txtTelefone = new JFormattedTextField(maskTelefone);
        } catch (ParseException e) {
            txtTelefone = new JFormattedTextField();
        }
        configurarCampo(txtTelefone, "TELEFONE");
        formulario.add(txtTelefone, gbc);

        return formulario;
    }

    private void configurarCampo(JTextField campo, String placeholder) {
        campo.setPreferredSize(new Dimension(400, 50));
        campo.setFont(new Font("Arial", Font.PLAIN, 20));
        campo.setHorizontalAlignment(JTextField.CENTER);
        campo.setBackground(new Color(52, 73, 94));
        campo.setForeground(Color.WHITE);
        campo.setCaretColor(Color.WHITE);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constantes.AZUL_CLARO, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Placeholder
        campo.setText(placeholder);
        campo.setForeground(Color.GRAY);

        campo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (campo.getText().equals(placeholder)) {
                    campo.setText("");
                    campo.setForeground(Color.WHITE);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (campo.getText().trim().isEmpty()) {
                    campo.setText(placeholder);
                    campo.setForeground(Color.GRAY);
                }
            }
        });
    }

    private void cadastrar() {
        // Validar campos
        if (!validarCampos()) {
            return;
        }

        try {
            // Criar cliente
            String cpf = txtCPF.getText().replaceAll("[^0-9]", "");
            String nome = txtNome.getText();
            String dataNascimento = txtDataNascimento.getText();
            String telefone = txtTelefone.getText();

            Cliente cliente = clienteServico.cadastrar(cpf, nome, dataNascimento, telefone);

            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

            if (peca != null && assentosSelecionados != null) {
                // Veio do fluxo de compra
                frame.setContentPane(new TelaConfirmarPedido(peca, cliente, assentosSelecionados));
            } else {
                // Cadastro direto
                JOptionPane.showMessageDialog(this,
                        "Cliente cadastrado com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                frame.setContentPane(new TelaPrincipal());
            }

            frame.revalidate();
            frame.repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao cadastrar cliente: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarCampos() {
        if (txtNome.getText().equals("NOME") || txtNome.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, informe o nome!", "Campo obrigatório", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (txtDataNascimento.getText().equals("DATA DE NASCIMENTO") || txtDataNascimento.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, informe a data de nascimento!", "Campo obrigatório", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validar formato da data
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate.parse(txtDataNascimento.getText(), formatter);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data de nascimento inválida!", "Data inválida", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (txtTelefone.getText().equals("TELEFONE") || txtTelefone.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, informe o telefone!", "Campo obrigatório", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private void voltar() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        if (peca != null) {
            frame.setContentPane(new TelaInformarCPF(false, peca, assentosSelecionados));
        } else {
            frame.setContentPane(new TelaPrincipal());
        }

        frame.revalidate();
        frame.repaint();
    }
}