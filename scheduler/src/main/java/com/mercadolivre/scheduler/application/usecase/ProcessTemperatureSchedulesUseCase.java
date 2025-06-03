package com.mercadolivre.scheduler.application.usecase;

import com.mercadolivre.scheduler.domain.ScheduleStatus;
import com.mercadolivre.scheduler.domain.TemperatureSchedule;
import com.mercadolivre.scheduler.domain.repository.TemperatureScheduleRepository;
import com.mercadolivre.scheduler.infrastructure.http.CptecClient;
import com.mercadolivre.scheduler.infrastructure.http.response.CidadeResponse;
import com.mercadolivre.scheduler.infrastructure.http.response.OndasResponse;
import com.mercadolivre.scheduler.infrastructure.http.response.PrevisaoResponse;
import com.mercadolivre.scheduler.infrastructure.sqs.SqsTemperatureResultSender;
import com.mercadolivre.scheduler.infrastructure.sqs.message.TemperatureResultMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProcessTemperatureSchedulesUseCase {

    private static final Set<ScheduleStatus> PROCESSABLE_STATUS = Set.of(ScheduleStatus.AGENDADO, ScheduleStatus.ERRO);
    private static final String CURRENT_DAY_PARAM = "0"; // Parâmetro para buscar ondas do dia atual
    
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
            
            // Tenta buscar dados de onda do dia atual
            OndasResponse onda = null;
            try {
                onda = cptecClient.getWaves(schedule.getCityId(), CURRENT_DAY_PARAM);
                
                // Verifica se os dados de onda são válidos
                if (!onda.hasValidData()) {
                    log.info("City {} has no valid wave data", schedule.getCityId());
                    onda = null;
                } else {
                    log.info("Current day wave data found for city {}", schedule.getCityId());
                }
            } catch (HttpServerErrorException e) {
                log.info("Error getting wave data for city {}: {}", schedule.getCityId(), e.getMessage());
            }

            sendResultToSqs(schedule, cidade, onda);

            schedule.setStatus(ScheduleStatus.CONCLUIDO);
            repository.save(schedule);

            log.info("Schedule processed successfully");
        } catch (Exception e) {
            log.error("Error processing schedule: {}", e.getMessage(), e);
            schedule.setStatus(ScheduleStatus.ERRO);
            repository.save(schedule);
        }
    }

    private void sendResultToSqs(TemperatureSchedule schedule, CidadeResponse cidade, OndasResponse onda) {
        List<TemperatureResultMessage.ForecastData> forecasts = cidade.getPrevisoes().stream()
                .map(previsao -> mapForecast(previsao))
                .collect(Collectors.toList());

        var messageBuilder = TemperatureResultMessage.builder()
                .scheduleId(schedule.getScheduleId())
                .cityId(schedule.getCityId())
                .cityName(cidade.getNome())
                .state(cidade.getUf())
                .requestDate(schedule.getScheduleDate())
                .forecasts(forecasts);

        // Adiciona dados de onda se disponíveis e válidos
        if (onda != null && onda.hasValidData()) {
            messageBuilder.wave(
                TemperatureResultMessage.WaveData.builder()
                    .date(onda.getAtualizacaoAsLocalDate())
                    .morning(mapWavePeriod(onda.getManha()))
                    .afternoon(mapWavePeriod(onda.getTarde()))
                    .night(mapWavePeriod(onda.getNoite()))
                    .build()
            );
        }

        sqsSender.send(messageBuilder.build());
    }

    private TemperatureResultMessage.WavePeriodData mapWavePeriod(OndasResponse.PeriodoResponse periodo) {
        return TemperatureResultMessage.WavePeriodData.builder()
            .time(periodo.getDia())
            .agitation(periodo.getAgitacao())
            .waveHeight(periodo.getAltura())
            .waveDirection(periodo.getDirecao())
            .windSpeed(periodo.getVento())
            .windDirection(periodo.getVento_dir())
            .build();
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