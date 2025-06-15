package com.teatroabc.infraestrutura.ui_swing.componentes;

import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.modelos.Bilhete;
import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.dominio.modelos.Sessao; // Importa a nova entidade de domínio
import com.teatroabc.dominio.validadores.ValidadorCPF;
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorData;
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorMoeda;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.stream.Collectors;

/**
 * Diálogo modal para exibir os detalhes completos de um Bilhete.
 * Refatorado para obter os dados da peça e do turno através do objeto Sessao contido no Bilhete.
 * 
 * Na Arquitetura Hexagonal, esta classe é um Adaptador Primário que serve como
 * uma "view" especializada. Sua única responsabilidade é receber uma entidade de
 * domínio (Bilhete) e traduzir seus dados para uma representação visual.
 */
public class DialogoDetalhesBilhete extends JDialog {
    
    private final Bilhete bilheteExibido;

    /**
     * Construtor principal que define um Frame pai.
     * @param parent O Frame proprietário.
     * @param bilhete O objeto Bilhete cujos detalhes serão exibidos. Não pode ser nulo.
     */
    public DialogoDetalhesBilhete(Frame parent, Bilhete bilhete) {
        super(parent, "Detalhes do Bilhete", true);
        if (bilhete == null) throw new IllegalArgumentException("Bilhete não pode ser nulo para DialogoDetalhesBilhete.");
        this.bilheteExibido = bilhete;
        configurarDialogo();
    }
    
    /**
     * Construtor de conveniência sem Frame pai explícito.
     * @param bilhete O objeto Bilhete a ser exibido.
     */
    public DialogoDetalhesBilhete(Bilhete bilhete) {
        this((Frame) null, bilhete);
    }
    
    /**
     * Configura a estrutura, tamanho, e os componentes do diálogo.
     */
    private void configurarDialogo() {
        setSize(580, 750);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(Constantes.AZUL_ESCURO);
        
        JPanel cabecalho = new JPanel(new FlowLayout(FlowLayout.CENTER));
        cabecalho.setBackground(Constantes.AZUL_ESCURO);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(20, 0, 15, 0));
        LogoTeatro logo = new LogoTeatro();
        logo.setPreferredSize(new Dimension(250, 70));
        cabecalho.add(logo);
        
        JScrollPane scrollConteudo = new JScrollPane(criarPainelConteudoBilhete());
        scrollConteudo.setBorder(null);
        scrollConteudo.getViewport().setOpaque(false);
        scrollConteudo.setOpaque(false);
        
        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotao.setBackground(Constantes.AZUL_ESCURO);
        painelBotao.setBorder(BorderFactory.createEmptyBorder(15, 0, 20, 0));
        
        BotaoAnimado btnFechar = new BotaoAnimado("FECHAR", Constantes.AZUL_CLARO, Constantes.AZUL_CLARO.darker(), new Dimension(160, 50));
        btnFechar.setFont(new Font("Arial", Font.BOLD, 16));
        btnFechar.addActionListener(e -> dispose());
        painelBotao.add(btnFechar);
        
        painelPrincipal.add(cabecalho, BorderLayout.NORTH);
        painelPrincipal.add(scrollConteudo, BorderLayout.CENTER);
        painelPrincipal.add(painelBotao, BorderLayout.SOUTH);
        
        setContentPane(painelPrincipal);
    }
    
    /**
     * Cria o painel que contém todos os detalhes formatados do bilhete.
     * Acessa os dados da peça e do turno através do objeto Sessao.
     * @return Um JPanel com o conteúdo do bilhete.
     */
    private JPanel criarPainelConteudoBilhete() {
        JPanel painelContainer = new JPanel(new BorderLayout());
        painelContainer.setOpaque(false);
        painelContainer.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        JPanel painelDetalhes = new JPanel();
        painelDetalhes.setLayout(new BoxLayout(painelDetalhes, BoxLayout.Y_AXIS));
        painelDetalhes.setBackground(Color.WHITE);
        painelDetalhes.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));
        
        Cliente clienteDoBilhete = this.bilheteExibido.getCliente();
        Sessao sessaoDoBilhete = this.bilheteExibido.getSessao(); // Pega o objeto Sessao do bilhete

        if (clienteDoBilhete.isMembroGold()) {
            painelDetalhes.add(criarBadgeABCGoldVisual());
            painelDetalhes.add(Box.createVerticalStrut(15));
        }
        
        // MUDANÇA: Acessa o título da peça através da sessão
        JLabel lblTituloPeca = new JLabel(sessaoDoBilhete.getPeca().getTitulo());
        lblTituloPeca.setFont(new Font("Arial", Font.BOLD, 26));
        lblTituloPeca.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTituloPeca.setForeground(Color.BLACK);
        painelDetalhes.add(lblTituloPeca);

        if (sessaoDoBilhete.getPeca().getSubtitulo() != null && !sessaoDoBilhete.getPeca().getSubtitulo().isEmpty()){
            JLabel lblSubtituloPeca = new JLabel(sessaoDoBilhete.getPeca().getSubtitulo());
            lblSubtituloPeca.setFont(new Font("Arial", Font.ITALIC, 16));
            lblSubtituloPeca.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblSubtituloPeca.setForeground(Color.DARK_GRAY);
            painelDetalhes.add(lblSubtituloPeca);
        }
        painelDetalhes.add(Box.createVerticalStrut(25));
        
        // MUDANÇA: Acessa a data e o turno através da sessão
        adicionarLinhaDeInformacao(painelDetalhes, "Data Apresentação:", FormatadorData.formatar(sessaoDoBilhete.getDataHora()));
        adicionarLinhaDeInformacao(painelDetalhes, "Turno:", sessaoDoBilhete.getTurno().toString());
        adicionarLinhaDeInformacao(painelDetalhes, "Cliente:", clienteDoBilhete.getNome());
        adicionarLinhaDeInformacao(painelDetalhes, "CPF:", ValidadorCPF.formatarParaExibicao(clienteDoBilhete.getCpf()));
        
        String assentosStr = this.bilheteExibido.getAssentos().stream()
            .map(assento -> assento.getCodigo())
            .collect(Collectors.joining(", "));
        adicionarLinhaDeInformacao(painelDetalhes, "Assentos:", assentosStr);
        
        painelDetalhes.add(Box.createVerticalStrut(15));
        
        adicionarLinhaDeInformacao(painelDetalhes, "Subtotal:", FormatadorMoeda.formatar(this.bilheteExibido.getSubtotal()));
        
        if (this.bilheteExibido.getValorDesconto().compareTo(BigDecimal.ZERO) > 0) {
            String nomePlano = clienteDoBilhete.getNomePlanoFidelidade();
            JLabel lblRotuloDesc = criarLabelParaRotulo("Desconto (" + nomePlano + "):");
            JLabel lblValorDesc = criarLabelParaValor("- " + FormatadorMoeda.formatar(this.bilheteExibido.getValorDesconto()));
            adicionarLinhaComComponentesCustom(painelDetalhes, lblRotuloDesc, lblValorDesc, false, Constantes.LARANJA);
        }
        
        JLabel lblTotalRotulo = criarLabelParaRotulo("VALOR TOTAL PAGO:");
        lblTotalRotulo.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel lblTotalValor = criarLabelParaValor(FormatadorMoeda.formatar(this.bilheteExibido.getValorTotal()));
        lblTotalValor.setFont(new Font("Arial", Font.BOLD, 18));
        adicionarLinhaComComponentesCustom(painelDetalhes, lblTotalRotulo, lblTotalValor, true, Color.BLACK);
        
        painelDetalhes.add(Box.createVerticalStrut(25));
        
        JLabel lblTituloCodigoBarras = new JLabel("CÓDIGO DE BARRAS");
        lblTituloCodigoBarras.setFont(new Font("Arial", Font.BOLD, 12));
        lblTituloCodigoBarras.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTituloCodigoBarras.setForeground(Color.DARK_GRAY);
        painelDetalhes.add(lblTituloCodigoBarras);
        painelDetalhes.add(Box.createVerticalStrut(5));
        
        PainelCodigoBarras painelVisCodigoBarras = new PainelCodigoBarras(this.bilheteExibido.getCodigoBarras());
        painelVisCodigoBarras.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelVisCodigoBarras.setMaximumSize(new Dimension(350, 80));
        painelDetalhes.add(painelVisCodigoBarras);
        
        painelContainer.add(painelDetalhes, BorderLayout.CENTER);
        return painelContainer;
    }
    
    private void adicionarLinhaDeInformacao(JPanel painel, String rotulo, String valor) {
        adicionarLinhaComComponentesCustom(painel, criarLabelParaRotulo(rotulo), criarLabelParaValor(valor), false, Color.BLACK);
    }
    
    private void adicionarLinhaComComponentesCustom(JPanel painel, Component compRotulo, Component compValor, boolean comBorda, Color corTexto) {
        JPanel linhaPainel = new JPanel(new BorderLayout(10, 0));
        linhaPainel.setBackground(Color.WHITE);
        linhaPainel.setOpaque(true);
        linhaPainel.setAlignmentX(Component.CENTER_ALIGNMENT);
        linhaPainel.setMaximumSize(new Dimension(480, 40));
        
        compRotulo.setForeground(corTexto.darker());
        compValor.setForeground(corTexto);

        if (comBorda) {
            linhaPainel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(8, 0, 5, 0) 
            ));
        } else {
            linhaPainel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        }
        
        linhaPainel.add(compRotulo, BorderLayout.WEST);
        linhaPainel.add(compValor, BorderLayout.CENTER);
        painel.add(linhaPainel);
    }

    private JLabel criarLabelParaRotulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setPreferredSize(new Dimension(180, 22)); 
        return label;
    }

    private JLabel criarLabelParaValor(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        return label;
    }

    private JPanel criarBadgeABCGoldVisual() {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        badge.setOpaque(false);
        JLabel estrela = new JLabel("⭐");
        estrela.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        JLabel texto = new JLabel("MEMBRO ABC GOLD");
        texto.setFont(new Font("Arial", Font.BOLD, 16));
        texto.setForeground(new Color(218, 165, 32)); // Cor dourada
        badge.add(estrela);
        badge.add(texto);
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);
        return badge;
    }
}