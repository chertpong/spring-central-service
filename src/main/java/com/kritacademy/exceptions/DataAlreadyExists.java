package com.kritacademy.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by chertpong.github.io on 22/06/2016.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DataAlreadyExists extends RuntimeException {
    public DataAlreadyExists() {
        super("Data is already exists");
    }

    public DataAlreadyExists(String message) {
        super(message);
    }
}
