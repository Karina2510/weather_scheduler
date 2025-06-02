package com.mercadolivre.scheduler.application.usecase;

import com.mercadolivre.scheduler.application.dto.TemperatureScheduleInput;
import com.mercadolivre.scheduler.domain.TemperatureSchedule;
import com.mercadolivre.scheduler.domain.repository.TemperatureScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduleTemperatureUseCase {
    
    private final TemperatureScheduleRepository repository;
    
    public void execute(TemperatureScheduleInput input) {
        TemperatureSchedule schedule = new TemperatureSchedule(
                input.getScheduleId(),
                input.getCityId(), 
                input.getScheduleDate());

        log.info("Creating temperature schedule for city {} and date {} (scheduleId: {})", 
                schedule.getCityId(), 
                schedule.getScheduleDate(),
                schedule.getScheduleId());

        repository.save(schedule);
    }
} 