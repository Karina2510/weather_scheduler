package com.mercadolivre.webapp.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ProcessTemperatureResultInput {
    private Long scheduleId;
    private String cityId;
    private String cityName;
    private String state;
    private LocalDate requestDate;
    private List<ForecastData> forecasts;
    private WaveData wave;

    @Data
    @Builder
    public static class ForecastData {
        private LocalDate date;
        private String condition;
        private Integer minTemperature;
        private Integer maxTemperature;
        private Double uvIndex;
    }

    @Data
    @Builder
    public static class WaveData {
        private LocalDate date;
        private WavePeriodData morning;
        private WavePeriodData afternoon;
        private WavePeriodData night;
    }

    @Data
    @Builder
    public static class WavePeriodData {
        private String time;
        private String agitation;
        private Double waveHeight;
        private String waveDirection;
        private Double windSpeed;
        private String windDirection;
    }
} 