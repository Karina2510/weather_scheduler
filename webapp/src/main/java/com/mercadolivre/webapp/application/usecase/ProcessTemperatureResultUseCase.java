package com.mercadolivre.webapp.application.usecase;

import com.mercadolivre.webapp.application.dto.ProcessTemperatureResultInput;
import com.mercadolivre.webapp.domain.Schedule;
import com.mercadolivre.webapp.domain.Temperature;
import com.mercadolivre.webapp.domain.Wave;
import com.mercadolivre.webapp.domain.enums.ScheduleStatus;
import com.mercadolivre.webapp.domain.repository.ScheduleRepository;
import com.mercadolivre.webapp.domain.repository.TemperatureRepository;
import com.mercadolivre.webapp.domain.repository.WaveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProcessTemperatureResultUseCase {

    private final TemperatureRepository temperatureRepository;
    private final WaveRepository waveRepository;
    private final ScheduleRepository scheduleRepository;

    public void execute(ProcessTemperatureResultInput input) {
        log.info("Processing temperature result for scheduleId: {}", input.getScheduleId());

        // Salva os dados de temperatura
        input.getForecasts().forEach(forecast -> {
            Temperature temperature = Temperature.builder()
                    .scheduleId(input.getScheduleId())
                    .cityId(input.getCityId())
                    .cityName(input.getCityName())
                    .state(input.getState())
                    .forecastDate(forecast.getDate())
                    .condition(forecast.getCondition())
                    .minTemperature(forecast.getMinTemperature())
                    .maxTemperature(forecast.getMaxTemperature())
                    .uvIndex(forecast.getUvIndex())
                    .build();

            temperatureRepository.save(temperature);
        });

        // Se tiver dados de onda, salva também
        if (input.getWave() != null) {
            // Salva dados do período da manhã
            if (input.getWave().getMorning() != null) {
                saveWaveData(input, input.getWave().getMorning(), "MORNING");
            }
            
            // Salva dados do período da tarde
            if (input.getWave().getAfternoon() != null) {
                saveWaveData(input, input.getWave().getAfternoon(), "AFTERNOON");
            }
            
            // Salva dados do período da noite
            if (input.getWave().getNight() != null) {
                saveWaveData(input, input.getWave().getNight(), "NIGHT");
            }
            
            log.info("Wave data saved for scheduleId: {}", input.getScheduleId());
        }

        // Atualiza o status do agendamento para CONCLUIDO
        Schedule schedule = scheduleRepository.findById(input.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Schedule not found: " + input.getScheduleId()));
        
        schedule.setStatus(ScheduleStatus.CONCLUIDO);
        schedule.setUpdatedAt(LocalDateTime.now());
        scheduleRepository.save(schedule);

        log.info("Temperature result processed successfully for scheduleId: {}", input.getScheduleId());
    }

    private void saveWaveData(ProcessTemperatureResultInput input, ProcessTemperatureResultInput.WavePeriodData periodData, String period) {
        if (periodData == null || period == null) {
            log.warn("Skipping wave data save due to null period data or period");
            return;
        }

        Wave wave = Wave.builder()
                .scheduleId(input.getScheduleId())
                .cityId(input.getCityId())
                .cityName(input.getCityName())
                .state(input.getState())
                .forecastDate(input.getWave().getDate())
                .period(period)
                .time(periodData.getTime())
                .waveAgitation(periodData.getAgitation())
                .waveHeight(periodData.getWaveHeight())
                .waveDirection(periodData.getWaveDirection())
                .windSpeed(periodData.getWindSpeed())
                .windDirection(periodData.getWindDirection())
                .build();

        waveRepository.save(wave);
        log.debug("Wave data saved for period: {}", period);
    }
} 