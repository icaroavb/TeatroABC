package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.infraestrutura.ui_swing.componentes.*;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.aplicacao.dto.DadosCadastroClienteDTO; // Para enviar ao serviço
import com.teatroabc.aplicacao.excecoes.ClienteJaCadastradoException;
import com.teatroabc.aplicacao.interfaces.IClienteServico; // Interface do Serviço
import com.teatroabc.aplicacao.interfaces.IPecaServico;   // Interface do Serviço (para passar adiante)
import com.teatroabc.aplicacao.interfaces.IReservaServico; // Interface do Serviço (para passar adiante)
// Removido: import com.teatroabc.aplicacao.servicos.ClienteServico; // Não instanciar diretamente

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TelaCadastrar extends JPanel {
    // Dados da compra em andamento (podem ser null se for cadastro avulso)
    private final String cpfInformadoOriginalmente; // CPF que veio da tela anterior (pode já estar formatado)
    private final Peca peca;
    private final List<Assento> assentosSelecionados;
    private Turno turnoSelecionado; // Pode ser setado após construção

    // Campos da UI
    private JFormattedTextField txtCPF;
    private JTextField txtNome;
    private JFormattedTextField txtDataNascimento;
    private JFormattedTextField txtTelefone;
    private JTextField txtEmail;
    private JCheckBox chkMembroABC;
    private JPanel painelTelefone;
    private JPanel painelEmail;

    // Serviços injetados
    private final IClienteServico clienteServico;
    private final IPecaServico pecaServico;       // Para passar para TelaConfirmarPedido, se necessário
    private final IReservaServico reservaServico; // Para passar para TelaConfirmarPedido

    /**
     * Construtor da TelaCadastrar.
     *
     * @param cpf O CPF informado na tela anterior (pode ser null para cadastro avulso).
     * @param peca A peça selecionada (pode ser null para cadastro avulso).
     * @param assentosSelecionados Lista de assentos (pode ser null para cadastro avulso).
     * @param clienteServico Serviço para operações de cliente.
     * @param pecaServico Serviço para operações de peça (para repassar).
     * @param reservaServico Serviço para operações de reserva (para repassar).
     */
    public TelaCadastrar(String cpf, Peca peca, List<Assento> assentosSelecionados,
                         IClienteServico clienteServico, IPecaServico pecaServico, IReservaServico reservaServico) {
        if (clienteServico == null || pecaServico == null || reservaServico == null) {
            throw new IllegalArgumentException("Serviços não podem ser nulos em TelaCadastrar.");
        }
        this.cpfInformadoOriginalmente = cpf;
        this.peca = peca;
        this.assentosSelecionados = assentosSelecionados;
        // this.turnoSelecionado será setado por setTurnoSelecionado

        this.clienteServico = clienteServico;
        this.pecaServico = pecaServico;
        this.reservaServico = reservaServico;

        // Removida instanciação: this.clienteServico = new ClienteServico();
        configurarTelaVisual();
    }
    
    /**
     * Define o turno selecionado, relevante para o fluxo de compra.
     * @param turno O turno selecionado.
     */
    public void setTurnoSelecionado(Turno turno) {
        // Este método é chamado por quem cria esta tela (TelaInformarCPF)
        // se o cadastro for parte do fluxo de compra.
        this.turnoSelecionado = turno;
        // System.out.println("TelaCadastrar: Turno selecionado definido: " + (turno != null ? turno.getNome() : "null"));
    }

    private void configurarTelaVisual() { // Renomeado de configurarTela
        // ... (toda a lógica de criação da UI: painelLogo, titulo, formulario, checkbox, btnCadastrar, btnVoltar)
        // O método criarCampoCPF() usará this.cpfInformadoOriginalmente.
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel containerPrincipal = new JPanel();
        containerPrincipal.setLayout(new BoxLayout(containerPrincipal, BoxLayout.Y_AXIS));
        containerPrincipal.setBackground(Constantes.AZUL_ESCURO);

        JPanel painelLogo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelLogo.setBackground(Constantes.AZUL_ESCURO);
        painelLogo.add(new LogoTeatro());
        containerPrincipal.add(Box.createVerticalStrut(30));
        containerPrincipal.add(painelLogo);

        JLabel titulo = new JLabel("CADASTRAR");
        titulo.setFont(Constantes.FONTE_TITULO);
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        containerPrincipal.add(Box.createVerticalStrut(40));
        containerPrincipal.add(titulo);

        JPanel formulario = criarFormulario();
        containerPrincipal.add(Box.createVerticalStrut(40));
        containerPrincipal.add(formulario);

        JPanel painelCheckbox = criarCheckboxABCGold();
        containerPrincipal.add(Box.createVerticalStrut(30));
        containerPrincipal.add(painelCheckbox);

        BotaoAnimado btnCadastrarUI = new BotaoAnimado("CADASTRAR", // Renomeado para não conflitar com método
                Constantes.LARANJA, Constantes.AMARELO, new Dimension(400, 70));
        btnCadastrarUI.setFont(new Font("Arial", Font.BOLD, 28));
        btnCadastrarUI.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCadastrarUI.addActionListener(e -> realizarCadastro()); // Chama o método de lógica

        containerPrincipal.add(Box.createVerticalStrut(40));
        containerPrincipal.add(btnCadastrarUI);
        containerPrincipal.add(Box.createVerticalStrut(30));

        scrollPane.setViewportView(containerPrincipal);
        add(scrollPane, BorderLayout.CENTER);

        JPanel painelVoltar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // ... (configurações do painelVoltar e btnVoltar como antes) ...
        painelVoltar.setBackground(Constantes.AZUL_ESCURO);
        JButton btnVoltar = new JButton("VOLTAR");
        btnVoltar.setFont(new Font("Arial", Font.PLAIN, 18));
        btnVoltar.setForeground(Constantes.AZUL_CLARO);
        btnVoltar.setBackground(Constantes.AZUL_ESCURO);
        // ... (configurações do btnVoltar) ...
        btnVoltar.addActionListener(e -> voltar());
        painelVoltar.add(btnVoltar);
        add(painelVoltar, BorderLayout.SOUTH);
    }

    private JPanel criarFormulario() {
        // ... (como antes, usando criarCampoComRotulo para cada campo da UI) ...
        // O método criarCampoCPF() usará this.cpfInformadoOriginalmente
        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        // ... (demais configurações)
        formulario.add(criarCampoComRotulo("CPF", criarCampoCPF()));
        // ...
        return formulario;
    }
    
    private JFormattedTextField criarCampoCPF() {
        try {
            MaskFormatter maskCPF = new MaskFormatter("###.###.###-##");
            maskCPF.setPlaceholderCharacter('_');
            txtCPF = new JFormattedTextField(maskCPF);
            if (this.cpfInformadoOriginalmente != null && !this.cpfInformadoOriginalmente.isBlank()) {
                // Se o CPF informado já vier normalizado (só números), a máscara pode não aplicar bem.
                // Idealmente, TelaInformarCPF passaria o CPF formatado ou a máscara seria mais flexível.
                // Por ora, apenas setamos o texto.
                txtCPF.setText(this.cpfInformadoOriginalmente); // Pode precisar de ajuste se cpfInformadoOriginalmente não tiver máscara
                txtCPF.setEditable(false);
                txtCPF.setBackground(new Color(70, 80, 90));
            }
        } catch (ParseException e) {
            txtCPF = new JFormattedTextField();
            System.err.println("Erro ao criar máscara de CPF em TelaCadastrar: " + e.getMessage());
        }
        configurarCampo(txtCPF);
        return txtCPF;
    }
    // ... (criarCampoNome, criarCampoDataNascimento, criarCampoTelefone, criarCampoEmail, configurarCampo como antes) ...
    // ... (criarCheckboxABCGold como antes) ...
    private JTextField criarCampoNome() { txtNome = new JTextField(); configurarCampo(txtNome); return txtNome;}
    private JFormattedTextField criarCampoDataNascimento() { /*...*/ txtDataNascimento = new JFormattedTextField(); configurarCampo(txtDataNascimento); return txtDataNascimento;}
    private JFormattedTextField criarCampoTelefone() { /*...*/ txtTelefone = new JFormattedTextField(); configurarCampo(txtTelefone); return txtTelefone;}
    private JTextField criarCampoEmail() { txtEmail = new JTextField(); configurarCampo(txtEmail); return txtEmail;}
    private void configurarCampo(JTextField campo) { /* ... */ }
    private JPanel criarCampoComRotulo(String rotulo, JTextField campo) { /* ... */ return new JPanel();}
    private JPanel criarCheckboxABCGold() { /* ... */ chkMembroABC = new JCheckBox(); /*...*/ return new JPanel(); }


    private void realizarCadastro() { // Renomeado de cadastrar para evitar conflito com método da interface
        if (!validarCamposEntrada()) { // Renomeado de validarCampos
            return;
        }

        try {
            String cpf = txtCPF.getValue() != null ? txtCPF.getText() : this.cpfInformadoOriginalmente; // Pega do campo ou o original
            String nome = txtNome.getText().trim();
            String dataNascimentoStr = txtDataNascimento.getText(); // A máscara já formata
            String telefone = chkMembroABC.isSelected() ? txtTelefone.getText() : ""; // Pega da máscara
            String email = chkMembroABC.isSelected() ? txtEmail.getText().trim() : "";
            
            String identificadorPlano = chkMembroABC.isSelected() ?
                                        com.teatroabc.dominio.fidelidade.MembroABCGold.IDENTIFICADOR :
                                        com.teatroabc.dominio.fidelidade.SemFidelidade.IDENTIFICADOR;

            DadosCadastroClienteDTO dto = new DadosCadastroClienteDTO(
                cpf, nome, dataNascimentoStr, telefone, email, identificadorPlano
            );

            Cliente clienteCadastrado = this.clienteServico.cadastrar(dto); // USA O SERVIÇO INJETADO

            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

            if (this.peca != null && this.assentosSelecionados != null && this.turnoSelecionado != null) {
                // Veio do fluxo de compra, prosseguir para TelaConfirmarPedido
                // TelaConfirmarPedido precisa de Peca, Cliente, List<Assento>, Turno, e IReservaServico
                TelaConfirmarPedido telaConfirmar = new TelaConfirmarPedido(
                    this.peca,
                    clienteCadastrado,
                    this.assentosSelecionados,
                    this.turnoSelecionado,
                    this.reservaServico // Passa o serviço de reserva
                );
                frame.setContentPane(telaConfirmar);
            } else {
                // Cadastro avulso, mostrar mensagem de sucesso e voltar para TelaPrincipal
                String mensagem = chkMembroABC.isSelected() ? 
                    "Cliente cadastrado como membro ABC GOLD com sucesso!\n" + clienteCadastrado.getDescricaoBeneficiosPlano() :
                    "Cliente cadastrado com sucesso!";
                JOptionPane.showMessageDialog(this, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                // TelaPrincipal precisa dos serviços
                frame.setContentPane(new TelaPrincipal(this.clienteServico, this.pecaServico, this.reservaServico));
            }

            frame.revalidate();
            frame.repaint();

        } catch (ClienteJaCadastradoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro de Cadastro", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Erro nos dados informados: " + e.getMessage(), "Dados Inválidos", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) { // Captura genérica para outros erros inesperados
            JOptionPane.showMessageDialog(this,
                    "Ocorreu um erro inesperado ao cadastrar cliente: " + e.getMessage(),
                    "Erro Crítico",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Logar para debug
        }
    }

    private boolean validarCamposEntrada() { // Renomeado de validarCampos
        // ... (lógica de validação dos campos da UI como antes: nome, data de nascimento,
        //         telefone e email se chkMembroABC estiver selecionado) ...
        //         O CPF já é validado pelo MaskFormatter e pela lógica em realizarCadastro()
        //         que pode usar ValidadorCPF se implementado.
        //         A validação de formato de data é feita no serviço, mas podemos ter uma aqui também.
        if (txtNome.getText().trim().isEmpty()) { /*...*/ return false; }
        if (txtDataNascimento.getText().replace("_", "").replace("/", "").trim().isEmpty()) { /*...*/ return false; }
        try {
            LocalDate.parse(txtDataNascimento.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) { /*...*/ return false;}

        if (chkMembroABC.isSelected()) {
            if (txtTelefone.getText().replace("(", "").replace(")", "").replace("-", "").replace("_", "").trim().isEmpty()) { /*...*/ return false; }
            if (txtEmail.getText().trim().isEmpty()) { /*...*/ return false; }
            if (!txtEmail.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) { /*...*/ return false; } // Validação simples de email
        }
        return true;
    }

    private void voltar() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        if (this.peca != null) {
            // Se veio do fluxo de compra, volta para TelaInformarCPF
            // TelaInformarCPF precisa dos dados da compra e dos serviços
            TelaInformarCPF telaInformarCPF = new TelaInformarCPF(
                false, // modoConsulta
                this.peca,
                this.assentosSelecionados,
                this.clienteServico,
                this.reservaServico
            );
            telaInformarCPF.setTurnoSelecionado(this.turnoSelecionado);
            frame.setContentPane(telaInformarCPF);
        } else {
            // Se foi cadastro avulso, volta para TelaPrincipal
            frame.setContentPane(new TelaPrincipal(this.clienteServico, this.pecaServico, this.reservaServico));
        }

        frame.revalidate();
        frame.repaint();
    }
}