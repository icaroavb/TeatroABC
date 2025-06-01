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
        setSize(500, 600);
        setLocationRelativeTo(getParent());
        setResizable(false);
        
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
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        painel.setMaximumSize(new Dimension(400, 400));
        
        // Título da peça
        JLabel lblTitulo = new JLabel(bilhete.getPeca().getTitulo());
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(lblTitulo);
        
        painel.add(Box.createVerticalStrut(20));
        
        // Informações
        adicionarInfo(painel, "Data:", FormatadorData.formatar(bilhete.getPeca().getDataHora()));
        adicionarInfo(painel, "Cliente:", bilhete.getCliente().getNome());
        adicionarInfo(painel, "CPF:", formatarCPF(bilhete.getCliente().getCpf()));
        
        // Assentos
        String assentosStr = bilhete.getAssentos().stream()
            .map(a -> a.getCodigo())
            .collect(Collectors.joining(", "));
        adicionarInfo(painel, "Assentos:", assentosStr);
        
        adicionarInfo(painel, "Valor Total:", FormatadorMoeda.formatar(bilhete.getValorTotal()));
        
        painel.add(Box.createVerticalStrut(30));
        
        // Código de barras
        JLabel lblCodigoBarras = new JLabel("CÓDIGO DE BARRAS");
        lblCodigoBarras.setFont(new Font("Arial", Font.BOLD, 14));
        lblCodigoBarras.setAlignmentX(Component