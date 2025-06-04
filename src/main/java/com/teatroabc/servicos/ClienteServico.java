package com.teatroabc.servicos;

import com.teatroabc.fidelidade.MembroABCGold;
import com.teatroabc.fidelidade.PlanoFidelidade;
import com.teatroabc.fidelidade.PlanoFidelidadeFactory;
import com.teatroabc.fidelidade.SemFidelidade;
import com.teatroabc.modelos.Cliente;
import com.teatroabc.repositorios.ClienteRepositorio;
import com.teatroabc.servicos.interfaces.IClienteServico;

import java.util.Optional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ClienteServico implements IClienteServico {
    
    private final ClienteRepositorio repo = new ClienteRepositorio();

    @Override
    public Cliente cadastrar(String cpf, String nome, String dataNascimento, String telefone, String email, String identificadorPlanoDesejado) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataNascLocalDate = LocalDate.parse(dataNascimento, formatter);

        PlanoFidelidade plano;
        // Exemplo simples baseado em um identificador, poderia ser mais complexo
        if (MembroABCGold.IDENTIFICADOR.equals(identificadorPlanoDesejado)) {
            // Validar se telefone e email foram fornecidos, pois MembroABCGold pode exigi-los
            if ((telefone == null || telefone.trim().isEmpty()) || (email == null || email.trim().isEmpty())) {
                throw new IllegalArgumentException("Telefone e Email são obrigatórios para o plano ABC GOLD.");
            }
            plano = PlanoFidelidadeFactory.criar(MembroABCGold.IDENTIFICADOR);
        } else {
            plano = PlanoFidelidadeFactory.criar(SemFidelidade.IDENTIFICADOR);
        }
        // Ou simplesmente:
        // PlanoFidelidade plano = PlanoFidelidadeFactory.criar(identificadorPlanoDesejado);
        // E deixar as validações de campos para o construtor do Cliente ou para o PlanoFidelidade em si.


        Cliente cliente = new Cliente(cpf, nome, dataNascLocalDate, telefone, email, plano);
        repo.salvar(cliente); // repo.salvar(cliente) já pegaria o identificador do plano do objeto cliente
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
