package com.mercadolivre.webapp.application.usecase;

import com.mercadolivre.webapp.application.dto.TemperatureResponse;
import com.mercadolivre.webapp.domain.Schedule;
import com.mercadolivre.webapp.domain.Temperature;
import com.mercadolivre.webapp.domain.enums.ScheduleStatus;
import com.mercadolivre.webapp.domain.repository.ScheduleRepository;
import com.mercadolivre.webapp.domain.repository.TemperatureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class GetTemperaturesUseCase {

    private final TemperatureRepository temperatureRepository;
    private final ScheduleRepository scheduleRepository;

    public GetTemperaturesResult execute(Long scheduleId) {
        log.info("Getting temperatures for scheduleId: {}", scheduleId);

        // Busca o agendamento
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElse(null);
        
        if (schedule == null) {
            log.info("Schedule not found for ID: {}", scheduleId);
            return GetTemperaturesResult.notFound("Agendamento não encontrado");
        }
        
        // Verifica se o agendamento está cancelado
        if (schedule.getStatus() == ScheduleStatus.CANCELADO) {
            log.info("Schedule {} is canceled", scheduleId);
            return GetTemperaturesResult.pending("Agendamento cancelado");
        }
        
        // Verifica se o agendamento ainda está pendente
        if (schedule.getStatus() == ScheduleStatus.AGENDADO) {
            log.info("Schedule {} is still pending", scheduleId);
            return GetTemperaturesResult.pending("Agendamento ainda não disponível");
        }

        List<Temperature> temperatures = temperatureRepository.findByScheduleId(scheduleId);
        
        if (temperatures.isEmpty()) {
            log.info("No temperatures found for scheduleId: {}", scheduleId);
            return GetTemperaturesResult.notFound("Temperaturas não encontradas");
        }

        // Pega os dados da primeira temperatura para montar o cabeçalho
        Temperature firstTemp = temperatures.get(0);

        List<TemperatureResponse.ForecastData> forecasts = temperatures.stream()
                .map(temp -> TemperatureResponse.ForecastData.builder()
                        .date(temp.getForecastDate())
                        .condition(temp.getCondition())
                        .minTemperature(temp.getMinTemperature())
                        .maxTemperature(temp.getMaxTemperature())
                        .uvIndex(temp.getUvIndex())
                        .build())
                .collect(Collectors.toList());

        TemperatureResponse response = TemperatureResponse.builder()
                .cityId(firstTemp.getCityId())
                .cityName(firstTemp.getCityName())
                .state(firstTemp.getState())
                .forecasts(forecasts)
                .build();

        return GetTemperaturesResult.success(response);
    }
} 