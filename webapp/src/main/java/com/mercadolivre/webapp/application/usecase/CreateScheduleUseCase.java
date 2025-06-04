package com.mercadolivre.webapp.application.usecase;

import com.mercadolivre.webapp.domain.Schedule;
import com.mercadolivre.webapp.domain.repository.ScheduleRepository;
import com.mercadolivre.webapp.application.usecase.dto.CreateScheduleInput;
import com.mercadolivre.webapp.domain.enums.ScheduleStatus;
import com.mercadolivre.webapp.domain.message.ScheduleQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateScheduleUseCase {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleQueue scheduleQueue;

    public Schedule execute(CreateScheduleInput input) {
        var schedule = Schedule.builder()
                .cityId(input.getCityId())
                .scheduleDate(input.getScheduleDate())
                .status(ScheduleStatus.AGENDADO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        var savedSchedule = scheduleRepository.save(schedule);
        scheduleQueue.send(savedSchedule);

        return savedSchedule;
    }
} 