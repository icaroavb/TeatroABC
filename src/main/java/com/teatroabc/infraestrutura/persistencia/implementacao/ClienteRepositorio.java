package com.teatroabc.infraestrutura.persistencia.implementacao;

import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.infraestrutura.persistencia.interfaces.IClienteRepositorio; // Implementar a interface
import com.teatroabc.infraestrutura.persistencia.util.GerenciadorArquivos;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
// import java.util.Optional; // Se for usar Optional

public class ClienteRepositorio implements IClienteRepositorio { // IMPLEMENTA A INTERFACE
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void salvar(Cliente cliente) {
        // A lógica de verificar se já existe foi movida para o Serviço.
        // O repositório assume que a decisão de salvar (ou não) já foi tomada.
        // Se ainda quiser uma verificação aqui, pode ser um log ou uma exceção de infraestrutura.

        String linha = String.format("%s|%s|%s|%s|%s|%s",
                cliente.getCpf(), // Assumindo que o CPF no objeto Cliente já está normalizado
                cliente.getNome(),
                cliente.getDataNascimento().format(DATE_FORMATTER),
                cliente.getTelefone() == null ? "" : cliente.getTelefone(),
                cliente.getEmail() == null ? "" : cliente.getEmail(),
                cliente.getPlanoFidelidade().getIdentificadorPlano() // Usar o identificador do plano
        );

        GerenciadorArquivos.salvarCliente(linha);
        System.out.println("Cliente persistido: " + cliente.getNome() +
                " - Plano: " + cliente.getPlanoFidelidade().getNomePlano());
    }

    @Override
    public Cliente buscarPorCpf(String cpf) { // cpf já vem normalizado do serviço
        String linha = GerenciadorArquivos.buscarClientePorCpf(cpf); // GerenciadorArquivos deve buscar pelo CPF normalizado

        if (linha != null) {
            String[] partes = linha.split("\\|");
            // Assumindo o novo formato de 6 partes: CPF|NOME|DATA_NASC|TELEFONE|EMAIL|ID_PLANO
            if (partes.length == 6) {
                return new Cliente(
                        partes[0], // CPF
                        partes[1], // Nome
                        LocalDate.parse(partes[2], DATE_FORMATTER), // Data Nascimento
                        partes[3].isEmpty() ? null : partes[3],  // Telefone
                        partes[4].isEmpty() ? null : partes[4],  // Email
                        partes[5]  // Identificador do Plano de Fidelidade
                );
            } else if (partes.length == 4) { // Compatibilidade com formato antigo (sem email, sem plano)
                System.out.println("Atenção: Lendo cliente em formato antigo (CPF: " + partes[0] + "). Assumindo plano padrão.");
                return new Cliente(
                        partes[0],
                        partes[1],
                        LocalDate.parse(partes[2], DATE_FORMATTER),
                        partes[3],
                        null, // Sem email
                        "PADRAO" // Assumir plano padrão
                );
            } else {
                System.err.println("Formato de linha inválido para cliente no arquivo: " + linha);
                return null;
            }
        }
        return null;
    }

    @Override
    public boolean existe(String cpf) { // cpf já vem normalizado do serviço
        return GerenciadorArquivos.buscarClientePorCpf(cpf) != null;
    }
}