package com.mini.cti.core.exceptions;

public class InvalidIpAddressException extends RuntimeException {
    public InvalidIpAddressException(String ipAddress) {
        super("Invalid Ip address format: " + ipAddress);
    }
}
