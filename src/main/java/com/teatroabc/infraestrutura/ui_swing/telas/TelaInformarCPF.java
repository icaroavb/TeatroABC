package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAnimado;
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico;
import com.teatroabc.dominio.validadores.ValidadorCPF;
import com.teatroabc.aplicacao.excecoes.ClienteJaCadastradoException; // Embora não tratada diretamente aqui, o serviço que ela chama pode lançar

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.FocusAdapter; // Import para FocusAdapter
import java.awt.event.FocusEvent;   // Import para FocusEvent
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

/**
 * Tela para o usuário informar o CPF.
 * Utilizada tanto no fluxo de compra de ingressos (para identificar o cliente ou direcionar para cadastro)
 * quanto no fluxo de consulta de bilhetes.
 * Atua como um Adaptador Primário, interagindo com IClienteServico e orquestrando a navegação.
 */
public class TelaInformarCPF extends JPanel {
    private final boolean modoConsulta;
    private final Peca peca;
    private final List<Assento> assentosSelecionados;
    private Turno turnoSelecionado; // Não é final para permitir setTurnoSelecionado

    private JFormattedTextField txtCPF;

    // Serviços injetados via construtor
    private final IClienteServico clienteServico;
    private final IPecaServico pecaServico;
    private final IReservaServico reservaServico;

    /**
     * Construtor da TelaInformarCPF.
     *
     * @param modoConsulta {@code true} se a tela for para consulta de bilhetes,
     *                     {@code false} se for parte do fluxo de compra de ingressos.
     * @param peca A peça selecionada (relevante no modo compra, pode ser {@code null} em modo consulta).
     * @param assentosSelecionados Lista de assentos selecionados (relevante no modo compra, pode ser {@code null} em modo consulta).
     * @param clienteServico Serviço para operações de cliente. Não pode ser nulo.
     * @param pecaServico Serviço para operações de peça (para repassar na navegação). Não pode ser nulo.
     * @param reservaServico Serviço para operações de reserva/bilhete. Não pode ser nulo.
     * @throws IllegalArgumentException se algum dos serviços injetados for nulo.
     */
    public TelaInformarCPF(boolean modoConsulta, Peca peca, List<Assento> assentosSelecionados,
                           IClienteServico clienteServico, IPecaServico pecaServico, IReservaServico reservaServico) {
        if (clienteServico == null) throw new IllegalArgumentException("IClienteServico não pode ser nulo.");
        if (pecaServico == null) throw new IllegalArgumentException("IPecaServico não pode ser nulo.");
        if (reservaServico == null) throw new IllegalArgumentException("IReservaServico não pode ser nulo.");
        
        this.modoConsulta = modoConsulta;
        this.peca = peca;
        this.assentosSelecionados = assentosSelecionados;
        this.clienteServico = clienteServico;
        this.pecaServico = pecaServico;
        this.reservaServico = reservaServico;
        
        configurarTelaVisual();
    }
    
    /**
     * Define o turno selecionado pelo usuário na tela anterior.
     * Este método é chamado externamente (pela {@code TelaSelecionarAssento} ou {@code TelaSelecionarTurno})
     * quando esta tela é parte do fluxo de compra.
     *
     * @param turno O turno da apresentação selecionado.
     */
    public void setTurnoSelecionado(Turno turno) {
        if (!this.modoConsulta && turno == null && (this.peca != null || (this.assentosSelecionados != null && !this.assentosSelecionados.isEmpty()))) {
            System.err.println("Aviso: Turno nulo definido para TelaInformarCPF em modo de compra com dados de pedido existentes.");
        }
        this.turnoSelecionado = turno;
    }

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
        try {
            MaskFormatter maskCPF = new MaskFormatter("###.###.###-##");
            maskCPF.setPlaceholderCharacter('_');
            txtCPF = new JFormattedTextField(maskCPF);
        } catch (ParseException e) {
            txtCPF = new JFormattedTextField();
            System.err.println("Erro crítico ao criar máscara de CPF: " + e.getMessage());
        }
        
        txtCPF.setPreferredSize(new Dimension(380, 55));
        txtCPF.setFont(new Font("Arial", Font.BOLD, 22));
        txtCPF.setHorizontalAlignment(JTextField.CENTER);
        txtCPF.setBackground(new Color(52, 73, 94));
        txtCPF.setForeground(Color.GRAY); // Inicia com cor de placeholder
        txtCPF.setCaretColor(Constantes.AMARELO);
        txtCPF.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constantes.AZUL_CLARO, 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        // Placeholder e comportamento de foco
        final String placeholderCPF = "___.___.___-__";
        txtCPF.setText(placeholderCPF); // Define o texto inicial do placeholder

        FocusAdapter cpfFocusAdapter = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                if (txtCPF.getText().equals(placeholderCPF)) {
                    txtCPF.setValue(null); // Limpa o valor interno da máscara
                    txtCPF.setText("");    // Limpa o texto visualmente
                    txtCPF.setForeground(Color.WHITE);
                }
            }

            @Override
            public void focusLost(FocusEvent evt) {
                // Verifica se o conteúdo útil (sem máscara) está vazio
                String content = "";
                if (txtCPF.getValue() != null) { // JFormattedTextField com máscara retorna o valor sem formatação
                    content = String.valueOf(txtCPF.getValue()).trim();
                } else { // Se getValue for null, pega o texto e remove placeholders
                    content = txtCPF.getText().replaceAll("[_.-]", "").trim();
                }

                if (content.isEmpty()) {
                    txtCPF.setValue(null); // Garante que o valor interno seja nulo
                    txtCPF.setText(placeholderCPF);
                    txtCPF.setForeground(Color.GRAY);
                } else {
                    txtCPF.setForeground(Color.WHITE); // Garante cor normal se houver conteúdo
                }
            }
        };
        txtCPF.addFocusListener(cpfFocusAdapter);

        // Chama focusLost programaticamente para garantir que o placeholder apareça se o campo começar vazio e não focado.
        SwingUtilities.invokeLater(() -> {
            // Simula um evento de perda de foco para o listener que acabamos de adicionar
            FocusEvent focusLostEventSimulado = new FocusEvent(txtCPF, FocusEvent.FOCUS_LOST);
            cpfFocusAdapter.focusLost(focusLostEventSimulado);
        });

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
        btnVoltarUI.setOpaque(false);
        btnVoltarUI.setContentAreaFilled(false);
        btnVoltarUI.setBorderPainted(false);
        btnVoltarUI.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltarUI.addActionListener(e -> navegarParaTelaAnterior());
        painelVoltar.add(btnVoltarUI);
        add(painelVoltar, BorderLayout.SOUTH);
    }

    /**
     * Processa a ação do botão "Continuar".
     * Valida o CPF e navega para a próxima tela apropriada com base no modo (compra ou consulta)
     * e na existência do cliente.
     */
    private void processarContinuar() {
        String cpfInput = txtCPF.getText();
        // Se o campo ainda estiver com o placeholder, considera como vazio
        if (cpfInput.equals("___.___.___-__")) {
            cpfInput = "";
        }
        String cpfNormalizado = ValidadorCPF.normalizar(cpfInput);

        if (cpfNormalizado == null || cpfNormalizado.length() != 11) {
            JOptionPane.showMessageDialog(this, "Por favor, informe um CPF completo com 11 dígitos.", "CPF Incompleto", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!ValidadorCPF.isValid(cpfNormalizado)) {
            JOptionPane.showMessageDialog(this, "O CPF informado é inválido. Por favor, verifique os dados.", "CPF Inválido", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        try {
            if (modoConsulta) {
                frame.setContentPane(new TelaListaBilhetes(cpfNormalizado, this.reservaServico, this.clienteServico, this.pecaServico));
            } else { // Modo compra
                if (this.turnoSelecionado == null && this.peca != null) {
                     JOptionPane.showMessageDialog(this, "Erro interno: Turno não selecionado para a compra.", "Erro de Fluxo", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Optional<Cliente> clienteOpt = this.clienteServico.buscarPorCpf(cpfNormalizado);
                if (clienteOpt.isPresent()) {
                    Cliente clienteExistente = clienteOpt.get();
                    frame.setContentPane(new TelaConfirmarPedido(
                        this.peca, clienteExistente, this.assentosSelecionados, this.turnoSelecionado,
                        this.clienteServico, this.pecaServico, this.reservaServico
                    ));
                } else {
                    TelaCadastrar telaCadastrar = new TelaCadastrar(
                        cpfNormalizado, this.peca, this.assentosSelecionados,
                        this.clienteServico, this.pecaServico, this.reservaServico
                    );
                    telaCadastrar.setTurnoSelecionado(this.turnoSelecionado);
                    frame.setContentPane(telaCadastrar);
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
     * Navega para a tela anterior apropriada com base no modo atual (compra ou consulta).
     */
    private void navegarParaTelaAnterior() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (modoConsulta) {
            frame.setContentPane(new TelaPrincipal(this.clienteServico, this.pecaServico, this.reservaServico));
        } else {
            // Se estiver no fluxo de compra, volta para TelaSelecionarAssento
            // É crucial que 'this.peca' e 'this.turnoSelecionado' estejam corretos
            // (turnoSelecionado é setado por TelaSelecionarAssento antes de chamar esta tela)
            frame.setContentPane(new TelaSelecionarAssento(this.peca, this.turnoSelecionado, this.pecaServico, this.clienteServico, this.reservaServico));
        }
        frame.revalidate();
        frame.repaint();
    }
}