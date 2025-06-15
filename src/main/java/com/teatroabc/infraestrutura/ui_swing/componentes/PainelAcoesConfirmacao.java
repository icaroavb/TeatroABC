package com.teatroabc.infraestrutura.ui_swing.componentes;

import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Componente que encapsula os botões de ação ("Voltar", "Finalizar Compra")
 * da tela de confirmação de pedido.
 * Este painel agrupa os botões e associa as ações recebidas aos seus listeners.
 */
public class PainelAcoesConfirmacao extends JPanel {
    
    /**
     * Construtor do PainelAcoesConfirmacao.
     * @param voltarAction O ActionListener a ser executado quando o botão "VOLTAR" for clicado.
     * @param confirmarAction O ActionListener a ser executado quando o botão "FINALIZAR COMPRA" for clicado.
     */
    public PainelAcoesConfirmacao(ActionListener voltarAction, ActionListener confirmarAction) {
        configurarPainel();
        adicionarBotoes(voltarAction, confirmarAction);
    }
    
    /**
     * Configura as propriedades visuais do painel, como layout e transparência.
     */
    private void configurarPainel() {
        setOpaque(false); // Torna o painel transparente
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0)); // Centraliza os botões com espaçamento
    }
    
    /**
     * Cria, estiliza e adiciona os botões de ação ao painel.
     * @param voltarAction Ação do botão "Voltar".
     * @param confirmarAction Ação do botão "Finalizar Compra".
     */
    private void adicionarBotoes(ActionListener voltarAction, ActionListener confirmarAction) {
        // Criação do botão "Voltar"
        BotaoAnimado btnVoltar = new BotaoAnimado(
                "VOLTAR",
                Constantes.CINZA_ESCURO, 
                Constantes.AZUL_CLARO.darker(), 
                new Dimension(180, 55)
        );
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 18));
        btnVoltar.addActionListener(voltarAction);

        // Criação do botão "Finalizar Compra"
        BotaoAnimado btnConfirmar = new BotaoAnimado(
                "FINALIZAR COMPRA",
                Constantes.LARANJA, 
                Constantes.AMARELO.darker(), 
                new Dimension(260, 55)
        );
        btnConfirmar.setFont(new Font("Arial", Font.BOLD, 18));
        btnConfirmar.addActionListener(confirmarAction);
        
        // Adiciona os botões ao painel
        add(btnVoltar);
        add(btnConfirmar);
    }
}