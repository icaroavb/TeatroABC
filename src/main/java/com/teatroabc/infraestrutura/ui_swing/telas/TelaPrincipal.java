package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAnimado;
import com.teatroabc.infraestrutura.ui_swing.componentes.CardPeca; // Usado para exibir peças
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes; // Constantes de UI
import com.teatroabc.dominio.modelos.Peca; // Modelo de Peca
import com.teatroabc.aplicacao.interfaces.IClienteServico; // Interface do Serviço
import com.teatroabc.aplicacao.interfaces.IPecaServico;   // Interface do Serviço
import com.teatroabc.aplicacao.interfaces.IReservaServico; // Interface do Serviço

import javax.swing.*;
import java.awt.*;

public class TelaPrincipal extends JPanel {

    // Campos para armazenar os serviços injetados
    private final IClienteServico clienteServico;
    private final IPecaServico pecaServico;
    private final IReservaServico reservaServico;

    /**
     * Construtor da TelaPrincipal que recebe as dependências dos serviços.
     * @param clienteServico Serviço para operações de cliente.
     * @param pecaServico Serviço para operações de peça.
     * @param reservaServico Serviço para operações de reserva/bilhete.
     */
    public TelaPrincipal(IClienteServico clienteServico, IPecaServico pecaServico, IReservaServico reservaServico) {
        // Validação das dependências injetadas
        if (clienteServico == null) {
            throw new IllegalArgumentException("IClienteServico não pode ser nulo.");
        }
        if (pecaServico == null) {
            throw new IllegalArgumentException("IPecaServico não pode ser nulo.");
        }
        if (reservaServico == null) {
            throw new IllegalArgumentException("IReservaServico não pode ser nulo.");
        }

        this.clienteServico = clienteServico;
        this.pecaServico = pecaServico;
        this.reservaServico = reservaServico;

        configurarTelaVisual(); // Renomeado para clareza
    }

    private void configurarTelaVisual() { // Renomeado de configurarTela
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        JPanel containerPrincipal = new JPanel();
        containerPrincipal.setLayout(new BoxLayout(containerPrincipal, BoxLayout.Y_AXIS));
        containerPrincipal.setBackground(Constantes.AZUL_ESCURO);

        JPanel painelLogo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelLogo.setBackground(Constantes.AZUL_ESCURO);
        painelLogo.add(new LogoTeatro());
        containerPrincipal.add(painelLogo);

        containerPrincipal.add(Box.createVerticalStrut(50));

        JPanel painelPecas = new JPanel(new GridLayout(1, 0, 30, 0)); // GridLayout com 0 colunas se ajusta ao número de componentes
        painelPecas.setBackground(Constantes.AZUL_ESCURO);
        painelPecas.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));

        // Carregar e adicionar cards das peças USANDO O SERVIÇO
        adicionarCardsPecasDinamicamente(painelPecas);

        containerPrincipal.add(painelPecas);
        containerPrincipal.add(Box.createVerticalStrut(80));

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        painelBotoes.setBackground(Constantes.AZUL_ESCURO);

        BotaoAnimado btnComprar = new BotaoAnimado("COMPRAR BILHETE",
                Constantes.LARANJA, Constantes.AMARELO, new Dimension(450, 80));
        btnComprar.setFont(Constantes.FONTE_BOTAO);
        btnComprar.addActionListener(e -> abrirSelecaoPeca());

        BotaoAnimado btnConsultar = new BotaoAnimado("CONSULTAR BILHETE",
                Constantes.CINZA_ESCURO, Constantes.AZUL_CLARO, new Dimension(280, 60));
        btnConsultar.setFont(new Font("Arial", Font.BOLD, 20));
        btnConsultar.addActionListener(e -> abrirConsultaBilhete());

        BotaoAnimado btnCadastrar = new BotaoAnimado("CADASTRAR",
                Constantes.CINZA_ESCURO, Constantes.AZUL_CLARO, new Dimension(200, 60));
        btnCadastrar.setFont(new Font("Arial", Font.BOLD, 20));
        btnCadastrar.addActionListener(e -> abrirCadastroCliente()); // Renomeado para clareza

        painelBotoes.add(btnComprar);
        painelBotoes.add(btnConsultar);
        painelBotoes.add(btnCadastrar);

        containerPrincipal.add(painelBotoes);
        add(containerPrincipal, BorderLayout.CENTER);
    }

    /**
     * Busca as peças através do PecaServico e cria os CardPeca correspondentes.
     * @param painel O JPanel onde os cards serão adicionados.
     */
    private void adicionarCardsPecasDinamicamente(JPanel painel) {
        // Limpa o painel antes de adicionar novos cards (caso seja chamado múltiplas vezes)
        painel.removeAll();
        
        List <Peca> listaDePecas = this.pecaServico.buscarTodasPecas(); // USA O SERVIÇO INJETADO
        
        if (listaDePecas.isEmpty()) {
            // Tratar caso nenhuma peça seja encontrada, se necessário (ex: exibir mensagem)
            JLabel lblSemPecas = new JLabel("Nenhuma peça em cartaz no momento.");
            lblSemPecas.setForeground(Color.WHITE);
            lblSemPecas.setFont(Constantes.FONTE_TEXTO);
            lblSemPecas.setHorizontalAlignment(SwingConstants.CENTER);
            painel.add(lblSemPecas);
        } else {
            for (Peca peca : listaDePecas) {
                // CardPeca agora usa a Peca do domínio, que tem corFundoHex
                // A conversão de corFundoHex para java.awt.Color deve ocorrer DENTRO de CardPeca
                CardPeca card = new CardPeca(peca);
                // Se o CardPeca precisasse interagir com serviços (ex: ao ser clicado para uma ação complexa),
                // os serviços relevantes teriam que ser passados para o construtor do CardPeca.
                // Para o comportamento atual (apenas visualização), passar Peca é suficiente.
                painel.add(card);
            }
        }
        // Revalida e repinta o painel para mostrar os cards adicionados
        painel.revalidate();
        painel.repaint();
    }

    // O método adicionarCardsPecas original foi substituído por adicionarCardsPecasDinamicamente
    // private void adicionarCardsPecas(JPanel painel) { ... }


    private void abrirSelecaoPeca() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        // Passe os serviços necessários para a próxima tela
        frame.setContentPane(new TelaSelecionarPeca(this.pecaServico, this.clienteServico, this.reservaServico));
        frame.revalidate();
        frame.repaint();
    }

    private void abrirConsultaBilhete() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        // TelaInformarCPF (modo consulta) precisa do clienteServico (para validar CPF)
        // e do reservaServico (para buscar bilhetes após CPF informado).
        // Os parâmetros Peca e List<Assento> são null pois não vêm do fluxo de compra.
        frame.setContentPane(new TelaInformarCPF(true, null, null, this.clienteServico, this.reservaServico));
        frame.revalidate();
        frame.repaint();
    }

    private void abrirCadastroCliente() { // Renomeado de abrirCadastro
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        // TelaCadastrar (modo cadastro avulso) precisa do clienteServico.
        // Os outros serviços são passados como null ou podem ser necessários se o cadastro
        // for parte de um fluxo de compra interrompido, mas para cadastro avulso,
        // clienteServico é o principal.
        // O construtor de TelaCadastrar precisa ser ajustado para aceitar os serviços.
        frame.setContentPane(new TelaCadastrar(null, null, null, this.clienteServico, this.pecaServico, this.reservaServico)); // Passando null para CPF, Peca, Assentos
        frame.revalidate();
        frame.repaint();
    }
}