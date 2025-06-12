package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.componentes.PainelAcoesConfirmacao;
import com.teatroabc.infraestrutura.ui_swing.componentes.PainelResumoPedido;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.modelos.Bilhete;
import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.dominio.modelos.Sessao;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico;
import com.teatroabc.aplicacao.interfaces.ISessaoServico;
import com.teatroabc.aplicacao.excecoes.ReservaInvalidaException;
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorMoeda;
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorData;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tela que orquestra a exibição do resumo do pedido e as ações de confirmação.
 * Delega a renderização dos detalhes e dos botões para componentes encapsulados.
 */
public class TelaConfirmarPedido extends JPanel {
    private final Cliente cliente;
    private final Sessao sessao;
    private final List<Assento> assentos;

    private final IClienteServico clienteServico;
    private final IPecaServico pecaServico;
    private final IReservaServico reservaServico;
    private final ISessaoServico sessaoServico;

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

    /**
     * Configura a estrutura principal da tela, utilizando os componentes encapsulados
     * para exibir o resumo do pedido e as ações.
     */
    private void configurarTelaVisual() {
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        JPanel containerPrincipal = new JPanel();
        containerPrincipal.setLayout(new BoxLayout(containerPrincipal, BoxLayout.Y_AXIS));
        containerPrincipal.setOpaque(false);
        containerPrincipal.setBorder(BorderFactory.createEmptyBorder(20,30,20,30));

        containerPrincipal.add(new LogoTeatro());
        containerPrincipal.add(Box.createVerticalStrut(30));

        JLabel titulo = new JLabel("CONFIRMAR PEDIDO");
        titulo.setFont(Constantes.FONTE_TITULO.deriveFont(42f));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        containerPrincipal.add(titulo);
        containerPrincipal.add(Box.createVerticalStrut(30));

        //Instancia o novo painel de resumo, passando os dados.
        PainelResumoPedido painelDetalhes = new PainelResumoPedido(this.cliente, this.sessao, this.assentos);
        painelDetalhes.setAlignmentX(Component.CENTER_ALIGNMENT);
        containerPrincipal.add(painelDetalhes);
        containerPrincipal.add(Box.createVerticalStrut(40));

        //Instancia o novo painel de ações, passando os métodos de navegação.
        PainelAcoesConfirmacao painelBotoes = new PainelAcoesConfirmacao(
            e -> navegarParaTelaAnterior(),
            e -> processarConfirmacaoDaCompra()
        );
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
     * Delega a ação de finalização da compra para o serviço de reserva.
     * Trata as exceções e exibe feedback apropriado ao usuário.
     */
    private void processarConfirmacaoDaCompra() {
        try {
            Bilhete bilheteCriado = this.reservaServico.criarReserva(
                this.sessao, this.cliente, this.assentos
            );

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

    /**
     * Navega de volta para a tela de seleção de assentos, permitindo ao usuário
     * alterar sua escolha.
     */
    private void navegarParaTelaAnterior() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaSelecionarAssento(
            this.sessao, this.pecaServico, this.clienteServico, this.reservaServico, this.sessaoServico
        ));
        frame.revalidate();
        frame.repaint();
    }
}