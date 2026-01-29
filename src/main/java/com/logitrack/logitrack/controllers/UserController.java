package com.logitrack.logitrack.controllers;


import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.logitrack.logitrack.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.logitrack.logitrack.dtos.User.UserDTO;
import com.logitrack.logitrack.dtos.User.UserResponseDTO;
import com.logitrack.logitrack.mapper.UserMapper;
import com.logitrack.logitrack.models.User;
import com.logitrack.logitrack.repositories.UserRepository;
import com.logitrack.logitrack.services.AuthService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/me")
    public Object getCurrentUser(HttpSession session, @RequestBody Map<String, String> requestBody) {
        String ssID= requestBody.get("sessionId");
        return session.getAttribute(ssID);
    }
    
    @GetMapping("")
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        if (userDTO.getPasswordHash() == null || userDTO.getPasswordHash().isEmpty()) {
            throw new IllegalArgumentException("Password is required for user creation");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.registerUser(userDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable UUID id, @RequestBody UserDTO userDTO) {
       return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
