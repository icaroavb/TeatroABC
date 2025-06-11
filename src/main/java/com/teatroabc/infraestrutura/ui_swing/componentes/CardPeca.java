package com.teatroabc.infraestrutura.ui_swing.componentes;

import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.infraestrutura.ui_swing.util.CarregadorImagem;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

/**
 * Componente visual customizado para exibir as informações de uma peça em cartaz
 * de forma atraente e interativa, como um "card".
 * 
 * Na Arquitetura Hexagonal, esta classe é um Adaptador de UI. Sua principal
 * responsabilidade é receber um objeto de domínio (Peca) e traduzir seus dados
 * (título, imagem, cor) em uma representação gráfica. Ela não contém lógica de
 * negócio, apenas lógica de apresentação.
 */
public class CardPeca extends JPanel {
    private final Peca peca; // A entidade de domínio que este card representa.
    private boolean modoSelecao = false; // Flag para indicar se o card deve mostrar feedback de seleção.
    private boolean selecionado = false; // Flag para indicar se o card está atualmente selecionado.
    private final BufferedImage imagem; // Imagem da peça, carregada no construtor.
    private ActionListener actionListener; // Listener para notificar quando o card é clicado.
    private final Color corFundoCalculada; // Cor de fundo convertida de String Hex para java.awt.Color.

    /**
     * Construtor do CardPeca.
     * @param peca O objeto de domínio Peca a ser exibido. Não pode ser nulo.
     */
    public CardPeca(Peca peca) {
        if (peca == null) {
            throw new IllegalArgumentException("O objeto Peca não pode ser nulo.");
        }
        this.peca = peca;
        
        // A camada de UI é responsável por lidar com recursos de UI, como imagens.
        this.imagem = CarregadorImagem.carregar(peca.getCaminhoImagem());
        // A camada de UI também traduz dados do domínio (String Hex) para tipos de UI (java.awt.Color).
        this.corFundoCalculada = converterHexParaColor(peca.getCorFundoHex());

        setPreferredSize(new Dimension(350, 450));
        setOpaque(false); // O fundo será desenhado manualmente no método paintComponent.
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Adiciona um listener de mouse para capturar cliques no card.
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Notifica o listener externo apenas se estiver em modo de seleção.
                if (modoSelecao && actionListener != null) {
                    actionListener.actionPerformed(new ActionEvent(CardPeca.this, ActionEvent.ACTION_PERFORMED, "cardClicked"));
                }
            }
        });
    }

    /**
     * Método auxiliar para converter uma String de cor em formato hexadecimal (ex: "#RRGGBB")
     * para um objeto {@link java.awt.Color}.
     * @param hexColor A string da cor.
     * @return O objeto Color correspondente, ou uma cor padrão em caso de erro.
     */
    private Color converterHexParaColor(String hexColor) {
        if (hexColor == null || !hexColor.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
            System.err.println("CardPeca: Formato de cor hexadecimal inválido '" + hexColor + "'. Usando Cinza como padrão.");
            return Color.DARK_GRAY;
        }
        try {
            return Color.decode(hexColor);
        } catch (NumberFormatException e) {
            System.err.println("CardPeca: Erro ao decodificar cor hexadecimal '" + hexColor + "'. Erro: " + e.getMessage());
            return Color.DARK_GRAY;
        }
    }
    
    public Peca getPeca() {
        return peca;
    }

    public void setSelecao(boolean modoSelecao) {
        this.modoSelecao = modoSelecao;
    }
    
    public boolean isSelecionado() {
        return selecionado;
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
        repaint(); // Repinta o componente para exibir/remover a borda de seleção.
    }
    
    public void addActionListener(ActionListener listener) {
        this.actionListener = listener;
    }
    
    /**
     * Sobrescreve o método de pintura para desenhar a aparência customizada do card.
     * @param g O contexto gráfico.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int areaImagemAltura = getHeight() - 100;
        
        // Desenha a imagem da peça ou um placeholder visual se a imagem não for encontrada.
        if (imagem != null) {
            g2d.drawImage(imagem, 0, 0, getWidth(), areaImagemAltura, this);
        } else {
            desenharPlaceholderVisual(g2d, areaImagemAltura);
        }
        
        // Desenha a área colorida inferior com o título.
        g2d.setColor(this.corFundoCalculada);
        g2d.fillRect(0, areaImagemAltura, getWidth(), 100);
        
        // Lógica para desenhar o título e subtítulo da peça.
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        
        int x = (getWidth() - fm.stringWidth(peca.getTitulo())) / 2;
        g2d.drawString(peca.getTitulo(), x, areaImagemAltura + 40);
        
        if (peca.getSubtitulo() != null && !peca.getSubtitulo().isEmpty()) {
            g2d.setFont(new Font("Arial", Font.PLAIN, 16));
            fm = g2d.getFontMetrics();
            x = (getWidth() - fm.stringWidth(peca.getSubtitulo())) / 2;
            g2d.drawString(peca.getSubtitulo(), x, areaImagemAltura + 65);
        }
        
        // Desenha uma borda de destaque se o card estiver no modo de seleção e for o selecionado.
        if (modoSelecao && selecionado) {
            g2d.setColor(Constantes.AMARELO);
            g2d.setStroke(new BasicStroke(4));
            g2d.drawRect(2, 2, getWidth() - 4, getHeight() - 4);
        }
        
        g2d.dispose();
    }
    
    /**
     * Desenha um fundo visual genérico para o caso de a imagem da peça não ser encontrada.
     * @param g2d O contexto gráfico 2D.
     * @param altura A altura da área a ser preenchida.
     */
    private void desenharPlaceholderVisual(Graphics2D g2d, int altura) {
        Color corBase = this.corFundoCalculada;
        Color corClara = corBase.brighter();
        Color corEscura = corBase.darker();
        
        GradientPaint gradient = new GradientPaint(0, 0, corClara, getWidth(), altura, corEscura);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), altura);
        
        // Adiciona um ícone genérico para indicar que é uma peça de teatro.
        g2d.setColor(new Color(255, 255, 255, 50)); // Cor branca semi-transparente
        g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 150));
        FontMetrics fm = g2d.getFontMetrics();
        String icone = "🎭";
        int x = (getWidth() - fm.stringWidth(icone)) / 2;
        int y = (altura - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(icone, x, y);
    }
}