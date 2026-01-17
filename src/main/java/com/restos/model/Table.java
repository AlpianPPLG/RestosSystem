package com.restos.model;

import java.time.LocalDateTime;

/**
 * Table Model - Represents physical restaurant table
 * Maps to 'tables' table in database
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class Table {

    // Status constants
    public static final String STATUS_AVAILABLE = "available";
    public static final String STATUS_RESERVED = "reserved";
    public static final String STATUS_OCCUPIED = "occupied";

    private int id;
    private String tableNumber;
    private int capacity;
    private String status;
    private LocalDateTime createdAt;

    // Related object - current active order
    private Order currentOrder;

    /**
     * Default constructor
     */
    public Table() {
        this.status = STATUS_AVAILABLE;
    }

    /**
     * Constructor with basic fields
     * 
     * @param id          Table ID
     * @param tableNumber Table number
     * @param capacity    Seating capacity
     */
    public Table(int id, String tableNumber, int capacity) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.status = STATUS_AVAILABLE;
    }

    /**
     * Full constructor
     * 
     * @param id          Table ID
     * @param tableNumber Table number
     * @param capacity    Seating capacity
     * @param status      Table status
     * @param createdAt   Creation timestamp
     */
    public Table(int id, String tableNumber, int capacity, String status, LocalDateTime createdAt) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }

    public void setCurrentOrder(Order currentOrder) {
        this.currentOrder = currentOrder;
    }

    // Helper methods

    /**
     * Check if table is available
     * 
     * @return true if available
     */
    public boolean isAvailable() {
        return STATUS_AVAILABLE.equals(this.status);
    }

    /**
     * Check if table is occupied
     * 
     * @return true if occupied
     */
    public boolean isOccupied() {
        return STATUS_OCCUPIED.equals(this.status);
    }

    /**
     * Check if table is reserved
     * 
     * @return true if reserved
     */
    public boolean isReserved() {
        return STATUS_RESERVED.equals(this.status);
    }

    /**
     * Get status display name in Indonesian
     * 
     * @return Localized status name
     */
    public String getStatusDisplayName() {
        switch (status) {
            case STATUS_AVAILABLE:
                return "Tersedia";
            case STATUS_OCCUPIED:
                return "Terisi";
            case STATUS_RESERVED:
                return "Direservasi";
            default:
                return status;
        }
    }

    /**
     * Get status color for UI
     * 
     * @return Color hex code
     */
    public String getStatusColor() {
        switch (status) {
            case STATUS_AVAILABLE:
                return "#22C55E"; // Green
            case STATUS_OCCUPIED:
                return "#EF4444"; // Red
            case STATUS_RESERVED:
                return "#EAB308"; // Yellow
            default:
                return "#6B7280"; // Gray
        }
    }

    /**
     * Get display text for table
     * 
     * @return Display text (e.g., "Meja T01")
     */
    public String getDisplayText() {
        return "Meja " + tableNumber;
    }

    @Override
    public String toString() {
        return getDisplayText();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Table table = (Table) o;
        return id == table.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
