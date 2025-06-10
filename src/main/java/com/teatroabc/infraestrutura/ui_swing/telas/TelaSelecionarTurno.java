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
import java.util.Arrays; // Para iterar sobre os valores do enum Turno

public class TelaSelecionarTurno extends JPanel {

    private final Peca pecaSelecionada;
    private Turno turnoEscolhido;

    // Serviços injetados (para repassar)
    private final IClienteServico clienteServico;
    private final IPecaServico pecaServico;
    private final IReservaServico reservaServico;

    private BotaoAnimado btnContinuar;
    private ButtonGroup grupoRadiosTurno;

    public TelaSelecionarTurno(Peca peca, IPecaServico pecaServico, IClienteServico clienteServico, IReservaServico reservaServico) {
        if (peca == null || pecaServico == null || clienteServico == null || reservaServico == null) {
            throw new IllegalArgumentException("Peca e Serviços não podem ser nulos para TelaSelecionarTurno.");
        }
        this.pecaSelecionada = peca;
        this.pecaServico = pecaServico;
        this.clienteServico = clienteServico;
        this.reservaServico = reservaServico;
        this.turnoEscolhido = null; // Nenhum turno escolhido inicialmente

        configurarTelaVisual();
    }

    private void configurarTelaVisual() {
        setLayout(new BorderLayout(0, 20)); // Adiciona espaçamento vertical entre componentes do BorderLayout
        setBackground(Constantes.AZUL_ESCURO);
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Cabeçalho com Título e Logo
        add(criarCabecalhoDaTela(), BorderLayout.NORTH);

        // Painel Central para Seleção de Turnos
        JPanel painelCentral = new JPanel();
        painelCentral.setLayout(new BoxLayout(painelCentral, BoxLayout.Y_AXIS));
        painelCentral.setOpaque(false); // Para o fundo da tela ser visível
        painelCentral.add(Box.createVerticalGlue()); // Empurra conteúdo para o centro verticalmente

        // Nome da Peça Selecionada
        JLabel lblNomePeca = new JLabel("Peça: " + pecaSelecionada.getTitulo());
        lblNomePeca.setFont(Constantes.FONTE_SUBTITULO.deriveFont(Font.PLAIN, 28f)); // Fonte um pouco menor
        lblNomePeca.setForeground(Color.WHITE);
        lblNomePeca.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCentral.add(lblNomePeca);
        painelCentral.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel lblInstrucao = new JLabel("Selecione o Turno Desejado:");
        lblInstrucao.setFont(Constantes.FONTE_TEXTO.deriveFont(Font.BOLD));
        lblInstrucao.setForeground(Constantes.AMARELO);
        lblInstrucao.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCentral.add(lblInstrucao);
        painelCentral.add(Box.createRigidArea(new Dimension(0, 30)));

        // Painel para os RadioButtons dos Turnos
        JPanel painelRadios = new JPanel();
        // Usar FlowLayout ou GridLayout para os botões de turno
        painelRadios.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        painelRadios.setOpaque(false);
        grupoRadiosTurno = new ButtonGroup();

        for (Turno turno : Turno.values()) {
            JRadioButton radio = new JRadioButton(turno.toString()); // Ex: "Manhã - 10:00"
            radio.setFont(Constantes.FONTE_BOTAO.deriveFont(20f));
            radio.setForeground(Color.WHITE);
            radio.setOpaque(false); // Para o fundo do painelRadios ser visível
            radio.setFocusPainted(false);
            radio.setCursor(new Cursor(Cursor.HAND_CURSOR));
            // Estilizar ícones do radio button (similar a TelaSelecionarAssento)
            // ...

            radio.addActionListener(e -> {
                this.turnoEscolhido = turno;
                this.btnContinuar.setEnabled(true);
            });
            grupoRadiosTurno.add(radio);
            painelRadios.add(radio);
        }
        painelCentral.add(painelRadios);
        painelCentral.add(Box.createVerticalGlue()); // Empurra conteúdo para o centro verticalmente

        add(painelCentral, BorderLayout.CENTER);

        // Rodapé com Botões de Navegação
        add(criarRodapeNavegacao(), BorderLayout.SOUTH);
    }

    private JPanel criarCabecalhoDaTela() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        // Botão Voltar
        JButton btnVoltarUI = new JButton("<< Voltar para Seleção de Peça");
        btnVoltarUI.setFont(new Font("Arial", Font.PLAIN, 16));
        btnVoltarUI.setForeground(Constantes.AZUL_CLARO);
        btnVoltarUI.setOpaque(false);
        btnVoltarUI.setContentAreaFilled(false);
        btnVoltarUI.setBorderPainted(false);
        btnVoltarUI.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltarUI.addActionListener(e -> voltarParaSelecaoPeca());
        cabecalho.add(btnVoltarUI, BorderLayout.WEST);

        // Logo
        LogoTeatro logo = new LogoTeatro();
        logo.setPreferredSize(new Dimension(200,60));
        cabecalho.add(logo, BorderLayout.EAST);
        return cabecalho;
    }

    private JPanel criarRodapeNavegacao() {
        JPanel painelBotoesNavegacao = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotoesNavegacao.setOpaque(false);
        painelBotoesNavegacao.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        btnContinuar = new BotaoAnimado("CONTINUAR",
                Constantes.LARANJA, Constantes.AMARELO.darker(),
                new Dimension(300, 60));
        btnContinuar.setFont(Constantes.FONTE_BOTAO);
        btnContinuar.setEnabled(false); // Habilita após seleção de turno
        btnContinuar.addActionListener(e -> prosseguirParaSelecionarAssento());

        painelBotoesNavegacao.add(btnContinuar);
        return painelBotoesNavegacao;
    }

    private void prosseguirParaSelecionarAssento() {
        if (this.turnoEscolhido == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um turno.", "Turno Não Selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        // TelaSelecionarAssento espera Peca e os 3 serviços, e agora o Turno também.
        // O construtor de TelaSelecionarAssento precisará ser atualizado.
        TelaSelecionarAssento telaAssentos = new TelaSelecionarAssento(
                this.pecaSelecionada,
                this.turnoEscolhido, // Passa o turno escolhido
                this.pecaServico,
                this.clienteServico,
                this.reservaServico
        );
        frame.setContentPane(telaAssentos);
        frame.revalidate();
        frame.repaint();
    }

    private void voltarParaSelecaoPeca() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        // TelaSelecionarPeca espera os 3 serviços
        frame.setContentPane(new TelaSelecionarPeca(this.pecaServico, this.clienteServico, this.reservaServico));
        frame.revalidate();
        frame.repaint();
    }
}