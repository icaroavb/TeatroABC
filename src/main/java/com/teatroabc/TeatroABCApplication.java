package com.teatroabc;

// --- Portas de Saída (Interfaces dos Repositórios) ---
import com.teatroabc.infraestrutura.persistencia.interfaces.IClienteRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.IPecaRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.IAssentoRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.IBilheteRepositorio;

// --- Adaptadores de Saída (Implementações Concretas dos Repositórios) ---
import com.teatroabc.infraestrutura.persistencia.implementacao.ClienteRepositorio;
import com.teatroabc.infraestrutura.persistencia.implementacao.PecaRepositorio;
import com.teatroabc.infraestrutura.persistencia.implementacao.AssentoRepositorio;
import com.teatroabc.infraestrutura.persistencia.implementacao.BilheteRepositorio;

// --- Portas de Entrada (Interfaces dos Serviços de Aplicação) ---
import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico;

// --- Implementações dos Serviços de Aplicação ---
import com.teatroabc.aplicacao.servicos.ClienteServico;
import com.teatroabc.aplicacao.servicos.PecaServico;
import com.teatroabc.aplicacao.servicos.ReservaServico;

// --- Adaptador de Entrada Principal (UI) ---
import com.teatroabc.infraestrutura.ui_swing.telas.TelaPrincipal;

import javax.swing.*;

/**
 * Ponto de entrada principal da aplicação Teatro ABC.
 * Responsável por configurar o look and feel, instanciar e injetar as dependências
 * (repositórios nos serviços, e serviços na UI principal) e iniciar a interface gráfica.
 * Este é o "Composition Root" da aplicação para a injeção de dependência manual.
 */
public class TeatroABCApplication {

    public static void main(String[] args) {
        // Configuração inicial da UI (LookAndFeel)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Falha ao configurar o LookAndFeel do sistema: " + e.getMessage());
            // Considerar se a aplicação deve prosseguir ou terminar em caso de falha aqui.
            // e.printStackTrace(); // Útil para debug
        }

        // --- Montagem da Arquitetura e Injeção de Dependência ---

        // 1. Criação dos Adaptadores de Saída (Repositórios Concretos)
        // Estes são os componentes que interagem com a "infraestrutura externa" (arquivos de dados).
        IClienteRepositorio clienteRepositorio = new ClienteRepositorio();
        IPecaRepositorio pecaRepositorio = new PecaRepositorio(); // Atualmente não tem dependências externas
        IAssentoRepositorio assentoRepositorio = new AssentoRepositorio(); // Atualmente não tem dependências externas

        // BilheteRepositorio depende de outros repositórios (via interfaces) para reconstruir entidades.
        IBilheteRepositorio bilheteRepositorio = new BilheteRepositorio(clienteRepositorio, pecaRepositorio);

        // 2. Criação dos Serviços de Aplicação (Núcleo do Hexágono - Camada de Casos de Uso)
        // Os serviços recebem as interfaces dos repositórios (Portas de Saída) como dependências.
        IClienteServico clienteServico = new ClienteServico(clienteRepositorio);
        IPecaServico pecaServico = new PecaServico(pecaRepositorio, assentoRepositorio); // PecaServico agora precisa de IAssentoRepositorio
        IReservaServico reservaServico = new ReservaServico(bilheteRepositorio, assentoRepositorio);

        // 3. Criação e Início do Adaptador de Entrada Principal (UI Swing)
        // A TelaPrincipal recebe as interfaces dos serviços (Portas de Entrada) como dependências.
        final IClienteServico finalClienteServico = clienteServico;
        final IPecaServico finalPecaServico = pecaServico;
        final IReservaServico finalReservaServico = reservaServico;

        SwingUtilities.invokeLater(() -> {
            TelaPrincipal telaPrincipal = new TelaPrincipal(finalClienteServico, finalPecaServico, finalReservaServico);

            JFrame frame = new JFrame("Teatro ABC - Sistema de Bilheteria");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            // Definir um tamanho mínimo pode ser útil
            // frame.setMinimumSize(new Dimension(1200, 800));
            frame.setSize(1400, 900);
            frame.setLocationRelativeTo(null); // Centraliza na tela
            frame.setResizable(true);
            
            frame.setContentPane(telaPrincipal);
            frame.setVisible(true);
        });
    }
}