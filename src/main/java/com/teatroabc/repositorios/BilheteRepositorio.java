package com.teatroabc.repositorios;

import com.teatroabc.modelos.*;
import com.teatroabc.utilitarios.GerenciadorArquivos;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BilheteRepositorio {
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private ClienteRepositorio clienteRepo = new ClienteRepositorio();
    private PecaRepositorio pecaRepo = new PecaRepositorio();
    
    public void salvar(Bilhete bilhete) {
        // Formato: ID|CODIGO_BARRAS|CPF_CLIENTE|ID_PECA|ASSENTOS|VALOR_TOTAL|DATA_HORA_COMPRA
        StringBuilder assentosStr = new StringBuilder();
        for (Assento assento : bilhete.getAssentos()) {
            if (assentosStr.length() > 0) assentosStr.append(",");
            assentosStr.append(assento.getCodigo());
            
            // Salvar status do assento como OCUPADO
            GerenciadorArquivos.atualizarStatusAssento(
                bilhete.getPeca().getId(), 
                assento.getCodigo(), 
                "OCUPADO"
            );
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
        
        GerenciadorArquivos.salvarBilhete(linha);
    }
    
    public List<Bilhete> listarPorCpf(String cpf) {
        List<String> linhas = GerenciadorArquivos.buscarBilhetesPorCpf(cpf);
        List<Bilhete> bilhetes = new ArrayList<>();
        
        for (String linha : linhas) {
            Bilhete bilhete = parsearBilhete(linha);
            if (bilhete != null) {
                bilhetes.add(bilhete);
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
                if (cliente == null) return null;
                
                // Buscar peça
                Peca peca = pecaRepo.buscarPorId(partes[3]);
                if (peca == null) return null;
                
                // Criar lista de assentos (simplificado - apenas com códigos)
                List<Assento> assentos = new ArrayList<>();
                String[] codigosAssentos = partes[4].split(",");
                for (String codigo : codigosAssentos) {
                    // Criar assento básico com as informações do código
                    String[] partesAssento = codigo.split("-");
                    if (partesAssento.length == 2) {
                        int fileira = Integer.parseInt(partesAssento[0].substring(1));
                        int numero = Integer.parseInt(partesAssento[1]);
                        
                        com.teatroabc.enums.CategoriaAssento categoria;
                        if (codigo.startsWith("F")) {
                            categoria = com.teatroabc.enums.CategoriaAssento.FRISAS;
                        } else if (codigo.startsWith("B") && fileira <= 2) {
                            categoria = com.teatroabc.enums.CategoriaAssento.BALCAO_NOBRE;
                        } else {
                            categoria = com.teatroabc.enums.CategoriaAssento.BALCAO;
                        }
                        
                        Assento assento = new Assento(codigo, fileira, numero, categoria);
                        assento.setStatus(com.teatroabc.enums.StatusAssento.OCUPADO);
                        assentos.add(assento);
                    }
                }
                
                // Criar bilhete manualmente (não usar construtor que gera novo ID)
                // Isso é uma simplificação - idealmente teríamos um construtor específico
                return new Bilhete(peca, cliente, assentos);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
