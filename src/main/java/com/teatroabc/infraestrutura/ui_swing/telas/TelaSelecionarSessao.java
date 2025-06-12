package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico;
import com.teatroabc.aplicacao.interfaces.ISessaoServico;
import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.dominio.modelos.Sessao;
import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAnimado;
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.componentes.PainelSelecaoDia;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorData;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.*;

/**
 * Tela responsável por permitir ao usuário selecionar uma data e turno específicos
 * (uma Sessao) para uma peça previamente escolhida. A lógica de renderização de
 * cada dia foi encapsulada no componente {@link PainelSelecaoDia}.
 *
 * Na Arquitetura Hexagonal, atua como um Adaptador Primário, utilizando o ISessaoServico
 * para buscar as opções disponíveis e orquestrando a navegação para a próxima etapa.
 */
public class TelaSelecionarSessao extends JPanel {

    // Contexto da navegação
    private final Peca pecaSelecionada;
    private Sessao sessaoEscolhida;

    // Serviços injetados
    private final ISessaoServico sessaoServico;
    private final IClienteServico clienteServico;
    private final IPecaServico pecaServico;
    private final IReservaServico reservaServico;

    // Componentes da UI
    private BotaoAnimado btnContinuar;

    /**
     * Construtor da TelaSelecionarSessao.
     * @param peca A peça selecionada na tela anterior.
     * @param sessaoServico Serviço para buscar as sessões disponíveis.
     * @param pecaServico Serviço para repassar na navegação.
     * @param clienteServico Serviço para repassar na navegação.
     * @param reservaServico Serviço para repassar na navegação.
     */
    public TelaSelecionarSessao(Peca peca, ISessaoServico sessaoServico, IPecaServico pecaServico, IClienteServico clienteServico, IReservaServico reservaServico) {
        if (peca == null || sessaoServico == null || pecaServico == null || clienteServico == null || reservaServico == null) {
            throw new IllegalArgumentException("Peça e Serviços não podem ser nulos para TelaSelecionarSessao.");
        }
        this.pecaSelecionada = peca;
        this.sessaoServico = sessaoServico;
        this.pecaServico = pecaServico;
        this.clienteServico = clienteServico;
        this.reservaServico = reservaServico;
        this.sessaoEscolhida = null;

        configurarTelaVisual();
    }

    /**
     * Configura os componentes visuais e o layout da tela.
     */
    private void configurarTelaVisual() {
        setLayout(new BorderLayout(0, 15));
        setBackground(Constantes.AZUL_ESCURO);
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        add(criarCabecalhoDaTela(), BorderLayout.NORTH);

        JPanel painelCentral = new JPanel();
        painelCentral.setLayout(new BoxLayout(painelCentral, BoxLayout.Y_AXIS));
        painelCentral.setOpaque(false);
        
        // Ponto de interação com o núcleo: busca as sessões disponíveis para a peça.
        List<Sessao> sessoesDisponiveis = this.sessaoServico.buscarSessoesPorPeca(pecaSelecionada.getId());
        
        if (sessoesDisponiveis.isEmpty()) {
            // Exibe mensagem informativa se não houver sessões cadastradas.
            JLabel lblSemSessoes = new JLabel("Nenhuma sessão disponível para esta peça no momento.");
            lblSemSessoes.setFont(Constantes.FONTE_SUBTITULO);
            lblSemSessoes.setForeground(Color.WHITE);
            lblSemSessoes.setAlignmentX(Component.CENTER_ALIGNMENT);
            painelCentral.add(Box.createVerticalGlue());
            painelCentral.add(lblSemSessoes);
            painelCentral.add(Box.createVerticalGlue());
        } else {
            // Agrupa as sessões por dia para exibição organizada.
            Map<String, List<Sessao>> sessoesPorDia = sessoesDisponiveis.stream()
                .collect(Collectors.groupingBy(s -> FormatadorData.formatar(s.getDataHora(), "yyyy-MM-dd")));
                
            ButtonGroup grupoRadiosSessao = new ButtonGroup();

            // Itera sobre os dias e cria um painel de seleção para cada um.
            sessoesPorDia.keySet().stream().sorted().forEach(dia -> {
                // ActionListener que será passado para cada PainelSelecaoDia.
                // Ele atualiza a sessão escolhida na tela principal.
                ActionListener listenerSelecao = e -> {
                    String idSessaoSelecionada = e.getActionCommand();
                    this.sessaoEscolhida = sessoesDisponiveis.stream()
                        .filter(s -> s.getId().equals(idSessaoSelecionada))
                        .findFirst().orElse(null);
                    
                    if (btnContinuar != null) btnContinuar.setEnabled(true);
                };

                // Instancia o novo componente encapsulado.
                PainelSelecaoDia painelDoDia = new PainelSelecaoDia(
                    sessoesPorDia.get(dia), grupoRadiosSessao, listenerSelecao
                );
                painelCentral.add(painelDoDia);
                painelCentral.add(Box.createRigidArea(new Dimension(0, 25)));
            });
        }

        JScrollPane scrollPane = new JScrollPane(painelCentral);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        add(scrollPane, BorderLayout.CENTER);
        add(criarRodapeComBotaoContinuar(), BorderLayout.SOUTH);
    }

    /**
     * Cria o painel de cabeçalho da tela.
     * @return JPanel do cabeçalho.
     */
    private JPanel criarCabecalhoDaTela() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(0,0,20,0));

        JButton btnVoltarUI = new JButton("<< Voltar para Seleção de Peça");
        btnVoltarUI.setFont(Constantes.FONTE_LABEL.deriveFont(15f));
        btnVoltarUI.setForeground(Constantes.AZUL_CLARO);
        btnVoltarUI.setContentAreaFilled(false);
        btnVoltarUI.setBorderPainted(false);
        btnVoltarUI.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltarUI.addActionListener(e -> voltarParaSelecaoPeca());
        cabecalho.add(btnVoltarUI, BorderLayout.WEST);
        
        JLabel titulo = new JLabel("ESCOLHA A SESSÃO");
        titulo.setFont(Constantes.FONTE_TITULO.deriveFont(42f));
        titulo.setForeground(Constantes.AMARELO);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        cabecalho.add(titulo, BorderLayout.CENTER);

        LogoTeatro logo = new LogoTeatro();
        logo.setPreferredSize(new Dimension(200,60));
        cabecalho.add(logo, BorderLayout.EAST);
        return cabecalho;
    }

    /**
     * Cria o painel do rodapé contendo o botão "Continuar".
     * @return JPanel configurado para o rodapé.
     */
    private JPanel criarRodapeComBotaoContinuar() {
        JPanel painelRodape = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelRodape.setOpaque(false);
        painelRodape.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        this.btnContinuar = new BotaoAnimado(
                "ESCOLHER ASSENTOS",
                Constantes.LARANJA,
                Constantes.AMARELO,
                new Dimension(350, 70)
        );
        this.btnContinuar.setFont(Constantes.FONTE_BOTAO);
        this.btnContinuar.setEnabled(false);
        this.btnContinuar.addActionListener(e -> prosseguirParaSelecionarAssento());

        painelRodape.add(this.btnContinuar);
        return painelRodape;
    }

    /**
     * Navega para a tela de seleção de assentos, passando a sessão escolhida e os serviços.
     */
    private void prosseguirParaSelecionarAssento() {
        if (this.sessaoEscolhida == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma data e turno para continuar.", "Sessão Não Selecionada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaSelecionarAssento(
                this.sessaoEscolhida,
                this.pecaServico, 
                this.clienteServico, 
                this.reservaServico,
                this.sessaoServico
        ));
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Navega de volta para a tela de seleção de peças.
     */
    private void voltarParaSelecaoPeca() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaSelecionarPeca(this.pecaServico, this.clienteServico, this.reservaServico, this.sessaoServico));
        frame.revalidate();
        frame.repaint();
    }
}