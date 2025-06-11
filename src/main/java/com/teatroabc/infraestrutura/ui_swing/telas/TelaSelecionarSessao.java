package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.dominio.modelos.Sessao;
import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico;
import com.teatroabc.aplicacao.interfaces.ISessaoServico;
import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAnimado;
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorData;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Tela responsável por permitir ao usuário selecionar uma data e turno específicos
 * (uma Sessao) para uma peça previamente escolhida. Esta tela substitui a antiga
 * TelaSelecionarTurno, oferecendo uma experiência mais completa.
 *
 * Na Arquitetura Hexagonal, atua como um Adaptador Primário, utilizando o ISessaoServico
 * para buscar as opções disponíveis e orquestrando a navegação.
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
     * @throws IllegalArgumentException se Peca ou qualquer um dos serviços for nulo.
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
        
        // Ponto de interação com o núcleo: busca as sessões disponíveis.
        List<Sessao> sessoesDisponiveis = this.sessaoServico.buscarSessoesPorPeca(pecaSelecionada.getId());
        
        if (sessoesDisponiveis.isEmpty()) {
            // Exibe mensagem se não houver sessões para a peça.
            JLabel lblSemSessoes = new JLabel("Nenhuma sessão disponível para esta peça no momento.");
            lblSemSessoes.setFont(Constantes.FONTE_SUBTITULO);
            lblSemSessoes.setForeground(Color.WHITE);
            lblSemSessoes.setAlignmentX(Component.CENTER_ALIGNMENT);
            painelCentral.add(Box.createVerticalGlue());
            painelCentral.add(lblSemSessoes);
            painelCentral.add(Box.createVerticalGlue());
        } else {
            // Agrupa as sessões por data para exibição organizada.
            Map<String, List<Sessao>> sessoesPorDia = sessoesDisponiveis.stream()
                .collect(Collectors.groupingBy(s -> FormatadorData.formatar(s.getDataHora(), "yyyy-MM-dd")));
                
            ButtonGroup grupoRadiosSessao = new ButtonGroup();

            // Cria um bloco de seleção para cada dia com sessões disponíveis.
            sessoesPorDia.keySet().stream().sorted().forEach(dia -> {
                painelCentral.add(criarBlocoDeSessaoParaDia(sessoesPorDia.get(dia), grupoRadiosSessao));
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
     * Cria um painel visual para um dia específico, contendo os botões de rádio para cada turno.
     * @param sessoesDoDia A lista de sessões para aquele dia.
     * @param grupoRadios O ButtonGroup ao qual os botões de rádio pertencerão.
     * @return Um JPanel representando o bloco de seleção para um dia.
     */
    private JPanel criarBlocoDeSessaoParaDia(List<Sessao> sessoesDoDia, ButtonGroup grupoRadios) {
        JPanel painelDia = new JPanel();
        painelDia.setLayout(new BoxLayout(painelDia, BoxLayout.Y_AXIS));
        painelDia.setOpaque(false);
        // Usa a data da primeira sessão para formatar o título do bloco.
        String tituloDoDia = formatarTituloDia(sessoesDoDia.get(0));
        painelDia.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Constantes.AZUL_CLARO, 1, true),
            tituloDoDia,
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 18), Constantes.AMARELO
        ));

        JPanel painelHorarios = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        painelHorarios.setOpaque(false);

        for (Sessao sessao : sessoesDoDia) {
            JRadioButton radio = new JRadioButton(sessao.getTurno().toString());
            radio.setFont(Constantes.FONTE_BOTAO.deriveFont(18f));
            radio.setForeground(Color.WHITE);
            radio.setOpaque(false);
            radio.setCursor(new Cursor(Cursor.HAND_CURSOR));
            radio.addActionListener(e -> {
                this.sessaoEscolhida = sessao;
                if (btnContinuar != null) btnContinuar.setEnabled(true);
            });
            grupoRadios.add(radio);
            painelHorarios.add(radio);
        }
        
        painelDia.add(painelHorarios);
        return painelDia;
    }

    /**
     * Formata o título para o bloco do dia (ex: "Sexta-feira, 20 de junho").
     * @param sessao Uma sessão daquele dia.
     * @return Uma string formatada.
     */
    private String formatarTituloDia(Sessao sessao) {
        Locale br = new Locale("pt", "BR");
        String diaDaSemana = sessao.getDataHora().getDayOfWeek().getDisplayName(TextStyle.FULL, br);
        String dataFormatada = FormatadorData.formatar(sessao.getDataHora(), "dd 'de' MMMM");
        return String.format("%s, %s", diaDaSemana.substring(0, 1).toUpperCase() + diaDaSemana.substring(1), dataFormatada);
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
     * Navega para a tela de seleção de assentos.
     */
    private void prosseguirParaSelecionarAssento() {
        if (this.sessaoEscolhida == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma data e turno para continuar.", "Sessão Não Selecionada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        // Agora, a TelaSelecionarAssento precisa ser refatorada para aceitar um objeto Sessao.
        TelaSelecionarAssento telaAssentos = new TelaSelecionarAssento(
                this.sessaoEscolhida, // Passa o objeto Sessao inteiro
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
        frame.setContentPane(new TelaSelecionarPeca(this.pecaServico, this.clienteServico, this.reservaServico, this.sessaoServico));
        frame.revalidate();
        frame.repaint();
    }
}