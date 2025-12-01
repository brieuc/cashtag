package com.brieuc.cashtag;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.brieuc.cashtag.dto.ErrorResponse;
import com.brieuc.cashtag.exception.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

@ExceptionHandler(EntityNotFoundException.class)      
      public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(exception.getMessage()));
      }
}
