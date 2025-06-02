package com.mercadolivre.scheduler.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemperatureSchedule {
    private Long id;
    private Long scheduleId;
    private String cityId;
    private LocalDate scheduleDate;
    private ScheduleStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Double temperature;

    public TemperatureSchedule(Long scheduleId, String cityId, LocalDate scheduleDate) {
        LocalDateTime now = LocalDateTime.now();
        this.scheduleId = scheduleId;
        this.cityId = cityId;
        this.scheduleDate = scheduleDate;
        this.status = ScheduleStatus.AGENDADO;
        this.createdAt = now;
        this.updatedAt = now;
    }
} 