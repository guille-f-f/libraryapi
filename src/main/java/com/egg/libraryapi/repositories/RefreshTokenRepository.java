package com.egg.libraryapi.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.egg.libraryapi.entities.RefreshToken;
import com.egg.libraryapi.entities.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByUser(User user);

    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    @Query("SELECT r FROM RefreshToken r WHERE r.refreshToken = :token")
    Optional<RefreshToken> buscarPorRefreshToken(@Param("token") String token);

    void deleteByRefreshToken(String refreshToken);

}
