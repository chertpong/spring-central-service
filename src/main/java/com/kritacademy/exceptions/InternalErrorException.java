package com.kritacademy.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by chertpong.github.io on 12/23/2016
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalErrorException extends RuntimeException {
    public InternalErrorException() {
        super("Internal error, please contact admin");
    }

    public InternalErrorException(String message) {
        super(message);
    }
}
