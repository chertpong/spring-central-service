package com.kritacademy.storage;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by chertpong.github.io on 22/06/2016.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StorageException extends RuntimeException{
    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

}
