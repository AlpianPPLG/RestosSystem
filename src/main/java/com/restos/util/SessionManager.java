package com.restos.util;

import com.restos.model.User;

/**
 * Session Manager - Singleton class to manage user session
 * Stores current logged-in user information throughout the application
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class SessionManager {

    private static SessionManager instance;
    private User currentUser;
    private long loginTime;

    /**
     * Private constructor for Singleton pattern
     */
    private SessionManager() {
    }

    /**
     * Get singleton instance of SessionManager
     * @return SessionManager instance
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Set current logged-in user
     * @param user User object
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        this.loginTime = System.currentTimeMillis();
    }

    /**
     * Get current logged-in user
     * @return Current User or null if not logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Get current user's ID
     * @return User ID or -1 if not logged in
     */
    public int getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }

    /**
     * Get current user's username
     * @return Username or null if not logged in
     */
    public String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : null;
    }

    /**
     * Get current user's role
     * @return Role string or null if not logged in
     */
    public String getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }

    /**
     * Get current user's full name
     * @return Full name or null if not logged in
     */
    public String getCurrentUserFullName() {
        return currentUser != null ? currentUser.getFullName() : null;
    }

    /**
     * Check if user is logged in
     * @return true if user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Check if current user has admin role
     * @return true if admin, false otherwise
     */
    public boolean isAdmin() {
        return currentUser != null && "admin".equalsIgnoreCase(currentUser.getRole());
    }

    /**
     * Check if current user has waiter role
     * @return true if waiter, false otherwise
     */
    public boolean isWaiter() {
        return currentUser != null && "waiter".equalsIgnoreCase(currentUser.getRole());
    }

    /**
     * Check if current user has kitchen role
     * @return true if kitchen, false otherwise
     */
    public boolean isKitchen() {
        return currentUser != null && "kitchen".equalsIgnoreCase(currentUser.getRole());
    }

    /**
     * Check if current user has cashier role
     * @return true if cashier, false otherwise
     */
    public boolean isCashier() {
        return currentUser != null && "cashier".equalsIgnoreCase(currentUser.getRole());
    }

    /**
     * Get login timestamp
     * @return Login time in milliseconds or 0 if not logged in
     */
    public long getLoginTime() {
        return loginTime;
    }

    /**
     * Get session duration in milliseconds
     * @return Session duration or 0 if not logged in
     */
    public long getSessionDuration() {
        if (loginTime == 0) {
            return 0;
        }
        return System.currentTimeMillis() - loginTime;
    }

    /**
     * Clear current session (logout)
     */
    public void clearSession() {
        this.currentUser = null;
        this.loginTime = 0;
    }

    /**
     * Logout and clear session
     */
    public void logout() {
        clearSession();
    }
}
