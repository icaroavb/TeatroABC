package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.infraestrutura.ui_swing.componentes.*;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.modelos.*;
import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.aplicacao.interfaces.IReservaServico; // Interface do Serviço
import com.teatroabc.aplicacao.excecoes.ReservaInvalidaException; // Exceção do Serviço
// Removido: import com.teatroabc.aplicacao.servicos.ReservaServico; // Não instanciar diretamente
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorMoeda;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal; // Usar BigDecimal para cálculos na UI também
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

public class TelaConfirmarPedido extends JPanel {
    private final Peca peca;
    private final Cliente cliente;
    private final List<Assento> assentos;
    private final Turno turnoSelecionado;

    // Serviço injetado
    private final IReservaServico reservaServico;

    // Constante de desconto ainda pode ser usada para EXIBIÇÃO do percentual,
    // mas o cálculo do valor do desconto virá do PlanoFidelidade do Cliente.
    private static final BigDecimal PERCENTUAL_DESCONTO_ABC_GOLD = new BigDecimal("0.05");

    /**
     * Construtor da TelaConfirmarPedido.
     * @param peca A peça selecionada.
     * @param cliente O cliente que está fazendo a compra.
     * @param assentos A lista de assentos selecionados.
     * @param turno O turno selecionado para a apresentação.
     * @param reservaServico O serviço para criar a reserva/bilhete.
     */
    public TelaConfirmarPedido(Peca peca, Cliente cliente, List<Assento> assentos, Turno turno,
                               IReservaServico reservaServico) {
        if (peca == null || cliente == null || assentos == null || assentos.isEmpty() || turno == null || reservaServico == null) {
            throw new IllegalArgumentException("Parâmetros inválidos para TelaConfirmarPedido.");
        }
        this.peca = peca;
        this.cliente = cliente;
        this.assentos = assentos; // Idealmente, receber uma cópia imutável
        this.turnoSelecionado = turno;
        this.reservaServico = reservaServico;
        // Removida instanciação: this.reservaServico = new ReservaServico();
        configurarTelaVisual();
    }

    private void configurarTelaVisual() { // Renomeado de configurarTela
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
        titulo.setFont(new Font("Arial", Font.BOLD, 48));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        containerPrincipal.add(Box.createVerticalStrut(40));
        containerPrincipal.add(titulo);

        JPanel painelDetalhes = criarPainelDetalhesPedido(); // Renomeado de criarPainelDetalhes
        containerPrincipal.add(Box.createVerticalStrut(40));
        containerPrincipal.add(painelDetalhes);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        painelBotoes.setBackground(Constantes.AZUL_ESCURO);
        BotaoAnimado btnVoltarUI = new BotaoAnimado("VOLTAR",
                Constantes.AZUL_CLARO, new Color(70, 130, 180), new Dimension(200, 60));
        btnVoltarUI.setFont(new Font("Arial", Font.BOLD, 24));
        btnVoltarUI.addActionListener(e -> voltarParaSelecaoAssentos()); // Renomeado de voltar
        BotaoAnimado btnConfirmarUI = new BotaoAnimado("CONFIRMAR",
                Constantes.LARANJA, Constantes.AMARELO, new Dimension(250, 60));
        btnConfirmarUI.setFont(new Font("Arial", Font.BOLD, 24));
        btnConfirmarUI.addActionListener(e -> processarConfirmacao()); // Renomeado de confirmar
        painelBotoes.add(btnVoltarUI);
        painelBotoes.add(btnConfirmarUI);
        containerPrincipal.add(Box.createVerticalStrut(60));
        containerPrincipal.add(painelBotoes);

        add(containerPrincipal, BorderLayout.CENTER);
    }

    private JPanel criarPainelDetalhesPedido() { // Renomeado de criarPainelDetalhes
        JPanel painel = new JPanel();
        painel.setBackground(new Color(52, 73, 94));
        painel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        painel.setLayout(new GridBagLayout());
        painel.setMaximumSize(new Dimension(700, 500)); // Ajustar conforme necessidade

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 20, 10, 20);
        int linha = 0;

        // Calcular subtotal para exibição
        BigDecimal subtotalExibicao = BigDecimal.ZERO;
        for (Assento assento : this.assentos) {
            subtotalExibicao = subtotalExibicao.add(assento.getPreco());
        }
        subtotalExibicao = subtotalExibicao.setScale(2, RoundingMode.HALF_UP);

        // Obter fator de desconto e calcular valor do desconto para exibição
        BigDecimal fatorDesconto = this.cliente.getPlanoFidelidade().getFatorDesconto();
        BigDecimal descontoExibicao = subtotalExibicao.multiply(fatorDesconto).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalExibicao = subtotalExibicao.subtract(descontoExibicao);
        if (totalExibicao.compareTo(BigDecimal.ZERO) < 0) {
            totalExibicao = BigDecimal.ZERO;
        }
        totalExibicao = totalExibicao.setScale(2, RoundingMode.HALF_UP);


        if (this.cliente.isMembroGold()) { // Usar o método de conveniência do Cliente
            gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
            painel.add(criarBadgeABCGold(), gbc); // Badge como antes
            linha++;
            gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        }

        // Peça
        gbc.gridx = 0; gbc.gridy = linha;
        painel.add(criarLabelDetalhe("Peça:"), gbc);
        gbc.gridx = 1;
        painel.add(criarLabelValor(this.peca.getTitulo()), gbc);
        linha++;

        // Turno
        gbc.gridx = 0; gbc.gridy = linha;
        painel.add(criarLabelDetalhe("Turno:"), gbc);
        gbc.gridx = 1;
        painel.add(criarLabelValor(this.turnoSelecionado.toString()), gbc);
        linha++;
        
        // Assentos
        gbc.gridx = 0; gbc.gridy = linha;
        painel.add(criarLabelDetalhe("Assentos:"), gbc);
        gbc.gridx = 1;
        String assentosStr = this.assentos.stream().map(Assento::getCodigo).collect(Collectors.joining(", "));
        painel.add(criarLabelValor(assentosStr), gbc);
        linha++;

        // Subtotal
        gbc.gridx = 0; gbc.gridy = linha;
        painel.add(criarLabelDetalhe("Subtotal:"), gbc);
        gbc.gridx = 1;
        JLabel lblSubtotalValor = criarLabelValor(FormatadorMoeda.formatar(subtotalExibicao)); // Usa BigDecimal
        painel.add(lblSubtotalValor, gbc);
        linha++;

        // Desconto (se aplicável)
        if (descontoExibicao.compareTo(BigDecimal.ZERO) > 0) {
            gbc.gridx = 0; gbc.gridy = linha;
            JLabel lblDescontoTitulo = criarLabelDetalhe("Desconto (" + this.cliente.getNomePlanoFidelidade() + "):");
            lblDescontoTitulo.setForeground(Constantes.AMARELO);
            painel.add(lblDescontoTitulo, gbc);
            gbc.gridx = 1;
            JLabel lblDescontoValor = criarLabelValor("- " + FormatadorMoeda.formatar(descontoExibicao)); // Usa BigDecimal
            lblDescontoValor.setForeground(Constantes.AMARELO);
            painel.add(lblDescontoValor, gbc);
            linha++;
        }

        // Linha separadora
        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        painel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
        linha++;
        gbc.fill = GridBagConstraints.NONE; gbc.gridwidth = 1;

        // Total
        gbc.gridx = 0; gbc.gridy = linha;
        JLabel lblTotalTitulo = criarLabelDetalhe("Total a Pagar:");
        lblTotalTitulo.setFont(new Font("Arial", Font.BOLD, 24)); // Destaque maior
        painel.add(lblTotalTitulo, gbc);
        gbc.gridx = 1;
        JLabel lblTotalValorExibicao = criarLabelValor(FormatadorMoeda.formatar(totalExibicao)); // Usa BigDecimal
        lblTotalValorExibicao.setFont(new Font("Arial", Font.BOLD, 28)); // Destaque maior
        if (this.cliente.isMembroGold()) {
            lblTotalValorExibicao.setForeground(Constantes.AMARELO);
        }
        painel.add(lblTotalValorExibicao, gbc);

        return painel;
    }

    private JLabel criarLabelDetalhe(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.PLAIN, 20));
        label.setForeground(Color.LIGHT_GRAY);
        return label;
    }

    private JLabel criarLabelValor(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setForeground(Color.WHITE);
        return label;
    }
    
    private JPanel criarBadgeABCGold() { // Renomeado de criarBadgeABC
        // ... (lógica do badge como antes) ...
        JPanel badge = new JPanel(); /*...*/ return badge;
    }

    private void processarConfirmacao() { // Renomeado de confirmar
        try {
            // Chama o serviço para criar a reserva/bilhete.
            // Os cálculos finais e a criação da entidade Bilhete são feitos pelo serviço.
            Bilhete bilheteCriado = this.reservaServico.criarReserva(
                this.peca,
                this.cliente,
                this.assentos, // A lista de Assentos selecionados
                this.turnoSelecionado
            );

            // Monta a mensagem de sucesso com base no bilhete retornado
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

            // Voltar para tela principal, passando os serviços
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setContentPane(new TelaPrincipal(this.clienteServico, this.pecaServico, this.reservaServico));
            frame.revalidate();
            frame.repaint();

        } catch (ReservaInvalidaException e) {
            JOptionPane.showMessageDialog(this, "Erro ao confirmar pedido: " + e.getMessage(), "Reserva Inválida", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Dados inválidos para reserva: " + e.getMessage(), "Erro de Dados", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) { // Captura genérica para outros erros inesperados
            JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado: " + e.getMessage(), "Erro Crítico", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Logar para debug
        }
    }

    private void voltarParaSelecaoAssentos() { // Renomeado de voltar
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        // TelaSelecionarAssento precisa de Peca e dos serviços
        frame.setContentPane(new TelaSelecionarAssento(this.peca, this.pecaServico, this.clienteServico, this.reservaServico));
        frame.revalidate();
        frame.repaint();
    }
}