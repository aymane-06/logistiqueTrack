package com.logitrack.logitrack.controllers;

import com.logitrack.logitrack.dtos.User.UserDTO;
import com.logitrack.logitrack.dtos.User.UserResponseDTO;

import com.logitrack.logitrack.services.AuthService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody UserDTO userDTO) {
        UserResponseDTO userResponseDTO= authService.registerUser(userDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("user", userResponseDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> requestBody, HttpSession session) {
        String email = requestBody.get("email");
        String password = requestBody.get("password");

        authService.login(email, password,session);

        return "Login successful. Session ID: " + session.getId()+" "+ session.getAttribute(session.getId());
    }

}
