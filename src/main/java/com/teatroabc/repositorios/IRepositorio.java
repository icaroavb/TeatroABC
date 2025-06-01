package com.teatroabc.repositorios;

import java.util.List;
import java.util.Optional;

public interface IRepositorio<T, ID> {
    T salvar(T entidade);
    Optional<T> buscarPorId(ID id);
    List<T> buscarTodos();
    void deletar(ID id);
    boolean existe(ID id);
}