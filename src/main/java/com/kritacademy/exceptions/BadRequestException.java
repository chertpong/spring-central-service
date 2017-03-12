package com.kritacademy.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by chertpong.github.io on 22/06/2016.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    public BadRequestException() {
        super("Input is not in correct format");
    }

    public BadRequestException(String message) {
        super(message);
    }
}