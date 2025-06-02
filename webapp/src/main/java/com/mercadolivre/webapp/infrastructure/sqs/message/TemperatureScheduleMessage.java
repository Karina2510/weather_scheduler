package com.mercadolivre.webapp.infrastructure.sqs.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemperatureScheduleMessage {
    private Long scheduleId;
    private String cityId;
    private LocalDate scheduleDate;
} 