package com.example.cprmtest.demo.exceptions.handler;

import com.example.cprmtest.demo.exceptions.user.BadParametersException;
import com.example.cprmtest.demo.exceptions.user.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@RestControllerAdvice
public class WebExceptionHandler extends ResponseEntityExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(WebExceptionHandler.class);

    private void logException(Exception e) {
        logger.error(e.getMessage());
        logger.error(Arrays.toString(e.getStackTrace()));
    }
    @ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR, reason="DB Down")
    @ExceptionHandler(DataAccessException.class)
    public void dbException(RuntimeException ex, HttpServletResponse response) throws IOException {
        logException(ex);
    }

    @ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Entity Not Found")
    @ExceptionHandler(EntityNotFoundException.class)
    public void entityNotFoundException(RuntimeException ex, HttpServletResponse response) {
        logException(ex);
    }

    @ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Malformed request")
    @ExceptionHandler(IllegalArgumentException.class)
    public void malformedInputException(RuntimeException ex, HttpServletResponse response){
        logException(ex);
    }

    @ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Bad parameters given")
    @ExceptionHandler(BadParametersException.class)
    public void badParameterException(RuntimeException ex, HttpServletResponse response) {
        logException(ex);
    }


}
