package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAnimado;
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.modelos.Sessao; // MUDANÇA: Agora usa Sessao
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico;
import com.teatroabc.aplicacao.interfaces.ISessaoServico; // MUDANÇA: Inclui o novo serviço
import com.teatroabc.dominio.validadores.ValidadorCPF;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

/**
 * Tela para o usuário informar o CPF. Atua como um roteador de fluxos.
 * Refatorada para trabalhar com o conceito de Sessao.
 */
public class TelaInformarCPF extends JPanel {
    // Contexto de navegação
    private final boolean modoConsulta;
    private final Sessao sessao; // MUDANÇA: Armazena o objeto Sessao
    private final List<Assento> assentosSelecionados;

    // Componente da UI
    private JFormattedTextField txtCPF;

    // Serviços injetados
    private final IClienteServico clienteServico;
    private final IPecaServico pecaServico;
    private final IReservaServico reservaServico;
    private final ISessaoServico sessaoServico;

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
     * @throws IllegalArgumentException se algum dos serviços for nulo.
     */
    public TelaInformarCPF(boolean modoConsulta, Sessao sessao, List<Assento> assentosSelecionados,
                           IClienteServico clienteServico, IPecaServico pecaServico, 
                           IReservaServico reservaServico, ISessaoServico sessaoServico) {
        if (clienteServico == null || pecaServico == null || reservaServico == null || sessaoServico == null) {
            throw new IllegalArgumentException("Serviços não podem ser nulos em TelaInformarCPF.");
        }
        
        this.modoConsulta = modoConsulta;
        this.sessao = sessao; // Armazena a Sessao
        this.assentosSelecionados = assentosSelecionados;
        this.clienteServico = clienteServico;
        this.pecaServico = pecaServico;
        this.reservaServico = reservaServico;
        this.sessaoServico = sessaoServico;
        
        configurarTelaVisual();
    }
    
    // O método setTurnoSelecionado() não é mais necessário, pois o turno já está dentro da Sessao.

    /**
     * Configura os componentes visuais e o layout da tela.
     */
    private void configurarTelaVisual() {
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        JPanel containerPrincipal = new JPanel();
        containerPrincipal.setLayout(new BoxLayout(containerPrincipal, BoxLayout.Y_AXIS));
        containerPrincipal.setBackground(Constantes.AZUL_ESCURO);
        containerPrincipal.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JPanel painelLogo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelLogo.setOpaque(false);
        painelLogo.add(new LogoTeatro());
        containerPrincipal.add(painelLogo);
        containerPrincipal.add(Box.createRigidArea(new Dimension(0, 60)));

        JLabel titulo = new JLabel(modoConsulta ? "CONSULTAR BILHETES" : "IDENTIFIQUE-SE COM CPF");
        titulo.setFont(Constantes.FONTE_TITULO.deriveFont(48f));
        titulo.setForeground(Constantes.AMARELO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        containerPrincipal.add(titulo);
        containerPrincipal.add(Box.createRigidArea(new Dimension(0, 40)));

        JPanel painelCPF = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelCPF.setOpaque(false);
        configurarCampoCPF();
        painelCPF.add(txtCPF);
        containerPrincipal.add(painelCPF);
        containerPrincipal.add(Box.createRigidArea(new Dimension(0, 40)));

        BotaoAnimado btnContinuar = new BotaoAnimado("CONTINUAR",
                Constantes.LARANJA, Constantes.AMARELO.darker(), new Dimension(380, 65));
        btnContinuar.setFont(Constantes.FONTE_BOTAO);
        btnContinuar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnContinuar.addActionListener(e -> processarContinuar());
        containerPrincipal.add(btnContinuar);

        add(containerPrincipal, BorderLayout.CENTER);

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
    
    private void configurarCampoCPF() {
        try {
            MaskFormatter maskCPF = new MaskFormatter("###.###.###-##");
            maskCPF.setPlaceholderCharacter('_');
            txtCPF = new JFormattedTextField(maskCPF);
        } catch (ParseException e) {
            txtCPF = new JFormattedTextField();
            System.err.println("Erro ao criar máscara de CPF: " + e.getMessage());
        }
        
        txtCPF.setPreferredSize(new Dimension(380, 55));
        // ... Estilo do campo permanece o mesmo ...
    }

    /**
     * Processa a ação do botão "Continuar".
     * A lógica de navegação agora usa o objeto Sessao.
     */
    private void processarContinuar() {
        String cpfNormalizado = ValidadorCPF.normalizar(txtCPF.getText());

        if (cpfNormalizado == null || !ValidadorCPF.isValid(cpfNormalizado)) {
            JOptionPane.showMessageDialog(this, "O CPF informado é inválido ou incompleto. Por favor, verifique.", "CPF Inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        try {
            if (modoConsulta) {
                frame.setContentPane(new TelaListaBilhetes(cpfNormalizado, reservaServico, clienteServico, pecaServico, sessaoServico));
            } else { // Modo compra
                Optional<Cliente> clienteOpt = this.clienteServico.buscarPorCpf(cpfNormalizado);
                if (clienteOpt.isPresent()) {
                    // Cliente existe, vai para a tela de confirmação
                    frame.setContentPane(new TelaConfirmarPedido(
                        clienteOpt.get(), this.sessao, this.assentosSelecionados,
                        this.clienteServico, this.pecaServico, this.reservaServico, this.sessaoServico
                    ));
                } else {
                    // Cliente não existe, vai para a tela de cadastro
                    frame.setContentPane(new TelaCadastrar(
                        cpfNormalizado, this.sessao, this.assentosSelecionados,
                        this.clienteServico, this.pecaServico, this.reservaServico, this.sessaoServico
                    ));
                }
            }
            frame.revalidate();
            frame.repaint();
        } catch (Exception ex) {
            // Tratamento de erro...
        }
    }

    /**
     * Navega para a tela anterior apropriada.
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