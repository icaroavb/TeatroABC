package com.teatroabc.infraestrutura.persistencia.implementacao;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.teatroabc.dominio.modelos.Cliente;
import com.teatroabc.infraestrutura.persistencia.interfaces.IClienteRepositorio;
import com.teatroabc.infraestrutura.persistencia.util.GerenciadorArquivos;

/**
 * Implementação (Adaptador Secundário) do repositório de Clientes.
 * Responsável por traduzir objetos Cliente para o formato de persistência
 * em arquivo de texto e vice-versa.
 */
public class ClienteRepositorio implements IClienteRepositorio {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * {@inheritDoc}
     */
    @Override   
    public void salvar(Cliente cliente) {


        
        // Formata os dados do cliente em uma string para salvar no arquivo.
        String linha = String.format("%s|%s|%s|%s|%s|%s",
                cliente.getCpf(),
                cliente.getNome(),
                cliente.getDataNascimento().format(DATE_FORMATTER),
                cliente.getTelefone() == null ? "" : cliente.getTelefone(),
                cliente.getEmail() == null ? "" : cliente.getEmail(),
                cliente.getPlanoFidelidade().getIdentificadorPlano()
        );
        GerenciadorArquivos.salvarCliente(linha);


    }

    /**
     * {@inheritDoc}
     * O método agora retorna um Optional para indicar de forma segura a possível ausência de um cliente.
     */
    @Override
    public Optional<Cliente> buscarPorCpf(String cpf) {
        String linha = GerenciadorArquivos.buscarClientePorCpf(cpf);

        if (linha != null) {
            try {
                String[] partes = linha.split("\\|");
                if (partes.length >= 6) {
                    
                    // Lógica para traduzir o valor do plano lido do arquivo para um identificador padrão.
                    String identificadorPlano;
                    String valorPlanoDoArquivo = partes[5].toUpperCase();

                    if (valorPlanoDoArquivo.equals("TRUE") || valorPlanoDoArquivo.equals("GOLD")) {
                        identificadorPlano = "GOLD";
                    } else {
                        identificadorPlano = "PADRAO";
                    }

                    Cliente cliente = new Cliente(
                            partes[0], // CPF
                            partes[1], // Nome
                            LocalDate.parse(partes[2], DATE_FORMATTER), // Data Nascimento
                            partes[3].isEmpty() ? null : partes[3],  // Telefone
                            partes[4].isEmpty() ? null : partes[4],  // Email
                            identificadorPlano // Usa o identificador traduzido
                    );

                    // MUDANÇA: Retorna o cliente "enrolado" em um Optional.
                    return Optional.of(cliente);
                }
            } catch (Exception e) {
                System.err.println("Erro ao parsear cliente da linha: " + linha);
                e.printStackTrace();
            }
        }
        
        // Se a linha for nula ou o parse falhar, retorna um Optional vazio.
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existe(String cpf) {
        // A forma mais eficiente de verificar a existência é ver se a busca retorna um valor.
        return GerenciadorArquivos.buscarClientePorCpf(cpf) != null;
    }
}