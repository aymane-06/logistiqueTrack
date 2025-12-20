package com.logitrack.logitrack.services;

import com.logitrack.logitrack.dtos.Auth.AuthenticationResponse;
import com.logitrack.logitrack.dtos.User.UserDTO;
import com.logitrack.logitrack.dtos.User.UserResponseDTO;
import com.logitrack.logitrack.mapper.UserMapper;
import com.logitrack.logitrack.models.User;
import com.logitrack.logitrack.repositories.UserRepository;
import com.logitrack.logitrack.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    /**
     * Enregistre un nouvel utilisateur en utilisant le hachage PasswordEncoder
     * et la logique de mapping par rôle.
     */
    @Transactional
    public UserResponseDTO registerUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        // 1. Hachage du mot de passe via PasswordEncoder (BCrypt)
        userDTO.setPasswordHash(passwordEncoder.encode(userDTO.getPasswordHash()));

        // 2. Mapping vers l'entité spécifique selon le rôle
        User user = switch (userDTO.getRole()) {
            case ADMIN -> userMapper.toAdminEntity(userDTO);
            case CLIENT -> userMapper.toClientEntity(userDTO);
            case WAREHOUSE_MANAGER -> userMapper.toWarehouseManagerEntity(userDTO);
        };

        // 3. Sauvegarde et retour
        userRepository.save(user);
        return userMapper.toResponseDTO(user);
    }

    /**
     * Authentifie l'utilisateur via Spring Security et génère les jetons JWT.
     * Remplace la gestion manuelle de session par une réponse Stateless.
     */
    public AuthenticationResponse authenticate(String email, String password) {
        // 1. Délégation de la vérification à l'AuthenticationManager
        // Cela vérifie l'email, le mot de passe (via PasswordEncoder) et le statut du compte
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        // 2. Récupération de l'utilisateur pour générer les jetons
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found after authentication"));

        CustomUserDetails userDetails = new CustomUserDetails(user);

        // 3. Création de l'Access Token (JWT) et du Refresh Token (Persistant)
        String jwtToken = jwtService.generateToken(userDetails);
        String refreshToken = refreshTokenService.createRefreshToken(user.getId()).getToken();

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .message("Login successful")
                .build();
    }

    /**
     * Gère le rafraîchissement d'un Access Token expiré.
     */
    public AuthenticationResponse refreshToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Refresh token is missing or invalid header");
        }

        refreshToken = authHeader.substring(7);

        // Appelle le service de jeton pour valider et créer un nouvel Access Token
        return refreshTokenService.refreshAccessToken(refreshToken);
    }

    /**
     * Invalide le jeton de rafraîchissement lors de la déconnexion.
     */
    @Transactional
    public void logout(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            String email = jwtService.extractUsername(jwt);

            // Supprime le refresh token de la base de données
            userRepository.findByEmail(email).ifPresent(refreshTokenService::deleteByUser);
        }
    }
}