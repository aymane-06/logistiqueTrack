package com.logitrack.logitrack.repositories;

import com.logitrack.logitrack.models.RefreshToken;
import com.logitrack.logitrack.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // Trouver un jeton par sa chaîne de caractères
    Optional<RefreshToken> findByToken(String token);

    // Supprimer le jeton associé à un utilisateur (pour la déconnexion)
    @Modifying
    void deleteByUser(User user);
}