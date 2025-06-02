package com.mercadolivre.webapp.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class TemperatureResponse {
    private String cityId;
    private String cityName;
    private String state;
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