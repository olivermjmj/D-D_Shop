package app.service.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface IService<CreateDTO, UpdateDTO, ResponseDTO, ID> {

    CompletableFuture<ResponseDTO> create(CreateDTO dto);

    CompletableFuture<List<ResponseDTO>> getAll();

    CompletableFuture<Optional<ResponseDTO>> getById(ID id);

    CompletableFuture<ResponseDTO> update(ID id, UpdateDTO dto);

    CompletableFuture<Void> delete(ID id);
}