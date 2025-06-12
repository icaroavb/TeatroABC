package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.dominio.modelos.Sessao;
import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAnimado;
import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAssento;
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.enums.StatusAssento;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorMoeda;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico;
import com.teatroabc.aplicacao.interfaces.ISessaoServico;
import com.teatroabc.infraestrutura.config.ConfiguracaoPlantaTeatro;
import com.teatroabc.infraestrutura.config.SecaoConfig;
import com.teatroabc.dominio.enums.CategoriaAssento;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Tela responsável por permitir ao usuário selecionar assentos para uma Sessão específica.
 * Renderiza a planta do teatro dinamicamente com base na configuração centralizada
 * e na disponibilidade de assentos para a sessão escolhida.
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

    /**
     * Construtor refatorado para receber um objeto Sessao.
     * @param sessao A sessão (data, hora, turno, peça) para a qual os assentos serão selecionados.
     * @param pecaServico Serviço para operações de peça.
     * @param clienteServico Serviço para operações de cliente.
     * @param reservaServico Serviço para operações de reserva.
     * @param sessaoServico Serviço para operações de sessão.
     * @throws IllegalArgumentException se a sessão ou algum dos serviços for nulo.
     */
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
        
        // A lógica de busca de assentos agora usa o ID da Sessão, não mais o ID da peça.
        this.assentosDaPlanta = carregarAssentosParaSessao();
        configurarTelaVisual();
    }

    /**
     * Carrega a lista de assentos disponíveis para a sessão escolhida.
     * Esta é a principal interação com o núcleo da aplicação nesta tela.
     * @return Uma lista de objetos Assento.
     */
    private List<Assento> carregarAssentosParaSessao() {
        try {
            // A busca de assentos deve, idealmente, ser por ID da Sessão.
            // Vamos adaptar usando o que temos: ID da peça e turno.
            // Em uma evolução, o método no serviço seria `buscarAssentosPorSessao(sessaoEscolhida.getId())`.
            return pecaServico.buscarAssentosDaPecaPorTurno(sessaoEscolhida.getPeca().getId(), sessaoEscolhida.getTurno());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // O restante da classe (configurarTelaVisual, atualizarVisualizacaoDasSecoes, etc.)
    // permanece o mesmo da sua versão funcional anterior. A única mudança conceitual
    // é que as informações como `turnoEscolhido` agora vêm de `sessaoEscolhida.getTurno()`.
    
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
        // Implementação deste método permanece a mesma da sua última versão funcional.
        // Ele lê a configuração e renderiza as colunas e blocos de assentos.
    }

    private void criarBlocosDeFrisa(JPanel painelColuna, List<Assento> frisas, String titulo) {
        // Implementação deste método permanece a mesma.
    }

    private JPanel criarPainelDeSecao(SecaoConfig config, List<Assento> assentosDaSecao) {
        // Implementação deste método permanece a mesma.
    }

    private void onAssentoClicked(Assento assento, BotaoAssento botao) {
        // Implementação deste método permanece a mesma.
    }
    
    // Métodos auxiliares de UI e navegação
    private JPanel criarCabecalhoDaTela() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // As informações de peça e turno agora vêm do objeto Sessao.
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

    private void confirmarSelecaoEAvancar() {
        if (!assentosSelecionadosPeloUsuario.isEmpty()) {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            // Ao avançar, passa o objeto Sessao para a próxima tela.
            TelaInformarCPF telaInformarCPF = new TelaInformarCPF(
                false, 
                this.sessaoEscolhida, // Passa o objeto Sessao
                new ArrayList<>(this.assentosSelecionadosPeloUsuario),
                this.clienteServico,
                this.pecaServico,
                this.reservaServico,
                this.sessaoServico
            );
            frame.setContentPane(telaInformarCPF);
            frame.revalidate();
            frame.repaint();
        }
    }

    private void voltarParaSelecaoDeSessao() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        // Ao voltar, recria a TelaSelecionarSessao, passando a peça original.
        frame.setContentPane(new TelaSelecionarSessao(
            this.sessaoEscolhida.getPeca(), 
            this.sessaoServico, 
            this.pecaServico, 
            this.clienteServico, 
            this.reservaServico
        ));
        frame.revalidate();
        frame.repaint();
    }
    
    // ... Os demais métodos auxiliares (criarRodape, criarLegenda, etc.) não precisam de alteração e são omitidos por brevidade.
    // Cole o conteúdo completo da sua versão anterior para eles.
}