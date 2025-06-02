package com.mercadolivre.scheduler.infrastructure.api;

import com.mercadolivre.scheduler.application.usecase.CancelScheduleUseCase;
import com.mercadolivre.scheduler.application.usecase.CancelScheduleResult;
import com.mercadolivre.scheduler.infrastructure.api.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@Slf4j
public class ScheduleController {

    private final CancelScheduleUseCase cancelScheduleUseCase;

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<?> cancelSchedule(@PathVariable Long scheduleId) {
        log.info("Received request to cancel schedule: {}", scheduleId);

        CancelScheduleResult result = cancelScheduleUseCase.execute(scheduleId);

        if (!result.isSuccess()) {
            HttpStatus status = switch (result.getType()) {
                case NOT_FOUND -> HttpStatus.NOT_FOUND;
                case ALREADY_PROCESSED -> HttpStatus.BAD_REQUEST;
                default -> HttpStatus.INTERNAL_SERVER_ERROR;
            };

            return ResponseEntity.status(status)
                    .body(new ErrorResponse(result.getErrorMessage()));
        }

        return ResponseEntity.noContent().build();
    }
} 