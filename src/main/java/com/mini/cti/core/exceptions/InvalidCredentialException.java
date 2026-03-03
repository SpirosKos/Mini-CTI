package com.mini.cti.core.exceptions;

public class InvalidCredentialException extends AppGenericException {
    private static final String DEFAULT_CODE = "Invalid Credentials";

    public InvalidCredentialException(String code,String message) {
        super(code + DEFAULT_CODE, message);
    }
}
