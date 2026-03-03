package com.mini.cti.core.exceptions;

public class UserAlreadyExistsException extends AppGenericException {
    private static final String DEFAULT_CODE = "Already Exists.";

    public UserAlreadyExistsException(String code,String message) {
        super(code + DEFAULT_CODE, message);
    }
}
