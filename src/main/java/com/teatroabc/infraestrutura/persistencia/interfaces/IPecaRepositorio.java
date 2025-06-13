// Pacote: com.teatroabc.repositorios.interfaces - Porta de Saída
package com.teatroabc.infraestrutura.persistencia.interfaces;

import com.teatroabc.dominio.modelos.Peca;
import java.util.List;
import java.util.Optional; // Sugestão

public interface IPecaRepositorio {
    
    List<Peca> listarTodas();
    
    Optional<Peca> buscarPorId(String id); // Usando Optional
    
}