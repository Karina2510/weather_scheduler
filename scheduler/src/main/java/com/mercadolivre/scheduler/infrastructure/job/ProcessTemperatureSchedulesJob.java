package com.mercadolivre.scheduler.infrastructure.job;

import com.mercadolivre.scheduler.application.usecase.ProcessTemperatureSchedulesUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProcessTemperatureSchedulesJob {

    private final ProcessTemperatureSchedulesUseCase useCase;

    @Scheduled(fixedDelay = 60000) // Executa a cada 1 minuto
    public void execute() {
        log.info("Starting temperature schedules job...");
        try {
            useCase.execute();
        } catch (Exception e) {
            log.error("Error executing temperature schedules job", e);
        }
        log.info("Finished temperature schedules job.");
    }
} 