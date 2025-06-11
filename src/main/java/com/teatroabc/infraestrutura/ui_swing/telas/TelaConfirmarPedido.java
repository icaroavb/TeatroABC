package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAnimado;
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.componentes.PainelCodigoBarras; // Se for usar um componente separado
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.modelos.Bilhete;
import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.aplicacao.interfaces.IClienteServico; // Para repassar
import com.teatroabc.aplicacao.interfaces.IPecaServico;   // Para repassar
import com.teatroabc.aplicacao.interfaces.IReservaServico;
import com.teatroabc.aplicacao.excecoes.ReservaInvalidaException;
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorMoeda;
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorData; // Para exibir data da peça, se desejado

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tela responsável por exibir os detalhes finais de um pedido de compra de ingressos
 * e permitir que o usuário confirme a operação, resultando na criação de um Bilhete.
 * Atua como um Adaptador Primário, interagindo com o IReservaServico.
 */
public class TelaConfirmarPedido extends JPanel {
    // Dados do pedido recebidos via construtor
    private final Peca peca;
    private final Cliente cliente;
    private final List<Assento> assentos;
    private final Turno turnoSelecionado;

    // Serviços injetados
    private final IClienteServico clienteServico; // Para repassar ao voltar para TelaPrincipal
    private final IPecaServico pecaServico;       // Para repassar ao voltar para TelaPrincipal
    private final IReservaServico reservaServico; // Para efetivar a reserva

    /**
     * Construtor da TelaConfirmarPedido.
     *
     * @param peca A peça selecionada para a compra.
     * @param cliente O cliente que está realizando a compra.
     * @param assentos A lista de assentos selecionados pelo cliente.
     * @param turno O turno escolhido para a apresentação da peça.
     * @param clienteServico Serviço para operações de cliente (usado para repassar na navegação).
     * @param pecaServico Serviço para operações de peça (usado para repassar na navegação).
     * @param reservaServico Serviço para efetivar a reserva/criação do bilhete.
     * @throws IllegalArgumentException se algum dos parâmetros essenciais for nulo ou inválido.
     */
    public TelaConfirmarPedido(Peca peca, Cliente cliente, List<Assento> assentos, Turno turno,
                               IClienteServico clienteServico, IPecaServico pecaServico, IReservaServico reservaServico) {
        
        if (peca == null) throw new IllegalArgumentException("Peca não pode ser nula para TelaConfirmarPedido.");
        if (cliente == null) throw new IllegalArgumentException("Cliente não pode ser nulo para TelaConfirmarPedido.");
        if (assentos == null || assentos.isEmpty()) throw new IllegalArgumentException("Lista de assentos não pode ser nula ou vazia para TelaConfirmarPedido.");
        if (turno == null) throw new IllegalArgumentException("Turno não pode ser nulo para TelaConfirmarPedido.");
        if (clienteServico == null) throw new IllegalArgumentException("IClienteServico não pode ser nulo.");
        if (pecaServico == null) throw new IllegalArgumentException("IPecaServico não pode ser nulo.");
        if (reservaServico == null) throw new IllegalArgumentException("IReservaServico não pode ser nulo.");
        
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
        containerPrincipal.add(Box.createVerticalStrut(30)); // Espaçamento reduzido

        JLabel titulo = new JLabel("CONFIRMAR PEDIDO");
        titulo.setFont(Constantes.FONTE_TITULO.deriveFont(42f)); // Fonte ajustada
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        containerPrincipal.add(titulo);
        containerPrincipal.add(Box.createVerticalStrut(30)); // Espaçamento reduzido

        JPanel painelDetalhes = criarPainelDetalhesDoPedido(); // Nome mais descritivo
        painelDetalhes.setAlignmentX(Component.CENTER_ALIGNMENT); // Centraliza o painel de detalhes
        containerPrincipal.add(painelDetalhes);
        containerPrincipal.add(Box.createVerticalStrut(40));

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0)); // Espaçamento ajustado
        painelBotoes.setOpaque(false);
        BotaoAnimado btnVoltarUI = new BotaoAnimado("VOLTAR",
                Constantes.CINZA_ESCURO, Constantes.AZUL_CLARO.darker(), new Dimension(180, 55)); // Cores e tamanho ajustados
        btnVoltarUI.setFont(new Font("Arial", Font.BOLD, 18));
        btnVoltarUI.addActionListener(e -> navegarParaTelaAnterior());
        BotaoAnimado btnConfirmarUI = new BotaoAnimado("FINALIZAR COMPRA", // Texto mais claro
                Constantes.LARANJA, Constantes.AMARELO.darker(), new Dimension(260, 55)); // Tamanho ajustado
        btnConfirmarUI.setFont(new Font("Arial", Font.BOLD, 18));
        btnConfirmarUI.addActionListener(e -> processarConfirmacaoDaCompra());
        painelBotoes.add(btnVoltarUI);
        painelBotoes.add(btnConfirmarUI);
        containerPrincipal.add(painelBotoes);
        containerPrincipal.add(Box.createVerticalStrut(20)); // Espaçamento final

        // Envolve o container principal em um JScrollPane para o caso de conteúdo extenso
        JScrollPane scrollPane = new JScrollPane(containerPrincipal);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Cria o painel que exibe os detalhes do pedido para confirmação do usuário.
     * Calcula e exibe subtotal, desconto (se aplicável) e total com base nos dados atuais.
     * Estes valores são para *exibição*; o cálculo final é feito pelo {@link IReservaServico}.
     * @return JPanel com os detalhes do pedido.
     */
    private JPanel criarPainelDetalhesDoPedido() {
        JPanel painel = new JPanel();
        painel.setBackground(Constantes.CINZA_ESCURO); // Usando constante
        painel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Constantes.AZUL_CLARO.darker(), 1),
            BorderFactory.createEmptyBorder(20, 30, 20, 30))
        );
        painel.setLayout(new GridBagLayout());
        painel.setMaximumSize(new Dimension(650, 480)); // Ajustar conforme necessidade

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 15, 8, 15); // Padding ajustado
        int linhaAtual = 0; // Renomeado de 'linha'

        // Cálculos para exibição
        BigDecimal subtotalExibicao = calcularSubtotalParaExibicao();
        BigDecimal fatorDesconto = this.cliente.getPlanoFidelidade().getFatorDesconto();
        BigDecimal descontoExibicao = subtotalExibicao.multiply(fatorDesconto).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalExibicao = calcularTotalParaExibicao(subtotalExibicao, descontoExibicao);

        // Badge de Membro (se aplicável)
        if (this.cliente.isMembroGold()) {
            gbc.gridx = 0; gbc.gridy = linhaAtual++; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
            gbc.insets = new Insets(0, 0, 15, 0); // Espaçamento abaixo do badge
            painel.add(criarBadgeABCGoldVisual(), gbc);
            gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(8, 15, 8, 15); // Restaura insets padrão
        }

        // Detalhes do Pedido
        adicionarLinhaDetalhe(painel, gbc, linhaAtual++, "Peça:", this.peca.getTitulo());
        adicionarLinhaDetalhe(painel, gbc, linhaAtual++, "Data:", FormatadorData.formatar(this.peca.getDataHora()));
        adicionarLinhaDetalhe(painel, gbc, linhaAtual++, "Turno:", this.turnoSelecionado.toString());
        String assentosStr = this.assentos.stream().map(Assento::getCodigo).collect(Collectors.joining(", "));
        adicionarLinhaDetalhe(painel, gbc, linhaAtual++, "Assentos:", assentosStr);
        adicionarLinhaDetalhe(painel, gbc, linhaAtual++, "Cliente:", this.cliente.getNome());

        // Linha separadora antes dos valores
        gbc.gridx = 0; gbc.gridy = linhaAtual++; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 0, 10, 0); // Espaçamento para a linha
        painel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
        gbc.fill = GridBagConstraints.NONE; gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 15, 8, 15); // Restaura insets padrão

        // Valores Financeiros
        adicionarLinhaDetalhe(painel, gbc, linhaAtual++, "Subtotal:", FormatadorMoeda.formatar(subtotalExibicao));
        if (descontoExibicao.compareTo(BigDecimal.ZERO) > 0) {
            JLabel lblDescRotulo = criarLabelDetalhe("Desconto (" + this.cliente.getNomePlanoFidelidade() + "):");
            lblDescRotulo.setForeground(Constantes.AMARELO);
            JLabel lblDescValor = criarLabelValor("- " + FormatadorMoeda.formatar(descontoExibicao));
            lblDescValor.setForeground(Constantes.AMARELO);
            adicionarLinhaComponentes(painel, gbc, linhaAtual++, lblDescRotulo, lblDescValor);
        }

        JLabel lblTotalRotulo = criarLabelDetalhe("TOTAL A PAGAR:");
        lblTotalRotulo.setFont(new Font("Arial", Font.BOLD, 20)); // Fonte ajustada
        JLabel lblTotalValor = criarLabelValor(FormatadorMoeda.formatar(totalExibicao));
        lblTotalValor.setFont(new Font("Arial", Font.BOLD, 22)); // Fonte ajustada
        if (this.cliente.isMembroGold()) {
            lblTotalValor.setForeground(Constantes.AMARELO);
        }
        adicionarLinhaComponentes(painel, gbc, linhaAtual++, lblTotalRotulo, lblTotalValor);

        return painel;
    }
    
    private BigDecimal calcularSubtotalParaExibicao() { /* ... como antes ... */ return BigDecimal.ZERO; }
    private BigDecimal calcularTotalParaExibicao(BigDecimal subtotal, BigDecimal desconto) { /* ... como antes ... */ return BigDecimal.ZERO; }
    private void adicionarLinhaDetalhe(JPanel painel, GridBagConstraints gbc, int linha, String rotulo, String valor) { /* ... como antes ... */ }
    private void adicionarLinhaComponentes(JPanel painel, GridBagConstraints gbc, int linha, Component comp1, Component comp2) { /* ... como antes ... */ }
    private JLabel criarLabelDetalhe(String texto) { /* ... como antes ... */ return new JLabel(texto); }
    private JLabel criarLabelValor(String texto) { /* ... como antes ... */ return new JLabel(texto); }
    private JPanel criarBadgeABCGoldVisual() { /* ... como antes ... */ return new JPanel(); }

    /**
     * Processa a confirmação do pedido, chamando o serviço de reserva para criar o bilhete.
     * Exibe uma mensagem de sucesso ou erro ao usuário.
     * Em caso de sucesso, navega para a TelaPrincipal.
     */
    private void processarConfirmacaoDaCompra() { // Renomeado de processarConfirmacao
        try {
            Bilhete bilheteCriado = this.reservaServico.criarReserva(
                this.peca, this.cliente, this.assentos, this.turnoSelecionado
            );

            // Monta a mensagem de sucesso detalhada
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
     * Navega de volta para a tela de seleção de assentos, passando os dados e serviços necessários.
     */
    private void navegarParaTelaAnterior() { // Renomeado de voltarParaSelecaoAssentos
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        // TelaSelecionarAssento espera Peca, Turno e os 3 serviços
        frame.setContentPane(new TelaSelecionarAssento(this.peca, this.turnoSelecionado, this.pecaServico, this.clienteServico, this.reservaServico));
        frame.revalidate();
        frame.repaint();
    }
}