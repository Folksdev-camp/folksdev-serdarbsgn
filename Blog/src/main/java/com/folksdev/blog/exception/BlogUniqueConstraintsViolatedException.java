package com.folksdev.blog.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BlogUniqueConstraintsViolatedException extends RuntimeException{

    public BlogUniqueConstraintsViolatedException(String message) {
        super(message);
    }
}
