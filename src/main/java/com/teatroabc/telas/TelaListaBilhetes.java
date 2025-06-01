package com.teatroabc.telas;

import com.teatroabc.componentes.*;
import com.teatroabc.constantes.Constantes;
import com.teatroabc.modelos.Bilhete;
import com.teatroabc.servicos.ReservaServico;
import com.teatroabc.servicos.interfaces.IReservaServico;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TelaListaBilhetes extends JPanel {
    private String cpf;
    private IReservaServico reservaServico;

    public TelaListaBilhetes(String cpf) {
        this.cpf = cpf;
        this.reservaServico = new ReservaServico();
        configurarTela();
    }

    private void configurarTela() {
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        // Cabeçalho
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(Constantes.AZUL_ESCURO);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JButton btnVoltar = new JButton("VOLTAR");
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 18));
        btnVoltar.setForeground(Constantes.AZUL_CLARO);
        btnVoltar.setBackground(Constantes.AZUL_ESCURO);
        btnVoltar.setBorderPainted(false);
        btnVoltar.setFocusPainted(false);
        btnVoltar.setContentAreaFilled(false);
        btnVoltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltar.addActionListener(e -> voltar());

        cabecalho.add(btnVoltar, BorderLayout.WEST);
        cabecalho.add(new LogoTeatro(), BorderLayout.EAST);

        add(cabecalho, BorderLayout.NORTH);

        // Lista de bilhetes
        JPanel containerBilhetes = new JPanel();
        containerBilhetes.setLayout(new BoxLayout(containerBilhetes, BoxLayout.Y_AXIS));
        containerBilhetes.setBackground(Constantes.AZUL_ESCURO);
        containerBilhetes.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        List<Bilhete> bilhetes = reservaServico.buscarBilhetesCliente(cpf);

        if (bilhetes.isEmpty()) {
            JLabel lblVazio = new JLabel("Nenhum bilhete encontrado");
            lblVazio.setFont(new Font("Arial", Font.PLAIN, 24));
            lblVazio.setForeground(Color.WHITE);
            lblVazio.setAlignmentX(Component.CENTER_ALIGNMENT);
            containerBilhetes.add(Box.createVerticalStrut(100));
            containerBilhetes.add(lblVazio);
        } else {
            for (Bilhete bilhete : bilhetes) {
                containerBilhetes.add(criarCardBilhete(bilhete));
                containerBilhetes.add(Box.createVerticalStrut(20));
            }
        }

        JScrollPane scrollPane = new JScrollPane(containerBilhetes);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel criarCardBilhete(Bilhete bilhete) {
        CardBilhete card = new CardBilhete(bilhete);
        card.addActionListener(e -> {
            DialogoDetalhesBilhete dialogo = new DialogoDetalhesBilhete(bilhete);
            dialogo.setVisible(true);
        });
        return card;
    }

    private void voltar() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaPrincipal());
        frame.revalidate();
        frame.repaint();
    }
}
