package com.unisew.server.services;

import com.unisew.server.models.Account;
import org.springframework.security.core.userdetails.UserDetails;

public interface JWTService {
    String extractEmailFromJWT(String jwt);

    String generateAccessToken(Account account);

    String generateRefreshToken(Account account);

    boolean checkIfNotExpired(String jwt);
}
