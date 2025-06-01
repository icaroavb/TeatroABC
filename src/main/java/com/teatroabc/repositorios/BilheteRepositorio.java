package com.teatroabc.repositorios;

import com.teatroabc.modelos.Bilhete;
import java.util.*;

public class BilheteRepositorio {
    private static final List<Bilhete> bilhetes = new ArrayList<>();

    public void salvar(Bilhete bilhete) {
        bilhetes.add(bilhete);
    }

    public List<Bilhete> listarPorCpf(String cpf) {
        List<Bilhete> result = new ArrayList<>();
        for (Bilhete b : bilhetes) {
            if (b.getCliente().getCpf().equals(cpf)) {
                result.add(b);
            }
        }
        return result;
    }

    public Bilhete buscarPorId(String id) {
        for (Bilhete b : bilhetes) {
            if (b.getId().equals(id)) {
                return b;
            }
        }
        return null;
    }
}
