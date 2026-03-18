package com.mini.cti.config;

import com.mini.cti.enums.Role;
import com.mini.cti.model.User;
import com.mini.cti.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.findByEmail("admin@admin-cti.com").isEmpty()) {

            User adminUser = new User();
            adminUser.setEmail("admin@admin-cti.com");
            adminUser.setUsername(adminUser.getEmail());
            adminUser.setPassword(passwordEncoder.encode("secretPass123@"));
            adminUser.setRole(Role.ADMIN);
            userRepository.save(adminUser);

            System.out.println("System admin injected successfully");
        }
    }
}
