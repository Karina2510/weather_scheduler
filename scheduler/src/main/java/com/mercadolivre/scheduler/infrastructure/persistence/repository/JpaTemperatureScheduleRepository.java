package com.mercadolivre.scheduler.infrastructure.persistence.repository;

import com.mercadolivre.scheduler.domain.ScheduleStatus;
import com.mercadolivre.scheduler.infrastructure.persistence.entity.TemperatureScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface JpaTemperatureScheduleRepository extends JpaRepository<TemperatureScheduleEntity, Long> {
    List<TemperatureScheduleEntity> findByStatusInAndScheduleDateLessThanEqual(Set<ScheduleStatus> status, LocalDate date);
} 