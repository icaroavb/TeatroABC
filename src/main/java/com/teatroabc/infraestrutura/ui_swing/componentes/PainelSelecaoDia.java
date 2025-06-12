package com.teatroabc.infraestrutura.ui_swing.componentes;

import com.teatroabc.dominio.modelos.Sessao;
import com.teatroabc.infraestrutura.ui_swing.constantes_ui.Constantes;
import com.teatroabc.infraestrutura.ui_swing.util.FormatadorData;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 * Componente que encapsula a exibição e seleção de sessões para um único dia.
 * Exibe a data formatada e os turnos/horários disponíveis como botões de rádio.
 */
public class PainelSelecaoDia extends JPanel {

    /**
     * Construtor do PainelSelecaoDia.
     * @param sessoesDoDia A lista de sessões disponíveis para este dia.
     * @param grupoRadios O ButtonGroup global para garantir a seleção única entre todos os dias.
     * @param onSessaoSelecionada O ActionListener a ser notificado quando uma sessão for selecionada.
     */
    public PainelSelecaoDia(List<Sessao> sessoesDoDia, ButtonGroup grupoRadios, ActionListener onSessaoSelecionada) {
        configurarPainel(sessoesDoDia.get(0)); // Usa a primeira sessão para obter a data para o título
        adicionarRadiosDeSessao(sessoesDoDia, grupoRadios, onSessaoSelecionada);
    }

    /**
     * Configura o layout e a borda do painel com o título da data.
     * @param umaSessaoDoDia Uma sessão qualquer do dia para extrair a data.
     */
    private void configurarPainel(Sessao umaSessaoDoDia) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        String tituloDoDia = formatarTituloDia(umaSessaoDoDia);
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Constantes.AZUL_CLARO, 1, true),
            tituloDoDia,
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 18), Constantes.AMARELO
        ));
    }

    /**
     * Cria e adiciona os botões de rádio para cada sessão do dia.
     */
    private void adicionarRadiosDeSessao(List<Sessao> sessoesDoDia, ButtonGroup grupoRadios, ActionListener onSessaoSelecionada) {
        JPanel painelHorarios = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        painelHorarios.setOpaque(false);

        for (Sessao sessao : sessoesDoDia) {
            JRadioButton radio = new JRadioButton(sessao.getTurno().toString());
            radio.setFont(Constantes.FONTE_BOTAO.deriveFont(18f));
            radio.setForeground(Color.WHITE);
            radio.setOpaque(false);
            radio.setCursor(new Cursor(Cursor.HAND_CURSOR));
            // Define o objeto Sessao como o "comando de ação" a ser enviado.
            radio.setActionCommand(sessao.getId());
            radio.addActionListener(onSessaoSelecionada);
            
            grupoRadios.add(radio);
            painelHorarios.add(radio);
        }
        
        add(painelHorarios);
    }
    
    /**
     * Formata o título para o bloco do dia (ex: "Sexta-feira, 20 de junho").
     * @param sessao Uma sessão daquele dia para extrair a data.
     * @return Uma string formatada.
     */
    private String formatarTituloDia(Sessao sessao) {
        Locale br = new Locale("pt", "BR");
        String diaDaSemana = sessao.getDataHora().getDayOfWeek().getDisplayName(TextStyle.FULL, br);
        String dataFormatada = FormatadorData.formatar(sessao.getDataHora(), "dd 'de' MMMM");
        // Capitaliza a primeira letra do dia da semana.
        return String.format("%s, %s", diaDaSemana.substring(0, 1).toUpperCase() + diaDaSemana.substring(1), dataFormatada);
    }
}