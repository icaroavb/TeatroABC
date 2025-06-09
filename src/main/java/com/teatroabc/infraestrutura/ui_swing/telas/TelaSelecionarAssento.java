package com.teatroabc.infraestrutura.ui_swing.telas;

import com.teatroabc.infraestrutura.ui_swing.componentes.*;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.enums.CategoriaAssento;
import com.teatroabc.dominio.enums.StatusAssento;
import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.modelos.Peca;
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorMoeda;
import com.teatroabc.infraestrutura.persistencia.util.GerenciadorArquivos;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TelaSelecionarAssento extends JPanel {
    private Peca peca;
    private List<Assento> assentosSelecionados;
    private Map<Turno, List<Assento>> assentosPorTurno;
    private Turno turnoSelecionado;
    private JLabel lblTotal;
    private BotaoAnimado btnConfirmar;
    private JPanel painelSecoes;
    private ButtonGroup grupoTurnos;

    public TelaSelecionarAssento(Peca peca) {
        this.peca = peca;
        this.assentosSelecionados = new ArrayList<>();
        this.turnoSelecionado = Turno.NOITE; // Turno padrão
        this.assentosPorTurno = new HashMap<>();
        
        // Carregar assentos para cada turno com ocupação real baseada no arquivo
        carregarAssentosPorTurno();
        configurarTela();
    }

    private void carregarAssentosPorTurno() {
        System.out.println("Carregando assentos para peça: " + peca.getId());
        
        for (Turno turno : Turno.values()) {
            List<Assento> assentosTurno = new ArrayList<>();
            
            // Buscar assentos ocupados para este turno específico
            Set<String> assentosOcupados = GerenciadorArquivos.buscarAssentosOcupados(peca.getId(), turno.name());
            
            // Criar todos os assentos para este turno
            // Frisas - 3 fileiras x 8 assentos
            for (int f = 1; f <= 3; f++) {
                for (int a = 1; a <= 8; a++) {
                    String codigo = "F" + f + "-" + a;
                    Assento assento = new Assento(codigo, f, a, CategoriaAssento.FRISAS);
                    
                    // Verificar se está ocupado no arquivo
                    if (assentosOcupados.contains(codigo)) {
                        assento.setStatus(StatusAssento.OCUPADO);
                        System.out.println("Assento " + codigo + " ocupado no turno " + turno.name());
                    } else {
                        // Se não está no arquivo, simular ocupação baseada em padrão determinístico
                        if (isAssentoOcupadoSimulacao(codigo, turno)) {
                            assento.setStatus(StatusAssento.OCUPADO);
                        }
                    }
                    
                    assentosTurno.add(assento);
                }
            }
            
            // Balcão Nobre - 4 fileiras x 10 assentos
            for (int f = 1; f <= 4; f++) {
                for (int a = 1; a <= 10; a++) {
                    String codigo = "B" + f + "-" + a;
                    Assento assento = new Assento(codigo, f, a, CategoriaAssento.BALCAO_NOBRE);
                    
                    if (assentosOcupados.contains(codigo)) {
                        assento.setStatus(StatusAssento.OCUPADO);
                        System.out.println("Assento " + codigo + " ocupado no turno " + turno.name());
                    } else if (isAssentoOcupadoSimulacao(codigo, turno)) {
                        assento.setStatus(StatusAssento.OCUPADO);
                    }
                    
                    assentosTurno.add(assento);
                }
            }
            
            // Balcão - 4 fileiras x 10 assentos
            for (int f = 1; f <= 4; f++) {
                for (int a = 1; a <= 10; a++) {
                    String codigo = "C" + f + "-" + a;
                    Assento assento = new Assento(codigo, f, a, CategoriaAssento.BALCAO);
                    
                    if (assentosOcupados.contains(codigo)) {
                        assento.setStatus(StatusAssento.OCUPADO);
                        System.out.println("Assento " + codigo + " ocupado no turno " + turno.name());
                    } else if (isAssentoOcupadoSimulacao(codigo, turno)) {
                        assento.setStatus(StatusAssento.OCUPADO);
                    }
                    
                    assentosTurno.add(assento);
                }
            }
            
            assentosPorTurno.put(turno, assentosTurno);
            
            long ocupados = assentosTurno.stream().mapToLong(a -> a.getStatus() == StatusAssento.OCUPADO ? 1 : 0).sum();
            System.out.println("Turno " + turno.name() + ": " + ocupados + " assentos ocupados de " + assentosTurno.size());
        }
    }

    // Método para simular ocupação quando não há dados reais
    private boolean isAssentoOcupadoSimulacao(String codigo, Turno turno) {
        // Criar um hash único baseado no código do assento, peça e turno
        int hash = (codigo + peca.getId() + turno.name()).hashCode();
        
        // Usar o hash para criar padrões diferentes para cada turno
        double probabilidade = getProbabilidadeOcupacao(turno);
        
        // Usar o hash para criar uma decisão determinística mas aparentemente aleatória
        return (Math.abs(hash) % 100) < (probabilidade * 100);
    }

    private double getProbabilidadeOcupacao(Turno turno) {
        // Diferentes probabilidades de ocupação por turno (mais realista)
        switch (turno) {
            case MANHA:
                return 0.15; // 15% ocupado de manhã
            case TARDE:
                return 0.35; // 35% ocupado à tarde
            case NOITE:
                return 0.55; // 55% ocupado à noite (mais popular)
            default:
                return 0.25;
        }
    }

    private void configurarTela() {
        setLayout(new BorderLayout());
        setBackground(Constantes.AZUL_ESCURO);

        // Container principal com painel lateral
        JPanel containerPrincipal = new JPanel(new BorderLayout());
        containerPrincipal.setBackground(Constantes.AZUL_ESCURO);

        // Cabeçalho
        containerPrincipal.add(criarCabecalho(), BorderLayout.NORTH);

        // Painel central com teatro
        JPanel painelCentral = new JPanel(new BorderLayout());
        painelCentral.setBackground(Constantes.AZUL_ESCURO);
        painelCentral.add(criarPainelTeatro(), BorderLayout.CENTER);

        containerPrincipal.add(painelCentral, BorderLayout.CENTER);

        // Painel lateral direito para seleção de turno
        containerPrincipal.add(criarPainelTurnos(), BorderLayout.EAST);

        add(containerPrincipal, BorderLayout.CENTER);

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

    private JPanel criarPainelTurnos() {
        JPanel painelTurnos = new JPanel();
        painelTurnos.setBackground(new Color(52, 73, 94));
        painelTurnos.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        painelTurnos.setPreferredSize(new Dimension(200, 0));
        painelTurnos.setLayout(new BoxLayout(painelTurnos, BoxLayout.Y_AXIS));

        // Título
        JLabel lblTitulo = new JLabel("TURNOS");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelTurnos.add(lblTitulo);
        painelTurnos.add(Box.createVerticalStrut(30));

        // Grupo de botões
        grupoTurnos = new ButtonGroup();

        // Criar botões para cada turno
        for (Turno turno : Turno.values()) {
            JRadioButton radioTurno = criarRadioTurno(turno);
            grupoTurnos.add(radioTurno);
            painelTurnos.add(radioTurno);
            painelTurnos.add(Box.createVerticalStrut(15));
            
            // Selecionar turno noite por padrão
            if (turno == Turno.NOITE) {
                radioTurno.setSelected(true);
            }
        }

        // Informação sobre disponibilidade
        painelTurnos.add(Box.createVerticalStrut(30));
        JLabel lblInfo = new JLabel("<html><center>A disponibilidade<br>varia por turno</center></html>");
        lblInfo.setFont(new Font("Arial", Font.ITALIC, 14));
        lblInfo.setForeground(Color.LIGHT_GRAY);
        lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelTurnos.add(lblInfo);

        return painelTurnos;
    }

    private JRadioButton criarRadioTurno(Turno turno) {
        JRadioButton radio = new JRadioButton(turno.toString());
        radio.setFont(new Font("Arial", Font.PLAIN, 16));
        radio.setForeground(Color.WHITE);
        radio.setBackground(new Color(52, 73, 94));
        radio.setFocusPainted(false);
        radio.setCursor(new Cursor(Cursor.HAND_CURSOR));
        radio.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Ícone personalizado
        radio.setIcon(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(x, y, 16, 16);
                g2d.dispose();
            }
            @Override
            public int getIconWidth() { return 16; }
            @Override
            public int getIconHeight() { return 16; }
        });
        
        radio.setSelectedIcon(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Constantes.LARANJA);
                g2d.fillOval(x, y, 16, 16);
                g2d.setColor(Color.WHITE);
                g2d.fillOval(x + 4, y + 4, 8, 8);
                g2d.dispose();
            }
            @Override
            public int getIconWidth() { return 16; }
            @Override
            public int getIconHeight() { return 16; }
        });

        radio.addActionListener(e -> {
            Turno turnoAnterior = turnoSelecionado;
            turnoSelecionado = turno;
            
            // Resetar seleções do turno anterior
            if (turnoAnterior != turnoSelecionado) {
                List<Assento> assentosAnteriores = assentosPorTurno.get(turnoAnterior);
                if (assentosAnteriores != null) {
                    assentosAnteriores.forEach(assento -> {
                        if (assento.getStatus() == StatusAssento.SELECIONADO) {
                            assento.setStatus(StatusAssento.DISPONIVEL);
                        }
                    });
                }
                
                // Limpar lista de selecionados
                assentosSelecionados.clear();
            }
            
            // Recriar seções com novos assentos
            atualizarSecoes();
            atualizarTotal();
        });

        return radio;
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

        // Container para as seções
        painelSecoes = new JPanel();
        painelSecoes.setBackground(Constantes.AZUL_ESCURO);
        painelSecoes.setAlignmentX(Component.CENTER_ALIGNMENT);
        atualizarSecoes();
        painelTeatro.add(painelSecoes);

        painelTeatro.add(Box.createVerticalStrut(30));

        // Legenda
        JPanel legenda = criarLegenda();
        legenda.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelTeatro.add(legenda);

        return painelTeatro;
    }

    private void atualizarSecoes() {
        painelSecoes.removeAll();
        painelSecoes.setLayout(new GridLayout(1, 3, 30, 0));
        painelSecoes.setMaximumSize(new Dimension(1000, 200));

        List<Assento> assentosTurno = assentosPorTurno.get(turnoSelecionado);

        // Frisas
        painelSecoes.add(criarSecao("FRISAS", CategoriaAssento.FRISAS, 3, 8, assentosTurno));

        // Balcão Nobre
        painelSecoes.add(criarSecao("BALCÃO NOBRE", CategoriaAssento.BALCAO_NOBRE, 4, 10, assentosTurno));

        // Balcão
        painelSecoes.add(criarSecao("BALCÃO", CategoriaAssento.BALCAO, 4, 10, assentosTurno));

        painelSecoes.revalidate();
        painelSecoes.repaint();
    }

    private JPanel criarSecao(String nome, CategoriaAssento categoria, int fileiras, int assentosPorFileira, List<Assento> assentosTurno) {
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

        // Adicionar assentos
        for (int f = 1; f <= fileiras; f++) {
            for (int a = 1; a <= assentosPorFileira; a++) {
                String codigo = obterPrefixoCategoria(categoria) + f + "-" + a;
                
                // Buscar assento específico do turno
                Assento assento = buscarAssentoPorCodigo(codigo, assentosTurno);
                
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

    private Assento buscarAssentoPorCodigo(String codigo, List<Assento> assentos) {
        return assentos.stream()
                .filter(a -> a.getCodigo().equals(codigo))
                .findFirst()
                .orElse(null);
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
            
            // Criar nova tela passando o turno selecionado
            TelaInformarCPF telaInformarCPF = new TelaInformarCPF(false, peca, new ArrayList<>(assentosSelecionados));
            telaInformarCPF.setTurnoSelecionado(turnoSelecionado);
            
            frame.setContentPane(telaInformarCPF);
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