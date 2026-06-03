package com.pragma.Inventario.infrastructure.adapters.in.web;

import java.time.OffsetDateTime;
import java.util.Map;

public class ErrorResponse {

    private final OffsetDateTime timestamp;
    private final String error;
    private final String message;
    private final Map<String, Object> details;

    public ErrorResponse(String error, String message, Map<String, Object> details) {
        this.timestamp = OffsetDateTime.now();
        this.error = error;
        this.message = message;
        this.details = details;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}
