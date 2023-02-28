package com.example.cloudstorage.exception;


import com.example.cloudstorage.dto.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
@Slf4j
public class CloudServiceGlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadCredentialsException.class)
    public ErrorDTO badCredentialsExceptionHandler(BadCredentialsException e) {
        log.error(e.getMessage(), e);
        return ErrorDTO.builder()
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CloudServiceFileException.class)
    public ErrorDTO cloudServiceFileException(CloudServiceFileException e) {
        log.error(e.getMessage(), e);
        return ErrorDTO.builder()
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorDTO exception(Exception e) {
        log.error(e.getMessage(), e);
        return ErrorDTO.builder()
                .message(e.getMessage())
                .build();
    }
}