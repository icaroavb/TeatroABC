package com.teatroabc.infraestrutura.ui_swing.componentes;

import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Componente que encapsula os botões de navegação da tela principal.
 * Agrupa as ações de "Comprar Bilhete", "Consultar Bilhete" e "Cadastrar Cliente"
 * em um único painel coeso e reutilizável.
 */
public class PainelNavegacaoPrincipal extends JPanel {

    /**
     * Construtor do PainelNavegacaoPrincipal.
     * @param comprarAction O ActionListener para o botão "Comprar Bilhete".
     * @param consultarAction O ActionListener para o botão "Consultar Bilhete".
     * @param cadastrarAction O ActionListener para o botão "Cadastrar Cliente".
     */
    public PainelNavegacaoPrincipal(ActionListener comprarAction, ActionListener consultarAction, ActionListener cadastrarAction) {
        configurarPainel();
        adicionarBotoes(comprarAction, consultarAction, cadastrarAction);
    }

    /**
     * Configura as propriedades visuais do painel.
     */
    private void configurarPainel() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 40, 0));
        setBackground(Constantes.AZUL_ESCURO);
    }

    /**
     * Cria, estiliza e adiciona os botões de navegação ao painel.
     */
    private void adicionarBotoes(ActionListener comprarAction, ActionListener consultarAction, ActionListener cadastrarAction) {
        BotaoAnimado btnComprar = new BotaoAnimado("COMPRAR BILHETE",
                Constantes.LARANJA, Constantes.AMARELO, new Dimension(400, 70));
        btnComprar.setFont(Constantes.FONTE_BOTAO);
        btnComprar.addActionListener(comprarAction);

        BotaoAnimado btnConsultar = new BotaoAnimado("CONSULTAR BILHETE",
                Constantes.CINZA_ESCURO, Constantes.AZUL_CLARO, new Dimension(280, 60));
        btnConsultar.setFont(new Font("Arial", Font.BOLD, 18));
        btnConsultar.addActionListener(consultarAction);

        BotaoAnimado btnCadastrar = new BotaoAnimado("CADASTRAR CLIENTE",
                Constantes.CINZA_ESCURO, Constantes.AZUL_CLARO, new Dimension(250, 60));
        btnCadastrar.setFont(new Font("Arial", Font.BOLD, 18));
        btnCadastrar.addActionListener(cadastrarAction);

        add(btnComprar);
        add(btnConsultar);
        add(btnCadastrar);
    }
}