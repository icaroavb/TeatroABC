package com.teatroabc.utilitarios;

import javax.swing.*;
import java.awt.*;

public class CarregadorImagem {
    public static ImageIcon carregar(String caminho, int largura, int altura) {
        java.net.URL imgURL = Thread.currentThread().getContextClassLoader().getResource(caminho.startsWith("/") ? caminho.substring(1) : caminho);
        System.out.println("Loading image from URL: " + imgURL);
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            Image img = icon.getImage().getScaledInstance(largura, altura, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } else {
            System.err.println("Couldn't find file: " + caminho);
            // Try loading from file system as fallback
            java.io.File file = new java.io.File(caminho);
            if (file.exists()) {
                System.out.println("Loading image from file system: " + file.getAbsolutePath());
                ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                Image img = icon.getImage().getScaledInstance(largura, altura, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            } else {
                System.err.println("File not found in file system: " + file.getAbsolutePath());
                return null;
            }
        }
    }
}
