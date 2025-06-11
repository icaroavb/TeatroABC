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
// Removido: import java.awt.event.FocusAdapter; // Não usado aqui
// Removido: import java.awt.event.FocusEvent;   // Não usado aqui

/**
 * Tela responsável por permitir ao usuário selecionar o turno desejado
 * para uma peça previamente escolhida.
 * Atua como um Adaptador Primário na arquitetura hexagonal.
 */
public class TelaSelecionarTurno extends JPanel {

    private final Peca pecaSelecionada;
    private Turno turnoEscolhido; // Não final, pois é definido pela seleção do usuário

    // Serviços injetados, principalmente para repassar para as próximas telas
    private final IClienteServico clienteServico;
    private final IPecaServico pecaServico;
    private final IReservaServico reservaServico;

    private BotaoAnimado btnContinuar; // Botão para prosseguir
    private ButtonGroup grupoRadiosTurno; // Agrupa os radio buttons para seleção única

    /**
     * Construtor da TelaSelecionarTurno.
     * @param peca A peça selecionada na tela anterior.
     * @param pecaServico Serviço para operações de peça.
     * @param clienteServico Serviço para operações de cliente.
     * @param reservaServico Serviço para operações de reserva.
     * @throws IllegalArgumentException se Peca ou qualquer um dos serviços for nulo.
     */
    public TelaSelecionarTurno(Peca peca, IPecaServico pecaServico, IClienteServico clienteServico, IReservaServico reservaServico) {
        if (peca == null) throw new IllegalArgumentException("Peca não pode ser nula para TelaSelecionarTurno.");
        if (pecaServico == null) throw new IllegalArgumentException("IPecaServico não pode ser nulo.");
        if (clienteServico == null) throw new IllegalArgumentException("IClienteServico não pode ser nulo.");
        if (reservaServico == null) throw new IllegalArgumentException("IReservaServico não pode ser nulo.");
        
        this.pecaSelecionada = peca;
        this.pecaServico = pecaServico;
        this.clienteServico = clienteServico;
        this.reservaServico = reservaServico;
        this.turnoEscolhido = null; // Nenhum turno selecionado inicialmente

        configurarTelaVisual();
    }

    /**
     * Configura os componentes visuais e o layout da tela.
     */
    private void configurarTelaVisual() {
        setLayout(new BorderLayout(0, 15)); // Espaçamento vertical entre regiões do BorderLayout
        setBackground(Constantes.AZUL_ESCURO); // Usando constante nomeada
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40)); // Margens gerais

        // Cabeçalho da tela
        add(criarCabecalhoDaTelaComVoltar(), BorderLayout.NORTH);

        // Painel central para o conteúdo principal
        JPanel painelCentral = new JPanel();
        painelCentral.setLayout(new BoxLayout(painelCentral, BoxLayout.Y_AXIS));
        painelCentral.setOpaque(false); // Para o fundo da tela (AZUL_ESCURO_FUNDO) ser visível
        
        painelCentral.add(Box.createVerticalGlue()); // Espaçador para empurrar conteúdo para o centro

        // Exibição da Peça Selecionada
        JLabel lblNomePeca = new JLabel("Peça: " + pecaSelecionada.getTitulo().toUpperCase());
        lblNomePeca.setFont(Constantes.FONTE_SUBTITULO.deriveFont(Font.PLAIN, 28f));
        lblNomePeca.setForeground(Color.WHITE);
        lblNomePeca.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCentral.add(lblNomePeca);
        painelCentral.add(Box.createRigidArea(new Dimension(0, 20)));

        // Instrução para o usuário
        JLabel lblInstrucao = new JLabel("Selecione o Turno Desejado:");
        lblInstrucao.setFont(Constantes.FONTE_TEXTO.deriveFont(Font.BOLD, 20f)); // Fonte maior
        lblInstrucao.setForeground(Constantes.AMARELO);
        lblInstrucao.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelCentral.add(lblInstrucao);
        painelCentral.add(Box.createRigidArea(new Dimension(0, 30)));

        // Painel para os RadioButtons dos Turnos
        JPanel painelRadios = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15)); // Layout e espaçamentos
        painelRadios.setOpaque(false);
        this.grupoRadiosTurno = new ButtonGroup();

        for (Turno turno : Turno.values()) {
            JRadioButton radio = criarRadioTurnoEstilizado(turno);
            this.grupoRadiosTurno.add(radio);
            painelRadios.add(radio);
        }
        painelCentral.add(painelRadios);
        
        painelCentral.add(Box.createVerticalGlue()); // Espaçador para empurrar conteúdo para o centro

        add(painelCentral, BorderLayout.CENTER);

        // Rodapé com o Botão Continuar
        add(criarRodapeComBotaoContinuar(), BorderLayout.SOUTH); // Método renomeado
    }

    /**
     * Cria o painel de cabeçalho da tela, incluindo o botão "Voltar".
     * @return JPanel configurado para o cabeçalho.
     */
    private JPanel criarCabecalhoDaTelaComVoltar() { // Nome do método atualizado
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(0,0,20,0)); // Margem inferior

        JButton btnVoltarUI = new JButton("<< Voltar para Seleção de Peça");
        btnVoltarUI.setFont(Constantes.FONTE_LABEL.deriveFont(15f)); // Fonte ajustada
        btnVoltarUI.setForeground(Constantes.AZUL_CLARO);
        btnVoltarUI.setOpaque(false);
        btnVoltarUI.setContentAreaFilled(false);
        btnVoltarUI.setBorderPainted(false);
        btnVoltarUI.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltarUI.addActionListener(e -> voltarParaSelecaoPeca());
        cabecalho.add(btnVoltarUI, BorderLayout.WEST);

        LogoTeatro logo = new LogoTeatro();
        logo.setPreferredSize(new Dimension(200,60)); // Ajuste de tamanho se necessário
        cabecalho.add(logo, BorderLayout.EAST);
        return cabecalho;
    }

    /**
     * Cria e estiliza um JRadioButton para um Turno específico.
     * @param turnoOpcao O Turno a ser representado pelo botão.
     * @return JRadioButton configurado.
     */
    private JRadioButton criarRadioTurnoEstilizado(Turno turnoOpcao) {
        JRadioButton radio = new JRadioButton(turnoOpcao.toString()); // Ex: "Manhã - 10:00"
        radio.setFont(Constantes.FONTE_BOTAO.deriveFont(20f)); // Usando constante
        radio.setForeground(Color.WHITE);
        radio.setOpaque(false);
        radio.setFocusPainted(false);
        radio.setCursor(new Cursor(Cursor.HAND_CURSOR));
        radio.setIconTextGap(10);

        // Ícones personalizados para JRadioButton (exemplo)
        // TODO: Refinar os ícones para melhor estética, se desejado.
        radio.setIcon(new Icon() {
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawOval(x+2, y+2, 14, 14);
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
                g2.fillOval(x+2, y+2, 15, 15);
                g2.setColor(Constantes.AZUL_CLARO);
                g2.fillOval(x + 5, y + 5, 9, 9);
                g2.dispose();
            }
            public int getIconWidth() { return 18; }
            public int getIconHeight() { return 18; }
        });

        radio.addActionListener(e -> {
            this.turnoEscolhido = turnoOpcao;
            if (this.btnContinuar != null) { // Garante que o botão já foi inicializado
                this.btnContinuar.setEnabled(true);
            }
        });
        return radio;
    }

    /**
     * Cria o painel do rodapé contendo o botão "Continuar".
     * @return JPanel configurado para o rodapé.
     */
    private JPanel criarRodapeComBotaoContinuar() { // Nome do método atualizado
        JPanel painelRodape = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Centraliza o botão
        painelRodape.setOpaque(false);
        painelRodape.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0)); // Mais margem superior

        this.btnContinuar = new BotaoAnimado(
                "CONTINUAR",
                Constantes.LARANJA,
                Constantes.AMARELO, // Usando cor de alerta para hover
                new Dimension(350, 70) // Botão maior
        );
        this.btnContinuar.setFont(Constantes.FONTE_BOTAO);
        this.btnContinuar.setEnabled(false); // Habilitado somente após seleção de turno
        this.btnContinuar.addActionListener(e -> prosseguirParaSelecionarAssento());

        painelRodape.add(this.btnContinuar);
        return painelRodape;
    }

    /**
     * Navega para a tela de seleção de assentos, passando a peça, o turno escolhido e os serviços.
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
        // Instancia TelaSelecionarAssento com todos os parâmetros necessários
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
        // TelaSelecionarPeca espera os três serviços
        frame.setContentPane(new TelaSelecionarPeca(this.pecaServico, this.clienteServico, this.reservaServico));
        frame.revalidate();
        frame.repaint();
    }
}