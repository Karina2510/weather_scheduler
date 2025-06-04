package com.mercadolivre.webapp.application.usecase;

import com.mercadolivre.webapp.application.dto.CancelScheduleResult;
import com.mercadolivre.webapp.domain.Schedule;
import com.mercadolivre.webapp.domain.enums.ScheduleStatus;
import com.mercadolivre.webapp.domain.repository.ScheduleRepository;
import com.mercadolivre.webapp.infrastructure.feign.SchedulerClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CancelScheduleUseCase {

    private final ScheduleRepository scheduleRepository;
    private final SchedulerClient schedulerClient;

    public CancelScheduleResult execute(Long scheduleId) {
        log.info("Canceling schedule with ID: {}", scheduleId);

        // Busca o agendamento
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElse(null);

        if (schedule == null) {
            log.info("Schedule not found for ID: {}", scheduleId);
            return CancelScheduleResult.notFound("Agendamento não encontrado");
        }

        // Verifica se o agendamento já foi processado
        if (schedule.getStatus() != ScheduleStatus.AGENDADO) {
            log.info("Schedule {} cannot be canceled because it's already {}", scheduleId, schedule.getStatus());
            if (schedule.getStatus() == ScheduleStatus.CANCELADO) {
                return CancelScheduleResult.alreadyProcessed("Agendamento já cancelado");
            }
            return CancelScheduleResult.alreadyProcessed("Agendamento não pode ser cancelado pois já foi processado");
        }

        try {
            // Primeiro atualiza o status no banco de dados
            schedule.setStatus(ScheduleStatus.CANCELADO);
            scheduleRepository.save(schedule);

            log.info("Schedule {} canceled successfully", scheduleId);
            return CancelScheduleResult.success();
        } catch (Exception e) {
            log.error("Error canceling schedule: {}", e.getMessage(), e);
            return CancelScheduleResult.error("Erro ao cancelar agendamento");
        }
    }
} 