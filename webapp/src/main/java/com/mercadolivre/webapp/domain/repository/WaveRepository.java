package com.mercadolivre.webapp.domain.repository;

import com.mercadolivre.webapp.domain.Wave;

import java.util.List;

public interface WaveRepository {
    List<Wave> findByScheduleId(Long scheduleId);
    Wave save(Wave wave);
} 