package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAnimado;
import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAssento;
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.enums.CategoriaAssento;
import com.teatroabc.dominio.enums.StatusAssento;
import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorMoeda;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TelaSelecionarAssento extends JPanel {
    private final Peca pecaSelecionada;
    private final Turno turnoEscolhido;
    private List<Assento> assentosSelecionadosNaTela;
    private List<Assento> assentosDisponiveisParaEsteTurno;

    private final IPecaServico pecaServico;
    private final IClienteServico clienteServico;
    private final IReservaServico reservaServico;

    private JLabel lblTotal;
    private BotaoAnimado btnConfirmar;
    private JPanel painelSecoes;

    public TelaSelecionarAssento(Peca peca, Turno turno,
                                 IPecaServico pecaServico, IClienteServico clienteServico, IReservaServico reservaServico) {
        if (peca == null || turno == null || pecaServico == null || clienteServico == null || reservaServico == null) {
            throw new IllegalArgumentException("Peca, Turno e Serviços não podem ser nulos em TelaSelecionarAssento.");
        }
        this.pecaSelecionada = peca;
        this.turnoEscolhido = turno;
        this.pecaServico = pecaServico;
        this.clienteServico = clienteServico;
        this.reservaServico = reservaServico;
        this.assentosSelecionadosNaTela = new ArrayList<>();
        this.assentosDisponiveisParaEsteTurno = new ArrayList<>();
        carregarAssentosParaTurnoEscolhido();
        configurarTelaVisual();
    }

    private void carregarAssentosParaTurnoEscolhido() {
        this.assentosSelecionadosNaTela.clear();
        try {
            this.assentosDisponiveisParaEsteTurno = pecaServico.buscarAssentosDaPecaPorTurno(pecaSelecionada.getId(), this.turnoEscolhido);
            if (this.assentosDisponiveisParaEsteTurno != null) {
                this.assentosDisponiveisParaEsteTurno.forEach(assento -> {
                    if (assento.getStatus() == StatusAssento.SELECIONADO) {
                        assento.setStatus(StatusAssento.DISPONIVEL);
                    }
                });
            } else {
                this.assentosDisponiveisParaEsteTurno = Collections.emptyList();
            }
        } catch (Exception e) {
            System.err.println("Erro crítico ao carregar assentos para o turno " + this.turnoEscolhido.getNome() + 
                               " da peça " + pecaSelecionada.getId() + ": " + e.getMessage());
            e.printStackTrace();
            this.assentosDisponiveisParaEsteTurno = Collections.emptyList();
        }
    }
    
    private void configurarTelaVisual() {
        setLayout(new BorderLayout()); 
        setBackground(Constantes.AZUL_ESCURO);

        JPanel containerPrincipalDaTela = new JPanel(new BorderLayout(0,15)); 
        containerPrincipalDaTela.setOpaque(false);
        containerPrincipalDaTela.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        containerPrincipalDaTela.add(criarCabecalhoDaTelaComTurno(), BorderLayout.NORTH);

        JPanel painelConteudoTeatro = new JPanel();
        painelConteudoTeatro.setLayout(new BoxLayout(painelConteudoTeatro, BoxLayout.Y_AXIS));
        painelConteudoTeatro.setOpaque(false); 
        painelConteudoTeatro.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel palco = new JPanel(); 
        palco.setBackground(Constantes.BEGE.darker());
        palco.setPreferredSize(new Dimension(600, 40)); 
        palco.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel lblPalco = new JLabel("PALCO");
        lblPalco.setFont(new Font("Arial", Font.BOLD, 20));
        lblPalco.setForeground(Color.WHITE);
        palco.add(lblPalco);
        painelConteudoTeatro.add(palco); 
        painelConteudoTeatro.add(Box.createVerticalStrut(20));

        this.painelSecoes = new JPanel(); 
        this.painelSecoes.setOpaque(false);
        this.painelSecoes.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelConteudoTeatro.add(this.painelSecoes); 
        painelConteudoTeatro.add(Box.createVerticalStrut(20));
        
        painelConteudoTeatro.add(criarLegendaVisual()); 
        painelConteudoTeatro.add(Box.createVerticalStrut(10));

        JScrollPane scrollTeatro = new JScrollPane(painelConteudoTeatro);
        scrollTeatro.setBorder(BorderFactory.createEmptyBorder());
        scrollTeatro.getViewport().setOpaque(false);
        scrollTeatro.setOpaque(false);
        scrollTeatro.getVerticalScrollBar().setUnitIncrement(16);
        scrollTeatro.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        containerPrincipalDaTela.add(scrollTeatro, BorderLayout.CENTER); 
        
        add(containerPrincipalDaTela, BorderLayout.CENTER);
        add(criarRodapeDeControles(), BorderLayout.SOUTH);

        atualizarVisualizacaoSecoesDeAssentos();
        atualizarTotalDaCompra();
    }

    private JPanel criarCabecalhoDaTelaComTurno() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JLabel titulo = new JLabel("ESCOLHA SEU(S) ASSENTO(S) PARA " + turnoEscolhido.getNome().toUpperCase(), SwingConstants.CENTER);
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
                g2d.fillOval(0, 0, 20, 20);
                g2d.setColor(cor.darker());
                g2d.drawOval(0,0,20,20);
                g2d.dispose();
            }
        };
        circulo.setPreferredSize(new Dimension(20, 20));
        circulo.setOpaque(false);
        JLabel label = new JLabel(texto);
        label.setForeground(Color.WHITE);
        label.setFont(Constantes.FONTE_LABEL);
        item.add(circulo);
        item.add(label);
        painel.add(item);
    }
    
    private void atualizarVisualizacaoSecoesDeAssentos() {
        // ... (IMPLEMENTAÇÃO COMPLETA E DETALHADA DA LÓGICA DE RENDERIZAÇÃO DA PLANTA DO TEATRO - O GRANDE TODO)
        // Este método precisa ser cuidadosamente implementado por você e seu grupo.
        // O código abaixo é um ESBOÇO MUITO SIMPLIFICADO e precisa ser adaptado.
        if (painelSecoes == null) { return; }
        painelSecoes.removeAll();
        painelSecoes.setLayout(new BoxLayout(painelSecoes, BoxLayout.Y_AXIS));
        painelSecoes.add(Box.createVerticalStrut(10));

        if (this.assentosDisponiveisParaEsteTurno.isEmpty()) {
            JLabel lblMsg = new JLabel("<html><center>Não há assentos configurados para este turno<br>ou ocorreu um erro ao carregá-los.</center></html>");
            lblMsg.setFont(Constantes.FONTE_TEXTO);
            lblMsg.setForeground(Color.ORANGE);
            lblMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
            painelSecoes.add(lblMsg);
        } else {
            Map<CategoriaAssento, List<Assento>> assentosAgrupados =
                this.assentosDisponiveisParaEsteTurno.stream().collect(Collectors.groupingBy(Assento::getCategoria));
            
            CategoriaAssento[] ordemDeExibicao = {
                CategoriaAssento.PLATEIA_A, CategoriaAssento.PLATEIA_B,
                CategoriaAssento.FRISA, CategoriaAssento.CAMAROTE,
                CategoriaAssento.BALCAO_NOBRE
            };

            for (CategoriaAssento categoria : ordemDeExibicao) {
                if (assentosAgrupados.containsKey(categoria)) {
                    // Esta chamada é um exemplo. A lógica real será mais complexa.
                    criarEAdicionarSecaoParaCategoria(categoria, assentosAgrupados.get(categoria));
                    painelSecoes.add(Box.createVerticalStrut(10));
                }
            }
        }
        painelSecoes.revalidate();
        painelSecoes.repaint();
    }
    
    private void criarEAdicionarSecaoParaCategoria(CategoriaAssento categoria, List<Assento> assentosDaCategoria) {
        // LÓGICA DE PLACEHOLDER - SUBSTITUIR PELA IMPLEMENTAÇÃO CORRETA DA PLANTA
        String nomeExibicao = categoria.getNome();
        JPanel painelDaSecao;
        switch (categoria) {
            case PLATEIA_A: painelDaSecao = criarPainelDeSecaoComGradeSimples(nomeExibicao, 5, 5, assentosDaCategoria); break;
            case PLATEIA_B: painelDaSecao = criarPainelDeSecaoComGradeSimples(nomeExibicao, 10, 10, assentosDaCategoria); break;
            case FRISA: painelDaSecao = criarPainelParaMultiplasUnidades(nomeExibicao, assentosDaCategoria, 5, "Frisa"); break;
            case CAMAROTE: painelDaSecao = criarPainelParaMultiplasUnidades(nomeExibicao, assentosDaCategoria, 10, "Camarote"); break;
            case BALCAO_NOBRE: painelDaSecao = criarPainelDeSecaoComGradeSimples(nomeExibicao, 5, 10, assentosDaCategoria); break;
            default: painelDaSecao = new JPanel(); painelDaSecao.add(new JLabel("Layout para " + nomeExibicao + " pendente.")); break;
        }
        if (painelDaSecao != null) { painelSecoes.add(painelDaSecao); }
    }

    private JPanel criarPainelParaMultiplasUnidades(String nomeTituloSecao, List<Assento> assentosDaCategoria, int assentosPorUnidade, String prefixoUnidadeNome) {
        // LÓGICA DE PLACEHOLDER - SUBSTITUIR
        JPanel painelWrapper = new JPanel(new BorderLayout()); painelWrapper.setOpaque(false);
        JLabel lblTituloSecao = new JLabel(nomeTituloSecao, SwingConstants.CENTER); /*...*/ painelWrapper.add(lblTituloSecao, BorderLayout.NORTH);
        JPanel painelContainerUnidades = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); painelContainerUnidades.setOpaque(false);
        // Loop para criar painel para cada unidade (frisa/camarote)
        // Ex: for (int i = 0; ...) painelContainerUnidades.add(criarPainelDeSecaoComGradeSimples(...));
        if (assentosDaCategoria.isEmpty()) painelContainerUnidades.add(new JLabel("Sem " + prefixoUnidadeNome.toLowerCase() + "s disponíveis."));
        else painelContainerUnidades.add(new JLabel( prefixoUnidadeNome + "s aqui (implementar layout)..."));
        painelWrapper.add(painelContainerUnidades, BorderLayout.CENTER);
        return painelWrapper;
    }
    
    private JPanel criarPainelDeSecaoComGradeSimples(String nomeExibicao, int fileirasVisuais, int assentosPorFileiraVisual, List<Assento> assentosParaEstaSecao) {
        // LÓGICA DE PLACEHOLDER - SUBSTITUIR
        // A implementação com GridLayout e BotaoAssento + ActionListener está correta conceitualmente.
        // A chave é popular corretamente.
        JPanel secaoPanel = new JPanel(new BorderLayout(5, 5)); /*...*/
        JLabel lblNome = new JLabel(nomeExibicao, SwingConstants.CENTER); /*...*/ secaoPanel.add(lblNome, BorderLayout.NORTH);
        JPanel containerAssentos = new JPanel(new GridLayout(fileirasVisuais, assentosPorFileiraVisual, 3, 3)); containerAssentos.setOpaque(false);
        int assentoIndex = 0;
        for (int f = 0; f < fileirasVisuais; f++) {
            for (int a = 0; a < assentosPorFileiraVisual; a++) {
                if (assentoIndex < assentosParaEstaSecao.size()) {
                    Assento assento = assentosParaEstaSecao.get(assentoIndex++);
                    BotaoAssento botao = new BotaoAssento(assento);
                    botao.addActionListener(e -> {
                        if (assento.getStatus() == StatusAssento.SELECIONADO) {
                            assento.setStatus(StatusAssento.DISPONIVEL);
                            assentosSelecionadosNaTela.remove(assento);
                        } else if (assento.getStatus() == StatusAssento.DISPONIVEL) {
                            assentosSelecionadosNaTela.add(assento);
                            assento.setStatus(StatusAssento.SELECIONADO);
                        }
                        ((JButton)e.getSource()).repaint();
                        atualizarTotalDaCompra();
                    });
                    containerAssentos.add(botao);
                } else {
                    JPanel placeholder = new JPanel(); placeholder.setOpaque(false);
                    placeholder.setPreferredSize(new Dimension(30,30)); containerAssentos.add(placeholder);
                }
            }
        }
        secaoPanel.add(containerAssentos, BorderLayout.CENTER);
        return secaoPanel;
    }

    private JPanel criarRodapeDeControles() {
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setOpaque(false);
        rodape.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        JButton btnVoltarUI = new JButton("<< Voltar para Seleção de Turno");
        btnVoltarUI.setFont(new Font("Arial", Font.PLAIN, 16));
        btnVoltarUI.setForeground(Constantes.AZUL_CLARO);
        btnVoltarUI.setOpaque(false);
        btnVoltarUI.setContentAreaFilled(false);
        btnVoltarUI.setBorderPainted(false);
        btnVoltarUI.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltarUI.addActionListener(e -> voltarParaSelecaoDeTurno());

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
        BigDecimal total = BigDecimal.ZERO;
        for (Assento assento : assentosSelecionadosNaTela) {
            total = total.add(assento.getPreco());
        }
        lblTotal.setText("TOTAL: " + FormatadorMoeda.formatar(total)); // FormatadorMoeda aceita BigDecimal
        btnConfirmar.setEnabled(!assentosSelecionadosNaTela.isEmpty());
    }

    private void confirmarSelecaoEAvancar() {
        if (!assentosSelecionadosNaTela.isEmpty()) {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            TelaInformarCPF telaInformarCPF = new TelaInformarCPF(
                false, 
                this.pecaSelecionada,
                new ArrayList<>(this.assentosSelecionadosNaTela),
                this.clienteServico,
                this.pecaServico,
                this.reservaServico
            );
            telaInformarCPF.setTurnoSelecionado(this.turnoEscolhido);
            
            frame.setContentPane(telaInformarCPF);
            frame.revalidate();
            frame.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecione ao menos um assento.", "Nenhum Assento Selecionado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void voltarParaSelecaoDeTurno() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaSelecionarTurno(this.pecaSelecionada, this.pecaServico, this.clienteServico, this.reservaServico));
        frame.revalidate();
        frame.repaint();
    }
}