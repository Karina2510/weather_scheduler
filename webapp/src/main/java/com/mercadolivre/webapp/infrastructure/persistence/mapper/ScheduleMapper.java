package com.mercadolivre.webapp.infrastructure.persistence.mapper;

import com.mercadolivre.webapp.domain.Schedule;
import com.mercadolivre.webapp.infrastructure.persistence.entity.ScheduleEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class ScheduleMapper {

    public Schedule toDomain(ScheduleEntity entity) {
        log.debug("Converting entity to domain. Entity scheduleDate: {}", entity.getScheduleDate());
        
        return Schedule.builder()
                .id(entity.getId())
                .cityId(entity.getCityId())
                .scheduleDate(entity.getScheduleDate())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ScheduleEntity toEntity(Schedule domain) {
        log.debug("Converting domain to entity. Domain scheduleDate: {}", domain.getScheduleDate());
        
        return ScheduleEntity.builder()
                .id(domain.getId())
                .cityId(domain.getCityId())
                .scheduleDate(domain.getScheduleDate())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt() != null ? domain.getCreatedAt() : LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
} 