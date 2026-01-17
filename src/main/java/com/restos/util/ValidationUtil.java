package com.restos.util;

import java.util.regex.Pattern;

/**
 * Utility class for input validation
 * Provides common validation methods for forms
 *
 * @author Restos Team
 * @version 1.0.0
 */
public class ValidationUtil {

    // Regex patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(\\+62|62|0)[0-9]{9,13}$");

    private static final Pattern USERNAME_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_]{3,20}$");

    private static final Pattern NUMERIC_PATTERN = Pattern.compile(
            "^[0-9]+$");

    private static final Pattern DECIMAL_PATTERN = Pattern.compile(
            "^[0-9]+(\\.[0-9]{1,2})?$");

    /**
     * Check if string is null or empty
     * 
     * @param value String to check
     * @return true if null or empty
     */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Check if string is not null and not empty
     * 
     * @param value String to check
     * @return true if not null and not empty
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Check if email format is valid
     * 
     * @param email Email to validate
     * @return true if valid email format
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email))
            return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Check if phone number format is valid (Indonesian format)
     * 
     * @param phone Phone number to validate
     * @return true if valid phone format
     */
    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone))
            return false;
        String cleaned = phone.replaceAll("[\\s\\-()]", "");
        return PHONE_PATTERN.matcher(cleaned).matches();
    }

    /**
     * Check if username format is valid
     * 
     * @param username Username to validate
     * @return true if valid username (3-20 chars, alphanumeric + underscore)
     */
    public static boolean isValidUsername(String username) {
        if (isEmpty(username))
            return false;
        return USERNAME_PATTERN.matcher(username.trim()).matches();
    }

    /**
     * Check if password meets minimum requirements
     * 
     * @param password Password to validate
     * @return true if password is at least 6 characters
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    /**
     * Check if password meets strong requirements
     * 
     * @param password Password to validate
     * @return true if password has uppercase, lowercase, number, and at least 8
     *         chars
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8)
            return false;

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c))
                hasUpper = true;
            else if (Character.isLowerCase(c))
                hasLower = true;
            else if (Character.isDigit(c))
                hasDigit = true;
        }

        return hasUpper && hasLower && hasDigit;
    }

    /**
     * Check if string is numeric (integers only)
     * 
     * @param value String to check
     * @return true if numeric
     */
    public static boolean isNumeric(String value) {
        if (isEmpty(value))
            return false;
        return NUMERIC_PATTERN.matcher(value.trim()).matches();
    }

    /**
     * Check if string is a valid decimal number
     * 
     * @param value String to check
     * @return true if valid decimal
     */
    public static boolean isDecimal(String value) {
        if (isEmpty(value))
            return false;
        return DECIMAL_PATTERN.matcher(value.trim()).matches();
    }

    /**
     * Check if number is within range
     * 
     * @param value Value to check
     * @param min   Minimum value (inclusive)
     * @param max   Maximum value (inclusive)
     * @return true if within range
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /**
     * Check if number is within range
     * 
     * @param value Value to check
     * @param min   Minimum value (inclusive)
     * @param max   Maximum value (inclusive)
     * @return true if within range
     */
    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    /**
     * Check if string length is within range
     * 
     * @param value     String to check
     * @param minLength Minimum length
     * @param maxLength Maximum length
     * @return true if length is within range
     */
    public static boolean isLengthInRange(String value, int minLength, int maxLength) {
        if (value == null)
            return minLength == 0;
        int length = value.length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Check if value is positive
     * 
     * @param value Value to check
     * @return true if positive (> 0)
     */
    public static boolean isPositive(int value) {
        return value > 0;
    }

    /**
     * Check if value is positive
     * 
     * @param value Value to check
     * @return true if positive (> 0)
     */
    public static boolean isPositive(double value) {
        return value > 0;
    }

    /**
     * Check if value is non-negative
     * 
     * @param value Value to check
     * @return true if non-negative (>= 0)
     */
    public static boolean isNonNegative(int value) {
        return value >= 0;
    }

    /**
     * Check if value is non-negative
     * 
     * @param value Value to check
     * @return true if non-negative (>= 0)
     */
    public static boolean isNonNegative(double value) {
        return value >= 0;
    }

    /**
     * Parse integer safely
     * 
     * @param value        String to parse
     * @param defaultValue Default value if parsing fails
     * @return Parsed integer or default value
     */
    public static int parseIntSafe(String value, int defaultValue) {
        if (isEmpty(value))
            return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Parse double safely
     * 
     * @param value        String to parse
     * @param defaultValue Default value if parsing fails
     * @return Parsed double or default value
     */
    public static double parseDoubleSafe(String value, double defaultValue) {
        if (isEmpty(value))
            return defaultValue;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Truncate string to max length
     * 
     * @param value     String to truncate
     * @param maxLength Maximum length
     * @return Truncated string with "..." if needed
     */
    public static String truncate(String value, int maxLength) {
        if (value == null)
            return "";
        if (value.length() <= maxLength)
            return value;
        return value.substring(0, maxLength - 3) + "...";
    }

    /**
     * Clean and sanitize string input
     * 
     * @param value String to clean
     * @return Cleaned string (trimmed, null-safe)
     */
    public static String sanitize(String value) {
        if (value == null)
            return "";
        return value.trim();
    }

    /**
     * Get validation error message
     */
    public static class ValidationError {
        public static final String REQUIRED = "Field ini wajib diisi";
        public static final String INVALID_EMAIL = "Format email tidak valid";
        public static final String INVALID_PHONE = "Format nomor telepon tidak valid";
        public static final String INVALID_USERNAME = "Username harus 3-20 karakter (huruf, angka, underscore)";
        public static final String PASSWORD_TOO_SHORT = "Password minimal 6 karakter";
        public static final String PASSWORD_WEAK = "Password harus mengandung huruf besar, kecil, dan angka";
        public static final String INVALID_NUMBER = "Harus berupa angka";
        public static final String MUST_BE_POSITIVE = "Nilai harus lebih dari 0";
        public static final String MUST_BE_NON_NEGATIVE = "Nilai tidak boleh negatif";

        public static String minLength(int min) {
            return "Minimal " + min + " karakter";
        }

        public static String maxLength(int max) {
            return "Maksimal " + max + " karakter";
        }

        public static String range(int min, int max) {
            return "Nilai harus antara " + min + " - " + max;
        }
    }
}
