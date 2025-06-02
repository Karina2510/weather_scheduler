package com.mercadolivre.scheduler.infrastructure.persistence.repository;

import com.mercadolivre.scheduler.domain.ScheduleStatus;
import com.mercadolivre.scheduler.domain.TemperatureSchedule;
import com.mercadolivre.scheduler.domain.repository.TemperatureScheduleRepository;
import com.mercadolivre.scheduler.infrastructure.persistence.entity.TemperatureScheduleEntity;
import com.mercadolivre.scheduler.infrastructure.persistence.mapper.TemperatureScheduleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MySqlTemperatureScheduleRepository implements TemperatureScheduleRepository {

    private final JpaTemperatureScheduleRepository repository;
    private final TemperatureScheduleMapper mapper;

    @Override
    public TemperatureSchedule save(TemperatureSchedule temperatureSchedule) {

        TemperatureScheduleEntity entity = mapper.toEntity(temperatureSchedule);

        TemperatureScheduleEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public List<TemperatureSchedule> findByStatusInAndScheduleDateLessThanEqual(Set<ScheduleStatus> status, LocalDate date) {
        return repository.findByStatusInAndScheduleDateLessThanEqual(status, date)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TemperatureSchedule> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }
} 