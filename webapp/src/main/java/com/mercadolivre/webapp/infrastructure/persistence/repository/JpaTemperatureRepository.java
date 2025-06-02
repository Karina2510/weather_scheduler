package com.mercadolivre.webapp.infrastructure.persistence.repository;

import com.mercadolivre.webapp.infrastructure.persistence.entity.TemperatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaTemperatureRepository extends JpaRepository<TemperatureEntity, Long> {
    List<TemperatureEntity> findByScheduleIdOrderByForecastDateAsc(Long scheduleId);
} 