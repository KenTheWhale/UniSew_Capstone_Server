package com.unisew.server.utils;

import com.unisew.server.enums.Status;
import com.unisew.server.models.Account;
import com.unisew.server.repositories.AccountRepo;
import com.unisew.server.services.JWTService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;

public class CookieUtil {
    public static Cookie getCookie(@NonNull HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equalsIgnoreCase(cookieName)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    public static void createCookies(HttpServletResponse response, String accessValue, String refreshValue, long accessExp, long refreshExp) {
        Cookie access = new Cookie("access", accessValue);
        access.setPath("/");
        access.setMaxAge((int) (accessExp / 1000));
        response.addCookie(access);

        Cookie refresh = new Cookie("refresh", refreshValue);
        refresh.setHttpOnly(true);
        refresh.setPath("/");
        refresh.setMaxAge((int) (refreshExp / 1000));
        response.addCookie(refresh);

        Cookie checkCookie = new Cookie("check", "true");
        checkCookie.setPath("/");
        response.addCookie(checkCookie);
    }

    public static void removeCookies(HttpServletResponse response) {
        Cookie access = new Cookie("access", null);
        access.setPath("/");
        access.setMaxAge(0);
        response.addCookie(access);

        Cookie refresh = new Cookie("refresh", null);
        refresh.setHttpOnly(true);
        refresh.setPath("/");
        refresh.setMaxAge(0);
        response.addCookie(refresh);
    }

    public static Account extractAccountFromCookie(HttpServletRequest request, JWTService jwtService, AccountRepo accountRepo) {
        Cookie cookie = CookieUtil.getCookie(request, "refresh");
        if (cookie == null) {
            return null;
        }

        String refreshToken = cookie.getValue();
        String email = jwtService.extractEmailFromJWT(refreshToken);

        return accountRepo.findByEmailAndStatus(email, Status.ACCOUNT_ACTIVE).orElse(null);

    }
}
