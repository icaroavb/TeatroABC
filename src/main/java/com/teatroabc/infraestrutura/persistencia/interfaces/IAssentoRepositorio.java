// Pacote: com.teatroabc.repositorios.interfaces  PORTA DE SAÍDA
package com.teatroabc.infraestrutura.persistencia.interfaces;

import com.teatroabc.dominio.modelos.Assento;
import com.teatroabc.dominio.enums.Turno;
import java.util.List;

public interface IAssentoRepositorio {
    /**
     * Retorna todos os assentos definidos para uma peça, com seu status atualizado
     * para um turno específico (considerando os já ocupados).
     */
    List<Assento> buscarTodosAssentosPorPecaETurno(String idPeca, Turno turno);

    /**
     * Verifica se um conjunto específico de assentos está disponível para uma peça e turno.
     * @return true se todos os assentos estiverem disponíveis, false caso contrário.
     */
    boolean verificarDisponibilidade(String idPeca, Turno turno, List<String> codigosAssentos);

    /**
     * Marca um conjunto de assentos como ocupados para uma peça e turno.
     * (Esta responsabilidade pode ser do BilheteRepositorio ao salvar o bilhete)
     * Se for aqui, o BilheteRepositorio chamaria este método.
     */
    // void marcarComoOcupados(String idPeca, Turno turno, List<String> codigosAssentos);

    // O método GerenciadorArquivos.buscarAssentosOcupados já existe.
    // Poderíamos ter o AssentoRepositorio usando-o.
}