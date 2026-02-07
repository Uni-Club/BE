package ycyh.uniclub.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
    private final String message;
    private final HttpStatus httpStatus;

    public CustomException(String message) {
        super(message);
        this.message = message;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public CustomException(String message, HttpStatus httpStatus) {
        super(message);
        this.message = message;
        this.httpStatus = httpStatus;
    }
}


