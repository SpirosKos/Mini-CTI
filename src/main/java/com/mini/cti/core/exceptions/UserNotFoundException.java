package com.mini.cti.core.exceptions;

public class UserNotFoundException extends AppGenericException {

    private static final String DEFAULT_CODE = "Invalid Argument";
    public UserNotFoundException(String code,String message) {
        super(code + DEFAULT_CODE, message);
    }
}
