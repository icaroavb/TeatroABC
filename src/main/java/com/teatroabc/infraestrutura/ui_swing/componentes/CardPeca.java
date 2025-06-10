package com.teatroabc.infraestrutura.ui_swing.componentes;

import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes; // Modelo Peca do domínio
import com.teatroabc.infraestrutura.ui_swing.util.CarregadorImagem; // Utilitário de UI
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class CardPeca extends JPanel {
    private Peca peca;
    private boolean modoSelecao = false;
    private boolean selecionado = false;
    private BufferedImage imagem;
    private ActionListener actionListener;
    private Color corFundoCalculada; // Para armazenar a cor convertida

    public CardPeca(Peca peca) {
        this.peca = peca;
        
        this.imagem = CarregadorImagem.carregar(peca.getCaminhoImagem());
        this.corFundoCalculada = converterHexParaColor(peca.getCorFundoHex()); // CONVERTE A COR AQUI

        setPreferredSize(new Dimension(350, 450));
        // setBackground(this.corFundoCalculada); // Define o fundo do JPanel em si (opcional, pois o paintComponent vai pintar)
        setOpaque(false); // Se paintComponent desenha o fundo, o JPanel pode ser não opaco
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (modoSelecao && actionListener != null) {
                    // Ao clicar, o CardPeca não precisa de serviços, ele apenas notifica o listener.
                    // A tela que contém o CardPeca (ex: TelaSelecionarPeca) é que lidará com a lógica de negócio.
                    actionListener.actionPerformed(new ActionEvent(CardPeca.this, ActionEvent.ACTION_PERFORMED, "cardClicked"));
                }
            }
        });
    }

    // Método auxiliar para converter String Hexadecimal para java.awt.Color
    private Color converterHexParaColor(String hexColor) {
        if (hexColor == null || !hexColor.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
            System.err.println("CardPeca: Formato de cor hexadecimal inválido '" + hexColor + "'. Usando Cinza como padrão.");
            return Color.DARK_GRAY; // Cor padrão em caso de erro de formato
        }
        try {
            return Color.decode(hexColor);
        } catch (NumberFormatException e) {
            System.err.println("CardPeca: Erro ao decodificar cor hexadecimal '" + hexColor + "'. Usando Cinza como padrão. Erro: " + e.getMessage());
            return Color.DARK_GRAY;
        }
    }
    
    public Peca getPeca() { // Getter para Peca, útil para quem usa o card
        return peca;
    }

    public void setSelecao(boolean modoSelecao) {
        this.modoSelecao = modoSelecao;
    }
    
    public boolean isSelecionado() { // Getter para o estado de seleção
        return selecionado;
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
        super.paintComponent(g); // Importante para limpar o fundo se o JPanel não for opaco
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int areaImagemAltura = getHeight() - 100;
        
        // Área da imagem ou placeholder visual
        if (imagem != null) {
            g2d.drawImage(imagem, 0, 0, getWidth(), areaImagemAltura, this);
        } else {
            desenharPlaceholderVisual(g2d, areaImagemAltura); // Usa corFundoCalculada
        }
        
        // Área colorida inferior com título
        g2d.setColor(this.corFundoCalculada); // USA A COR CONVERTIDA
        g2d.fillRect(0, areaImagemAltura, getWidth(), 100);
        
        // Título da peça
        g2d.setColor(Color.WHITE); // Cor do texto do título
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        
        // Lógica para quebrar o título se necessário (como antes)
        // ... (sua lógica de quebra de linha para "MORTE E VIDA SEVERINA" ou títulos longos)
        if (peca.getTitulo().contains("MORTE")) { // Mantendo a lógica original como exemplo
            String[] linhas = {"MORTE E VIDA", "SEVERINA"};
            int y = areaImagemAltura + 35;
            for (String linha : linhas) {
                int x = (getWidth() - fm.stringWidth(linha)) / 2;
                g2d.drawString(linha, x, y);
                y += 25;
            }
        } else {
            int x = (getWidth() - fm.stringWidth(peca.getTitulo())) / 2;
            g2d.drawString(peca.getTitulo(), x, areaImagemAltura + 40);
            
            if (peca.getSubtitulo() != null && !peca.getSubtitulo().isEmpty()) {
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
        // Fundo gradiente baseado na cor da peça (corFundoCalculada)
        Color corBase = this.corFundoCalculada;
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
        
        GradientPaint gradient = new GradientPaint(0, 0, corClara, getWidth(), altura, corEscura);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), altura);
        
        desenharIconePeca(g2d, altura); // Reusa a lógica de ícone
    }
    
    // ... (seus métodos desenharIconePeca, desenharIconeMagico, etc., permanecem)
    // Se eles usavam peca.getCorFundo() diretamente, agora devem usar this.corFundoCalculada se relevante
    private void desenharIconePeca(Graphics2D g2d, int altura) { /* ... sua lógica de ícone ... */ }
    private void desenharIconeMagico(Graphics2D g2d, int x, int y) { /* ... */ }
    private void desenharIconeComedia(Graphics2D g2d, int x, int y) { /* ... */ }
    private void desenharIconeDramatico(Graphics2D g2d, int x, int y) { /* ... */ }
    private void desenharIconeTeatro(Graphics2D g2d, int x, int y) { /* ... */ }
    private void desenharEstrela(Graphics2D g2d, int x, int y, int tamanho) { /* ... */ }
    private void desenharRisada(Graphics2D g2d, int x, int y) { /* ... */ }
    private void desenharCruz(Graphics2D g2d, int x, int y) { /* ... */ }
    private void desenharFlor(Graphics2D g2d, int x, int y) { /* ... */ }
    private void desenharCortina(Graphics2D g2d, int x, int y, boolean esquerda) { /* ... */ }

}