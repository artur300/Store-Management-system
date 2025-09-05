package com.myshopnet.auth;

public class PasswordPolicy {
    private Integer minimumAlphabeticCharacters;
    private Integer maximumAlphabeticCharacters;
    private Integer minimumNumericCharacters;
    private Integer maximumNumericCharacters;
    private boolean includeSigns;

    public PasswordPolicy(Integer minimumAlphabeticCharacters, Integer maximumAlphabeticCharacters,
                          Integer minimumNumericCharacters, Integer maximumNumericCharacters, boolean includeSigns) {
        this.minimumAlphabeticCharacters = minimumAlphabeticCharacters;
        this.maximumAlphabeticCharacters = maximumAlphabeticCharacters;
        this.minimumNumericCharacters = minimumNumericCharacters;
        this.maximumNumericCharacters = maximumNumericCharacters;
        this.includeSigns = includeSigns;
    }

    public Integer getMinimumAlphabeticCharacters() {
        return minimumAlphabeticCharacters;
    }

    public void setMinimumAlphabeticCharacters(Integer minimumAlphabeticCharacters) {
        this.minimumAlphabeticCharacters = minimumAlphabeticCharacters;
    }

    public Integer getMaximumAlphabeticCharacters() {
        return maximumAlphabeticCharacters;
    }

    public void setMaximumAlphabeticCharacters(Integer maximumAlphabeticCharacters) {
        this.maximumAlphabeticCharacters = maximumAlphabeticCharacters;
    }

    public Integer getMinimumNumericCharacters() {
        return minimumNumericCharacters;
    }

    public void setMinimumNumericCharacters(Integer minimumNumericCharacters) {
        this.minimumNumericCharacters = minimumNumericCharacters;
    }

    public Integer getMaximumNumericCharacters() {
        return maximumNumericCharacters;
    }

    public void setMaximumNumericCharacters(Integer maximumNumericCharacters) {
        this.maximumNumericCharacters = maximumNumericCharacters;
    }

    public boolean isIncludeSigns() {
        return includeSigns;
    }

    public void setIncludeSigns(boolean includeSigns) {
        this.includeSigns = includeSigns;
    }

    public boolean isValid(String password) {
        if (password == null) {
            return false;
        }

        long alphabeticCount = 0;
        long numericCount = 0;
        boolean hasSpecialChars = false;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                alphabeticCount++;
            } else if (Character.isDigit(c)) {
                numericCount++;
            } else {
                hasSpecialChars = true;
            }
        }

        if (minimumAlphabeticCharacters != null && alphabeticCount < minimumAlphabeticCharacters) {
            throw new SecurityException("Not enought minimum alphabetic characters");
        }

        // Check maximum alphabetic characters
        if (maximumAlphabeticCharacters != null && alphabeticCount > maximumAlphabeticCharacters) {
            throw new SecurityException("Too many alphabetic characters");
        }

        if (minimumNumericCharacters != null && numericCount < minimumNumericCharacters) {
            throw new SecurityException("Not enought minimum numeric characters");
        }

        if (maximumNumericCharacters != null && numericCount > maximumNumericCharacters) {
            throw new SecurityException("Too many numeric characters");
        }

        if (includeSigns && !hasSpecialChars) {
            throw new SecurityException("Should have special characters");

        }

        if (!includeSigns && hasSpecialChars) {
            throw new SecurityException("Shouldn't have special characters");
        }

        return true;
    }

    public static PasswordPolicy defaultPolicy() {
        return new PasswordPolicy(4, 10,
                2, 6, false);
    }
}
