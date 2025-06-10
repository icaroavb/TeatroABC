package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.infraestrutura.ui_swing.componentes.*;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.modelos.*;
import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico;
import com.teatroabc.aplicacao.excecoes.ReservaInvalidaException;
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorMoeda;
// FormatadorData não é usado diretamente aqui, mas poderia ser se a data da peça fosse exibida
// import com.teatroabc.infraestrutura.ui_swing.util.FormatadorData;


import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tela responsável por exibir os detalhes de um pedido de compra de ingressos
 * e permitir que o usuário confirme ou cancele a operação.
 * Atua como um Adaptador Primário na arquitetura hexagonal, interagindo com
 * o serviço de reserva para finalizar a compra.
 */
public class TelaConfirmarPedido extends JPanel {
    private final Peca peca;
    private final Cliente cliente;
    private final List<Assento> assentos;
    private final Turno turnoSelecionado;

    // Serviços injetados
    private final IClienteServico clienteServico;
    private final IPecaServico pecaServico;
    private final IReservaServico reservaServico;

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
     * @throws IllegalArgumentException se algum dos parâmetros essenciais (não nulos) for inválido.
     */
    public TelaConfirmarPedido(Peca peca, Cliente cliente, List<Assento> assentos, Turno turno,
                               IClienteServico clienteServico, IPecaServico pecaServico, IReservaServico reservaServico) {
        if (peca == null) throw new IllegalArgumentException("Peca não pode ser nula para TelaConfirmarPedido.");
        if (cliente == null) throw new IllegalArgumentException("Cliente não pode ser nulo para TelaConfirmarPedido.");
        if (assentos == null || assentos.isEmpty()) throw new IllegalArgumentException("Lista de assentos não pode ser nula ou vazia para TelaConfirmarPedido.");
        if (turno == null) throw new IllegalArgumentException("Turno não pode ser nulo para TelaConfirmarPedido.");
        if (clienteServico == null) throw new IllegalArgumentException("IClienteServico não pode ser nulo para TelaConfirmarPedido.");
        if (pecaServico == null) throw new IllegalArgumentException("IPecaServico não pode ser nulo para TelaConfirmarPedido.");
        if (reservaServico == null) throw new IllegalArgumentException("IReservaServico não pode ser nulo para TelaConfirmarPedido.");
        
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
     * Configura os componentes visuais da tela.
     */
    private void configurarTelaVisual() {
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        JPanel containerPrincipal = new JPanel();
        containerPrincipal.setLayout(new BoxLayout(containerPrincipal, BoxLayout.Y_AXIS));
        containerPrincipal.setBackground(Constantes.AZUL_ESCURO);

        JPanel painelLogo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelLogo.setBackground(Constantes.AZUL_ESCURO);
        painelLogo.add(new LogoTeatro());
        containerPrincipal.add(Box.createVerticalStrut(50));
        containerPrincipal.add(painelLogo);

        JLabel titulo = new JLabel("CONFIRMAR PEDIDO");
        titulo.setFont(new Font("Arial", Font.BOLD, 48)); // Fonte ajustada
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        containerPrincipal.add(Box.createVerticalStrut(40));
        containerPrincipal.add(titulo);

        JPanel painelDetalhes = criarPainelDetalhesPedido();
        containerPrincipal.add(Box.createVerticalStrut(40));
        containerPrincipal.add(painelDetalhes);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        painelBotoes.setBackground(Constantes.AZUL_ESCURO);
        BotaoAnimado btnVoltarUI = new BotaoAnimado("VOLTAR",
                Constantes.AZUL_CLARO, new Color(70, 130, 180), new Dimension(200, 60));
        btnVoltarUI.setFont(new Font("Arial", Font.BOLD, 24));
        btnVoltarUI.addActionListener(e -> voltarParaSelecaoAssentos());
        BotaoAnimado btnConfirmarUI = new BotaoAnimado("CONFIRMAR",
                Constantes.LARANJA, Constantes.AMARELO, new Dimension(250, 60));
        btnConfirmarUI.setFont(new Font("Arial", Font.BOLD, 24));
        btnConfirmarUI.addActionListener(e -> processarConfirmacao());
        painelBotoes.add(btnVoltarUI);
        painelBotoes.add(btnConfirmarUI);
        containerPrincipal.add(Box.createVerticalStrut(60));
        containerPrincipal.add(painelBotoes);

        add(containerPrincipal, BorderLayout.CENTER);
    }

    /**
     * Cria o painel que exibe os detalhes do pedido para confirmação do usuário.
     * Calcula e exibe subtotal, desconto (se aplicável) e total com base nos dados atuais.
     * @return JPanel com os detalhes do pedido.
     */
    private JPanel criarPainelDetalhesPedido() {
        JPanel painel = new JPanel();
        painel.setBackground(new Color(52, 73, 94));
        painel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        painel.setLayout(new GridBagLayout());
        painel.setMaximumSize(new Dimension(700, 550)); // Aumentada a altura máxima

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 20, 10, 20);
        int linha = 0;

        BigDecimal subtotalExibicao = calcularSubtotalParaExibicao();
        BigDecimal fatorDesconto = this.cliente.getPlanoFidelidade().getFatorDesconto();
        BigDecimal descontoExibicao = subtotalExibicao.multiply(fatorDesconto).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalExibicao = calcularTotalParaExibicao(subtotalExibicao, descontoExibicao);

        if (this.cliente.isMembroGold()) {
            gbc.gridx = 0; gbc.gridy = linha++; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
            painel.add(criarBadgeABCGold(), gbc);
            gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        }

        adicionarLinhaDetalhe(painel, gbc, linha++, "Peça:", this.peca.getTitulo());
        adicionarLinhaDetalhe(painel, gbc, linha++, "Turno:", this.turnoSelecionado.toString());
        String assentosStr = this.assentos.stream().map(Assento::getCodigo).collect(Collectors.joining(", "));
        adicionarLinhaDetalhe(painel, gbc, linha++, "Assentos:", assentosStr);
        adicionarLinhaDetalhe(painel, gbc, linha++, "Subtotal:", FormatadorMoeda.formatar(subtotalExibicao));

        if (descontoExibicao.compareTo(BigDecimal.ZERO) > 0) {
            JLabel lblDescontoTitulo = criarLabelDetalhe("Desconto (" + this.cliente.getNomePlanoFidelidade() + "):");
            lblDescontoTitulo.setForeground(Constantes.AMARELO);
            JLabel lblDescontoValor = criarLabelValor("- " + FormatadorMoeda.formatar(descontoExibicao));
            lblDescontoValor.setForeground(Constantes.AMARELO);
            adicionarLinhaComponentes(painel, gbc, linha++, lblDescontoTitulo, lblDescontoValor);
        }

        gbc.gridx = 0; gbc.gridy = linha++; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        painel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
        gbc.fill = GridBagConstraints.NONE; gbc.gridwidth = 1;

        JLabel lblTotalTitulo = criarLabelDetalhe("Total a Pagar:");
        lblTotalTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        JLabel lblTotalValorExibicao = criarLabelValor(FormatadorMoeda.formatar(totalExibicao));
        lblTotalValorExibicao.setFont(new Font("Arial", Font.BOLD, 28));
        if (this.cliente.isMembroGold()) {
            lblTotalValorExibicao.setForeground(Constantes.AMARELO);
        }
        adicionarLinhaComponentes(painel, gbc, linha++, lblTotalTitulo, lblTotalValorExibicao);

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

    private JLabel criarLabelDetalhe(String texto) { /* ... como antes ... */ return new JLabel(texto);}
    private JLabel criarLabelValor(String texto) { /* ... como antes ... */ return new JLabel(texto);}
    private JPanel criarBadgeABCGold() { /* ... como antes ... */ JPanel badge = new JPanel(); badge.add(new JLabel("MEMBRO GOLD")); return badge;}

    /**
     * Processa a confirmação do pedido, chamando o serviço de reserva para criar o bilhete.
     * Exibe uma mensagem de sucesso ou erro ao usuário.
     * Em caso de sucesso, navega para a TelaPrincipal.
     */
    private void processarConfirmacao() {
        try {
            Bilhete bilheteCriado = this.reservaServico.criarReserva(
                this.peca,
                this.cliente,
                this.assentos,
                this.turnoSelecionado
            );

            StringBuilder mensagem = new StringBuilder("Compra realizada com sucesso!\n");
            mensagem.append("Código do Bilhete: ").append(bilheteCriado.getCodigoBarras()).append("\n");
            mensagem.append("Peça: ").append(bilheteCriado.getPeca().getTitulo()).append("\n");
            mensagem.append("Turno: ").append(bilheteCriado.getTurno().toString()).append("\n");
            mensagem.append("Cliente: ").append(bilheteCriado.getCliente().getNome()).append("\n");
            String assentosStr = bilheteCriado.getAssentos().stream().map(Assento::getCodigo).collect(Collectors.joining(", "));
            mensagem.append("Assentos: ").append(assentosStr).append("\n");
            mensagem.append("Subtotal: ").append(FormatadorMoeda.formatar(bilheteCriado.getSubtotal())).append("\n");
            if (bilheteCriado.getValorDesconto().compareTo(BigDecimal.ZERO) > 0) {
                mensagem.append("Desconto (").append(bilheteCriado.getCliente().getNomePlanoFidelidade()).append("): -")
                        .append(FormatadorMoeda.formatar(bilheteCriado.getValorDesconto())).append("\n");
            }
            mensagem.append("VALOR TOTAL PAGO: ").append(FormatadorMoeda.formatar(bilheteCriado.getValorTotal()));

            JOptionPane.showMessageDialog(this, mensagem.toString(), "Sucesso na Compra", JOptionPane.INFORMATION_MESSAGE);

            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setContentPane(new TelaPrincipal(this.clienteServico, this.pecaServico, this.reservaServico));
            frame.revalidate();
            frame.repaint();

        } catch (ReservaInvalidaException e) {
            JOptionPane.showMessageDialog(this, "Erro ao confirmar pedido: " + e.getMessage(), "Reserva Inválida", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Dados inválidos para reserva: " + e.getMessage(), "Erro de Dados", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado: " + e.getMessage(), "Erro Crítico", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Navega de volta para a tela de seleção de assentos.
     */
    private void voltarParaSelecaoAssentos() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaSelecionarAssento(this.peca, this.pecaServico, this.clienteServico, this.reservaServico));
        frame.revalidate();
        frame.repaint();
    }
}