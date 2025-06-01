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
        containerPrincipal.add(Box.createVerticalStrut(50));
        containerPrincipal.add(painelLogo);

        // Título
        JLabel titulo = new JLabel("CADASTRAR");
        titulo.setFont(Constantes.FONTE_TITULO);
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        containerPrincipal.add(Box.createVerticalStrut(50));
        containerPrincipal.add(titulo);

        // Formulário
        JPanel formulario = criarFormulario();
        containerPrincipal.add(Box.createVerticalStrut(60));
        containerPrincipal.add(formulario);

        // Botão Cadastrar
        BotaoAnimado btnCadastrar = new BotaoAnimado("CADASTRAR",
                Constantes.LARANJA, Constantes.AMARELO, new Dimension(400, 70));
        btnCadastrar.setFont(new Font("Arial", Font.BOLD, 28));
        btnCadastrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCadastrar.addActionListener(e -> cadastrar());

        containerPrincipal.add(Box.createVerticalStrut(50));
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
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.setBackground(Constantes.AZUL_ESCURO);
        formulario.setBorder(BorderFactory.createEmptyBorder(0, 100, 0, 100));

        // CPF
        formulario.add(criarCampoComRotulo("CPF", criarCampoCPF()));
        formulario.add(Box.createVerticalStrut(25));

        // Nome
        formulario.add(criarCampoComRotulo("NOME", criarCampoNome()));
        formulario.add(Box.createVerticalStrut(25));

        // Data de Nascimento
        formulario.add(criarCampoComRotulo("DATA DE NASCIMENTO", criarCampoDataNascimento()));
        formulario.add(Box.createVerticalStrut(25));

        // Telefone
        formulario.add(criarCampoComRotulo("TELEFONE", criarCampoTelefone()));

        return formulario;
    }

    private JPanel criarCampoComRotulo(String rotulo, JTextField campo) {
        JPanel painelCampo = new JPanel();
        painelCampo.setLayout(new BoxLayout(painelCampo, BoxLayout.Y_AXIS));
        painelCampo.setBackground(Constantes.AZUL_ESCURO);

        // Rótulo
        JLabel lblRotulo = new JLabel(rotulo);
        lblRotulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblRotulo.setForeground(Color.WHITE);
        lblRotulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelCampo.add(lblRotulo);

        painelCampo.add(Box.createVerticalStrut(8));

        // Campo
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelCampo.add(campo);

        return painelCampo;
    }

    private JFormattedTextField criarCampoCPF() {
        try {
            MaskFormatter maskCPF = new MaskFormatter("###.###.###-##");
            maskCPF.setPlaceholderCharacter('_');
            txtCPF = new JFormattedTextField(maskCPF);
            if (cpfInformado != null) {
                txtCPF.setText(cpfInformado);
                txtCPF.setEditable(false);
                txtCPF.setBackground(new Color(70, 80, 90));
            }
        } catch (ParseException e) {
            txtCPF = new JFormattedTextField();
        }
        configurarCampo(txtCPF);
        return txtCPF;
    }

    private JTextField criarCampoNome() {
        txtNome = new JTextField();
        configurarCampo(txtNome);
        return txtNome;
    }

    private JFormattedTextField criarCampoDataNascimento() {
        try {
            MaskFormatter maskData = new MaskFormatter("##/##/####");
            maskData.setPlaceholderCharacter('_');
            txtDataNascimento = new JFormattedTextField(maskData);
        } catch (ParseException e) {
            txtDataNascimento = new JFormattedTextField();
        }
        configurarCampo(txtDataNascimento);
        return txtDataNascimento;
    }

    private JFormattedTextField criarCampoTelefone() {
        try {
            MaskFormatter maskTelefone = new MaskFormatter("(##) #####-####");
            maskTelefone.setPlaceholderCharacter('_');
            txtTelefone = new JFormattedTextField(maskTelefone);
        } catch (ParseException e) {
            txtTelefone = new JFormattedTextField();
        }
        configurarCampo(txtTelefone);
        return txtTelefone;
    }

    private void configurarCampo(JTextField campo) {
        campo.setPreferredSize(new Dimension(500, 55));
        campo.setMaximumSize(new Dimension(500, 55));
        campo.setFont(new Font("Arial", Font.PLAIN, 18));
        campo.setBackground(new Color(52, 73, 94));
        campo.setForeground(Color.WHITE);
        campo.setCaretColor(Color.WHITE);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constantes.AZUL_CLARO, 2),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
    }

    private void cadastrar() {
        // Validar campos
        if (!validarCampos()) {
            return;
        }

        try {
            // Criar cliente
            String cpf = txtCPF.getText().replaceAll("[^0-9]", "");
            String nome = txtNome.getText().trim();
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
        if (txtNome.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, informe o nome!", "Campo obrigatório", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (txtDataNascimento.getText().trim().isEmpty() || txtDataNascimento.getText().contains("_")) {
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

        if (txtTelefone.getText().trim().isEmpty() || txtTelefone.getText().contains("_")) {
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