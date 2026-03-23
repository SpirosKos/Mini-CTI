package com.mini.cti.core.exceptions;

public class CisaApiException extends AppGenericException {

    private static final String DEFAULT_CODE = "CISA-KEV ERROR";

    public CisaApiException(String message) {
        super(DEFAULT_CODE,message);
    }
}
