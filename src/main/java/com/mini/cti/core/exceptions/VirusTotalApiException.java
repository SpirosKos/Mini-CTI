package com.mini.cti.core.exceptions;

public class VirusTotalApiException extends AppGenericException {

    private static final String DEFAULT_CODE = "VIRUSTOTAL_ERROR";

    public VirusTotalApiException(String message) {
        super(DEFAULT_CODE ,message);
    }

    public VirusTotalApiException(String message, Throwable cause) {
        super(DEFAULT_CODE, message);
        initCause(cause);
    }
}
