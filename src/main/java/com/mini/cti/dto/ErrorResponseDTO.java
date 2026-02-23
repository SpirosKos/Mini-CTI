package com.mini.cti.dto;

public record ErrorResponseDTO(
        String message,
        int status
) {}
