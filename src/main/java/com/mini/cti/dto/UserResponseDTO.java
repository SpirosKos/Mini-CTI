package com.mini.cti.dto;

import com.mini.cti.enums.Role;

import java.util.UUID;

public record UserResponseDTO(String email, Role role, UUID uuid) {
}
