package com.kritacademy.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by chertpong.github.io on 22/06/2016.
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class DataNotFoundException extends RuntimeException{
    public DataNotFoundException() {
        super("Data not found");
    }

    public DataNotFoundException(String message) {
        super(message);
    }
}
