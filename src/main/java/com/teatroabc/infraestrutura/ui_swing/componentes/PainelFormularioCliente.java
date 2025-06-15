package com.teatroabc.infraestrutura.ui_swing.componentes;

import com.teatroabc.aplicacao.dto.DadosCadastroClienteDTO;
import com.teatroabc.dominio.validadores.ValidadorCPF;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Componente que encapsula o formulário completo para cadastro de um novo cliente.
 * Gerencia todos os campos de entrada, suas máscaras, estilos e a lógica de
 * visibilidade para campos opcionais relacionados ao plano de fidelidade.
 */
public class PainelFormularioCliente extends JPanel {

    private JFormattedTextField txtCPF;
    private JTextField txtNome;
    private JFormattedTextField txtDataNascimento;
    private JFormattedTextField txtTelefone;
    private JTextField txtEmail;
    private JPanel painelTelefone;
    private JPanel painelEmail;
    private boolean isMembroGold = false;
    private final String cpfInicial;

    /**
     * Construtor do PainelFormularioCliente.
     * @param cpfInicial O CPF vindo da tela anterior, se houver. Pode ser nulo para cadastro avulso.
     */
    public PainelFormularioCliente(String cpfInicial) {
        this.cpfInicial = cpfInicial;
        configurarPainel();
        adicionarCampos();
    }

    /**
     * Coleta os dados de todos os campos do formulário e os monta em um DTO.
     * Este método é a interface pública para a tela principal obter os dados inseridos.
     * @return um objeto DadosCadastroClienteDTO com as informações do formulário.
     */
    public DadosCadastroClienteDTO getDadosCadastroDTO() {
        String cpfFinal;
        // Se o CPF veio pré-preenchido (fluxo de compra), usa esse valor.
        if (this.cpfInicial != null && !this.cpfInicial.isBlank()) {
            cpfFinal = this.cpfInicial;
        } else {
            // Caso contrário (cadastro avulso), pega o valor do campo de texto e normaliza.
            cpfFinal = ValidadorCPF.normalizar(txtCPF.getText());
        }

        String identificadorPlano = isMembroGold ? "GOLD" : "PADRAO";

        return new DadosCadastroClienteDTO(
            cpfFinal,
            txtNome.getText().trim(),
            txtDataNascimento.getText(),
            isMembroGold ? txtTelefone.getText().replaceAll("[^0-9]", "") : "",
            isMembroGold ? txtEmail.getText().trim() : "",
            identificadorPlano
        );
    }

    /**
     * Controla a visibilidade dos campos de Telefone e Email, que são
     * obrigatórios apenas para membros do plano de fidelidade.
     * @param visivel true para mostrar os campos, false para ocultar.
     */
    public void setCamposMembroGoldVisivel(boolean visivel) {
        this.isMembroGold = visivel;
        painelTelefone.setVisible(visivel);
        painelEmail.setVisible(visivel);
        // Força a revalidação do layout do contêiner pai para ajustar o espaço
        if (this.getParent() != null) {
             SwingUtilities.getWindowAncestor(this).revalidate();
             SwingUtilities.getWindowAncestor(this).repaint();
        }
    }
    
    /**
     * Valida os campos do formulário antes de uma tentativa de submissão.
     * @return true se todos os campos obrigatórios estiverem válidos, false caso contrário.
     */
    public boolean validarCampos() {
        if (txtNome.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O campo Nome é obrigatório.", "Campo Inválido", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        String dataNascStr = txtDataNascimento.getText().replaceAll("[_/]", "").trim();
        if (dataNascStr.length() != 8) {
            JOptionPane.showMessageDialog(this, "O campo Data de Nascimento deve ser preenchido completamente.", "Campo Inválido", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            LocalDate.parse(txtDataNascimento.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "O formato da Data de Nascimento é inválido. Use dd/MM/yyyy.", "Formato Inválido", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (isMembroGold) {
            if (txtTelefone.getText().replaceAll("[()_ -]", "").trim().length() < 10) {
                JOptionPane.showMessageDialog(this, "Telefone é obrigatório e deve ser completo para Membros GOLD.", "Campo Obrigatório", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (!txtEmail.getText().trim().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                JOptionPane.showMessageDialog(this, "O formato do E-mail é inválido.", "Formato Inválido", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        return true;
    }

    private void configurarPainel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setMaximumSize(new Dimension(500, 600));
        setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void adicionarCampos() {
        add(criarCampoComRotulo("CPF", criarCampoCPF()));
        add(Box.createVerticalStrut(25));
        add(criarCampoComRotulo("NOME COMPLETO", criarCampoNome()));
        add(Box.createVerticalStrut(25));
        add(criarCampoComRotulo("DATA DE NASCIMENTO", criarCampoDataNascimento()));
        
        painelTelefone = criarCampoComRotulo("TELEFONE", criarCampoTelefone());
        painelTelefone.setVisible(false);
        add(Box.createVerticalStrut(25));
        add(painelTelefone);
    
        painelEmail = criarCampoComRotulo("E-MAIL", criarCampoEmail());
        painelEmail.setVisible(false);
        add(Box.createVerticalStrut(25));
        add(painelEmail);
    }
    
    private void configurarCampo(JComponent campo) { 
        campo.setPreferredSize(new Dimension(500, 55));
        campo.setMaximumSize(new Dimension(500, 55));
        campo.setFont(new Font("Arial", Font.PLAIN, 18));
        campo.setBackground(new Color(52, 73, 94));
        campo.setForeground(Color.WHITE);
        
        if (campo instanceof JTextComponent) {
            ((JTextComponent) campo).setCaretColor(Constantes.AMARELO);
        }
        
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Constantes.AZUL_CLARO, 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
    }
    
    private JFormattedTextField criarCampoCPF() {
        try {
            MaskFormatter maskCPF = new MaskFormatter("###.###.###-##");
            maskCPF.setPlaceholderCharacter('_');
            txtCPF = new JFormattedTextField(maskCPF);
            if (this.cpfInicial != null && !this.cpfInicial.isBlank()) {
                txtCPF.setValue(this.cpfInicial);
                txtCPF.setEditable(false);
                txtCPF.setBackground(new Color(70, 80, 90));
            }
        } catch (ParseException e) {
            txtCPF = new JFormattedTextField();
            System.err.println("Erro ao criar máscara de CPF: " + e.getMessage());
        }
        configurarCampo(txtCPF);
        return txtCPF;
    }
    
    private JTextField criarCampoNome() {
        txtNome = new JTextField();
        configurarCampo(txtNome);
        return txtNome;
    }

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

    private JTextField criarCampoEmail() {
        txtEmail = new JTextField();
        configurarCampo(txtEmail);
        return txtEmail;
    }
    
    private JPanel criarCampoComRotulo(String rotuloTexto, JComponent campo) { 
        JPanel painelCampo = new JPanel();
        painelCampo.setLayout(new BoxLayout(painelCampo, BoxLayout.Y_AXIS));
        painelCampo.setOpaque(false);
        painelCampo.setMaximumSize(new Dimension(500, 85));
        painelCampo.setAlignmentX(Component.LEFT_ALIGNMENT);
    
        JLabel lblRotulo = new JLabel(rotuloTexto);
        lblRotulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblRotulo.setForeground(Color.WHITE);
        lblRotulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelCampo.add(lblRotulo);
    
        painelCampo.add(Box.createVerticalStrut(8));
    
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelCampo.add(campo);
    
        return painelCampo;
    }
}