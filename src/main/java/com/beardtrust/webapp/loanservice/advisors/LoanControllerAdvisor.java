package com.beardtrust.webapp.loanservice.advisors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class LoanControllerAdvisor extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.trace("HTTP invalid argument found...");
        log.debug("Headers found: " + headers.toString());
        log.debug("Status found: " + status.toString());
        log.debug("Request found: " + request.toString());
        log.warn(String.format("Encountered method argument not valid exception in %s", request.toString()));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());

        for (FieldError error : e.getFieldErrors()) {
            String name = error.getField();
            String message = error.getDefaultMessage();
            body.put(name, message);
        }
        log.trace("Returning bad request...");
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
