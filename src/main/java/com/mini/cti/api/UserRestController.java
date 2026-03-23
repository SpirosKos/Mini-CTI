package com.mini.cti.api;


import com.mini.cti.authentication.JwtService;
import com.mini.cti.core.exceptions.InvalidCredentialException;
import com.mini.cti.core.exceptions.UserAlreadyExistsException;
import com.mini.cti.core.exceptions.UserNotFoundException;
import com.mini.cti.core.exceptions.ValidationException;
import com.mini.cti.dto.*;
import com.mini.cti.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;



    @Operation(
            summary = "Register a new user",
            description ="Creates a new user in the system")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "created successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User already exists.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server error.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
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



    @Operation(
            summary = "Login to system",
            description ="A register user can login to system.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully login, returns token.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials error.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server error.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PostMapping("/users/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody UserRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
                (loginRequest.email(), loginRequest.password()));

        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("USER");

        String token = jwtService.generateToken(authentication.getName(), role);

        return ResponseEntity.ok(new AuthResponseDTO(token));
    }




    // TODO For a future service and prevent users to inject another users UUID
    @GetMapping("/users/{uuid}")
    public ResponseEntity<UserResponseDTO> getUserByUUID(@PathVariable UUID uuid) throws UserNotFoundException {
        UserResponseDTO responseDTO = iUserService.getUserByUUID(uuid);
        return ResponseEntity.status(200).body(responseDTO);
    }

}
