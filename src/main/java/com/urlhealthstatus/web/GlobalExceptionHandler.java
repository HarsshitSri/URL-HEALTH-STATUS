package com.urlhealthstatus.web;

import com.urlhealthstatus.dto.ErrorResponse;
import com.urlhealthstatus.exception.FetchFailedException;
import com.urlhealthstatus.exception.FetchTimeoutException;
import com.urlhealthstatus.exception.InvalidUrlException;
import com.urlhealthstatus.exception.NonHtmlException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUrl(InvalidUrlException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage(), "INVALID_URL"));
    }

    @ExceptionHandler(FetchTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleTimeout(FetchTimeoutException ex) {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .body(new ErrorResponse(ex.getMessage(), "TIMEOUT"));
    }

    @ExceptionHandler(NonHtmlException.class)
    public ResponseEntity<ErrorResponse> handleNonHtml(NonHtmlException ex) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(new ErrorResponse(ex.getMessage(), "NON_HTML"));
    }

    @ExceptionHandler(FetchFailedException.class)
    public ResponseEntity<ErrorResponse> handleFetchFailed(FetchFailedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse(ex.getMessage(), "FETCH_FAILED"));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Resource not found", "NOT_FOUND"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Unexpected server error", "INTERNAL_ERROR"));
    }
}
