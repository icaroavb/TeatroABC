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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataNascimentoLocalDate = LocalDate.parse(dataNascimento, formatter);
        Cliente cliente = new Cliente(cpf, nome, dataNascimentoLocalDate, telefone);
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
