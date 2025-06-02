package com.teatroabc.repositorios;

import com.teatroabc.modelos.*;
import com.teatroabc.utilitarios.GerenciadorArquivos;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BilheteRepositorio {
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private ClienteRepositorio clienteRepo = new ClienteRepositorio();
    private PecaRepositorio pecaRepo = new PecaRepositorio();
    private AssentoRepositorio assentoRepo = new AssentoRepositorio();
    
    public void salvar(Bilhete bilhete) {
        // Formato: ID|CODIGO_BARRAS|CPF_CLIENTE|ID_PECA|ASSENTOS|VALOR_TOTAL|DATA_HORA_COMPRA
        StringBuilder assentosStr = new StringBuilder();
        for (Assento assento : bilhete.getAssentos()) {
            if (assentosStr.length() > 0) assentosStr.append(",");
            assentosStr.append(assento.getCodigo());
        }
        
        String linha = String.format("%s|%s|%s|%s|%s|%.2f|%s",
            bilhete.getId(),
            bilhete.getCodigoBarras(),
            bilhete.getCliente().getCpf(),
            bilhete.getPeca().getId(),
            assentosStr.toString(),
            bilhete.getValorTotal(),
            bilhete.getDataHoraCompra().format(DATETIME_FORMATTER)
        );
        
        // Salvar bilhete
        GerenciadorArquivos.salvarBilhete(linha);
        
        // Marcar assentos como ocupados
        for (Assento assento : bilhete.getAssentos()) {
            GerenciadorArquivos.atualizarStatusAssento(
                bilhete.getPeca().getId(), 
                assento.getCodigo(), 
                "OCUPADO"
            );
        }
        
        System.out.println("Bilhete salvo: " + linha);
        System.out.println("Assentos marcados como ocupados: " + assentosStr.toString());
    }
    
    public List<Bilhete> listarPorCpf(String cpf) {
        List<String> linhas = GerenciadorArquivos.buscarBilhetesPorCpf(cpf);
        List<Bilhete> bilhetes = new ArrayList<>();
        
        System.out.println("Buscando bilhetes para CPF: " + cpf);
        System.out.println("Linhas encontradas: " + linhas.size());
        
        for (String linha : linhas) {
            System.out.println("Processando linha: " + linha);
            Bilhete bilhete = parsearBilhete(linha);
            if (bilhete != null) {
                bilhetes.add(bilhete);
                System.out.println("Bilhete adicionado: " + bilhete.getPeca().getTitulo());
            } else {
                System.out.println("Falha ao parsear bilhete da linha: " + linha);
            }
        }
        
        return bilhetes;
    }
    
    public Bilhete buscarPorId(String id) {
        List<String> linhas = GerenciadorArquivos.lerBilhetes();
        
        for (String linha : linhas) {
            if (linha.startsWith(id + "|")) {
                return parsearBilhete(linha);
            }
        }
        
        return null;
    }
    
    private Bilhete parsearBilhete(String linha) {
        String[] partes = linha.split("\\|");
        if (partes.length == 7) {
            try {
                // Buscar cliente
                Cliente cliente = clienteRepo.buscarPorCpf(partes[2]);
                if (cliente == null) {
                    System.out.println("Cliente não encontrado: " + partes[2]);
                    return null;
                }
                
                // Buscar peça
                Peca peca = pecaRepo.buscarPorId(partes[3]);
                if (peca == null) {
                    System.out.println("Peça não encontrada: " + partes[3]);
                    return null;
                }
                
                // Criar lista de assentos
                List<Assento> assentos = new ArrayList<>();
                String[] codigosAssentos = partes[4].split(",");
                for (String codigo : codigosAssentos) {
                    codigo = codigo.trim();
                    // Criar assento básico com as informações do código
                    if (codigo.contains("-")) {
                        String[] partesAssento = codigo.split("-");
                        if (partesAssento.length == 2) {
                            try {
                                char prefixo = partesAssento[0].charAt(0);
                                int fileira = Integer.parseInt(partesAssento[0].substring(1));
                                int numero = Integer.parseInt(partesAssento[1]);
                                
                                com.teatroabc.enums.CategoriaAssento categoria;
                                if (prefixo == 'F') {
                                    categoria = com.teatroabc.enums.CategoriaAssento.FRISAS;
                                } else if (prefixo == 'B') {
                                    categoria = com.teatroabc.enums.CategoriaAssento.BALCAO_NOBRE;
                                } else {
                                    categoria = com.teatroabc.enums.CategoriaAssento.BALCAO;
                                }
                                
                                Assento assento = new Assento(codigo, fileira, numero, categoria);
                                assento.setStatus(com.teatroabc.enums.StatusAssento.OCUPADO);
                                assentos.add(assento);
                            } catch (NumberFormatException e) {
                                System.out.println("Erro ao parsear código de assento: " + codigo);
                            }
                        }
                    }
                }
                
                if (assentos.isEmpty()) {
                    System.out.println("Nenhum assento válido encontrado para: " + partes[4]);
                    return null;
                }
                
                // Criar bilhete
                return new Bilhete(peca, cliente, assentos);
                
            } catch (Exception e) {
                System.out.println("Erro ao parsear bilhete: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Linha com formato inválido: " + linha + " (partes: " + partes.length + ")");
        }
        return null;
    }
}