package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAnimado;
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.componentes.PainelCodigoBarras; // Usado para o painel visual do código de barras
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.modelos.Bilhete;
import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.dominio.validadores.ValidadorCPF;
// import com.teatroabc.dominio.modelos.Assento; // Import não é estritamente necessário aqui, pois os códigos são obtidos via stream
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorData;
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorMoeda;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.stream.Collectors;

/**
 * Diálogo para exibir os detalhes completos de um Bilhete.
 * Atua como um Adaptador Primário, recebendo uma entidade de domínio Bilhete
 * e sendo responsável por sua formatação e apresentação visual.
 */
public class DialogoDetalhesBilhete extends JDialog {
    
    private final Bilhete bilheteExibido; // Armazena o bilhete para referência

    /**
     * Construtor que define um Frame pai.
     * @param parent O Frame proprietário do diálogo.
     * @param bilhete O objeto Bilhete cujos detalhes serão exibidos. Não pode ser nulo.
     * @throws IllegalArgumentException se bilhete for nulo.
     */
    public DialogoDetalhesBilhete(Frame parent, Bilhete bilhete) {
        super(parent, "Detalhes do Bilhete", true);
        if (bilhete == null) throw new IllegalArgumentException("Bilhete não pode ser nulo para DialogoDetalhesBilhete.");
        this.bilheteExibido = bilhete;
        configurarDialogo();
    }
    
    /**
     * Construtor que não define um Frame pai explícito (usará um Frame oculto padrão).
     * @param bilhete O objeto Bilhete cujos detalhes serão exibidos. Não pode ser nulo.
     * @throws IllegalArgumentException se bilhete for nulo.
     */
    public DialogoDetalhesBilhete(Bilhete bilhete) {
        this((Frame) null, bilhete); // Delega para o outro construtor
    }
    
    /**
     * Configura a estrutura e os componentes do diálogo.
     */
    private void configurarDialogo() {
        setSize(600, 780); // Altura ajustada para acomodar mais informações
        setLocationRelativeTo(getParent());
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(Constantes.AZUL_ESCURO);
        
        // Cabeçalho com logo
        JPanel cabecalho = new JPanel(new FlowLayout(FlowLayout.CENTER));
        cabecalho.setBackground(Constantes.AZUL_ESCURO);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(20, 0, 15, 0)); // Ajuste de padding
        LogoTeatro logo = new LogoTeatro();
        logo.setPreferredSize(new Dimension(250, 70)); // Ajustar tamanho do logo se necessário
        cabecalho.add(logo);
        
        // Conteúdo do bilhete
        JPanel conteudo = criarPainelConteudoBilhete(); // Usa o bilheteExibido
        
        // Botão fechar
        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotao.setBackground(Constantes.AZUL_ESCURO);
        painelBotao.setBorder(BorderFactory.createEmptyBorder(15, 0, 20, 0)); // Ajuste de padding
        
        BotaoAnimado btnFechar = new BotaoAnimado(
            "FECHAR",
            Constantes.AZUL_CLARO,
            Constantes.AZUL_CLARO.darker(), // Cor de hover um pouco mais escura
            new Dimension(160, 50)
        );
        btnFechar.setFont(new Font("Arial", Font.BOLD, 16));
        btnFechar.addActionListener(e -> dispose());
        painelBotao.add(btnFechar);
        
        painelPrincipal.add(cabecalho, BorderLayout.NORTH);
        painelPrincipal.add(conteudo, BorderLayout.CENTER);
        painelPrincipal.add(painelBotao, BorderLayout.SOUTH);
        
        setContentPane(painelPrincipal);
    }
    
    /**
     * Cria o painel principal que contém todos os detalhes formatados do bilhete.
     * @return Um JPanel com o conteúdo do bilhete.
     */
    private JPanel criarPainelConteudoBilhete() {
        JPanel painelContainer = new JPanel(new BorderLayout());
        painelContainer.setOpaque(false); // Para o fundo do painelPrincipal aparecer
        painelContainer.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40)); // Margens laterais

        JPanel painelDetalhesInterno = new JPanel(); // Renomeado de 'painel'
        painelDetalhesInterno.setLayout(new BoxLayout(painelDetalhesInterno, BoxLayout.Y_AXIS));
        painelDetalhesInterno.setBackground(Color.WHITE); // Fundo branco para o "corpo" do bilhete
        painelDetalhesInterno.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), // Borda sutil ao redor do corpo
            BorderFactory.createEmptyBorder(25, 30, 25, 30)  // Padding interno
        ));
        
        Cliente clienteDoBilhete = this.bilheteExibido.getCliente();

        // Badge ABC GOLD se aplicável
        if (clienteDoBilhete.isMembroGold()) {
            JPanel badgeABC = criarBadgeABCGold();
            badgeABC.setAlignmentX(Component.CENTER_ALIGNMENT);
            painelDetalhesInterno.add(badgeABC);
            painelDetalhesInterno.add(Box.createVerticalStrut(15));
        }
        
        // Título da Peça
        JLabel lblTituloPeca = new JLabel(this.bilheteExibido.getPeca().getTitulo());
        lblTituloPeca.setFont(new Font("Arial", Font.BOLD, 26)); // Fonte um pouco menor
        lblTituloPeca.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTituloPeca.setForeground(Color.BLACK);
        painelDetalhesInterno.add(lblTituloPeca);
        if (this.bilheteExibido.getPeca().getSubtitulo() != null && !this.bilheteExibido.getPeca().getSubtitulo().isEmpty()){
            JLabel lblSubtituloPeca = new JLabel(this.bilheteExibido.getPeca().getSubtitulo());
            lblSubtituloPeca.setFont(new Font("Arial", Font.ITALIC, 16));
            lblSubtituloPeca.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblSubtituloPeca.setForeground(Color.DARK_GRAY);
            painelDetalhesInterno.add(lblSubtituloPeca);
        }
        painelDetalhesInterno.add(Box.createVerticalStrut(25));
        
        // Informações principais formatadas
        adicionarLinhaDeInformacao(painelDetalhesInterno, "Data Apresentação:", FormatadorData.formatar(this.bilheteExibido.getPeca().getDataHora()));
        adicionarLinhaDeInformacao(painelDetalhesInterno, "Turno:", this.bilheteExibido.getTurno().toString());
        adicionarLinhaDeInformacao(painelDetalhesInterno, "Cliente:", clienteDoBilhete.getNome());
        adicionarLinhaDeInformacao(painelDetalhesInterno, "CPF:", ValidadorCPF.formatarParaExibicao(clienteDoBilhete.getCpf())); // Usando o formatador de ValidadorCPF
        
        String assentosStr = this.bilheteExibido.getAssentos().stream()
            .map(assento -> assento.getCodigo()) // Assumindo que Assento tem getCodigo()
            .collect(Collectors.joining(", "));
        adicionarLinhaDeInformacao(painelDetalhesInterno, "Assentos:", assentosStr);
        
        painelDetalhesInterno.add(Box.createVerticalStrut(10)); // Espaço antes dos valores
        
        // Subtotal
        adicionarLinhaDeInformacao(painelDetalhesInterno, "Subtotal:", FormatadorMoeda.formatar(this.bilheteExibido.getSubtotal()));
        
        // Desconto (se houver)
        if (this.bilheteExibido.getValorDesconto().compareTo(BigDecimal.ZERO) > 0) {
            String nomePlano = clienteDoBilhete.getNomePlanoFidelidade();
            JLabel lblRotuloDesc = criarLabelParaRotulo("Desconto (" + nomePlano + "):");
            lblRotuloDesc.setForeground(Constantes.LARANJA); // Cor de destaque
            JLabel lblValorDesc = criarLabelParaValor("- " + FormatadorMoeda.formatar(this.bilheteExibido.getValorDesconto()));
            lblValorDesc.setForeground(Constantes.LARANJA);
            adicionarLinhaComComponentesCustom(painelDetalhesInterno, lblRotuloDesc, lblValorDesc, false);
        }
        
        // Valor Total Pago (com destaque)
        JLabel lblTotalRotulo = criarLabelParaRotulo("VALOR TOTAL PAGO:");
        lblTotalRotulo.setFont(new Font("Arial", Font.BOLD, 20)); // Fonte maior para total
        JLabel lblTotalValor = criarLabelParaValor(FormatadorMoeda.formatar(this.bilheteExibido.getValorTotal()));
        lblTotalValor.setFont(new Font("Arial", Font.BOLD, 20));
        if (clienteDoBilhete.isMembroGold()) {
            lblTotalValor.setForeground(Constantes.AMARELO); // Destaque amarelo se for membro Gold
        }
        adicionarLinhaComComponentesCustom(painelDetalhesInterno, lblTotalRotulo, lblTotalValor, true); // true para borda superior
        
        painelDetalhesInterno.add(Box.createVerticalStrut(25));
        
        // Código de Barras (Visual e Texto)
        JLabel lblTituloCodigoBarras = new JLabel("CÓDIGO DE BARRAS");
        lblTituloCodigoBarras.setFont(new Font("Arial", Font.BOLD, 12)); // Fonte menor
        lblTituloCodigoBarras.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTituloCodigoBarras.setForeground(Color.DARK_GRAY);
        painelDetalhesInterno.add(lblTituloCodigoBarras);
        painelDetalhesInterno.add(Box.createVerticalStrut(5));
        
        // Usando o componente PainelCodigoBarras
        PainelCodigoBarras painelVisCodigoBarras = new PainelCodigoBarras(this.bilheteExibido.getCodigoBarras());
        painelVisCodigoBarras.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Para garantir que o painel do código de barras não exceda uma largura razoável
        painelVisCodigoBarras.setMaximumSize(new Dimension(350, 80));
        painelDetalhesInterno.add(painelVisCodigoBarras);
        // O PainelCodigoBarras já desenha o texto do código abaixo das barras.
        // Se quiser um texto separado adicional:
        // JLabel lblCodigoNumerico = new JLabel(this.bilheteExibido.getCodigoBarras());
        // ... (configurar e adicionar lblCodigoNumerico)

        painelContainer.add(new JScrollPane(painelDetalhesInterno), BorderLayout.CENTER); // Adiciona scroll ao conteúdo
        return painelContainer;
    }
    
    // Métodos auxiliares para criar JLabels padronizados
    private JLabel criarLabelParaRotulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.BOLD, 16)); // Fonte um pouco menor para rótulos
        label.setForeground(Color.DARK_GRAY);
        // Define um tamanho preferencial para alinhar os valores, pode precisar de ajuste
        label.setPreferredSize(new Dimension(170, 22)); 
        return label;
    }

    private JLabel criarLabelParaValor(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.PLAIN, 16)); // Fonte um pouco menor para valores
        label.setForeground(Color.BLACK);
        return label;
    }
    
    /**
     * Adiciona uma linha de informação (rótulo e valor) ao painel especificado.
     * @param painelAlvo O JPanel onde a linha será adicionada.
     * @param textoRotulo O texto para o rótulo.
     * @param textoValor O texto para o valor.
     */
    private void adicionarLinhaDeInformacao(JPanel painelAlvo, String textoRotulo, String textoValor) {
        adicionarLinhaComComponentesCustom(painelAlvo, criarLabelParaRotulo(textoRotulo), criarLabelParaValor(textoValor), false);
    }

    /**
     * Adiciona uma linha de informação ao painel, composta por dois componentes (geralmente JLabels).
     * @param painelAlvo O JPanel onde a linha será adicionada.
     * @param compRotulo O JComponent para o rótulo.
     * @param compValor O JComponent para o valor.
     * @param comBordaSuperior true se uma borda superior sutil deve ser adicionada.
     */
    private void adicionarLinhaComComponentesCustom(JPanel painelAlvo, Component compRotulo, Component compValor, boolean comBordaSuperior) {
        JPanel linhaPainel = new JPanel(new BorderLayout(10, 0)); // Espaçamento entre rótulo e valor
        linhaPainel.setBackground(Color.WHITE);
        linhaPainel.setOpaque(true);
        linhaPainel.setAlignmentX(Component.CENTER_ALIGNMENT); // Tenta centralizar o painel da linha
        // Define um tamanho máximo para controlar a largura das linhas de informação
        linhaPainel.setMaximumSize(new Dimension(480, 40)); 
        
        if (comBordaSuperior) {
            linhaPainel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(8, 0, 5, 0) 
            ));
        } else {
            linhaPainel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Padding vertical
        }
        
        linhaPainel.add(compRotulo, BorderLayout.WEST);
        linhaPainel.add(compValor, BorderLayout.CENTER);
        painelAlvo.add(linhaPainel);
    }
        
    // O método formatarCPF foi substituído pelo uso de ValidadorCPF.formatarParaExibicao
    
    private JPanel criarBadgeABCGold() {
        // ... (implementação do badge como antes)
        JPanel badge = new JPanel(); /*...*/ return badge;
    }
    
    // O método criarPainelCodigoBarras foi substituído pelo uso do componente PainelCodigoBarras
}