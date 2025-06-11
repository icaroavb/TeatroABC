package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAnimado;
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.modelos.Bilhete;
import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico;
import com.teatroabc.aplicacao.excecoes.ReservaInvalidaException;
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorMoeda;
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorData;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tela responsável por exibir os detalhes finais de um pedido de compra de ingressos
 * e permitir que o usuário confirme a operação, resultando na criação de um Bilhete.
 * 
 * Na Arquitetura Hexagonal, atua como um Adaptador Primário. Sua principal função
 * é apresentar um resumo claro do pedido ao usuário e, mediante confirmação,
 * invocar o serviço IReservaServico para efetivar a transação.
 */
public class TelaConfirmarPedido extends JPanel {
    // Contexto do pedido recebido via construtor
    private final Peca peca;
    private final Cliente cliente;
    private final List<Assento> assentos;
    private final Turno turnoSelecionado;

    // Serviços injetados
    private final IClienteServico clienteServico; // Para repassar na navegação
    private final IPecaServico pecaServico;       // Para repassar na navegação
    private final IReservaServico reservaServico; // Para efetivar a reserva

    /**
     * Construtor da TelaConfirmarPedido.
     *
     * @param peca A peça selecionada para a compra.
     * @param cliente O cliente que está realizando a compra.
     * @param assentos A lista de assentos selecionados pelo cliente.
     * @param turno O turno escolhido para a apresentação da peça.
     * @param clienteServico Serviço para operações de cliente.
     * @param pecaServico Serviço para operações de peça.
     * @param reservaServico Serviço para efetivar a reserva/criação do bilhete.
     * @throws IllegalArgumentException se algum dos parâmetros essenciais for nulo ou inválido.
     */
    public TelaConfirmarPedido(Peca peca, Cliente cliente, List<Assento> assentos, Turno turno,
                               IClienteServico clienteServico, IPecaServico pecaServico, IReservaServico reservaServico) {
        
        if (peca == null) throw new IllegalArgumentException("Peca não pode ser nula para TelaConfirmarPedido.");
        if (cliente == null) throw new IllegalArgumentException("Cliente não pode ser nulo para TelaConfirmarPedido.");
        if (assentos == null || assentos.isEmpty()) throw new IllegalArgumentException("Lista de assentos não pode ser nula ou vazia.");
        if (turno == null) throw new IllegalArgumentException("Turno não pode ser nulo.");
        if (clienteServico == null || pecaServico == null || reservaServico == null) {
            throw new IllegalArgumentException("Serviços não podem ser nulos.");
        }
        
        this.peca = peca;
        this.cliente = cliente;
        this.assentos = assentos; 
        this.turnoSelecionado = turno;
        this.clienteServico = clienteServico;
        this.pecaServico = pecaServico;
        this.reservaServico = reservaServico;
        
        configurarTelaVisual();
    }

    /**
     * Configura os componentes visuais e o layout da tela.
     */
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

    /**
     * Cria o painel que exibe o resumo do pedido para confirmação do usuário.
     * Os valores financeiros aqui são para *exibição*; o cálculo final e autoritativo
     * é sempre realizado pelo {@link IReservaServico} no backend.
     * @return JPanel com os detalhes do pedido.
     */
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

        adicionarLinhaDetalhe(painel, gbc, linhaAtual++, "Peça:", this.peca.getTitulo());
        adicionarLinhaDetalhe(painel, gbc, linhaAtual++, "Data:", FormatadorData.formatar(this.peca.getDataHora()));
        adicionarLinhaDetalhe(painel, gbc, linhaAtual++, "Turno:", this.turnoSelecionado.toString());
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

    /**
     * Delega a ação de finalização da compra para o serviço de reserva.
     * Trata as exceções de negócio (ex: assento indisponível) e de sistema,
     * exibindo feedback apropriado ao usuário.
     * Em caso de sucesso, navega para a tela principal.
     */
    private void processarConfirmacaoDaCompra() {
        try {
            Bilhete bilheteCriado = this.reservaServico.criarReserva(
                this.peca, this.cliente, this.assentos, this.turnoSelecionado
            );

            StringBuilder mensagem = new StringBuilder("<html><body style='width: 350px;'>");
            mensagem.append("<h2>Compra Realizada com Sucesso!</h2>");
            mensagem.append("<p><b>Peça:</b> ").append(bilheteCriado.getPeca().getTitulo()).append("</p>");
            mensagem.append("<p><b>Data:</b> ").append(FormatadorData.formatar(bilheteCriado.getPeca().getDataHora())).append("</p>");
            mensagem.append("<p><b>Turno:</b> ").append(bilheteCriado.getTurno().toString()).append("</p>");
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
            frame.setContentPane(new TelaPrincipal(this.clienteServico, this.pecaServico, this.reservaServico));
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

    /**
     * Navega de volta para a tela de seleção de assentos, permitindo ao usuário
     * alterar sua escolha.
     */
    private void navegarParaTelaAnterior() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaSelecionarAssento(this.peca, this.turnoSelecionado, this.pecaServico, this.clienteServico, this.reservaServico));
        frame.revalidate();
        frame.repaint();
    }
}