// Arquivo: infraestrutura/persistencia/implementacao/BilheteRepositorio.java
package com.teatroabc.infraestrutura.persistencia.implementacao;

import com.teatroabc.dominio.modelos.*;
import com.teatroabc.dominio.enums.Turno;
import com.teatroabc.dominio.enums.CategoriaAssento;
import com.teatroabc.infraestrutura.persistencia.interfaces.IBilheteRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.IClienteRepositorio;
import com.teatroabc.infraestrutura.persistencia.interfaces.IPecaRepositorio;
import com.teatroabc.infraestrutura.persistencia.util.GerenciadorArquivos;
import com.teatroabc.infraestrutura.utilitarios_comuns.GeradorIdUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementação (Adaptador Secundário) do repositório de Bilhetes.
 * Responsável por traduzir objetos Bilhete para o formato de persistência
 * em arquivo de texto e vice-versa.
 * REFATORADO: A marcação de assentos ocupados agora usa o ID da Sessão.
 */
public class BilheteRepositorio implements IBilheteRepositorio {
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final IClienteRepositorio clienteRepositorio;
    private final IPecaRepositorio pecaRepositorio;
    
    public BilheteRepositorio(IClienteRepositorio clienteRepositorio, IPecaRepositorio pecaRepositorio) {
        this.clienteRepositorio = clienteRepositorio;
        this.pecaRepositorio = pecaRepositorio;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void salvar(Bilhete bilhete) {
        if (bilhete == null) {
            System.err.println("BilheteRepositorio: Tentativa de salvar bilhete nulo.");
            return;
        }

        Sessao sessao = bilhete.getSessao();
        String assentosStr = bilhete.getAssentos().stream()
                .map(Assento::getCodigo)
                .collect(Collectors.joining(","));

        // O formato da linha do bilhete é mantido para compatibilidade com dados existentes,
        // mas a lógica para marcar assentos é corrigida.
        String linha = String.format(Locale.US, "%s|%s|%s|%s|%s|%.2f|%.2f|%.2f|%s|%s",
                bilhete.getId(),
                bilhete.getCodigoBarras(),
                bilhete.getCliente().getCpf(),
                sessao.getPeca().getId(), 
                assentosStr,
                bilhete.getSubtotal(),
                bilhete.getValorDesconto(),
                bilhete.getValorTotal(),
                sessao.getTurno().name(),
                bilhete.getDataHoraCompra().format(DATETIME_FORMATTER)
        );

        GerenciadorArquivos.salvarBilhete(linha);

        // CORREÇÃO: Marca os assentos como ocupados usando o ID da Sessão.
        for (Assento assento : bilhete.getAssentos()) {
            GerenciadorArquivos.marcarAssentoOcupado(
                    sessao.getId(),
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

    /**
     * Traduz uma linha de texto do arquivo de bilhetes para um objeto de domínio Bilhete.
     * @param linha A string lida do arquivo.
     * @return Um Optional contendo o Bilhete se o parse for bem-sucedido.
     */
     private Optional<Bilhete> parsearBilhete(String linha) {
        String[] partes = linha.split("\\|");
        if (partes.length < 10) {
             return Optional.empty();
        }

        try {
            String idBilhete = partes[0];
            String codigoBarras = partes[1];
            String cpfCliente = partes[2];
            String idPeca = partes[3];
            String[] codigosAssentosStr = partes[4].split(",");
            BigDecimal subtotal = new BigDecimal(partes[5].replace(",","."));
            BigDecimal valorDesconto = new BigDecimal(partes[6].replace(",","."));
            BigDecimal valorTotal = new BigDecimal(partes[7].replace(",","."));
            Turno turno = Turno.valueOf(partes[8]);
            LocalDateTime dataHoraCompra = LocalDateTime.parse(partes[9], DATETIME_FORMATTER);

            Optional<Cliente> clienteOpt = clienteRepositorio.buscarPorCpf(cpfCliente);
            Optional<Peca> pecaOpt = pecaRepositorio.buscarPorId(idPeca);

            if (clienteOpt.isEmpty() || pecaOpt.isEmpty()) {
                return Optional.empty();
            }
            Cliente cliente = clienteOpt.get();
            Peca peca = pecaOpt.get();
            
            // Recria um objeto Sessao com base nos dados limitados do arquivo de bilhete.
            Sessao sessao = new Sessao(
                GeradorIdUtil.gerarNovoId(), // ID temporário para a sessão
                peca,
                dataHoraCompra, // Usando a data da compra como fallback para a data da sessão
                turno
            );

            List<Assento> assentos = new ArrayList<>();
            for (String codigoAssento : codigosAssentosStr) {
                char prefixoCat = codigoAssento.charAt(0);
                CategoriaAssento cat;
                if (prefixoCat == 'F') cat = CategoriaAssento.FRISA;
                else if (prefixoCat == 'B') cat = CategoriaAssento.BALCAO_NOBRE;
                else if (prefixoCat == 'P') cat = CategoriaAssento.PLATEIA_B; // Suposição
                else cat = CategoriaAssento.CAMAROTE; // Suposição
                
                String[] partesCodigo = codigoAssento.substring(1).split("-");
                int fileira = Integer.parseInt(partesCodigo[0]);
                int numero = Integer.parseInt(partesCodigo[1]);
                assentos.add(new Assento(codigoAssento, fileira, numero, cat, cat.getPrecoBase()));
            }

            Bilhete bilhete = new Bilhete(
                idBilhete, codigoBarras, sessao, cliente, assentos,
                subtotal, valorDesconto, valorTotal, dataHoraCompra
            );
            return Optional.of(bilhete);

        } catch (Exception e) {
            System.err.println("BilheteRepositorio: Erro ao parsear bilhete da linha: " + linha);
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
