package com.mercadolivre.scheduler.infrastructure.sqs.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class TemperatureResultMessage {
    private Long scheduleId;
    private String cityId;
    private String cityName;
    private String state;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate requestDate;
    
    private List<ForecastData> forecasts;

    @Data
    @Builder
    public static class ForecastData {
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate date;
        private String condition;
        private Integer minTemperature;
        private Integer maxTemperature;
        private Double uvIndex;
    }
} 