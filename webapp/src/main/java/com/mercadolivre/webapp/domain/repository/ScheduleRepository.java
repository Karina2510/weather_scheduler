package com.mercadolivre.webapp.domain.repository;

import com.mercadolivre.webapp.domain.Schedule;

import java.util.Optional;

public interface ScheduleRepository {
    Schedule save(Schedule schedule);
    Optional<Schedule> findById(Long id);
} 