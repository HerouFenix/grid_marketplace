package com.api.demo.grid.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ExceptionDetails extends Exception {

    public ExceptionDetails(String message) {
        super(message);
    }
}
