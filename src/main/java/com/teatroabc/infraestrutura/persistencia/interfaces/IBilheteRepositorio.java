// Pacote: com.teatroabc.repositorios.interfaces - PORTA DE SAÍDA
package com.teatroabc.infraestrutura.persistencia.interfaces;

import com.teatroabc.dominio.modelos.Bilhete;
import com.teatroabc.dominio.enums.Turno;
import java.util.List;
import java.util.Optional;

public interface IBilheteRepositorio {
    void salvar(Bilhete bilhete, Turno turno);
    List<Bilhete> listarPorCpfCliente(String cpf);
    Optional<Bilhete> buscarPorId(String id);
}
