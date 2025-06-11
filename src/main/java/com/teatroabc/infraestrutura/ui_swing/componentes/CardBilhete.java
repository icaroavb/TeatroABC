package com.teatroabc.infraestrutura.ui_swing.componentes;

import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.modelos.Bilhete;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.stream.Collectors;

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

    public CardBilhete(Bilhete bilhete) {
        if (bilhete == null) {
            throw new IllegalArgumentException("O objeto Bilhete não pode ser nulo.");
        }
        this.bilhete = bilhete;
        
        configurarLayoutECores();
        adicionarComponentesVisuais();
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
    
    private JPanel criarPainelDeInformacoes() {
        JPanel painelInfo = new JPanel(new GridBagLayout());
        painelInfo.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 0, 2, 20);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblTituloPeca = new JLabel(bilhete.getPeca().getTitulo());
        lblTituloPeca.setFont(new Font("Arial", Font.BOLD, 20));
        lblTituloPeca.setForeground(Color.WHITE);
        painelInfo.add(lblTituloPeca, gbc);

        gbc.gridy = 1; gbc.gridwidth = 1;
        painelInfo.add(criarLabelDeRotulo("Data"), gbc);
        
        gbc.gridy = 2;
        painelInfo.add(criarLabelDeRotulo("Assentos"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        JLabel lblDataValor = criarLabelDeValor(FormatadorData.formatar(bilhete.getPeca().getDataHora()));
        painelInfo.add(lblDataValor, gbc);

        gbc.gridy = 2;
        String assentosTexto = bilhete.getAssentos().stream()
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

    /**
     * Cria o painel que contém o botão de ação "Visualizar".
     * @return JPanel com o botão.
     */
    private JPanel criarPainelDoBotao() {
        JPanel painelBotao = new JPanel(new GridBagLayout());
        painelBotao.setOpaque(false);

        BotaoAnimado btnVisualizar = new BotaoAnimado("VISUALIZAR",
            Constantes.AZUL_CLARO, new Color(70, 130, 180), new Dimension(120, 40));
        btnVisualizar.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Adiciona um listener que repassa o evento para o listener externo.
        btnVisualizar.addActionListener(e -> {
            if (actionListener != null) {
                // **CORREÇÃO APLICADA AQUI**
                // Cria um novo ActionEvent, mas agora a fonte (o primeiro argumento)
                // é o próprio CardBilhete (this), e não o botão que foi clicado.
                // Isso permite que a tela que ouve este evento faça o cast para CardBilhete
                // sem erro e obtenha os dados do bilhete correto.
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
    
    public Bilhete getBilhete() {
        return bilhete;
    }
}