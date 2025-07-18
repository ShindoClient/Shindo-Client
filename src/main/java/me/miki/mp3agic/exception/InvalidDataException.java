package me.miki.mp3agic.exception;

import me.miki.mp3agic.BaseException;

public class InvalidDataException extends BaseException {

    private static final long serialVersionUID = 1L;

    public InvalidDataException() {
        super();
    }

    public InvalidDataException(String message) {
        super(message);
    }

    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
