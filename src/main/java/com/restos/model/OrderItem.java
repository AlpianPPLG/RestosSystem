package com.restos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * OrderItem Model - Represents order detail/line item
 * Maps to 'order_items' table in database
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class OrderItem {

    // Status constants
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_COOKING = "cooking";
    public static final String STATUS_SERVED = "served";

    private int id;
    private int orderId;
    private int menuId;
    private int quantity;
    private BigDecimal priceAtTime; // Snapshot of price when ordered
    private BigDecimal subtotal; // Generated column in DB
    private String specialNotes;
    private String status;
    private LocalDateTime createdAt;

    // Related objects
    private Menu menu;
    private Order order;

    /**
     * Default constructor
     */
    public OrderItem() {
        this.quantity = 1;
        this.status = STATUS_PENDING;
        this.priceAtTime = BigDecimal.ZERO;
    }

    /**
     * Constructor with basic fields
     * 
     * @param menuId      Menu ID
     * @param quantity    Quantity
     * @param priceAtTime Price at time of order
     */
    public OrderItem(int menuId, int quantity, BigDecimal priceAtTime) {
        this.menuId = menuId;
        this.quantity = quantity;
        this.priceAtTime = priceAtTime;
        this.status = STATUS_PENDING;
        calculateSubtotal();
    }

    /**
     * Constructor with menu object
     * 
     * @param menu     Menu object
     * @param quantity Quantity
     */
    public OrderItem(Menu menu, int quantity) {
        this.menu = menu;
        this.menuId = menu.getId();
        this.quantity = quantity;
        this.priceAtTime = menu.getPrice();
        this.status = STATUS_PENDING;
        calculateSubtotal();
    }

    /**
     * Full constructor
     */
    public OrderItem(int id, int orderId, int menuId, int quantity, BigDecimal priceAtTime,
            BigDecimal subtotal, String specialNotes, String status) {
        this.id = id;
        this.orderId = orderId;
        this.menuId = menuId;
        this.quantity = quantity;
        this.priceAtTime = priceAtTime;
        this.subtotal = subtotal;
        this.specialNotes = specialNotes;
        this.status = status;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateSubtotal();
    }

    public BigDecimal getPriceAtTime() {
        return priceAtTime;
    }

    public void setPriceAtTime(BigDecimal priceAtTime) {
        this.priceAtTime = priceAtTime;
        calculateSubtotal();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getSpecialNotes() {
        return specialNotes;
    }

    public void setSpecialNotes(String specialNotes) {
        this.specialNotes = specialNotes;
    }

    // Alias for database column 'notes'
    public String getNotes() {
        return specialNotes;
    }

    public void setNotes(String notes) {
        this.specialNotes = notes;
    }

    // Alias for database column 'price'
    public BigDecimal getPrice() {
        return priceAtTime;
    }

    public void setPrice(BigDecimal price) {
        this.priceAtTime = price;
        calculateSubtotal();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
        if (order != null) {
            this.orderId = order.getId();
        }
    }

    // Helper methods

    /**
     * Calculate subtotal from quantity and price
     */
    public void calculateSubtotal() {
        if (priceAtTime != null) {
            this.subtotal = priceAtTime.multiply(BigDecimal.valueOf(quantity));
        }
    }

    /**
     * Get price as double
     * 
     * @return Price as double
     */
    public double getPriceAsDouble() {
        return priceAtTime != null ? priceAtTime.doubleValue() : 0.0;
    }

    /**
     * Get subtotal as double
     * 
     * @return Subtotal as double
     */
    public double getSubtotalAsDouble() {
        return subtotal != null ? subtotal.doubleValue() : 0.0;
    }

    /**
     * Increase quantity by 1
     */
    public void increaseQuantity() {
        this.quantity++;
        calculateSubtotal();
    }

    /**
     * Decrease quantity by 1 (minimum 1)
     */
    public void decreaseQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
            calculateSubtotal();
        }
    }

    /**
     * Check if item is pending
     * 
     * @return true if pending
     */
    public boolean isPending() {
        return STATUS_PENDING.equals(this.status);
    }

    /**
     * Check if item is cooking
     * 
     * @return true if cooking
     */
    public boolean isCooking() {
        return STATUS_COOKING.equals(this.status);
    }

    /**
     * Check if item is served
     * 
     * @return true if served
     */
    public boolean isServed() {
        return STATUS_SERVED.equals(this.status);
    }

    /**
     * Get status display name in Indonesian
     * 
     * @return Localized status name
     */
    public String getStatusDisplayName() {
        switch (status) {
            case STATUS_PENDING:
                return "Menunggu";
            case STATUS_COOKING:
                return "Dimasak";
            case STATUS_SERVED:
                return "Disajikan";
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
            case STATUS_PENDING:
                return "#F97316"; // Orange
            case STATUS_COOKING:
                return "#3B82F6"; // Blue
            case STATUS_SERVED:
                return "#22C55E"; // Green
            default:
                return "#6B7280"; // Gray
        }
    }

    /**
     * Get menu name
     * 
     * @return Menu name or empty string
     */
    public String getMenuName() {
        return menu != null ? menu.getName() : "";
    }

    /**
     * Check if has special notes
     * 
     * @return true if has notes
     */
    public boolean hasSpecialNotes() {
        return specialNotes != null && !specialNotes.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", menuId=" + menuId +
                ", quantity=" + quantity +
                ", subtotal=" + subtotal +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        OrderItem orderItem = (OrderItem) o;
        return id == orderItem.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
