package com.example.BookShop.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.logout.LogoutHandler;

public class CustomLogoutHandler implements LogoutHandler {

    private final PersistentTokenRepository tokenRepository;

    public CustomLogoutHandler(PersistentTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null) {
            String username = authentication.getName();
            tokenRepository.removeUserTokens(username);
        }
        // Xóa cookie JSESSIONID
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);


    }
}
