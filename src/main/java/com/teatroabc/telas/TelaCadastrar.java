package com.teatroabc.telas;

import com.teatroabc.componentes.*;
import com.teatroabc.constantes.Constantes;
import com.teatroabc.modelos.*;
import com.teatroabc.enums.Turno;
import com.teatroabc.servicos.ClienteServico;
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
    private Turno turnoSelecionado;
    private JFormattedTextField txtCPF;
    private JTextField txtNome;
    private JFormattedTextField txtDataNascimento;
    private JFormattedTextField txtTelefone;
    private JTextField txtEmail;
    private JCheckBox chkMembroABC;
    private JPanel painelTelefone;
    private JPanel painelEmail;
    private ClienteServico clienteServico;

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
    
    public void setTurnoSelecionado(Turno turno) {
        this.turnoSelecionado = turno;
        System.out.println("Turno selecionado definido no cadastro: " + turno);
    }

    private void configurarTela() {
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel containerPrincipal = new JPanel();
        containerPrincipal.setLayout(new BoxLayout(containerPrincipal, BoxLayout.Y_AXIS));
        containerPrincipal.setBackground(Constantes.AZUL_ESCURO);

        JPanel painelLogo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelLogo.setBackground(Constantes.AZUL_ESCURO);
        painelLogo.add(new LogoTeatro());
        containerPrincipal.add(Box.createVerticalStrut(30));
        containerPrincipal.add(painelLogo);

        JLabel titulo = new JLabel("CADASTRAR");
        titulo.setFont(Constantes.FONTE_TITULO);
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        containerPrincipal.add(Box.createVerticalStrut(40));
        containerPrincipal.add(titulo);

        JPanel formulario = criarFormulario();
        containerPrincipal.add(Box.createVerticalStrut(40));
        containerPrincipal.add(formulario);

        JPanel painelCheckbox = criarCheckboxABCGold();
        containerPrincipal.add(Box.createVerticalStrut(30));
        containerPrincipal.add(painelCheckbox);

        BotaoAnimado btnCadastrar = new BotaoAnimado("CADASTRAR",
                Constantes.LARANJA, Constantes.AMARELO, new Dimension(400, 70));
        btnCadastrar.setFont(new Font("Arial", Font.BOLD, 28));
        btnCadastrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCadastrar.addActionListener(e -> cadastrar());

        containerPrincipal.add(Box.createVerticalStrut(40));
        containerPrincipal.add(btnCadastrar);
        containerPrincipal.add(Box.createVerticalStrut(30));

        scrollPane.setViewportView(containerPrincipal);
        add(scrollPane, BorderLayout.CENTER);

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
        formulario.setMaximumSize(new Dimension(500, 600));
        formulario.setAlignmentX(Component.CENTER_ALIGNMENT);

        formulario.add(criarCampoComRotulo("CPF", criarCampoCPF()));
        formulario.add(Box.createVerticalStrut(25));

        formulario.add(criarCampoComRotulo("NOME", criarCampoNome()));
        formulario.add(Box.createVerticalStrut(25));

        formulario.add(criarCampoComRotulo("DATA DE NASCIMENTO", criarCampoDataNascimento()));

        painelTelefone = criarCampoComRotulo("TELEFONE", criarCampoTelefone());
        painelTelefone.setVisible(false);
        formulario.add(Box.createVerticalStrut(25));
        formulario.add(painelTelefone);

        painelEmail = criarCampoComRotulo("E-MAIL", criarCampoEmail());
        painelEmail.setVisible(false);
        formulario.add(Box.createVerticalStrut(25));
        formulario.add(painelEmail);

        return formulario;
    }

    private JPanel criarCampoComRotulo(String rotulo, JTextField campo) {
        JPanel painelCampo = new JPanel();
        painelCampo.setLayout(new BoxLayout(painelCampo, BoxLayout.Y_AXIS));
        painelCampo.setBackground(Constantes.AZUL_ESCURO);
        painelCampo.setMaximumSize(new Dimension(500, 85));
        painelCampo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblRotulo = new JLabel(rotulo);
        lblRotulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblRotulo.setForeground(Color.WHITE);
        lblRotulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelCampo.add(lblRotulo);

        painelCampo.add(Box.createVerticalStrut(8));

        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelCampo.add(campo);

        return painelCampo;
    }

    private JPanel criarCheckboxABCGold() {
        JPanel painelCheckbox = new JPanel();
        painelCheckbox.setLayout(new FlowLayout(FlowLayout.CENTER));
        painelCheckbox.setBackground(Constantes.AZUL_ESCURO);
        painelCheckbox.setMaximumSize(new Dimension(500, 60));

        JPanel containerCheckbox = new JPanel();
        containerCheckbox.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        containerCheckbox.setBackground(new Color(255, 193, 7, 20));
        containerCheckbox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Constantes.AMARELO, 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        chkMembroABC = new JCheckBox("Ser membro ABC GOLD");
        chkMembroABC.setFont(new Font("Arial", Font.BOLD, 18));
        chkMembroABC.setForeground(Constantes.AMARELO);
        chkMembroABC.setBackground(new Color(0, 0, 0, 0));
        chkMembroABC.setFocusPainted(false);
        chkMembroABC.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        chkMembroABC.setIcon(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRect(x, y, 20, 20);
                g2d.setColor(Constantes.AMARELO);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRect(x, y, 20, 20);
                g2d.dispose();
            }
            @Override
            public int getIconWidth() { return 20; }
            @Override
            public int getIconHeight() { return 20; }
        });
        
        chkMembroABC.setSelectedIcon(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Constantes.AMARELO);
                g2d.fillRect(x, y, 20, 20);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawLine(x + 4, y + 10, x + 8, y + 14);
                g2d.drawLine(x + 8, y + 14, x + 16, y + 6);
                g2d.dispose();
            }
            @Override
            public int getIconWidth() { return 20; }
            @Override
            public int getIconHeight() { return 20; }
        });

        chkMembroABC.addActionListener(e -> {
            boolean selecionado = chkMembroABC.isSelected();
            painelTelefone.setVisible(selecionado);
            painelEmail.setVisible(selecionado);
            revalidate();
            repaint();
        });

        JLabel lblEstrela = new JLabel("⭐");
        lblEstrela.setFont(new Font("Arial", Font.PLAIN, 24));

        containerCheckbox.add(chkMembroABC);
        
        painelCheckbox.add(containerCheckbox);
        return painelCheckbox;
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

    private JTextField criarCampoEmail() {
        txtEmail = new JTextField();
        configurarCampo(txtEmail);
        return txtEmail;
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
        if (!validarCampos()) {
            return;
        }

        try {
            String cpf = txtCPF.getText().replaceAll("[^0-9]", "");
            
            if (clienteServico.existe(cpf)) {
                JOptionPane.showMessageDialog(this,
                        "Cliente com este CPF já está cadastrado!",
                        "CPF Já Cadastrado",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String nome = txtNome.getText().trim();
            String dataNascimento = txtDataNascimento.getText();
            String telefone = chkMembroABC.isSelected() ? txtTelefone.getText() : "";
            String email = chkMembroABC.isSelected() ? txtEmail.getText().trim() : "";
            boolean isMembroABC = chkMembroABC.isSelected();

            Cliente cliente = clienteServico.cadastrar(cpf, nome, dataNascimento, telefone, email, isMembroABC);

            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

            if (peca != null && assentosSelecionados != null) {
                TelaConfirmarPedido telaConfirmar = new TelaConfirmarPedido(peca, cliente, assentosSelecionados, turnoSelecionado);
                frame.setContentPane(telaConfirmar);
            } else {
                String mensagem = isMembroABC ? 
                    "Cliente cadastrado como membro ABC GOLD com sucesso!\nVocê terá 5% de desconto em todas as compras!" :
                    "Cliente cadastrado com sucesso!";
                JOptionPane.showMessageDialog(this, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
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

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate.parse(txtDataNascimento.getText(), formatter);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data de nascimento inválida!", "Data inválida", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (chkMembroABC.isSelected()) {
            if (txtTelefone.getText().trim().isEmpty() || txtTelefone.getText().contains("_")) {
                JOptionPane.showMessageDialog(this, "Por favor, informe o telefone para ser membro ABC GOLD!", "Campo obrigatório", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            if (txtEmail.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, informe o e-mail para ser membro ABC GOLD!", "Campo obrigatório", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            if (!txtEmail.getText().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                JOptionPane.showMessageDialog(this, "E-mail inválido!", "E-mail inválido", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        return true;
    }

    private void voltar() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        if (peca != null) {
            TelaInformarCPF telaInformarCPF = new TelaInformarCPF(false, peca, assentosSelecionados);
            telaInformarCPF.setTurnoSelecionado(turnoSelecionado);
            frame.setContentPane(telaInformarCPF);
        } else {
            frame.setContentPane(new TelaPrincipal());
        }

        frame.revalidate();
        frame.repaint();
    }
}