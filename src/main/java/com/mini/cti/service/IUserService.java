package com.mini.cti.service;

import com.mini.cti.core.exceptions.InvalidCredentialException;
import com.mini.cti.core.exceptions.UserAlreadyExistsException;
import com.mini.cti.core.exceptions.UserNotFoundException;
import com.mini.cti.dto.UserRequestDTO;
import com.mini.cti.dto.UserResponseDTO;

import java.util.UUID;

public interface IUserService {
    UserResponseDTO registerUser (UserRequestDTO userRequestDTO)
        throws UserAlreadyExistsException;

    UserResponseDTO getUserByUUID(UUID uuid)
        throws UserNotFoundException;

}
