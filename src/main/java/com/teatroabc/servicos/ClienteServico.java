package com.teatroabc.servicos;

import com.teatroabc.modelos.Cliente;
import com.teatroabc.repositorios.ClienteRepositorio;
import com.teatroabc.servicos.interfaces.IClienteServico;

import java.util.Optional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ClienteServico implements IClienteServico {
    private final ClienteRepositorio repo = new ClienteRepositorio();

    @Override
    public Cliente cadastrar(String cpf, String nome, String dataNascimento, String telefone) {
        return cadastrar(cpf, nome, dataNascimento, telefone, "", false);
    }

    public Cliente cadastrar(String cpf, String nome, String dataNascimento, String telefone, String email, boolean membroABC) {
        // Verificar se cliente já existe
        if (repo.existe(cpf)) {
            System.out.println("Cliente já existe: " + cpf);
            return repo.buscarPorCpf(cpf);
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataNascimentoLocalDate = LocalDate.parse(dataNascimento, formatter);
        Cliente cliente = new Cliente(cpf, nome, dataNascimentoLocalDate, telefone, email, membroABC);
        repo.salvar(cliente);
        return cliente;
    }

    @Override
    public Optional<Cliente> buscarPorCpf(String cpf) {
        Cliente cliente = repo.buscarPorCpf(cpf);
        return Optional.ofNullable(cliente);
    }

    @Override
    public boolean existe(String cpf) {
        return repo.buscarPorCpf(cpf) != null;
    }
}