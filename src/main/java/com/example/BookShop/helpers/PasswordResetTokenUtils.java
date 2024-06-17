package com.example.BookShop.helpers;
import java.security.SecureRandom;
import java.util.Base64;
public class PasswordResetTokenUtils {
    private static final int TOKEN_LENGTH = 16;

    public static String generateToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] token = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    }
}
