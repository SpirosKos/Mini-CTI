package com.mini.cti.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserRequestDTO(

        @NotBlank(message = "Email cannot be empty.")
        @Email(message = "Email must be valid.")
        String email,

        @NotBlank(message = "Password must be at least 8 characters 1 Capital 1 lower 1 number and a special character.")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*?<>]).{8,}$")
        String password) {

}
