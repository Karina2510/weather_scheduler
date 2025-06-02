package com.mercadolivre.scheduler.application.usecase;

import com.mercadolivre.scheduler.domain.ScheduleStatus;
import com.mercadolivre.scheduler.domain.TemperatureSchedule;
import com.mercadolivre.scheduler.domain.repository.TemperatureScheduleRepository;
import com.mercadolivre.scheduler.infrastructure.http.CptecClient;
import com.mercadolivre.scheduler.infrastructure.http.response.CidadeResponse;
import com.mercadolivre.scheduler.infrastructure.http.response.PrevisaoResponse;
import com.mercadolivre.scheduler.infrastructure.sqs.SqsTemperatureResultSender;
import com.mercadolivre.scheduler.infrastructure.sqs.message.TemperatureResultMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProcessTemperatureSchedulesUseCase {

    private static final Set<ScheduleStatus> PROCESSABLE_STATUS = Set.of(ScheduleStatus.AGENDADO, ScheduleStatus.ERRO);
    
    private final TemperatureScheduleRepository repository;
    private final CptecClient cptecClient;
    private final SqsTemperatureResultSender sqsSender;

    public void execute() {
        log.info("Starting to process temperature schedules...");
        
        var schedules = repository.findByStatusInAndScheduleDateLessThanEqual(PROCESSABLE_STATUS, LocalDate.now());
        schedules.forEach(this::processSchedule);
                
        log.info("Finished processing temperature schedules.");
    }

    private void processSchedule(TemperatureSchedule schedule) {
        try {
            log.info("Processing schedule for city {} and date {}", 
                    schedule.getCityId(), 
                    schedule.getScheduleDate());

            schedule.setStatus(ScheduleStatus.PROCESSANDO);
            repository.save(schedule);

            CidadeResponse cidade = cptecClient.getTemperature(schedule.getCityId());
            sendResultToSqs(schedule, cidade);

            schedule.setStatus(ScheduleStatus.CONCLUIDO);
            repository.save(schedule);

            log.info("Schedule processed successfully");
        } catch (Exception e) {
            log.error("Error processing schedule: {}", e.getMessage(), e);
            schedule.setStatus(ScheduleStatus.ERRO);
            repository.save(schedule);
        }
    }

    private void sendResultToSqs(TemperatureSchedule schedule, CidadeResponse cidade) {
        List<TemperatureResultMessage.ForecastData> forecasts = cidade.getPrevisoes().stream()
                .map(this::mapForecast)
                .collect(Collectors.toList());

        log.info("Sending message to SQS for city: {} (scheduleDate: {})", 
                schedule.getCityId(), 
                schedule.getScheduleDate());

        TemperatureResultMessage message = TemperatureResultMessage.builder()
                .scheduleId(schedule.getScheduleId())
                .cityId(schedule.getCityId())
                .cityName(cidade.getNome())
                .state(cidade.getUf())
                .requestDate(schedule.getScheduleDate())
                .forecasts(forecasts)
                .build();

        sqsSender.send(message);
    }

    private TemperatureResultMessage.ForecastData mapForecast(PrevisaoResponse previsao) {
        return TemperatureResultMessage.ForecastData.builder()
                .date(previsao.getDia())
                .condition(previsao.getTempo())
                .minTemperature(previsao.getMinima())
                .maxTemperature(previsao.getMaxima())
                .uvIndex(previsao.getIuv())
                .build();
    }
} 