package com.teatroabc.telas;

import com.teatroabc.componentes.*;
import com.teatroabc.constantes.Constantes;
import com.teatroabc.enums.CategoriaAssento;
import com.teatroabc.enums.StatusAssento;
import com.teatroabc.modelos.Assento;
import com.teatroabc.modelos.Peca;
import com.teatroabc.repositorios.AssentoRepositorio;
import com.teatroabc.utilitarios.FormatadorMoeda;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TelaSelecionarAssento extends JPanel {
    private Peca peca;
    private List<Assento> assentosSelecionados;
    private List<Assento> todosAssentos;
    private AssentoRepositorio assentoRepo;
    private JLabel lblTotal;
    private BotaoAnimado btnConfirmar;

    public TelaSelecionarAssento(Peca peca) {
        this.peca = peca;
        this.assentosSelecionados = new ArrayList<>();
        this.assentoRepo = new AssentoRepositorio();
        this.todosAssentos = assentoRepo.buscarAssentosPorPeca(peca.getId());
        configurarTela();
    }

    private void configurarTela() {
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        // Cabeçalho
        add(criarCabecalho(), BorderLayout.NORTH);

        // Teatro
        add(criarPainelTeatro(), BorderLayout.CENTER);

        // Rodapé com total e botão
        add(criarRodape(), BorderLayout.SOUTH);
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(Constantes.AZUL_ESCURO);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Título com fundo laranja
        JLabel titulo = new JLabel("ESCOLHA O SEU ASSENTO");
        titulo.setFont(Constantes.FONTE_SUBTITULO);
        titulo.setForeground(Color.WHITE);
        titulo.setBackground(Constantes.LARANJA);
        titulo.setOpaque(true);
        titulo.setHorizontalAlignment(JLabel.CENTER);
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));

        cabecalho.add(titulo, BorderLayout.CENTER);
        cabecalho.add(new LogoTeatro(), BorderLayout.EAST);

        return cabecalho;
    }

    private JPanel criarPainelTeatro() {
        JPanel painelTeatro = new JPanel();
        painelTeatro.setBackground(Constantes.AZUL_ESCURO);
        painelTeatro.setLayout(new BoxLayout(painelTeatro, BoxLayout.Y_AXIS));

        // Palco
        JPanel palco = new JPanel();
        palco.setBackground(Constantes.BEGE);
        palco.setPreferredSize(new Dimension(700, 60));
        palco.setMaximumSize(new Dimension(700, 60));
        JLabel lblPalco = new JLabel("PALCO");
        lblPalco.setFont(new Font("Arial", Font.BOLD, 28));
        palco.add(lblPalco);
        palco.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelTeatro.add(Box.createVerticalStrut(20));
        painelTeatro.add(palco);
        painelTeatro.add(Box.createVerticalStrut(30));

        // Área principal (plateia visual)
        JPanel plateia = criarPlateia();
        plateia.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelTeatro.add(plateia);

        painelTeatro.add(Box.createVerticalStrut(30));

        // Seções de assentos
        JPanel secoes = criarSecoes();
        secoes.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelTeatro.add(secoes);

        painelTeatro.add(Box.createVerticalStrut(30));

        // Legenda
        JPanel legenda = criarLegenda();
        legenda.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelTeatro.add(legenda);

        return painelTeatro;
    }

    private JPanel criarPlateia() {
        JPanel plateia = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Desenhar formato trapezoidal
                int[] xPoints = {100, getWidth() - 100, getWidth() - 50, 50};
                int[] yPoints = {0, 0, getHeight(), getHeight()};

                g2d.setColor(new Color(139, 195, 74, 180));
                g2d.fillPolygon(xPoints, yPoints, 4);

                g2d.dispose();
            }
        };
        plateia.setPreferredSize(new Dimension(800, 300));
        plateia.setOpaque(false);
        return plateia;
    }

    private JPanel criarSecoes() {
        JPanel secoes = new JPanel(new GridLayout(1, 3, 30, 0));
        secoes.setBackground(Constantes.AZUL_ESCURO);
        secoes.setMaximumSize(new Dimension(1000, 200));

        // Frisas
        secoes.add(criarSecao("FRISAS", CategoriaAssento.FRISAS, 10, 8));

        // Balcão Nobre
        secoes.add(criarSecao("BALCÃO NOBRE", CategoriaAssento.BALCAO_NOBRE, 10, 10));

        // Balcão
        secoes.add(criarSecao("BALCÃO", CategoriaAssento.BALCAO, 10, 10));

        return secoes;
    }

    private JPanel criarSecao(String nome, CategoriaAssento categoria, int fileiras, int assentosPorFileira) {
        JPanel secao = new JPanel(new BorderLayout());
        secao.setBackground(Constantes.AZUL_ESCURO);

        // Nome da seção
        JLabel lblNome = new JLabel(nome, SwingConstants.CENTER);
        lblNome.setForeground(Color.WHITE);
        lblNome.setFont(new Font("Arial", Font.BOLD, 16));
        secao.add(lblNome, BorderLayout.NORTH);

        // Container dos assentos
        JPanel containerAssentos = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(Constantes.BEGE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2d.dispose();
            }
        };
        containerAssentos.setLayout(new GridLayout(fileiras, assentosPorFileira, 3, 3));
        containerAssentos.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        containerAssentos.setOpaque(false);

        // Adicionar assentos usando os dados persistidos
        for (int f = 1; f <= fileiras; f++) {
            for (int a = 1; a <= assentosPorFileira; a++) {
                String codigo = obterPrefixoCategoria(categoria) + f + "-" + a;
                
                // Buscar assento nos dados carregados
                Assento assento = buscarAssentoPorCodigo(codigo);
                if (assento == null) {
                    // Se não existe, criar novo
                    assento = new Assento(codigo, f, a, categoria);
                    todosAssentos.add(assento);
                }

                BotaoAssento botao = new BotaoAssento(assento);
                
                final Assento assentoFinal = assento;
                botao.addActionListener(e -> {
                    if (assentoFinal.getStatus() == StatusAssento.SELECIONADO) {
                        assentoFinal.setStatus(StatusAssento.DISPONIVEL);
                        assentosSelecionados.remove(assentoFinal);
                    } else if (assentoFinal.getStatus() == StatusAssento.DISPONIVEL) {
                        assentoFinal.setStatus(StatusAssento.SELECIONADO);
                        assentosSelecionados.add(assentoFinal);
                    }
                    botao.repaint();
                    atualizarTotal();
                });

                containerAssentos.add(botao);
            }
        }

        secao.add(containerAssentos, BorderLayout.CENTER);
        return secao;
    }

    private String obterPrefixoCategoria(CategoriaAssento categoria) {
        switch (categoria) {
            case FRISAS:
                return "F";
            case BALCAO_NOBRE:
                return "B";
            case BALCAO:
                return "C";
            default:
                return "X";
        }
    }

    private Assento buscarAssentoPorCodigo(String codigo) {
        return todosAssentos.stream()
                .filter(a -> a.getCodigo().equals(codigo))
                .findFirst()
                .orElse(null);
    }

    private JPanel criarLegenda() {
        JPanel legenda = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        legenda.setBackground(Constantes.AZUL_ESCURO);

        adicionarItemLegenda(legenda, "Disponível", Constantes.ASSENTO_DISPONIVEL);
        adicionarItemLegenda(legenda, "Ocupado", Constantes.ASSENTO_OCUPADO);
        adicionarItemLegenda(legenda, "Selecionado", Constantes.ASSENTO_SELECIONADO);

        return legenda;
    }

    private void adicionarItemLegenda(JPanel painel, String texto, Color cor) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        item.setBackground(Constantes.AZUL_ESCURO);

        JPanel circulo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(cor);
                g2d.fillOval(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        circulo.setPreferredSize(new Dimension(25, 25));
        circulo.setBackground(Constantes.AZUL_ESCURO);

        JLabel label = new JLabel(texto);
        label.setForeground(Color.WHITE);
        label.setFont(Constantes.FONTE_LABEL);

        item.add(circulo);
        item.add(label);
        painel.add(item);
    }

    private JPanel criarRodape() {
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBackground(Constantes.AZUL_ESCURO);
        rodape.setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));

        // Botão Voltar
        JButton btnVoltar = new JButton("VOLTAR");
        btnVoltar.setFont(new Font("Arial", Font.PLAIN, 18));
        btnVoltar.setForeground(Constantes.AZUL_CLARO);
        btnVoltar.setBackground(Constantes.AZUL_ESCURO);
        btnVoltar.setBorderPainted(false);
        btnVoltar.setFocusPainted(false);
        btnVoltar.setContentAreaFilled(false);
        btnVoltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltar.addActionListener(e -> voltar());

        // Painel do total e botão confirmar
        JPanel painelDireita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        painelDireita.setBackground(Constantes.AZUL_ESCURO);

        // Total
        JPanel painelTotal = new JPanel();
        painelTotal.setBackground(new Color(52, 73, 94));
        painelTotal.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        lblTotal = new JLabel("TOTAL: R$ 0,00");
        lblTotal.setForeground(Color.WHITE);
        lblTotal.setFont(new Font("Arial", Font.BOLD, 20));
        painelTotal.add(lblTotal);

        // Botão Confirmar
        btnConfirmar = new BotaoAnimado("CONFIRMAR\nCOMPRA",
                Constantes.LARANJA, Constantes.AMARELO, new Dimension(200, 80));
        btnConfirmar.setEnabled(false);
        btnConfirmar.addActionListener(e -> confirmarCompra());

        painelDireita.add(painelTotal);
        painelDireita.add(btnConfirmar);

        rodape.add(btnVoltar, BorderLayout.WEST);
        rodape.add(painelDireita, BorderLayout.EAST);

        return rodape;
    }

    private void atualizarTotal() {
        double total = assentosSelecionados.stream()
                .mapToDouble(Assento::getPreco)
                .sum();

        lblTotal.setText("TOTAL: " + FormatadorMoeda.formatar(total));
        btnConfirmar.setEnabled(!assentosSelecionados.isEmpty());
    }

    private void confirmarCompra() {
        if (!assentosSelecionados.isEmpty()) {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setContentPane(new TelaInformarCPF(false, peca, new ArrayList<>(assentosSelecionados)));
            frame.revalidate();
            frame.repaint();
        }
    }

    private void voltar() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.setContentPane(new TelaSelecionarPeca());
        frame.revalidate();
        frame.repaint();
    }
}