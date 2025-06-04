package com.mercadolivre.webapp.infrastructure.api.weather;

import com.mercadolivre.webapp.application.usecase.CancelScheduleUseCase;
import com.mercadolivre.webapp.application.usecase.CreateScheduleUseCase;
import com.mercadolivre.webapp.application.usecase.GetTemperaturesUseCase;
import com.mercadolivre.webapp.application.dto.GetTemperaturesResult;
import com.mercadolivre.webapp.application.dto.CancelScheduleResult;
import com.mercadolivre.webapp.application.usecase.dto.CreateScheduleInput;
import com.mercadolivre.webapp.infrastructure.api.weather.dto.ErrorResponse;
import com.mercadolivre.webapp.infrastructure.api.weather.dto.ScheduleRequest;
import com.mercadolivre.webapp.infrastructure.api.weather.dto.ScheduleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/weather")
public class WeatherController {

    private final CreateScheduleUseCase createScheduleUseCase;
    private final GetTemperaturesUseCase getTemperaturesUseCase;
    private final CancelScheduleUseCase cancelScheduleUseCase;

    @PostMapping("/schedule")
    public ResponseEntity<ScheduleResponse> createSchedule(@Valid @RequestBody ScheduleRequest request) {
        log.info("Received request: " + request);

        var input = CreateScheduleInput.builder()
                .cityId(request.getCityId())
                .scheduleDate(request.getScheduleDate())
                .build();

        var schedule = createScheduleUseCase.execute(input);

        var response = new ScheduleResponse();
        response.setId(schedule.getId().toString());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/schedule/{id}")
    public ResponseEntity<?> getScheduleById(@PathVariable String id) {
        log.info("Fetching temperatures for schedule ID: {}", id);

        try {
            Long scheduleId = Long.parseLong(id);
            GetTemperaturesResult result = getTemperaturesUseCase.execute(scheduleId);
            
            if (!result.isSuccess()) {
                HttpStatus status = switch (result.getType()) {
                    case PENDING -> HttpStatus.BAD_REQUEST;
                    case NOT_FOUND -> HttpStatus.NOT_FOUND;
                    default -> HttpStatus.INTERNAL_SERVER_ERROR;
                };
                
                return ResponseEntity.status(status)
                        .body(new ErrorResponse(result.getErrorMessage()));
            }
            
            return ResponseEntity.ok(result.getData());
        } catch (NumberFormatException e) {
            log.error("Invalid schedule ID format: {}", id);
            return ResponseEntity.badRequest().body(new ErrorResponse("ID do agendamento inválido"));
        }
    }

    @DeleteMapping("/schedule/{id}")
    public ResponseEntity<ErrorResponse> cancelSchedule(@PathVariable String id) {
        log.info("Canceling schedule with ID: {}", id);

        try {
            Long scheduleId = Long.parseLong(id);
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
        } catch (NumberFormatException e) {
            log.error("Invalid schedule ID format: {}", id);
            return ResponseEntity.badRequest().body(new ErrorResponse("ID do agendamento inválido"));
        }
    }
}
