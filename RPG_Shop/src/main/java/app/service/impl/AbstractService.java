package app.service.impl;

import app.dao.interfaces.IDAO;
import app.exceptions.ApiException;
import app.exceptions.DatabaseException;
import app.service.interfaces.IService;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

public abstract class AbstractService<CreateDTO, UpdateDTO, ResponseDTO, T, ID> implements IService<CreateDTO, UpdateDTO, ResponseDTO, ID> {

    protected final IDAO<T, ID> dao;
    protected final Function<T, ResponseDTO> toResponseDTO;
    protected final ExecutorService executorService;

    protected AbstractService(IDAO<T, ID> dao, Function<T, ResponseDTO> toResponseDTO, ExecutorService executorService) {

        this.dao = dao;
        this.toResponseDTO = toResponseDTO;
        this.executorService = executorService;
    }

    protected abstract T createDtoToEntity(CreateDTO dto);

    protected abstract T updateDtoToEntity(T entity, UpdateDTO dto);

    @Override
    public CompletableFuture<ResponseDTO> create(CreateDTO dto) {

        return CompletableFuture.supplyAsync(() -> {

            try {

                T entity = createDtoToEntity(dto);

                return toResponseDTO.apply(dao.create(entity));
            } catch (DatabaseException e) {
                throw new RuntimeException(e);
            }
        }, executorService);
    }

    @Override
    public CompletableFuture<List<ResponseDTO>> getAll() {
        return CompletableFuture.supplyAsync(() -> dao.getAll()
                                .stream()
                                .map(toResponseDTO)
                                .toList(), executorService);
    }

    @Override
    public CompletableFuture<Optional<ResponseDTO>> getById(ID id) {

        return CompletableFuture.supplyAsync(() -> dao.getById(id).map(toResponseDTO), executorService);
    }

    @Override
    public CompletableFuture<ResponseDTO> update(ID id, UpdateDTO dto) {

        return CompletableFuture.supplyAsync(() -> {

            try {

                T entity = dao.getById(id).orElseThrow(() -> new ApiException(404, "Not found"));
                T updatedEntity = updateDtoToEntity(entity, dto);

                return toResponseDTO.apply(dao.update(updatedEntity));
            } catch (DatabaseException e) {
                throw new RuntimeException(e);
            }
        }, executorService);
    }

    @Override
    public CompletableFuture<Void> delete(ID id) {

        return CompletableFuture.runAsync(() -> {

            try {

                dao.deleteById(id);
            } catch (DatabaseException e) {
                throw new RuntimeException(e);
            }
        }, executorService);
    }
}