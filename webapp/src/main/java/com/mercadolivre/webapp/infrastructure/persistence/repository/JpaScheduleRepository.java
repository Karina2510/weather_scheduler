package com.mercadolivre.webapp.infrastructure.persistence.repository;

import com.mercadolivre.webapp.infrastructure.persistence.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaScheduleRepository extends JpaRepository<ScheduleEntity, Long> {
} 