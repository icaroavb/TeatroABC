package com.teatroabc.infraestrutura.ui_swing.componentes;

import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.dominio.enums.StatusAssento;
import com.teatroabc.dominio.modelos.Assento;
import javax.swing.*;
import java.awt.*;

/**
 * Componente JButton customizado para representar visualmente um Assento e seu estado.
 * Este componente é um Adaptador de UI, responsável por traduzir o estado do
 * modelo de domínio Assento em uma representação gráfica interativa (um botão circular).
 * A cor e a interatividade do botão mudam de acordo com o status do assento
 * (Disponível, Ocupado, Selecionado).
 */
public class BotaoAssento extends JButton {
    private final Assento assento; // A entidade de domínio que este botão representa.
    private static final int TAMANHO_BOTAO = 30; // Define o tamanho padrão do botão.

    /**
     * Construtor para BotaoAssento.
     * @param assento O objeto de domínio Assento que este botão representa. Não pode ser nulo.
     * @throws IllegalArgumentException se o objeto assento for nulo.
     */
    public BotaoAssento(Assento assento) {
        this.assento = assento;
        
        apurarValidacaoAssento();

        // Configurações visuais básicas do botão.
        setPreferredSize(new Dimension(TAMANHO_BOTAO, TAMANHO_BOTAO));
        setFocusPainted(false);
        setBorderPainted(false); // A borda será desenhada manualmente.
        setContentAreaFilled(false); // O fundo será desenhado manualmente.

        // Define a interatividade inicial com base no status do assento.
        atualizarInteratividadeBaseadaNoStatus();
    }

    //encapsulamento da logica de validação do assento
    private boolean verificarAssentoNull (Assento assento){
        return assento == null;
    }
    //encapsulamento da lógica total da validação de asssentos
    private void apurarValidacaoAssento (){
        if (verificarAssentoNull(assento)) {
            throw new IllegalArgumentException("Objeto Assento não pode ser nulo para criar um BotaoAssento.");
        }
    }

    /**
     * Atualiza a interatividade do botão (cursor, habilitado/desabilitado) com base
     * no status atual do assento associado. Este método deve ser chamado se o status
     * do assento for alterado externamente para que a UI reflita a mudança.
     */
    public void atualizarInteratividadeBaseadaNoStatus() {
        
        if (verificarStatusOcupado(assento)) {
            // Se o assento está ocupado, ele não é clicável.
            setCursor(Cursor.getDefaultCursor());
            setEnabled(false);
        } else {
            // Para assentos disponíveis ou selecionados, permite a interação.
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setEnabled(true);
        }
        repaint(); // Força a repintura para refletir qualquer mudança visual.
    }

    //encapsulamento da lógica de assentos ocupados
    private boolean verificarStatusOcupado (Assento assento){
        return assento.getStatus().equals(StatusAssento.OCUPADO);
    }
    

    /**
     * {@inheritDoc}
     * Sobrescreve o método de pintura para desenhar a aparência customizada do botão de assento.
     * Desenha um círculo preenchido com uma cor que representa o status do assento
     * (Disponível, Ocupado, Selecionado).
     */
    @Override
    protected void paintComponent(Graphics g) {
        // Não chamamos super.paintComponent(g) para ter controle total do desenho.
        
        Graphics2D g2d = (Graphics2D) g.create();
        // Habilita o anti-aliasing para um desenho mais suave das bordas.
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color corDePreenchimento;
        Color corDaBorda;

        // Lógica de tradução: mapeia o estado do domínio para a apresentação visual.
        switch (this.assento.getStatus()) {
            case DISPONIVEL:
                corDePreenchimento = Constantes.AZUL_CLARO;
                corDaBorda = Constantes.AZUL_CLARO.darker();
                break;
            case OCUPADO:
                // Mesmo que o botão esteja desabilitado, definimos a cor para 'ocupado'.
                corDePreenchimento = Constantes.BEGE;
                corDaBorda = Constantes.BEGE.darker();
                break;
            case SELECIONADO:
                corDePreenchimento = Constantes.VERDE;
                corDaBorda = Constantes.VERDE.darker();
                break;
            default: // Cor de fallback para um estado inesperado.
                corDePreenchimento = Color.GRAY;
                corDaBorda = Color.DARK_GRAY;
                break;
        }
        
        // Se o botão estiver desabilitado (o que acontece para assentos OCUPADOS),
        // usa uma cor mais esmaecida para indicar visualmente a inatividade.
        if (!isEnabled()) {
            corDePreenchimento = Constantes.CINZA_ESCURO.brighter();
            corDaBorda = Color.DARK_GRAY;
        }

        // Desenha o círculo que representa o assento.
        g2d.setColor(corDePreenchimento);
        g2d.fillOval(1, 1, getWidth() - 3, getHeight() - 3);
        
        // Desenha a borda do círculo.
        g2d.setColor(corDaBorda);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawOval(1, 1, getWidth() - 3, getHeight() - 3);
        
        g2d.dispose();
    }

    /**
     * Retorna o objeto de domínio Assento associado a este botão.
     * @return O Assento.
     */
    public Assento getAssento() {
        return assento;
    }
}