package com.teatroabc.utilitarios;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.io.File;

public class CarregadorImagem {
    
    public static ImageIcon carregar(String caminho, int largura, int altura) {
        BufferedImage img = carregar(caminho);
        if (img != null) {
            Image scaledImg = img.getScaledInstance(largura, altura, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);
        }
        return null;
    }
    
    public static BufferedImage carregar(String caminho) {
        if (caminho == null || caminho.isEmpty()) {
            System.out.println("Caminho da imagem está vazio ou nulo");
            return null;
        }
        
        try {
            // Primeiro, tenta carregar do classpath
            URL resource = CarregadorImagem.class.getResource(caminho);
            if (resource == null && !caminho.startsWith("/")) {
                resource = CarregadorImagem.class.getResource("/" + caminho);
            }
            
            if (resource != null) {
                System.out.println("Carregando imagem do classpath: " + caminho);
                return ImageIO.read(resource);
            }
            
            // Se não encontrou no classpath, tenta carregar do sistema de arquivos
            File arquivo = new File(caminho);
            if (arquivo.exists()) {
                System.out.println("Carregando imagem do arquivo: " + caminho);
                return ImageIO.read(arquivo);
            }
            
            // Tenta sem a barra inicial
            if (caminho.startsWith("/")) {
                arquivo = new File(caminho.substring(1));
                if (arquivo.exists()) {
                    System.out.println("Carregando imagem do arquivo (sem barra): " + caminho.substring(1));
                    return ImageIO.read(arquivo);
                }
            }
            
            System.out.println("Imagem não encontrada: " + caminho);
            
        } catch (IOException e) {
            System.err.println("Erro ao carregar imagem: " + caminho);
            e.printStackTrace();
        }
        
        // Retorna uma imagem placeholder se não conseguir carregar
        return criarImagemPlaceholder();
    }
    
    private static BufferedImage criarImagemPlaceholder() {
        BufferedImage placeholder = new BufferedImage(350, 350, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = placeholder.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fundo gradiente
        GradientPaint gradient = new GradientPaint(0, 0, new Color(70, 70, 70), 
                                                  350, 350, new Color(50, 50, 50));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, 350, 350);
        
        // Ícone de teatro
        g2d.setColor(new Color(150, 150, 150));
        g2d.setFont(new Font("Arial", Font.BOLD, 80));
        FontMetrics fm = g2d.getFontMetrics();
        String texto = "🎭";
        int x = (350 - fm.stringWidth(texto)) / 2;
        int y = (350 + fm.getAscent()) / 2;
        g2d.drawString(texto, x, y);
        
        // Texto "IMAGEM"
        g2d.setColor(new Color(120, 120, 120));
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        fm = g2d.getFontMetrics();
        texto = "IMAGEM";
        x = (350 - fm.stringWidth(texto)) / 2;
        y = y + 60;
        g2d.drawString(texto, x, y);
        
        g2d.dispose();
        return placeholder;
    }
}