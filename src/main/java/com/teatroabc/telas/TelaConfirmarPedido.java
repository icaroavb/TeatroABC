package com.teatroabc.telas;

import com.teatroabc.componentes.*;
import com.teatroabc.constantes.Constantes;
import com.teatroabc.modelos.*;
import com.teatroabc.servicos.ReservaServico;
import com.teatroabc.servicos.interfaces.IReservaServico;
import com.teatroabc.utilitarios.FormatadorMoeda;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class TelaConfirmarPedido extends JPanel {
    private Peca peca;
    private Cliente cliente;
    private List<Assento> assentos;
    private IReservaServico reservaServico;

    public TelaConfirmarPedido(Peca peca, Cliente cliente, List<Assento> assentos) {
        this.peca = peca;
        this.cliente = cliente;
        this.assentos = assentos;
        this.reservaServico = new ReservaServico();
        configurarTela();
    }

    private void configurarTela() {
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        // Container principal
        JPanel containerPrincipal = new JPanel();
        containerPrincipal.setLayout(new BoxLayout(containerPrincipal, BoxLayout.Y_AXIS));
        containerPrincipal.setBackground(Constantes.AZUL_ESCURO);

        // Logo
        JPanel painelLogo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelLogo.setBackground(Constantes.AZUL_ESCURO);
        painelLogo.add(new LogoTeatro());
        containerPrincipal.add(Box.createVerticalStrut(50));
        containerPrincipal.add(painelLogo);

        // Título
        JLabel titulo = new JLabel("CONFIRMAR PEDIDO");
        titulo.setFont(Constantes.FONTE_TITULO);
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        containerPrincipal.add(Box.createVerticalStrut(60));
        containerPrincipal.add(titulo);

        // Detalhes do pedido
        JPanel painelDetalhes = criarPainelDetalhes();
        containerPrincipal.add(Box.createVerticalStrut(40));
        containerPrincipal.add(painelDetalhes);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        painelBotoes.setBackground(Constantes.AZUL_ESCURO);

        BotaoAnimado btnVoltar = new BotaoAnimado("VOLTAR",
                Constantes.AZUL_CLARO, new Color(70, 130, 180), new Dimension(200, 60));
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 24));
        btnVoltar.addActionListener(e -> voltar());

        BotaoAnimado btnConfirmar = new BotaoAnimado("CONFIRMAR",
                Constantes.LARANJA, Constantes.AMARELO, new Dimension(250, 60));
        btnConfirmar.setFont(new Font("Arial", Font.BOLD, 24));
        btnConfirmar.addActionListener(e -> confirmar());

        painelBotoes.add(btnVoltar);
        painelBotoes.add(btnConfirmar);

        containerPrincipal.add(Box.createVerticalStrut(60));
        containerPrincipal.add(painelBotoes);

        add(containerPrincipal, BorderLayout.CENTER);
    }

    private JPanel criarPainelDetalhes() {
        JPanel painel = new JPanel();
        painel.setBackground(new Color(52, 73, 94));
        painel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        painel.setLayout(new GridBagLayout());
        painel.setMaximumSize(new Dimension(600, 300));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 20, 10, 20);

        // Peça
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblPecaTitulo = new JLabel("Peça");
        lblPecaTitulo.setFont(new Font("Arial", Font.PLAIN, 20));
        lblPecaTitulo.setForeground(Color.LIGHT_GRAY);
        painel.add(lblPecaTitulo, gbc);

        gbc.gridx = 1;
        JLabel lblPecaValor = new JLabel(peca.getTitulo());
        lblPecaValor.setFont(new Font("Arial", Font.BOLD, 20));
        lblPecaValor.setForeground(Color.WHITE);
        painel.add(lblPecaValor, gbc);

        // Assentos
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblAssentosTitulo = new JLabel("Assentos");
        lblAssentosTitulo.setFont(new Font("Arial", Font.PLAIN, 20));
        lblAssentosTitulo.setForeground(Color.LIGHT_GRAY);
        painel.add(lblAssentosTitulo, gbc);

        gbc.gridx = 1;
        String assentosStr = assentos.stream()
                .map(Assento::getCodigo)
                .collect(Collectors.joining(", "));
        JLabel lblAssentosValor = new JLabel(assentosStr);
        lblAssentosValor.setFont(new Font("Arial", Font.BOLD, 20));
        lblAssentosValor.setForeground(Color.WHITE);
        painel.add(lblAssentosValor, gbc);

        // Total
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblTotalTitulo = new JLabel("Total");
        lblTotalTitulo.setFont(new Font("Arial", Font.PLAIN, 20));
        lblTotalTitulo.setForeground(Color.LIGHT_GRAY);
        painel.add(lblTotalTitulo, gbc);

        gbc.gridx = 1;
        double total = assentos.stream().mapToDouble(Assento::getPreco).sum();
        JLabel lblTotalValor = new JLabel(FormatadorMoeda.formatar(total));
        lblTotalValor.setFont(new Font("Arial", Font.BOLD, 24));
        lblTotalValor.setForeground(Color.WHITE);
        painel.add(lblTotalValor, gbc);

        return painel;
    }

    private void confirmar() {
        try {
            // Criar bilhete
            Bilhete bilhete = reservaServico.criarReserva(peca, cliente, assentos);

            JOptionPane.showMessageDialog(this,
                    "Compra realizada com sucesso!\nCódigo do bilhete: " + bilhete.getCodigoBarras(),
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);

            // Voltar para tela principal
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setContentPane(new TelaPrincipal());
            frame.revalidate();
            frame.repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao confirmar pedido: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void voltar() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaSelecionarAssento(peca));
        frame.revalidate();
        frame.repaint();
    }
}
