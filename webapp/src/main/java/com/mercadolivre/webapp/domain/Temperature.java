package com.mercadolivre.webapp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Temperature {
    private Long id;
    private Long scheduleId;
    private String cityId;
    private String cityName;
    private String state;
    private LocalDate forecastDate;
    private String condition;
    private Integer minTemperature;
    private Integer maxTemperature;
    private Double uvIndex;
} 