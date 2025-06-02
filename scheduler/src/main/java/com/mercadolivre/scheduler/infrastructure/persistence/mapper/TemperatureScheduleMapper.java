package com.mercadolivre.scheduler.infrastructure.persistence.mapper;

import com.mercadolivre.scheduler.domain.ScheduleStatus;
import com.mercadolivre.scheduler.domain.TemperatureSchedule;
import com.mercadolivre.scheduler.infrastructure.persistence.entity.TemperatureScheduleEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class TemperatureScheduleMapper {

    public TemperatureSchedule toDomain(TemperatureScheduleEntity entity) {
        log.debug("Converting entity to domain. Entity scheduleDate: {}", entity.getScheduleDate());
        
        var domain = new TemperatureSchedule(
                entity.getScheduleId(),
                entity.getCityId(),
                entity.getScheduleDate());

        domain.setId(entity.getId());
        domain.setStatus(entity.getStatus());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());

        log.debug("Domain object created. Domain scheduleDate: {}", domain.getScheduleDate());
        return domain;
    }

    public TemperatureScheduleEntity toEntity(TemperatureSchedule domain) {
        log.debug("Converting domain to entity. Domain scheduleDate: {}", domain.getScheduleDate());
        
        TemperatureScheduleEntity entity = new TemperatureScheduleEntity();
        entity.setId(domain.getId());
        entity.setScheduleId(domain.getScheduleId());
        entity.setCityId(domain.getCityId());
        entity.setScheduleDate(domain.getScheduleDate());
        entity.setStatus(domain.getStatus() != null ? domain.getStatus() : ScheduleStatus.AGENDADO);
        entity.setCreatedAt(domain.getCreatedAt() != null ? domain.getCreatedAt() : LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        log.debug("Entity object created. Entity scheduleDate: {}", entity.getScheduleDate());
        return entity;
    }
} 