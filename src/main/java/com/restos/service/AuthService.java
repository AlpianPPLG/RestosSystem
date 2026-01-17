package com.restos.service;

import com.restos.config.DatabaseConfig;
import com.restos.model.User;
import com.restos.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Authentication Service
 * Handles user login, logout, and authentication operations
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class AuthService {

    private DatabaseConfig databaseConfig;

    /**
     * Constructor
     */
    public AuthService() {
        this.databaseConfig = DatabaseConfig.getInstance();
    }

    /**
     * Authenticate user with username and password
     * @param username Username
     * @param password Plain text password
     * @return User object if authentication successful, null otherwise
     */
    public User login(String username, String password) {
        String sql = "SELECT id, username, password_hash, role, full_name, created_at, updated_at " +
                     "FROM users WHERE username = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    
                    // Verify password
                    if (PasswordUtil.verifyPassword(password, storedHash)) {
                        User user = new User();
                        user.setId(rs.getInt("id"));
                        user.setUsername(rs.getString("username"));
                        user.setPasswordHash(storedHash);
                        user.setRole(rs.getString("role"));
                        user.setFullName(rs.getString("full_name"));
                        
                        if (rs.getTimestamp("created_at") != null) {
                            user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                        }
                        if (rs.getTimestamp("updated_at") != null) {
                            user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                        }
                        
                        System.out.println("Login successful for user: " + username);
                        return user;
                    } else {
                        System.out.println("Invalid password for user: " + username);
                    }
                } else {
                    System.out.println("User not found: " + username);
                }
            }
        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Check if username exists
     * @param username Username to check
     * @return true if exists, false otherwise
     */
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
        }

        return false;
    }

    /**
     * Change user password
     * @param userId User ID
     * @param currentPassword Current plain text password
     * @param newPassword New plain text password
     * @return true if password changed successfully, false otherwise
     */
    public boolean changePassword(int userId, String currentPassword, String newPassword) {
        // First verify current password
        String verifySql = "SELECT password_hash FROM users WHERE id = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement verifyStmt = conn.prepareStatement(verifySql)) {

            verifyStmt.setInt(1, userId);
            
            try (ResultSet rs = verifyStmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    
                    if (!PasswordUtil.verifyPassword(currentPassword, storedHash)) {
                        System.out.println("Current password is incorrect");
                        return false;
                    }
                } else {
                    System.out.println("User not found: " + userId);
                    return false;
                }
            }

            // Update password
            String updateSql = "UPDATE users SET password_hash = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                String newHash = PasswordUtil.hashPassword(newPassword);
                updateStmt.setString(1, newHash);
                updateStmt.setInt(2, userId);
                
                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Password changed successfully for user ID: " + userId);
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error changing password: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Reset user password (admin function - no current password needed)
     * @param userId User ID
     * @param newPassword New plain text password
     * @return true if password reset successfully, false otherwise
     */
    public boolean resetPassword(int userId, String newPassword) {
        String updateSql = "UPDATE users SET password_hash = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            
            String newHash = PasswordUtil.hashPassword(newPassword);
            updateStmt.setString(1, newHash);
            updateStmt.setInt(2, userId);
            
            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Password reset successfully for user ID: " + userId);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error resetting password: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Register a new user
     * @param name Full name
     * @param email Email (used as username)
     * @param password Plain text password
     * @param phone Phone number (optional)
     * @param role User role
     * @return Generated user ID or -1 if failed
     */
    public int register(String name, String email, String password, String phone, String role) {
        String sql = "INSERT INTO users (username, password_hash, role, full_name) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            
            String passwordHash = PasswordUtil.hashPassword(password);
            stmt.setString(1, email);
            stmt.setString(2, passwordHash);
            stmt.setString(3, role);
            stmt.setString(4, name);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        System.out.println("User registered successfully with ID: " + userId);
                        return userId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Get user by ID
     * @param userId User ID
     * @return User object or null if not found
     */
    public User getUserById(int userId) {
        String sql = "SELECT id, username, password_hash, role, full_name, created_at, updated_at " +
                     "FROM users WHERE id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setRole(rs.getString("role"));
                    user.setFullName(rs.getString("full_name"));
                    
                    if (rs.getTimestamp("created_at") != null) {
                        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    }
                    if (rs.getTimestamp("updated_at") != null) {
                        user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    }
                    
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user: " + e.getMessage());
        }

        return null;
    }
}
