package com.mercadolivre.webapp.domain.port;

import com.mercadolivre.webapp.domain.Schedule;

public interface ScheduleQueue {
    void send(Schedule schedule);
} 