package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico; // IMPORT DO NOVO COMPONENTE
import com.teatroabc.aplicacao.interfaces.ISessaoServico;
import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.infraestrutura.ui_swing.componentes.CardPeca;
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.componentes.PainelNavegacao_TelaPrincipal;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import java.awt.*;
import java.util.List;
import javax.swing.*;

/**
 * Tela principal da aplicação, que serve como ponto de partida para os fluxos do usuário.
 * Sua responsabilidade agora é orquestrar os grandes blocos visuais: logo,
 * painel de peças em cartaz e o painel de navegação principal.
 */
public class TelaPrincipal extends JPanel {

    private final IClienteServico clienteServico;
    private final IPecaServico pecaServico;
    private final IReservaServico reservaServico;
    private final ISessaoServico sessaoServico;

    public TelaPrincipal(IClienteServico clienteServico, IPecaServico pecaServico, 
                         IReservaServico reservaServico, ISessaoServico sessaoServico) {
        if (clienteServico == null || pecaServico == null || reservaServico == null || sessaoServico == null) {
            throw new IllegalArgumentException("Todos os serviços injetados na TelaPrincipal não podem ser nulos.");
        }
        this.clienteServico = clienteServico;
        this.pecaServico = pecaServico;
        this.reservaServico = reservaServico;
        this.sessaoServico = sessaoServico;

        configurarTelaVisual();
    }

    //Encapsulamento das lógicas de validação quanto aos serviços injetados (considerados individualmente)

    /**
     * Configura os componentes visuais e o layout da tela principal.
     * A lógica de criação dos botões foi movida para o componente PainelNavegacaoPrincipal.
     */
    private void configurarTelaVisual() {
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        JPanel containerPrincipal = new JPanel();
        containerPrincipal.setLayout(new BoxLayout(containerPrincipal, BoxLayout.Y_AXIS));
        containerPrincipal.setBackground(Constantes.AZUL_ESCURO);

        JPanel painelLogo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelLogo.setBackground(Constantes.AZUL_ESCURO);
        painelLogo.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 30));
        painelLogo.add(new LogoTeatro());
        containerPrincipal.add(painelLogo);

        containerPrincipal.add(Box.createVerticalStrut(30));

        JPanel painelPecas = new JPanel(new GridLayout(1, 0, 30, 0));
        painelPecas.setOpaque(false);
        painelPecas.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));
        adicionarCardsPecasDinamicamente(painelPecas);
        containerPrincipal.add(painelPecas);

        containerPrincipal.add(Box.createVerticalStrut(50));

        // Instancia o novo componente e passa as ações (métodos de navegação) como lambdas.
        PainelNavegacao_TelaPrincipal painelBotoes = new PainelNavegacao_TelaPrincipal(
            e -> abrirSelecaoPeca(),
            e -> abrirConsultaBilhete(),
            e -> abrirCadastroCliente()
        );
        containerPrincipal.add(painelBotoes);
        
        containerPrincipal.add(Box.createVerticalStrut(30));

        // Envolve o container em um JScrollPane para garantir a visualização em telas menores.
        JScrollPane scrollPane = new JScrollPane(containerPrincipal);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(Constantes.AZUL_ESCURO);

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Busca as peças através do IPecaServico e cria os componentes {@link CardPeca}.
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
     * Repassa todas as dependências de serviço para a próxima tela.
     */
    private void abrirSelecaoPeca() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaSelecionarPeca(
            this.pecaServico, 
            this.clienteServico, 
            this.reservaServico,
            this.sessaoServico
        ));
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Navega para a tela de informar CPF para o fluxo de consulta de bilhetes.
     * Repassa todas as dependências de serviço para a próxima tela.
     */
    private void abrirConsultaBilhete() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaInformarCPF(
                true, // modoConsulta = true
                null, // sessao
                null, // assentosSelecionados
                this.clienteServico,
                this.pecaServico,
                this.reservaServico,
                this.sessaoServico
        ));
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Navega para a tela de cadastro de cliente (fluxo de cadastro avulso).
     * Repassa todas as dependências de serviço para a próxima tela.
     */
    private void abrirCadastroCliente() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaCadastrar(
                null, // cpf
                null, // sessao
                null, 
                this.clienteServico,
                this.pecaServico,
                this.reservaServico,
                this.sessaoServico
        ));
        frame.revalidate();
        frame.repaint();
    }
}