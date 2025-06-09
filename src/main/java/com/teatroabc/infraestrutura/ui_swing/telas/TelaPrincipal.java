package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.infraestrutura.ui_swing.componentes.*;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.modelos.Peca;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

public class TelaPrincipal extends JPanel {

    public TelaPrincipal() {
        configurarTela();
    }

    private void configurarTela() {
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        // Container principal
        JPanel containerPrincipal = new JPanel();
        containerPrincipal.setLayout(new BoxLayout(containerPrincipal, BoxLayout.Y_AXIS));
        containerPrincipal.setBackground(Constantes.AZUL_ESCURO);

        // Logo
        JPanel painelLogo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelLogo.setBackground(Constantes.AZUL_ESCURO);
        painelLogo.add(new LogoTeatro());
        containerPrincipal.add(painelLogo);

        // Espaço
        containerPrincipal.add(Box.createVerticalStrut(50));

        // Cards das peças
        JPanel painelPecas = new JPanel(new GridLayout(1, 3, 30, 0));
        painelPecas.setBackground(Constantes.AZUL_ESCURO);
        painelPecas.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));

        // Criar cards das peças
        adicionarCardsPecas(painelPecas);

        containerPrincipal.add(painelPecas);

        // Espaço
        containerPrincipal.add(Box.createVerticalStrut(80));

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        painelBotoes.setBackground(Constantes.AZUL_ESCURO);

        // Botão Comprar Bilhete
        BotaoAnimado btnComprar = new BotaoAnimado("COMPRAR BILHETE",
                Constantes.LARANJA, Constantes.AMARELO, new Dimension(450, 80));
        btnComprar.setFont(Constantes.FONTE_BOTAO);
        btnComprar.addActionListener(e -> abrirSelecaoPeca());

        // Botão Consultar Bilhete
        BotaoAnimado btnConsultar = new BotaoAnimado("CONSULTAR BILHETE",
                Constantes.CINZA_ESCURO, Constantes.AZUL_CLARO, new Dimension(280, 60));
        btnConsultar.setFont(new Font("Arial", Font.BOLD, 20));
        btnConsultar.addActionListener(e -> abrirConsultaBilhete());

        // Botão Cadastrar
        BotaoAnimado btnCadastrar = new BotaoAnimado("CADASTRAR",
                Constantes.CINZA_ESCURO, Constantes.AZUL_CLARO, new Dimension(200, 60));
        btnCadastrar.setFont(new Font("Arial", Font.BOLD, 20));
        btnCadastrar.addActionListener(e -> abrirCadastro());

        painelBotoes.add(btnComprar);
        painelBotoes.add(btnConsultar);
        painelBotoes.add(btnCadastrar);

        containerPrincipal.add(painelBotoes);

        add(containerPrincipal, BorderLayout.CENTER);
    }

    private void adicionarCardsPecas(JPanel painel) {
        // Wickedonia
        Peca wickedonia = new Peca(
                "WICKEDONIA",
                "A PARÓDIA MUSICAL",
                "Uma paródia hilária do famoso musical",
                new Color(60, 179, 113),
                "/imagens/wickedonia.png",
                LocalDateTime.of(2024, 4, 25, 20, 0)
        );

        // Hermanoteu
        Peca hermanoteu = new Peca(
                "HERMANOTEU",
                "",
                "Comédia com os personagens mais queridos do humor",
                Constantes.LARANJA,
                "/imagens/hermanoteu.png",
                LocalDateTime.of(2024, 5, 2, 21, 0)
        );

        // Morte e Vida Severina
        Peca morteVida = new Peca(
                "MORTE E VIDA SEVERINA",
                "",
                "Clássico de João Cabral de Melo Neto",
                Constantes.VERMELHO,
                "/imagens/morte_vida_severina.jpg",
                LocalDateTime.of(2024, 5, 10, 19, 30)
        );

        painel.add(new CardPeca(wickedonia));
        painel.add(new CardPeca(hermanoteu));
        painel.add(new CardPeca(morteVida));
    }

    private void abrirSelecaoPeca() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaSelecionarPeca());
        frame.revalidate();
        frame.repaint();
    }

    private void abrirConsultaBilhete() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaInformarCPF(true)); // true = modo consulta
        frame.revalidate();
        frame.repaint();
    }

    private void abrirCadastro() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaCadastrar(null));
        frame.revalidate();
        frame.repaint();
    }
}

