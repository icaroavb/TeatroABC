package com.teatroabc; // Pacote raiz da aplicação

// Imports para as Interfaces de Repositório e Serviço
import com.teatroabc.infraestrutura.persistencia.interfaces.IClienteRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.IPecaRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.IAssentoRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.IBilheteRepositorio;
import com.teatroabc.aplicacao.interfaces.IClienteServico;
import com.teatroabc.aplicacao.interfaces.IPecaServico;
import com.teatroabc.aplicacao.interfaces.IReservaServico;

// Imports para as Implementações Concretas dos Repositórios e Serviços
import com.teatroabc.infraestrutura.persistencia.implementacao.ClienteRepositorio;
import com.teatroabc.infraestrutura.persistencia.implementacao.PecaRepositorio;
import com.teatroabc.infraestrutura.persistencia.implementacao.AssentoRepositorio;
import com.teatroabc.infraestrutura.persistencia.implementacao.BilheteRepositorio;
import com.teatroabc.aplicacao.servicos.ClienteServico;
import com.teatroabc.aplicacao.servicos.PecaServico;
import com.teatroabc.aplicacao.servicos.ReservaServico;

// Import da Tela Principal (já deve estar no pacote correto da UI)
import com.teatroabc.infraestrutura.ui_swing.telas.TelaPrincipal;

import javax.swing.*;

public class TeatroABCApplication {

    public static void main(String[] args) {
        // Configuração do Look and Feel (como estava)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Falha ao configurar o LookAndFeel do sistema: " + e.getMessage());
            e.printStackTrace();
        }

        // --- CONFIGURAÇÃO DA INJEÇÃO DE DEPENDÊNCIA ---

        // 1. Criar Instâncias dos Repositórios Concretos (Adaptadores de Saída)
        // (Lembre-se que PecaRepositorio e AssentoRepositorio como estão não usam GerenciadorArquivos para carregar peças/layout, são hardcoded/procedurais)
        IClienteRepositorio clienteRepositorio = new ClienteRepositorio();
        IPecaRepositorio pecaRepositorio = new PecaRepositorio(); // Assume que não precisa de dependências
        IAssentoRepositorio assentoRepositorio = new AssentoRepositorio(); // Assume que não precisa de dependências

        // BilheteRepositorio depende de IClienteRepositorio e IPecaRepositorio
        IBilheteRepositorio bilheteRepositorio = new BilheteRepositorio(clienteRepositorio, pecaRepositorio);

        // 2. Criar Instâncias dos Serviços Concretos, Injetando as Dependências dos Repositórios
        // (Serviços de Aplicação - Portas de Entrada)
        IClienteServico clienteServico = new ClienteServico(clienteRepositorio);
        IPecaServico pecaServico = new PecaServico(pecaRepositorio);
        IReservaServico reservaServico = new ReservaServico(bilheteRepositorio, assentoRepositorio);

        // 3. Iniciar a Interface Gráfica (UI - Adaptador de Entrada)
        SwingUtilities.invokeLater(() -> {
            // Modificar o construtor da TelaPrincipal para aceitar os serviços
            // Esta é uma forma simples de passar as dependências para a UI.
            // Em aplicações maiores, um framework de DI ou um Service Locator poderia ser usado.
            TelaPrincipal telaPrincipal = new TelaPrincipal(clienteServico, pecaServico, reservaServico);

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