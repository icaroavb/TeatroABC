package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico;
import com.teatroabc.aplicacao.interfaces.ISessaoServico;
import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAnimado;
import com.teatroabc.infraestrutura.ui_swing.componentes.CardPeca;
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import java.awt.*;
import java.util.List;
import javax.swing.*;

/**
 * Tela responsável por permitir ao usuário visualizar e selecionar uma das peças em cartaz.
 * Após a seleção, o usuário prosseguirá para a tela de seleção de sessão (data e turno).
 * 
 * Na Arquitetura Hexagonal, atua como um Adaptador Primário, interagindo com o IPecaServico
 * para buscar os dados e orquestrando a navegação para a próxima etapa do fluxo.
 */
public class TelaSelecionarPeca extends JPanel {

    // Serviços injetados via construtor
    private final IPecaServico pecaServico;
    private final IClienteServico clienteServico;
    private final IReservaServico reservaServico;
    private final ISessaoServico sessaoServico;

    // Estado da UI
    private Peca pecaSelecionada;
    private BotaoAnimado btnContinuar;
    private BotaoAnimado btnVoltar;
    private JPanel painelDosCardsDePecas;

    /**
     * Construtor da TelaSelecionarPeca.
     * @param pecaServico Serviço para buscar informações das peças.
     * @param clienteServico Serviço de cliente.
     * @param reservaServico Serviço de reserva.
     * @param sessaoServico Serviço de sessão.
     * @throws IllegalArgumentException se algum dos serviços for nulo.
     */
    public TelaSelecionarPeca(IPecaServico pecaServico, IClienteServico clienteServico, 
                              IReservaServico reservaServico, ISessaoServico sessaoServico) {
        if (pecaServico == null || clienteServico == null || reservaServico == null || sessaoServico == null) {
            throw new IllegalArgumentException("Os serviços injetados não podem ser nulos em TelaSelecionarPeca.");
        }
        this.pecaServico = pecaServico;
        this.clienteServico = clienteServico;
        this.reservaServico = reservaServico;
        this.sessaoServico = sessaoServico;
        this.pecaSelecionada = null;

        configurarTelaVisual();
    }

    /**
     * Configura os componentes visuais e o layout da tela.
     */
    private void configurarTelaVisual() {
        setLayout(new BorderLayout(0, 20));
        setBackground(Constantes.AZUL_ESCURO);
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
     
        JPanel painelConteudo = new JPanel();
        painelConteudo.setLayout(new BoxLayout(painelConteudo, BoxLayout.Y_AXIS));
        painelConteudo.setOpaque(false);

        JLabel titulo = new JLabel("ESCOLHA UMA PEÇA");
        titulo.setFont(Constantes.FONTE_TITULO);
        titulo.setForeground(Constantes.AMARELO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelConteudo.add(titulo);
        painelConteudo.add(Box.createRigidArea(new Dimension(0, 40)));

        this.painelDosCardsDePecas = new JPanel(new GridLayout(1, 0, 30, 0));
        this.painelDosCardsDePecas.setOpaque(false);

        //this.painelDosCardsDePecas.setMaximumSize(new Dimension(1150, 460));

        this.painelDosCardsDePecas.setAlignmentX(Component.CENTER_ALIGNMENT);

        adicionarCardsDePecasAoPainel(this.painelDosCardsDePecas);
        painelConteudo.add(this.painelDosCardsDePecas);
        painelConteudo.add(Box.createRigidArea(new Dimension(0, 40)));

        btnVoltar = new BotaoAnimado("VOLTAR",
            Constantes.CINZA_ESCURO, //Cor padrão
            Constantes.AZUL_CLARO, //Cor Transição 
            new Dimension(400, 70));
            
        btnVoltar.setFont(Constantes.FONTE_BOTAO.deriveFont(22f));
        btnVoltar.setEnabled(true);
        btnVoltar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnVoltar.addActionListener(_ -> voltarParaTelaPrincipal());
        /**
         * O que faz: Para cada ActionListener esperado pelo construtor, estamos criando uma função anônima na hora. 
         * _ -> abrirSelecaoPeca() pode ser lido como: "Crie uma função que recebe um argumento vazio e, quando chamada, ignore o e e execute o método abrirSelecaoPeca()".
         * Vantagens: É extremamente conciso, direto e fácil de ler. A intenção ("quando este botão for clicado, chame este método") é imediatamente óbvia.
         * Outra abordagem mais clássica:
         * // Em TelaPrincipal.java
         * PainelNavegacaoPrincipal painelBotoes = new PainelNavegacaoPrincipal(
         *      this::abrirSelecaoPeca,
         *      this::abrirConsultaBilhete,
         *      this::abrirCadastroCliente);
         */
        
        btnContinuar = new BotaoAnimado("CONTINUAR",
            Constantes.CINZA_ESCURO, //Cor padrão
            Constantes.AZUL_CLARO, //Cor Transição
            new Dimension(400, 70));

        btnContinuar.setFont(Constantes.FONTE_BOTAO.deriveFont(22f));
        btnContinuar.setEnabled(false);
        btnContinuar.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnContinuar.addActionListener(_ -> navegarParaSelecionarSessao());
        /**
         * O que faz: Para cada ActionListener esperado pelo construtor, estamos criando uma função anônima na hora. 
         * _ -> abrirSelecaoPeca() pode ser lido como: "Crie uma função que recebe um argumento vazio e, quando chamada, ignore o e e execute o método abrirSelecaoPeca()".
         * Vantagens: É extremamente conciso, direto e fácil de ler. A intenção ("quando este botão for clicado, chame este método") é imediatamente óbvia.
         * Outra abordagem mais clássica:
         * // Em TelaPrincipal.java
         * PainelNavegacaoPrincipal painelBotoes = new PainelNavegacaoPrincipal(
         *      this::abrirSelecaoPeca,
         *      this::abrirConsultaBilhete,
         *      this::abrirCadastroCliente);
         */
        
         // 1. Painel para alinhar os botões
        JPanel painelBotoes = new JPanel();
        //painelBotoes.setLayout(new BoxLayout(painelBotoes, BoxLayout.X_AXIS));
        painelBotoes.setOpaque(false); // Para manter o fundo azul da tela
        // 2. Adicione o botão Voltar à esquerda
        painelBotoes.add(btnVoltar);
        // 3. Adicione um espaçador flexível no meio
        painelBotoes.add(Box.createHorizontalGlue());
        // 4. Adicione o botão Continuar à direita
        painelBotoes.add(btnContinuar);
        // 5. Adicione o painel de botões ao painel de conteúdo principal
        painelConteudo.add(painelBotoes);
        painelConteudo.add(Box.createVerticalGlue());

        add(painelConteudo, BorderLayout.CENTER);
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
            
            if (listaDePecas.isEmpty()) {
                JLabel lblSemPecas = new JLabel("Nenhuma peça disponível no momento.");
                lblSemPecas.setForeground(Color.WHITE);
                lblSemPecas.setFont(Constantes.FONTE_TEXTO);
                painel.setLayout(new FlowLayout(FlowLayout.CENTER));
                painel.add(lblSemPecas);
            } else {
                painel.setLayout(new GridLayout(1, 0, 30, 0));
                for (Peca peca : listaDePecas) {
                    CardPeca card = new CardPeca(peca);
                    card.setSelecao(true);

                    card.addActionListener(e -> {
                        CardPeca cardClicado = (CardPeca) e.getSource();
                        this.pecaSelecionada = cardClicado.getPeca();
                        
                        if (this.btnContinuar != null) {
                            this.btnContinuar.setEnabled(true);
                        }

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
     * Navega para a nova tela de seleção de sessão, passando a peça selecionada e todos os serviços.
     */
    private void navegarParaSelecionarSessao() {
        if (this.pecaSelecionada == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma peça para continuar.", "Nenhuma Peça Selecionada", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaSelecionarSessao(
            this.pecaSelecionada, 
            this.sessaoServico, 
            this.pecaServico, 
            this.clienteServico, 
            this.reservaServico
        ));
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Navega de volta para a tela principal.
     */
    private void voltarParaTelaPrincipal() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaPrincipal(
            this.clienteServico, 
            this.pecaServico, 
            this.reservaServico, 
            this.sessaoServico
        ));
        frame.revalidate();
        frame.repaint();
    }
}