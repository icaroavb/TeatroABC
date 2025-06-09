package com.teatroabc.infraestrutura.persistencia.implementacao;

import com.teatroabc.dominio.enums.CategoriaAssento;
import com.teatroabc.dominio.modelos.*;
import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.infraestrutura.persistencia.interfaces.IBilheteRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.IClienteRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.IPecaRepositorio;
import com.teatroabc.infraestrutura.persistencia.util.GerenciadorArquivos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class BilheteRepositorio implements IBilheteRepositorio {
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final IClienteRepositorio clienteRepositorio;
    private final IPecaRepositorio pecaRepositorio;
    // private final IAssentoRepositorio assentoRepositorio; // Não é usado diretamente aqui agora

    public BilheteRepositorio(IClienteRepositorio clienteRepositorio, IPecaRepositorio pecaRepositorio) {
        this.clienteRepositorio = clienteRepositorio;
        this.pecaRepositorio = pecaRepositorio;
    }

    @Override
    public void salvar(Bilhete bilhete, Turno turno) { // O turno aqui é o mesmo do bilhete.getTurno()
        if (bilhete == null || turno == null) {
            // Lançar exceção ou logar erro, não salvar bilhete inválido
            System.err.println("BilheteRepositorio: Tentativa de salvar bilhete ou turno nulo.");
            return;
        }
        if (bilhete.getTurno() != turno) {
            // Isso indicaria um erro de lógica na chamada, o turno do bilhete deve ser o mesmo
            System.err.println("BilheteRepositorio: Inconsistência - Turno do objeto Bilhete (" +
                    bilhete.getTurno() + ") diferente do turno passado para salvar (" + turno + ").");
            // Poderia lançar uma exceção aqui. Por ora, usa o turno do objeto bilhete.
        }

        String assentosStr = bilhete.getAssentos().stream()
                .map(Assento::getCodigo)
                .collect(Collectors.joining(","));

        // Formato: ID|CODIGO_BARRAS|CPF_CLIENTE|ID_PECA|ASSENTOS_CSV|SUBTOTAL|VALOR_DESCONTO|VALOR_TOTAL|TURNO_ENUM_NAME|DATA_HORA_COMPRA
        String linha = String.format(Locale.US, "%s|%s|%s|%s|%s|%.2f|%.2f|%.2f|%s|%s",
                bilhete.getId(),
                bilhete.getCodigoBarras(),
                bilhete.getCliente().getCpf(),
                bilhete.getPeca().getId(),
                assentosStr,
                bilhete.getSubtotal(),
                bilhete.getValorDesconto(),
                bilhete.getValorTotal(),
                bilhete.getTurno().name(), // USA O TURNO DO OBJETO BILHETE
                bilhete.getDataHoraCompra().format(DATETIME_FORMATTER)
        );

        GerenciadorArquivos.salvarBilhete(linha);

        for (Assento assento : bilhete.getAssentos()) {
            GerenciadorArquivos.marcarAssentoOcupado(
                    bilhete.getPeca().getId(),
                    bilhete.getTurno().name(), // USA O TURNO DO OBJETO BILHETE
                    assento.getCodigo()
            );
        }
    }

    @Override
    public List<Bilhete> listarPorCpfCliente(String cpf) {
        List<String> linhas = GerenciadorArquivos.buscarBilhetesPorCpf(cpf);
        List<Bilhete> bilhetesDoCliente = new ArrayList<>();
        for (String linha : linhas) {
            parsearBilhete(linha).ifPresent(bilhetesDoCliente::add);
        }
        return bilhetesDoCliente;
    }

    @Override
    public Optional<Bilhete> buscarPorId(String id) {
        if (id == null) return Optional.empty();
        List<String> linhas = GerenciadorArquivos.lerBilhetes();
        for (String linha : linhas) {
            if (linha.startsWith(id + "|")) {
                return parsearBilhete(linha);
            }
        }
        return Optional.empty();
    }

    private Optional<Bilhete> parsearBilhete(String linha) {
        String[] partes = linha.split("\\|");
        if (partes.length == 10) {
            try {
                String id = partes[0];
                String codigoBarras = partes[1];
                String cpfCliente = partes[2];
                String idPeca = partes[3];
                String[] codigosAssentosStr = partes[4].split(",");
                BigDecimal subtotal = new BigDecimal(partes[5].replace(",","."));
                BigDecimal valorDesconto = new BigDecimal(partes[6].replace(",","."));
                BigDecimal valorTotal = new BigDecimal(partes[7].replace(",","."));
                Turno turno; // TURNO SERÁ LIDO DO ARQUIVO
                try {
                    turno = Turno.valueOf(partes[8]); // LÊ O TURNO DO ARQUIVO
                } catch (IllegalArgumentException e) {
                    System.err.println("BilheteRepositorio: Turno inválido na linha do bilhete: '" + partes[8] + "'. Bilhete ID: " + id);
                    return Optional.empty(); // Não pode criar bilhete sem turno válido
                }
                LocalDateTime dataHoraCompra = LocalDateTime.parse(partes[9], DATETIME_FORMATTER);

                Optional<Cliente> clienteOpt = Optional.ofNullable(clienteRepositorio.buscarPorCpf(cpfCliente));
                if (clienteOpt.isEmpty()) return Optional.empty();
                Cliente cliente = clienteOpt.get();

                Optional<Peca> pecaOpt = pecaRepositorio.buscarPorId(idPeca);
                if (pecaOpt.isEmpty()) return Optional.empty();
                Peca peca = pecaOpt.get();

                List<Assento> assentos = new ArrayList<>();
                for (String codigoAssento : codigosAssentosStr) {
                    char prefixoCat = codigoAssento.charAt(0);
                    CategoriaAssento cat;
                    if (prefixoCat == 'F') cat = CategoriaAssento.FRISAS;
                    else if (prefixoCat == 'B') cat = CategoriaAssento.BALCAO_NOBRE;
                    else cat = CategoriaAssento.BALCAO;

                    String[] partesCodigo = codigoAssento.substring(1).split("-");
                    int fileira = Integer.parseInt(partesCodigo[0]);
                    int numero = Integer.parseInt(partesCodigo[1]);

                    assentos.add(new Assento(codigoAssento, fileira, numero, cat, cat.getPrecoBase()));
                }

                Bilhete bilhete = new Bilhete(id, codigoBarras, peca, cliente, assentos,
                        turno, // USA O TURNO LIDO DO ARQUIVO
                        subtotal, valorDesconto, valorTotal, dataHoraCompra);
                return Optional.of(bilhete);

            } catch (Exception e) {
                System.err.println("BilheteRepositorio: Erro ao parsear bilhete da linha: " + linha + " - " + e.getMessage());
                // e.printStackTrace(); // Descomentar para debug detalhado
            }
        } else {
            System.err.println("BilheteRepositorio: Formato de linha inválido (esperava 10 partes): " + linha);
        }
        return Optional.empty();
    }
}