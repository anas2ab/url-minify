package com.anasboot.url_minify.url;

import org.springframework.dao.DataIntegrityViolationException;

public class AliasTakenException extends RuntimeException {
    public AliasTakenException(String message) {
        super(message);
    }

    public AliasTakenException(String string, DataIntegrityViolationException e) {
        super(string, e);
    }
}
