package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.infraestrutura.ui_swing.componentes.CardBilhete;
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.modelos.Bilhete;
import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Tela responsável por exibir uma lista de todos os bilhetes pertencentes a um cliente,
 * identificado por seu CPF.
 *
 * Na Arquitetura Hexagonal, esta classe atua como um Adaptador Primário. Ela é
 * iniciada a partir do fluxo de consulta e utiliza a porta de entrada IReservaServico
 * para buscar os dados do domínio. Sua principal responsabilidade é traduzir a
 * lista de objetos Bilhete em uma representação visual de cards para o usuário.
 */
public class TelaListaBilhetes extends JPanel {
    private final String cpfCliente;

    // Serviços injetados via construtor
    private final IReservaServico reservaServico;
    private final IClienteServico clienteServico; // Para repassar na navegação
    private final IPecaServico pecaServico;       // Para repassar na navegação

    /**
     * Construtor da TelaListaBilhetes.
     * @param cpf O CPF do cliente para o qual os bilhetes serão listados. Não pode ser nulo.
     * @param reservaServico O serviço para buscar os bilhetes. Não pode ser nulo.
     * @param clienteServico O serviço de cliente (para repassar ao voltar para TelaPrincipal).
     * @param pecaServico O serviço de peça (para repassar ao voltar para TelaPrincipal).
     * @throws IllegalArgumentException se o CPF ou algum dos serviços for nulo.
     */
    public TelaListaBilhetes(String cpf, IReservaServico reservaServico,
                             IClienteServico clienteServico, IPecaServico pecaServico) {
        if (cpf == null || cpf.trim().isEmpty() || reservaServico == null || clienteServico == null || pecaServico == null) {
            throw new IllegalArgumentException("CPF e Serviços não podem ser nulos em TelaListaBilhetes.");
        }

        this.cpfCliente = cpf;
        this.reservaServico = reservaServico;
        this.clienteServico = clienteServico;
        this.pecaServico = pecaServico;

        configurarTelaVisual();
    }

    /**
     * Configura os componentes visuais e o layout da tela.
     * Este método orquestra a busca dos dados e a construção da lista de bilhetes.
     */
    private void configurarTelaVisual() {
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        // Cabeçalho com botão de voltar, título e logo
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(Constantes.AZUL_ESCURO);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JButton btnVoltarUI = new JButton("VOLTAR");
        btnVoltarUI.setFont(new Font("Arial", Font.BOLD, 18));
        btnVoltarUI.setForeground(Constantes.AZUL_CLARO);
        btnVoltarUI.setContentAreaFilled(false);
        btnVoltarUI.setBorderPainted(false);
        btnVoltarUI.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltarUI.addActionListener(e -> voltarParaTelaPrincipal());

        cabecalho.add(btnVoltarUI, BorderLayout.WEST);
        
        JLabel lblTituloTela = new JLabel("MEUS BILHETES");
        lblTituloTela.setFont(new Font("Arial", Font.BOLD, 32));
        lblTituloTela.setForeground(Constantes.AMARELO);
        lblTituloTela.setHorizontalAlignment(SwingConstants.CENTER);
        cabecalho.add(lblTituloTela, BorderLayout.CENTER);

        cabecalho.add(new LogoTeatro(), BorderLayout.EAST);
        add(cabecalho, BorderLayout.NORTH);

        // Painel que conterá a lista de cards de bilhetes, com rolagem
        JPanel painelConteudoBilhetes = new JPanel();
        painelConteudoBilhetes.setLayout(new BoxLayout(painelConteudoBilhetes, BoxLayout.Y_AXIS));
        painelConteudoBilhetes.setBackground(Constantes.AZUL_ESCURO);
        painelConteudoBilhetes.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));

        // Ponto de interação com o núcleo: chama o serviço para buscar os bilhetes.
        List<Bilhete> listaDeBilhetes = this.reservaServico.buscarBilhetesCliente(this.cpfCliente);

        if (listaDeBilhetes == null || listaDeBilhetes.isEmpty()) {
            // Caso nenhum bilhete seja encontrado, exibe uma mensagem informativa.
            JLabel lblVazio = new JLabel("Nenhum bilhete encontrado para o CPF informado.");
            lblVazio.setFont(new Font("Arial", Font.PLAIN, 24));
            lblVazio.setForeground(Color.WHITE);
            lblVazio.setAlignmentX(Component.CENTER_ALIGNMENT);
            painelConteudoBilhetes.add(Box.createVerticalStrut(100));
            painelConteudoBilhetes.add(lblVazio);
        } else {
            // Para cada objeto Bilhete retornado pelo serviço, cria um componente CardBilhete.
            for (Bilhete bilhete : listaDeBilhetes) {
                CardBilhete card = new CardBilhete(bilhete);
                
                // Adiciona a ação de clique ao botão "VISUALIZAR" do card.
                card.addActionListener(actionEvent -> {
                    // A variável 'card' é capturada pelo lambda, garantindo que obtemos
                    // o bilhete do card correto, evitando o ClassCastException.
                    Bilhete bilheteDoCard = card.getBilhete(); 
                    DialogoDetalhesBilhete dialogo = new DialogoDetalhesBilhete(
                        (Frame) SwingUtilities.getWindowAncestor(this),
                        bilheteDoCard
                    );
                    dialogo.setVisible(true);
                });

                painelConteudoBilhetes.add(card);
                painelConteudoBilhetes.add(Box.createVerticalStrut(15));
            }
        }

        JScrollPane scrollPane = new JScrollPane(painelConteudoBilhetes);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(Constantes.AZUL_ESCURO);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Navega de volta para a tela principal da aplicação.
     */
    private void voltarParaTelaPrincipal() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaPrincipal(this.clienteServico, this.pecaServico, this.reservaServico));
        frame.revalidate();
        frame.repaint();
    }
}