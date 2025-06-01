package com.teatroabc.telas;

import com.teatroabc.componentes.*;
import com.teatroabc.constantes.Constantes;
import com.teatroabc.modelos.Bilhete;
import com.teatroabc.modelos.Assento;
import com.teatroabc.servicos.ReservaServico;
import com.teatroabc.servicos.interfaces.IReservaServico;
import com.teatroabc.utilitarios.FormatadorData;
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
        containerBilhetes.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));

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
                containerBilhetes.add(criarCardBilheteCompleto(bilhete));
                containerBilhetes.add(Box.createVerticalStrut(15));
            }
        }

        JScrollPane scrollPane = new JScrollPane(containerBilhetes);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(Constantes.AZUL_ESCURO);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel criarCardBilheteCompleto(Bilhete bilhete) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(52, 73, 94));
        card.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // Painel principal com informações
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(new Color(52, 73, 94));

        // Nome da peça (lado esquerdo)
        JLabel lblTituloPeca = new JLabel(bilhete.getPeca().getTitulo());
        lblTituloPeca.setFont(new Font("Arial", Font.BOLD, 22));
        lblTituloPeca.setForeground(Color.WHITE);
        
        // Informações centrais
        JPanel painelCentral = new JPanel(new GridLayout(2, 2, 20, 5));
        painelCentral.setBackground(new Color(52, 73, 94));
        
        // Data
        JLabel lblDataRotulo = new JLabel("Data");
        lblDataRotulo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblDataRotulo.setForeground(Color.LIGHT_GRAY);
        
        JLabel lblDataValor = new JLabel(FormatadorData.formatar(bilhete.getPeca().getDataHora()));
        lblDataValor.setFont(new Font("Arial", Font.BOLD, 16));
        lblDataValor.setForeground(Color.WHITE);

        // Assentos
        JLabel lblAssentosRotulo = new JLabel("Assentos");
        lblAssentosRotulo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblAssentosRotulo.setForeground(Color.LIGHT_GRAY);
        
        List<Assento> assentos = bilhete.getAssentos();
        String assentosTexto = assentos.isEmpty() ? "N/A" : 
            assentos.stream()
                .map(a -> a.getCodigo())
                .reduce((a, b) -> a + ", " + b)
                .orElse("N/A");
        JLabel lblAssentosValor = new JLabel(assentosTexto);
        lblAssentosValor.setFont(new Font("Arial", Font.BOLD, 16));
        lblAssentosValor.setForeground(Color.WHITE);

        painelCentral.add(lblDataRotulo);
        painelCentral.add(lblAssentosRotulo);
        painelCentral.add(lblDataValor);
        painelCentral.add(lblAssentosValor);

        // Layout do painel principal
        painelPrincipal.add(lblTituloPeca, BorderLayout.WEST);
        painelPrincipal.add(painelCentral, BorderLayout.CENTER);

        // Código de barras visual e botão no lado direito
        JPanel painelDireito = new JPanel(new BorderLayout());
        painelDireito.setBackground(new Color(52, 73, 94));
        
        // Mini código de barras
        JPanel miniCodigoBarras = criarMiniCodigoBarras(bilhete.getCodigoBarras());
        painelDireito.add(miniCodigoBarras, BorderLayout.CENTER);
        
        // Botão Visualizar
        BotaoAnimado btnVisualizar = new BotaoAnimado("VISUALIZAR",
            Constantes.AZUL_CLARO, new Color(70, 130, 180), new Dimension(120, 35));
        btnVisualizar.setFont(new Font("Arial", Font.BOLD, 12));
        btnVisualizar.addActionListener(e -> {
            DialogoDetalhesBilhete dialogo = new DialogoDetalhesBilhete(bilhete);
            dialogo.setVisible(true);
        });
        
        painelDireito.add(btnVisualizar, BorderLayout.EAST);

        card.add(painelPrincipal, BorderLayout.CENTER);
        card.add(painelDireito, BorderLayout.EAST);

        return card;
    }

    private JPanel criarMiniCodigoBarras(String codigo) {
        JPanel painelBarras = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Desenhar código de barras pequeno
                int x = 5;
                int larguraBarra = 2;
                int espacamento = 1;
                
                g2d.setColor(Color.WHITE);
                
                // Desenhar apenas algumas barras representativas
                for (int i = 0; i < 15 && x < getWidth() - 10; i++) {
                    char c = codigo.charAt(i % codigo.length());
                    int altura = 20 + (c % 10);
                    
                    g2d.fillRect(x, (getHeight() - altura) / 2, larguraBarra, altura);
                    x += larguraBarra + espacamento;
                }
                
                g2d.dispose();
            }
        };
        
        painelBarras.setPreferredSize(new Dimension(60, 60));
        painelBarras.setBackground(Color.BLACK);
        painelBarras.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        
        return painelBarras;
    }

    private void voltar() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaPrincipal());
        frame.revalidate();
        frame.repaint();
    }
}