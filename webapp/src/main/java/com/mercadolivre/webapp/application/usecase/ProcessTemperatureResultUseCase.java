package com.mercadolivre.webapp.application.usecase;

import com.mercadolivre.webapp.application.dto.ProcessTemperatureResultInput;
import com.mercadolivre.webapp.domain.Schedule;
import com.mercadolivre.webapp.domain.Temperature;
import com.mercadolivre.webapp.domain.enums.ScheduleStatus;
import com.mercadolivre.webapp.domain.repository.ScheduleRepository;
import com.mercadolivre.webapp.domain.repository.TemperatureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProcessTemperatureResultUseCase {

    private final TemperatureRepository temperatureRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public void execute(ProcessTemperatureResultInput input) {
        log.info("Processing temperature result for scheduleId: {}", input.getScheduleId());

        // Salva as previsões de temperatura
        List<Temperature> temperatures = input.getForecasts().stream()
                .map(forecast -> Temperature.builder()
                        .scheduleId(input.getScheduleId())
                        .cityId(input.getCityId())
                        .cityName(input.getCityName())
                        .state(input.getState())
                        .forecastDate(forecast.getDate())
                        .condition(forecast.getCondition())
                        .minTemperature(forecast.getMinTemperature())
                        .maxTemperature(forecast.getMaxTemperature())
                        .uvIndex(forecast.getUvIndex())
                        .build())
                .collect(Collectors.toList());

        temperatureRepository.saveAll(temperatures);
        log.info("Saved {} temperature forecasts", temperatures.size());

        // Atualiza o status do agendamento para COMPLETED
        Schedule schedule = scheduleRepository.findById(input.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Schedule not found: " + input.getScheduleId()));
        
        schedule.setStatus(ScheduleStatus.CONCLUIDO);
        scheduleRepository.save(schedule);
        log.info("Updated schedule status to CONCLUIDO");
    }
} 