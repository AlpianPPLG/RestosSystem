package com.restos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Menu Model - Represents menu item (food/beverage)
 * Maps to 'menus' table in database
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class Menu {

    private int id;
    private int categoryId;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private boolean isActive;
    private LocalDateTime createdAt;

    // Related object
    private Category category;
    private Inventory inventory;

    /**
     * Default constructor
     */
    public Menu() {
        this.isActive = true;
        this.price = BigDecimal.ZERO;
    }

    /**
     * Constructor with basic fields
     * 
     * @param id    Menu ID
     * @param name  Menu name
     * @param price Menu price
     */
    public Menu(int id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.isActive = true;
    }

    /**
     * Full constructor
     * 
     * @param id          Menu ID
     * @param categoryId  Category ID
     * @param name        Menu name
     * @param description Description
     * @param price       Price
     * @param imageUrl    Image URL
     * @param isActive    Active status
     * @param createdAt   Creation timestamp
     */
    public Menu(int id, int categoryId, String name, String description,
            BigDecimal price, String imageUrl, boolean isActive, LocalDateTime createdAt) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Alias for database column 'is_available'
    public boolean getIsAvailable() {
        return isActive;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isActive = isAvailable;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
        if (category != null) {
            this.categoryId = category.getId();
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    // Helper methods

    /**
     * Get price as double for calculations
     * 
     * @return Price as double
     */
    public double getPriceAsDouble() {
        return price != null ? price.doubleValue() : 0.0;
    }

    /**
     * Check if menu item is available (active and has stock)
     * 
     * @return true if available
     */
    public boolean isAvailable() {
        if (!isActive)
            return false;
        if (inventory != null) {
            return inventory.getRemainingStock() > 0;
        }
        return true; // No inventory tracking = always available
    }

    /**
     * Get remaining stock
     * 
     * @return Remaining stock or -1 if no inventory tracking
     */
    public int getRemainingStock() {
        return inventory != null ? inventory.getRemainingStock() : -1;
    }

    /**
     * Get category name
     * 
     * @return Category name or empty string
     */
    public String getCategoryName() {
        return category != null ? category.getName() : "";
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Menu menu = (Menu) o;
        return id == menu.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
