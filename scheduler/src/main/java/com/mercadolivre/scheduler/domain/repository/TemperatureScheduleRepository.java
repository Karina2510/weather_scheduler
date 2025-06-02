package com.mercadolivre.scheduler.domain.repository;

import com.mercadolivre.scheduler.domain.ScheduleStatus;
import com.mercadolivre.scheduler.domain.TemperatureSchedule;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TemperatureScheduleRepository {
    TemperatureSchedule save(TemperatureSchedule temperatureSchedule);
    List<TemperatureSchedule> findByStatusInAndScheduleDateLessThanEqual(Set<ScheduleStatus> status, LocalDate date);
    Optional<TemperatureSchedule> findById(Long id);
} 