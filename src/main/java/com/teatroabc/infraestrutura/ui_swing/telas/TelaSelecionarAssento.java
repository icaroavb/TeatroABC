// Arquivo: infraestrutura/ui_swing/telas/TelaSelecionarAssento.java
package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico;
import com.teatroabc.aplicacao.interfaces.ISessaoServico;
import com.teatroabc.dominio.enums.CategoriaAssento;
import com.teatroabc.dominio.enums.StatusAssento;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.modelos.Sessao;
import com.teatroabc.infraestrutura.config.Alinhamento;
import com.teatroabc.infraestrutura.config.ConfiguracaoPlantaTeatro;
import com.teatroabc.infraestrutura.config.SecaoConfig;
import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAnimado;
import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAssento;
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorMoeda;

import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 * Tela responsável por permitir ao usuário selecionar assentos para uma Sessão específica.
 * Renderiza a planta do teatro dinamicamente com base na configuração centralizada
 * e na disponibilidade de assentos para a sessão escolhida.
 * REFATORADO: Utiliza ISessaoServico para buscar os assentos.
 */
public class TelaSelecionarAssento extends JPanel {
    // Contexto da seleção
    private final Sessao sessaoEscolhida;

    // Serviços injetados
    private final IPecaServico pecaServico;
    private final IClienteServico clienteServico;
    private final IReservaServico reservaServico;
    private final ISessaoServico sessaoServico;

    // Estado da UI
    private final List<Assento> assentosDaPlanta;
    private final List<Assento> assentosSelecionadosPeloUsuario;
    private JLabel lblTotal;
    private BotaoAnimado btnConfirmar;
    private JPanel painelSecoes;

    public TelaSelecionarAssento(Sessao sessao, IPecaServico pecaServico, IClienteServico clienteServico, IReservaServico reservaServico, ISessaoServico sessaoServico) {
        if (sessao == null) throw new IllegalArgumentException("Sessao não pode ser nula.");
        if (pecaServico == null || clienteServico == null || reservaServico == null || sessaoServico == null) {
            throw new IllegalArgumentException("Serviços não podem ser nulos.");
        }
        this.sessaoEscolhida = sessao;
        this.pecaServico = pecaServico;
        this.clienteServico = clienteServico;
        this.reservaServico = reservaServico;
        this.sessaoServico = sessaoServico;
        this.assentosSelecionadosPeloUsuario = new ArrayList<>();
        
        // A carga dos assentos agora usa o serviço correto.
        this.assentosDaPlanta = carregarAssentosParaSessao();
        
        configurarTelaVisual();
    }

    private List<Assento> carregarAssentosParaSessao() {
        try {
            // REFATORADO: Chama o serviço correto (SessaoServico) para buscar os assentos.
            return this.sessaoServico.buscarAssentosPorSessao(this.sessaoEscolhida);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erro ao carregar os assentos para esta sessão.\n" + e.getMessage(),
                "Erro de Sistema", JOptionPane.ERROR_MESSAGE);
            return Collections.emptyList();
        }
    }

    private void configurarTelaVisual() {
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(Constantes.AZUL_ESCURO);

        JPanel painelConteudoPrincipal = new JPanel(new BorderLayout(0, 15));
        painelConteudoPrincipal.setBackground(Constantes.AZUL_ESCURO);
        painelConteudoPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        painelConteudoPrincipal.add(criarCabecalhoDaTela(), BorderLayout.NORTH);

        JPanel painelCentral = new JPanel();
        painelCentral.setLayout(new BoxLayout(painelCentral, BoxLayout.Y_AXIS));
        painelCentral.setOpaque(false);

        JPanel palco = new JPanel();
        palco.setBackground(Constantes.BEGE.darker());
        palco.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel lblPalco = new JLabel("PALCO");
        lblPalco.setFont(new Font("Arial", Font.BOLD, 20));
        lblPalco.setForeground(Color.WHITE);
        palco.add(lblPalco);
        painelCentral.add(palco);
        painelCentral.add(Box.createVerticalStrut(20));

        this.painelSecoes = new JPanel(new GridBagLayout());
        this.painelSecoes.setOpaque(false);
        painelCentral.add(this.painelSecoes);
        
        painelConteudoPrincipal.add(painelCentral, BorderLayout.CENTER);
        painelConteudoPrincipal.add(criarLegendaVisual(), BorderLayout.SOUTH);
        
        scrollPane.setViewportView(painelConteudoPrincipal);
        
        add(scrollPane, BorderLayout.CENTER);
        add(criarRodapeDeControles(), BorderLayout.SOUTH);

        atualizarVisualizacaoDasSecoes();
        atualizarTotalDaCompra();
    }
    
    private void atualizarVisualizacaoDasSecoes() {
        painelSecoes.removeAll();

        Map<CategoriaAssento, List<Assento>> assentosPorCategoria = this.assentosDaPlanta.stream()
            .collect(Collectors.groupingBy(Assento::getCategoria));

        JPanel painelEsquerda = createColumnPanel();
        JPanel painelCentro = createColumnPanel();
        JPanel painelDireita = createColumnPanel();
        
        ConfiguracaoPlantaTeatro.getLayout().getSecoes().forEach(secaoConfig -> {
            List<Assento> assentos = assentosPorCategoria.getOrDefault(secaoConfig.getCategoria(), Collections.emptyList());
            
            if (secaoConfig.getAlinhamento() == Alinhamento.CENTRO && secaoConfig.getCategoria() != CategoriaAssento.FRISA && !assentos.isEmpty()) {
                painelCentro.add(criarPainelDeSecao(secaoConfig, assentos));
                painelCentro.add(Box.createVerticalStrut(20));
            }
        });
        
        List<Assento> todasAsFrisas = assentosPorCategoria.getOrDefault(CategoriaAssento.FRISA, Collections.emptyList());
        if (!todasAsFrisas.isEmpty()) {
            todasAsFrisas.sort(Comparator.comparing(Assento::getCodigo));
            int metade = todasAsFrisas.size() / 2;
            criarBlocosDeFrisa(painelEsquerda, todasAsFrisas.subList(0, metade), "Frisas Ímpares");
            criarBlocosDeFrisa(painelDireita, todasAsFrisas.subList(metade, todasAsFrisas.size()), "Frisas Pares");
        }
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.VERTICAL;

        gbc.gridx = 0; gbc.weightx = 0.25; painelSecoes.add(painelEsquerda, gbc);
        gbc.gridx = 1; gbc.weightx = 0.5;  painelSecoes.add(painelCentro, gbc);
        gbc.gridx = 2; gbc.weightx = 0.25; painelSecoes.add(painelDireita, gbc);

        painelSecoes.revalidate();
        painelSecoes.repaint();
    }
    
    private void criarBlocosDeFrisa(JPanel painelColuna, List<Assento> frisas, String titulo) {
        if (frisas.isEmpty()) return;

        JPanel painelWrapper = new JPanel();
        painelWrapper.setLayout(new BoxLayout(painelWrapper, BoxLayout.Y_AXIS));
        painelWrapper.setOpaque(false);
        painelWrapper.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80,100,120)), titulo, 
            TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), Constantes.AMARELO));

        Map<Integer, List<Assento>> frisasAgrupadas = frisas.stream()
            .collect(Collectors.groupingBy(Assento::getFileira));
        
        frisasAgrupadas.keySet().stream().sorted().forEach(numFrisa -> {
            List<Assento> assentosDaFrisa = frisasAgrupadas.get(numFrisa);
            JPanel painelDaFrisa = new JPanel(new GridLayout(0, 2, 4, 4));
            painelDaFrisa.setOpaque(false);

            for (Assento assento : assentosDaFrisa) {
                BotaoAssento botao = new BotaoAssento(assento);
                botao.addActionListener(e -> onAssentoClicked(assento, botao));
                
                JPanel wrapperDoBotao = new JPanel(new GridBagLayout());
                wrapperDoBotao.setOpaque(false);
                wrapperDoBotao.add(botao);

                painelDaFrisa.add(wrapperDoBotao);
            }
            painelWrapper.add(painelDaFrisa);
            if (frisasAgrupadas.size() > 1) {
                painelWrapper.add(Box.createVerticalStrut(15));
            }
        });
        painelColuna.add(painelWrapper);
    }

    private JPanel criarPainelDeSecao(SecaoConfig config, List<Assento> assentosDaSecao) {
        JPanel painelWrapper = new JPanel(new BorderLayout(5, 10));
        painelWrapper.setOpaque(false);
        painelWrapper.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80,100,120)), config.getNomeDaSecao(), 
            TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), Constantes.AMARELO));
        
        JPanel painelGridAssentos = new JPanel(new GridLayout(
            config.getNumeroDeFileiras(), config.getAssentosPorFileira(), 5, 5
        ));
        painelGridAssentos.setOpaque(false);

        for (Assento assento : assentosDaSecao) {
            BotaoAssento botao = new BotaoAssento(assento);
            botao.addActionListener(e -> onAssentoClicked(assento, botao));

            JPanel wrapperDoBotao = new JPanel(new GridBagLayout());
            wrapperDoBotao.setOpaque(false);
            wrapperDoBotao.add(botao);

            painelGridAssentos.add(wrapperDoBotao);
        }
        
        painelWrapper.add(painelGridAssentos, BorderLayout.CENTER);
        return painelWrapper;
    }

    private void onAssentoClicked(Assento assento, BotaoAssento botao) {
        if (assento.getStatus() == StatusAssento.DISPONIVEL) {
            assento.setStatus(StatusAssento.SELECIONADO);
            assentosSelecionadosPeloUsuario.add(assento);
        } else if (assento.getStatus() == StatusAssento.SELECIONADO) {
            assento.setStatus(StatusAssento.DISPONIVEL);
            assentosSelecionadosPeloUsuario.remove(assento);
        }
        botao.atualizarInteratividadeBaseadaNoStatus();
        atualizarTotalDaCompra();
    }
    
    private JPanel createColumnPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }
    
    private JPanel criarCabecalhoDaTela() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JLabel titulo = new JLabel("ESCOLHA SEU(S) ASSENTO(S) PARA " + sessaoEscolhida.getTurno().getNome().toUpperCase(), SwingConstants.CENTER);
        titulo.setFont(Constantes.FONTE_SUBTITULO);
        titulo.setForeground(Color.WHITE);
        titulo.setOpaque(true);
        titulo.setBackground(Constantes.LARANJA);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        cabecalho.add(titulo, BorderLayout.CENTER);
        
        LogoTeatro logo = new LogoTeatro();
        logo.setPreferredSize(new Dimension(200, 60));
        cabecalho.add(logo, BorderLayout.EAST);
        return cabecalho;
    }

    private JPanel criarLegendaVisual() {
        JPanel legenda = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        legenda.setOpaque(false);
        legenda.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Constantes.AMARELO),
            "LEGENDA",
            TitledBorder.CENTER, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14), Constantes.AMARELO
        ));

        adicionarItemLegenda(legenda, "Disponível", Constantes.AZUL_CLARO);
        adicionarItemLegenda(legenda, "Ocupado", Constantes.BEGE);
        adicionarItemLegenda(legenda, "Selecionado", Constantes.VERDE);
        return legenda;
    }

    private void adicionarItemLegenda(JPanel painel, String texto, Color cor) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        item.setOpaque(false);
        
        JPanel circulo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(cor);
                g2d.fillOval(0, 0, 18, 18);
                g2d.setColor(cor.darker());
                g2d.drawOval(0,0,18,18);
                g2d.dispose();
            }
        };
        circulo.setPreferredSize(new Dimension(18, 18));
        circulo.setOpaque(false);
        
        JLabel label = new JLabel(texto);
        label.setForeground(Color.WHITE);
        label.setFont(Constantes.FONTE_LABEL);
        
        item.add(circulo);
        item.add(label);
        painel.add(item);
    }
    
    private JPanel criarRodapeDeControles() {
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBackground(Constantes.AZUL_ESCURO);
        rodape.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(80,100,120)),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        JButton btnVoltarUI = new JButton("<< Voltar para Seleção de Sessão");
        btnVoltarUI.setFont(new Font("Arial", Font.PLAIN, 16));
        btnVoltarUI.setForeground(Constantes.AZUL_CLARO);
        btnVoltarUI.setContentAreaFilled(false);
        btnVoltarUI.setBorderPainted(false);
        btnVoltarUI.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltarUI.addActionListener(e -> voltarParaSelecaoDeSessao());

        JPanel painelDireita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        painelDireita.setOpaque(false);

        JPanel painelTotal = new JPanel();
        painelTotal.setBackground(new Color(52, 73, 94));
        painelTotal.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        lblTotal = new JLabel("TOTAL: R$ 0,00");
        lblTotal.setForeground(Color.WHITE);
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));
        painelTotal.add(lblTotal);

        btnConfirmar = new BotaoAnimado("CONFIRMAR ASSENTOS",
                Constantes.LARANJA, Constantes.AMARELO.darker(), new Dimension(220, 60));
        btnConfirmar.setFont(new Font("Arial", Font.BOLD, 16));
        btnConfirmar.setEnabled(false);
        btnConfirmar.addActionListener(e -> confirmarSelecaoEAvancar());

        painelDireita.add(painelTotal);
        painelDireita.add(btnConfirmar);

        rodape.add(btnVoltarUI, BorderLayout.WEST);
        rodape.add(painelDireita, BorderLayout.EAST);

        return rodape;
    }

    private void atualizarTotalDaCompra() {
        BigDecimal total = assentosSelecionadosPeloUsuario.stream()
                .map(Assento::getPreco)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        lblTotal.setText("TOTAL: " + FormatadorMoeda.formatar(total));
        btnConfirmar.setEnabled(!assentosSelecionadosPeloUsuario.isEmpty());
    }

    private void confirmarSelecaoEAvancar() {
        if (assentosSelecionadosPeloUsuario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione ao menos um assento.", "Nenhum Assento Selecionado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaInformarCPF(
            false, this.sessaoEscolhida, new ArrayList<>(this.assentosSelecionadosPeloUsuario),
            this.clienteServico, this.pecaServico, this.reservaServico, this.sessaoServico
        ));
        frame.revalidate();
        frame.repaint();
    }

    private void voltarParaSelecaoDeSessao() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaSelecionarSessao(
            this.sessaoEscolhida.getPeca(), this.sessaoServico, 
            this.pecaServico, this.clienteServico, this.reservaServico
        ));
        frame.revalidate();
        frame.repaint();
    }
}
