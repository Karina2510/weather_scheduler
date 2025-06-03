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
@Table(name = "ondas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaveEntity {
    
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

    @Column(name = "period", nullable = false)
    private String period;

    @Column(name = "time", nullable = false)
    private String time;

    @Column(name = "wave_agitation")
    private String waveAgitation;

    @Column(name = "wave_height")
    private Double waveHeight;

    @Column(name = "wave_direction")
    private String waveDirection;

    @Column(name = "wind_speed")
    private Double windSpeed;

    @Column(name = "wind_direction")
    private String windDirection;
} 