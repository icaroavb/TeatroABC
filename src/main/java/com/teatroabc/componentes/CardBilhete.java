package com.teatroabc.componentes;

import com.teatroabc.modelos.Bilhete;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.util.List;
import com.teatroabc.modelos.Assento;

public class CardBilhete extends JPanel {
    private ActionListener actionListener;

    public CardBilhete(Bilhete bilhete) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel("Peça: " + bilhete.getPeca().getTitulo());
        List<Assento> assentos = bilhete.getAssentos();
        String assentoCodigo = assentos.isEmpty() ? "N/A" : assentos.get(0).getCodigo();
        JLabel lblAssento = new JLabel("Assento: " + assentoCodigo);
        JLabel lblData = new JLabel("Data: " + bilhete.getPeca().getDataHora().toString());
        JLabel lblValor = new JLabel("Valor: R$ " + String.format("%.2f", bilhete.getValorTotal()));

        JPanel painelInfo = new JPanel(new GridLayout(4,1));
        painelInfo.add(lblTitulo);
        painelInfo.add(lblAssento);
        painelInfo.add(lblData);
        painelInfo.add(lblValor);

        add(painelInfo, BorderLayout.CENTER);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (actionListener != null) {
                    actionListener.actionPerformed(new ActionEvent(CardBilhete.this, ActionEvent.ACTION_PERFORMED, "clicked"));
                }
            }
        });
    }

    public void addActionListener(ActionListener listener) {
        this.actionListener = listener;
    }
}
