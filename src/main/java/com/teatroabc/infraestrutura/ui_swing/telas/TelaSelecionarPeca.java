package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.infraestrutura.ui_swing.componentes.*;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.aplicacao.servicos.PecaServico;
import javax.swing.*;
import java.awt.*;

public class TelaSelecionarPeca extends JPanel {
    private PecaServico pecaServico;
    private Peca pecaSelecionada;
    private BotaoAnimado btnContinuar;

    public TelaSelecionarPeca() {
        this.pecaServico = new PecaServico();
        configurarTela();
    }

    private void configurarTela() {
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        // Cabeçalho
        JPanel painelCabecalho = new JPanel(new BorderLayout());
        painelCabecalho.setBackground(Constantes.AZUL_ESCURO);
        painelCabecalho.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Botão Voltar
        JButton btnVoltar = criarBotaoVoltar();
        painelCabecalho.add(btnVoltar, BorderLayout.WEST);

        // Logo
        painelCabecalho.add(new LogoTeatro(), BorderLayout.EAST);

        add(painelCabecalho, BorderLayout.NORTH);

        // Container principal
        JPanel containerPrincipal = new JPanel();
        containerPrincipal.setLayout(new BoxLayout(containerPrincipal, BoxLayout.Y_AXIS));
        containerPrincipal.setBackground(Constantes.AZUL_ESCURO);

        // Título
        JLabel titulo = new JLabel("ESCOLHA UMA PEÇA");
        titulo.setFont(Constantes.FONTE_TITULO);
        titulo.setForeground(Constantes.AMARELO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        containerPrincipal.add(titulo);

        // Espaço
        containerPrincipal.add(Box.createVerticalStrut(50));

        // Cards das peças
        JPanel painelPecas = new JPanel(new GridLayout(1, 3, 30, 0));
        painelPecas.setBackground(Constantes.AZUL_ESCURO);
        painelPecas.setMaximumSize(new Dimension(1200, 450));

        adicionarCardsPecas(painelPecas);

        containerPrincipal.add(painelPecas);

        // Espaço
        containerPrincipal.add(Box.createVerticalStrut(50));

        // Botão Continuar
        btnContinuar = new BotaoAnimado("CONTINUAR",
                Constantes.LARANJA, Constantes.AMARELO, new Dimension(300, 60));
        btnContinuar.setFont(new Font("Arial", Font.BOLD, 24));
        btnContinuar.setEnabled(false);
        btnContinuar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnContinuar.addActionListener(e -> abrirSelecaoAssento());

        containerPrincipal.add(btnContinuar);

        add(containerPrincipal, BorderLayout.CENTER);
    }

    private void adicionarCardsPecas(JPanel painel) {
        for (Peca peca : pecaServico.buscarTodasPecas()) {
            CardPeca card = new CardPeca(peca);
            card.setSelecao(true); // Habilita modo seleção
            card.addActionListener(e -> {
                pecaSelecionada = peca;
                btnContinuar.setEnabled(true);

                // Desmarcar outros cards
                for (Component c : painel.getComponents()) {
                    if (c instanceof CardPeca && c != card) {
                        ((CardPeca) c).setSelecionado(false);
                    }
                }
                card.setSelecionado(true);
            });
            painel.add(card);
        }
    }

    private JButton criarBotaoVoltar() {
        JButton btnVoltar = new JButton("VOLTAR");
        btnVoltar.setFont(new Font("Arial", Font.PLAIN, 18));
        btnVoltar.setForeground(Constantes.AZUL_CLARO);
        btnVoltar.setBackground(Constantes.AZUL_ESCURO);
        btnVoltar.setBorderPainted(false);
        btnVoltar.setFocusPainted(false);
        btnVoltar.setContentAreaFilled(false);
        btnVoltar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnVoltar.addActionListener(e -> voltar());

        return btnVoltar;
    }

    private void abrirSelecaoAssento() {
        if (pecaSelecionada != null) {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setContentPane(new TelaSelecionarAssento(pecaSelecionada));
            frame.revalidate();
            frame.repaint();
        }
    }

    private void voltar() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaPrincipal());
        frame.revalidate();
        frame.repaint();
    }
}
