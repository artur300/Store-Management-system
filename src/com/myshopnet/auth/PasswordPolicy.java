package com.myshopnet.auth;

public class PasswordPolicy {
    public boolean isValid(String raw) {
        if (raw == null || raw.length() < 6) return false;
        boolean hasDigit  = raw.chars().anyMatch(Character::isDigit);
        boolean hasLetter = raw.chars().anyMatch(Character::isLetter);
        return hasDigit && hasLetter;
    }
}
