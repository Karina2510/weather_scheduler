package com.mercadolivre.webapp.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "temperaturas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemperatureEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(name = "city_id", nullable = false)
    private String cityId;

    @Column(name = "city_name", nullable = false)
    private String cityName;

    @Column(nullable = false)
    private String state;

    @Column(name = "forecast_date", nullable = false)
    private LocalDate forecastDate;

    @Column(name = "weather_condition", nullable = false)
    private String condition;

    @Column(name = "min_temperature", nullable = false)
    private Integer minTemperature;

    @Column(name = "max_temperature", nullable = false)
    private Integer maxTemperature;

    @Column(name = "uv_index")
    private Double uvIndex;
} 