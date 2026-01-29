package com.logitrack.logitrack.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logitrack.logitrack.dtos.Auth.AuthenticationResponse;
import com.logitrack.logitrack.dtos.User.UserDTO;
import com.logitrack.logitrack.services.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody UserDTO userDTO) {
        if (userDTO.getRole() != com.logitrack.logitrack.models.ENUM.Role.CLIENT) {
             throw new IllegalArgumentException("Only Clients can register via this endpoint. Contact Admin for other roles.");
        }
        String rawPassword = userDTO.getPasswordHash();
        authService.registerUser(userDTO);
        AuthenticationResponse response = authService.authenticate(userDTO.getEmail(), rawPassword);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String password = requestBody.get("password");
        AuthenticationResponse response = authService.authenticate(email, password);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(HttpServletRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }
}