package com.mini.cti.core;

import com.mini.cti.core.exceptions.InvalidCredentialException;
import com.mini.cti.core.exceptions.UserAlreadyExistsException;
import com.mini.cti.core.exceptions.UserNotFoundException;
import com.mini.cti.core.exceptions.ValidationException;
import com.mini.cti.dto.ErrorResponseDTO;
import com.mini.cti.dto.ValidationErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.naming.AuthenticationException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ValidationErrorResponseDTO> handleValidationException (ValidationException e){


        log.warn("Validation Failed. Message={}" , e.getMessage());

        BindingResult bindingResult = e.getBindingResult();

        Map<String,String> errors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()){
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return new ResponseEntity<>(new ValidationErrorResponseDTO(e.getMessage(), errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCredentialException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidCredentialException(InvalidCredentialException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)    // 401 Unauthorized
                .body(new ErrorResponseDTO(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)        // 409 Conflict
                .body(new ErrorResponseDTO(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)       // 404 Not found
                .body(new ErrorResponseDTO(e.getCode(), e.getMessage()));
    }

    public ResponseEntity<ErrorResponseDTO> handleDatabaseException(DataAccessException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO("INTERNAL_SERVER_ERROR","A database error occurred."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO(("Something went wrong"), "An unexpected error occurred."));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthenticationException(AuthenticationException e,
                                                                                 HttpServletRequest request) {
       log.warn("Failed login for IP={}", request.getRemoteAddr());

       String errorCode = switch (e) {
           case BadCredentialsException ex -> "INVALID_CREDENTIALS";
           case DisabledException ex -> "ACCOUNT_DISABLED";
           case LockedException ex -> "ACCOUNT_LOCKED";
           case AccountExpiredException ex -> "ACCOUNT_EXPIRED";
           case CredentialsExpiredException ex -> "CREDENTIALS_EXPIRED";
           default -> "AUTHENTICATION_ERROR";
       };

       return ResponseEntity
               .status(HttpStatus.UNAUTHORIZED)     // 401 unauthorized
               .body(new ErrorResponseDTO(errorCode, e.getMessage()));
    }
}
