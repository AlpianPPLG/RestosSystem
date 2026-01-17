package com.restos.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for password hashing and verification
 * Uses BCrypt algorithm for secure password storage
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class PasswordUtil {

    // BCrypt workload (cost factor)
    private static final int WORKLOAD = 12;

    /**
     * Hash a plain text password using BCrypt
     * @param plainPassword Plain text password to hash
     * @return Hashed password string
     */
    public static String hashPassword(String plainPassword) {
        String salt = BCrypt.gensalt(WORKLOAD);
        return BCrypt.hashpw(plainPassword, salt);
    }

    /**
     * Verify a plain text password against a hashed password
     * @param plainPassword Plain text password to verify
     * @param hashedPassword Hashed password to compare against
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Invalid hash format
            return false;
        }
    }

    /**
     * Check if a string is a valid BCrypt hash
     * @param hash String to check
     * @return true if valid BCrypt hash, false otherwise
     */
    public static boolean isValidHash(String hash) {
        if (hash == null || hash.length() != 60) {
            return false;
        }
        return hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$");
    }
}
