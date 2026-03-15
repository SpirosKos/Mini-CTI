package com.mini.cti.security;

import com.mini.cti.enums.Role;
import com.mini.cti.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("userSecurityService")
public class SecurityService {


    public boolean isOwnUser(UUID requestedUuid, Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        return user.getUuid().equals(requestedUuid) || user.getRole().equals(Role.ADMIN);

    }
}
