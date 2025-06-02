package com.mercadolivre.webapp.application.usecase;

import com.mercadolivre.webapp.domain.Schedule;
import com.mercadolivre.webapp.domain.enums.ScheduleStatus;
import com.mercadolivre.webapp.domain.repository.ScheduleRepository;
import com.mercadolivre.webapp.infrastructure.feign.SchedulerClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class CancelScheduleUseCase {

    private final ScheduleRepository scheduleRepository;
    private final SchedulerClient schedulerClient;

    @Transactional
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

            // Depois tenta cancelar no serviço scheduler
            try {
                schedulerClient.cancelSchedule(scheduleId);
            } catch (FeignException e) {
                log.error("Error canceling schedule in scheduler service: {}", e.getMessage(), e);
                // Mesmo com erro no scheduler, mantemos o status CANCELADO no webapp
                // pois o usuário solicitou o cancelamento e ele não deve ser mais processado
            }

            log.info("Schedule {} canceled successfully", scheduleId);
            return CancelScheduleResult.success();
        } catch (Exception e) {
            log.error("Error canceling schedule: {}", e.getMessage(), e);
            return CancelScheduleResult.error("Erro ao cancelar agendamento");
        }
    }
} 