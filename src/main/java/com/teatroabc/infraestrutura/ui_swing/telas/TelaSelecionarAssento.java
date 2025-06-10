package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.infraestrutura.ui_swing.componentes.*;
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


public class TelaSelecionarAssento extends JPanel {
    private final Peca pecaSelecionada;
    private List<Assento> assentosSelecionadosNaTela;
    private Map<Turno, List<Assento>> cacheAssentosPorTurno;
    private Turno turnoAtualSelecionado;

    private final IPecaServico pecaServico;
    private final IClienteServico clienteServico;
    private final IReservaServico reservaServico;

    private JLabel lblTotal;
    private BotaoAnimado btnConfirmar;
    private JPanel painelSecoes;
    private ButtonGroup grupoTurnosRadios;

    public TelaSelecionarAssento(Peca peca, IPecaServico pecaServico, IClienteServico clienteServico, IReservaServico reservaServico) {
        if (peca == null || pecaServico == null || clienteServico == null || reservaServico == null) {
            throw new IllegalArgumentException("Peca e Serviços não podem ser nulos em TelaSelecionarAssento.");
        }
        this.pecaSelecionada = peca;
        this.pecaServico = pecaServico;
        this.clienteServico = clienteServico;
        this.reservaServico = reservaServico;

        this.assentosSelecionadosNaTela = new ArrayList<>();
        this.turnoAtualSelecionado = Turno.NOITE;
        this.cacheAssentosPorTurno = new HashMap<>();
        
        carregarEConfigurarAssentosParaTurno(this.turnoAtualSelecionado);
        configurarTelaVisual();
    }

    private void carregarEConfigurarAssentosParaTurno(Turno turno) {
        if (!cacheAssentosPorTurno.containsKey(turno)) {
            // System.out.println("TelaSelecionarAssento: Carregando assentos para peça " + pecaSelecionada.getId() + ", turno " + turno.name() + " via serviço.");
            List<Assento> assentosDoServico = pecaServico.buscarAssentosDaPecaPorTurno(pecaSelecionada.getId(), turno);
            cacheAssentosPorTurno.put(turno, new ArrayList<>(assentosDoServico));
        } else {
            // System.out.println("TelaSelecionarAssento: Usando assentos cacheados para turno " + turno.name());
        }
        resetarSelecaoDeAssentos();
    }
    
    private void resetarSelecaoDeAssentos() {
        this.assentosSelecionadosNaTela.clear();
        this.cacheAssentosPorTurno.values().forEach(listaAssentos -> 
            listaAssentos.forEach(assento -> {
                if (assento.getStatus() == StatusAssento.SELECIONADO) {
                    assento.setStatus(StatusAssento.DISPONIVEL);
                }
            })
        );
    }

    private void configurarTelaVisual() {
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        JPanel containerPrincipal = new JPanel(new BorderLayout());
        containerPrincipal.setBackground(Constantes.AZUL_ESCURO);
        
        // Cabeçalho
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(Constantes.AZUL_ESCURO);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        JLabel tituloCabecalho = new JLabel("ESCOLHA O SEU ASSENTO"); // Renomeado para clareza
        tituloCabecalho.setFont(Constantes.FONTE_SUBTITULO);
        tituloCabecalho.setForeground(Color.WHITE);
        tituloCabecalho.setBackground(Constantes.LARANJA);
        tituloCabecalho.setOpaque(true);
        tituloCabecalho.setHorizontalAlignment(JLabel.CENTER);
        tituloCabecalho.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));
        cabecalho.add(tituloCabecalho, BorderLayout.CENTER);
        cabecalho.add(new LogoTeatro(), BorderLayout.EAST); // LogoTeatro já é um JPanel
        containerPrincipal.add(cabecalho, BorderLayout.NORTH);


        // Painel central com palco e seções de assentos
        JPanel painelCentral = new JPanel(new BorderLayout());
        painelCentral.setBackground(Constantes.AZUL_ESCURO);
        // Adiciona um painel que conterá o palco e as seções para melhor controle do layout vertical
        JPanel painelTeatroLayout = new JPanel();
        painelTeatroLayout.setBackground(Constantes.AZUL_ESCURO);
        painelTeatroLayout.setLayout(new BoxLayout(painelTeatroLayout, BoxLayout.Y_AXIS));

        // Palco
        JPanel palco = new JPanel(); // Palco como antes
        palco.setBackground(Constantes.BEGE);
        palco.setPreferredSize(new Dimension(700, 60));
        palco.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60)); // Largura máxima, altura fixa
        JLabel lblPalco = new JLabel("PALCO");
        lblPalco.setFont(new Font("Arial", Font.BOLD, 28));
        palco.add(lblPalco);
        palco.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelTeatroLayout.add(Box.createVerticalStrut(20));
        painelTeatroLayout.add(palco);
        painelTeatroLayout.add(Box.createVerticalStrut(30));
        
        // Container para as seções de assentos
        // painelSecoes será inicializado e populado em atualizarVisualizacaoSecoesDeAssentos
        painelSecoes = new JPanel(); 
        painelSecoes.setBackground(Constantes.AZUL_ESCURO);
        painelSecoes.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Layout e tamanho máximo serão definidos em atualizarVisualizacaoSecoesDeAssentos
        painelTeatroLayout.add(painelSecoes);
        
        painelTeatroLayout.add(Box.createVerticalStrut(30));

        // Legenda
        JPanel legenda = criarLegenda(); // Legenda como antes
        legenda.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelTeatroLayout.add(legenda);
        painelTeatroLayout.add(Box.createVerticalStrut(20)); // Espaço após legenda

        // Adiciona o painelTeatroLayout (com palco, seções, legenda) ao painelCentral
        // Envolve com JScrollPane para permitir rolagem se o conteúdo for grande
        JScrollPane scrollTeatro = new JScrollPane(painelTeatroLayout);
        scrollTeatro.setBorder(null);
        scrollTeatro.getViewport().setOpaque(false);
        scrollTeatro.setOpaque(false);
        scrollTeatro.getVerticalScrollBar().setUnitIncrement(16);
        painelCentral.add(scrollTeatro, BorderLayout.CENTER);
        
        containerPrincipal.add(painelCentral, BorderLayout.CENTER);
        containerPrincipal.add(criarPainelSelecaoTurnos(), BorderLayout.EAST);
        
        add(containerPrincipal, BorderLayout.CENTER);
        add(criarRodapeControles(), BorderLayout.SOUTH);

        atualizarVisualizacaoSecoesDeAssentos();
        atualizarTotalCompra();
    }
    
    // criarCabecalho() foi incorporado em configurarTelaVisual()
    // criarPainelTeatro() foi incorporado e reestruturado em configurarTelaVisual()

    private JPanel criarLegenda() { /* ... como antes ... */ return new JPanel();}
    private void adicionarItemLegenda(JPanel painel, String texto, Color cor) { /* ... */}
    // criarPlateia() foi removido, pois a exibição dos assentos agora é feita por criarSecaoVisual

    private String obterPrefixoCategoria(CategoriaAssento categoria) {
        // Este método é usado internamente por criarSecaoVisual se a lógica de
        // geração de códigos de assentos depender dele.
        // Por exemplo, para filtrar Assentos da lista `todosAssentosDoTurno`
        // ou para construir códigos esperados se a planta for gerada proceduralmente aqui.
        // No entanto, com `buscarAssentosDaPecaPorTurno` fornecendo todos os assentos já
        // com seus códigos e categorias, este método pode não ser mais necessário aqui,
        // a menos que a lógica de `criarSecaoVisual` precise dele para alguma filtragem
        // baseada em prefixo (o que não é ideal, é melhor filtrar pela instância de CategoriaAssento).

        // Vamos mantê-lo por enquanto, caso a lógica de display em criarSecaoVisual precise.
        switch (categoria) {
            case FRISA: return "F"; // Mantido para consistência, mas o enum agora é FRISA, não FRISAS
            case BALCAO_NOBRE: return "B"; // Ou BN, dependendo da sua codificação
            // case BALCAO: return "C"; // Removido pois BALCAO não está no enum atualizado
            case PLATEIA_A: return "PA";
            case PLATEIA_B: return "PB";
            case CAMAROTE: return "CM";
            default: return "X"; // Fallback
        }
    }

    private JPanel criarPainelSelecaoTurnos() { /* ... como antes ... */ return new JPanel();}
    private JRadioButton criarRadioTurno(Turno turnoOpcao) { /* ... como antes ... */ return new JRadioButton();}

    private void atualizarVisualizacaoSecoesDeAssentos() {
        if (painelSecoes == null) {
            // Isso não deveria acontecer se painelSecoes for inicializado em configurarTelaVisual.
            System.err.println("ERRO: painelSecoes é nulo em atualizarVisualizacaoSecoesDeAssentos!");
            return;
        }
        painelSecoes.removeAll();
        // Definir o layout aqui garante que ele seja aplicado corretamente.
        // O número de colunas (segundo argumento do GridLayout) é 0 para que se ajuste ao número de seções.
        // Ou fixe em 1 se quiser as seções uma abaixo da outra.
        // Para exibir lado a lado, e o número de seções for conhecido (ex: 5 categorias), use esse número.
        // Por exemplo, para exibir todas as 5 categorias do enum:
        painelSecoes.setLayout(new GridLayout(0, 1, 10, 10)); // 1 coluna, múltiplas linhas, com espaçamento
        // painelSecoes.setMaximumSize(new Dimension(1000, 600)); // Ajustar conforme o conteúdo


        List<Assento> assentosDoTurnoAtual = cacheAssentosPorTurno.get(this.turnoAtualSelecionado);

        if (assentosDoTurnoAtual == null || assentosDoTurnoAtual.isEmpty()) {
            System.err.println("Nenhum assento carregado para o turno " + this.turnoAtualSelecionado + " ou lista vazia.");
            painelSecoes.add(new JLabel("Nenhum assento disponível para este turno."));
        } else {
            // **REFAZER A LÓGICA DE CRIAÇÃO DAS SEÇÕES AQUI**
            // Você precisa criar um painel para cada CATEGORIA definida no seu documento
            // e popular com os assentos correspondentes da lista `assentosDoTurnoAtual`.

            // Exemplo:
            criarEAdicionarSecao(CategoriaAssento.PLATEIA_A, "Plateia A (Ex: 5x5)", 5, 5, assentosDoTurnoAtual);
            criarEAdicionarSecao(CategoriaAssento.PLATEIA_B, "Plateia B (Ex: 10x10)", 10, 10, assentosDoTurnoAtual);
            // Para Frisa e Camarote, a lógica de "X por frisa/camarote" precisará de tratamento especial.
            // Você pode precisar de um loop externo para o número de frisas/camarotes e um interno para os assentos.
            // E os parâmetros fileirasVisuais/assentosPorFileiraVisual seriam fixos (ex: 1x5 para frisa).
            criarEAdicionarSecao(CategoriaAssento.FRISA, "Frisas (Ex: 6 frisas de 1x5)", 1, 5, assentosDoTurnoAtual); // Isso precisaria de um loop para cada frisa
            criarEAdicionarSecao(CategoriaAssento.CAMAROTE, "Camarotes (Ex: 4 camarotes de 1x10)", 1, 10, assentosDoTurnoAtual); // Loop para cada camarote
            criarEAdicionarSecao(CategoriaAssento.BALCAO_NOBRE, "Balcão Nobre (Ex: 5x10)", 5, 10, assentosDoTurnoAtual);
        }
        painelSecoes.revalidate();
        painelSecoes.repaint();
    }
    
    /**
     * Método auxiliar para criar e adicionar uma seção de assentos ao painel principal de seções.
     * A lógica de disposição (fileirasVisuais, assentosPorFileiraVisual) precisa ser ajustada
     * para cada categoria específica para corresponder à planta real do teatro.
     */
    private void criarEAdicionarSecao(CategoriaAssento categoria, String nomeExibicaoSecao,
                                     int fileirasVisuais, int assentosPorFileiraVisual,
                                     List<Assento> todosAssentosDoTurno) {
        // Filtra os assentos que pertencem a esta categoria específica
        List<Assento> assentosDaCategoria = todosAssentosDoTurno.stream()
                                             .filter(a -> a.getCategoria() == categoria)
                                             .collect(Collectors.toList());

        if (!assentosDaCategoria.isEmpty()) {
            // Se você tem múltiplas frisas/camarotes, precisará de uma lógica para dividir
            // `assentosDaCategoria` em subgrupos e chamar `criarPainelDeUmaSecao` para cada um.
            // Para Plateias e Balcão Nobre, pode ser um único painel.

            // Exemplo simplificado: criando um painel para todos os assentos da categoria.
            // A disposição em fileirasVisuais x assentosPorFileiraVisual ainda é um desafio aqui
            // se a lista `assentosDaCategoria` não corresponder exatamente a essa grade.
            JPanel painelDeUmaSecao = criarPainelDeUmaSecao(nomeExibicaoSecao, fileirasVisuais, assentosPorFileiraVisual, assentosDaCategoria);
            painelSecoes.add(painelDeUmaSecao);
        } else {
            // System.out.println("Nenhum assento encontrado para a categoria: " + categoria.getNome());
        }
    }

    /**
     * Cria o painel visual para UMA unidade de seção (ex: uma frisa, um camarote, ou toda a Plateia A).
     * @param nomeExibicao O nome a ser exibido para esta seção.
     * @param fileiras Número de fileiras visuais nesta unidade de seção.
     * @param assentosPorFileira Número de assentos por fileira visual nesta unidade.
     * @param assentosParaEstaSecao A lista de objetos Assento pertencentes a esta unidade.
     * @return JPanel configurado com os botões de assento.
     */
    private JPanel criarPainelDeUmaSecao(String nomeExibicao, int fileiras, int assentosPorFileira, List<Assento> assentosParaEstaSecao) {
        JPanel secaoPanel = new JPanel(new BorderLayout(5, 5));
        secaoPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        secaoPanel.setOpaque(false); // Para que o fundo do painelSecoes seja visível

        JLabel lblNome = new JLabel(nomeExibicao, SwingConstants.CENTER);
        lblNome.setForeground(Color.WHITE);
        lblNome.setFont(new Font("Arial", Font.BOLD, 16));
        secaoPanel.add(lblNome, BorderLayout.NORTH);

        JPanel containerAssentos = new JPanel(new GridLayout(fileiras, assentosPorFileira, 3, 3));
        containerAssentos.setOpaque(false); // Para o fundo do secaoPanel ser visível
        // containerAssentos.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY)); // Borda para debug do layout

        int assentoIndex = 0;
        for (int f = 0; f < fileiras; f++) {
            for (int a = 0; a < assentosPorFileira; a++) {
                if (assentoIndex < assentosParaEstaSecao.size()) {
                    Assento assento = assentosParaEstaSecao.get(assentoIndex++);
                    BotaoAssento botao = new BotaoAssento(assento);
                    botao.addActionListener(e -> {
                        if (assento.getStatus() == StatusAssento.SELECIONADO) {
                            assento.setStatus(StatusAssento.DISPONIVEL);
                            assentosSelecionadosNaTela.remove(assento);
                        } else if (assento.getStatus() == StatusAssento.DISPONIVEL) {
                            assento.setStatus(StatusAssento.SELECIONADO);
                            assentosSelecionadosNaTela.add(assento);
                        }
                        ((JButton)e.getSource()).repaint();
                        atualizarTotalCompra();
                    });
                    containerAssentos.add(botao);
                } else {
                    // Adiciona um placeholder se não houver assento para preencher a grade
                    // Isso é útil se a contagem de fileiras/assentosPorFileira for fixa
                    // mas a lista de assentos for menor (ex: final de uma seção irregular).
                    containerAssentos.add(Box.createRigidArea(new Dimension(30,30))); // Tamanho do BotaoAssento
                }
            }
        }
        secaoPanel.add(containerAssentos, BorderLayout.CENTER);
        return secaoPanel;
    }


    private JPanel criarRodapeControles() {
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBackground(Constantes.AZUL_ESCURO);
        rodape.setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));

        JButton btnVoltarUI = new JButton("VOLTAR");
        btnVoltarUI.setFont(new Font("Arial", Font.PLAIN, 18));
        btnVoltarUI.setForeground(Constantes.AZUL_CLARO);
        // ... (configurações do btnVoltarUI como antes)
        btnVoltarUI.addActionListener(e -> voltarParaSelecaoPeca());

        JPanel painelDireita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        painelDireita.setOpaque(false);

        JPanel painelTotal = new JPanel();
        painelTotal.setBackground(new Color(52, 73, 94));
        painelTotal.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        lblTotal = new JLabel("TOTAL: R$ 0,00");
        lblTotal.setForeground(Color.WHITE);
        lblTotal.setFont(new Font("Arial", Font.BOLD, 20));
        painelTotal.add(lblTotal);

        // CORREÇÃO na linha 295 da sua versão anterior (aqui será a instanciação do btnConfirmar)
        btnConfirmar = new BotaoAnimado("CONFIRMAR\nCOMPRA",
                Constantes.LARANJA, Constantes.AMARELO, new Dimension(200, 70)); // Ajustado tamanho
        btnConfirmar.setFont(new Font("Arial", Font.BOLD, 16)); // Ajustada fonte para caber "COMPRA"
        btnConfirmar.setEnabled(false);
        btnConfirmar.addActionListener(e -> confirmarSelecaoEAvancar());

        painelDireita.add(painelTotal);
        painelDireita.add(btnConfirmar);

        rodape.add(btnVoltarUI, BorderLayout.WEST);
        rodape.add(painelDireita, BorderLayout.EAST);

        return rodape;
    }

    private void atualizarTotalCompra() {
        BigDecimal total = BigDecimal.ZERO;
        for (Assento assento : assentosSelecionadosNaTela) {
            total = total.add(assento.getPreco());
        }
        lblTotal.setText("TOTAL: " + FormatadorMoeda.formatar(total));
        btnConfirmar.setEnabled(!assentosSelecionadosNaTela.isEmpty());
    }

    private void confirmarSelecaoEAvancar() {
        if (!assentosSelecionadosNaTela.isEmpty()) {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            // CORREÇÃO AQUI: Passar todos os serviços necessários para TelaInformarCPF
            TelaInformarCPF telaInformarCPF = new TelaInformarCPF(
                false, 
                this.pecaSelecionada,
                new ArrayList<>(this.assentosSelecionadosNaTela),
                this.clienteServico,
                this.pecaServico,    // Passando o pecaServico
                this.reservaServico
            );
            telaInformarCPF.setTurnoSelecionado(this.turnoAtualSelecionado);
            
            frame.setContentPane(telaInformarCPF);
            frame.revalidate();
            frame.repaint();
        }
    }

    private void voltarParaSelecaoPeca() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaSelecionarPeca(this.pecaServico, this.clienteServico, this.reservaServico));
        frame.revalidate();
        frame.repaint();
    }
}