package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.infraestrutura.ui_swing.componentes.*;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.aplicacao.dto.DadosCadastroClienteDTO;
import com.teatroabc.aplicacao.excecoes.ClienteJaCadastradoException;
import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TelaCadastrar extends JPanel {
    // Dados da compra em andamento
    private final String cpfInformadoOriginalmente;
    private final Peca peca;
    private final List<Assento> assentosSelecionados;
    private Turno turnoSelecionado;

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
    private final IPecaServico pecaServico;
    private final IReservaServico reservaServico;

    public TelaCadastrar(String cpf, Peca peca, List<Assento> assentosSelecionados,
                         IClienteServico clienteServico, IPecaServico pecaServico, IReservaServico reservaServico) {
        if (clienteServico == null || pecaServico == null || reservaServico == null) {
            throw new IllegalArgumentException("Serviços não podem ser nulos em TelaCadastrar.");
        }
        this.cpfInformadoOriginalmente = cpf;
        this.peca = peca;
        this.assentosSelecionados = assentosSelecionados;
        this.clienteServico = clienteServico;
        this.pecaServico = pecaServico;
        this.reservaServico = reservaServico;
        configurarTelaVisual();
    }
    
    public void setTurnoSelecionado(Turno turno) {
        this.turnoSelecionado = turno;
    }

    private void configurarTelaVisual() {
        // ... (código da UI como na sua última versão) ...
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

        BotaoAnimado btnCadastrarUI = new BotaoAnimado("CADASTRAR", 
                Constantes.LARANJA, Constantes.AMARELO, new Dimension(400, 70));
        btnCadastrarUI.setFont(new Font("Arial", Font.BOLD, 28));
        btnCadastrarUI.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCadastrarUI.addActionListener(e -> realizarCadastro()); 

        containerPrincipal.add(Box.createVerticalStrut(40));
        containerPrincipal.add(btnCadastrarUI);
        containerPrincipal.add(Box.createVerticalStrut(30));

        scrollPane.setViewportView(containerPrincipal);
        add(scrollPane, BorderLayout.CENTER);

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

    private JPanel criarFormulario() {
        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.setBackground(Constantes.AZUL_ESCURO);
        formulario.setMaximumSize(new Dimension(500, 600)); // Ajustar conforme necessidade
        formulario.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        formulario.add(criarCampoComRotulo("CPF", criarCampoCPF()));
        formulario.add(Box.createVerticalStrut(25));
    
        formulario.add(criarCampoComRotulo("NOME", criarCampoNome()));
        formulario.add(Box.createVerticalStrut(25));
    
        formulario.add(criarCampoComRotulo("DATA DE NASCIMENTO", criarCampoDataNascimento()));
    
        // Campos para membro ABC GOLD (inicialmente ocultos)
        painelTelefone = criarCampoComRotulo("TELEFONE", criarCampoTelefone());
        painelTelefone.setVisible(false); // Inicia oculto
        formulario.add(Box.createVerticalStrut(25));
        formulario.add(painelTelefone);
    
        painelEmail = criarCampoComRotulo("E-MAIL", criarCampoEmail());
        painelEmail.setVisible(false); // Inicia oculto
        formulario.add(Box.createVerticalStrut(25)); // Espaçamento mesmo se oculto, para consistência
        formulario.add(painelEmail);
    
        return formulario;
    }
    
    private JFormattedTextField criarCampoCPF() {
        try {
            MaskFormatter maskCPF = new MaskFormatter("###.###.###-##");
            maskCPF.setPlaceholderCharacter('_');
            txtCPF = new JFormattedTextField(maskCPF);
            if (this.cpfInformadoOriginalmente != null && !this.cpfInformadoOriginalmente.isBlank()) {
                String cpfApenasDigitos = this.cpfInformadoOriginalmente.replaceAll("[^0-9]", "");
                if (cpfApenasDigitos.length() == 11) {
                    // Se o CPF informado já vier normalizado (só números), a máscara pode não aplicar bem diretamente.
                    // Tentamos formatar para a máscara.
                    txtCPF.setValue(cpfApenasDigitos); // setValue tenta aplicar a máscara
                } else {
                    txtCPF.setText(this.cpfInformadoOriginalmente); // Se não for 11 digitos, apenas seta o texto
                }
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

    private JTextField criarCampoNome() { txtNome = new JTextField(); configurarCampo(txtNome); return txtNome;}
    private JFormattedTextField criarCampoDataNascimento() { 
        try {
            MaskFormatter maskData = new MaskFormatter("##/##/####");
            maskData.setPlaceholderCharacter('_');
            txtDataNascimento = new JFormattedTextField(maskData);
        } catch (ParseException e) {
            txtDataNascimento = new JFormattedTextField();
            System.err.println("Erro ao criar máscara de Data de Nascimento: " + e.getMessage());
        }
        configurarCampo(txtDataNascimento); 
        return txtDataNascimento;
    }
    private JFormattedTextField criarCampoTelefone() { 
        try {
            MaskFormatter maskTelefone = new MaskFormatter("(##) #####-####");
            maskTelefone.setPlaceholderCharacter('_');
            txtTelefone = new JFormattedTextField(maskTelefone);
        } catch (ParseException e) {
            txtTelefone = new JFormattedTextField();
            System.err.println("Erro ao criar máscara de Telefone: " + e.getMessage());
        }
        configurarCampo(txtTelefone); 
        return txtTelefone;
    }
    private JTextField criarCampoEmail() { txtEmail = new JTextField(); configurarCampo(txtEmail); return txtEmail;}
    
    private void configurarCampo(JTextField campo) { 
        campo.setPreferredSize(new Dimension(500, 55));
        campo.setMaximumSize(new Dimension(500, 55));
        campo.setFont(new Font("Arial", Font.PLAIN, 18));
        campo.setBackground(new Color(52, 73, 94));
        campo.setForeground(Color.WHITE);
        campo.setCaretColor(Color.WHITE);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constantes.AZUL_CLARO, 2),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
    }

    private JPanel criarCampoComRotulo(String rotuloTexto, JTextField campo) { 
        JPanel painelCampo = new JPanel();
        painelCampo.setLayout(new BoxLayout(painelCampo, BoxLayout.Y_AXIS));
        painelCampo.setBackground(Constantes.AZUL_ESCURO);
        painelCampo.setMaximumSize(new Dimension(500, 85));
        painelCampo.setAlignmentX(Component.LEFT_ALIGNMENT); // Alinhar o painel em si à esquerda
    
        JLabel lblRotulo = new JLabel(rotuloTexto);
        lblRotulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblRotulo.setForeground(Color.WHITE);
        lblRotulo.setAlignmentX(Component.LEFT_ALIGNMENT); // Alinhar o rótulo à esquerda
        painelCampo.add(lblRotulo);
    
        painelCampo.add(Box.createVerticalStrut(8));
    
        campo.setAlignmentX(Component.LEFT_ALIGNMENT); // Alinhar o campo à esquerda
        painelCampo.add(campo);
    
        return painelCampo;
    }

    private JPanel criarCheckboxABCGold() { 
        JPanel painelCheckbox = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelCheckbox.setBackground(Constantes.AZUL_ESCURO);
        painelCheckbox.setMaximumSize(new Dimension(500, 70)); // Um pouco mais de altura
    
        JPanel containerCheckbox = new JPanel();
        containerCheckbox.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5)); // Ajustar espaçamento
        containerCheckbox.setBackground(new Color(255, 193, 7, 30)); // Amarelo um pouco mais opaco
        containerCheckbox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Constantes.AMARELO, 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15) // Ajustar padding
        ));
    
        chkMembroABC = new JCheckBox("Desejo ser membro ABC GOLD"); // Texto alterado
        chkMembroABC.setFont(new Font("Arial", Font.BOLD, 16)); // Fonte um pouco menor
        chkMembroABC.setForeground(Constantes.AMARELO);
        chkMembroABC.setOpaque(false); // Para o fundo do containerCheckbox ser visível
        chkMembroABC.setFocusPainted(false);
        chkMembroABC.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chkMembroABC.setIconTextGap(10);
        
        // ... (lógica dos ícones do checkbox como antes) ...
    
        chkMembroABC.addActionListener(e -> {
            boolean selecionado = chkMembroABC.isSelected();
            painelTelefone.setVisible(selecionado);
            painelEmail.setVisible(selecionado);
            // Forçar o re-layout do contêiner pai para ajustar o espaço
            SwingUtilities.getWindowAncestor(this).revalidate();
            SwingUtilities.getWindowAncestor(this).repaint();
        });
    
        // JLabel lblEstrela = new JLabel("⭐"); // Opcional, pode poluir
        // lblEstrela.setFont(new Font("Arial", Font.PLAIN, 20));
        // containerCheckbox.add(lblEstrela);
        containerCheckbox.add(chkMembroABC);
        // containerCheckbox.add(lblEstrela); // Outra estrela opcional
        
        painelCheckbox.add(containerCheckbox);
        return painelCheckbox;
    }


    private void realizarCadastro() {
        if (!validarCamposEntrada()) {
            return;
        }

        try {
            // Se txtCPF foi editável (cadastro avulso), pega seu valor. Senão, usa o que veio pré-preenchido.
            String cpfFinal = (this.cpfInformadoOriginalmente != null && !this.cpfInformadoOriginalmente.isBlank()) ?
                               this.cpfInformadoOriginalmente.replaceAll("[^0-9]","") :
                               txtCPF.getText().replaceAll("[^0-9]","");

            String nome = txtNome.getText().trim();
            String dataNascimentoStr = txtDataNascimento.getText();
            String telefone = chkMembroABC.isSelected() ? txtTelefone.getText().replaceAll("[^0-9]", "") : "";
            String email = chkMembroABC.isSelected() ? txtEmail.getText().trim() : "";
            
            String identificadorPlano = chkMembroABC.isSelected() ?
                                        com.teatroabc.dominio.fidelidade.MembroABCGold.IDENTIFICADOR :
                                        com.teatroabc.dominio.fidelidade.SemFidelidade.IDENTIFICADOR;

            DadosCadastroClienteDTO dto = new DadosCadastroClienteDTO(
                cpfFinal, nome, dataNascimentoStr, telefone, email, identificadorPlano
            );

            Cliente clienteCadastrado = this.clienteServico.cadastrar(dto);

            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

            if (this.peca != null && this.assentosSelecionados != null && this.turnoSelecionado != null) {
                // CORREÇÃO AQUI: Passar todos os serviços para TelaConfirmarPedido
                TelaConfirmarPedido telaConfirmar = new TelaConfirmarPedido(
                    this.peca,
                    clienteCadastrado,
                    this.assentosSelecionados,
                    this.turnoSelecionado,
                    this.clienteServico, // Passando
                    this.pecaServico,    // Passando
                    this.reservaServico
                );
                frame.setContentPane(telaConfirmar);
            } else {
                String mensagem = chkMembroABC.isSelected() ? 
                    "Cliente cadastrado como membro ABC GOLD!\n" + clienteCadastrado.getDescricaoBeneficiosPlano() :
                    "Cliente cadastrado com sucesso!";
                JOptionPane.showMessageDialog(this, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                frame.setContentPane(new TelaPrincipal(this.clienteServico, this.pecaServico, this.reservaServico));
            }

            frame.revalidate();
            frame.repaint();

        } catch (ClienteJaCadastradoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro de Cadastro", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Erro nos dados informados: " + e.getMessage(), "Dados Inválidos", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado: " + e.getMessage(), "Erro Crítico", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private boolean validarCamposEntrada() {
        // ... (lógica de validação da UI como antes) ...
        if (txtNome.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Nome é obrigatório."); return false; }
        String dataNascStr = txtDataNascimento.getText().replaceAll("[_/]", "");
        if (dataNascStr.trim().isEmpty() ) { JOptionPane.showMessageDialog(this, "Data de Nascimento é obrigatória."); return false; }
        try {
            LocalDate.parse(txtDataNascimento.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) { JOptionPane.showMessageDialog(this, "Data de Nascimento inválida."); return false;}

        if (chkMembroABC.isSelected()) {
            String telStr = txtTelefone.getText().replaceAll("[()_ -]", "");
            if (telStr.trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Telefone é obrigatório para Membro GOLD."); return false; }
            if (txtEmail.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Email é obrigatório para Membro GOLD."); return false; }
            if (!txtEmail.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) { JOptionPane.showMessageDialog(this, "Email inválido."); return false; }
        }
        return true;
    }

    private void voltar() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        if (this.peca != null) {
            // CORREÇÃO AQUI: Passar todos os serviços para TelaInformarCPF
            TelaInformarCPF telaInformarCPF = new TelaInformarCPF(
                false, 
                this.peca,
                this.assentosSelecionados,
                this.clienteServico,
                this.pecaServico, // Passando
                this.reservaServico
            );
            telaInformarCPF.setTurnoSelecionado(this.turnoSelecionado);
            frame.setContentPane(telaInformarCPF);
        } else {
            frame.setContentPane(new TelaPrincipal(this.clienteServico, this.pecaServico, this.reservaServico));
        }

        frame.revalidate();
        frame.repaint();
    }
}