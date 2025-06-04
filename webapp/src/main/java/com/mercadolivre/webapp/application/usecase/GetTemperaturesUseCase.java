package com.mercadolivre.webapp.application.usecase;

import com.mercadolivre.webapp.application.dto.GetTemperaturesResult;
import com.mercadolivre.webapp.application.dto.TemperatureResponse;
import com.mercadolivre.webapp.domain.Temperature;
import com.mercadolivre.webapp.domain.Wave;
import com.mercadolivre.webapp.domain.enums.ScheduleStatus;
import com.mercadolivre.webapp.domain.repository.ScheduleRepository;
import com.mercadolivre.webapp.domain.repository.TemperatureRepository;
import com.mercadolivre.webapp.domain.repository.WaveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class GetTemperaturesUseCase {

    private final ScheduleRepository scheduleRepository;
    private final TemperatureRepository temperatureRepository;
    private final WaveRepository waveRepository;

    public GetTemperaturesResult execute(Long scheduleId) {
        log.info("Getting temperatures for scheduleId: {}", scheduleId);

        // Busca o agendamento
        var schedule = scheduleRepository.findById(scheduleId)
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

        // Busca temperaturas
        List<Temperature> temperatures = temperatureRepository.findByScheduleId(scheduleId);
        
        if (temperatures.isEmpty()) {
            log.info("No temperatures found for scheduleId: {}", scheduleId);
            return GetTemperaturesResult.notFound("Temperaturas não encontradas");
        }

        // Busca dados de onda se existirem
        List<Wave> waves = waveRepository.findByScheduleId(scheduleId);

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

        var responseBuilder = TemperatureResponse.builder()
                .cityId(firstTemp.getCityId())
                .cityName(firstTemp.getCityName())
                .state(firstTemp.getState())
                .forecasts(forecasts);

        // Adiciona dados de onda se disponíveis
        if (!waves.isEmpty()) {
            // Agrupa as ondas por período
            Map<String, Wave> wavesByPeriod = waves.stream()
                .collect(Collectors.toMap(Wave::getPeriod, wave -> wave));

            responseBuilder.wave(
                TemperatureResponse.WaveData.builder()
                    .date(waves.get(0).getForecastDate())
                    .morning(mapWavePeriod(wavesByPeriod.get("MORNING")))
                    .afternoon(mapWavePeriod(wavesByPeriod.get("AFTERNOON")))
                    .night(mapWavePeriod(wavesByPeriod.get("NIGHT")))
                    .build()
            );
        }

        return GetTemperaturesResult.success(responseBuilder.build());
    }

    private TemperatureResponse.WavePeriodData mapWavePeriod(Wave wave) {
        if (wave == null) return null;
        
        return TemperatureResponse.WavePeriodData.builder()
            .time(wave.getTime())
            .agitation(wave.getWaveAgitation())
            .waveHeight(wave.getWaveHeight())
            .waveDirection(wave.getWaveDirection())
            .windSpeed(wave.getWindSpeed())
            .windDirection(wave.getWindDirection())
            .build();
    }
} 