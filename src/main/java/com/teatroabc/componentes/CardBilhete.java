package com.teatroabc.componentes;

import com.teatroabc.constantes.Constantes;
import com.teatroabc.modelos.Bilhete;
import com.teatroabc.modelos.Assento;
import com.teatroabc.utilitarios.FormatadorData;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.util.List;

public class CardBilhete extends JPanel {
    private ActionListener actionListener;
    private Bilhete bilhete;

    public CardBilhete(Bilhete bilhete) {
        this.bilhete = bilhete;
        setLayout(new BorderLayout());
        setBackground(new Color(52, 73, 94));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        
        configurarCard();
    }
    
    private void configurarCard() {
        JPanel painelInfo = new JPanel(new GridBagLayout());
        painelInfo.setBackground(new Color(52, 73, 94));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(3, 0, 3, 30);

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblTituloPeca = new JLabel(bilhete.getPeca().getTitulo());
        lblTituloPeca.setFont(new Font("Arial", Font.BOLD, 20));
        lblTituloPeca.setForeground(Color.WHITE);
        painelInfo.add(lblTituloPeca, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 4;
        gbc.fill = GridBagConstraints.VERTICAL;
        JPanel miniCodigoBarras = criarMiniCodigoBarras(bilhete.getCodigoBarras());
        painelInfo.add(miniCodigoBarras, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        JLabel lblData = new JLabel("Data");
        lblData.setFont(new Font("Arial", Font.PLAIN, 14));
        lblData.setForeground(Color.LIGHT_GRAY);
        painelInfo.add(lblData, gbc);

        gbc.gridy = 2;
        JLabel lblDataValor = new JLabel(FormatadorData.formatar(bilhete.getPeca().getDataHora()));
        lblDataValor.setFont(new Font("Arial", Font.BOLD, 16));
        lblDataValor.setForeground(Color.WHITE);
        painelInfo.add(lblDataValor, gbc);

        gbc.gridy = 3;
        JLabel lblAssentos = new JLabel("Assentos");
        lblAssentos.setFont(new Font("Arial", Font.PLAIN, 14));
        lblAssentos.setForeground(Color.LIGHT_GRAY);
        painelInfo.add(lblAssentos, gbc);

        gbc.gridy = 4;
        List<Assento> assentos = bilhete.getAssentos();
        String assentosTexto = assentos.isEmpty() ? "N/A" : 
            assentos.stream()
                .map(a -> a.getCodigo())
                .reduce((a, b) -> a + ", " + b)
                .orElse("N/A");
        JLabel lblAssentosValor = new JLabel(assentosTexto);
        lblAssentosValor.setFont(new Font("Arial", Font.BOLD, 16));
        lblAssentosValor.setForeground(Color.WHITE);
        painelInfo.add(lblAssentosValor, gbc);

        add(painelInfo, BorderLayout.CENTER);

        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotao.setBackground(new Color(52, 73, 94));
        
        BotaoAnimado btnVisualizar = new BotaoAnimado("VISUALIZAR",
            Constantes.AZUL_CLARO, new Color(70, 130, 180), new Dimension(120, 40));
        btnVisualizar.setFont(new Font("Arial", Font.BOLD, 14));
        btnVisualizar.addActionListener(e -> {
            if (actionListener != null) {
                actionListener.actionPerformed(e);
            }
        });
        
        painelBotao.add(btnVisualizar);
        add(painelBotao, BorderLayout.EAST);
    }

    private JPanel criarMiniCodigoBarras(String codigo) {
        JPanel painelBarras = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                int x = 5;
                int larguraBarra = 2;
                int espacamento = 1;
                
                g2d.setColor(Color.WHITE);
                
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

    public void addActionListener(ActionListener listener) {
        this.actionListener = listener;
    }
    
    public Bilhete getBilhete() {
        return bilhete;
    }
}