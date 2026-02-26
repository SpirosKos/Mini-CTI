package com.mini.cti.core.exceptions;

import lombok.Getter;
import org.springframework.validation.BindingResult;

@Getter
public class ValidationException extends AppGenericException {

    private static final String DEFAULT_CODE = "Validation Error";
    private final BindingResult bindingResult;

    public ValidationException(String code,String message, BindingResult bindingResult) {
        super(code + DEFAULT_CODE, message);
        this.bindingResult = bindingResult;
  }
}
