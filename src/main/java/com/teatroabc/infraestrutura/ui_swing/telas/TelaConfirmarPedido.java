package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.infraestrutura.ui_swing.componentes.*;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.modelos.*;
import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.aplicacao.servicos.ReservaServico;
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorMoeda;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class TelaConfirmarPedido extends JPanel {
    private Peca peca;
    private Cliente cliente;
    private List<Assento> assentos;
    private Turno turnoSelecionado;
    private ReservaServico reservaServico;
    private static final double DESCONTO_ABC = 0.05; // 5% de desconto

    public TelaConfirmarPedido(Peca peca, Cliente cliente, List<Assento> assentos) {
        this(peca, cliente, assentos, null);
    }
    
    public TelaConfirmarPedido(Peca peca, Cliente cliente, List<Assento> assentos, Turno turno) {
        this.peca = peca;
        this.cliente = cliente;
        this.assentos = assentos;
        this.turnoSelecionado = turno;
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

        // Título - CORRIGIDO para não quebrar
        JLabel titulo = new JLabel("CONFIRMAR PEDIDO");
        titulo.setFont(new Font("Arial", Font.BOLD, 48)); // Reduzido de FONTE_TITULO
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        containerPrincipal.add(Box.createVerticalStrut(40));
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
        painel.setMaximumSize(new Dimension(700, 500));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 20, 10, 20);

        int linha = 0;

        // Se for membro ABC, mostrar badge
        if (cliente.isMembroABC()) {
            gbc.gridx = 0;
            gbc.gridy = linha;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            
            JPanel badgeABC = criarBadgeABC();
            painel.add(badgeABC, gbc);
            
            linha++;
            gbc.gridwidth = 1;
            gbc.anchor = GridBagConstraints.WEST;
        }

        // Peça
        gbc.gridx = 0;
        gbc.gridy = linha;
        JLabel lblPecaTitulo = new JLabel("Peça");
        lblPecaTitulo.setFont(new Font("Arial", Font.PLAIN, 20));
        lblPecaTitulo.setForeground(Color.LIGHT_GRAY);
        painel.add(lblPecaTitulo, gbc);

        gbc.gridx = 1;
        JLabel lblPecaValor = new JLabel(peca.getTitulo());
        lblPecaValor.setFont(new Font("Arial", Font.BOLD, 20));
        lblPecaValor.setForeground(Color.WHITE);
        painel.add(lblPecaValor, gbc);

        linha++;

        // Turno (se especificado)
        if (turnoSelecionado != null) {
            gbc.gridx = 0;
            gbc.gridy = linha;
            JLabel lblTurnoTitulo = new JLabel("Turno");
            lblTurnoTitulo.setFont(new Font("Arial", Font.PLAIN, 20));
            lblTurnoTitulo.setForeground(Color.LIGHT_GRAY);
            painel.add(lblTurnoTitulo, gbc);

            gbc.gridx = 1;
            JLabel lblTurnoValor = new JLabel(turnoSelecionado.toString());
            lblTurnoValor.setFont(new Font("Arial", Font.BOLD, 20));
            lblTurnoValor.setForeground(Color.WHITE);
            painel.add(lblTurnoValor, gbc);

            linha++;
        }

        // Assentos
        gbc.gridx = 0;
        gbc.gridy = linha;
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

        linha++;

        // Subtotal
        double subtotal = assentos.stream().mapToDouble(Assento::getPreco).sum();
        
        gbc.gridx = 0;
        gbc.gridy = linha;
        JLabel lblSubtotalTitulo = new JLabel("Subtotal");
        lblSubtotalTitulo.setFont(new Font("Arial", Font.PLAIN, 20));
        lblSubtotalTitulo.setForeground(Color.LIGHT_GRAY);
        painel.add(lblSubtotalTitulo, gbc);

        gbc.gridx = 1;
        JLabel lblSubtotalValor = new JLabel(FormatadorMoeda.formatar(subtotal));
        lblSubtotalValor.setFont(new Font("Arial", Font.PLAIN, 20));
        lblSubtotalValor.setForeground(Color.WHITE);
        painel.add(lblSubtotalValor, gbc);

        linha++;

        // Desconto ABC GOLD (se aplicável)
        if (cliente.isMembroABC()) {
            double desconto = subtotal * DESCONTO_ABC;
            
            gbc.gridx = 0;
            gbc.gridy = linha;
            JLabel lblDescontoTitulo = new JLabel("Desconto ABC GOLD (5%)");
            lblDescontoTitulo.setFont(new Font("Arial", Font.PLAIN, 18));
            lblDescontoTitulo.setForeground(Constantes.AMARELO);
            painel.add(lblDescontoTitulo, gbc);

            gbc.gridx = 1;
            JLabel lblDescontoValor = new JLabel("- " + FormatadorMoeda.formatar(desconto));
            lblDescontoValor.setFont(new Font("Arial", Font.PLAIN, 18));
            lblDescontoValor.setForeground(Constantes.AMARELO);
            painel.add(lblDescontoValor, gbc);

            linha++;
        }

        // Linha separadora
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JSeparator separador = new JSeparator();
        separador.setForeground(Color.GRAY);
        painel.add(separador, gbc);

        linha++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 1;

        // Total
        double total = cliente.isMembroABC() ? subtotal * (1 - DESCONTO_ABC) : subtotal;
        
        gbc.gridx = 0;
        gbc.gridy = linha;
        JLabel lblTotalTitulo = new JLabel("Total");
        lblTotalTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTotalTitulo.setForeground(Color.WHITE);
        painel.add(lblTotalTitulo, gbc);

        gbc.gridx = 1;
        JLabel lblTotalValor = new JLabel(FormatadorMoeda.formatar(total));
        lblTotalValor.setFont(new Font("Arial", Font.BOLD, 28));
        lblTotalValor.setForeground(cliente.isMembroABC() ? Constantes.AMARELO : Color.WHITE);
        painel.add(lblTotalValor, gbc);

        return painel;
    }

    private JPanel criarBadgeABC() {
        JPanel badge = new JPanel();
        badge.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        badge.setBackground(Constantes.AMARELO);
        badge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 3),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));

        // Estrela
        JLabel lblEstrela = new JLabel("⭐");
        lblEstrela.setFont(new Font("Arial", Font.PLAIN, 24));

        // Texto
        JLabel lblTexto = new JLabel("MEMBRO ABC GOLD");
        lblTexto.setFont(new Font("Arial", Font.BOLD, 18));
        lblTexto.setForeground(Color.BLACK);

        // Outra estrela
        JLabel lblEstrela2 = new JLabel("⭐");
        lblEstrela2.setFont(new Font("Arial", Font.PLAIN, 24));

        badge.add(lblEstrela);
        badge.add(lblTexto);
        badge.add(lblEstrela2);

        return badge;
    }

    private void confirmar() {
        try {
            // Criar bilhete com desconto se aplicável - PASSANDO O TURNO
            String turno = turnoSelecionado != null ? turnoSelecionado.name() : "NOITE";
            Bilhete bilhete = reservaServico.criarReserva(peca, cliente, assentos, turno);

            double subtotal = assentos.stream().mapToDouble(Assento::getPreco).sum();
            
            String mensagem = "Compra realizada com sucesso!\n" +
                            "Código do bilhete: " + bilhete.getCodigoBarras();
            
            if (turnoSelecionado != null) {
                mensagem += "\nTurno: " + turnoSelecionado.toString();
            }
            
            if (cliente.isMembroABC()) {
                double valorEconomizado = subtotal * DESCONTO_ABC;
                mensagem += "\n\nComo membro ABC GOLD, você economizou " + 
                           FormatadorMoeda.formatar(valorEconomizado) + " nesta compra!";
            }

            JOptionPane.showMessageDialog(this, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);

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
            e.printStackTrace();
        }
    }

    private void voltar() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaSelecionarAssento(peca));
        frame.revalidate();
        frame.repaint();
    }
}