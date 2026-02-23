package com.mini.cti.service;


import com.mini.cti.core.exceptions.InvalidCredentialException;
import com.mini.cti.core.exceptions.UserAlreadyExistsException;
import com.mini.cti.core.exceptions.UserNotFoundException;
import com.mini.cti.dto.UserRequestDTO;
import com.mini.cti.dto.UserResponseDTO;
import com.mini.cti.enums.Role;
import com.mini.cti.mapper.UserMapper;
import com.mini.cti.model.User;
import com.mini.cti.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService implements IUserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    /**
     * Registers a new user in the system
     * @param userRequestDTO    contains email and password
     * @return  userResponseDTO with email, role and uuid
     * @throws UserAlreadyExistsException   if email already exists
     */
    @Override
    public UserResponseDTO registerUser(UserRequestDTO userRequestDTO) throws UserAlreadyExistsException {
        try {
            if (userRepository.existsByEmail(userRequestDTO.email())) {
                throw new UserAlreadyExistsException("User with email= " + userRequestDTO.email() + "already exist");
            }

            User user = userMapper.mapToUserEntity(userRequestDTO);

            // encodes the password
            user.setPassword(passwordEncoder.encode(userRequestDTO.password()));
//        user.setRole(Role.USER);        // already defined in User model with @PrePersist

            // Uses savedUser to generate uuid from database.
            User savedUser = userRepository.save(user);
            log.info("User with email={} created successfully", userRequestDTO.email());
            return userMapper.mapToUserResponseDTO(user);
        }catch (UserAlreadyExistsException e) {
            log.error("User with email={} already exists.", userRequestDTO.email());
            throw e;
        }
    }

    @Override
    public UserResponseDTO getUserByUUID(UUID uuid) throws UserNotFoundException {
        return null;
    }

    @Override
    public UserResponseDTO loginUser(UserRequestDTO userRequestDTO) throws InvalidCredentialException {

        try {
            User user =  userRepository.findByEmail(userRequestDTO.email())
                    .orElseThrow(() -> new InvalidCredentialException("Invalid login credentials.Please try again."));
            if (!passwordEncoder.matches(userRequestDTO.password(), user.getPassword())) {
                throw new InvalidCredentialException("Invalid login credentials.Please try again");
            }

            return userMapper.mapToUserResponseDTO(user);

        }catch (InvalidCredentialException e){
            log.error("Invalid login credentials.Please try again.");
            throw e;
        }
    }
}
