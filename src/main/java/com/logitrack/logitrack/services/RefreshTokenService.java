package com.logitrack.logitrack.services;

import com.logitrack.logitrack.dtos.Auth.AuthenticationResponse;
import com.logitrack.logitrack.models.RefreshToken;
import com.logitrack.logitrack.models.User;
import com.logitrack.logitrack.repositories.RefreshTokenRepository;
import com.logitrack.logitrack.repositories.UserRepository;
import com.logitrack.logitrack.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    // Durée de validité : 7 jours
    private static final long REFRESH_TOKEN_EXPIRATION = 604800000;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    /**
     * Crée un nouveau Refresh Token ou met à jour celui existant pour un utilisateur.
     */
    @Transactional
    public RefreshToken createRefreshToken(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // On nettoie les anciens jetons pour cet utilisateur
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Valide un Refresh Token et génère un nouvel Access Token.
     */
    public AuthenticationResponse refreshAccessToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtService.generateToken(new CustomUserDetails(user));
                    return AuthenticationResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(token)
                            .message("Access token refreshed successfully")
                            .build();
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    /**
     * Vérifie si le jeton a expiré.
     */
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

}