package com.teatroabc;

// --- Portas de Saída (Interfaces dos Repositórios) ---
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager; 

import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico;
import com.teatroabc.aplicacao.interfaces.ISessaoServico;
import com.teatroabc.aplicacao.servicos.ClienteServico;
import com.teatroabc.aplicacao.servicos.PecaServico;
import com.teatroabc.aplicacao.servicos.ReservaServico;
import com.teatroabc.aplicacao.servicos.SessaoServico;
import com.teatroabc.infraestrutura.persistencia.implementacao_mysql.AssentoRepositorio_mySql;
import com.teatroabc.infraestrutura.persistencia.implementacao_mysql.BilheteRepositorio_mysql;
import com.teatroabc.infraestrutura.persistencia.implementacao_mysql.ClienteRepositorio_mySql;
import com.teatroabc.infraestrutura.persistencia.implementacao_mysql.PecaRepositorio_mysql;
import com.teatroabc.infraestrutura.persistencia.implementacao_mysql.SessaoRepositorio_mysql;
import com.teatroabc.infraestrutura.persistencia.interfaces.IAssentoRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.IBilheteRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.IClienteRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.IPecaRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.ISessaoRepositorio;
import com.teatroabc.infraestrutura.ui_swing.telas.TelaPrincipal;

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
        IClienteRepositorio clienteRepositorio = new ClienteRepositorio_mySql();
        IAssentoRepositorio assentoRepositorio = new AssentoRepositorio_mySql();
        
        // O PecaRepositorio agora é uma dependência para outros repositórios.
        IPecaRepositorio pecaRepositorio = new PecaRepositorio_mysql();
        
        // O SessaoRepositorio depende do PecaRepositorio para construir as sessões.
        ISessaoRepositorio sessaoRepositorio = new SessaoRepositorio_mysql(pecaRepositorio);

        // O BilheteRepositorio depende dos outros para reconstruir entidades completas.
        IBilheteRepositorio bilheteRepositorio = new BilheteRepositorio_mysql(clienteRepositorio, sessaoRepositorio);

        // 2. Criação dos Serviços de Aplicação (Núcleo do Hexágono)
        IClienteServico clienteServico = new ClienteServico(clienteRepositorio);
        IPecaServico pecaServico = new PecaServico(pecaRepositorio, assentoRepositorio);
        IReservaServico reservaServico = new ReservaServico(bilheteRepositorio, assentoRepositorio);
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

            //ponto de entrada para configurar sincronização com o banco de dados
            JFrame frame = new JFrame("Teatro ABC - Sistema de Bilheteria");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 900);

            //instruções abaixo adicionadas para garantir que app vai iniciar maximizado
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setLocationRelativeTo(null);            
            frame.setContentPane(telaPrincipal);
            frame.setVisible(true);
        });
    }
}