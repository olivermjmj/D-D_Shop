package app.service.interfaces;

import java.util.List;
import java.util.Optional;

public interface IService<CreateDTO, UpdateDTO, ResponseDTO, ID> {

    ResponseDTO create(CreateDTO dto);

    List<ResponseDTO> getAll();

    Optional<ResponseDTO> getById(ID id);

    ResponseDTO update(ID id, UpdateDTO dto);

    void delete(ID id);
}