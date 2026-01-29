package com.logitrack.logitrack.config;

import com.logitrack.logitrack.models.Admin;
import com.logitrack.logitrack.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSeeder {

    @Bean
    public CommandLineRunner seedAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@logitrack.com";
            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                Admin admin = new Admin();
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setFirstName("System");
                admin.setLastName("Administrator");
                userRepository.save(admin);
                System.out.println("Admin account created: " + adminEmail);
            } else {
                System.out.println("Admin account already exists: " + adminEmail);
            }
        };
    }
}
