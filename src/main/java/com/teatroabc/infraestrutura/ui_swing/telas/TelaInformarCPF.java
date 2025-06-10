package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAnimado;
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.modelos.Cliente; // Para buscar o cliente
import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.aplicacao.interfaces.IClienteServico; // Interface do Serviço
import com.teatroabc.aplicacao.interfaces.IReservaServico; // Interface do Serviço
import com.teatroabc.dominio.validadores.ValidadorCPF; // Se for usar para validação de formato

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.util.List;
import java.util.Optional; // Para o resultado de buscarPorCpf

public class TelaInformarCPF extends JPanel {
    private final boolean modoConsulta;
    private final Peca peca; // Pode ser null se modoConsulta for true
    private final List<Assento> assentosSelecionados; // Pode ser null se modoConsulta for true
    private final Turno turnoSelecionado; // Pode ser null se modoConsulta for true

    private JFormattedTextField txtCPF;

    // Serviços injetados
    private final IClienteServico clienteServico;
    private final IReservaServico reservaServico; // Necessário se esta tela levar a TelaListaBilhetes
                                              // ou se precisar passar para TelaConfirmarPedido (embora TelaConfirmarPedido
                                              // já receba do seu chamador, que seria TelaCadastrar ou esta tela).

    /**
     * Construtor para TelaInformarCPF.
     *
     * @param modoConsulta True se a tela for para consultar bilhetes, false se for para fluxo de compra.
     * @param peca A peça selecionada (relevante no modo compra, pode ser null em modo consulta).
     * @param assentosSelecionados Lista de assentos selecionados (relevante no modo compra, pode ser null em modo consulta).
     * @param clienteServico Serviço para operações de cliente.
     * @param reservaServico Serviço para operações de reserva/bilhete (usado para consulta ou para passar adiante).
     */
    public TelaInformarCPF(boolean modoConsulta, Peca peca, List<Assento> assentosSelecionados,
                           IClienteServico clienteServico, IReservaServico reservaServico) {
        if (clienteServico == null || reservaServico == null) {
            throw new IllegalArgumentException("Serviços não podem ser nulos em TelaInformarCPF.");
        }
        this.modoConsulta = modoConsulta;
        this.peca = peca;
        this.assentosSelecionados = assentosSelecionados;
        this.turnoSelecionado = null; // Será definido por setTurnoSelecionado se aplicável
        this.clienteServico = clienteServico;
        this.reservaServico = reservaServico;
        // Removida instanciação: this.clienteServico = new ClienteServico();
        configurarTelaVisual();
    }
    
    /**
     * Define o turno selecionado, relevante para o fluxo de compra.
     * @param turno O turno selecionado.
     */
    public void setTurnoSelecionado(Turno turno) {
        // Este método é chamado por quem cria esta tela (TelaSelecionarAssento)
        // apenas se estiver no fluxo de compra (modoConsulta = false).
        if (!this.modoConsulta && turno == null) {
            System.err.println("Aviso: Turno nulo definido para TelaInformarCPF em modo de compra.");
        }
        this.turnoSelecionado = turno;
        // System.out.println("TelaInformarCPF: Turno selecionado definido: " + (turno != null ? turno.getNome() : "null"));
    }

    private void configurarTelaVisual() { // Renomeado de configurarTela
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        JPanel containerPrincipal = new JPanel();
        containerPrincipal.setLayout(new BoxLayout(containerPrincipal, BoxLayout.Y_AXIS));
        containerPrincipal.setBackground(Constantes.AZUL_ESCURO);

        // ... (código do painelLogo, título, painelCPF como antes) ...
        JPanel painelLogo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelLogo.setBackground(Constantes.AZUL_ESCURO);
        painelLogo.add(new LogoTeatro());
        containerPrincipal.add(Box.createVerticalStrut(50));
        containerPrincipal.add(painelLogo);
        containerPrincipal.add(Box.createVerticalStrut(80));
        JLabel titulo = new JLabel("INFORME O CPF");
        titulo.setFont(Constantes.FONTE_TITULO);
        titulo.setForeground(Constantes.AMARELO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        containerPrincipal.add(titulo);
        containerPrincipal.add(Box.createVerticalStrut(50));
        JPanel painelCPF = new JPanel();
        painelCPF.setBackground(Constantes.AZUL_ESCURO);
        painelCPF.setLayout(new FlowLayout(FlowLayout.CENTER));
        try {
            MaskFormatter maskCPF = new MaskFormatter("###.###.###-##");
            maskCPF.setPlaceholderCharacter('_');
            txtCPF = new JFormattedTextField(maskCPF);
        } catch (ParseException e) {
            txtCPF = new JFormattedTextField(); // Fallback
            System.err.println("Erro ao criar máscara de CPF: " + e.getMessage());
        }
        // ... (configurações do txtCPF e FocusListener como antes) ...
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
        txtCPF.setText("___.___.___-__"); // Placeholder inicial
        txtCPF.setForeground(Color.GRAY);
        txtCPF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtCPF.getText().replace("_", "").replace(".", "").replace("-", "").isEmpty()) {
                    txtCPF.setText("");
                    txtCPF.setForeground(Color.WHITE);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtCPF.getText().replace("_", "").replace(".", "").replace("-", "").trim().isEmpty()) {
                    txtCPF.setText("___.___.___-__");
                    txtCPF.setForeground(Color.GRAY);
                }
            }
        });
        painelCPF.add(txtCPF);
        containerPrincipal.add(painelCPF);
        containerPrincipal.add(Box.createVerticalStrut(50));


        BotaoAnimado btnContinuar = new BotaoAnimado("CONTINUAR",
                Constantes.LARANJA, Constantes.AMARELO, new Dimension(400, 70));
        btnContinuar.setFont(new Font("Arial", Font.BOLD, 28));
        btnContinuar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnContinuar.addActionListener(e -> continuar());
        containerPrincipal.add(btnContinuar);

        add(containerPrincipal, BorderLayout.CENTER);

        JPanel painelVoltar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelVoltar.setBackground(Constantes.AZUL_ESCURO);
        JButton btnVoltar = new JButton("VOLTAR");
        // ... (configurações do btnVoltar como antes) ...
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
        String cpfInput = txtCPF.getText();
        String cpfNormalizado = ValidadorCPF.normalizar(cpfInput); // Usar o normalizador

        if (cpfNormalizado == null || cpfNormalizado.length() != 11) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, informe um CPF com 11 dígitos!",
                    "CPF Inválido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!ValidadorCPF.isValid(cpfNormalizado)) { // Validar o CPF normalizado
            JOptionPane.showMessageDialog(this,
                    "CPF inválido! Por favor, verifique os dados.",
                    "CPF Inválido",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        if (modoConsulta) {
            // Para listar bilhetes, passamos o CPF e o reservaServico
            frame.setContentPane(new TelaListaBilhetes(cpfNormalizado, this.reservaServico));
        } else {
            // Modo compra
            Optional<Cliente> clienteOpt = this.clienteServico.buscarPorCpf(cpfNormalizado);
            if (clienteOpt.isPresent()) {
                Cliente clienteExistente = clienteOpt.get();
                // Cliente existe, ir para confirmação de pedido
                // TelaConfirmarPedido precisa de Peca, Cliente, List<Assento>, Turno e IReservaServico
                TelaConfirmarPedido telaConfirmar = new TelaConfirmarPedido(
                    this.peca,
                    clienteExistente,
                    this.assentosSelecionados,
                    this.turnoSelecionado, // Passa o turno que foi setado
                    this.reservaServico   // Passa o serviço de reserva
                );
                frame.setContentPane(telaConfirmar);
            } else {
                // Cliente não existe, ir para cadastro
                // TelaCadastrar precisa do CPF informado, dados da compra (Peca, Assentos, Turno)
                // e dos serviços para operar e para passar adiante.
                TelaCadastrar telaCadastrar = new TelaCadastrar(
                    cpfNormalizado, // Passa o CPF normalizado e validado
                    this.peca,
                    this.assentosSelecionados,
                    this.clienteServico,
                    this.pecaServico,   // Necessário se TelaCadastrar precisar listar peças (improvável) ou passar adiante
                    this.reservaServico
                );
                telaCadastrar.setTurnoSelecionado(this.turnoSelecionado); // Informa o turno para o fluxo de cadastro
                frame.setContentPane(telaCadastrar);
            }
        }

        frame.revalidate();
        frame.repaint();
    }

    private void voltar() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (modoConsulta) {
            // Ao voltar da consulta de CPF, vai para TelaPrincipal
            // TelaPrincipal agora espera os serviços no construtor
            frame.setContentPane(new TelaPrincipal(this.clienteServico, this.pecaServico, this.reservaServico));
        } else {
            // Ao voltar do fluxo de compra (informar CPF), volta para TelaSelecionarAssento
            // TelaSelecionarAssento espera Peca e os serviços
            frame.setContentPane(new TelaSelecionarAssento(this.peca, this.pecaServico, this.clienteServico, this.reservaServico));
        }
        frame.revalidate();
        frame.repaint();
    }
}