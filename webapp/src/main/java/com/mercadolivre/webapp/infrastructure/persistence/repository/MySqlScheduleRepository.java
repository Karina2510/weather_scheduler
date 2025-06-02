package com.mercadolivre.webapp.infrastructure.persistence.repository;

import com.mercadolivre.webapp.domain.Schedule;
import com.mercadolivre.webapp.domain.repository.ScheduleRepository;
import com.mercadolivre.webapp.infrastructure.persistence.entity.ScheduleEntity;
import com.mercadolivre.webapp.infrastructure.persistence.mapper.ScheduleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MySqlScheduleRepository implements ScheduleRepository {

    private final JpaScheduleRepository repository;
    private final ScheduleMapper mapper;

    @Override
    public Schedule save(Schedule schedule) {
        var entity = mapper.toEntity(schedule);
        var savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Schedule> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }
} 