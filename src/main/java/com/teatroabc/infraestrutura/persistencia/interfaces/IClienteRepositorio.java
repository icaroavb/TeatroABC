// Pacote: com.teatroabc.repositorios.interfaces - PORTA DE SAÍDA
package com.teatroabc.infraestrutura.persistencia.interfaces;

import com.teatroabc.dominio.modelos.Cliente;
// import java.util.Optional; // Se for usar Optional no retorno de buscarPorCpf

public interface IClienteRepositorio {
    void salvar(Cliente cliente);
    Cliente buscarPorCpf(String cpf); // Pode retornar Optional<Cliente>
    boolean existe(String cpf);
    // Outros métodos de consulta podem ser adicionados aqui conforme necessário
}