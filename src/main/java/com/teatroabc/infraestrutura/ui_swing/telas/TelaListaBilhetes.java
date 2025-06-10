package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAnimado; // Usado no CardBilhete
import com.teatroabc.infraestrutura.ui_swing.componentes.CardBilhete; // O CardBilhete em si
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.modelos.Bilhete;
import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico; // Interface do Serviço


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent; // Para o lambda do CardBilhete
import java.util.List;
import java.util.Collections; // Se precisar de Collections.emptyList()

public class TelaListaBilhetes extends JPanel {
    private final String cpfCliente; 

    // Serviços injetados
    private final IReservaServico reservaServico;
    private final IClienteServico clienteServico; // Adicionado para repassar
    private final IPecaServico pecaServico;       // Adicionado para repassar

    /**
     * Construtor da TelaListaBilhetes.
     * @param cpf O CPF do cliente para o qual os bilhetes serão listados.
     * @param reservaServico O serviço para buscar os bilhetes.
     * @param clienteServico O serviço de cliente (para repassar ao voltar para TelaPrincipal).
     * @param pecaServico O serviço de peça (para repassar ao voltar para TelaPrincipal).
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

    private void configurarTelaVisual() { // Renomeado de configurarTela
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        // Cabeçalho
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(Constantes.AZUL_ESCURO);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JButton btnVoltarUI = new JButton("VOLTAR");
        btnVoltarUI.setFont(new Font("Arial", Font.BOLD, 18)); // Consistência na fonte do botão voltar
        // ... (configurações do btnVoltarUI como antes) ...
        btnVoltarUI.setForeground(Constantes.AZUL_CLARO);
        btnVoltarUI.setBackground(Constantes.AZUL_ESCURO);
        btnVoltarUI.setBorderPainted(false);
        btnVoltarUI.setContentAreaFilled(false);
        btnVoltarUI.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltarUI.addActionListener(e -> voltarParaTelaPrincipal()); // Renomeado de voltar

        cabecalho.add(btnVoltarUI, BorderLayout.WEST);
        // Título da Tela
        JLabel lblTituloTela = new JLabel("MEUS BILHETES");
        lblTituloTela.setFont(new Font("Arial", Font.BOLD, 32));
        lblTituloTela.setForeground(Constantes.AMARELO);
        lblTituloTela.setHorizontalAlignment(SwingConstants.CENTER);
        cabecalho.add(lblTituloTela, BorderLayout.CENTER);

        cabecalho.add(new LogoTeatro(), BorderLayout.EAST);
        add(cabecalho, BorderLayout.NORTH);

        // Lista de bilhetes
        JPanel painelConteudoBilhetes = new JPanel(); // Renomeado de containerBilhetes
        painelConteudoBilhetes.setLayout(new BoxLayout(painelConteudoBilhetes, BoxLayout.Y_AXIS));
        painelConteudoBilhetes.setBackground(Constantes.AZUL_ESCURO);
        painelConteudoBilhetes.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));

        // Busca os bilhetes usando o serviço injetado
        
        List<Bilhete> listaDeBilhetes = this.reservaServico.buscarBilhetesCliente(this.cpfCliente);

        if (listaDeBilhetes == null || listaDeBilhetes.isEmpty()) { // Checagem de nulidade da lista também
            JLabel lblVazio = new JLabel("Nenhum bilhete encontrado para o CPF informado.");
            lblVazio.setFont(new Font("Arial", Font.PLAIN, 24));
            lblVazio.setForeground(Color.WHITE);
            lblVazio.setAlignmentX(Component.CENTER_ALIGNMENT);
            painelConteudoBilhetes.add(Box.createVerticalStrut(100));
            painelConteudoBilhetes.add(lblVazio);
        } else {
            for (Bilhete bilhete : listaDeBilhetes) {
                CardBilhete card = new CardBilhete(bilhete); // CardBilhete recebe o objeto Bilhete
                // Adiciona ActionListener ao CardBilhete para abrir o DialogoDetalhesBilhete
                card.addActionListener(actionEvent -> {
                    // O CardBilhete agora passa o evento, podemos obter o bilhete do card que originou
                    Bilhete bilheteDoCard = ((CardBilhete) actionEvent.getSource()).getBilhete();
                    DialogoDetalhesBilhete dialogo = new DialogoDetalhesBilhete(
                        (Frame) SwingUtilities.getWindowAncestor(this), // Passa o Frame pai
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
        scrollPane.setBackground(Constantes.AZUL_ESCURO); // Para o fundo do scroll pane em si
        scrollPane.getViewport().setOpaque(false); // Para que o fundo do painelConteudoBilhetes seja visível
        add(scrollPane, BorderLayout.CENTER);
    }

    // O método criarCardBilheteCompleto foi incorporado/substituído pela lógica
    private void voltarParaTelaPrincipal() { // Renomeado de voltar
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        // Ao voltar, recria TelaPrincipal passando os serviços
        frame.setContentPane(new TelaPrincipal(this.clienteServico, this.pecaServico, this.reservaServico));
        frame.revalidate();
        frame.repaint();
    }
}