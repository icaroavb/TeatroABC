package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico;
import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAnimado;
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;

import javax.swing.*;
import java.awt.*;

/**
 * Tela responsável por permitir ao usuário selecionar o turno desejado
 * (Manhã, Tarde ou Noite) para uma peça previamente escolhida.
 * 
 * Na Arquitetura Hexagonal, atua como um Adaptador Primário, coletando a entrada
 * do usuário e orquestrando a navegação para a próxima etapa do fluxo de compra.
 */
public class TelaSelecionarTurno extends JPanel {

    // Contexto recebido da tela anterior
    private final Peca pecaSelecionada;
    
    // Estado da tela
    private Turno turnoEscolhido;

    // Serviços injetados, para repassar para as próximas telas
    private final IClienteServico clienteServico;
    private final IPecaServico pecaServico;
    private final IReservaServico reservaServico;

    // Componentes da UI
    private BotaoAnimado btnContinuar;
    private ButtonGroup grupoRadiosTurno;

    /**
     * Construtor da TelaSelecionarTurno.
     * @param peca A peça selecionada na tela anterior. Não pode ser nula.
     * @param pecaServico Serviço para operações de peça.
     * @param clienteServico Serviço para operações de cliente.
     * @param reservaServico Serviço para operações de reserva.
     * @throws IllegalArgumentException se Peca ou qualquer um dos serviços for nulo.
     */
    public TelaSelecionarTurno(Peca peca, IPecaServico pecaServico, IClienteServico clienteServico, IReservaServico reservaServico) {
        if (peca == null) throw new IllegalArgumentException("Peca não pode ser nula para TelaSelecionarTurno.");
        if (pecaServico == null || clienteServico == null || reservaServico == null) {
            throw new IllegalArgumentException("Serviços não podem ser nulos.");
        }
        
        this.pecaSelecionada = peca;
        this.pecaServico = pecaServico;
        this.clienteServico = clienteServico;
        this.reservaServico = reservaServico;
        this.turnoEscolhido = null;

        configurarTelaVisual();
    }

    /**
     * Configura os componentes visuais e o layout da tela.
     */
    private void configurarTelaVisual() {
        setLayout(new BorderLayout(0, 15));
        setBackground(Constantes.AZUL_ESCURO);
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        add(criarCabecalhoDaTelaComVoltar(), BorderLayout.NORTH);

        JPanel painelCentral = new JPanel();
        painelCentral.setLayout(new BoxLayout(painelCentral, BoxLayout.Y_AXIS));
        painelCentral.setOpaque(false);
        
        painelCentral.add(Box.createVerticalGlue());

        JLabel lblNomePeca = new JLabel("Peça: " + pecaSelecionada.getTitulo().toUpperCase());
        lblNomePeca.setFont(Constantes.FONTE_SUBTITULO.deriveFont(Font.PLAIN, 28f));
        lblNomePeca.setForeground(Color.WHITE);
        lblNomePeca.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCentral.add(lblNomePeca);
        painelCentral.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel lblInstrucao = new JLabel("Selecione o Turno Desejado:");
        lblInstrucao.setFont(Constantes.FONTE_TEXTO.deriveFont(Font.BOLD, 20f));
        lblInstrucao.setForeground(Constantes.AMARELO);
        lblInstrucao.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCentral.add(lblInstrucao);
        painelCentral.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel painelRadios = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
        painelRadios.setOpaque(false);
        this.grupoRadiosTurno = new ButtonGroup();

        for (Turno turno : Turno.values()) {
            JRadioButton radio = criarRadioTurnoEstilizado(turno);
            this.grupoRadiosTurno.add(radio);
            painelRadios.add(radio);
        }
        painelCentral.add(painelRadios);
        
        painelCentral.add(Box.createVerticalGlue());

        add(painelCentral, BorderLayout.CENTER);

        add(criarRodapeComBotaoContinuar(), BorderLayout.SOUTH);
    }

    /**
     * Cria o painel de cabeçalho da tela, incluindo o botão "Voltar".
     * @return JPanel configurado para o cabeçalho.
     */
    private JPanel criarCabecalhoDaTelaComVoltar() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton btnVoltarUI = new JButton("<< Voltar para Seleção de Peça");
        btnVoltarUI.setFont(Constantes.FONTE_LABEL.deriveFont(15f));
        btnVoltarUI.setForeground(Constantes.AZUL_CLARO);
        btnVoltarUI.setContentAreaFilled(false);
        btnVoltarUI.setBorderPainted(false);
        btnVoltarUI.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltarUI.addActionListener(e -> voltarParaSelecaoPeca());
        cabecalho.add(btnVoltarUI, BorderLayout.WEST);

        LogoTeatro logo = new LogoTeatro();
        logo.setPreferredSize(new Dimension(200, 60));
        cabecalho.add(logo, BorderLayout.EAST);
        return cabecalho;
    }

    /**
     * Cria e estiliza um JRadioButton para um Turno específico.
     * @param turnoOpcao O Turno a ser representado pelo botão.
     * @return JRadioButton configurado com ícones e action listener.
     */
    private JRadioButton criarRadioTurnoEstilizado(Turno turnoOpcao) {
        JRadioButton radio = new JRadioButton(turnoOpcao.toString());
        radio.setFont(Constantes.FONTE_BOTAO.deriveFont(20f));
        radio.setForeground(Color.WHITE);
        radio.setOpaque(false);
        radio.setFocusPainted(false);
        radio.setCursor(new Cursor(Cursor.HAND_CURSOR));
        radio.setIconTextGap(10);

        // Ícones customizados para estados selecionado e não-selecionado.
        radio.setIcon(new Icon() {
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawOval(x + 2, y + 2, 14, 14);
                g2.dispose();
            }
            public int getIconWidth() { return 18; }
            public int getIconHeight() { return 18; }
        });
        radio.setSelectedIcon(new Icon() {
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Constantes.LARANJA);
                g2.fillOval(x + 2, y + 2, 15, 15);
                g2.setColor(Constantes.AZUL_CLARO);
                g2.fillOval(x + 5, y + 5, 9, 9);
                g2.dispose();
            }
            public int getIconWidth() { return 18; }
            public int getIconHeight() { return 18; }
        });

        // Define a ação a ser executada quando este rádio for selecionado.
        radio.addActionListener(e -> {
            this.turnoEscolhido = turnoOpcao;
            if (this.btnContinuar != null) {
                this.btnContinuar.setEnabled(true);
            }
        });
        return radio;
    }

    /**
     * Cria o painel do rodapé contendo o botão "Continuar".
     * @return JPanel configurado para o rodapé.
     */
    private JPanel criarRodapeComBotaoContinuar() {
        JPanel painelRodape = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelRodape.setOpaque(false);
        painelRodape.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));

        this.btnContinuar = new BotaoAnimado(
                "CONTINUAR",
                Constantes.LARANJA,
                Constantes.AMARELO,
                new Dimension(350, 70)
        );
        this.btnContinuar.setFont(Constantes.FONTE_BOTAO);
        this.btnContinuar.setEnabled(false); // Habilitado somente após seleção de turno.
        this.btnContinuar.addActionListener(e -> prosseguirParaSelecionarAssento());

        painelRodape.add(this.btnContinuar);
        return painelRodape;
    }

    /**
     * Navega para a tela de seleção de assentos, passando a peça, o turno escolhido e os serviços.
     * Valida se um turno foi selecionado antes de prosseguir.
     */
    private void prosseguirParaSelecionarAssento() {
        if (this.turnoEscolhido == null) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, selecione um turno para continuar.",
                    "Turno Não Selecionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        TelaSelecionarAssento telaAssentos = new TelaSelecionarAssento(
                this.pecaSelecionada,
                this.turnoEscolhido,
                this.pecaServico,
                this.clienteServico,
                this.reservaServico
        );
        frame.setContentPane(telaAssentos);
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Navega de volta para a tela de seleção de peças.
     */
    private void voltarParaSelecaoPeca() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaSelecionarPeca(this.pecaServico, this.clienteServico, this.reservaServico));
        frame.revalidate();
        frame.repaint();
    }
}