package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAnimado;
import com.teatroabc.infraestrutura.ui_swing.componentes.CardPeca;
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico;

import javax.swing.*;
import java.awt.*;
import java.util.List;


/**
 * Tela principal da aplicação, que serve como ponto de partida para os fluxos do usuário.
 * Exibe as peças atualmente em cartaz e oferece as opções de navegação principais:
 * comprar bilhete, consultar bilhetes existentes ou cadastrar um novo cliente.
 * 
 * Na Arquitetura Hexagonal, esta classe atua como um Adaptador Primário (Driving Adapter),
 * sendo responsável por iniciar as interações do usuário com o núcleo da aplicação
 * através das interfaces de serviço (Portas de Entrada).
 */
public class TelaPrincipal extends JPanel {

    // Serviços injetados via construtor, representando as Portas de Entrada da aplicação.
    private final IClienteServico clienteServico;
    private final IPecaServico pecaServico;
    private final IReservaServico reservaServico;

    /**
     * Construtor da TelaPrincipal. Recebe as dependências dos serviços necessários
     * para operar e para repassar para as telas subsequentes.
     *
     * @param clienteServico Serviço para operações de cliente. Não pode ser nulo.
     * @param pecaServico Serviço para operações de peça. Não pode ser nulo.
     * @param reservaServico Serviço para operações de reserva/bilhete. Não pode ser nulo.
     * @throws IllegalArgumentException se algum dos serviços injetados for nulo.
     */
    public TelaPrincipal(IClienteServico clienteServico, IPecaServico pecaServico, IReservaServico reservaServico) {
        if (clienteServico == null || pecaServico == null || reservaServico == null) {
            throw new IllegalArgumentException("Os serviços injetados na TelaPrincipal não podem ser nulos.");
        }
        this.clienteServico = clienteServico;
        this.pecaServico = pecaServico;
        this.reservaServico = reservaServico;

        configurarTelaVisual();
    }

    /**
     * Configura os componentes visuais e o layout da tela principal.
     */
    private void configurarTelaVisual() {
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        // Container principal que organiza os elementos verticalmente
        JPanel containerPrincipal = new JPanel();
        containerPrincipal.setLayout(new BoxLayout(containerPrincipal, BoxLayout.Y_AXIS));
        containerPrincipal.setBackground(Constantes.AZUL_ESCURO);

        // Seção do cabeçalho com a logo
        JPanel painelLogo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelLogo.setBackground(Constantes.AZUL_ESCURO);
        painelLogo.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 30));
        painelLogo.add(new LogoTeatro());
        containerPrincipal.add(painelLogo);

        containerPrincipal.add(Box.createVerticalStrut(30));

        // Seção para exibir os cards das peças em cartaz
        JPanel painelPecas = new JPanel(new GridLayout(1, 0, 30, 0));
        painelPecas.setBackground(Constantes.AZUL_ESCURO);
        adicionarCardsPecasDinamicamente(painelPecas);
        containerPrincipal.add(painelPecas);

        containerPrincipal.add(Box.createVerticalStrut(50));

        // Seção dos botões de ação principais
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        painelBotoes.setBackground(Constantes.AZUL_ESCURO);

        BotaoAnimado btnComprar = new BotaoAnimado("COMPRAR BILHETE",
                Constantes.LARANJA, Constantes.AMARELO, new Dimension(400, 70));
        btnComprar.setFont(Constantes.FONTE_BOTAO);
        btnComprar.addActionListener(e -> abrirSelecaoPeca());

        BotaoAnimado btnConsultar = new BotaoAnimado("CONSULTAR BILHETE",
                Constantes.CINZA_ESCURO, Constantes.AZUL_CLARO, new Dimension(280, 60));
        btnConsultar.setFont(new Font("Arial", Font.BOLD, 18));
        btnConsultar.addActionListener(e -> abrirConsultaBilhete());

        BotaoAnimado btnCadastrar = new BotaoAnimado("CADASTRAR CLIENTE",
                Constantes.CINZA_ESCURO, Constantes.AZUL_CLARO, new Dimension(250, 60));
        btnCadastrar.setFont(new Font("Arial", Font.BOLD, 18));
        btnCadastrar.addActionListener(e -> abrirCadastroCliente());

        painelBotoes.add(btnComprar);
        painelBotoes.add(btnConsultar);
        painelBotoes.add(btnCadastrar);

        containerPrincipal.add(painelBotoes);
        containerPrincipal.add(Box.createVerticalStrut(30));

        add(containerPrincipal, BorderLayout.CENTER);
    }

    /**
     * Busca as peças através do IPecaServico e cria os componentes {@link CardPeca}
     * correspondentes, adicionando-os ao painel fornecido.
     * Este método é um exemplo claro de como a UI interage com o núcleo da aplicação.
     * @param painel O JPanel onde os cards das peças serão adicionados.
     */
    private void adicionarCardsPecasDinamicamente(JPanel painel) {
        painel.removeAll();
        
        try {
            List<Peca> listaDePecas = this.pecaServico.buscarTodasPecas();
            
            if (listaDePecas == null || listaDePecas.isEmpty()) {
                JLabel lblSemPecas = new JLabel("Nenhuma peça em cartaz no momento.");
                lblSemPecas.setFont(Constantes.FONTE_TEXTO);
                lblSemPecas.setForeground(Color.WHITE);
                painel.setLayout(new FlowLayout(FlowLayout.CENTER));
                painel.add(lblSemPecas);
            } else {
                 painel.setLayout(new GridLayout(1, 0, 30, 0));
                 for (Peca peca : listaDePecas) {
                     CardPeca card = new CardPeca(peca);
                     painel.add(card);
                 }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar todas as peças: " + e.getMessage());
            e.printStackTrace();
            JLabel lblErro = new JLabel("Erro ao carregar as peças. Tente novamente mais tarde.");
            lblErro.setFont(Constantes.FONTE_TEXTO);
            lblErro.setForeground(Color.RED);
            painel.setLayout(new FlowLayout(FlowLayout.CENTER));
            painel.add(lblErro);
        }
        
        painel.revalidate();
        painel.repaint();
    }

    /**
     * Navega para a tela de seleção de peças (início do fluxo de compra).
     * Repassa as dependências de serviço para a próxima tela.
     */
    private void abrirSelecaoPeca() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaSelecionarPeca(this.pecaServico, this.clienteServico, this.reservaServico));
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Navega para a tela de informar CPF para o fluxo de consulta de bilhetes.
     * Repassa as dependências de serviço para a próxima tela.
     */
    private void abrirConsultaBilhete() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaInformarCPF(
                true, // modoConsulta = true
                null, 
                null, 
                this.clienteServico,
                this.pecaServico,
                this.reservaServico
        ));
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Navega para a tela de cadastro de cliente (fluxo de cadastro avulso).
     * Repassa as dependências de serviço para a próxima tela.
     */
    private void abrirCadastroCliente() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaCadastrar(
                null,
                null,
                null,
                this.clienteServico,
                this.pecaServico,
                this.reservaServico
        ));
        frame.revalidate();
        frame.repaint();
    }
}