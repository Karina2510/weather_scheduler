package com.mercadolivre.webapp.infrastructure.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "scheduler", url = "${scheduler.url}")
public interface SchedulerClient {
    
    @DeleteMapping("/api/schedules/{scheduleId}")
    ResponseEntity<Void> cancelSchedule(@PathVariable Long scheduleId);
} 