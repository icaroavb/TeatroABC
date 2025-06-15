package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico;
import com.teatroabc.aplicacao.interfaces.ISessaoServico;
import com.teatroabc.dominio.modelos.Bilhete;
import com.teatroabc.infraestrutura.ui_swing.componentes.CardBilhete;
import com.teatroabc.infraestrutura.ui_swing.componentes.DialogoDetalhesBilhete;
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes; // NOVO IMPORT
import java.awt.*;
import java.util.List;
import javax.swing.*;

/**
 * Tela responsável por exibir uma lista de todos os bilhetes pertencentes a um cliente.
 * Na Arquitetura Hexagonal, esta classe atua como um Adaptador Primário, utilizando
 * o IReservaServico para buscar os dados e traduzindo-os em uma representação visual.
 */
public class TelaListaBilhetes extends JPanel {
    private final String cpfCliente;

    // Serviços injetados via construtor
    private final IReservaServico reservaServico;
    private final IClienteServico clienteServico;
    private final IPecaServico pecaServico;
    private final ISessaoServico sessaoServico; // NOVO CAMPO

    /**
     * Construtor da TelaListaBilhetes.
     * @param cpf O CPF do cliente para o qual os bilhetes serão listados.
     * @param reservaServico O serviço para buscar os bilhetes.
     * @param clienteServico O serviço de cliente.
     * @param pecaServico O serviço de peça.
     * @param sessaoServico O serviço de sessão.
     * @throws IllegalArgumentException se o CPF ou algum dos serviços for nulo.
     */
    public TelaListaBilhetes(String cpf, IReservaServico reservaServico,
                             IClienteServico clienteServico, IPecaServico pecaServico,
                             ISessaoServico sessaoServico) { // Construtor atualizado
        if (cpf == null || cpf.trim().isEmpty() || reservaServico == null || clienteServico == null || pecaServico == null || sessaoServico == null) {
            throw new IllegalArgumentException("CPF e todos os Serviços não podem ser nulos em TelaListaBilhetes.");
        }

        this.cpfCliente = cpf;
        this.reservaServico = reservaServico;
        this.clienteServico = clienteServico;
        this.pecaServico = pecaServico;
        this.sessaoServico = sessaoServico; // Armazena o novo serviço

        configurarTelaVisual();
    }

    /**
     * Configura os componentes visuais e o layout da tela.
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

        // Painel para a lista de bilhetes com rolagem
        JPanel painelConteudoBilhetes = new JPanel();
        painelConteudoBilhetes.setLayout(new BoxLayout(painelConteudoBilhetes, BoxLayout.Y_AXIS));
        painelConteudoBilhetes.setBackground(Constantes.AZUL_ESCURO);
        painelConteudoBilhetes.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));

        // Busca os bilhetes usando o serviço injetado.
        List<Bilhete> listaDeBilhetes = this.reservaServico.buscarBilhetesCliente(this.cpfCliente);

        if (listaDeBilhetes == null || listaDeBilhetes.isEmpty()) {
            JLabel lblVazio = new JLabel("Nenhum bilhete encontrado para o CPF informado.");
            lblVazio.setFont(new Font("Arial", Font.PLAIN, 24));
            lblVazio.setForeground(Color.WHITE);
            lblVazio.setAlignmentX(Component.CENTER_ALIGNMENT);
            painelConteudoBilhetes.add(Box.createVerticalStrut(100));
            painelConteudoBilhetes.add(lblVazio);
        } else {
            for (Bilhete bilhete : listaDeBilhetes) {
                CardBilhete card = new CardBilhete(bilhete);
                
                card.addActionListener(actionEvent -> {
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
     * A chamada ao construtor da TelaPrincipal foi atualizada para incluir o ISessaoServico.
     */
    private void voltarParaTelaPrincipal() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        // Atualiza a chamada para passar todos os serviços necessários para a TelaPrincipal.
        frame.setContentPane(new TelaPrincipal(
            this.clienteServico, 
            this.pecaServico, 
            this.reservaServico,
            this.sessaoServico // Passando o novo serviço
        ));
        frame.revalidate();
        frame.repaint();
    }
}