package com.teatroabc.infraestrutura.ui_swing.componentes;

import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.modelos.Bilhete;
import com.teatroabc.dominio.modelos.Sessao;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes; // Importa a nova entidade de domínio
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorData;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;

/**
 * Componente visual customizado para exibir um resumo das informações de um Bilhete
 * em uma lista, como na tela "Meus Bilhetes".
 * 
 * Na Arquitetura Hexagonal, esta classe é um Adaptador de UI. Ela recebe um objeto
 * de domínio (Bilhete) e é responsável por sua formatação e apresentação visual
 * de forma concisa.
 */
public class CardBilhete extends JPanel {
    private final Bilhete bilhete;
    private ActionListener actionListener;

    /**
     * Construtor do CardBilhete.
     * @param bilhete O objeto de domínio Bilhete a ser exibido. Não pode ser nulo.
     */
    public CardBilhete(Bilhete bilhete) {
        validacaoBilhete();
        this.bilhete = bilhete;        
        configurarLayoutECores();
        adicionarComponentesVisuais();
    }

    //encapsular a lógica de validaçao
    private boolean verificarBilheteNull (Bilhete bilhete){
        return bilhete == null;
    }
    private void validacaoBilhete (){
        if (verificarBilheteNull(bilhete)) {
            throw new IllegalArgumentException("O objeto Bilhete não pode ser nulo.");
        }
    }

    private void configurarLayoutECores() {
        setLayout(new BorderLayout(20, 0));
        setBackground(Constantes.CINZA_ESCURO);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Constantes.AZUL_ESCURO.brighter()),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 130)); 
    }

    private void adicionarComponentesVisuais() {
        add(criarPainelDeInformacoes(), BorderLayout.CENTER);
        add(criarPainelDoBotao(), BorderLayout.EAST);
    }
    
    /**
     * Cria o painel que contém as informações textuais do bilhete.
     * Acessa os dados da peça e da apresentação através do objeto Sessao.
     * @return JPanel configurado com os detalhes do bilhete.
     */
    private JPanel criarPainelDeInformacoes() {
        JPanel painelInfo = new JPanel(new GridBagLayout());
        painelInfo.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 0, 2, 20);

        // Pega a sessão do bilhete para facilitar o acesso aos dados
        Sessao sessao = bilhete.getSessao();

        // Título da Peça
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblTituloPeca = new JLabel(sessao.getPeca().getTitulo()); 
        lblTituloPeca.setFont(new Font("Arial", Font.BOLD, 20));
        lblTituloPeca.setForeground(Color.WHITE);
        painelInfo.add(lblTituloPeca, gbc);

        // Rótulo "Data"
        gbc.gridy = 1; gbc.gridwidth = 1;
        painelInfo.add(criarLabelDeRotulo("Data"), gbc);
        
        // Rótulo "Assentos"
        gbc.gridy = 2;
        painelInfo.add(criarLabelDeRotulo("Assentos"), gbc);
        
        // Valor da Data (formatado)
        gbc.gridx = 1; gbc.gridy = 1;
        JLabel lblDataValor = criarLabelDeValor(FormatadorData.formatar(sessao.getDataHora())); // MUDANÇA: Usa sessao.getDataHora()
        painelInfo.add(lblDataValor, gbc);

        // Valor dos Assentos (formatado)
        gbc.gridy = 2;
        List<Assento> assentos = bilhete.getAssentos();
        String assentosTexto = assentos.stream()
            .map(Assento::getCodigo)
            .collect(Collectors.joining(", "));
        JLabel lblAssentosValor = criarLabelDeValor(assentosTexto);
        painelInfo.add(lblAssentosValor, gbc);

        return painelInfo;
    }
    
    private JLabel criarLabelDeRotulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(Color.LIGHT_GRAY);
        return label;
    }
    
    private JLabel criarLabelDeValor(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JPanel criarPainelDoBotao() {
        JPanel painelBotao = new JPanel(new GridBagLayout());
        painelBotao.setOpaque(false);

        BotaoAnimado btnVisualizar = new BotaoAnimado("VISUALIZAR",
            Constantes.AZUL_CLARO, new Color(70, 130, 180), new Dimension(120, 40));
        btnVisualizar.setFont(new Font("Arial", Font.BOLD, 14));
        
        btnVisualizar.addActionListener(e -> {
            if (actionListener != null) {
                // Cria um novo ActionEvent, passando o CardBilhete (this) como a fonte.
                ActionEvent eventoDoCard = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null);
                actionListener.actionPerformed(eventoDoCard);
            }
        });
        
        painelBotao.add(btnVisualizar);
        return painelBotao;
    }

    public void addActionListener(ActionListener listener) {
        this.actionListener = listener;
    }
    
    //acesso ao bilhete para popular o card
    public Bilhete getBilhete() {
        return bilhete;
    }
}