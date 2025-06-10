package com.teatroabc.infraestrutura.ui_swing.componentes;

// Assumindo que Constantes foi movido para este pacote ou um subpacote de ui_swing
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.enums.StatusAssento;
import com.teatroabc.dominio.modelos.Assento;
import javax.swing.*;
import java.awt.*;

/**
 * Componente JButton customizado para representar visualmente um Assento e seu estado.
 * Este componente é um Adaptador de UI, responsável por traduzir o estado do
 * modelo de domínio Assento em uma representação gráfica.
 */
public class BotaoAssento extends JButton {
    private final Assento assento; // Tornar final, pois não deve mudar após construção do botão
    private static final int TAMANHO_BOTAO = 30; // Renomeado de TAMANHO para clareza

    /**
     * Construtor para BotaoAssento.
     * @param assento O objeto de domínio Assento que este botão representa. Não pode ser nulo.
     * @throws IllegalArgumentException se o assento for nulo.
     */
    public BotaoAssento(Assento assento) {
        if (assento == null) {
            throw new IllegalArgumentException("Objeto Assento não pode ser nulo para criar BotaoAssento.");
        }
        this.assento = assento;
        setPreferredSize(new Dimension(TAMANHO_BOTAO, TAMANHO_BOTAO));
        setFocusPainted(false);
        setBorderPainted(false); // O desenho customizado cuidará da borda
        setContentAreaFilled(false); // O paintComponent desenhará o fundo

        // Define o cursor e a interatividade com base no status inicial do assento
        atualizarInteratividadeBaseadaNoStatus();
        // Não é necessário chamar atualizarAparencia() aqui, pois o setPreferredSize e
        // a primeira pintura já farão o repaint. Se fosse mudar o status dinamicamente
        // por fora, aí sim precisaria de um método para forçar o repaint.
    }

    /**
     * Atualiza a interatividade do botão (cursor, habilitado/desabilitado)
     * com base no status atual do assento.
     * Este método pode ser chamado se o status do assento for alterado externamente
     * e o botão precisar refletir essa mudança na sua interatividade (além da cor).
     */
    public void atualizarInteratividadeBaseadaNoStatus() {
        if (this.assento.getStatus() != StatusAssento.OCUPADO) {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setEnabled(true); // Garante que esteja habilitado se não ocupado
        } else {
            setCursor(Cursor.getDefaultCursor()); // Cursor padrão para assentos ocupados/desabilitados
            setEnabled(false); // Desabilita interação para assentos ocupados
        }
        repaint(); // Repinta para refletir qualquer mudança visual associada ao enable/disable
    }

    // O método atualizarAparencia() foi incorporado/substituído por repaint() chamado
    // quando necessário, e a lógica de cor está em paintComponent.

    /**
     * {@inheritDoc}
     * Desenha a aparência customizada do botão de assento, representando seu status
     * (Disponível, Ocupado, Selecionado) com cores distintas.
     */
    @Override
    protected void paintComponent(Graphics g) {
        // Não chamar super.paintComponent(g) se quisermos controle total do desenho
        // e setContentAreaFilled(false) estiver ativo.
        // Se chamarmos, ele pode tentar desenhar o fundo padrão do JButton antes do nosso.
        // Para este caso, como estamos preenchendo toda a área com fillOval,
        // não chamar super.paintComponent(g) é geralmente seguro e evita artefatos.

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color corDePreenchimento;
        Color corDaBorda;

        // Define as cores com base no status do assento
        // Esta é a lógica de mapeamento do estado do domínio para a apresentação visual.
        switch (this.assento.getStatus()) {
            case DISPONIVEL:
                corDePreenchimento = Constantes.AZUL_CLARO; // Cor da UI para 'disponível'
                corDaBorda = Constantes.AZUL_CLARO.darker();
                break;
            case OCUPADO:
                corDePreenchimento = Constantes.BEGE;       // Cor da UI para 'ocupado'
                corDaBorda = Constantes.BEGE.darker();
                break;
            case SELECIONADO:
                corDePreenchimento = Constantes.VERDE;      // Cor da UI para 'selecionado'
                corDaBorda = Constantes.VERDE.darker();
                break;
            default: // Fallback para um status inesperado
                corDePreenchimento = Color.GRAY;
                corDaBorda = Color.DARK_GRAY;
                break;
        }
        
        // Se o botão estiver desabilitado (ex: assento OCUPADO), podemos querer uma aparência diferente
        if (!isEnabled()) {
            // Exemplo: usar uma versão mais esmaecida da cor de preenchimento para assentos ocupados
            // ou uma cor cinza específica.
            // corDePreenchimento = new Color(corDePreenchimento.getRed(), corDePreenchimento.getGreen(), corDePreenchimento.getBlue(), 150); // Mais transparente
            corDePreenchimento = Constantes.CINZA_ESCURO.brighter(); // Ou uma cor cinza específica de 'Constantes'
            corDaBorda = Color.DARK_GRAY;
        }

        // Desenhar o círculo (fundo do assento)
        // Usar getWidth() e getHeight() para preencher o componente.
        // O padding de 2 pixels em cada lado é para a borda não ser cortada.
        g2d.setColor(corDePreenchimento);
        g2d.fillOval(1, 1, getWidth() - 3, getHeight() - 3); // Ajustado para borda de 1px
        
        // Desenhar a borda do círculo
        g2d.setColor(corDaBorda);
        g2d.setStroke(new BasicStroke(1.5f)); // Borda um pouco mais espessa
        g2d.drawOval(1, 1, getWidth() - 3, getHeight() - 3);
        
        g2d.dispose();
    }

    /**
     * Retorna o objeto Assento associado a este botão.
     * @return O Assento.
     */
    public Assento getAssento() {
        return assento;
    }
}