// Caminho: TeatroABC-main/src/main/java/com/teatroabc/infraestrutura/ui_swing/telas/TelaSelecionarPeca.java
package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.aplicacao.interfaces.IClienteServico;   // NOVO: Dependência da interface
import com.teatroabc.aplicacao.interfaces.IPecaServico; // Para passar adiante
import com.teatroabc.aplicacao.interfaces.IReservaServico; // Para passar adiante
import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAnimado;
import com.teatroabc.infraestrutura.ui_swing.componentes.CardPeca;
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import java.awt.*;
import java.util.List;
import javax.swing.*; // Para a lista de peças

public class TelaSelecionarPeca extends JPanel {
    
    //Implementação do padrão adapter! O adapter nada mais é do que um wrapper -> dependência de interfaces!
    private final IPecaServico pecaServico;     
    private final IClienteServico clienteServico; 
    private final IReservaServico reservaServico; 

    private Peca pecaSelecionada;
    private BotaoAnimado btnContinuar;
    private JPanel painelPecas; // Adicionado para poder limpar e recarregar os cards

    /**
     * Construtor refatorado para aceitar os serviços via injeção de dependência.
     */
    public TelaSelecionarPeca(IPecaServico pecaServico, IClienteServico clienteServico, IReservaServico reservaServico) {
        if (pecaServico == null || clienteServico == null || reservaServico == null) {
            throw new IllegalArgumentException("Os serviços injetados não podem ser nulos.");
        }
        
        this.pecaServico = pecaServico;
        this.clienteServico = clienteServico;
        this.reservaServico = reservaServico;
        
        configurarTelaVisual();
    }

    private void configurarTelaVisual() {
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        JPanel painelCabecalho = new JPanel(new BorderLayout());
        painelCabecalho.setBackground(Constantes.AZUL_ESCURO);
        painelCabecalho.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        JButton btnVoltar = criarBotaoVoltar();
        painelCabecalho.add(btnVoltar, BorderLayout.WEST);
        painelCabecalho.add(new LogoTeatro(), BorderLayout.EAST);
        add(painelCabecalho, BorderLayout.NORTH);

        JPanel containerPrincipal = new JPanel();
        containerPrincipal.setLayout(new BoxLayout(containerPrincipal, BoxLayout.Y_AXIS));
        containerPrincipal.setBackground(Constantes.AZUL_ESCURO);

        JLabel titulo = new JLabel("ESCOLHA UMA PEÇA");
        titulo.setFont(Constantes.FONTE_TITULO);
        titulo.setForeground(Constantes.AMARELO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        containerPrincipal.add(titulo);
        containerPrincipal.add(Box.createVerticalStrut(50));

        // Inicializa o painelPecas aqui para que possa ser atualizado
        painelPecas = new JPanel(new GridLayout(1, 0, 30, 0)); // 0 colunas para layout flexível
        painelPecas.setBackground(Constantes.AZUL_ESCURO);
        painelPecas.setMaximumSize(new Dimension(1200, 450)); // Ajustar conforme necessidade
        adicionarCardsPecasDinamicamente(painelPecas);
        containerPrincipal.add(painelPecas);

        containerPrincipal.add(Box.createVerticalStrut(50));

        btnContinuar = new BotaoAnimado("CONTINUAR",
                Constantes.LARANJA, Constantes.AMARELO, new Dimension(300, 60));
        btnContinuar.setFont(new Font("Arial", Font.BOLD, 24));
        btnContinuar.setEnabled(false);
        btnContinuar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnContinuar.addActionListener(e -> abrirSelecaoAssento());
        containerPrincipal.add(btnContinuar);

        add(containerPrincipal, BorderLayout.CENTER);
    }

    /**
     * Busca as peças através do IPecaServico injetado e cria os CardPeca.
     */
    private void adicionarCardsPecasDinamicamente(JPanel painel) {
        painel.removeAll(); // Limpa cards anteriores, se houver

        List<Peca> listaDePecas = this.pecaServico.buscarTodasPecas(); // USA O SERVIÇO INJETADO

        if (listaDePecas.isEmpty()) {
            JLabel lblSemPecas = new JLabel("Nenhuma peça disponível no momento.");
            lblSemPecas.setForeground(Color.WHITE);
            // lblSemPecas.setFont(Constantes.FONTE_TEXTO); // Se Constantes.FONTE_TEXTO estiver definido
            painel.add(lblSemPecas);
        } else {
            for (Peca peca : listaDePecas) {
                CardPeca card = new CardPeca(peca); // CardPeca lida com a conversão de cor internamente
                card.setSelecao(true);
                card.addActionListener(e -> {
                    Peca pecaDoCard = ((CardPeca) e.getSource()).getPeca(); // Obtém a Peca do card clicado
                    this.pecaSelecionada = pecaDoCard;
                    this.btnContinuar.setEnabled(true);

                    // Desmarcar outros cards
                    for (Component c : painel.getComponents()) {
                        if (c instanceof CardPeca && c != card) {
                            ((CardPeca) c).setSelecionado(false);
                        }
                    }
                    card.setSelecionado(true); // Marcar o card clicado
                });
                painel.add(card);
            }
        }
        painel.revalidate();
        painel.repaint();
    }
    
    // O método antigo adicionarCardsPecas(JPanel painel) que criava Pecas hardcoded
    // foi substituído por adicionarCardsPecasDinamicamente.

    private JButton criarBotaoVoltar() {
        JButton btnVoltar = new JButton("VOLTAR");
        // ... (configurações do botão como antes) ...
        btnVoltar.setFont(new Font("Arial", Font.PLAIN, 18));
        btnVoltar.setForeground(Constantes.AZUL_CLARO);
        btnVoltar.setBackground(Constantes.AZUL_ESCURO);
        btnVoltar.setBorderPainted(false);
        btnVoltar.setFocusPainted(false);
        btnVoltar.setContentAreaFilled(false);
        btnVoltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltar.addActionListener(e -> voltarParaTelaPrincipal());
        return btnVoltar;
    }

    private void abrirSelecaoAssento() {
        if (pecaSelecionada != null) {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            // Passe os serviços necessários para TelaSelecionarAssento
            // TelaSelecionarAssento precisará, no mínimo, do IAssentoRepositorio (via algum serviço)
            // e dos outros serviços para passar adiante no fluxo.
            frame.setContentPane(new TelaSelecionarAssento(this.pecaSelecionada, this.pecaServico, this.clienteServico, this.reservaServico));
            frame.revalidate();
            frame.repaint();
        }
    }

    private void voltarParaTelaPrincipal() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        // Ao voltar, recria TelaPrincipal passando os serviços que esta tela já possui
        frame.setContentPane(new TelaPrincipal(this.clienteServico, this.pecaServico, this.reservaServico));
        frame.revalidate();
        frame.repaint();
    }
}