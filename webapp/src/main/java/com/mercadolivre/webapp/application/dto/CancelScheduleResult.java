package com.mercadolivre.webapp.application.dto;

import lombok.Getter;

@Getter
public class CancelScheduleResult {
    private final boolean success;
    private final String errorMessage;
    private final ResultType type;

    public enum ResultType {
        SUCCESS,
        NOT_FOUND,
        ALREADY_PROCESSED,
        ERROR
    }

    private CancelScheduleResult(boolean success, String errorMessage, ResultType type) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.type = type;
    }

    public static CancelScheduleResult success() {
        return new CancelScheduleResult(true, null, ResultType.SUCCESS);
    }

    public static CancelScheduleResult notFound(String message) {
        return new CancelScheduleResult(false, message, ResultType.NOT_FOUND);
    }

    public static CancelScheduleResult alreadyProcessed(String message) {
        return new CancelScheduleResult(false, message, ResultType.ALREADY_PROCESSED);
    }

    public static CancelScheduleResult error(String message) {
        return new CancelScheduleResult(false, message, ResultType.ERROR);
    }
} 