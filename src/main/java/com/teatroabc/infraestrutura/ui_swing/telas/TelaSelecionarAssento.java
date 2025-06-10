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
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Tela responsável por permitir ao usuário selecionar assentos para uma peça e turno específicos.
 * Interage com serviços para buscar a disponibilidade de assentos e, ao final,
 * encaminha para o fluxo de informação de CPF ou cadastro.
 * Atua como um Adaptador Primário na arquitetura hexagonal.
 */
public class TelaSelecionarAssento extends JPanel {
    private final Peca pecaSelecionada;
    private List<Assento> assentosSelecionadosNaTela; // Lista de assentos que o usuário marcou como SELECIONADO
    
    // Cache para armazenar a lista de todos os assentos (com seus status) para cada turno,
    // evitando recarregar do serviço a cada mudança de turno visual.
    private Map<Turno, List<Assento>> cacheAssentosPorTurno;
    
    private Turno turnoAtualSelecionado; // O turno atualmente visualizado e para o qual os assentos estão sendo selecionados

    // Serviços injetados via construtor
    private final IPecaServico pecaServico;
    private final IClienteServico clienteServico;
    private final IReservaServico reservaServico;

    // Componentes da UI
    private JLabel lblTotal;
    private BotaoAnimado btnConfirmar;
    private JPanel painelSecoes; // Painel principal que conterá os painéis de cada seção de assentos
    private ButtonGroup grupoTurnosRadios;

    /**
     * Construtor da TelaSelecionarAssento.
     *
     * @param peca A peça para a qual os assentos serão selecionados.
     * @param pecaServico Serviço para buscar informações de peças e assentos.
     * @param clienteServico Serviço para operações de cliente (para repassar na navegação).
     * @param reservaServico Serviço para operações de reserva (para repassar na navegação).
     * @throws IllegalArgumentException se Peca ou qualquer um dos serviços injetados for nulo.
     */
    public TelaSelecionarAssento(Peca peca, IPecaServico pecaServico, IClienteServico clienteServico, IReservaServico reservaServico) {
        if (peca == null) throw new IllegalArgumentException("Peca não pode ser nula para TelaSelecionarAssento.");
        if (pecaServico == null) throw new IllegalArgumentException("IPecaServico não pode ser nulo.");
        if (clienteServico == null) throw new IllegalArgumentException("IClienteServico não pode ser nulo.");
        if (reservaServico == null) throw new IllegalArgumentException("IReservaServico não pode ser nulo.");
        
        this.pecaSelecionada = peca;
        this.pecaServico = pecaServico;
        this.clienteServico = clienteServico;
        this.reservaServico = reservaServico;

        this.assentosSelecionadosNaTela = new ArrayList<>();
        this.turnoAtualSelecionado = Turno.NOITE; // Define um turno padrão inicial
        this.cacheAssentosPorTurno = new HashMap<>();
        
        // Carrega os assentos para o turno padrão inicial.
        // A lista de assentos retornada pelo serviço já contém o status (DISPONIVEL, OCUPADO)
        // vindo do AssentoRepositorio.
        carregarEConfigurarAssentosParaTurno(this.turnoAtualSelecionado);
        
        // Monta a interface gráfica
        configurarTelaVisual();
    }

    /**
     * Carrega os assentos para o turno especificado usando o serviço {@link IPecaServico}.
     * Se os assentos para o turno já estiverem em cache, usa a versão em cache.
     * Após carregar ou obter do cache, reseta quaisquer seleções anteriores.
     *
     * @param turno O turno para o qual os assentos devem ser carregados/configurados.
     */
    private void carregarEConfigurarAssentosParaTurno(Turno turno) {
        if (!cacheAssentosPorTurno.containsKey(turno)) {
            // System.out.println("TelaSelecionarAssento: Carregando assentos para peça " + pecaSelecionada.getId() + ", turno " + turno.name() + " via serviço.");
            List<Assento> assentosDoServico = pecaServico.buscarAssentosDaPecaPorTurno(pecaSelecionada.getId(), turno);
            // Armazena uma cópia para evitar modificações na lista original do cache por referência externa
            cacheAssentosPorTurno.put(turno, new ArrayList<>(assentosDoServico));
        } else {
            // System.out.println("TelaSelecionarAssento: Usando assentos cacheados para turno " + turno.name());
        }
        // Limpa a lista de assentos selecionados na UI e reseta o status 'SELECIONADO' dos objetos Assento no cache.
        resetarSelecaoDeAssentos();
    }
    
    /**
     * Limpa a lista {@link #assentosSelecionadosNaTela} e percorre todos os assentos
     * em todos os turnos cacheados, revertendo qualquer assento com status {@link StatusAssento#SELECIONADO}
     * para {@link StatusAssento#DISPONIVEL}. Isso garante que a seleção seja limpa ao mudar de turno
     * e que o estado visual e lógico dos assentos seja consistente.
     */
    private void resetarSelecaoDeAssentos() {
        this.assentosSelecionadosNaTela.clear(); // Limpa a lista de referências de assentos selecionados pela UI
        
        // Reseta o STATUS dos objetos Assento no cache que estavam como SELECIONADO
        this.cacheAssentosPorTurno.values().forEach(listaDeAssentosNoTurno -> 
            listaDeAssentosNoTurno.forEach(assento -> {
                if (assento.getStatus() == StatusAssento.SELECIONADO) {
                    assento.setStatus(StatusAssento.DISPONIVEL);
                }
            })
        );
    }

    /**
     * Configura os componentes visuais principais da tela de seleção de assentos.
     */
    private void configurarTelaVisual() {
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        JPanel containerPrincipal = new JPanel(new BorderLayout(20, 0)); // Adiciona espaçamento horizontal
        containerPrincipal.setBackground(Constantes.AZUL_ESCURO);
        containerPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Margens gerais
        
        containerPrincipal.add(criarCabecalhoDaTela(), BorderLayout.NORTH); // Método renomeado

        // Painel central para a representação do teatro (palco, assentos, legenda)
        JPanel painelCentralTeatro = new JPanel();
        painelCentralTeatro.setLayout(new BoxLayout(painelCentralTeatro, BoxLayout.Y_AXIS));
        painelCentralTeatro.setOpaque(false); // Para que o fundo do containerPrincipal apareça

        // Palco
        JPanel palco = new JPanel(); 
        palco.setBackground(Constantes.BEGE.darker()); // Cor um pouco mais escura para o palco
        palco.setPreferredSize(new Dimension(600, 40)); 
        palco.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel lblPalco = new JLabel("PALCO");
        lblPalco.setFont(new Font("Arial", Font.BOLD, 20));
        lblPalco.setForeground(Color.WHITE);
        palco.add(lblPalco);
        painelCentralTeatro.add(palco);
        painelCentralTeatro.add(Box.createVerticalStrut(20));

        // Painel que conterá as diversas seções de assentos
        this.painelSecoes = new JPanel(); 
        this.painelSecoes.setOpaque(false);
        // O layout de painelSecoes (ex: BoxLayout vertical) será definido em atualizarVisualizacaoSecoesDeAssentos
        painelCentralTeatro.add(this.painelSecoes);
        painelCentralTeatro.add(Box.createVerticalStrut(20));
        
        painelCentralTeatro.add(criarLegendaVisual()); // Método renomeado

        // Adiciona o painelCentralTeatro (com palco, seções, legenda) a um JScrollPane
        JScrollPane scrollTeatro = new JScrollPane(painelCentralTeatro);
        scrollTeatro.setBorder(BorderFactory.createEmptyBorder()); // Sem borda no scrollpane
        scrollTeatro.getViewport().setOpaque(false);
        scrollTeatro.setOpaque(false);
        scrollTeatro.getVerticalScrollBar().setUnitIncrement(16);
        scrollTeatro.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);


        containerPrincipal.add(scrollTeatro, BorderLayout.CENTER);
        containerPrincipal.add(criarPainelDeSelecaoDeTurnos(), BorderLayout.EAST); // Método renomeado
        
        add(containerPrincipal, BorderLayout.CENTER);
        add(criarRodapeDeControles(), BorderLayout.SOUTH); // Método renomeado

        // Após a UI ser configurada, popula as seções de assentos com base no turno inicial
        atualizarVisualizacaoSecoesDeAssentos();
        atualizarTotalDaCompra(); // Método renomeado
    }
    
    private JPanel criarCabecalhoDaTela() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0)); // Ajuste de margens

        JLabel titulo = new JLabel("ESCOLHA SEU(S) ASSENTO(S)"); // Título mais direto
        titulo.setFont(Constantes.FONTE_SUBTITULO); // Usando subtitulo para não ser tão grande
        titulo.setForeground(Color.WHITE);
        titulo.setOpaque(true);
        titulo.setBackground(Constantes.LARANJA);
        titulo.setHorizontalAlignment(JLabel.CENTER);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        cabecalho.add(titulo, BorderLayout.CENTER);
        
        LogoTeatro logo = new LogoTeatro();
        logo.setPreferredSize(new Dimension(200, 60)); // Tamanho menor para o logo no cabeçalho
        cabecalho.add(logo, BorderLayout.EAST);
        return cabecalho;
    }
    
    private JPanel criarPainelDeSelecaoDeTurnos() {
        // ... (implementação como na versão anterior, usando grupoTurnosRadios) ...
        // ... chamando criarRadioTurno(Turno turnoOpcao) ...
        JPanel painelTurnosUI = new JPanel(); /* ... */ return painelTurnosUI;
    }

    
    //private JRadioButton criarRadioTurno(Turno turnoOpcao) {
        // ... (implementação como na versão anterior) ...
        // O ActionListener deve chamar:
        // this.turnoAtualSelecionado = turnoOpcao;
        // carregarEConfigurarAssentosParaTurno(this.turnoAtualSelecionado);
        // atualizarVisualizacaoSecoesDeAssentos();
        // atualizarTotalDaCompra();
        //JRadioButton radio = new JRadioButton(); /* ... */ return radio;
    //}

    private JPanel criarLegendaVisual() {
        // ... (implementação como na versão anterior, chamando adicionarItemLegenda) ...
        JPanel legenda = new JPanel(); /* ... */ return legenda;
    }

    private void adicionarItemLegenda(JPanel painel, String texto, Color cor) {
        // ... (implementação como na versão anterior) ...
    }
    
    /**
     * Atualiza a exibição das seções de assentos (Plateia A, Frisas, etc.) no {@link #painelSecoes}.
     * Esta é a principal área que necessita de lógica customizada para renderizar a planta do teatro
     * conforme as especificações do projeto (ex: 5 assentos por frisa, 10 por camarote, etc.).
     * O método obtém a lista de assentos do turno atual (do cache) e os agrupa por categoria.
     * Para cada categoria, chama {@link #criarEAdicionarSecaoParaCategoria} para renderizar os assentos.
     */
    private void atualizarVisualizacaoSecoesDeAssentos() {
        if (painelSecoes == null) { return; }
        painelSecoes.removeAll();
        painelSecoes.setLayout(new BoxLayout(painelSecoes, BoxLayout.Y_AXIS)); // Seções empilhadas verticalmente
        painelSecoes.add(Box.createVerticalStrut(10));

        List<Assento> assentosDoTurnoAtual = cacheAssentosPorTurno.get(this.turnoAtualSelecionado);

        if (assentosDoTurnoAtual == null || assentosDoTurnoAtual.isEmpty()) {
            // Adiciona mensagem se não houver assentos
            // ... (código da mensagem) ...
        } else {
            Map<CategoriaAssento, List<Assento>> assentosAgrupados =
                assentosDoTurnoAtual.stream().collect(Collectors.groupingBy(Assento::getCategoria));

            // Definir a ordem de exibição das categorias
            CategoriaAssento[] ordemDeExibicao = {
                CategoriaAssento.PLATEIA_A, CategoriaAssento.PLATEIA_B,
                CategoriaAssento.FRISA, CategoriaAssento.CAMAROTE,
                CategoriaAssento.BALCAO_NOBRE
            };

            for (CategoriaAssento categoria : ordemDeExibicao) {
                if (assentosAgrupados.containsKey(categoria)) {
                    criarEAdicionarSecaoParaCategoria(
                        categoria,
                        assentosAgrupados.get(categoria)
                    );
                     painelSecoes.add(Box.createVerticalStrut(10)); // Espaço entre seções
                }
            }
        }
        painelSecoes.revalidate();
        painelSecoes.repaint();
    }
    
    /**
     * Cria e adiciona o(s) painel(is) para uma categoria específica de assentos.
     * Esta função precisa da lógica para lidar com as diferentes estruturas de cada categoria
     * (ex: múltiplas frisas/camarotes vs. uma única plateia).
     *
     * @param categoria A CategoriaAssento a ser renderizada.
     * @param assentosDaCategoria A lista de todos os assentos pertencentes a esta categoria para o turno atual.
     */
    private void criarEAdicionarSecaoParaCategoria(CategoriaAssento categoria, List<Assento> assentosDaCategoria) {
        // TODO: Implementar a lógica de layout específica para cada categoria
        // baseada na planta do teatro do documento do projeto.

        String nomeExibicao = categoria.getNome();
        JPanel painelDaSecao;

        switch (categoria) {
            case PLATEIA_A: // Ex: 25 assentos, digamos 5 fileiras de 5
                painelDaSecao = criarPainelDeSecaoComGradeSimples(nomeExibicao, 5, 5, assentosDaCategoria);
                break;
            case PLATEIA_B: // Ex: 100 assentos, digamos 10 fileiras de 10
                painelDaSecao = criarPainelDeSecaoComGradeSimples(nomeExibicao, 10, 10, assentosDaCategoria);
                break;
            case FRISA: // Ex: "5 por frisa". Se houver 30 assentos de frisa = 6 frisas.
                // Precisa agrupar os assentos por cada unidade de frisa.
                // Exemplo: se os códigos são F1-1, F1-2, ..., F2-1, ...
                // Você pode criar um painel contendo múltiplos painéis menores (um para cada frisa).
                painelDaSecao = criarPainelParaMultiplasUnidades(nomeExibicao, assentosDaCategoria, 5, "Frisa"); // 5 assentos por unidade
                break;
            case CAMAROTE: // Ex: "10 por camarote". Similar à Frisa.
                painelDaSecao = criarPainelParaMultiplasUnidades(nomeExibicao, assentosDaCategoria, 10, "Camarote"); // 10 assentos por unidade
                break;
            case BALCAO_NOBRE: // Ex: 50 assentos, digamos 5 fileiras de 10
                painelDaSecao = criarPainelDeSecaoComGradeSimples(nomeExibicao, 5, 10, assentosDaCategoria);
                break;
            default:
                painelDaSecao = new JPanel();
                painelDaSecao.add(new JLabel("Configuração de layout pendente para: " + nomeExibicao));
                break;
        }
        if (painelDaSecao != null) {
            painelSecoes.add(painelDaSecao);
        }
    }

    /**
     * Cria um painel para seções que contêm múltiplas unidades (como Frisas ou Camarotes).
     * Cada unidade é renderizada como uma pequena grade.
     */
    private JPanel criarPainelParaMultiplasUnidades(String nomeTituloSecao, List<Assento> assentosDaCategoria, int assentosPorUnidade, String prefixoUnidade) {
        JPanel painelContainerUnidades = new JPanel();
        // FlowLayout para que as unidades (frisas/camarotes) fluam e quebrem a linha se necessário.
        painelContainerUnidades.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10)); 
        painelContainerUnidades.setOpaque(false);

        JLabel lblTituloSecao = new JLabel(nomeTituloSecao, SwingConstants.CENTER);
        lblTituloSecao.setForeground(Constantes.AMARELO);
        lblTituloSecao.setFont(new Font("Arial", Font.BOLD, 18));
        lblTituloSecao.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Adicionar o título principal da seção (ex: "FRISAS") acima do FlowLayout das unidades.
        // Isso requer um painel wrapper.
        JPanel painelWrapper = new JPanel(new BorderLayout());
        painelWrapper.setOpaque(false);
        painelWrapper.add(lblTituloSecao, BorderLayout.NORTH);
        painelWrapper.add(painelContainerUnidades, BorderLayout.CENTER);


        if (assentosDaCategoria == null || assentosDaCategoria.isEmpty()) return painelWrapper;

        int totalAssentos = assentosDaCategoria.size();
        int unidadeNum = 1;
        for (int i = 0; i < totalAssentos; i += assentosPorUnidade) {
            List<Assento> assentosDaUnidade = assentosDaCategoria.subList(i, Math.min(i + assentosPorUnidade, totalAssentos));
            // Para Frisas (5 assentos), a grade é 1x5. Para Camarotes (10 assentos), 1x10 ou 2x5.
            int fileirasVisuaisUnidade = (assentosPorUnidade == 5) ? 1 : 2; // Exemplo para camarote 2x5
            int assentosPorFileiraUnidade = (assentosPorUnidade == 5) ? 5 : 5; // Exemplo para camarote 2x5

            JPanel painelDaUnidade = criarPainelDeSecaoComGradeSimples(
                prefixoUnidade + " " + unidadeNum++,
                fileirasVisuaisUnidade,
                assentosPorFileiraUnidade,
                assentosDaUnidade
            );
            painelContainerUnidades.add(painelDaUnidade);
        }
        return painelWrapper;
    }
    
    /**
     * Cria o painel visual para uma única seção/unidade de assentos com um layout de grade simples.
     */
    private JPanel criarPainelDeSecaoComGradeSimples(String nomeExibicao, int fileirasVisuais, int assentosPorFileiraVisual, List<Assento> assentosParaEstaSecao) {
        JPanel secaoPanel = new JPanel(new BorderLayout(5, 5));
        secaoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Constantes.BEGE.darker(), 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        secaoPanel.setOpaque(true);
        secaoPanel.setBackground(new Color(45, 65, 85)); // Cor de fundo para cada sub-seção

        JLabel lblNome = new JLabel(nomeExibicao, SwingConstants.CENTER);
        lblNome.setForeground(Color.WHITE);
        lblNome.setFont(new Font("Arial", Font.BOLD, 14)); // Fonte menor para nomes de sub-seção
        secaoPanel.add(lblNome, BorderLayout.NORTH);

        JPanel containerAssentos = new JPanel(new GridLayout(fileirasVisuais, assentosPorFileiraVisual, 3, 3));
        containerAssentos.setOpaque(false);

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
                    JPanel placeholder = new JPanel();
                    placeholder.setOpaque(false);
                    placeholder.setPreferredSize(new Dimension(30,30));
                    containerAssentos.add(placeholder);
                }
            }
        }
        secaoPanel.add(containerAssentos, BorderLayout.CENTER);
        // Definir um tamanho preferencial/máximo para o painel da seção pode ajudar no FlowLayout do container pai
        // int prefWidth = assentosPorFileiraVisual * 35 + 20; // Estimativa
        // int prefHeight = fileirasVisuais * 35 + 60; // Estimativa com título
        // secaoPanel.setPreferredSize(new Dimension(prefWidth, prefHeight));
        
        return secaoPanel;
    }

     /**
     * Atualiza o rótulo do total da compra com base nos assentos atualmente selecionados.
     * Habilita ou desabilita o botão de confirmar compra.
     */
    private void atualizarTotalDaCompra() {
        BigDecimal total = BigDecimal.ZERO;
        for (Assento assento : assentosSelecionadosNaTela) {
            total = total.add(assento.getPreco());
        }
        // Formata o BigDecimal para exibição como moeda
        lblTotal.setText("TOTAL: " + FormatadorMoeda.formatar(total));
        btnConfirmar.setEnabled(!assentosSelecionadosNaTela.isEmpty());
    }

    // --- MÉTODOS DO RODAPÉ REIMPLEMENTADOS COMPLETAMENTE ---
    /**
     * Cria o painel do rodapé contendo o total da compra e os botões de ação.
     * @return JPanel configurado para o rodapé.
     */
    private JPanel criarRodapeDeControles() {
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBackground(Constantes.AZUL_ESCURO);
        rodape.setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));

        // Botão Voltar
        JButton btnVoltarUI = new JButton("VOLTAR"); // Nome diferente para não conflitar com variáveis locais se houver
        btnVoltarUI.setFont(new Font("Arial", Font.PLAIN, 18));
        btnVoltarUI.setForeground(Constantes.AZUL_CLARO);
        btnVoltarUI.setBackground(Constantes.AZUL_ESCURO); // Embora não seja pintado, define a cor base
        btnVoltarUI.setBorderPainted(false);
        btnVoltarUI.setFocusPainted(false);
        btnVoltarUI.setContentAreaFilled(false); // Importante para botões customizados ou com fundo do painel
        btnVoltarUI.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltarUI.addActionListener(e -> voltarParaSelecaoPeca()); // Chama o método de navegação

        // Painel da direita para total e botão confirmar
        JPanel painelDireita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        painelDireita.setOpaque(false); // Para que o fundo AZUL_ESCURO do rodapé apareça

        // Total
        JPanel painelTotal = new JPanel();
        painelTotal.setBackground(new Color(52, 73, 94)); // Um cinza azulado para destaque
        painelTotal.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        lblTotal = new JLabel("TOTAL: R$ 0,00");
        lblTotal.setForeground(Color.WHITE);
        lblTotal.setFont(new Font("Arial", Font.BOLD, 20));
        painelTotal.add(lblTotal);

        // Botão Confirmar
        btnConfirmar = new BotaoAnimado("CONFIRMAR\nCOMPRA",
                Constantes.LARANJA, Constantes.AMARELO, new Dimension(200, 70));
        btnConfirmar.setFont(new Font("Arial", Font.BOLD, 16)); // Ajustado para texto em duas linhas
        btnConfirmar.setEnabled(false); // Inicia desabilitado
        btnConfirmar.addActionListener(e -> confirmarSelecaoEAvancar()); // Chama o método de navegação

        painelDireita.add(painelTotal);
        painelDireita.add(btnConfirmar);

        rodape.add(btnVoltarUI, BorderLayout.WEST);
        rodape.add(painelDireita, BorderLayout.EAST);

        return rodape;
    }

    /**
     * Ação executada ao clicar no botão "Confirmar Compra".
     * Navega para a tela de informar CPF, passando os dados da seleção atual.
     */
    private void confirmarSelecaoEAvancar() {
        if (!assentosSelecionadosNaTela.isEmpty()) {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            TelaInformarCPF telaInformarCPF = new TelaInformarCPF(
                false, // modoConsulta
                this.pecaSelecionada,
                new ArrayList<>(this.assentosSelecionadosNaTela), // Passa uma cópia da lista
                this.clienteServico,
                this.pecaServico,    // Passando o pecaServico
                this.reservaServico
            );
            telaInformarCPF.setTurnoSelecionado(this.turnoAtualSelecionado);
            
            frame.setContentPane(telaInformarCPF);
            frame.revalidate();
            frame.repaint();
        } else {
            // Opcional: Mostrar mensagem se tentar confirmar sem assentos, embora o botão deva estar desabilitado.
            JOptionPane.showMessageDialog(this, "Por favor, selecione ao menos um assento.", "Nenhum Assento Selecionado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Ação executada ao clicar no botão "Voltar".
     * Navega de volta para a tela de seleção de peças.
     */
    private void voltarParaSelecaoPeca() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        // Ao voltar, recria TelaSelecionarPeca passando os serviços que esta tela já possui
        frame.setContentPane(new TelaSelecionarPeca(this.pecaServico, this.clienteServico, this.reservaServico));
        frame.revalidate();
        frame.repaint();
    }

}