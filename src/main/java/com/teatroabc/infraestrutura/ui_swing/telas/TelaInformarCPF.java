package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.infraestrutura.ui_swing.componentes.BotaoAnimado;
import com.teatroabc.infraestrutura.ui_swing.componentes.LogoTeatro;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IPecaServico; // ADICIONADO IMPORT
import com.teatroabc.aplicacao.interfaces.IReservaServico;
import com.teatroabc.dominio.validadores.ValidadorCPF;
import com.teatroabc.aplicacao.excecoes.ClienteJaCadastradoException; // Para tratar no futuro se necessário

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

public class TelaInformarCPF extends JPanel {
    private final boolean modoConsulta;
    private final Peca peca;
    private final List<Assento> assentosSelecionados;
    private Turno turnoSelecionado; // REMOVIDO FINAL para permitir setTurnoSelecionado

    private JFormattedTextField txtCPF;

    // Serviços injetados
    private final IClienteServico clienteServico;
    private final IPecaServico pecaServico;       // ADICIONADO CAMPO
    private final IReservaServico reservaServico;

    /**
     * Construtor para TelaInformarCPF.
     * @param modoConsulta True para consulta, false para compra.
     * @param peca Peça selecionada (null em modo consulta).
     * @param assentosSelecionados Lista de assentos (null em modo consulta).
     * @param clienteServico Serviço de cliente.
     * @param pecaServico Serviço de peça (para repassar).
     * @param reservaServico Serviço de reserva/bilhete.
     */
    public TelaInformarCPF(boolean modoConsulta, Peca peca, List<Assento> assentosSelecionados,
                           IClienteServico clienteServico, IPecaServico pecaServico, IReservaServico reservaServico) { // ADICIONADO IPecaServico
        if (clienteServico == null || pecaServico == null || reservaServico == null) {
            throw new IllegalArgumentException("Serviços não podem ser nulos em TelaInformarCPF.");
        }
        this.modoConsulta = modoConsulta;
        this.peca = peca;
        this.assentosSelecionados = assentosSelecionados;
        // this.turnoSelecionado é inicializado por setTurnoSelecionado
        this.clienteServico = clienteServico;
        this.pecaServico = pecaServico; // ATRIBUIR
        this.reservaServico = reservaServico;
        
        configurarTelaVisual();
    }
    
    public void setTurnoSelecionado(Turno turno) {
        if (!this.modoConsulta && turno == null && (this.peca != null || this.assentosSelecionados != null)) {
             // Só emitir aviso se estiver claramente no fluxo de compra com dados de peça/assento
            System.err.println("Aviso: Turno nulo definido para TelaInformarCPF em modo de compra com dados de pedido.");
        }
        this.turnoSelecionado = turno;
    }

    private void configurarTelaVisual() {
        // ... (código da UI como antes, sem mudanças aqui) ...
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
        containerPrincipal.add(Box.createVerticalStrut(80));
        JLabel titulo = new JLabel("INFORME O CPF");
        titulo.setFont(Constantes.FONTE_TITULO);
        titulo.setForeground(Constantes.AMARELO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        containerPrincipal.add(titulo);
        containerPrincipal.add(Box.createVerticalStrut(50));
        JPanel painelCPF = new JPanel();
        painelCPF.setBackground(Constantes.AZUL_ESCURO);
        painelCPF.setLayout(new FlowLayout(FlowLayout.CENTER));
        try {
            MaskFormatter maskCPF = new MaskFormatter("###.###.###-##");
            maskCPF.setPlaceholderCharacter('_');
            txtCPF = new JFormattedTextField(maskCPF);
        } catch (ParseException e) {
            txtCPF = new JFormattedTextField(); 
            System.err.println("Erro ao criar máscara de CPF: " + e.getMessage());
        }
        txtCPF.setPreferredSize(new Dimension(400, 50));
        txtCPF.setFont(new Font("Arial", Font.PLAIN, 24));
        txtCPF.setHorizontalAlignment(JTextField.CENTER);
        txtCPF.setBackground(new Color(52, 73, 94));
        txtCPF.setForeground(Color.WHITE);
        txtCPF.setCaretColor(Color.WHITE);
        txtCPF.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constantes.AZUL_CLARO, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtCPF.setText("___.___.___-__"); 
        txtCPF.setForeground(Color.GRAY);
        txtCPF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtCPF.getText().replace("_", "").replace(".", "").replace("-", "").isEmpty()) {
                    txtCPF.setText("");
                    txtCPF.setForeground(Color.WHITE);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtCPF.getText().replace("_", "").replace(".", "").replace("-", "").trim().isEmpty()) {
                    txtCPF.setText("___.___.___-__");
                    txtCPF.setForeground(Color.GRAY);
                }
            }
        });
        painelCPF.add(txtCPF);
        containerPrincipal.add(painelCPF);
        containerPrincipal.add(Box.createVerticalStrut(50));


        BotaoAnimado btnContinuar = new BotaoAnimado("CONTINUAR",
                Constantes.LARANJA, Constantes.AMARELO, new Dimension(400, 70));
        btnContinuar.setFont(new Font("Arial", Font.BOLD, 28));
        btnContinuar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnContinuar.addActionListener(e -> continuar());
        containerPrincipal.add(btnContinuar);

        add(containerPrincipal, BorderLayout.CENTER);

        JPanel painelVoltar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelVoltar.setBackground(Constantes.AZUL_ESCURO);
        JButton btnVoltar = new JButton("VOLTAR");
        btnVoltar.setFont(new Font("Arial", Font.PLAIN, 18));
        btnVoltar.setForeground(Constantes.AZUL_CLARO);
        btnVoltar.setBackground(Constantes.AZUL_ESCURO);
        btnVoltar.setBorderPainted(false);
        btnVoltar.setFocusPainted(false);
        btnVoltar.setContentAreaFilled(false);
        btnVoltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltar.addActionListener(e -> voltar());
        painelVoltar.add(btnVoltar);
        add(painelVoltar, BorderLayout.SOUTH);
    }

    private void continuar() {
        String cpfInput = txtCPF.getText();
        String cpfNormalizado = ValidadorCPF.normalizar(cpfInput);

        if (cpfNormalizado == null || cpfNormalizado.length() != 11) {
            JOptionPane.showMessageDialog(this, "Por favor, informe um CPF com 11 dígitos!", "CPF Inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!ValidadorCPF.isValid(cpfNormalizado)) {
            JOptionPane.showMessageDialog(this, "CPF inválido! Por favor, verifique os dados.", "CPF Inválido", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        if (modoConsulta) {
            // TelaListaBilhetes precisa do cpf, reservaServico, e os outros dois para poder voltar para TelaPrincipal
            frame.setContentPane(new TelaListaBilhetes(cpfNormalizado, this.reservaServico, this.clienteServico, this.pecaServico));
        } else { // Modo compra
            Optional<Cliente> clienteOpt = this.clienteServico.buscarPorCpf(cpfNormalizado);
            if (clienteOpt.isPresent()) {
                Cliente clienteExistente = clienteOpt.get();
                // TelaConfirmarPedido precisa de Peca, Cliente, List<Assento>, Turno, e todos os serviços
                // (embora só use reservaServico diretamente, os outros são para repassar)
                TelaConfirmarPedido telaConfirmar = new TelaConfirmarPedido(
                    this.peca,
                    clienteExistente,
                    this.assentosSelecionados,
                    this.turnoSelecionado,
                    this.clienteServico, // Passando para poder voltar para TelaPrincipal
                    this.pecaServico,    // Passando para poder voltar para TelaPrincipal
                    this.reservaServico
                );
                frame.setContentPane(telaConfirmar);
            } else {
                // TelaCadastrar precisa do cpf, dados da compra (Peca, Assentos), e todos os serviços
                TelaCadastrar telaCadastrar = new TelaCadastrar(
                    cpfNormalizado,
                    this.peca,
                    this.assentosSelecionados,
                    this.clienteServico,
                    this.pecaServico,
                    this.reservaServico
                );
                telaCadastrar.setTurnoSelecionado(this.turnoSelecionado);
                frame.setContentPane(telaCadastrar);
            }
        }
        frame.revalidate();
        frame.repaint();
    }

    private void voltar() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (modoConsulta) {
            frame.setContentPane(new TelaPrincipal(this.clienteServico, this.pecaServico, this.reservaServico));
        } else {
            // TelaSelecionarAssento espera Peca e os 3 serviços
            frame.setContentPane(new TelaSelecionarAssento(this.peca, this.pecaServico, this.clienteServico, this.reservaServico));
        }
        frame.revalidate();
        frame.repaint();
    }
}