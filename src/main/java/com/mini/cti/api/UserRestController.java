package com.mini.cti.api;


import com.mini.cti.core.exceptions.InvalidCredentialException;
import com.mini.cti.core.exceptions.UserAlreadyExistsException;
import com.mini.cti.core.exceptions.UserNotFoundException;
import com.mini.cti.dto.UserRequestDTO;
import com.mini.cti.dto.UserResponseDTO;
import com.mini.cti.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserRestController {

    private final IUserService iUserService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) throws UserAlreadyExistsException {
        UserResponseDTO responseDTO = iUserService.registerUser(userRequestDTO);
        return ResponseEntity.status(201).body(responseDTO);
    }

    @GetMapping("/users/{uuid}")
    public ResponseEntity<UserResponseDTO> getUserByUUID(@PathVariable UUID uuid) throws UserNotFoundException {
        UserResponseDTO responseDTO = iUserService.getUserByUUID(uuid);
        return ResponseEntity.status(200).body(responseDTO);
    }

}
