package com.mercadolivre.webapp.domain.message;

import com.mercadolivre.webapp.domain.Schedule;

public interface ScheduleQueue {
    void send(Schedule schedule);
} 