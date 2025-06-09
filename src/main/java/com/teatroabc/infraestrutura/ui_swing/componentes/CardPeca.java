package com.teatroabc.infraestrutura.ui_swing.componentes;

import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.infraestrutura.ui_swing.util.CarregadorImagem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class CardPeca extends JPanel {
    private Peca peca;
    private boolean modoSelecao = false;
    private boolean selecionado = false;
    private BufferedImage imagem;
    private ActionListener actionListener;
    
    public CardPeca(Peca peca) {
        this.peca = peca;
        
        // Tentar carregar imagem, mas sempre criar um placeholder visual
        this.imagem = CarregadorImagem.carregar(peca.getCaminhoImagem());
        
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
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int areaImagemAltura = getHeight() - 100;
        
        // Área da imagem ou placeholder visual
        if (imagem != null) {
            // Desenhar imagem real se carregou
            g2d.drawImage(imagem, 0, 0, getWidth(), areaImagemAltura, this);
        } else {
            // Criar placeholder visual específico para cada peça
            desenharPlaceholderVisual(g2d, areaImagemAltura);
        }
        
        // Área colorida inferior com título
        g2d.setColor(peca.getCorFundo());
        g2d.fillRect(0, areaImagemAltura, getWidth(), 100);
        
        // Título da peça
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        
        if (peca.getTitulo().contains("MORTE")) {
            // Título em duas linhas para "MORTE E VIDA SEVERINA"
            String[] linhas = {"MORTE E VIDA", "SEVERINA"};
            int y = areaImagemAltura + 35;
            for (String linha : linhas) {
                int x = (getWidth() - fm.stringWidth(linha)) / 2;
                g2d.drawString(linha, x, y);
                y += 25;
            }
        } else {
            // Título normal
            int x = (getWidth() - fm.stringWidth(peca.getTitulo())) / 2;
            g2d.drawString(peca.getTitulo(), x, areaImagemAltura + 40);
            
            // Subtítulo se existir
            if (!peca.getSubtitulo().isEmpty()) {
                g2d.setFont(new Font("Arial", Font.PLAIN, 16));
                fm = g2d.getFontMetrics();
                x = (getWidth() - fm.stringWidth(peca.getSubtitulo())) / 2;
                g2d.drawString(peca.getSubtitulo(), x, areaImagemAltura + 65);
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
    
    private void desenharPlaceholderVisual(Graphics2D g2d, int altura) {
        // Fundo gradiente baseado na cor da peça
        Color corBase = peca.getCorFundo();
        Color corClara = new Color(
            Math.min(255, corBase.getRed() + 50),
            Math.min(255, corBase.getGreen() + 50),
            Math.min(255, corBase.getBlue() + 50)
        );
        Color corEscura = new Color(
            Math.max(0, corBase.getRed() - 30),
            Math.max(0, corBase.getGreen() - 30),
            Math.max(0, corBase.getBlue() - 30)
        );
        
        GradientPaint gradient = new GradientPaint(
            0, 0, corClara,
            getWidth(), altura, corEscura
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), altura);
        
        // Desenhar placeholder específico para cada peça
        desenharIconePeca(g2d, altura);
    }
    
    private void desenharIconePeca(Graphics2D g2d, int altura) {
        g2d.setColor(new Color(255, 255, 255, 150));
        g2d.setStroke(new BasicStroke(3));
        
        int centerX = getWidth() / 2;
        int centerY = altura / 2;
        
        if (peca.getTitulo().contains("WICKEDONIA")) {
            // Ícone de bruxa/mágica para Wickedonia
            desenharIconeMagico(g2d, centerX, centerY);
            
        } else if (peca.getTitulo().contains("HERMANOTEU")) {
            // Ícone de comédia para Hermanoteu
            desenharIconeComedia(g2d, centerX, centerY);
            
        } else if (peca.getTitulo().contains("MORTE")) {
            // Ícone dramático para Morte e Vida Severina
            desenharIconeDramatico(g2d, centerX, centerY);
            
        } else {
            // Ícone genérico de teatro
            desenharIconeTeatro(g2d, centerX, centerY);
        }
    }
    
    private void desenharIconeMagico(Graphics2D g2d, int x, int y) {
        // Chapéu de bruxa
        g2d.fillPolygon(
            new int[]{x - 30, x + 30, x},
            new int[]{y + 20, y + 20, y - 40},
            3
        );
        // Aba do chapéu
        g2d.fillOval(x - 40, y + 15, 80, 15);
        
        // Estrelas mágicas
        desenharEstrela(g2d, x - 50, y - 20, 8);
        desenharEstrela(g2d, x + 40, y - 10, 6);
        desenharEstrela(g2d, x + 20, y + 40, 5);
    }
    
    private void desenharIconeComedia(Graphics2D g2d, int x, int y) {
        // Máscara de comédia (sorrindo)
        g2d.fillOval(x - 40, y - 40, 80, 80);
        
        // Olhos
        g2d.setColor(Color.BLACK);
        g2d.fillOval(x - 25, y - 20, 15, 15);
        g2d.fillOval(x + 10, y - 20, 15, 15);
        
        // Sorriso
        g2d.setStroke(new BasicStroke(4));
        g2d.drawArc(x - 25, y - 5, 50, 30, 0, -180);
        
        // Voltar à cor branca para outros elementos
        g2d.setColor(new Color(255, 255, 255, 150));
        g2d.setStroke(new BasicStroke(3));
        
        // Elementos de comédia ao redor
        desenharRisada(g2d, x - 60, y - 60);
        desenharRisada(g2d, x + 60, y - 40);
        desenharRisada(g2d, x + 50, y + 50);
    }
    
    private void desenharIconeDramatico(Graphics2D g2d, int x, int y) {
        // Máscara de drama (triste)
        g2d.fillOval(x - 40, y - 40, 80, 80);
        
        // Olhos
        g2d.setColor(Color.BLACK);
        g2d.fillOval(x - 25, y - 20, 15, 15);
        g2d.fillOval(x + 10, y - 20, 15, 15);
        
        // Boca triste
        g2d.setStroke(new BasicStroke(4));
        g2d.drawArc(x - 25, y + 10, 50, 30, 0, 180);
        
        // Lágrimas
        g2d.fillOval(x - 30, y - 5, 6, 15);
        g2d.fillOval(x + 25, y - 5, 6, 15);
        
        // Voltar à cor branca
        g2d.setColor(new Color(255, 255, 255, 150));
        g2d.setStroke(new BasicStroke(3));
        
        // Elementos dramáticos
        desenharCruz(g2d, x - 60, y - 50);
        desenharCruz(g2d, x + 60, y - 30);
        desenharFlor(g2d, x - 50, y + 60);
    }
    
    private void desenharIconeTeatro(Graphics2D g2d, int x, int y) {
        // Máscara de teatro genérica
        g2d.fillOval(x - 35, y - 35, 70, 70);
        
        // Olhos
        g2d.setColor(Color.BLACK);
        g2d.fillOval(x - 20, y - 15, 10, 10);
        g2d.fillOval(x + 10, y - 15, 10, 10);
        
        // Boca neutra
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(x - 15, y + 10, x + 15, y + 10);
        
        // Voltar à cor branca
        g2d.setColor(new Color(255, 255, 255, 150));
        
        // Cortinas de teatro
        desenharCortina(g2d, x - 80, y - 60, true);
        desenharCortina(g2d, x + 80, y - 60, false);
    }
    
    private void desenharEstrela(Graphics2D g2d, int x, int y, int tamanho) {
        int[] xPoints = new int[10];
        int[] yPoints = new int[10];
        
        for (int i = 0; i < 10; i++) {
            double angle = Math.PI * i / 5;
            int radius = (i % 2 == 0) ? tamanho : tamanho / 2;
            xPoints[i] = x + (int)(radius * Math.cos(angle - Math.PI / 2));
            yPoints[i] = y + (int)(radius * Math.sin(angle - Math.PI / 2));
        }
        
        g2d.fillPolygon(xPoints, yPoints, 10);
    }
    
    private void desenharRisada(Graphics2D g2d, int x, int y) {
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("HA", x, y);
    }
    
    private void desenharCruz(Graphics2D g2d, int x, int y) {
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(x - 8, y - 8, x + 8, y + 8);
        g2d.drawLine(x - 8, y + 8, x + 8, y - 8);
        g2d.setStroke(new BasicStroke(3));
    }
    
    private void desenharFlor(Graphics2D g2d, int x, int y) {
        // Pétalas
        for (int i = 0; i < 6; i++) {
            double angle = Math.PI * i / 3;
            int petalX = x + (int)(12 * Math.cos(angle));
            int petalY = y + (int)(12 * Math.sin(angle));
            g2d.fillOval(petalX - 4, petalY - 4, 8, 8);
        }
        // Centro
        g2d.fillOval(x - 3, y - 3, 6, 6);
    }
    
    private void desenharCortina(Graphics2D g2d, int x, int y, boolean esquerda) {
        int largura = 25;
        int altura = 80;
        
        if (esquerda) {
            // Cortina da esquerda
            g2d.fillRect(x, y, largura, altura);
            // Dobras
            for (int i = 0; i < 4; i++) {
                g2d.drawLine(x + 5 + i * 5, y, x + 5 + i * 5, y + altura);
            }
        } else {
            // Cortina da direita
            g2d.fillRect(x - largura, y, largura, altura);
            // Dobras
            for (int i = 0; i < 4; i++) {
                g2d.drawLine(x - 5 - i * 5, y, x - 5 - i * 5, y + altura);
            }
        }
    }
}