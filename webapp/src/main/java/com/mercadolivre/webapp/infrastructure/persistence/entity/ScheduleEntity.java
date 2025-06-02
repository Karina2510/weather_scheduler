package com.mercadolivre.webapp.infrastructure.persistence.entity;

import com.mercadolivre.webapp.domain.Schedule;
import com.mercadolivre.webapp.domain.enums.ScheduleStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacoes_temperatura")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String cityId;
    private LocalDate scheduleDate;

    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ScheduleEntity fromDomain(Schedule schedule) {
        return ScheduleEntity.builder()
                .id(schedule.getId())
                .cityId(schedule.getCityId())
                .scheduleDate(schedule.getScheduleDate())
                .status(schedule.getStatus())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .build();
    }

    public Schedule toDomain() {
        return Schedule.builder()
                .id(this.id)
                .cityId(this.cityId)
                .scheduleDate(this.scheduleDate)
                .status(this.status)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
} 