package ru.aston.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<E, K> {

    List<E> findAll();

    Optional<E> findById(K id);

    E save(E entity);

    void update(E entity);

    boolean delete(K id);

}
