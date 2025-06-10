package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAnimado;
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
// Constantes está em infraestrutura.ui_swing.constantes_ui
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.modelos.Bilhete;
import com.teatroabc.dominio.modelos.Cliente; // Para o cliente.isMembroGold()
// Formatadores estão em infraestrutura.ui_swing.util
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorData;
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorMoeda;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal; // Importar BigDecimal
import java.util.stream.Collectors;

public class DialogoDetalhesBilhete extends JDialog {
    
    // Construtores permanecem os mesmos
    public DialogoDetalhesBilhete(Frame parent, Bilhete bilhete) {
        super(parent, "Detalhes do Bilhete", true);
        if (bilhete == null) throw new IllegalArgumentException("Bilhete não pode ser nulo.");
        configurarDialogo(bilhete);
    }
    
    public DialogoDetalhesBilhete(Bilhete bilhete) {
        super((Frame) null, "Detalhes do Bilhete", true); // Chama o construtor com Frame pai nulo
        if (bilhete == null) throw new IllegalArgumentException("Bilhete não pode ser nulo.");
        configurarDialogo(bilhete);
    }
    
    private void configurarDialogo(Bilhete bilhete) {
        // ... (configurações do JDialog como setSize, setLocationRelativeTo, etc. permanecem) ...
        setSize(600, 780); // Altura pode precisar de ajuste
        setLocationRelativeTo(getParent());
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(Constantes.AZUL_ESCURO);
        
        // Cabeçalho com logo (como antes)
        JPanel cabecalho = new JPanel(new FlowLayout(FlowLayout.CENTER));
        cabecalho.setBackground(Constantes.AZUL_ESCURO);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        cabecalho.add(new LogoTeatro());
        
        // Conteúdo do bilhete (aqui ocorrem as mudanças principais)
        JPanel conteudo = criarConteudoBilheteDetalhado(bilhete); // Renomeado para clareza
        
        // Botão fechar (como antes)
        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotao.setBackground(Constantes.AZUL_ESCURO); // Supondo que Constantes está no pacote correto
        painelBotao.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        BotaoAnimado btnFechar = new BotaoAnimado(
            "FECHAR",                             // texto
            Constantes.AZUL_CLARO,                // corFundo (exemplo, pode ser outra cor de Constantes)
            new Color(70, 130, 180),              // corHover (exemplo, uma variação do azul claro)
            new Dimension(150, 50)                // tamanho
        );
        btnFechar.setFont(new Font("Arial", Font.BOLD, 18));
        btnFechar.addActionListener(e -> dispose());
        painelBotao.add(btnFechar);

        painelPrincipal.add(cabecalho, BorderLayout.NORTH);
        painelPrincipal.add(conteudo, BorderLayout.CENTER);
        painelPrincipal.add(painelBotao, BorderLayout.SOUTH);
        
        setContentPane(painelPrincipal);
    }
    
    private JPanel criarConteudoBilheteDetalhado(Bilhete bilhete) { // Renomeado
        JPanel painelContainer = new JPanel(new BorderLayout());
        // ... (configurações do painelContainer como antes) ...
        painelContainer.setBackground(Constantes.AZUL_ESCURO);
        painelContainer.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        Cliente clienteDoBilhete = bilhete.getCliente(); // Obter o cliente uma vez

        // Badge ABC GOLD se aplicável (usando o método do Cliente)
        if (clienteDoBilhete.isMembroGold()) {
            JPanel badgeABC = criarBadgeABCGold(); // Método criarBadgeABCGold como antes
            badgeABC.setAlignmentX(Component.CENTER_ALIGNMENT);
            painel.add(badgeABC);
            painel.add(Box.createVerticalStrut(20));
        }
        
        JLabel lblTitulo = new JLabel(bilhete.getPeca().getTitulo());
        // ... (configurações do lblTitulo) ...
        painel.add(lblTitulo);
        painel.add(Box.createVerticalStrut(30));
        
        // Informações principais
        adicionarInfoLinha(painel, "Data:", FormatadorData.formatar(bilhete.getPeca().getDataHora()));
        adicionarInfoLinha(painel, "Turno:", bilhete.getTurno().toString()); // Exibe o turno
        adicionarInfoLinha(painel, "Cliente:", clienteDoBilhete.getNome());
        adicionarInfoLinha(painel, "CPF:", formatarCPFUmaLinha(clienteDoBilhete.getCpf())); // Renomeado formatarCPF
        
        String assentosStr = bilhete.getAssentos().stream()
            .map(a -> a.getCodigo()) // Assumindo que Assento tem getCodigo()
            .collect(Collectors.joining(", "));
        adicionarInfoLinha(painel, "Assentos:", assentosStr);
        
        // Valores financeiros do Bilhete (que já incluem o desconto)
        adicionarInfoLinha(painel, "Subtotal:", FormatadorMoeda.formatar(bilhete.getSubtotal()));
        
        // Exibir desconto se for maior que zero
        if (bilhete.getValorDesconto() != null && bilhete.getValorDesconto().compareTo(BigDecimal.ZERO) > 0) {
            // Usar o nome do plano de fidelidade do cliente
            String nomePlano = clienteDoBilhete.getNomePlanoFidelidade();
            JLabel lblRotuloDesconto = criarLabelInfo("Desconto (" + nomePlano + "):");
            lblRotuloDesconto.setForeground(new Color(255, 140, 0)); // Cor de destaque para desconto
            
            JLabel lblValorDesconto = criarLabelValor("- " + FormatadorMoeda.formatar(bilhete.getValorDesconto()));
            lblValorDesconto.setForeground(new Color(255, 140, 0));
            
            adicionarInfoLinhaComComponentes(painel, lblRotuloDesconto, lblValorDesconto);
        }
        
        // Valor total (destacado)
        // ... (código para exibir valor total como antes, usando bilhete.getValorTotal()) ...
        // ... (código para código de barras como antes) ...
        JPanel linhaTotalPainel = new JPanel(new BorderLayout());
        // ... (configurações linhaTotalPainel)
        JLabel lblTotalRotulo = criarLabelInfo("VALOR TOTAL PAGO:"); // Texto mais claro
        lblTotalRotulo.setFont(new Font("Arial", Font.BOLD, 22));
        JLabel lblTotalValor = criarLabelValor(FormatadorMoeda.formatar(bilhete.getValorTotal()));
        lblTotalValor.setFont(new Font("Arial", Font.BOLD, 22));
        if (clienteDoBilhete.isMembroGold()) {
            lblTotalValor.setForeground(Constantes.AMARELO); // Destaque se for membro Gold
        }
        adicionarInfoLinhaComComponentes(painel, lblTotalRotulo, lblTotalValor, true); // true para adicionar borda superior


        painel.add(Box.createVerticalStrut(30));
        // ... (Código de Barras como antes) ...
        
        painelContainer.add(painel, BorderLayout.CENTER);
        return painelContainer;
    }
    
    // Métodos auxiliares para criar os JLabels (para evitar repetição)
    private JLabel criarLabelInfo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(Color.DARK_GRAY);
        label.setPreferredSize(new Dimension(180, 25)); // Ajustar conforme necessidade
        return label;
    }

    private JLabel criarLabelValor(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.PLAIN, 18));
        label.setForeground(Color.BLACK);
        return label;
    }
    
    // Método auxiliar para adicionar uma linha de informação com JLabels customizados
    private void adicionarInfoLinhaComComponentes(JPanel painel, JLabel lblRotulo, JLabel lblValor) {
        adicionarInfoLinhaComComponentes(painel, lblRotulo, lblValor, false);
    }

    private void adicionarInfoLinhaComComponentes(JPanel painel, JLabel lblRotulo, JLabel lblValor, boolean comBordaSuperior) {
        JPanel linhaPainel = new JPanel(new BorderLayout());
        linhaPainel.setBackground(Color.WHITE);
        if (comBordaSuperior) {
            linhaPainel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY), // Borda mais sutil
                BorderFactory.createEmptyBorder(10, 0, 8, 0) // Aumentar padding superior
            ));
        } else {
            linhaPainel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        }
        
        linhaPainel.add(lblRotulo, BorderLayout.WEST);
        linhaPainel.add(lblValor, BorderLayout.CENTER);
        painel.add(linhaPainel);
    }

    // Método adicionarInfo original, agora refatorado para usar os auxiliares
    private void adicionarInfoLinha(JPanel painel, String rotuloTexto, String valorTexto) { // Renomeado de adicionarInfo
        adicionarInfoLinhaComComponentes(painel, criarLabelInfo(rotuloTexto), criarLabelValor(valorTexto));
    }
        
    private String formatarCPFUmaLinha(String cpf) { // Renomeado de formatarCPF
        // ... (lógica de formatação do CPF como antes) ...
        return cpf;
    }

    // O método criarBadgeABCGold() e criarPainelCodigoBarras() permanecem os mesmos
    private JPanel criarBadgeABCGold() { /* ... como antes ... */ return new JPanel();}
    private JPanel criarPainelCodigoBarras(String codigo) { /* ... como antes ... */ return new JPanel();}
}