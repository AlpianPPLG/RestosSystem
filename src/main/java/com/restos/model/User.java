package com.restos.model;

import java.time.LocalDateTime;

/**
 * User Model - Represents employee/user data
 * Maps to 'users' table in database
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class User {

    private int id;
    private String username;
    private String passwordHash;
    private String role; // admin, waiter, cashier, kitchen
    private String fullName;
    private String email;
    private String phone;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Transient field for password (not hashed yet)
    private String password;

    /**
     * Default constructor
     */
    public User() {
    }

    /**
     * Constructor with basic fields
     * 
     * @param id       User ID
     * @param username Username
     * @param role     User role
     * @param fullName Full name
     */
    public User(int id, String username, String role, String fullName) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.fullName = fullName;
    }

    /**
     * Full constructor
     * 
     * @param id           User ID
     * @param username     Username
     * @param passwordHash Hashed password
     * @param role         User role
     * @param fullName     Full name
     * @param createdAt    Creation timestamp
     * @param updatedAt    Update timestamp
     */
    public User(int id, String username, String passwordHash, String role,
            String fullName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.fullName = fullName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Alias methods for compatibility
    public String getName() {
        return fullName != null ? fullName : username;
    }

    public void setName(String name) {
        this.fullName = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper methods

    /**
     * Check if user is admin
     * 
     * @return true if admin, false otherwise
     */
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(this.role);
    }

    /**
     * Check if user is waiter
     * 
     * @return true if waiter, false otherwise
     */
    public boolean isWaiter() {
        return "waiter".equalsIgnoreCase(this.role);
    }

    /**
     * Check if user is cashier
     * 
     * @return true if cashier, false otherwise
     */
    public boolean isCashier() {
        return "cashier".equalsIgnoreCase(this.role);
    }

    /**
     * Check if user is kitchen staff
     * 
     * @return true if kitchen, false otherwise
     */
    public boolean isKitchen() {
        return "kitchen".equalsIgnoreCase(this.role);
    }

    /**
     * Get display name (full name or username)
     * 
     * @return Display name
     */
    public String getDisplayName() {
        return fullName != null && !fullName.isEmpty() ? fullName : username;
    }

    /**
     * Get role display name in Indonesian
     * 
     * @return Localized role name
     */
    public String getRoleDisplayName() {
        switch (role.toLowerCase()) {
            case "admin":
                return "Administrator";
            case "waiter":
                return "Pelayan";
            case "cashier":
                return "Kasir";
            case "kitchen":
                return "Dapur";
            default:
                return role;
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
