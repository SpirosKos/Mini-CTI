package com.mini.cti.api;


import com.mini.cti.core.exceptions.InvalidCredentialException;
import com.mini.cti.core.exceptions.UserAlreadyExistsException;
import com.mini.cti.core.exceptions.UserNotFoundException;
import com.mini.cti.core.exceptions.ValidationException;
import com.mini.cti.dto.UserRequestDTO;
import com.mini.cti.dto.UserResponseDTO;
import com.mini.cti.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserRestController {

    private final IUserService iUserService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO,
                                                        BindingResult bindingResult) throws UserAlreadyExistsException, ValidationException, InvalidCredentialException {

        if (bindingResult.hasErrors()){
            throw new ValidationException("User", "Invalid user data", bindingResult);
        }

        UserResponseDTO responseDTO = iUserService.registerUser(userRequestDTO);

        // Generates the full URL
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/users/{uuid}")
                .buildAndExpand(responseDTO.uuid())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(responseDTO);

    }

    @GetMapping("/users/{uuid}")
    public ResponseEntity<UserResponseDTO> getUserByUUID(@PathVariable UUID uuid) throws UserNotFoundException {
        UserResponseDTO responseDTO = iUserService.getUserByUUID(uuid);
        return ResponseEntity.status(200).body(responseDTO);
    }

}
