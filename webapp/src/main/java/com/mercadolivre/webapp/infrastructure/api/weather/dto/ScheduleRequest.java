package com.mercadolivre.webapp.infrastructure.api.weather.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ScheduleRequest {
    @NotNull
    private String cityId;

    @NotNull
    private LocalDate scheduleDate;
}
