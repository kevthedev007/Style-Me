package com.interswitch.StyleMe.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class StyleMeException extends Exception {
    private final HttpStatus httpStatus;
    public StyleMeException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
