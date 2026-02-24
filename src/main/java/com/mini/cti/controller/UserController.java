package com.mini.cti.controller;


import com.mini.cti.core.exceptions.InvalidCredentialException;
import com.mini.cti.core.exceptions.UserAlreadyExistsException;
import com.mini.cti.core.exceptions.UserNotFoundException;
import com.mini.cti.dto.ErrorResponseDTO;
import com.mini.cti.dto.UserRequestDTO;
import com.mini.cti.dto.UserResponseDTO;
import com.mini.cti.service.IUserService;
import com.mini.cti.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final IUserService iUserService;

    @PostMapping("/users/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) throws UserAlreadyExistsException {
        UserResponseDTO responseDTO = iUserService.registerUser(userRequestDTO);
        return ResponseEntity.status(201).body(responseDTO);
    }

    @PostMapping("/users/login")
    public ResponseEntity<UserResponseDTO> loginUser(@Valid @RequestBody UserRequestDTO userRequestDTO) throws InvalidCredentialException {
        UserResponseDTO responseDTO = iUserService.loginUser(userRequestDTO);
        return ResponseEntity.status(200).body(responseDTO);
    }

    @GetMapping("/users/{uuid}")
    public ResponseEntity<UserResponseDTO> getUserByUUID(@PathVariable UUID uuid) throws UserNotFoundException {
        UserResponseDTO responseDTO = iUserService.getUserByUUID(uuid);
        return ResponseEntity.status(200).body(responseDTO);
    }

}
