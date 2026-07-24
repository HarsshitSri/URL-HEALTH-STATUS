package com.urlhealthstatus.exception;

public class FetchTimeoutException extends RuntimeException {

    public FetchTimeoutException(String message) {
        super(message);
    }

    public FetchTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
