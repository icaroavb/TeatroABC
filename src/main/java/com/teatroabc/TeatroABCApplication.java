package com.teatroabc;

// --- Portas de Saída (Interfaces dos Repositórios) ---
import com.teatroabc.infraestrutura.persistencia.interfaces.IClienteRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.IPecaRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.IAssentoRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.IBilheteRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.ISessaoRepositorio; // NOVO IMPORT

// --- Adaptadores de Saída (Implementações Concretas dos Repositórios) ---
import com.teatroabc.infraestrutura.persistencia.implementacao.ClienteRepositorio;
import com.teatroabc.infraestrutura.persistencia.implementacao.PecaRepositorio;
import com.teatroabc.infraestrutura.persistencia.implementacao.AssentoRepositorio;
import com.teatroabc.infraestrutura.persistencia.implementacao.BilheteRepositorio;
import com.teatroabc.infraestrutura.persistencia.implementacao.SessaoRepositorio; // NOVO IMPORT

// --- Portas de Entrada (Interfaces dos Serviços de Aplicação) ---
import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico;
import com.teatroabc.aplicacao.interfaces.ISessaoServico; // NOVO IMPORT

// --- Implementações dos Serviços de Aplicação ---
import com.teatroabc.aplicacao.servicos.ClienteServico;
import com.teatroabc.aplicacao.servicos.PecaServico;
import com.teatroabc.aplicacao.servicos.ReservaServico;
import com.teatroabc.aplicacao.servicos.SessaoServico; // NOVO IMPORT

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

        // --- Montagem da Arquitetura e Injeção de Dependência ---

        // 1. Criação dos Adaptadores de Saída (Repositórios Concretos)
        IClienteRepositorio clienteRepositorio = new ClienteRepositorio();
        IAssentoRepositorio assentoRepositorio = new AssentoRepositorio();
        
        // O PecaRepositorio agora é uma dependência para outros repositórios.
        IPecaRepositorio pecaRepositorio = new PecaRepositorio();
        
        // O novo SessaoRepositorio depende do PecaRepositorio para construir as sessões.
        ISessaoRepositorio sessaoRepositorio = new SessaoRepositorio(pecaRepositorio);

        // O BilheteRepositorio depende dos outros para reconstruir entidades completas.
        IBilheteRepositorio bilheteRepositorio = new BilheteRepositorio(clienteRepositorio, pecaRepositorio);

        // 2. Criação dos Serviços de Aplicação (Núcleo do Hexágono)
        IClienteServico clienteServico = new ClienteServico(clienteRepositorio);
        IPecaServico pecaServico = new PecaServico(pecaRepositorio, assentoRepositorio);
        IReservaServico reservaServico = new ReservaServico(bilheteRepositorio, assentoRepositorio);
        
        // Instanciação do novo serviço de sessão.
        ISessaoServico sessaoServico = new SessaoServico(sessaoRepositorio);

        // 3. Criação e Início do Adaptador de Entrada Principal (UI Swing)
        // A TelaPrincipal agora também recebe o ISessaoServico.
        SwingUtilities.invokeLater(() -> {
            // A TelaPrincipal precisará ser adaptada para receber este novo serviço.
            // Por enquanto, vamos assumir que seu construtor foi atualizado.
            TelaPrincipal telaPrincipal = new TelaPrincipal(
                clienteServico, 
                pecaServico, 
                reservaServico,
                sessaoServico // Passando o novo serviço
            );

            JFrame frame = new JFrame("Teatro ABC - Sistema de Bilheteria");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 900);
            frame.setLocationRelativeTo(null);
            frame.setResizable(true);
            
            frame.setContentPane(telaPrincipal);
            frame.setVisible(true);
        });
    }
}