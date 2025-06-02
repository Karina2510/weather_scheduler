package com.mercadolivre.webapp.infrastructure.persistence.mapper;

import com.mercadolivre.webapp.domain.Temperature;
import com.mercadolivre.webapp.infrastructure.persistence.entity.TemperatureEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TemperatureMapper {

    public Temperature toDomain(TemperatureEntity entity) {
        log.debug("Converting entity to domain. Entity forecastDate: {}", entity.getForecastDate());
        
        return Temperature.builder()
                .id(entity.getId())
                .scheduleId(entity.getScheduleId())
                .cityId(entity.getCityId())
                .cityName(entity.getCityName())
                .state(entity.getState())
                .forecastDate(entity.getForecastDate())
                .condition(entity.getCondition())
                .minTemperature(entity.getMinTemperature())
                .maxTemperature(entity.getMaxTemperature())
                .uvIndex(entity.getUvIndex())
                .build();
    }

    public TemperatureEntity toEntity(Temperature domain) {
        log.debug("Converting domain to entity. Domain forecastDate: {}", domain.getForecastDate());
        
        return TemperatureEntity.builder()
                .id(domain.getId())
                .scheduleId(domain.getScheduleId())
                .cityId(domain.getCityId())
                .cityName(domain.getCityName())
                .state(domain.getState())
                .forecastDate(domain.getForecastDate())
                .condition(domain.getCondition())
                .minTemperature(domain.getMinTemperature())
                .maxTemperature(domain.getMaxTemperature())
                .uvIndex(domain.getUvIndex())
                .build();
    }
} 