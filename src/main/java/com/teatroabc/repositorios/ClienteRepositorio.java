package com.teatroabc.repositorios;

import com.teatroabc.modelos.Cliente;
import java.util.*;

public class ClienteRepositorio {
    private static final Map<String, Cliente> clientes = new HashMap<>();

    public void salvar(Cliente cliente) {
        clientes.put(cliente.getCpf(), cliente);
    }

    public Cliente buscarPorCpf(String cpf) {
        return clientes.get(cpf);
    }
}
