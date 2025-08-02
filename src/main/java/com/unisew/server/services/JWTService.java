package com.unisew.server.services;

import com.unisew.server.models.Account;

public interface JWTService {
    String extractEmailFromJWT(String jwt);

    String generateAccessToken(Account account);

    String generateRefreshToken(Account account);

    boolean checkIfNotExpired(String jwt);
}
