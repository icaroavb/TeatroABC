// Arquivo: infraestrutura/persistencia/interfaces/IPecaRepositorio.java
package com.teatroabc.infraestrutura.persistencia.interfaces;

import com.teatroabc.dominio.modelos.Peca;
import java.util.List;
import java.util.Optional;

/**
 * Interface (Porta de Saída) para o repositório de Peças.
 * Define o contrato para operações de acesso a dados relacionadas às peças,
 * como buscar todas as peças disponíveis ou uma peça específica por seu ID.
 */
public interface IPecaRepositorio {
    
    /**
     * Busca todas as peças cadastradas na fonte de dados.
     * @return Uma lista de objetos Peca. A lista pode ser vazia, mas não deve ser nula.
     */
    List<Peca> listarTodas();
    
    /**
     * Busca uma peça específica pelo seu identificador único.
     * @param id O ID da peça a ser buscada.
     * @return Um Optional contendo o objeto Peca se encontrado; caso contrário, um Optional vazio.
     */
    Optional<Peca> buscarPorId(String id);
    
}
