package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.aplicacao.excecoes.ReservaInvalidaException;
import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico;
import com.teatroabc.aplicacao.interfaces.ISessaoServico;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.modelos.Bilhete;
import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.dominio.modelos.Sessao;
import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAnimado;
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorData;
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorMoeda;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;

/**
 * Tela responsável por exibir os detalhes finais de um pedido de compra e
 * permitir que o usuário confirme a operação para gerar um Bilhete.
 * Refatorada para trabalhar com o conceito de Sessao.
 */
public class TelaConfirmarPedido extends JPanel {
    private final Cliente cliente;
    private final Sessao sessao;
    private final List<Assento> assentos;

    private final IClienteServico clienteServico;
    private final IPecaServico pecaServico;
    private final IReservaServico reservaServico;
    private final ISessaoServico sessaoServico;

    /**
     * Construtor refatorado da TelaConfirmarPedido.
     * @param cliente O cliente que está realizando a compra.
     * @param sessao A sessão selecionada para a compra.
     * @param assentos A lista de assentos selecionados.
     * @param clienteServico Serviço para repassar na navegação.
     * @param pecaServico Serviço para repassar na navegação.
     * @param reservaServico Serviço para efetivar a reserva.
     * @param sessaoServico Serviço para repassar na navegação.
     */
    public TelaConfirmarPedido(Cliente cliente, Sessao sessao, List<Assento> assentos,
                               IClienteServico clienteServico, IPecaServico pecaServico, 
                               IReservaServico reservaServico, ISessaoServico sessaoServico) {
        
        if (cliente == null || sessao == null || assentos == null || assentos.isEmpty() ||
            clienteServico == null || pecaServico == null || reservaServico == null || sessaoServico == null) {
            throw new IllegalArgumentException("Nenhum parâmetro pode ser nulo para TelaConfirmarPedido.");
        }
        
        this.cliente = cliente;
        this.sessao = sessao;
        this.assentos = assentos; 
        this.clienteServico = clienteServico;
        this.pecaServico = pecaServico;
        this.reservaServico = reservaServico;
        this.sessaoServico = sessaoServico;
        
        configurarTelaVisual();
    }

    private void configurarTelaVisual() {
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        JPanel containerPrincipal = new JPanel();
        containerPrincipal.setLayout(new BoxLayout(containerPrincipal, BoxLayout.Y_AXIS));
        containerPrincipal.setOpaque(false);
        containerPrincipal.setBorder(BorderFactory.createEmptyBorder(20,30,20,30));

        JPanel painelLogo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelLogo.setOpaque(false);
        painelLogo.add(new LogoTeatro());
        containerPrincipal.add(painelLogo);
        containerPrincipal.add(Box.createVerticalStrut(30));

        JLabel titulo = new JLabel("CONFIRMAR PEDIDO");
        titulo.setFont(Constantes.FONTE_TITULO.deriveFont(42f));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        containerPrincipal.add(titulo);
        containerPrincipal.add(Box.createVerticalStrut(30));

        JPanel painelDetalhes = criarPainelDetalhesDoPedido();
        painelDetalhes.setAlignmentX(Component.CENTER_ALIGNMENT);
        containerPrincipal.add(painelDetalhes);
        containerPrincipal.add(Box.createVerticalStrut(40));

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        painelBotoes.setOpaque(false);
        BotaoAnimado btnVoltarUI = new BotaoAnimado("VOLTAR",
                Constantes.CINZA_ESCURO, Constantes.AZUL_CLARO.darker(), new Dimension(180, 55));
        btnVoltarUI.setFont(new Font("Arial", Font.BOLD, 18));
        btnVoltarUI.addActionListener(e -> navegarParaTelaAnterior());
        BotaoAnimado btnConfirmarUI = new BotaoAnimado("FINALIZAR COMPRA",
                Constantes.LARANJA, Constantes.AMARELO.darker(), new Dimension(260, 55));
        btnConfirmarUI.setFont(new Font("Arial", Font.BOLD, 18));
        btnConfirmarUI.addActionListener(e -> processarConfirmacaoDaCompra());
        painelBotoes.add(btnVoltarUI);
        painelBotoes.add(btnConfirmarUI);
        containerPrincipal.add(painelBotoes);
        containerPrincipal.add(Box.createVerticalStrut(20));

        JScrollPane scrollPane = new JScrollPane(containerPrincipal);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel criarPainelDetalhesDoPedido() {
        JPanel painel = new JPanel();
        painel.setBackground(Constantes.CINZA_ESCURO);
        painel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Constantes.AZUL_CLARO.darker(), 1),
            BorderFactory.createEmptyBorder(20, 30, 20, 30))
        );
        painel.setLayout(new GridBagLayout());
        painel.setMaximumSize(new Dimension(650, 480));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 15, 8, 15);
        int linhaAtual = 0;

        BigDecimal subtotalExibicao = calcularSubtotalParaExibicao();
        BigDecimal fatorDesconto = this.cliente.getPlanoFidelidade().getFatorDesconto();
        BigDecimal descontoExibicao = subtotalExibicao.multiply(fatorDesconto).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalExibicao = calcularTotalParaExibicao(subtotalExibicao, descontoExibicao);

        if (this.cliente.isMembroGold()) {
            gbc.gridx = 0; gbc.gridy = linhaAtual++; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
            gbc.insets = new Insets(0, 0, 15, 0);
            painel.add(criarBadgeABCGoldVisual(), gbc);
            gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(8, 15, 8, 15);
        }

        adicionarLinhaDetalhe(painel, gbc, linhaAtual++, "Peça:", this.sessao.getPeca().getTitulo());
        adicionarLinhaDetalhe(painel, gbc, linhaAtual++, "Data:", FormatadorData.formatar(this.sessao.getDataHora()));
        adicionarLinhaDetalhe(painel, gbc, linhaAtual++, "Turno:", this.sessao.getTurno().toString());
        String assentosStr = this.assentos.stream().map(Assento::getCodigo).collect(Collectors.joining(", "));
        adicionarLinhaDetalhe(painel, gbc, linhaAtual++, "Assentos:", assentosStr);
        adicionarLinhaDetalhe(painel, gbc, linhaAtual++, "Cliente:", this.cliente.getNome());

        gbc.gridx = 0; gbc.gridy = linhaAtual++; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 0, 10, 0);
        painel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
        gbc.fill = GridBagConstraints.NONE; gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 15, 8, 15);

        adicionarLinhaDetalhe(painel, gbc, linhaAtual++, "Subtotal:", FormatadorMoeda.formatar(subtotalExibicao));
        if (descontoExibicao.compareTo(BigDecimal.ZERO) > 0) {
            JLabel lblDescRotulo = criarLabelDetalhe("Desconto (" + this.cliente.getNomePlanoFidelidade() + "):");
            lblDescRotulo.setForeground(Constantes.AMARELO);
            JLabel lblDescValor = criarLabelValor("- " + FormatadorMoeda.formatar(descontoExibicao));
            lblDescValor.setForeground(Constantes.AMARELO);
            adicionarLinhaComponentes(painel, gbc, linhaAtual++, lblDescRotulo, lblDescValor);
        }

        JLabel lblTotalRotulo = criarLabelDetalhe("TOTAL A PAGAR:");
        lblTotalRotulo.setFont(new Font("Arial", Font.BOLD, 20));
        JLabel lblTotalValor = criarLabelValor(FormatadorMoeda.formatar(totalExibicao));
        lblTotalValor.setFont(new Font("Arial", Font.BOLD, 22));
        if (this.cliente.isMembroGold()) {
            lblTotalValor.setForeground(Constantes.AMARELO);
        }
        adicionarLinhaComponentes(painel, gbc, linhaAtual, lblTotalRotulo, lblTotalValor);

        return painel;
    }
    
    private BigDecimal calcularSubtotalParaExibicao() {
        return this.assentos.stream()
                .map(Assento::getPreco)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }
    
    private BigDecimal calcularTotalParaExibicao(BigDecimal subtotal, BigDecimal desconto) {
        BigDecimal total = subtotal.subtract(desconto);
        return total.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : total.setScale(2, RoundingMode.HALF_UP);
    }

    private void adicionarLinhaDetalhe(JPanel painel, GridBagConstraints gbc, int linha, String rotulo, String valor) {
        gbc.gridx = 0; gbc.gridy = linha;
        painel.add(criarLabelDetalhe(rotulo), gbc);
        gbc.gridx = 1;
        painel.add(criarLabelValor(valor), gbc);
    }
    
    private void adicionarLinhaComponentes(JPanel painel, GridBagConstraints gbc, int linha, Component comp1, Component comp2) {
        gbc.gridx = 0; gbc.gridy = linha;
        painel.add(comp1, gbc);
        gbc.gridx = 1;
        painel.add(comp2, gbc);
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

    private void processarConfirmacaoDaCompra() {
        try {
            // A chamada para o serviço de reserva agora usa a assinatura correta.
            Bilhete bilheteCriado = this.reservaServico.criarReserva(
                this.sessao, this.cliente, this.assentos
            );

            // A exibição da mensagem de sucesso agora usa o getter correto do bilhete.
            StringBuilder mensagem = new StringBuilder("<html><body style='width: 350px;'>");
            mensagem.append("<h2>Compra Realizada com Sucesso!</h2>");
            mensagem.append("<p><b>Peça:</b> ").append(bilheteCriado.getSessao().getPeca().getTitulo()).append("</p>");
            mensagem.append("<p><b>Data:</b> ").append(FormatadorData.formatar(bilheteCriado.getSessao().getDataHora())).append("</p>");
            mensagem.append("<p><b>Turno:</b> ").append(bilheteCriado.getSessao().getTurno().toString()).append("</p>");
            mensagem.append("<p><b>Cliente:</b> ").append(bilheteCriado.getCliente().getNome()).append("</p>");
            String assentosStr = bilheteCriado.getAssentos().stream().map(Assento::getCodigo).collect(Collectors.joining(", "));
            mensagem.append("<p><b>Assentos:</b> ").append(assentosStr).append("</p>");
            mensagem.append("<hr><p><b>Subtotal:</b> ").append(FormatadorMoeda.formatar(bilheteCriado.getSubtotal())).append("</p>");
            if (bilheteCriado.getValorDesconto().compareTo(BigDecimal.ZERO) > 0) {
                mensagem.append("<p style='color:orange;'><b>Desconto (").append(bilheteCriado.getCliente().getNomePlanoFidelidade()).append("):</b> -")
                        .append(FormatadorMoeda.formatar(bilheteCriado.getValorDesconto())).append("</p>");
            }
            mensagem.append("<p style='font-size:1.1em;'><b>TOTAL PAGO: ").append(FormatadorMoeda.formatar(bilheteCriado.getValorTotal())).append("</b></p>");
            mensagem.append("<p><i>Código do Bilhete: ").append(bilheteCriado.getCodigoBarras()).append("</i></p>");
            mensagem.append("</body></html>");

            JOptionPane.showMessageDialog(this, mensagem.toString(), "Sucesso na Compra", JOptionPane.INFORMATION_MESSAGE);

            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setContentPane(new TelaPrincipal(this.clienteServico, this.pecaServico, this.reservaServico, this.sessaoServico));
            frame.revalidate();
            frame.repaint();

        } catch (ReservaInvalidaException e) {
            JOptionPane.showMessageDialog(this, "Não foi possível confirmar o pedido:\n" + e.getMessage(), "Reserva Inválida", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Erro nos dados fornecidos para a reserva:\n" + e.getMessage(), "Erro de Dados", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado ao finalizar a compra:\n" + e.getMessage(), "Erro Crítico", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void navegarParaTelaAnterior() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaSelecionarAssento(
            this.sessao, this.pecaServico, this.clienteServico, this.reservaServico, this.sessaoServico
        ));
        frame.revalidate();
        frame.repaint();
    }
}