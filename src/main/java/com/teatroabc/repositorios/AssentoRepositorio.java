package com.teatroabc.repositorios;

import com.teatroabc.modelos.Assento;
import java.util.*;

public class AssentoRepositorio {
    private static final Map<String, Assento> assentos = new HashMap<>();

    public void salvar(Assento assento) {
        assentos.put(assento.getCodigo(), assento);
    }

    public Assento buscarPorCodigo(String codigo) {
        return assentos.get(codigo);
    }

    public List<Assento> listarTodos() {
        return new ArrayList<>(assentos.values());
    }
}
