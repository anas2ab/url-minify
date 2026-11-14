package com.anasboot.url_minify.url;

public class AliasTakenException extends RuntimeException {
    public AliasTakenException(String message) {
        super(message);
    }

    public AliasTakenException(String string, Throwable cause) {
        super(string, cause);
    }
}
