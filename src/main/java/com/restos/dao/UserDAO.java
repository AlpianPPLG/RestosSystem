package com.restos.dao;

import com.restos.model.User;
import java.util.List;

/**
 * Data Access Object interface for User operations
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public interface UserDAO {

    /**
     * Find all users
     * 
     * @return List of all users
     */
    List<User> findAll();

    /**
     * Find user by ID
     * 
     * @param id User ID
     * @return User or null if not found
     */
    User findById(int id);

    /**
     * Find user by username
     * 
     * @param username Username
     * @return User or null if not found
     */
    User findByUsername(String username);

    /**
     * Find users by role
     * 
     * @param role User role
     * @return List of users with specified role
     */
    List<User> findByRole(String role);

    /**
     * Insert new user
     * 
     * @param user User to insert
     * @return Generated ID or -1 if failed
     */
    int insert(User user);

    /**
     * Update existing user
     * 
     * @param user User to update
     * @return true if successful
     */
    boolean update(User user);

    /**
     * Update user password
     * 
     * @param id           User ID
     * @param passwordHash New password hash
     * @return true if successful
     */
    boolean updatePassword(int id, String passwordHash);

    /**
     * Delete user by ID
     * 
     * @param id User ID
     * @return true if successful
     */
    boolean delete(int id);

    /**
     * Check if username exists
     * 
     * @param username Username to check
     * @return true if exists
     */
    boolean usernameExists(String username);

    /**
     * Count users by role
     * 
     * @param role User role
     * @return Count of users
     */
    int countByRole(String role);

    /**
     * Find user by email
     * 
     * @param email User email
     * @return User or null if not found
     */
    User findByEmail(String email);

    /**
     * Check if email exists
     * 
     * @param email Email to check
     * @return true if exists
     */
    boolean emailExists(String email);

    /**
     * Set user active status
     * 
     * @param id       User ID
     * @param isActive Active status
     * @return true if successful
     */
    boolean setActive(int id, boolean isActive);
}
