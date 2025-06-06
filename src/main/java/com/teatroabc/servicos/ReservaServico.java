package com.teatroabc.servicos;

import com.teatroabc.modelos.*;
import com.teatroabc.repositorios.BilheteRepositorio;
import com.teatroabc.servicos.interfaces.IReservaServico;
import java.util.List;

public class ReservaServico implements IReservaServico {
    private final BilheteRepositorio bilheteRepo = new BilheteRepositorio();
    private static final double DESCONTO_ABC = 0.05; // 5% de desconto

    @Override
    public Bilhete criarReserva(Peca peca, Cliente cliente, List<Assento> assentos) {
        return criarReserva(peca, cliente, assentos, null);
    }
    
    public Bilhete criarReserva(Peca peca, Cliente cliente, List<Assento> assentos, String turno) {
        double valorDesconto = 0.0;
        
        // Calcular desconto se cliente for membro ABC
        if (cliente.isMembroABC()) {
            double subtotal = assentos.stream().mapToDouble(Assento::getPreco).sum();
            valorDesconto = subtotal * DESCONTO_ABC;
            System.out.println("Cliente ABC GOLD - Subtotal: " + subtotal + ", Desconto: " + valorDesconto);
        } else {
            System.out.println("Cliente regular - Sem desconto");
        }
        
        Bilhete bilhete = new Bilhete(peca, cliente, assentos, valorDesconto);
        
        // Salvar com turno se fornecido
        if (turno != null) {
            bilheteRepo.salvar(bilhete, turno);
        } else {
            bilheteRepo.salvar(bilhete);
        }
        
        System.out.println("Reserva criada - Valor final: " + bilhete.getValorTotal() + 
                          " (Desconto ABC: " + bilhete.getValorDesconto() + ")");
        
        return bilhete;
    }

    @Override
    public List<Bilhete> buscarBilhetesCliente(String cpf) {
        return bilheteRepo.listarPorCpf(cpf);
    }

    @Override
    public Bilhete buscarBilhetePorId(String idBilhete) {
        return bilheteRepo.buscarPorId(idBilhete);
    }
}