package com.teatroabc.telas;

import com.teatroabc.componentes.*;
import com.teatroabc.constantes.Constantes;
import com.teatroabc.modelos.Bilhete;
import com.teatroabc.utilitarios.FormatadorData;
import com.teatroabc.utilitarios.FormatadorMoeda;
import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;

public class DialogoDetalhesBilhete extends JDialog {
    
    public DialogoDetalhesBilhete(Frame parent, Bilhete bilhete) {
        super(parent, "Detalhes do Bilhete", true);
        configurarDialogo(bilhete);
    }
    
    // Construtor sem parent para compatibilidade
    public DialogoDetalhesBilhete(Bilhete bilhete) {
        super();
        setTitle("Detalhes do Bilhete");
        setModal(true);
        configurarDialogo(bilhete);
    }
    
    private void configurarDialogo(Bilhete bilhete) {
        setSize(600, 750); // Aumentado para acomodar desconto
        setLocationRelativeTo(getParent());
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BorderLayout());
        painelPrincipal.setBackground(Constantes.AZUL_ESCURO);
        
        // Cabeçalho com logo
        JPanel cabecalho = new JPanel(new FlowLayout(FlowLayout.CENTER));
        cabecalho.setBackground(Constantes.AZUL_ESCURO);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        cabecalho.add(new LogoTeatro());
        
        // Conteúdo do bilhete
        JPanel conteudo = criarConteudoBilhete(bilhete);
        
        // Botão fechar
        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotao.setBackground(Constantes.AZUL_ESCURO);
        painelBotao.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        BotaoAnimado btnFechar = new BotaoAnimado("FECHAR",
            Constantes.AZUL_CLARO, new Color(70, 130, 180), new Dimension(150, 50));
        btnFechar.setFont(new Font("Arial", Font.BOLD, 18));
        btnFechar.addActionListener(e -> dispose());
        painelBotao.add(btnFechar);
        
        painelPrincipal.add(cabecalho, BorderLayout.NORTH);
        painelPrincipal.add(conteudo, BorderLayout.CENTER);
        painelPrincipal.add(painelBotao, BorderLayout.SOUTH);
        
        setContentPane(painelPrincipal);
    }
    
    private JPanel criarConteudoBilhete(Bilhete bilhete) {
        JPanel painelContainer = new JPanel(new BorderLayout());
        painelContainer.setBackground(Constantes.AZUL_ESCURO);
        painelContainer.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));
        
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Badge ABC GOLD se aplicável
        if (bilhete.getCliente().isMembroABC()) {
            JPanel badgeABC = criarBadgeABC();
            badgeABC.setAlignmentX(Component.CENTER_ALIGNMENT);
            painel.add(badgeABC);
            painel.add(Box.createVerticalStrut(20));
        }
        
        // Título da peça
        JLabel lblTitulo = new JLabel(bilhete.getPeca().getTitulo());
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setForeground(Color.BLACK);
        painel.add(lblTitulo);
        
        painel.add(Box.createVerticalStrut(30));
        
        // Informações principais
        adicionarInfo(painel, "Data:", FormatadorData.formatar(bilhete.getPeca().getDataHora()));
        adicionarInfo(painel, "Cliente:", bilhete.getCliente().getNome());
        adicionarInfo(painel, "CPF:", formatarCPF(bilhete.getCliente().getCpf()));
        
        // Assentos
        String assentosStr = bilhete.getAssentos().stream()
            .map(a -> a.getCodigo())
            .collect(Collectors.joining(", "));
        adicionarInfo(painel, "Assentos:", assentosStr);
        
        // Se houve desconto, mostrar detalhes
        if (bilhete.getValorDesconto() > 0) {
            adicionarInfo(painel, "Subtotal:", FormatadorMoeda.formatar(bilhete.getSubtotal()));
            
            // Desconto em amarelo
            JPanel linhaPainel = new JPanel(new BorderLayout());
            linhaPainel.setBackground(Color.WHITE);
            linhaPainel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
            
            JLabel lblRotulo = new JLabel("Desconto ABC GOLD:");
            lblRotulo.setFont(new Font("Arial", Font.BOLD, 18));
            lblRotulo.setForeground(new Color(255, 140, 0)); // Laranja dourado
            lblRotulo.setPreferredSize(new Dimension(180, 25));
            
            JLabel lblValor = new JLabel("- " + FormatadorMoeda.formatar(bilhete.getValorDesconto()));
            lblValor.setFont(new Font("Arial", Font.BOLD, 18));
            lblValor.setForeground(new Color(255, 140, 0));
            
            linhaPainel.add(lblRotulo, BorderLayout.WEST);
            linhaPainel.add(lblValor, BorderLayout.CENTER);
            painel.add(linhaPainel);
        }
        
        // Valor total (destacado)
        JPanel linhaTotalPainel = new JPanel(new BorderLayout());
        linhaTotalPainel.setBackground(Color.WHITE);
        linhaTotalPainel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 0, 8, 0)
        ));
        
        JLabel lblTotalRotulo = new JLabel("VALOR TOTAL:");
        lblTotalRotulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTotalRotulo.setForeground(Color.BLACK);
        lblTotalRotulo.setPreferredSize(new Dimension(180, 30));
        
        JLabel lblTotalValor = new JLabel(FormatadorMoeda.formatar(bilhete.getValorTotal()));
        lblTotalValor.setFont(new Font("Arial", Font.BOLD, 22));
        lblTotalValor.setForeground(bilhete.getCliente().isMembroABC() ? 
            new Color(255, 140, 0) : Color.BLACK);
        
        linhaTotalPainel.add(lblTotalRotulo, BorderLayout.WEST);
        linhaTotalPainel.add(lblTotalValor, BorderLayout.CENTER);
        painel.add(linhaTotalPainel);
        
        painel.add(Box.createVerticalStrut(30));
        
        // Código de barras
        JLabel lblCodigoBarras = new JLabel("CÓDIGO DE BARRAS");
        lblCodigoBarras.setFont(new Font("Arial", Font.BOLD, 14));
        lblCodigoBarras.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblCodigoBarras.setForeground(Color.BLACK);
        painel.add(lblCodigoBarras);
        
        painel.add(Box.createVerticalStrut(10));
        
        // Painel do código de barras visual
        JPanel painelCodigoBarras = criarPainelCodigoBarras(bilhete.getCodigoBarras());
        painelCodigoBarras.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(painelCodigoBarras);
        
        painel.add(Box.createVerticalStrut(10));
        
        // Código numérico
        JLabel lblCodigoNumerico = new JLabel(bilhete.getCodigoBarras());
        lblCodigoNumerico.setFont(new Font("Arial", Font.BOLD, 16));
        lblCodigoNumerico.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblCodigoNumerico.setForeground(Color.BLACK);
        painel.add(lblCodigoNumerico);
        
        painelContainer.add(painel, BorderLayout.CENTER);
        return painelContainer;
    }
    
    private JPanel criarBadgeABC() {
        JPanel badge = new JPanel();
        badge.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        badge.setBackground(Constantes.AMARELO);
        badge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        badge.setMaximumSize(new Dimension(300, 40));

        // Texto
        JLabel lblTexto = new JLabel("MEMBRO ABC GOLD");
        lblTexto.setFont(new Font("Arial", Font.BOLD, 14));
        lblTexto.setForeground(Color.BLACK);

       

     
        badge.add(lblTexto);
       

        return badge;
    }
    
    private void adicionarInfo(JPanel painel, String rotulo, String valor) {
        JPanel linhaPainel = new JPanel(new BorderLayout());
        linhaPainel.setBackground(Color.WHITE);
        linhaPainel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        
        JLabel lblRotulo = new JLabel(rotulo);
        lblRotulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblRotulo.setForeground(Color.DARK_GRAY);
        lblRotulo.setPreferredSize(new Dimension(120, 25));
        
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Arial", Font.PLAIN, 18));
        lblValor.setForeground(Color.BLACK);
        
        linhaPainel.add(lblRotulo, BorderLayout.WEST);
        linhaPainel.add(lblValor, BorderLayout.CENTER);
        
        painel.add(linhaPainel);
    }
    
    private String formatarCPF(String cpf) {
        if (cpf.length() == 11) {
            return cpf.substring(0, 3) + "." + 
                   cpf.substring(3, 6) + "." + 
                   cpf.substring(6, 9) + "-" + 
                   cpf.substring(9, 11);
        }
        return cpf;
    }
    
    private JPanel criarPainelCodigoBarras(String codigo) {
        JPanel painelBarras = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Desenhar código de barras simples
                int x = 10;
                int larguraBarra = 3;
                int espacamento = 2;
                
                g2d.setColor(Color.BLACK);
                
                // Padrão simples baseado no código
                for (int i = 0; i < codigo.length() && x < getWidth() - 20; i++) {
                    char c = codigo.charAt(i);
                    int altura = 40 + (c % 20); // Altura variável baseada no caractere
                    
                    // Alternar entre barras grossas e finas
                    int largura = (i % 3 == 0) ? larguraBarra + 1 : larguraBarra;
                    
                    g2d.fillRect(x, (getHeight() - altura) / 2, largura, altura);
                    x += largura + espacamento;
                }
                
                g2d.dispose();
            }
        };
        
        painelBarras.setPreferredSize(new Dimension(300, 80));
        painelBarras.setBackground(Color.WHITE);
        painelBarras.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        
        return painelBarras;
    }
}