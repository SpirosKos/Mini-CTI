package com.mini.cti.core.exceptions;

public class InvalidIpAddressException extends AppGenericException {

    private static final String DEFAULT_CODE = "Invalid IpAddress Format";

    public InvalidIpAddressException(String code,String message) {
        super(code +  DEFAULT_CODE, message);
    }
}
