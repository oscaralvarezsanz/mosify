package com.mosify.domain.exception;

public enum ErrorCode {
    RESOURCE_NOT_FOUND("Resource not found"),
    INSUFFICIENT_BALANCE("Insufficient points balance"),
    TASK_ALREADY_COMPLETED("Task already completed in the current period"),
    TASK_INACTIVE("Task is inactive"),
    BUSINESS_VALIDATION_ERROR("Business validation error");

    private final String defaultDescription;

    ErrorCode(String defaultDescription) {
        this.defaultDescription = defaultDescription;
    }

    public String getDefaultDescription() {
        return defaultDescription;
    }
}
