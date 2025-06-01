package com.teatroabc.componentes;

import com.teatroabc.constantes.Constantes;
import com.teatroabc.modelos.Peca;
import com.teatroabc.utilitarios.CarregadorImagem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.ImageIcon;

public class CardPeca extends JPanel {
    private Peca peca;
    private boolean modoSelecao = false;
    private boolean selecionado = false;
    private ImageIcon imagem;
    private ActionListener actionListener;

    public CardPeca(Peca peca) {
        this.peca = peca;
        this.imagem = CarregadorImagem.carregar(peca.getCaminhoImagem(), 350, 350);

        setPreferredSize(new Dimension(350, 450));
        setBackground(peca.getCorFundo());
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (modoSelecao && actionListener != null) {
                    actionListener.actionPerformed(new ActionEvent(CardPeca.this, 0, ""));
                }
            }
        });
    }

    public void setSelecao(boolean modoSelecao) {
        this.modoSelecao = modoSelecao;
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
        repaint();
    }

    public void addActionListener(ActionListener listener) {
        this.actionListener = listener;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Desenhar imagem ou placeholder
        if (imagem != null) {
            g2d.drawImage(imagem.getImage(), 0, 0, getWidth(), getHeight() - 100, this);
        } else {
            // Placeholder with error message
            g2d.setColor(Color.RED);
            g2d.fillRect(0, 0, getWidth(), getHeight() - 100);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            String errorMsg = "Imagem não carregada";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(errorMsg)) / 2;
            int y = (getHeight() - 100) / 2;
            g2d.drawString(errorMsg, x, y);
        }

        // Área colorida inferior
        g2d.setColor(peca.getCorFundo());
        g2d.fillRect(0, getHeight() - 100, getWidth(), 100);

        // Título
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();

        if (peca.getTitulo().contains("MORTE")) {
            // Título em duas linhas
            String[] linhas = {"MORTE E VIDA", "SEVERINA"};
            int y = getHeight() - 50;
            for (String linha : linhas) {
                int x = (getWidth() - fm.stringWidth(linha)) / 2;
                g2d.drawString(linha, x, y);
                y += 25;
            }
        } else {
            int x = (getWidth() - fm.stringWidth(peca.getTitulo())) / 2;
            g2d.drawString(peca.getTitulo(), x, getHeight() - 40);

            // Subtítulo
            if (!peca.getSubtitulo().isEmpty()) {
                g2d.setFont(new Font("Arial", Font.PLAIN, 16));
                fm = g2d.getFontMetrics();
                x = (getWidth() - fm.stringWidth(peca.getSubtitulo())) / 2;
                g2d.drawString(peca.getSubtitulo(), x, getHeight() - 15);
            }
        }

        // Borda de seleção
        if (modoSelecao && selecionado) {
            g2d.setColor(Constantes.AMARELO);
            g2d.setStroke(new BasicStroke(4));
            g2d.drawRect(2, 2, getWidth() - 4, getHeight() - 4);
        }

        g2d.dispose();
    }
}