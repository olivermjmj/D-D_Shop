package app.service.impl;

import app.config.ThreadPoolConfig;
import app.dao.StampDAO;
import app.dto.stamp.CreateStampDTO;
import app.dto.stamp.StampResponseDTO;
import app.dto.stamp.UpdateStampDTO;
import app.entities.Stamp;
import app.exceptions.ApiException;

import java.util.concurrent.ExecutorService;

public class StampServiceImpl extends AbstractService<CreateStampDTO, UpdateStampDTO, StampResponseDTO, Stamp, Integer> {

    private final StampDAO stampDAO;

    public StampServiceImpl() {
        this(new StampDAO(), ThreadPoolConfig.getExecutor());
    }

    public StampServiceImpl(StampDAO stampDAO, ExecutorService executorService) {
        super(stampDAO, StampResponseDTO::fromEntity, executorService);
        this.stampDAO = stampDAO;
    }

    @Override
    protected Stamp createDtoToEntity(CreateStampDTO dto) {
        validateName(dto.name());

        Stamp stamp = new Stamp();
        stamp.setName(dto.name());

        return stamp;
    }

    @Override
    protected Stamp updateDtoToEntity(Stamp stamp, UpdateStampDTO dto) {
        if (dto.name() != null) {
            validateName(dto.name());
            stamp.setName(dto.name());
        }

        return stamp;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ApiException(400, "Stamp name cannot be blank");
        }
    }
}