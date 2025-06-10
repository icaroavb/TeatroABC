// Caminho: TeatroABC-main/src/main/java/com/teatroabc/infraestrutura/ui_swing/telas/TelaSelecionarAssento.java
package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.infraestrutura.ui_swing.componentes.*;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.enums.CategoriaAssento; // Usado para criar seções
import com.teatroabc.dominio.enums.StatusAssento;
import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorMoeda;

// Imports dos serviços
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
// Removido: import java.util.Random; // Se a simulação for totalmente removida

public class TelaSelecionarAssento extends JPanel {
    private final Peca pecaSelecionada; // Renomeado de 'peca' para clareza
    private List<Assento> assentosSelecionadosNaTela; // Renomeado para clareza
    
    // Mapa para armazenar os assentos carregados do serviço para cada turno (para evitar recarregar toda vez)
    private Map<Turno, List<Assento>> cacheAssentosPorTurno;
    
    private Turno turnoAtualSelecionado; // Renomeado de 'turnoSelecionado'

    // Serviços injetados
    private final IPecaServico pecaServico;
    private final IClienteServico clienteServico;
    private final IReservaServico reservaServico;
    // Não precisamos mais do AssentoRepositorio diretamente aqui, usaremos o pecaServico

    private JLabel lblTotal;
    private BotaoAnimado btnConfirmar;
    private JPanel painelSecoes; // O painel que contém as seções de assentos
    private ButtonGroup grupoTurnosRadios; // Renomeado de 'grupoTurnos'

    public TelaSelecionarAssento(Peca peca, IPecaServico pecaServico, IClienteServico clienteServico, IReservaServico reservaServico) {
        if (peca == null || pecaServico == null || clienteServico == null || reservaServico == null) {
            throw new IllegalArgumentException("Peca e Serviços não podem ser nulos em TelaSelecionarAssento.");
        }
        this.pecaSelecionada = peca;
        this.pecaServico = pecaServico;
        this.clienteServico = clienteServico;
        this.reservaServico = reservaServico;

        this.assentosSelecionadosNaTela = new ArrayList<>();
        this.turnoAtualSelecionado = Turno.NOITE; // Turno padrão inicial
        this.cacheAssentosPorTurno = new HashMap<>();
        
        // Carregar assentos para o turno padrão inicial
        carregarEConfigurarAssentosParaTurno(this.turnoAtualSelecionado);
        configurarTelaVisual(); // Configura os componentes da UI
    }

    /**
     * Carrega (ou obtém do cache) e configura os assentos para o turno especificado.
     * Atualiza o cache interno de assentos.
     */
    private void carregarEConfigurarAssentosParaTurno(Turno turno) {
        // Verifica se já temos os assentos para este turno no cache
        if (!cacheAssentosPorTurno.containsKey(turno)) {
            System.out.println("TelaSelecionarAssento: Carregando assentos para peça " + pecaSelecionada.getId() + ", turno " + turno.name() + " via serviço.");
            // Busca os assentos através do IPecaServico (que delega para IAssentoRepositorio)
            List<Assento> assentosDoServico = pecaServico.buscarAssentosDaPecaPorTurno(pecaSelecionada.getId(), turno);
            cacheAssentosPorTurno.put(turno, new ArrayList<>(assentosDoServico)); // Armazena uma cópia no cache
        } else {
            System.out.println("TelaSelecionarAssento: Usando assentos cacheados para turno " + turno.name());
        }
        // Garante que os assentos selecionados anteriormente em outro turno sejam limpos
        // e que o status dos assentos do turno atual seja resetado se eles foram selecionados antes.
        resetarSelecaoDeAssentos();
    }
    
    /**
     * Reseta o status 'SELECIONADO' para 'DISPONIVEL' de todos os assentos no cache
     * e limpa a lista de assentos atualmente selecionados na tela.
     */
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


    // O método carregarAssentosPorTurno original foi substituído por carregarEConfigurarAssentosParaTurno
    // A lógica de simulação (isAssentoOcupadoSimulacao, getProbabilidadeOcupacao) foi removida
    // pois agora confiamos no serviço/repositório para fornecer os status corretos.

    private void configurarTelaVisual() { // Renomeado de configurarTela
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        JPanel containerPrincipal = new JPanel(new BorderLayout());
        containerPrincipal.setBackground(Constantes.AZUL_ESCURO);
        containerPrincipal.add(criarCabecalho(), BorderLayout.NORTH);

        JPanel painelCentral = new JPanel(new BorderLayout());
        painelCentral.setBackground(Constantes.AZUL_ESCURO);
        painelCentral.add(criarPainelTeatro(), BorderLayout.CENTER);
        containerPrincipal.add(painelCentral, BorderLayout.CENTER);
        
        containerPrincipal.add(criarPainelSelecaoTurnos(), BorderLayout.EAST); // Renomeado de criarPainelTurnos
        add(containerPrincipal, BorderLayout.CENTER);
        add(criarRodapeControles(), BorderLayout.SOUTH); // Renomeado de criarRodape

        // Após a UI ser configurada, atualiza as seções de assentos com base no turno inicial
        atualizarVisualizacaoSecoesDeAssentos();
        atualizarTotalCompra(); // Renomeado de atualizarTotal
    }

    // Os métodos criarCabecalho, criarPainelTeatro, criarLegenda, obterPrefixoCategoria permanecem os mesmos.
    // ... (código omitido por brevidade, igual ao da sua classe) ...
    private JPanel criarCabecalho() { /* ... */ return new JPanel();}
    private JPanel criarPainelTeatro() { /* ... */painelSecoes = new JPanel(); /*...*/ return new JPanel();}
    private JPanel criarLegenda() { /* ... */ return new JPanel();}
    private String obterPrefixoCategoria(CategoriaAssento categoria) { /* ... */ return "";}
    private void adicionarItemLegenda(JPanel painel, String texto, Color cor) { /* ... */}
    private JPanel criarPlateia() { /* ... */ return new JPanel();}


    private JPanel criarPainelSelecaoTurnos() { // Renomeado de criarPainelTurnos
        JPanel painelTurnosUI = new JPanel(); // Renomeado para não confundir com o enum
        painelTurnosUI.setBackground(new Color(52, 73, 94));
        painelTurnosUI.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        painelTurnosUI.setPreferredSize(new Dimension(250, 0)); // Aumentar um pouco a largura
        painelTurnosUI.setLayout(new BoxLayout(painelTurnosUI, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel("ESCOLHA O TURNO");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelTurnosUI.add(lblTitulo);
        painelTurnosUI.add(Box.createVerticalStrut(30));

        grupoTurnosRadios = new ButtonGroup();
        for (Turno turnoOpcao : Turno.values()) {
            JRadioButton radioTurno = criarRadioTurno(turnoOpcao);
            grupoTurnosRadios.add(radioTurno);
            painelTurnosUI.add(radioTurno);
            painelTurnosUI.add(Box.createVerticalStrut(15));
            if (turnoOpcao == this.turnoAtualSelecionado) { // Seleciona o turno padrão
                radioTurno.setSelected(true);
            }
        }
        // ... (info sobre disponibilidade)
        return painelTurnosUI;
    }

    private JRadioButton criarRadioTurno(Turno turnoOpcao) {
        JRadioButton radio = new JRadioButton(turnoOpcao.toString());
        // ... (configurações do radio como antes) ...
        radio.setFont(new Font("Arial", Font.PLAIN, 16));
        radio.setForeground(Color.WHITE);
        radio.setBackground(new Color(52, 73, 94));
        radio.setFocusPainted(false);
        radio.setCursor(new Cursor(Cursor.HAND_CURSOR));
        radio.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Ícones personalizados como antes
        // ...

        radio.addActionListener(e -> {
            if (radio.isSelected()) { // Ação apenas se o radio for selecionado
                this.turnoAtualSelecionado = turnoOpcao;
                System.out.println("Turno alterado para: " + this.turnoAtualSelecionado.getNome());
                carregarEConfigurarAssentosParaTurno(this.turnoAtualSelecionado); // Carrega dados do novo turno
                atualizarVisualizacaoSecoesDeAssentos(); // Redesenha as seções de assentos
                atualizarTotalCompra(); // Reseta o total e o botão
            }
        });
        return radio;
    }

    /**
     * Limpa e recria as seções de botões de assento (Frisas, Balcão Nobre, Balcão)
     * com base nos assentos do turno atualmente selecionado (obtidos do cache).
     */
    private void atualizarVisualizacaoSecoesDeAssentos() {
        if (painelSecoes == null) { // painelSecoes é inicializado em criarPainelTeatro
            painelSecoes = new JPanel(); // Fallback, mas não deveria ser necessário
            // Adicionar ao layout principal se estiver sendo criado aqui
        }
        painelSecoes.removeAll();
        // A estrutura de layout (ex: GridLayout) e tamanho do painelSecoes
        // deve ser definida consistentemente, talvez em criarPainelTeatro.
        // Ex: painelSecoes.setLayout(new GridLayout(1, 3, 30, 0));
        // painelSecoes.setMaximumSize(new Dimension(1000, 250)); // Ajustar altura

        List<Assento> assentosDoTurnoAtual = cacheAssentosPorTurno.get(this.turnoAtualSelecionado);
        if (assentosDoTurnoAtual == null) {
            // Isso não deveria acontecer se carregarEConfigurarAssentosParaTurno foi chamado
            System.err.println("Erro: Nenhum assento carregado para o turno " + this.turnoAtualSelecionado);
            // Adicionar uma mensagem na UI, se apropriado
            painelSecoes.add(new JLabel("Erro ao carregar assentos para este turno."));
        } else {
            // Recriar as seções com base nas categorias definidas no seu projeto
            // e na planta do teatro do documento (Frisa, Camarote, Plateia A/B, Balcao Nobre).
            // O exemplo abaixo mantém a estrutura antiga de 3 seções, PRECISA SER AJUSTADO.

            // TODO: AJUSTAR ESTA SEÇÃO PARA REFLETIR AS CATEGORIAS CORRETAS DO PROJETO
            // E A DISPOSIÇÃO CORRETA DOS ASSENTOS CONFORME A PLANTA DO TEATRO
            // (Plateia A, Plateia B, Frisa, Camarote, Balcão Nobre)

            // Exemplo MANTENDO A ESTRUTURA ANTIGA DE 3 BLOCOS para demonstração:
            // Você precisará de uma lógica mais sofisticada para agrupar e exibir
            // os assentos conforme as categorias do seu documento do projeto.
            painelSecoes.add(criarSecaoVisual("FRISAS", CategoriaAssento.FRISA, 3, 8, assentosDoTurnoAtual));
            painelSecoes.add(criarSecaoVisual("BALCÃO NOBRE", CategoriaAssento.BALCAO_NOBRE, 4, 10, assentosDoTurnoAtual));
            // painelSecoes.add(criarSecaoVisual("BALCÃO", CategoriaAssento.BALCAO, 4, 10, assentosDoTurnoAtual)); // Categoria "BALCAO" não existe mais no enum atualizado
            // Adicionar seções para PLATEIA_A, PLATEIA_B, CAMAROTE
            painelSecoes.add(criarSecaoVisual("PLATEIA A", CategoriaAssento.PLATEIA_A, 5, 5, assentosDoTurnoAtual)); // Exemplo 5x5 = 25


        }
        painelSecoes.revalidate();
        painelSecoes.repaint();
    }

    /**
     * Cria um painel visual para uma seção de assentos.
     * Este método agora filtra os assentos da lista geral (assentosDoTurnoAtual)
     * que pertencem à categoria especificada.
     */
    private JPanel criarSecaoVisual(String nomeSecao, CategoriaAssento categoriaAlvo,
                                    int fileirasVisuais, int assentosPorFileiraVisual,
                                    List<Assento> todosAssentosDoTurno) {
        JPanel secaoPanel = new JPanel(new BorderLayout());
        // ... (configurações do secaoPanel)

        JLabel lblNome = new JLabel(nomeSecao, SwingConstants.CENTER);
        // ... (configurações do lblNome)
        secaoPanel.add(lblNome, BorderLayout.NORTH);

        JPanel containerAssentos = new JPanel(); // Configurar layout (GridLayout)
        // ... (configurações do containerAssentos)

        // Filtra os assentos que pertencem a esta categoria E os organiza visualmente
        List<Assento> assentosDaCategoria = todosAssentosDoTurno.stream()
                                             .filter(a -> a.getCategoria() == categoriaAlvo)
                                             .collect(Collectors.toList());
        
        // A lógica de `fileirasVisuais` e `assentosPorFileiraVisual` precisa corresponder
        // a como você quer exibir os assentos da `assentosDaCategoria`.
        // O exemplo abaixo é uma simplificação e pode não mapear corretamente para todos os casos
        // sem uma lógica de preenchimento ou mapeamento de código de assento para posição na grade.
        containerAssentos.setLayout(new GridLayout(fileirasVisuais, assentosPorFileiraVisual, 3, 3));
        int assentoIndex = 0;
        for (int f = 0; f < fileirasVisuais; f++) {
            for (int a = 0; a < assentosPorFileiraVisual; a++) {
                if (assentoIndex < assentosDaCategoria.size()) {
                    Assento assento = assentosDaCategoria.get(assentoIndex++);
                    BotaoAssento botao = new BotaoAssento(assento);
                    botao.addActionListener(e -> {
                        // Lógica de seleção/desseleção do assento
                        if (assento.getStatus() == StatusAssento.SELECIONADO) {
                            assento.setStatus(StatusAssento.DISPONIVEL);
                            assentosSelecionadosNaTela.remove(assento);
                        } else if (assento.getStatus() == StatusAssento.DISPONIVEL) {
                            assento.setStatus(StatusAssento.SELECIONADO);
                            assentosSelecionadosNaTela.add(assento);
                        }
                        ((BotaoAssento)e.getSource()).repaint(); // Repinta o botão específico
                        atualizarTotalCompra();
                    });
                    containerAssentos.add(botao);
                } else {
                    containerAssentos.add(new JLabel()); // Espaço vazio se não houver assento suficiente para a grade
                }
            }
        }
        secaoPanel.add(containerAssentos, BorderLayout.CENTER);
        return secaoPanel;
    }


    private JPanel criarRodapeControles() { // Renomeado de criarRodape
        // ... (lógica do rodapé como antes, usando lblTotal e btnConfirmar) ...
        JPanel rodape = new JPanel(new BorderLayout()); /*...*/
        lblTotal = new JLabel("TOTAL: R$ 0,00"); /*...*/
        btnConfirmar = new BotaoAnimado("CONFIRMAR\nCOMPRA", /*...*/);
        btnConfirmar.addActionListener(e -> confirmarSelecaoEAvancar()); // Renomeado de confirmarCompra
        /* ... adicionar componentes ao rodape ... */
        return rodape;
    }

    private void atualizarTotalCompra() { // Renomeado de atualizarTotal
        BigDecimal total = BigDecimal.ZERO;
        for (Assento assento : assentosSelecionadosNaTela) {
            total = total.add(assento.getPreco());
        }
        lblTotal.setText("TOTAL: " + FormatadorMoeda.formatar(total)); // FormatadorMoeda agora aceita BigDecimal
        btnConfirmar.setEnabled(!assentosSelecionadosNaTela.isEmpty());
    }

    private void confirmarSelecaoEAvancar() { // Renomeado de confirmarCompra
        if (!assentosSelecionadosNaTela.isEmpty()) {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            // Passe os serviços para TelaInformarCPF
            TelaInformarCPF telaInformarCPF = new TelaInformarCPF(
                false, // modoConsulta = false
                this.pecaSelecionada,
                new ArrayList<>(this.assentosSelecionadosNaTela), // Passa uma cópia da lista
                this.clienteServico,
                this.reservaServico
                // this.pecaServico não parece ser usado por TelaInformarCPF diretamente
            );
            telaInformarCPF.setTurnoSelecionado(this.turnoAtualSelecionado);
            
            frame.setContentPane(telaInformarCPF);
            frame.revalidate();
            frame.repaint();
        }
    }

    private void voltarParaSelecaoPeca() { // Renomeado de voltar
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        // Passe os serviços de volta para TelaSelecionarPeca
        frame.setContentPane(new TelaSelecionarPeca(this.pecaServico, this.clienteServico, this.reservaServico));
        frame.revalidate();
        frame.repaint();
    }
}