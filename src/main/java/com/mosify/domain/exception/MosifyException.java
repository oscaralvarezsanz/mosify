package com.mosify.domain.exception;

public class MosifyException extends RuntimeException {
    private final ErrorCode errorCode;

    public MosifyException(ErrorCode errorCode) {
        super(errorCode.getDefaultDescription());
        this.errorCode = errorCode;
    }

    public MosifyException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
