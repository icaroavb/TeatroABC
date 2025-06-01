package com.teatroabc.telas;

import com.teatroabc.modelos.Bilhete;
import com.teatroabc.componentes.PainelCodigoBarras;
import javax.swing.*;
import java.awt.*;

public class DialogoDetalhesBilhete extends JDialog {
    public DialogoDetalhesBilhete(Bilhete bilhete) {
        setTitle("Detalhes do Bilhete");
        setSize(350, 300);
        setLayout(new BorderLayout());

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.add(new JLabel("Peça: " + bilhete.getPeca().getTitulo()));
        // Show first assento code or "N/A" if none
        String assentoCodigo = bilhete.getAssentos().isEmpty() ? "N/A" : bilhete.getAssentos().get(0).getCodigo();
        centro.add(new JLabel("Assento: " + assentoCodigo));
        centro.add(new JLabel("Cliente: " + bilhete.getCliente().getNome()));
        centro.add(new JLabel("Valor: R$ " + String.format("%.2f", bilhete.getValorTotal())));
        centro.add(new PainelCodigoBarras(bilhete.getCodigoBarras()));

        add(centro, BorderLayout.CENTER);

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());
        add(btnFechar, BorderLayout.SOUTH);

        setModal(true);
        setLocationRelativeTo(null);
    }
}
