package com.mindspark.advice;

import com.mindspark.exception.AccountNotFoundException;
import com.mindspark.exception.InvalidDateException;
import com.mindspark.exception.UsersNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class MyControllerAdvice {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<String> handlerAccountNotFoundException(AccountNotFoundException accountNotFoundException){
        return new ResponseEntity<>("Please Provide correct Account Number is incorrect Account Number", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<String> handlerInvalidDateException(InvalidDateException invalidDateException){
        return new ResponseEntity<>("Please provide Date in can not be null...!",HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsersNotFoundException.class)
    public ResponseEntity<String> handlerUsersNotFoundException(UsersNotFoundException usersNotFoundException){
        return new ResponseEntity<>("Please provide Proper Date",HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handlerMethodArgumentNotValidException(MethodArgumentNotValidException exception){
        Map<String, String> errorMap = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->{
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        return errorMap;
    }
}
