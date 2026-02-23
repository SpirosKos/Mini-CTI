package com.mini.cti.mapper;

import com.mini.cti.dto.UserRequestDTO;
import com.mini.cti.dto.UserResponseDTO;
import com.mini.cti.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User mapToUserEntity(UserRequestDTO userRequestDTO){
        User user = new User();
        user.setEmail(userRequestDTO.email());
        user.setPassword(userRequestDTO.password());
        return user;
//        return new User(userRequestDTO.email(), userRequestDTO.password());
    }

    public UserResponseDTO mapToUserResponseDTO(User user) {
        return new UserResponseDTO(user.getEmail(), user.getRole(), user.getUuid());
    }
}
