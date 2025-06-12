package com.teatroabc.infraestrutura.ui_swing.componentes;

import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.dominio.modelos.Sessao;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorData;
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorMoeda;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Componente que exibe um resumo detalhado de um pedido para confirmação.
 * Encapsula toda a lógica de layout e formatação dos dados do pedido,
 * como informações da sessão, cliente, assentos e valores financeiros.
 */
public class PainelResumoPedido extends JPanel {

    /**
     * Construtor do PainelResumoPedido.
     * @param cliente O cliente associado ao pedido.
     * @param sessao A sessão escolhida.
     * @param assentos A lista de assentos selecionados.
     */
    public PainelResumoPedido(Cliente cliente, Sessao sessao, List<Assento> assentos) {
        configurarPainel();
        popularDetalhes(cliente, sessao, assentos);
    }

    /**
     * Configura as propriedades visuais do painel principal.
     */
    private void configurarPainel() {
        setBackground(Constantes.CINZA_ESCURO);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Constantes.AZUL_CLARO.darker(), 1),
            BorderFactory.createEmptyBorder(20, 30, 20, 30))
        );
        setLayout(new GridBagLayout());
        setMaximumSize(new Dimension(650, 480));
    }
    
    /**
     * Adiciona todos os componentes visuais de detalhe ao painel.
     */
    private void popularDetalhes(Cliente cliente, Sessao sessao, List<Assento> assentos) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 15, 8, 15);
        int linhaAtual = 0;

        // Cálculos para exibição
        BigDecimal subtotalExibicao = calcularSubtotalParaExibicao(assentos);
        BigDecimal fatorDesconto = cliente.getPlanoFidelidade().getFatorDesconto();
        BigDecimal descontoExibicao = subtotalExibicao.multiply(fatorDesconto).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalExibicao = calcularTotalParaExibicao(subtotalExibicao, descontoExibicao);

        // Badge de Membro (se aplicável)
        if (cliente.isMembroGold()) {
            gbc.gridx = 0; gbc.gridy = linhaAtual++; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
            gbc.insets = new Insets(0, 0, 15, 0);
            add(criarBadgeABCGoldVisual(), gbc);
            gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(8, 15, 8, 15);
        }

        // Detalhes do Pedido
        adicionarLinhaDetalhe(gbc, linhaAtual++, "Peça:", sessao.getPeca().getTitulo());
        adicionarLinhaDetalhe(gbc, linhaAtual++, "Data:", FormatadorData.formatar(sessao.getDataHora()));
        adicionarLinhaDetalhe(gbc, linhaAtual++, "Turno:", sessao.getTurno().toString());
        String assentosStr = assentos.stream().map(Assento::getCodigo).collect(Collectors.joining(", "));
        adicionarLinhaDetalhe(gbc, linhaAtual++, "Assentos:", assentosStr);
        adicionarLinhaDetalhe(gbc, linhaAtual++, "Cliente:", cliente.getNome());

        // Linha separadora antes dos valores
        gbc.gridx = 0; gbc.gridy = linhaAtual++; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 0, 10, 0);
        add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
        gbc.fill = GridBagConstraints.NONE; gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 15, 8, 15);

        // Valores Financeiros
        adicionarLinhaDetalhe(gbc, linhaAtual++, "Subtotal:", FormatadorMoeda.formatar(subtotalExibicao));
        if (descontoExibicao.compareTo(BigDecimal.ZERO) > 0) {
            JLabel lblDescRotulo = criarLabelDetalhe("Desconto (" + cliente.getNomePlanoFidelidade() + "):");
            lblDescRotulo.setForeground(Constantes.AMARELO);
            JLabel lblDescValor = criarLabelValor("- " + FormatadorMoeda.formatar(descontoExibicao));
            lblDescValor.setForeground(Constantes.AMARELO);
            adicionarLinhaComponentes(gbc, linhaAtual++, lblDescRotulo, lblDescValor);
        }

        JLabel lblTotalRotulo = criarLabelDetalhe("TOTAL A PAGAR:");
        lblTotalRotulo.setFont(new Font("Arial", Font.BOLD, 20));
        JLabel lblTotalValor = criarLabelValor(FormatadorMoeda.formatar(totalExibicao));
        lblTotalValor.setFont(new Font("Arial", Font.BOLD, 22));
        if (cliente.isMembroGold()) {
            lblTotalValor.setForeground(Constantes.AMARELO);
        }
        adicionarLinhaComponentes(gbc, linhaAtual, lblTotalRotulo, lblTotalValor);
    }
    
    private BigDecimal calcularSubtotalParaExibicao(List<Assento> assentos) {
        return assentos.stream()
                .map(Assento::getPreco)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }
    
    private BigDecimal calcularTotalParaExibicao(BigDecimal subtotal, BigDecimal desconto) {
        BigDecimal total = subtotal.subtract(desconto);
        return total.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : total.setScale(2, RoundingMode.HALF_UP);
    }

    private void adicionarLinhaDetalhe(GridBagConstraints gbc, int linha, String rotulo, String valor) {
        gbc.gridx = 0; gbc.gridy = linha;
        add(criarLabelDetalhe(rotulo), gbc);
        gbc.gridx = 1;
        add(criarLabelValor(valor), gbc);
    }
    
    private void adicionarLinhaComponentes(GridBagConstraints gbc, int linha, Component comp1, Component comp2) {
        gbc.gridx = 0; gbc.gridy = linha;
        add(comp1, gbc);
        gbc.gridx = 1;
        add(comp2, gbc);
    }

    private JLabel criarLabelDetalhe(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JLabel criarLabelValor(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JPanel criarBadgeABCGoldVisual() {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        badge.setOpaque(false);
        JLabel estrela = new JLabel("⭐");
        estrela.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        JLabel texto = new JLabel("MEMBRO ABC GOLD");
        texto.setFont(new Font("Arial", Font.BOLD, 16));
        texto.setForeground(Constantes.AMARELO);
        badge.add(estrela);
        badge.add(texto);
        return badge;
    }
}