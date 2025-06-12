package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IPecaServico; // IMPORT DO NOVO COMPONENTE
import com.teatroabc.aplicacao.interfaces.IReservaServico;
import com.teatroabc.aplicacao.interfaces.ISessaoServico;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.dominio.modelos.Sessao;
import com.teatroabc.dominio.validadores.ValidadorCPF;
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.componentes.PainelEntradaCPF;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import javax.swing.*;

/**
 * Tela para o usuário informar o CPF. Atua como um roteador de fluxos.
 * A lógica do formulário de entrada foi encapsulada no componente {@link PainelEntradaCPF}.
 */
public class TelaInformarCPF extends JPanel {
    // Contexto de navegação
    private final boolean modoConsulta;
    private final Sessao sessao;
    private final List<Assento> assentosSelecionados;

    // Serviços injetados
    private final IClienteServico clienteServico;
    private final IPecaServico pecaServico;
    private final IReservaServico reservaServico;
    private final ISessaoServico sessaoServico;

    // A referência ao painel de entrada é mantida para que se possa obter o CPF dele.
    private PainelEntradaCPF painelEntradaCPF;

    /**
     * Construtor refatorado da TelaInformarCPF.
     *
     * @param modoConsulta {@code true} para consulta, {@code false} para compra.
     * @param sessao A sessão selecionada (relevante no modo compra, pode ser nulo em consulta).
     * @param assentosSelecionados Lista de assentos (relevante no modo compra).
     * @param clienteServico Serviço de cliente.
     * @param pecaServico Serviço de peça.
     * @param reservaServico Serviço de reserva.
     * @param sessaoServico Serviço de sessão.
     */
    public TelaInformarCPF(boolean modoConsulta, Sessao sessao, List<Assento> assentosSelecionados,
                           IClienteServico clienteServico, IPecaServico pecaServico, 
                           IReservaServico reservaServico, ISessaoServico sessaoServico) {
        if (clienteServico == null || pecaServico == null || reservaServico == null || sessaoServico == null) {
            throw new IllegalArgumentException("Serviços não podem ser nulos em TelaInformarCPF.");
        }
        
        this.modoConsulta = modoConsulta;
        this.sessao = sessao;
        this.assentosSelecionados = assentosSelecionados;
        this.clienteServico = clienteServico;
        this.pecaServico = pecaServico;
        this.reservaServico = reservaServico;
        this.sessaoServico = sessaoServico;
        
        configurarTelaVisual();
    }
    
    /**
     * Configura os componentes visuais e o layout da tela.
     * A lógica de criação do formulário foi movida para o componente PainelEntradaCPF.
     */
    private void configurarTelaVisual() {
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        JPanel containerPrincipal = new JPanel();
        containerPrincipal.setLayout(new BoxLayout(containerPrincipal, BoxLayout.Y_AXIS));
        containerPrincipal.setBackground(Constantes.AZUL_ESCURO);
        containerPrincipal.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        // Adiciona um espaçador no topo para empurrar o conteúdo para o centro vertical.
        containerPrincipal.add(Box.createVerticalGlue());

        containerPrincipal.add(new LogoTeatro());
        containerPrincipal.add(Box.createRigidArea(new Dimension(0, 60)));

        // Instancia o novo componente e passa a ação de "Continuar" como um lambda.
        String titulo = modoConsulta ? "CONSULTAR BILHETES" : "IDENTIFIQUE-SE COM CPF";
        this.painelEntradaCPF = new PainelEntradaCPF(titulo, e -> processarContinuar());
        containerPrincipal.add(this.painelEntradaCPF);

        // Adiciona outro espaçador para manter o conteúdo centralizado.
        containerPrincipal.add(Box.createVerticalGlue());
        
        add(containerPrincipal, BorderLayout.CENTER);

        // O botão de voltar permanece aqui, pois é parte do layout geral da TELA.
        JPanel painelVoltar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelVoltar.setOpaque(false);
        painelVoltar.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 0));
        JButton btnVoltarUI = new JButton("<< Voltar");
        btnVoltarUI.setFont(new Font("Arial", Font.PLAIN, 16));
        btnVoltarUI.setForeground(Constantes.AZUL_CLARO);
        btnVoltarUI.setContentAreaFilled(false);
        btnVoltarUI.setBorderPainted(false);
        btnVoltarUI.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltarUI.addActionListener(e -> navegarParaTelaAnterior());
        painelVoltar.add(btnVoltarUI);
        add(painelVoltar, BorderLayout.SOUTH);
    }
    
    /**
     * Processa a ação do botão "Continuar". Valida o CPF e navega para a próxima tela.
     */
    private void processarContinuar() {
        // O CPF agora é obtido diretamente do componente encapsulado.
        String cpfNormalizado = this.painelEntradaCPF.getCPF();

        if (!ValidadorCPF.isValid(cpfNormalizado)) {
            JOptionPane.showMessageDialog(this, "O CPF informado é inválido ou incompleto. Por favor, verifique.", "CPF Inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        try {
            if (modoConsulta) {
                frame.setContentPane(new TelaListaBilhetes(cpfNormalizado, reservaServico, clienteServico, pecaServico, sessaoServico));
            } else {
                Optional<Cliente> clienteOpt = this.clienteServico.buscarPorCpf(cpfNormalizado);
                if (clienteOpt.isPresent()) {
                    frame.setContentPane(new TelaConfirmarPedido(
                        clienteOpt.get(), this.sessao, this.assentosSelecionados,
                        this.clienteServico, this.pecaServico, this.reservaServico, this.sessaoServico
                    ));
                } else {
                    frame.setContentPane(new TelaCadastrar(
                        cpfNormalizado, this.sessao, this.assentosSelecionados,
                        this.clienteServico, this.pecaServico, this.reservaServico, this.sessaoServico
                    ));
                }
            }
            frame.revalidate();
            frame.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro ao processar sua solicitação: " + ex.getMessage(), "Erro de Sistema", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Navega para a tela anterior apropriada com base no modo atual.
     */
    private void navegarParaTelaAnterior() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (modoConsulta) {
            frame.setContentPane(new TelaPrincipal(this.clienteServico, this.pecaServico, this.reservaServico, this.sessaoServico));
        } else {
            // Volta para a tela de seleção de assentos, passando a sessão.
            frame.setContentPane(new TelaSelecionarAssento(
                this.sessao, this.pecaServico, this.clienteServico, this.reservaServico, this.sessaoServico
            ));
        }
        frame.revalidate();
        frame.repaint();
    }
}