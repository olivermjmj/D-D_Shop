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
    public ResponseDTO create(CreateDTO dto) {

        try {

            T entity = createDtoToEntity(dto);
            return toResponseDTO.apply(dao.create(entity));
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<ResponseDTO> createAsync(CreateDTO dto) {

        return CompletableFuture.supplyAsync(() -> create(dto), executorService);
    }

    @Override
    public List<ResponseDTO> getAll() {

        return dao.getAll()
                .stream()
                .map(toResponseDTO)
                .toList();
    }

    public CompletableFuture<List<ResponseDTO>> getAllAsync() {

        return CompletableFuture.supplyAsync(this::getAll, executorService);
    }

    @Override
    public Optional<ResponseDTO> getById(ID id) {

        return dao.getById(id).map(toResponseDTO);
    }

    public CompletableFuture<Optional<ResponseDTO>> getByIdAsync(ID id) {

        return CompletableFuture.supplyAsync(() -> getById(id), executorService);
    }

    @Override
    public ResponseDTO update(ID id, UpdateDTO dto) {

        try {

            T entity = dao.getById(id).orElseThrow(() -> new ApiException(404, "Not found"));
            T updatedEntity = updateDtoToEntity(entity, dto);
            return toResponseDTO.apply(dao.update(updatedEntity));
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<ResponseDTO> updateAsync(ID id, UpdateDTO dto) {

        return CompletableFuture.supplyAsync(() -> update(id, dto), executorService);
    }

    @Override
    public void delete(ID id) {

        try {

            dao.deleteById(id);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<Void> deleteAsync(ID id) {

        return CompletableFuture.runAsync(() -> delete(id), executorService);
    }
}