package com.mercadolivre.webapp.infrastructure.persistence.repository;

import com.mercadolivre.webapp.infrastructure.persistence.entity.WaveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaWaveRepository extends JpaRepository<WaveEntity, Long> {
    List<WaveEntity> findByScheduleIdOrderByForecastDateAsc(Long scheduleId);
} 