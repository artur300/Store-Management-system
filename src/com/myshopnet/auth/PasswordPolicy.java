package com.myshopnet.auth;

public class PasswordPolicy {
    public static boolean isValid(String password) {
        if (password == null || password.length() < 6) return false;
        boolean hasDigit  = password.chars().anyMatch(Character::isDigit);
        boolean hasLetter = password.chars().anyMatch(Character::isLetter);
        return hasDigit && hasLetter;
    }
}
