package com.mini.cti.core.exceptions;

public class InvalidIpAddressException extends AppGenericException {

    private static final String DEFAULT_CODE = "Invalid IpAddress Format";

    public InvalidIpAddressException(String message) {
        super(DEFAULT_CODE, "Invalid IP address format: " + message);
    }
}
