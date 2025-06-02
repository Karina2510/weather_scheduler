package com.mercadolivre.webapp.application.usecase;

import com.mercadolivre.webapp.application.dto.TemperatureResponse;
import lombok.Getter;

@Getter
public class GetTemperaturesResult {
    private final boolean success;
    private final String errorMessage;
    private final TemperatureResponse data;
    private final ResultType type;

    public enum ResultType {
        SUCCESS,
        NOT_FOUND,
        PENDING,
        ERROR
    }

    private GetTemperaturesResult(boolean success, String errorMessage, TemperatureResponse data, ResultType type) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.data = data;
        this.type = type;
    }

    public static GetTemperaturesResult success(TemperatureResponse data) {
        return new GetTemperaturesResult(true, null, data, ResultType.SUCCESS);
    }

    public static GetTemperaturesResult notFound(String message) {
        return new GetTemperaturesResult(false, message, null, ResultType.NOT_FOUND);
    }

    public static GetTemperaturesResult pending(String message) {
        return new GetTemperaturesResult(false, message, null, ResultType.PENDING);
    }

    public static GetTemperaturesResult error(String message) {
        return new GetTemperaturesResult(false, message, null, ResultType.ERROR);
    }
} 