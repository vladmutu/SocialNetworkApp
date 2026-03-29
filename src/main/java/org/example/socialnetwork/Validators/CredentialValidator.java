package org.example.socialnetwork.Validators;

import java.util.regex.Pattern;

public final class CredentialValidator {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private CredentialValidator() {
    }

    public static String requireName(String value, String fieldName) {
        String normalized = requireNonBlank(value, fieldName).trim();
        if (normalized.length() < 2 || normalized.length() > 50) {
            throw new ValidationException(fieldName + " must have between 2 and 50 characters");
        }
        return normalized;
    }

    public static String requireEmail(String email) {
        String normalized = requireNonBlank(email, "Email").trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new ValidationException("Please provide a valid email address");
        }
        if (normalized.length() > 100) {
            throw new ValidationException("Email is too long");
        }
        return normalized;
    }

    public static String requirePassword(String password) {
        String normalized = requireNonBlank(password, "Password");
        if (normalized.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long");
        }
        return normalized;
    }

    public static String requireMessage(String message) {
        String normalized = requireNonBlank(message, "Message").trim();
        if (normalized.length() > 1000) {
            throw new ValidationException("Message is too long");
        }
        return normalized;
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " is required");
        }
        return value;
    }
}

