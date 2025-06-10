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
import java.util.List; // CERTIFIQUE-SE QUE É ESTE IMPORT E NÃO java.awt.List
import java.util.Collections; // Para Collections.emptyList()

/**
 * Tela principal da aplicação, exibe as peças em cartaz e opções de navegação.
 * Atua como um Adaptador Primário e ponto de partida para os fluxos do usuário.
 */
public class TelaPrincipal extends JPanel {

    private final IClienteServico clienteServico;
    private final IPecaServico pecaServico;
    private final IReservaServico reservaServico;

    /**
     * Construtor da TelaPrincipal que recebe as dependências dos serviços.
     * @param clienteServico Serviço para operações de cliente.
     * @param pecaServico Serviço para operações de peça.
     * @param reservaServico Serviço para operações de reserva/bilhete.
     * @throws IllegalArgumentException se algum dos serviços for nulo.
     */
    public TelaPrincipal(IClienteServico clienteServico, IPecaServico pecaServico, IReservaServico reservaServico) {
        if (clienteServico == null) {
            throw new IllegalArgumentException("IClienteServico não pode ser nulo na TelaPrincipal.");
        }
        if (pecaServico == null) {
            throw new IllegalArgumentException("IPecaServico não pode ser nulo na TelaPrincipal.");
        }
        if (reservaServico == null) {
            throw new IllegalArgumentException("IReservaServico não pode ser nulo na TelaPrincipal.");
        }

        this.clienteServico = clienteServico;
        this.pecaServico = pecaServico;
        this.reservaServico = reservaServico;

        configurarTelaVisual();
    }

    /**
     * Configura os componentes visuais da tela principal.
     */
    private void configurarTelaVisual() {
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        JPanel containerPrincipal = new JPanel();
        containerPrincipal.setLayout(new BoxLayout(containerPrincipal, BoxLayout.Y_AXIS));
        containerPrincipal.setBackground(Constantes.AZUL_ESCURO);

        JPanel painelLogo = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Logo à direita
        painelLogo.setBackground(Constantes.AZUL_ESCURO);
        painelLogo.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 30)); // Adiciona margem à direita e superior
        painelLogo.add(new LogoTeatro());
        containerPrincipal.add(painelLogo);

        containerPrincipal.add(Box.createVerticalStrut(30)); // Reduzido espaço após logo

        // Cards das peças
        JPanel painelPecas = new JPanel(new GridLayout(1, 0, 30, 0)); // 0 colunas para layout flexível
        painelPecas.setBackground(Constantes.AZUL_ESCURO);
        // Ajuste de borda para centralizar melhor os cards
        int margemHorizontalCards = Math.max(50, (this.getWidth() - (3 * 350 + 2 * 30)) / 2); // Calcula margem dinâmica
        painelPecas.setBorder(BorderFactory.createEmptyBorder(0, margemHorizontalCards, 0, margemHorizontalCards));
        // painelPecas.setMaximumSize(new Dimension(1200, 460)); // Ajustar altura máxima se necessário

        adicionarCardsPecasDinamicamente(painelPecas);
        containerPrincipal.add(painelPecas);

        containerPrincipal.add(Box.createVerticalStrut(50)); // Reduzido espaço antes dos botões

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        painelBotoes.setBackground(Constantes.AZUL_ESCURO);

        BotaoAnimado btnComprar = new BotaoAnimado("COMPRAR BILHETE",
                Constantes.LARANJA, Constantes.AMARELO, new Dimension(400, 70)); // Tamanho ajustado
        btnComprar.setFont(Constantes.FONTE_BOTAO);
        btnComprar.addActionListener(e -> abrirSelecaoPeca());

        BotaoAnimado btnConsultar = new BotaoAnimado("CONSULTAR BILHETE",
                Constantes.CINZA_ESCURO, Constantes.AZUL_CLARO, new Dimension(280, 60));
        btnConsultar.setFont(new Font("Arial", Font.BOLD, 18)); // Fonte ajustada
        btnConsultar.addActionListener(e -> abrirConsultaBilhete());

        BotaoAnimado btnCadastrar = new BotaoAnimado("CADASTRAR CLIENTE", // Texto mais específico
                Constantes.CINZA_ESCURO, Constantes.AZUL_CLARO, new Dimension(250, 60)); // Tamanho ajustado
        btnCadastrar.setFont(new Font("Arial", Font.BOLD, 18)); // Fonte ajustada
        btnCadastrar.addActionListener(e -> abrirCadastroCliente());

        painelBotoes.add(btnComprar);
        painelBotoes.add(btnConsultar);
        painelBotoes.add(btnCadastrar);

        containerPrincipal.add(painelBotoes);
        containerPrincipal.add(Box.createVerticalStrut(30)); // Espaço no final

        add(containerPrincipal, BorderLayout.CENTER);
    }

    /**
     * Busca as peças através do IPecaServico injetado e cria os CardPeca correspondentes,
     * adicionando-os ao painel fornecido.
     * @param painel O JPanel onde os cards das peças serão adicionados.
     */
    private void adicionarCardsPecasDinamicamente(JPanel painel) {
        painel.removeAll();
        
        // CORREÇÃO AQUI: Certificar que está usando java.util.List
        List<Peca> listaDePecas = this.pecaServico.buscarTodasPecas();
        
        if (listaDePecas == null || listaDePecas.isEmpty()) {
            JLabel lblSemPecas = new JLabel("Nenhuma peça em cartaz no momento.");
            lblSemPecas.setForeground(Color.WHITE);
            lblSemPecas.setFont(Constantes.FONTE_TEXTO);
            lblSemPecas.setHorizontalAlignment(SwingConstants.CENTER);
            painel.setLayout(new BorderLayout()); // Mudar layout para centralizar a mensagem
            painel.add(lblSemPecas, BorderLayout.CENTER);
        } else {
            // Restaura o GridLayout se houver peças
            painel.setLayout(new GridLayout(1, 0, 30, 0));
            for (Peca peca : listaDePecas) {
                CardPeca card = new CardPeca(peca);
                // O CardPeca não precisa de serviços para ser exibido, apenas do objeto Peca.
                // A ação de clique no CardPeca é tratada pela TelaSelecionarPeca, que já terá os serviços.
                painel.add(card);
            }
        }
        painel.revalidate();
        painel.repaint();
    }

    /**
     * Navega para a tela de seleção de peças, passando os serviços necessários.
     */
    private void abrirSelecaoPeca() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        // TelaSelecionarPeca precisa de todos os serviços para operar e/ou repassar
        frame.setContentPane(new TelaSelecionarPeca(this.pecaServico, this.clienteServico, this.reservaServico));
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Navega para a tela de informar CPF para consulta de bilhetes, passando os serviços.
     */
    private void abrirConsultaBilhete() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        // TelaInformarCPF (modo consulta) precisa de clienteServico, pecaServico (para voltar), e reservaServico.
        frame.setContentPane(new TelaInformarCPF(
                true, // modoConsulta
                null, // peca (não aplicável na consulta inicial)
                null, // assentosSelecionados (não aplicável)
                this.clienteServico,
                this.pecaServico, // CORREÇÃO: Passando pecaServico
                this.reservaServico
        ));
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Navega para a tela de cadastro de cliente (cadastro avulso), passando os serviços.
     */
    private void abrirCadastroCliente() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        // TelaCadastrar (cadastro avulso) precisa dos três serviços.
        frame.setContentPane(new TelaCadastrar(
                null, // cpf (cadastro novo)
                null, // peca (não aplicável em cadastro avulso inicial)
                null, // assentosSelecionados (não aplicável)
                this.clienteServico,
                this.pecaServico,
                this.reservaServico
        ));
        frame.revalidate();
        frame.repaint();
    }
}