package app.dao.interfaces;

import app.exceptions.DatabaseException;

import java.util.List;
import java.util.Optional;

public interface IDAO<T, ID> {

    T create(T entity) throws DatabaseException;
    T update(T entity) throws DatabaseException;

    Optional<T> getById(ID id);
    List<T> getAll();

    boolean delete(T entity) throws DatabaseException;
    boolean deleteById(ID id) throws DatabaseException;

    void deleteAll() throws DatabaseException;
}