// Arquivo: com/teatroabc/TeatroABCApplication.java
package com.teatroabc;

// --- Portas de Saída (Interfaces dos Repositórios) ---
import com.teatroabc.infraestrutura.persistencia.interfaces.IClienteRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.IPecaRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.IAssentoRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.IBilheteRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.ISessaoRepositorio; 

// --- Adaptadores de Saída (Implementações Concretas dos Repositórios) ---
import com.teatroabc.infraestrutura.persistencia.implementacao.ClienteRepositorio;
import com.teatroabc.infraestrutura.persistencia.implementacao.PecaRepositorio;
import com.teatroabc.infraestrutura.persistencia.implementacao.AssentoRepositorio;
import com.teatroabc.infraestrutura.persistencia.implementacao.BilheteRepositorio;
import com.teatroabc.infraestrutura.persistencia.implementacao.SessaoRepositorio; 

// --- Portas de Entrada (Interfaces dos Serviços de Aplicação) ---
import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico;
import com.teatroabc.aplicacao.interfaces.ISessaoServico; 

// --- Implementações dos Serviços de Aplicação ---
import com.teatroabc.aplicacao.servicos.ClienteServico;
import com.teatroabc.aplicacao.servicos.PecaServico;
import com.teatroabc.aplicacao.servicos.ReservaServico;
import com.teatroabc.aplicacao.servicos.SessaoServico;

// --- Adaptador de Entrada Principal (UI) ---
import com.teatroabc.infraestrutura.ui_swing.telas.TelaPrincipal;

import javax.swing.*;

/**
 * Ponto de entrada principal da aplicação Teatro ABC.
 * Responsável por configurar o look and feel, instanciar e injetar as dependências
 * (repositórios nos serviços, e serviços na UI principal) e iniciar a interface gráfica.
 * Este é o "Composition Root" da aplicação, o único local onde as implementações
 * concretas são acopladas.
 */
public class TeatroABCApplication {

    public static void main(String[] args) {
        // Configuração inicial da UI (LookAndFeel)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Falha ao configurar o LookAndFeel do sistema: " + e.getMessage());
            e.printStackTrace();
        }

        // --- Montagem da Arquitetura e Injeção de Dependência REFATORADA ---

        // 1. Criação dos Adaptadores de Saída (Repositórios Concretos)
        IClienteRepositorio clienteRepositorio = new ClienteRepositorio();
        IAssentoRepositorio assentoRepositorio = new AssentoRepositorio();
        IPecaRepositorio pecaRepositorio = new PecaRepositorio();
        ISessaoRepositorio sessaoRepositorio = new SessaoRepositorio(pecaRepositorio);
        IBilheteRepositorio bilheteRepositorio = new BilheteRepositorio(clienteRepositorio, pecaRepositorio);

        // 2. Criação dos Serviços de Aplicação (Núcleo do Hexágono)
        IClienteServico clienteServico = new ClienteServico(clienteRepositorio);
        // PecaServico agora não depende mais de AssentoRepositorio
        IPecaServico pecaServico = new PecaServico(pecaRepositorio);
        IReservaServico reservaServico = new ReservaServico(bilheteRepositorio, assentoRepositorio);
        // SessaoServico agora depende de AssentoRepositorio
        ISessaoServico sessaoServico = new SessaoServico(sessaoRepositorio, assentoRepositorio);

        // 3. Criação e Início do Adaptador de Entrada Principal (UI Swing)
        SwingUtilities.invokeLater(() -> {
            TelaPrincipal telaPrincipal = new TelaPrincipal(
                clienteServico, 
                pecaServico, 
                reservaServico,
                sessaoServico
            );

            JFrame frame = new JFrame("Teatro ABC - Sistema de Bilheteria");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 900);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setLocationRelativeTo(null);            
            frame.setContentPane(telaPrincipal);
            frame.setVisible(true);
        });
    }
}
