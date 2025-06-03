package com.mercadolivre.webapp.infrastructure.sqs.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemperatureResultMessage {
    private Long scheduleId;
    private String cityId;
    private String cityName;
    private String state;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate requestDate;
    
    private List<ForecastData> forecasts;
    private WaveData wave;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ForecastData {
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate date;
        private String condition;
        private Integer minTemperature;
        private Integer maxTemperature;
        private Double uvIndex;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WaveData {
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate date;
        private WavePeriodData morning;
        private WavePeriodData afternoon;
        private WavePeriodData night;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WavePeriodData {
        private String time;
        private String agitation;
        private Double waveHeight;
        private String waveDirection;
        private Double windSpeed;
        private String windDirection;
    }
} 