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
import java.util.Collections;
import java.util.List;

/**
 * Tela responsável por permitir ao usuário visualizar e selecionar uma das peças em cartaz.
 * Após a seleção, o usuário pode prosseguir para a seleção do turno.
 * Atua como um Adaptador Primário na arquitetura hexagonal, interagindo com IPecaServico.
 */
public class TelaSelecionarPeca extends JPanel {

    private final IPecaServico pecaServico;
    private final IClienteServico clienteServico; // Para repassar na navegação
    private final IReservaServico reservaServico; // Para repassar na navegação

    private Peca pecaSelecionada; // Armazena a peça que o usuário clicou/selecionou
    private BotaoAnimado btnContinuar;
    private JPanel painelDosCardsDePecas; // Referência ao painel que contém os cards

    /**
     * Construtor da TelaSelecionarPeca.
     * @param pecaServico Serviço para buscar informações das peças.
     * @param clienteServico Serviço de cliente (para repassar).
     * @param reservaServico Serviço de reserva (para repassar).
     * @throws IllegalArgumentException se algum dos serviços for nulo.
     */
    public TelaSelecionarPeca(IPecaServico pecaServico, IClienteServico clienteServico, IReservaServico reservaServico) {
        if (pecaServico == null || clienteServico == null || reservaServico == null) {
            throw new IllegalArgumentException("Serviços não podem ser nulos em TelaSelecionarPeca.");
        }
        this.pecaServico = pecaServico;
        this.clienteServico = clienteServico;
        this.reservaServico = reservaServico;
        this.pecaSelecionada = null; // Nenhuma peça selecionada inicialmente

        configurarTelaVisual();
    }

    /**
     * Configura os componentes visuais da tela.
     */
    private void configurarTelaVisual() {
        setLayout(new BorderLayout(0, 20)); // Espaçamento vertical
        setBackground(Constantes.AZUL_ESCURO);
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Cabeçalho
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
        painelConteudo.add(Box.createRigidArea(new Dimension(0, 40))); // Espaçamento

        // Painel para os Cards das Peças
        this.painelDosCardsDePecas = new JPanel(new GridLayout(1, 0, 30, 0)); // 0 colunas = flexível
        this.painelDosCardsDePecas.setOpaque(false);
        // Definir um tamanho máximo preferencial para o painel de cards
        // A altura (460) deve acomodar a altura do CardPeca (450) mais um pequeno respiro.
        // A largura (1150) acomodaria 3 cards de 350 + 2 espaçamentos de 30 = 1050 + 60 = 1110.
        this.painelDosCardsDePecas.setMaximumSize(new Dimension(1150, 460));
        this.painelDosCardsDePecas.setAlignmentX(Component.CENTER_ALIGNMENT); // Centraliza o painel de cards

        adicionarCardsDePecasAoPainel(this.painelDosCardsDePecas);
        painelConteudo.add(this.painelDosCardsDePecas);
        painelConteudo.add(Box.createRigidArea(new Dimension(0, 40))); // Espaçamento

        // Botão Continuar
        btnContinuar = new BotaoAnimado("CONTINUAR",
                Constantes.LARANJA, Constantes.AMARELO.darker(),
                new Dimension(300, 60));
        btnContinuar.setFont(Constantes.FONTE_BOTAO.deriveFont(22f)); // Fonte um pouco menor
        btnContinuar.setEnabled(false); // Inicia desabilitado
        btnContinuar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnContinuar.addActionListener(e -> navegarParaSelecionarTurno()); // Ação do botão Continuar

        painelConteudo.add(btnContinuar);
        painelConteudo.add(Box.createVerticalGlue()); // Empurra o botão para cima se houver espaço

        add(painelConteudo, BorderLayout.CENTER);
    }

    /**
     * Cria o painel de cabeçalho da tela.
     * @return JPanel do cabeçalho.
     */
    private JPanel criarCabecalhoDaTela() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false); // Para fundo da tela ser visível

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
     * Busca as peças através do serviço e popula o painel fornecido com {@link CardPeca}.
     * Configura o ActionListener para cada card para lidar com a seleção.
     * @param painel O JPanel onde os cards serão adicionados.
     */
    private void adicionarCardsDePecasAoPainel(JPanel painel) {
        painel.removeAll();
        List<Peca> listaDePecas = Collections.emptyList();

        try {
            listaDePecas = this.pecaServico.buscarTodasPecas();
        } catch (Exception e) {
            System.err.println("Erro ao buscar todas as peças em TelaSelecionarPeca: " + e.getMessage());
            e.printStackTrace();
            JLabel lblErro = new JLabel("Erro ao carregar peças. Tente novamente.");
            lblErro.setFont(Constantes.FONTE_TEXTO);
            lblErro.setForeground(Color.RED);
            lblErro.setHorizontalAlignment(SwingConstants.CENTER);
            painel.setLayout(new BorderLayout());
            painel.add(lblErro, BorderLayout.CENTER);
            painel.revalidate();
            painel.repaint();
            return;
        }

        if (listaDePecas.isEmpty()) {
            JLabel lblSemPecas = new JLabel("Nenhuma peça disponível no momento.");
            lblSemPecas.setForeground(Color.WHITE);
            lblSemPecas.setFont(Constantes.FONTE_TEXTO);
            lblSemPecas.setHorizontalAlignment(SwingConstants.CENTER);
            painel.setLayout(new BorderLayout());
            painel.add(lblSemPecas, BorderLayout.CENTER);
        } else {
            painel.setLayout(new GridLayout(1, 0, 30, 0)); // Restaura GridLayout
            for (Peca peca : listaDePecas) {
                CardPeca card = new CardPeca(peca);
                card.setSelecao(true); // Habilita o modo de feedback visual de seleção no card

                // ActionListener para quando um card de peça é clicado
                card.addActionListener(e -> {
                    CardPeca cardClicado = (CardPeca) e.getSource();
                    Peca pecaDoCard = cardClicado.getPeca();
                    
                    // Atualiza a peça selecionada na tela
                    this.pecaSelecionada = pecaDoCard;
                    
                    // Habilita o botão de continuar
                    if (this.btnContinuar != null) {
                        this.btnContinuar.setEnabled(true);
                    }

                    // Lógica para feedback visual: desmarcar outros cards e marcar o atual
                    for (Component c : this.painelDosCardsDePecas.getComponents()) {
                        if (c instanceof CardPeca) {
                            ((CardPeca) c).setSelecionado(c == cardClicado);
                        }
                    }
                });
                painel.add(card);
            }
        }
        painel.revalidate();
        painel.repaint();
    }

    /**
     * Navega para a tela de seleção de turno, passando a peça selecionada e os serviços.
     */
    private void navegarParaSelecionarTurno() {
        if (this.pecaSelecionada == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma peça para continuar.", "Nenhuma Peça Selecionada", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        // TelaSelecionarTurno espera a Peca selecionada e os três serviços
        frame.setContentPane(new TelaSelecionarTurno(this.pecaSelecionada, this.pecaServico, this.clienteServico, this.reservaServico));
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Navega de volta para a tela principal.
     */
    private void voltarParaTelaPrincipal() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        // TelaPrincipal espera os três serviços
        frame.setContentPane(new TelaPrincipal(this.clienteServico, this.pecaServico, this.reservaServico));
        frame.revalidate();
        frame.repaint();
    }
}