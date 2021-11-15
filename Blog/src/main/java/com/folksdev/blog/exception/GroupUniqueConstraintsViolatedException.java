package com.folksdev.blog.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class GroupUniqueConstraintsViolatedException extends RuntimeException{

    public GroupUniqueConstraintsViolatedException(String message) {
        super(message);
    }
}
