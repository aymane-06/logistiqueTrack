package com.logitrack.logitrack.repositories;

import com.logitrack.logitrack.models.RefreshToken;
import com.logitrack.logitrack.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // Trouver un jeton par sa chaîne de caractères
    Optional<RefreshToken> findByToken(String token);

    // Find refresh token by user ID
    Optional<RefreshToken> findByUserId(UUID userId);

    // Supprimer le jeton associé à un utilisateur (pour la déconnexion)
    // Use an explicit JPQL delete with @Modifying and @Transactional to ensure execution
    @Modifying
    @Transactional
    @Query("delete from RefreshToken r where r.user = ?1")
    void deleteByUser(User user);

    // Alternative: delete by the user's id directly (can be more reliable in some situations)
    @Modifying
    @Transactional
    @Query("delete from RefreshToken r where r.user.id = ?1")
    void deleteByUserId(UUID userId);
}