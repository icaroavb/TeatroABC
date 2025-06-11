package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAnimado;
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico;
import com.teatroabc.dominio.validadores.ValidadorCPF;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

/**
 * Tela para o usuário informar o CPF.
 * Esta tela é multifuncional e atua como um roteador de fluxos:
 * 1. No modo de compra, ela identifica o cliente. Se o cliente não existe, direciona para o cadastro.
 * 2. No modo de consulta, ela inicia a busca de bilhetes para o CPF informado.
 * 
 * Na Arquitetura Hexagonal, é um Adaptador Primário que interage com o IClienteServico
 * para tomar decisões de navegação.
 */
public class TelaInformarCPF extends JPanel {
    // Contexto de navegação
    private final boolean modoConsulta;
    private final Peca peca;
    private final List<Assento> assentosSelecionados;
    private Turno turnoSelecionado;

    // Componente da UI
    private JFormattedTextField txtCPF;

    // Serviços injetados via construtor
    private final IClienteServico clienteServico;
    private final IPecaServico pecaServico;
    private final IReservaServico reservaServico;

    /**
     * Construtor da TelaInformarCPF.
     *
     * @param modoConsulta {@code true} se a tela for para consulta, {@code false} se for para compra.
     * @param peca A peça selecionada (relevante no modo compra).
     * @param assentosSelecionados Lista de assentos selecionados (relevante no modo compra).
     * @param clienteServico Serviço para operações de cliente.
     * @param pecaServico Serviço para operações de peça.
     * @param reservaServico Serviço para operações de reserva.
     * @throws IllegalArgumentException se algum dos serviços for nulo.
     */
    public TelaInformarCPF(boolean modoConsulta, Peca peca, List<Assento> assentosSelecionados,
                           IClienteServico clienteServico, IPecaServico pecaServico, IReservaServico reservaServico) {
        if (clienteServico == null || pecaServico == null || reservaServico == null) {
            throw new IllegalArgumentException("Serviços não podem ser nulos em TelaInformarCPF.");
        }
        
        this.modoConsulta = modoConsulta;
        this.peca = peca;
        this.assentosSelecionados = assentosSelecionados;
        this.clienteServico = clienteServico;
        this.pecaServico = pecaServico;
        this.reservaServico = reservaServico;
        
        configurarTelaVisual();
    }
    
    /**
     * Define o turno selecionado, necessário quando esta tela é parte do fluxo de compra.
     * @param turno O turno da apresentação selecionado.
     */
    public void setTurnoSelecionado(Turno turno) {
        this.turnoSelecionado = turno;
    }

    /**
     * Configura os componentes visuais e o layout da tela.
     */
    private void configurarTelaVisual() {
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        JPanel containerPrincipal = new JPanel();
        containerPrincipal.setLayout(new BoxLayout(containerPrincipal, BoxLayout.Y_AXIS));
        containerPrincipal.setBackground(Constantes.AZUL_ESCURO);
        containerPrincipal.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JPanel painelLogo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelLogo.setOpaque(false);
        painelLogo.add(new LogoTeatro());
        containerPrincipal.add(painelLogo);
        containerPrincipal.add(Box.createRigidArea(new Dimension(0, 60)));

        JLabel titulo = new JLabel(modoConsulta ? "CONSULTAR BILHETES" : "IDENTIFIQUE-SE COM CPF");
        titulo.setFont(Constantes.FONTE_TITULO.deriveFont(48f));
        titulo.setForeground(Constantes.AMARELO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        containerPrincipal.add(titulo);
        containerPrincipal.add(Box.createRigidArea(new Dimension(0, 40)));

        // Painel para o campo de CPF
        JPanel painelCPF = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelCPF.setOpaque(false);
        configurarCampoCPF();
        painelCPF.add(txtCPF);
        containerPrincipal.add(painelCPF);
        containerPrincipal.add(Box.createRigidArea(new Dimension(0, 40)));

        // Botão de ação principal
        BotaoAnimado btnContinuar = new BotaoAnimado("CONTINUAR",
                Constantes.LARANJA, Constantes.AMARELO.darker(), new Dimension(380, 65));
        btnContinuar.setFont(Constantes.FONTE_BOTAO);
        btnContinuar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnContinuar.addActionListener(e -> processarContinuar());
        containerPrincipal.add(btnContinuar);

        add(containerPrincipal, BorderLayout.CENTER);

        // Botão de voltar no rodapé
        JPanel painelVoltar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelVoltar.setOpaque(false);
        painelVoltar.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 0));
        JButton btnVoltarUI = new JButton("<< Voltar");
        btnVoltarUI.setFont(new Font("Arial", Font.PLAIN, 16));
        btnVoltarUI.setForeground(Constantes.AZUL_CLARO);
        btnVoltarUI.setContentAreaFilled(false);
        btnVoltarUI.setBorderPainted(false);
        btnVoltarUI.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltarUI.addActionListener(e -> navegarParaTelaAnterior());
        painelVoltar.add(btnVoltarUI);
        add(painelVoltar, BorderLayout.SOUTH);
    }
    
    /**
     * Configura o campo de texto formatado para CPF, incluindo máscara e comportamento de placeholder.
     */
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
        txtCPF.setFont(new Font("Arial", Font.BOLD, 22));
        txtCPF.setHorizontalAlignment(JTextField.CENTER);
        txtCPF.setBackground(new Color(52, 73, 94));
        txtCPF.setForeground(Color.GRAY);
        txtCPF.setCaretColor(Constantes.AMARELO);
        txtCPF.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constantes.AZUL_CLARO, 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        // Comportamento de foco para simular um placeholder
        txtCPF.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                // Ao ganhar foco, se o texto for o placeholder, limpa para o usuário digitar.
                if (txtCPF.getText().contains("_")) {
                    txtCPF.setValue(null);
                    txtCPF.setForeground(Color.WHITE);
                }
            }

            @Override
            public void focusLost(FocusEvent evt) {
                // Ao perder foco, se o campo estiver vazio, restaura o placeholder.
                String content = String.valueOf(txtCPF.getValue()).trim();
                if (content.equals("null") || content.isEmpty()) {
                    txtCPF.setValue(null);
                    txtCPF.setForeground(Color.GRAY);
                }
            }
        });
    }

    /**
     * Processa a ação do botão "Continuar". Valida o CPF e navega para a próxima tela
     * apropriada com base no modo (compra/consulta) e na existência do cliente.
     */
    private void processarContinuar() {
        String cpfNormalizado = ValidadorCPF.normalizar(txtCPF.getText());

        if (cpfNormalizado == null || !ValidadorCPF.isValid(cpfNormalizado)) {
            JOptionPane.showMessageDialog(this, "O CPF informado é inválido ou incompleto. Por favor, verifique.", "CPF Inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        try {
            if (modoConsulta) {
                // No modo de consulta, vai para a lista de bilhetes
                frame.setContentPane(new TelaListaBilhetes(cpfNormalizado, this.reservaServico, this.clienteServico, this.pecaServico));
            } else { // No modo de compra
                if (this.turnoSelecionado == null) {
                    JOptionPane.showMessageDialog(this, "Erro interno: Turno não foi definido para a compra.", "Erro de Fluxo", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Verifica se o cliente já existe
                Optional<Cliente> clienteOpt = this.clienteServico.buscarPorCpf(cpfNormalizado);
                if (clienteOpt.isPresent()) {
                    // Cliente existe, vai para a tela de confirmação
                    frame.setContentPane(new TelaConfirmarPedido(
                        this.peca, clienteOpt.get(), this.assentosSelecionados, this.turnoSelecionado,
                        this.clienteServico, this.pecaServico, this.reservaServico
                    ));
                } else {
                    // Cliente não existe, vai para a tela de cadastro
                    TelaCadastrar telaCadastrar = new TelaCadastrar(
                        cpfNormalizado, this.peca, this.assentosSelecionados,
                        this.clienteServico, this.pecaServico, this.reservaServico
                    );
                    telaCadastrar.setTurnoSelecionado(this.turnoSelecionado);
                    frame.setContentPane(telaCadastrar);
                }
            }
            frame.revalidate();
            frame.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro ao processar sua solicitação: " + ex.getMessage(), "Erro de Sistema", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Navega para a tela anterior apropriada com base no modo atual (compra ou consulta).
     */
    private void navegarParaTelaAnterior() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (modoConsulta) {
            frame.setContentPane(new TelaPrincipal(this.clienteServico, this.pecaServico, this.reservaServico));
        } else {
            frame.setContentPane(new TelaSelecionarAssento(this.peca, this.turnoSelecionado, this.pecaServico, this.clienteServico, this.reservaServico));
        }
        frame.revalidate();
        frame.repaint();
    }
}