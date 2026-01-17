package com.restos.model;

import java.time.LocalDateTime;

/**
 * Inventory Model - Represents daily stock for menu items
 * Maps to 'inventories' table in database
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class Inventory {

    private int id;
    private int menuId;
    private int dailyStock;
    private int remainingStock;
    private LocalDateTime lastUpdated;

    // Related object
    private Menu menu;

    /**
     * Default constructor
     */
    public Inventory() {
        this.dailyStock = 0;
        this.remainingStock = 0;
    }

    /**
     * Constructor with basic fields
     * 
     * @param id             Inventory ID
     * @param menuId         Menu ID
     * @param dailyStock     Initial daily stock
     * @param remainingStock Current remaining stock
     */
    public Inventory(int id, int menuId, int dailyStock, int remainingStock) {
        this.id = id;
        this.menuId = menuId;
        this.dailyStock = dailyStock;
        this.remainingStock = remainingStock;
    }

    /**
     * Full constructor
     * 
     * @param id             Inventory ID
     * @param menuId         Menu ID
     * @param dailyStock     Initial daily stock
     * @param remainingStock Current remaining stock
     * @param lastUpdated    Last update timestamp
     */
    public Inventory(int id, int menuId, int dailyStock, int remainingStock, LocalDateTime lastUpdated) {
        this.id = id;
        this.menuId = menuId;
        this.dailyStock = dailyStock;
        this.remainingStock = remainingStock;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public int getDailyStock() {
        return dailyStock;
    }

    public void setDailyStock(int dailyStock) {
        this.dailyStock = dailyStock;
    }

    public int getRemainingStock() {
        return remainingStock;
    }

    public void setRemainingStock(int remainingStock) {
        this.remainingStock = remainingStock;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
        if (menu != null) {
            this.menuId = menu.getId();
        }
    }

    // Helper methods

    /**
     * Check if stock is available
     * 
     * @return true if remaining stock > 0
     */
    public boolean hasStock() {
        return remainingStock > 0;
    }

    /**
     * Check if stock is low (below 20% of daily)
     * 
     * @return true if low stock
     */
    public boolean isLowStock() {
        if (dailyStock == 0)
            return false;
        return remainingStock <= (dailyStock * 0.2);
    }

    /**
     * Check if out of stock
     * 
     * @return true if no stock remaining
     */
    public boolean isOutOfStock() {
        return remainingStock <= 0;
    }

    /**
     * Get stock percentage
     * 
     * @return Stock percentage (0-100)
     */
    public int getStockPercentage() {
        if (dailyStock == 0)
            return 100;
        return (int) ((remainingStock * 100.0) / dailyStock);
    }

    /**
     * Get sold quantity for today
     * 
     * @return Number of items sold
     */
    public int getSoldQuantity() {
        return dailyStock - remainingStock;
    }

    /**
     * Decrease stock by quantity
     * 
     * @param quantity Quantity to decrease
     * @return true if successful, false if not enough stock
     */
    public boolean decreaseStock(int quantity) {
        if (remainingStock >= quantity) {
            remainingStock -= quantity;
            return true;
        }
        return false;
    }

    /**
     * Increase stock by quantity (e.g., order cancelled)
     * 
     * @param quantity Quantity to restore
     */
    public void increaseStock(int quantity) {
        remainingStock += quantity;
        // Don't exceed daily stock
        if (remainingStock > dailyStock) {
            remainingStock = dailyStock;
        }
    }

    /**
     * Reset stock to daily stock (new day)
     */
    public void resetStock() {
        this.remainingStock = this.dailyStock;
    }

    /**
     * Get menu name
     * 
     * @return Menu name or empty string
     */
    public String getMenuName() {
        return menu != null ? menu.getName() : "";
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "menuId=" + menuId +
                ", dailyStock=" + dailyStock +
                ", remainingStock=" + remainingStock +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Inventory inventory = (Inventory) o;
        return id == inventory.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
