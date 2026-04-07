package app.service.impl;

import app.dao.interfaces.IDAO;
import app.exceptions.DatabaseException;
import app.service.interfaces.IService;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractService<CreateDTO, UpdateDTO, ResponseDTO, T, ID> implements IService<CreateDTO, UpdateDTO, ResponseDTO, ID> {

    protected final IDAO<T, ID> dao;
    protected final Function<T, ResponseDTO> toResponseDTO;

    protected AbstractService(IDAO<T, ID> dao, Function<T, ResponseDTO> toResponseDTO) {

        this.dao = dao;
        this.toResponseDTO = toResponseDTO;
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

    @Override
    public List<ResponseDTO> getAll() {

        return dao.getAll()
                .stream()
                .map(toResponseDTO)
                .toList();
    }

    @Override
    public Optional<ResponseDTO> getById(ID id) {

        return dao.getById(id).map(toResponseDTO);
    }

    @Override
    public ResponseDTO update(ID id, UpdateDTO dto) {

        try {

            T entity = dao.getById(id).orElseThrow(() -> new RuntimeException("Not found"));

            T updatedEntity = updateDtoToEntity(entity, dto);
            return toResponseDTO.apply(dao.update(updatedEntity));
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ID id) {

        try {

            dao.deleteById(id);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }
}