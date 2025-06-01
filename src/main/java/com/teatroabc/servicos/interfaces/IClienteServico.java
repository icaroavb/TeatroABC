package com.teatroabc.servicos.interfaces;

import com.teatroabc.modelos.Cliente;
import java.util.Optional;

public interface IClienteServico {
    Cliente cadastrar(String cpf, String nome, String dataNascimento, String telefone);
    Optional<Cliente> buscarPorCpf(String cpf);
    boolean existe(String cpf);
}
