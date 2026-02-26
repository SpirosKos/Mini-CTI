package com.mini.cti.dto;

import org.springframework.http.HttpStatus;

import java.util.Map;


public record ValidationErrorResponseDTO (String message, Map<String, String> errors){
}
