package com.mercadolivre.webapp.infrastructure.persistence.mapper;

import com.mercadolivre.webapp.domain.Wave;
import com.mercadolivre.webapp.infrastructure.persistence.entity.WaveEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WaveMapper {

    public Wave toDomain(WaveEntity entity) {
        log.debug("Converting wave entity to domain. Entity forecastDate: {}", entity.getForecastDate());
        
        return Wave.builder()
                .id(entity.getId())
                .scheduleId(entity.getScheduleId())
                .cityId(entity.getCityId())
                .cityName(entity.getCityName())
                .state(entity.getState())
                .forecastDate(entity.getForecastDate())
                .period(entity.getPeriod())
                .time(entity.getTime())
                .waveAgitation(entity.getWaveAgitation())
                .waveHeight(entity.getWaveHeight())
                .waveDirection(entity.getWaveDirection())
                .windSpeed(entity.getWindSpeed())
                .windDirection(entity.getWindDirection())
                .build();
    }

    public WaveEntity toEntity(Wave domain) {
        log.debug("Converting wave domain to entity. Domain forecastDate: {}", domain.getForecastDate());
        
        return WaveEntity.builder()
                .id(domain.getId())
                .scheduleId(domain.getScheduleId())
                .cityId(domain.getCityId())
                .cityName(domain.getCityName())
                .state(domain.getState())
                .forecastDate(domain.getForecastDate())
                .period(domain.getPeriod())
                .time(domain.getTime())
                .waveAgitation(domain.getWaveAgitation())
                .waveHeight(domain.getWaveHeight())
                .waveDirection(domain.getWaveDirection())
                .windSpeed(domain.getWindSpeed())
                .windDirection(domain.getWindDirection())
                .build();
    }
} 