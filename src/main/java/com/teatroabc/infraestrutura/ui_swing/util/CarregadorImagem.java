package com.teatroabc.infraestrutura.ui_swing.util;

/**
 * Conclusão:
 * A classe CarregadorImagem é um utilitário complexo e útil, focado em uma tarefa de infraestrutura específica
 * (carregar imagens para a UI Swing). Ela está bem estruturada para essa tarefa e sua lógica de múltiplas tentativas de
 * carregamento e debug é robusta. Do ponto de vista da arquitetura hexagonal, ela está corretamente posicionada como um
 * componente auxiliar da camada de apresentação/adaptadores, e não como parte do núcleo de domínio.
 * Seu uso pelo CardPeca está alinhado com esse princípio.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class CarregadorImagem {
    
    private static final boolean DEBUG = true; // Ativar/desativar logs detalhados
    
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
            debug("Caminho da imagem está vazio ou nulo");
            return null;
        }
        
        debug("=== INICIANDO CARREGAMENTO DE IMAGEM ===");
        debug("Caminho solicitado: " + caminho);
        
        try {
            // 1. Tentar carregar do classpath (resources)
            BufferedImage imagem = tentarCarregarDoClasspath(caminho);
            if (imagem != null) {
                debug("✓ Imagem carregada com sucesso do classpath!");
                return imagem;
            }
            
            // 2. Tentar carregar do sistema de arquivos
            imagem = tentarCarregarDoSistemaArquivos(caminho);
            if (imagem != null) {
                debug("✓ Imagem carregada com sucesso do sistema de arquivos!");
                return imagem;
            }
            
            // 3. Tentar caminhos alternativos comuns
            imagem = tentarCaminhosAlternativos(caminho);
            if (imagem != null) {
                debug("✓ Imagem carregada com sucesso de caminho alternativo!");
                return imagem;
            }
            
            // 4. Se nada funcionou, mostrar estrutura de diretórios
            debug("❌ Imagem não encontrada em nenhum local");
            listarEstruturaDiretorios();
            
        } catch (Exception e) {
            debug("❌ Erro durante carregamento: " + e.getMessage());
            e.printStackTrace();
        }
        
        debug("Retornando null - placeholder será usado");
        debug("=== FIM CARREGAMENTO ===\n");
        return null; // Retorna null para usar placeholder visual
    }
    
    private static BufferedImage tentarCarregarDoClasspath(String caminho) {
        debug("\n--- TENTATIVA 1: Classpath ---");
        
        List<String> variantes = Arrays.asList(
            caminho,                          // Original
            "/" + caminho,                    // Com barra inicial
            caminho.startsWith("/") ? caminho.substring(1) : caminho, // Sem barra inicial
            "src/main/resources/" + caminho,  // Caminho completo development
            "resources/" + caminho            // Caminho resources simples
        );
        
        for (String variante : variantes) {
            try {
                debug("Testando classpath: " + variante);
                URL resource = CarregadorImagem.class.getResource(variante);
                if (resource != null) {
                    debug("✓ Recurso encontrado: " + resource);
                    BufferedImage imagem = ImageIO.read(resource);
                    if (imagem != null) {
                        debug("✓ Imagem lida com sucesso!");
                        return imagem;
                    }
                } else {
                    debug("✗ Recurso não encontrado");
                }
            } catch (IOException e) {
                debug("✗ Erro ao ler: " + e.getMessage());
            }
        }
        
        return null;
    }
    
    private static BufferedImage tentarCarregarDoSistemaArquivos(String caminho) {
        debug("\n--- TENTATIVA 2: Sistema de Arquivos ---");
        
        List<String> caminhos = Arrays.asList(
            caminho,                                    // Original
            caminho.startsWith("/") ? caminho.substring(1) : caminho, // Sem barra inicial
            System.getProperty("user.dir") + "/" + caminho,          // Diretório atual
            System.getProperty("user.dir") + "/src/main/resources/" + caminho,
            System.getProperty("user.dir") + "/resources/" + caminho
        );
        
        for (String caminhoTeste : caminhos) {
            try {
                File arquivo = new File(caminhoTeste);
                debug("Testando arquivo: " + arquivo.getAbsolutePath());
                
                if (arquivo.exists() && arquivo.isFile()) {
                    debug("✓ Arquivo encontrado: " + arquivo.getAbsolutePath());
                    BufferedImage imagem = ImageIO.read(arquivo);
                    if (imagem != null) {
                        debug("✓ Imagem lida com sucesso!");
                        return imagem;
                    }
                } else {
                    debug("✗ Arquivo não existe ou não é arquivo: " + arquivo.exists() + " / " + arquivo.isFile());
                }
            } catch (IOException e) {
                debug("✗ Erro ao ler arquivo: " + e.getMessage());
            }
        }
        
        return null;
    }
    
    private static BufferedImage tentarCaminhosAlternativos(String caminho) {
        debug("\n--- TENTATIVA 3: Caminhos Alternativos ---");
        
        // Extrair nome do arquivo
        String nomeArquivo = caminho;
        if (caminho.contains("/")) {
            nomeArquivo = caminho.substring(caminho.lastIndexOf("/") + 1);
        }
        if (caminho.contains("\\")) {
            nomeArquivo = caminho.substring(caminho.lastIndexOf("\\") + 1);
        }
        
        debug("Nome do arquivo extraído: " + nomeArquivo);
        
        // Diretórios comuns onde imagens podem estar
        List<String> diretoriosComuns = Arrays.asList(
            "imagens/",
            "img/", 
            "images/",
            "assets/",
            "resources/imagens/",
            "src/main/resources/imagens/",
            "src/main/resources/img/",
            "src/main/resources/images/",
            ""  // Diretório raiz
        );
        
        for (String diretorio : diretoriosComuns) {
            String caminhoCompleto = diretorio + nomeArquivo;
            debug("Testando caminho alternativo: " + caminhoCompleto);
            
            // Tentar no classpath
            try {
                URL resource = CarregadorImagem.class.getResource("/" + caminhoCompleto);
                if (resource != null) {
                    debug("✓ Encontrado no classpath: " + resource);
                    BufferedImage imagem = ImageIO.read(resource);
                    if (imagem != null) return imagem;
                }
            } catch (IOException e) {
                debug("✗ Erro classpath: " + e.getMessage());
            }
            
            // Tentar no sistema de arquivos
            try {
                File arquivo = new File(caminhoCompleto);
                if (arquivo.exists()) {
                    debug("✓ Encontrado no sistema: " + arquivo.getAbsolutePath());
                    BufferedImage imagem = ImageIO.read(arquivo);
                    if (imagem != null) return imagem;
                }
            } catch (IOException e) {
                debug("✗ Erro sistema: " + e.getMessage());
            }
        }
        
        return null;
    }
    
    private static void listarEstruturaDiretorios() {
        debug("\n--- ESTRUTURA DE DIRETÓRIOS ---");
        
        try {
            // Diretório atual
            File diretorioAtual = new File(".");
            debug("Diretório atual: " + diretorioAtual.getAbsolutePath());
            listarConteudo(diretorioAtual, 0, 2);
            
            // Verificar diretórios comuns
            String[] diretoriosVerificar = {
                "src/main/resources",
                "resources", 
                "imagens",
                "img",
                "images",
                "assets"
            };
            
            for (String dir : diretoriosVerificar) {
                File diretorio = new File(dir);
                if (diretorio.exists() && diretorio.isDirectory()) {
                    debug("\n📁 Conteúdo de " + dir + ":");
                    listarConteudo(diretorio, 0, 3);
                }
            }
            
            // Listar recursos do classpath se possível
            debug("\n--- RECURSOS DO CLASSPATH ---");
            tentarListarRecursosClasspath();
            
        } catch (Exception e) {
            debug("Erro ao listar diretórios: " + e.getMessage());
        }
        debug("--- FIM ESTRUTURA ---\n");
    }
    
    private static void listarConteudo(File diretorio, int nivel, int maxNivel) {
        if (nivel > maxNivel || !diretorio.isDirectory()) {
            return;
        }
        
        File[] arquivos = diretorio.listFiles();
        if (arquivos != null) {
            for (File arquivo : arquivos) {
                String indent = "  ".repeat(nivel);
                if (arquivo.isDirectory()) {
                    debug(indent + "📁 " + arquivo.getName() + "/");
                    listarConteudo(arquivo, nivel + 1, maxNivel);
                } else {
                    String extensao = "";
                    String nome = arquivo.getName();
                    if (nome.contains(".")) {
                        extensao = nome.substring(nome.lastIndexOf(".")).toLowerCase();
                    }
                    
                    String icone = "📄";
                    if (".png,.jpg,.jpeg,.gif,.bmp".contains(extensao)) {
                        icone = "🖼️";
                    }
                    
                    debug(indent + icone + " " + arquivo.getName() + " (" + arquivo.length() + " bytes)");
                }
            }
        }
    }
    
    private static void tentarListarRecursosClasspath() {
        try {
            // Tentar listar alguns recursos conhecidos
            String[] testePaths = {"/", "/imagens", "/img", "/images", "/resources"};
            
            for (String path : testePaths) {
                URL resource = CarregadorImagem.class.getResource(path);
                if (resource != null) {
                    debug("✓ Recurso classpath encontrado: " + path + " -> " + resource);
                } else {
                    debug("✗ Recurso classpath não encontrado: " + path);
                }
            }
        } catch (Exception e) {
            debug("Erro ao listar recursos do classpath: " + e.getMessage());
        }
    }
    
    private static void debug(String mensagem) {
        if (DEBUG) {
            System.out.println("CarregadorImagem: " + mensagem);
        }
    }
    
    // Método estático para criar placeholder (mantido para compatibilidade)
    public static BufferedImage criarImagemPlaceholder() {
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
        g2d.setFont(new Font("Arial", Font.BOLD, 60));
        FontMetrics fm = g2d.getFontMetrics();
        String texto = "🎭";
        int x = (350 - fm.stringWidth(texto)) / 2;
        int y = (350 + fm.getAscent()) / 2;
        g2d.drawString(texto, x, y);
        
        // Texto "SEM IMAGEM"
        g2d.setColor(new Color(120, 120, 120));
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        fm = g2d.getFontMetrics();
        texto = "IMAGEM NÃO ENCONTRADA";
        x = (350 - fm.stringWidth(texto)) / 2;
        y = y + 50;
        g2d.drawString(texto, x, y);
        
        g2d.dispose();
        return placeholder;
    }
    
    // Método para desativar debug em produção
    public static void setDebug(boolean ativo) {
        // Não é possível modificar final, mas pode ser usado para controle futuro
        debug("Debug mode: " + (DEBUG ? "ATIVO" : "INATIVO"));
    }
    
    // Método para testar carregamento de uma imagem específica
    public static void testarCarregamento(String caminho) {
        debug("\n=== TESTE DE CARREGAMENTO ===");
        debug("Testando: " + caminho);
        
        BufferedImage resultado = carregar(caminho);
        
        if (resultado != null) {
            debug("✅ SUCESSO! Imagem carregada: " + resultado.getWidth() + "x" + resultado.getHeight());
        } else {
            debug("❌ FALHOU! Imagem não foi carregada");
        }
        debug("=== FIM TESTE ===\n");
    }
    }