package com.mercadolivre.webapp.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class Wave {
    private Long id;
    private Long scheduleId;
    private String cityId;
    private String cityName;
    private String state;
    private LocalDate forecastDate;
    private String period;
    private String time;
    private String waveAgitation;
    private Double waveHeight;
    private String waveDirection;
    private Double windSpeed;
    private String windDirection;
} 