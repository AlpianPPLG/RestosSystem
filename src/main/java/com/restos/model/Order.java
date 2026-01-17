package com.restos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order Model - Represents transaction/order header
 * Maps to 'orders' table in database
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class Order {

    // Order type constants
    public static final String TYPE_DINE_IN = "dine_in";
    public static final String TYPE_TAKE_AWAY = "take_away";

    // Status constants
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_PROCESSING = "processing";
    public static final String STATUS_DELIVERED = "delivered";
    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_CANCELLED = "cancelled";

    private int id;
    private Integer tableId; // Nullable for take away
    private int userId;
    private String customerName;
    private String orderType;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Related objects
    private Table table;
    private User user;
    private List<OrderItem> orderItems;
    private Payment payment;

    /**
     * Default constructor
     */
    public Order() {
        this.orderType = TYPE_DINE_IN;
        this.status = STATUS_PENDING;
        this.totalAmount = BigDecimal.ZERO;
        this.orderItems = new ArrayList<>();
    }

    /**
     * Constructor with basic fields
     * 
     * @param id        Order ID
     * @param tableId   Table ID
     * @param userId    User/Waiter ID
     * @param orderType Order type
     */
    public Order(int id, Integer tableId, int userId, String orderType) {
        this.id = id;
        this.tableId = tableId;
        this.userId = userId;
        this.orderType = orderType;
        this.status = STATUS_PENDING;
        this.totalAmount = BigDecimal.ZERO;
        this.orderItems = new ArrayList<>();
    }

    /**
     * Full constructor
     */
    public Order(int id, Integer tableId, int userId, String customerName, String orderType,
            String status, BigDecimal totalAmount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.tableId = tableId;
        this.userId = userId;
        this.customerName = customerName;
        this.orderType = orderType;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.orderItems = new ArrayList<>();
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getTableId() {
        return tableId;
    }

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
        if (table != null) {
            this.tableId = table.getId();
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userId = user.getId();
        }
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems != null ? orderItems : new ArrayList<>();
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    // Helper methods

    /**
     * Add item to order
     * 
     * @param item OrderItem to add
     */
    public void addItem(OrderItem item) {
        if (orderItems == null) {
            orderItems = new ArrayList<>();
        }
        orderItems.add(item);
        item.setOrderId(this.id);
        calculateTotal();
    }

    /**
     * Remove item from order
     * 
     * @param item OrderItem to remove
     */
    public void removeItem(OrderItem item) {
        if (orderItems != null) {
            orderItems.remove(item);
            calculateTotal();
        }
    }

    /**
     * Calculate and update total amount from items
     */
    public void calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        if (orderItems != null) {
            for (OrderItem item : orderItems) {
                if (item.getSubtotal() != null) {
                    total = total.add(item.getSubtotal());
                }
            }
        }
        this.totalAmount = total;
    }

    /**
     * Get total amount as double
     * 
     * @return Total as double
     */
    public double getTotalAsDouble() {
        return totalAmount != null ? totalAmount.doubleValue() : 0.0;
    }

    /**
     * Get total item count
     * 
     * @return Number of items
     */
    public int getItemCount() {
        if (orderItems == null)
            return 0;
        return orderItems.stream().mapToInt(OrderItem::getQuantity).sum();
    }

    /**
     * Check if order is dine in
     * 
     * @return true if dine in
     */
    public boolean isDineIn() {
        return TYPE_DINE_IN.equals(this.orderType);
    }

    /**
     * Check if order is take away
     * 
     * @return true if take away
     */
    public boolean isTakeAway() {
        return TYPE_TAKE_AWAY.equals(this.orderType);
    }

    /**
     * Check if order is pending
     * 
     * @return true if pending
     */
    public boolean isPending() {
        return STATUS_PENDING.equals(this.status);
    }

    /**
     * Check if order is processing
     * 
     * @return true if processing
     */
    public boolean isProcessing() {
        return STATUS_PROCESSING.equals(this.status);
    }

    /**
     * Check if order is delivered
     * 
     * @return true if delivered
     */
    public boolean isDelivered() {
        return STATUS_DELIVERED.equals(this.status);
    }

    /**
     * Check if order is completed
     * 
     * @return true if completed
     */
    public boolean isCompleted() {
        return STATUS_COMPLETED.equals(this.status);
    }

    /**
     * Check if order is cancelled
     * 
     * @return true if cancelled
     */
    public boolean isCancelled() {
        return STATUS_CANCELLED.equals(this.status);
    }

    /**
     * Check if order is paid
     * 
     * @return true if has payment
     */
    public boolean isPaid() {
        return payment != null;
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
            case STATUS_PROCESSING:
                return "Diproses";
            case STATUS_DELIVERED:
                return "Diantar";
            case STATUS_COMPLETED:
                return "Selesai";
            case STATUS_CANCELLED:
                return "Dibatalkan";
            default:
                return status;
        }
    }

    /**
     * Get order type display name in Indonesian
     * 
     * @return Localized type name
     */
    public String getOrderTypeDisplayName() {
        switch (orderType) {
            case TYPE_DINE_IN:
                return "Makan di Tempat";
            case TYPE_TAKE_AWAY:
                return "Bawa Pulang";
            default:
                return orderType;
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
            case STATUS_PROCESSING:
                return "#3B82F6"; // Blue
            case STATUS_DELIVERED:
                return "#8B5CF6"; // Purple
            case STATUS_COMPLETED:
                return "#22C55E"; // Green
            case STATUS_CANCELLED:
                return "#EF4444"; // Red
            default:
                return "#6B7280"; // Gray
        }
    }

    /**
     * Get table number display
     * 
     * @return Table number or "Take Away"
     */
    public String getTableDisplay() {
        if (table != null) {
            return "Meja " + table.getTableNumber();
        } else if (tableId != null) {
            return "Meja #" + tableId;
        } else {
            return "Take Away";
        }
    }

    /**
     * Get waiter name display
     * 
     * @return Waiter name
     */
    public String getWaiterName() {
        return user != null ? user.getDisplayName() : "";
    }

    /**
     * Get order display ID (e.g., "ORD-001")
     * 
     * @return Formatted order ID
     */
    public String getOrderDisplayId() {
        return String.format("ORD-%03d", id);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", tableId=" + tableId +
                ", status='" + status + '\'' +
                ", totalAmount=" + totalAmount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Order order = (Order) o;
        return id == order.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
