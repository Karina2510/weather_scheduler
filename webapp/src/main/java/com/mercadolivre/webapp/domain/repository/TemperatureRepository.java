package com.mercadolivre.webapp.domain.repository;

import com.mercadolivre.webapp.domain.Temperature;

import java.util.List;

public interface TemperatureRepository {
    List<Temperature> saveAll(List<Temperature> temperatures);
    List<Temperature> findByScheduleId(Long scheduleId);
    Temperature save(Temperature temperature);
} 