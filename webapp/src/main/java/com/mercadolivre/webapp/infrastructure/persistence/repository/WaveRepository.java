package com.mercadolivre.webapp.infrastructure.persistence.repository;

import com.mercadolivre.webapp.domain.Wave;
import com.mercadolivre.webapp.infrastructure.persistence.mapper.WaveMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class WaveRepository implements com.mercadolivre.webapp.domain.repository.WaveRepository {

    private final JpaWaveRepository jpaWaveRepository;
    private final WaveMapper waveMapper;

    @Override
    public List<Wave> findByScheduleId(Long scheduleId) {
        return jpaWaveRepository.findByScheduleIdOrderByForecastDateAsc(scheduleId)
                .stream()
                .map(waveMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Wave save(Wave wave) {
        var entity = waveMapper.toEntity(wave);
        var savedEntity = jpaWaveRepository.save(entity);
        return waveMapper.toDomain(savedEntity);
    }
} 