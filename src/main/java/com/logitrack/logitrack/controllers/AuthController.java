package com.logitrack.logitrack.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.logitrack.logitrack.dtos.User.UserDTO;
import com.logitrack.logitrack.dtos.User.UserResponseDTO;
import com.logitrack.logitrack.services.AuthService;
import com.logitrack.logitrack.services.KeycloakAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RestTemplate restTemplate;
    private final KeycloakAdminService keycloakAdminService;

    @Value("${spring.security.oauth2.client.provider.keycloak.token-uri}")
    private String keycloakTokenUri;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.keycloak.scope}")
    private String scope;


    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody UserDTO userDTO) {
        // The plain text password is in userDTO.getPasswordHash() at this point
        String plainTextPassword = userDTO.getPasswordHash();

        // 1. Create user in Keycloak using the plain text password
        String keycloakUserId = keycloakAdminService.createUser(
                userDTO.getEmail(),                    // username
                userDTO.getEmail(),                    // email
                userDTO.getFirstName(),                // firstName
                userDTO.getLastName(),                 // lastName
                plainTextPassword,                     // The real, plain-text password
                List.of(userDTO.getRole().name())      // roles
        );

        // 2. Register user in local database.
        // authService.registerUser will hash the password before saving.
        UserResponseDTO userResponseDTO = authService.registerUser(userDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully in both application and Keycloak");
        response.put("user", userResponseDTO);
        response.put("keycloakUserId", keycloakUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String password = requestBody.get("password");

        // Prepare request to Keycloak token endpoint
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("username", email); // Keycloak accepts email as username
        body.add("password", password);
        // Don't add scope - let Keycloak use default scopes

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // Call Keycloak token endpoint
        @SuppressWarnings("unchecked")
        ResponseEntity<Map<String, Object>> keycloakResponse = (ResponseEntity<Map<String, Object>>) (ResponseEntity<?>) restTemplate.postForEntity(
                keycloakTokenUri,
                request,
                Map.class
        );

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("access_token", keycloakResponse.getBody().get("access_token"));
        response.put("refresh_token", keycloakResponse.getBody().get("refresh_token"));
        response.put("expires_in", keycloakResponse.getBody().get("expires_in"));
        response.put("token_type", keycloakResponse.getBody().get("token_type"));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestBody Map<String, String> requestBody) {
        String refreshToken = requestBody.get("refresh_token");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        @SuppressWarnings("unchecked")
        ResponseEntity<Map<String, Object>> keycloakResponse = (ResponseEntity<Map<String, Object>>) (ResponseEntity<?>) restTemplate.postForEntity(
                keycloakTokenUri,
                request,
                Map.class
        );

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Token refreshed successfully");
        response.put("access_token", keycloakResponse.getBody().get("access_token"));
        response.put("refresh_token", keycloakResponse.getBody().get("refresh_token"));
        response.put("expires_in", keycloakResponse.getBody().get("expires_in"));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestBody Map<String, String> requestBody) {
        String refreshToken = requestBody.get("refresh_token");

        String logoutUri = keycloakTokenUri.replace("/token", "/logout");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        restTemplate.postForEntity(logoutUri, request, String.class);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logout successful");

        return ResponseEntity.ok(response);
    }

}
