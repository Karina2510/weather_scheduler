package com.mercadolivre.webapp.infrastructure.persistence.repository;

import com.mercadolivre.webapp.domain.Temperature;
import com.mercadolivre.webapp.domain.repository.TemperatureRepository;
import com.mercadolivre.webapp.infrastructure.persistence.entity.TemperatureEntity;
import com.mercadolivre.webapp.infrastructure.persistence.mapper.TemperatureMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MySqlTemperatureRepository implements TemperatureRepository {

    private final JpaTemperatureRepository repository;
    private final TemperatureMapper mapper;

    @Override
    public List<Temperature> saveAll(List<Temperature> temperatures) {
        List<TemperatureEntity> entities = temperatures.stream()
                .map(mapper::toEntity)
                .collect(Collectors.toList());

        List<TemperatureEntity> savedEntities = repository.saveAll(entities);

        return savedEntities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Temperature> findByScheduleId(Long scheduleId) {
        return repository.findByScheduleIdOrderByForecastDateAsc(scheduleId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Temperature save(Temperature temperature) {
        TemperatureEntity entity = mapper.toEntity(temperature);
        TemperatureEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }
} 