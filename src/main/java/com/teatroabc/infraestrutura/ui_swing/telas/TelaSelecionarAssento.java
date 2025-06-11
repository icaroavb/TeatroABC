package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAnimado;
import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAssento;
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.enums.StatusAssento;
import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorMoeda;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico;

import com.teatroabc.infraestrutura.config.ConfiguracaoPlantaTeatro;
import com.teatroabc.infraestrutura.config.SecaoConfig;
import com.teatroabc.infraestrutura.config.TeatroLayoutConfig;

import com.teatroabc.dominio.enums.CategoriaAssento;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Tela responsável por permitir ao usuário selecionar assentos para uma peça e turno específicos.
 * Atua como um Adaptador Primário, sendo "burra" em relação à lógica de layout.
 * Ela renderiza a planta do teatro dinamicamente com base nos dados recebidos do serviço
 * e nas configurações de layout centralizadas.
 */
public class TelaSelecionarAssento extends JPanel {
    private final Peca pecaSelecionada;
    private final Turno turnoEscolhido;

    // Serviços injetados
    private final IPecaServico pecaServico;
    private final IClienteServico clienteServico;
    private final IReservaServico reservaServico;

    // Estado da UI
    private final List<Assento> assentosDaPlanta; // Lista completa de assentos para o turno.
    private final List<Assento> assentosSelecionadosPeloUsuario;
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
        this.assentosSelecionadosPeloUsuario = new ArrayList<>();
        
        // Carrega a lista completa de assentos para este turno UMA VEZ.
        this.assentosDaPlanta = carregarAssentosParaTurno();

        configurarTelaVisual();
    }

    private List<Assento> carregarAssentosParaTurno() {
        try {
            // Usa o serviço para buscar os assentos, que por sua vez usa o repositório refatorado.
            List<Assento> assentos = pecaServico.buscarAssentosDaPecaPorTurno(pecaSelecionada.getId(), this.turnoEscolhido);
            return assentos != null ? assentos : Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Erro crítico ao carregar assentos: " + e.getMessage());
            e.printStackTrace();
            // Em uma aplicação real, poderia mostrar um JOptionPane de erro aqui.
            return Collections.emptyList();
        }
    }
    
    private void configurarTelaVisual() {
        setLayout(new BorderLayout()); 
        setBackground(Constantes.AZUL_ESCURO);

        JPanel containerPrincipal = new JPanel(new BorderLayout(0,15)); 
        containerPrincipal.setOpaque(false);
        containerPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        containerPrincipal.add(criarCabecalhoDaTela(), BorderLayout.NORTH);

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

        containerPrincipal.add(scrollTeatro, BorderLayout.CENTER); 
        
        add(containerPrincipal, BorderLayout.CENTER);
        add(criarRodapeDeControles(), BorderLayout.SOUTH);

        atualizarVisualizacaoDasSecoes();
        atualizarTotalDaCompra();
    }
    
    private JPanel criarCabecalhoDaTela() {
        // ... (Implementação como na sua versão)
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

    /**
     * CONSTRUÇÃO DINÂMICA DA PLANTA DO TEATRO.
     * Este método é a implementação final da refatoração.
     * Ele lê a configuração e os dados dos assentos para renderizar a UI.
     */
    private void atualizarVisualizacaoDasSecoes() {
        painelSecoes.removeAll();
        painelSecoes.setLayout(new BoxLayout(painelSecoes, BoxLayout.Y_AXIS));

        // 1. Agrupar os assentos recebidos do serviço por sua categoria.
        Map<CategoriaAssento, List<Assento>> assentosAgrupados =
            this.assentosDaPlanta.stream().collect(Collectors.groupingBy(Assento::getCategoria));

        // 2. Obter a configuração de layout da fonte única de verdade.
        TeatroLayoutConfig layoutConfig = ConfiguracaoPlantaTeatro.getLayout();

        // 3. Iterar sobre a CONFIGURAÇÃO para manter a ORDEM VISUAL correta.
        for (SecaoConfig secaoConfig : layoutConfig.getSecoes()) {
            CategoriaAssento categoriaAtual = secaoConfig.getCategoria();
            
            if (assentosAgrupados.containsKey(categoriaAtual)) {
                List<Assento> assentosParaRenderizar = assentosAgrupados.get(categoriaAtual);
                
                // Agrupa as unidades (ex: cada frisa ou camarote é uma "unidade")
                // Para seções contínuas como plateia, haverá apenas uma unidade.
                // A chave do mapa é o número da fileira/unidade.
                Map<Integer, List<Assento>> unidadesAgrupadas = assentosParaRenderizar.stream()
                        .collect(Collectors.groupingBy(Assento::getFileira));

                JPanel painelDaSecao = criarPainelDeSecao(secaoConfig, unidadesAgrupadas);
                
                painelSecoes.add(painelDaSecao);
                painelSecoes.add(Box.createVerticalStrut(25));
            }
        }

        painelSecoes.revalidate();
        painelSecoes.repaint();
    }

    /**
     * Novo método genérico para criar o painel de uma seção (ex: "FRISAS", "PLATEIA A").
     * Renderiza cada unidade (fileira, frisa, camarote) dentro da seção.
     */
    private JPanel criarPainelDeSecao(SecaoConfig config, Map<Integer, List<Assento>> unidadesDaSecao) {
        JPanel painelWrapper = new JPanel(new BorderLayout(5, 10));
        painelWrapper.setOpaque(false);

        JLabel lblTituloSecao = new JLabel(config.getNomeDaSecao(), SwingConstants.CENTER);
        lblTituloSecao.setFont(new Font("Arial", Font.BOLD, 18));
        lblTituloSecao.setForeground(Constantes.AMARELO);
        painelWrapper.add(lblTituloSecao, BorderLayout.NORTH);

        // Painel que conterá as unidades (fileiras, frisas, etc.)
        JPanel painelContainerUnidades = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        painelContainerUnidades.setOpaque(false);

        // Itera sobre as unidades agrupadas (ex: Fileira 1, Fileira 2, etc.)
        unidadesDaSecao.keySet().stream().sorted().forEach(numeroUnidade -> {
            List<Assento> assentosDaUnidade = unidadesDaSecao.get(numeroUnidade);
            
            JPanel painelDaUnidade = new JPanel(new GridLayout(1, assentosDaUnidade.size(), 5, 5));
            painelDaUnidade.setOpaque(false);

            for (Assento assento : assentosDaUnidade) {
                BotaoAssento botao = new BotaoAssento(assento);
                botao.addActionListener(e -> {
                    if (assento.getStatus() == StatusAssento.DISPONIVEL) {
                        assento.setStatus(StatusAssento.SELECIONADO);
                        assentosSelecionadosPeloUsuario.add(assento);
                    } else if (assento.getStatus() == StatusAssento.SELECIONADO) {
                        assento.setStatus(StatusAssento.DISPONIVEL);
                        assentosSelecionadosPeloUsuario.remove(assento);
                    }
                    botao.atualizarInteratividadeBaseadaNoStatus();
                    atualizarTotalDaCompra();
                });
                painelDaUnidade.add(botao);
            }
            painelContainerUnidades.add(painelDaUnidade);
        });
        
        painelWrapper.add(new JScrollPane(painelContainerUnidades), BorderLayout.CENTER);
        return painelWrapper;
    }


    private void atualizarTotalDaCompra() {
        // ... (Implementação como na sua versão)
        BigDecimal total = assentosSelecionadosPeloUsuario.stream()
                .map(Assento::getPreco)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        lblTotal.setText("TOTAL: " + FormatadorMoeda.formatar(total));
        btnConfirmar.setEnabled(!assentosSelecionadosPeloUsuario.isEmpty());
    }

    private void confirmarSelecaoEAvancar() {
        // ... (Implementação como na sua versão)
        if (!assentosSelecionadosPeloUsuario.isEmpty()) {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            TelaInformarCPF telaInformarCPF = new TelaInformarCPF(
                false, 
                this.pecaSelecionada,
                new ArrayList<>(this.assentosSelecionadosPeloUsuario),
                this.clienteServico,
                this.pecaServico,
                this.reservaServico
            );
            telaInformarCPF.setTurnoSelecionado(this.turnoEscolhido);
            
            frame.setContentPane(telaInformarCPF);
            frame.revalidate();
            frame.repaint();
        }
    }

    private void voltarParaSelecaoDeTurno() {
        // ... (Implementação como na sua versão)
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaSelecionarTurno(this.pecaSelecionada, this.pecaServico, this.clienteServico, this.reservaServico));
        frame.revalidate();
        frame.repaint();
    }
    
    // --- MÉTODOS VISUAIS AUXILIARES (sem alteração de lógica) ---
    private JPanel criarLegendaVisual() {
        // ... (Implementação como na sua versão)
        JPanel legenda = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        legenda.setOpaque(false);
        adicionarItemLegenda(legenda, "Disponível", Constantes.AZUL_CLARO);
        adicionarItemLegenda(legenda, "Ocupado", Constantes.BEGE);
        adicionarItemLegenda(legenda, "Selecionado", Constantes.VERDE);
        return legenda;
    }

    private void adicionarItemLegenda(JPanel painel, String texto, Color cor) {
        // ... (Implementação como na sua versão)
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        item.setOpaque(false);
        // ...
        JLabel label = new JLabel(texto);
        label.setForeground(Color.WHITE);
        item.add(new JLabel(new ImageIcon())); // Placeholder for circle
        item.add(label);
        painel.add(item);
    }
    
    private JPanel criarRodapeDeControles() {
        // ... (Implementação como na sua versão)
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
}