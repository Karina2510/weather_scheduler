package com.mercadolivre.scheduler.application.usecase;

import com.mercadolivre.scheduler.domain.ScheduleStatus;
import com.mercadolivre.scheduler.domain.TemperatureSchedule;
import com.mercadolivre.scheduler.domain.repository.TemperatureScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CancelScheduleUseCase {

    private final TemperatureScheduleRepository repository;

    public CancelScheduleResult execute(Long scheduleId) {
        log.info("Canceling schedule with ID: {}", scheduleId);

        // Busca o agendamento
        TemperatureSchedule schedule = repository.findById(scheduleId)
                .orElseGet(() -> {
                    log.info("Schedule not found for ID: {}", scheduleId);
                    return null;
                });

        if (schedule == null) {
            return CancelScheduleResult.notFound("Agendamento não encontrado");
        }

        // Verifica se o agendamento já foi processado
        if (schedule.getStatus() != ScheduleStatus.AGENDADO) {
            log.info("Schedule {} cannot be canceled because it's already {}", scheduleId, schedule.getStatus());
            return CancelScheduleResult.alreadyProcessed("Agendamento não pode ser cancelado pois já foi processado");
        }

        try {
            // Atualiza o status no banco de dados
            schedule.setStatus(ScheduleStatus.CANCELADO);
            repository.save(schedule);

            log.info("Schedule {} canceled successfully", scheduleId);
            return CancelScheduleResult.success();
        } catch (Exception e) {
            log.error("Error canceling schedule: {}", e.getMessage(), e);
            return CancelScheduleResult.error("Erro ao cancelar agendamento");
        }
    }
} 