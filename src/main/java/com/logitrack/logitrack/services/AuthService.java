package com.logitrack.logitrack.services;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logitrack.logitrack.audit.SecurityAuditService;
import com.logitrack.logitrack.dtos.Auth.AuthenticationResponse;
import com.logitrack.logitrack.dtos.User.UserDTO;
import com.logitrack.logitrack.dtos.User.UserResponseDTO;
import com.logitrack.logitrack.mapper.UserMapper;
import com.logitrack.logitrack.models.User;
import com.logitrack.logitrack.repositories.UserRepository;
import com.logitrack.logitrack.security.CustomUserDetails;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final SecurityAuditService securityAuditService;

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
        
        // 4. Audit log
        securityAuditService.logUserRegistration(user.getEmail(), user.getId().toString(), user.getRole().name());
        
        return userMapper.toResponseDTO(user);
    }


    public AuthenticationResponse authenticate(String email, String password) {
        try {

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found after authentication"));

            if(!passwordEncoder.matches(password,user.getPasswordHash())){
                throw new UsernameNotFoundException("Invalid credentials");
            }

            CustomUserDetails userDetails = new CustomUserDetails(user);

            
            String jwtToken = jwtService.generateToken(userDetails);
            String refreshToken = refreshTokenService.createRefreshToken(user.getId()).getToken();

           
            securityAuditService.logLoginSuccess(email, user.getId().toString(), user.getRole().name());

            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .user(userMapper.toResponseDTO(user))
                    .message("Login successful")
                    .build();
        } catch (BadCredentialsException e) {
            // Audit log - Failure
            securityAuditService.logLoginFailure(email, "Invalid credentials");
            throw e;
        } catch (Exception e) {
            // Audit log - Failure
            securityAuditService.logLoginFailure(email, e.getMessage());
            throw e;
        }
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
        AuthenticationResponse response = refreshTokenService.refreshAccessToken(refreshToken);
        
        // Audit log - Extract user info from new token
        String newAccessToken = response.getAccessToken();
        String email = jwtService.extractUsername(newAccessToken);
        userRepository.findByEmail(email).ifPresent(user -> 
            securityAuditService.logTokenRefresh(user.getId().toString(), email)
        );
        
        return response;
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
            userRepository.findByEmail(email).ifPresent(user -> {
                refreshTokenService.deleteByUser(user);
                // Audit log
                securityAuditService.logLogout(user.getId().toString(), email);
            });
        }
    }
}