package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico;
import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAnimado;
import com.teatroabc.infraestrutura.ui_swing.componentes.CardPeca;
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Tela responsável por permitir ao usuário visualizar e selecionar uma das peças em cartaz.
 * Após a seleção, o usuário pode prosseguir para a seleção do turno.
 * 
 * Na Arquitetura Hexagonal, esta classe atua como um Adaptador Primário,
 * interagindo com o serviço IPecaServico para buscar os dados e orquestrando a
 * navegação para as próximas telas do fluxo de compra.
 */
public class TelaSelecionarPeca extends JPanel {

    // Serviços injetados (Portas de Entrada)
    private final IPecaServico pecaServico;
    private final IClienteServico clienteServico; // Para repassar na navegação
    private final IReservaServico reservaServico; // Para repassar na navegação

    // Estado da UI
    private Peca pecaSelecionada; // Armazena a peça que o usuário clicou/selecionou
    private BotaoAnimado btnContinuar;
    private JPanel painelDosCardsDePecas; // Referência ao painel que contém os cards

    /**
     * Construtor da TelaSelecionarPeca.
     * @param pecaServico Serviço para buscar informações das peças. Utilizado diretamente.
     * @param clienteServico Serviço de cliente (para repassar para as próximas telas).
     * @param reservaServico Serviço de reserva (para repassar para as próximas telas).
     * @throws IllegalArgumentException se algum dos serviços injetados for nulo.
     */
    public TelaSelecionarPeca(IPecaServico pecaServico, IClienteServico clienteServico, IReservaServico reservaServico) {
        if (pecaServico == null || clienteServico == null || reservaServico == null) {
            throw new IllegalArgumentException("Os serviços injetados não podem ser nulos em TelaSelecionarPeca.");
        }
        this.pecaServico = pecaServico;
        this.clienteServico = clienteServico;
        this.reservaServico = reservaServico;
        this.pecaSelecionada = null; // Nenhuma peça selecionada inicialmente

        configurarTelaVisual();
    }

    /**
     * Configura os componentes visuais e o layout da tela.
     */
    private void configurarTelaVisual() {
        setLayout(new BorderLayout(0, 20));
        setBackground(Constantes.AZUL_ESCURO);
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Cabeçalho com botão de voltar e logo
        add(criarCabecalhoDaTela(), BorderLayout.NORTH);

        // Painel Central para o conteúdo principal
        JPanel painelConteudo = new JPanel();
        painelConteudo.setLayout(new BoxLayout(painelConteudo, BoxLayout.Y_AXIS));
        painelConteudo.setOpaque(false);

        JLabel titulo = new JLabel("ESCOLHA UMA PEÇA");
        titulo.setFont(Constantes.FONTE_TITULO);
        titulo.setForeground(Constantes.AMARELO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelConteudo.add(titulo);
        painelConteudo.add(Box.createRigidArea(new Dimension(0, 40)));

        // Painel para os Cards das Peças
        this.painelDosCardsDePecas = new JPanel(new GridLayout(1, 0, 30, 0));
        this.painelDosCardsDePecas.setOpaque(false);
        this.painelDosCardsDePecas.setMaximumSize(new Dimension(1150, 460));
        this.painelDosCardsDePecas.setAlignmentX(Component.CENTER_ALIGNMENT);

        adicionarCardsDePecasAoPainel(this.painelDosCardsDePecas);
        painelConteudo.add(this.painelDosCardsDePecas);
        painelConteudo.add(Box.createRigidArea(new Dimension(0, 40)));

        // Botão Continuar
        btnContinuar = new BotaoAnimado("CONTINUAR",
                Constantes.LARANJA, Constantes.AMARELO.darker(),
                new Dimension(300, 60));
        btnContinuar.setFont(Constantes.FONTE_BOTAO.deriveFont(22f));
        btnContinuar.setEnabled(false); // Inicia desabilitado
        btnContinuar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnContinuar.addActionListener(e -> navegarParaSelecionarTurno());

        painelConteudo.add(btnContinuar);
        painelConteudo.add(Box.createVerticalGlue()); // Garante que o conteúdo fique centralizado verticalmente

        add(painelConteudo, BorderLayout.CENTER);
    }

    /**
     * Cria o painel de cabeçalho da tela, contendo o botão "Voltar" e a logo.
     * @return JPanel configurado para o cabeçalho.
     */
    private JPanel criarCabecalhoDaTela() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);

        JButton btnVoltarUI = new JButton("<< Voltar para Tela Principal");
        btnVoltarUI.setFont(new Font("Arial", Font.PLAIN, 16));
        btnVoltarUI.setForeground(Constantes.AZUL_CLARO);
        btnVoltarUI.setOpaque(false);
        btnVoltarUI.setContentAreaFilled(false);
        btnVoltarUI.setBorderPainted(false);
        btnVoltarUI.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltarUI.addActionListener(e -> voltarParaTelaPrincipal());
        cabecalho.add(btnVoltarUI, BorderLayout.WEST);

        LogoTeatro logo = new LogoTeatro();
        logo.setPreferredSize(new Dimension(200,60));
        cabecalho.add(logo, BorderLayout.EAST);
        return cabecalho;
    }

    /**
     * Busca as peças através do serviço e popula o painel fornecido com componentes {@link CardPeca}.
     * Configura o ActionListener para cada card para lidar com a seleção pelo usuário.
     * @param painel O JPanel onde os cards serão adicionados.
     */
    private void adicionarCardsDePecasAoPainel(JPanel painel) {
        painel.removeAll();
        
        try {
            List<Peca> listaDePecas = this.pecaServico.buscarTodasPecas();

            if (listaDePecas == null || listaDePecas.isEmpty()) {
                JLabel lblSemPecas = new JLabel("Nenhuma peça disponível no momento.");
                lblSemPecas.setForeground(Color.WHITE);
                lblSemPecas.setFont(Constantes.FONTE_TEXTO);
                painel.setLayout(new FlowLayout(FlowLayout.CENTER));
                painel.add(lblSemPecas);
            } else {
                painel.setLayout(new GridLayout(1, 0, 30, 0));
                for (Peca peca : listaDePecas) {
                    CardPeca card = new CardPeca(peca);
                    card.setSelecao(true); // Habilita o modo de feedback visual de seleção

                    // ActionListener para quando um card de peça é clicado
                    card.addActionListener(e -> {
                        CardPeca cardClicado = (CardPeca) e.getSource();
                        
                        // Atualiza a peça selecionada na tela
                        this.pecaSelecionada = cardClicado.getPeca();
                        
                        // Habilita o botão de continuar, pois uma seleção foi feita
                        if (this.btnContinuar != null) {
                            this.btnContinuar.setEnabled(true);
                        }

                        // Lógica para feedback visual: desmarca outros cards e marca o atual
                        for (Component c : this.painelDosCardsDePecas.getComponents()) {
                            if (c instanceof CardPeca) {
                                ((CardPeca) c).setSelecionado(c == cardClicado);
                            }
                        }
                    });
                    painel.add(card);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar todas as peças em TelaSelecionarPeca: " + e.getMessage());
            e.printStackTrace();
            JLabel lblErro = new JLabel("Erro ao carregar as peças. Por favor, tente novamente.");
            lblErro.setFont(Constantes.FONTE_TEXTO);
            lblErro.setForeground(Color.RED);
            painel.setLayout(new FlowLayout(FlowLayout.CENTER));
            painel.add(lblErro);
        }
        
        painel.revalidate();
        painel.repaint();
    }

    /**
     * Navega para a tela de seleção de turno, passando a peça selecionada e os serviços.
     * Valida se uma peça foi selecionada antes de prosseguir.
     */
    private void navegarParaSelecionarTurno() {
        if (this.pecaSelecionada == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma peça para continuar.", "Nenhuma Peça Selecionada", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaSelecionarTurno(this.pecaSelecionada, this.pecaServico, this.clienteServico, this.reservaServico));
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Navega de volta para a tela principal da aplicação.
     */
    private void voltarParaTelaPrincipal() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaPrincipal(this.clienteServico, this.pecaServico, this.reservaServico));
        frame.revalidate();
        frame.repaint();
    }
}