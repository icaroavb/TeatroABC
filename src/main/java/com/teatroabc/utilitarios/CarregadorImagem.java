package com.teatroabc.utilitarios;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;

public class CarregadorImagem {
    
    public static ImageIcon carregar(String caminho, int largura, int altura) {
        try {
            // Tentar carregar do classpath primeiro
            URL resource = CarregadorImagem.class.getResource(caminho);
            if (resource == null && !caminho.startsWith("/")) {
                resource = CarregadorImagem.class.getResource("/" + caminho);
            }
            
            if (resource != null) {
                BufferedImage img = ImageIO.read(resource);
                Image scaledImg = img.getScaledInstance(largura, altura, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImg);
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar imagem: " + caminho);
            e.printStackTrace();
        }
        
        // Retornar null se não conseguir carregar
        return null;
    }
    
    public static BufferedImage carregar(String caminho) {
        try {
            URL resource = CarregadorImagem.class.getResource(caminho);
            if (resource == null && !caminho.startsWith("/")) {
                resource = CarregadorImagem.class.getResource("/" + caminho);
            }
            
            if (resource != null) {
                return ImageIO.read(resource);
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar imagem: " + caminho);
            e.printStackTrace();
        }
        
        return null;
    }
}