package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAnimado;
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.aplicacao.dto.DadosCadastroClienteDTO;
import com.teatroabc.aplicacao.excecoes.ClienteJaCadastradoException;
import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Tela responsável por coletar os dados de um novo cliente para cadastro no sistema.
 * Esta tela é acionada tanto pelo fluxo de compra (quando um CPF não é encontrado)
 * quanto pela opção de cadastro direto na tela principal.
 * 
 * Na Arquitetura Hexagonal, atua como um Adaptador Primário, coletando a entrada do
 * usuário, montando um DTO (Data Transfer Object) e interagindo com o IClienteServico.
 */
public class TelaCadastrar extends JPanel {
    private final String cpfInformadoOriginalmente;
    private final Peca peca;
    private final List<Assento> assentosSelecionados;
    private Turno turnoSelecionado;

    private JFormattedTextField txtCPF;
    private JTextField txtNome;
    private JFormattedTextField txtDataNascimento;
    private JFormattedTextField txtTelefone;
    private JTextField txtEmail;
    private JCheckBox chkMembroABC;
    private JPanel painelTelefone;
    private JPanel painelEmail;

    private final IClienteServico clienteServico;
    private final IPecaServico pecaServico;
    private final IReservaServico reservaServico;

    public TelaCadastrar(String cpf, Peca peca, List<Assento> assentosSelecionados,
                         IClienteServico clienteServico, IPecaServico pecaServico, IReservaServico reservaServico) {
        if (clienteServico == null || pecaServico == null || reservaServico == null) {
            throw new IllegalArgumentException("Serviços não podem ser nulos em TelaCadastrar.");
        }
        this.cpfInformadoOriginalmente = cpf;
        this.peca = peca;
        this.assentosSelecionados = assentosSelecionados;
        this.clienteServico = clienteServico;
        this.pecaServico = pecaServico;
        this.reservaServico = reservaServico;
        configurarTelaVisual();
    }
    
    public void setTurnoSelecionado(Turno turno) {
        this.turnoSelecionado = turno;
    }

    private void configurarTelaVisual() {
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel containerPrincipal = new JPanel();
        containerPrincipal.setLayout(new BoxLayout(containerPrincipal, BoxLayout.Y_AXIS));
        containerPrincipal.setBackground(Constantes.AZUL_ESCURO);
        containerPrincipal.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

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

        BotaoAnimado btnCadastrarUI = new BotaoAnimado("CADASTRAR", 
                Constantes.LARANJA, Constantes.AMARELO, new Dimension(400, 70));
        btnCadastrarUI.setFont(new Font("Arial", Font.BOLD, 28));
        btnCadastrarUI.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCadastrarUI.addActionListener(e -> realizarCadastro()); 

        containerPrincipal.add(Box.createVerticalStrut(40));
        containerPrincipal.add(btnCadastrarUI);
        containerPrincipal.add(Box.createVerticalStrut(30));

        scrollPane.setViewportView(containerPrincipal);
        add(scrollPane, BorderLayout.CENTER);

        JPanel painelVoltar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelVoltar.setBackground(Constantes.AZUL_ESCURO);
        JButton btnVoltar = new JButton("<< Voltar");
        btnVoltar.setFont(new Font("Arial", Font.PLAIN, 18));
        btnVoltar.setForeground(Constantes.AZUL_CLARO);
        btnVoltar.setContentAreaFilled(false);
        btnVoltar.setBorderPainted(false);
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
    
        formulario.add(criarCampoComRotulo("NOME COMPLETO", criarCampoNome()));
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
    
    // --- IMPLEMENTAÇÃO COMPLETA DOS MÉTODOS DE CRIAÇÃO ---

    private JFormattedTextField criarCampoCPF() {
        try {
            MaskFormatter maskCPF = new MaskFormatter("###.###.###-##");
            maskCPF.setPlaceholderCharacter('_');
            txtCPF = new JFormattedTextField(maskCPF);
            if (this.cpfInformadoOriginalmente != null && !this.cpfInformadoOriginalmente.isBlank()) {
                txtCPF.setValue(this.cpfInformadoOriginalmente);
                txtCPF.setEditable(false);
                txtCPF.setBackground(new Color(70, 80, 90));
            }
        } catch (ParseException e) {
            txtCPF = new JFormattedTextField();
            System.err.println("Erro ao criar máscara de CPF: " + e.getMessage());
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
            System.err.println("Erro ao criar máscara de Data de Nascimento: " + e.getMessage());
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
            System.err.println("Erro ao criar máscara de Telefone: " + e.getMessage());
        }
        configurarCampo(txtTelefone); 
        return txtTelefone;
    }

    private JTextField criarCampoEmail() {
        txtEmail = new JTextField();
        configurarCampo(txtEmail);
        return txtEmail;
    }
    
    private JPanel criarCampoComRotulo(String rotuloTexto, JTextField campo) { 
        JPanel painelCampo = new JPanel();
        painelCampo.setLayout(new BoxLayout(painelCampo, BoxLayout.Y_AXIS));
        painelCampo.setBackground(Constantes.AZUL_ESCURO);
        painelCampo.setMaximumSize(new Dimension(500, 85));
        painelCampo.setAlignmentX(Component.LEFT_ALIGNMENT);
    
        JLabel lblRotulo = new JLabel(rotuloTexto);
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
        JPanel painelCheckbox = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelCheckbox.setBackground(Constantes.AZUL_ESCURO);
        painelCheckbox.setMaximumSize(new Dimension(500, 70));
    
        JPanel containerCheckbox = new JPanel();
        containerCheckbox.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        containerCheckbox.setBackground(new Color(255, 193, 7, 30));
        containerCheckbox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Constantes.AMARELO, 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
    
        chkMembroABC = new JCheckBox("Desejo ser membro ABC GOLD");
        chkMembroABC.setFont(new Font("Arial", Font.BOLD, 16));
        chkMembroABC.setForeground(Constantes.AMARELO);
        chkMembroABC.setOpaque(false);
        chkMembroABC.setFocusPainted(false);
        chkMembroABC.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chkMembroABC.setIconTextGap(10);
        
        chkMembroABC.addActionListener(e -> {
            boolean selecionado = chkMembroABC.isSelected();
            painelTelefone.setVisible(selecionado);
            painelEmail.setVisible(selecionado);
            SwingUtilities.getWindowAncestor(this).revalidate();
            SwingUtilities.getWindowAncestor(this).repaint();
        });
        
        containerCheckbox.add(chkMembroABC);
        
        painelCheckbox.add(containerCheckbox);
        return painelCheckbox;
    }

    private boolean validarCamposEntrada() {
        if (txtNome.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O campo Nome é obrigatório.", "Campo Inválido", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        String dataNascStr = txtDataNascimento.getText().replaceAll("[_/]", "").trim();
        if (dataNascStr.isEmpty() || dataNascStr.length() != 8) {
            JOptionPane.showMessageDialog(this, "O campo Data de Nascimento é obrigatório e deve ser preenchido completamente.", "Campo Inválido", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            LocalDate.parse(txtDataNascimento.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "O formato da Data de Nascimento é inválido. Use dd/MM/yyyy.", "Formato Inválido", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (chkMembroABC.isSelected()) {
            if (txtTelefone.getText().replaceAll("[()_ -]", "").trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Telefone é obrigatório para Membros GOLD.", "Campo Obrigatório", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (txtEmail.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Email é obrigatório para Membros GOLD.", "Campo Obrigatório", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (!txtEmail.getText().trim().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                JOptionPane.showMessageDialog(this, "O formato do E-mail é inválido.", "Formato Inválido", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        return true;
    }
    
    private void realizarCadastro() {
        if (!validarCamposEntrada()) {
            return;
        }

        try {
            String cpfFinal = (this.cpfInformadoOriginalmente != null && !this.cpfInformadoOriginalmente.isBlank()) ?
                               this.cpfInformadoOriginalmente :
                               txtCPF.getText().replaceAll("[^0-9]","");

            String identificadorPlano = chkMembroABC.isSelected() ?
                                        com.teatroabc.dominio.fidelidade.MembroABCGold.IDENTIFICADOR :
                                        com.teatroabc.dominio.fidelidade.SemFidelidade.IDENTIFICADOR;

            DadosCadastroClienteDTO dto = new DadosCadastroClienteDTO(
                cpfFinal,
                txtNome.getText().trim(),
                txtDataNascimento.getText(),
                chkMembroABC.isSelected() ? txtTelefone.getText().replaceAll("[^0-9]", "") : "",
                chkMembroABC.isSelected() ? txtEmail.getText().trim() : "",
                identificadorPlano
            );

            Cliente clienteCadastrado = this.clienteServico.cadastrar(dto);

            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

            if (this.peca != null && this.assentosSelecionados != null && this.turnoSelecionado != null) {
                TelaConfirmarPedido telaConfirmar = new TelaConfirmarPedido(
                    this.peca, clienteCadastrado, this.assentosSelecionados, this.turnoSelecionado,
                    this.clienteServico, this.pecaServico, this.reservaServico
                );
                frame.setContentPane(telaConfirmar);
            } else {
                String mensagem = chkMembroABC.isSelected() ? 
                    "Cliente cadastrado como membro ABC GOLD!\n" + clienteCadastrado.getDescricaoBeneficiosPlano() :
                    "Cliente cadastrado com sucesso!";
                JOptionPane.showMessageDialog(this, mensagem, "Cadastro Realizado", JOptionPane.INFORMATION_MESSAGE);
                frame.setContentPane(new TelaPrincipal(this.clienteServico, this.pecaServico, this.reservaServico));
            }

            frame.revalidate();
            frame.repaint();

        } catch (ClienteJaCadastradoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro de Cadastro", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Erro nos dados informados: " + e.getMessage(), "Dados Inválidos", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado: " + e.getMessage(), "Erro Crítico", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void voltar() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        if (this.peca != null) {
            TelaInformarCPF telaInformarCPF = new TelaInformarCPF(
                false, this.peca, this.assentosSelecionados,
                this.clienteServico, this.pecaServico, this.reservaServico
            );
            telaInformarCPF.setTurnoSelecionado(this.turnoSelecionado);
            frame.setContentPane(telaInformarCPF);
        } else {
            frame.setContentPane(new TelaPrincipal(this.clienteServico, this.pecaServico, this.reservaServico));
        }

        frame.revalidate();
        frame.repaint();
    }
}