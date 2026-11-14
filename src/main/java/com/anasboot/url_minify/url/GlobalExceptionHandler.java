package com.anasboot.url_minify.url;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AliasTakenException.class)
    public ProblemDetail handleAliasTakenException(AliasTakenException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }
    
    @ExceptionHandler (UrlNotFoundException.class)
    public ProblemDetail handleUrlNotFoundException(UrlNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler (UrlExpiredException.class)
    public ProblemDetail handleUrlExpiredException(UrlExpiredException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.GONE, ex.getMessage());
    }
}
