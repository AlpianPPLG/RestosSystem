package com.restos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment Model - Represents payment transaction
 * Maps to 'payments' table in database
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class Payment {

    // Payment method constants
    public static final String METHOD_CASH = "cash";
    public static final String METHOD_QRIS = "qris";
    public static final String METHOD_DEBIT = "debit";

    private int id;
    private int orderId;
    private int cashierId;
    private String paymentMethod;
    private BigDecimal amountPaid;
    private BigDecimal changeAmount;
    private LocalDateTime transactionDate;

    // Related objects
    private Order order;
    private User cashier;

    /**
     * Default constructor
     */
    public Payment() {
        this.paymentMethod = METHOD_CASH;
        this.amountPaid = BigDecimal.ZERO;
        this.changeAmount = BigDecimal.ZERO;
    }

    /**
     * Constructor with basic fields
     * 
     * @param orderId       Order ID
     * @param cashierId     Cashier user ID
     * @param paymentMethod Payment method
     * @param amountPaid    Amount paid by customer
     */
    public Payment(int orderId, int cashierId, String paymentMethod, BigDecimal amountPaid) {
        this.orderId = orderId;
        this.cashierId = cashierId;
        this.paymentMethod = paymentMethod;
        this.amountPaid = amountPaid;
        this.changeAmount = BigDecimal.ZERO;
    }

    /**
     * Full constructor
     */
    public Payment(int id, int orderId, int cashierId, String paymentMethod,
            BigDecimal amountPaid, BigDecimal changeAmount, LocalDateTime transactionDate) {
        this.id = id;
        this.orderId = orderId;
        this.cashierId = cashierId;
        this.paymentMethod = paymentMethod;
        this.amountPaid = amountPaid;
        this.changeAmount = changeAmount;
        this.transactionDate = transactionDate;
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

    public int getCashierId() {
        return cashierId;
    }

    public void setCashierId(int cashierId) {
        this.cashierId = cashierId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public BigDecimal getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(BigDecimal changeAmount) {
        this.changeAmount = changeAmount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
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

    public User getCashier() {
        return cashier;
    }

    public void setCashier(User cashier) {
        this.cashier = cashier;
        if (cashier != null) {
            this.cashierId = cashier.getId();
        }
    }

    // Alias for database column 'user_id'
    public int getUserId() {
        return cashierId;
    }

    public void setUserId(int userId) {
        this.cashierId = userId;
    }

    // Alias for database column 'created_at'
    public LocalDateTime getCreatedAt() {
        return transactionDate;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.transactionDate = createdAt;
    }

    // Alias for user relation
    public User getUser() {
        return cashier;
    }

    public void setUser(User user) {
        this.cashier = user;
        if (user != null) {
            this.cashierId = user.getId();
        }
    }

    // Helper methods

    /**
     * Calculate change amount based on order total
     * 
     * @param orderTotal Total amount of the order
     */
    public void calculateChange(BigDecimal orderTotal) {
        if (amountPaid != null && orderTotal != null) {
            this.changeAmount = amountPaid.subtract(orderTotal);
            if (this.changeAmount.compareTo(BigDecimal.ZERO) < 0) {
                this.changeAmount = BigDecimal.ZERO;
            }
        }
    }

    /**
     * Get amount paid as double
     * 
     * @return Amount paid as double
     */
    public double getAmountPaidAsDouble() {
        return amountPaid != null ? amountPaid.doubleValue() : 0.0;
    }

    /**
     * Get change amount as double
     * 
     * @return Change amount as double
     */
    public double getChangeAsDouble() {
        return changeAmount != null ? changeAmount.doubleValue() : 0.0;
    }

    /**
     * Check if payment is cash
     * 
     * @return true if cash
     */
    public boolean isCash() {
        return METHOD_CASH.equals(this.paymentMethod);
    }

    /**
     * Check if payment is QRIS
     * 
     * @return true if QRIS
     */
    public boolean isQris() {
        return METHOD_QRIS.equals(this.paymentMethod);
    }

    /**
     * Check if payment is debit
     * 
     * @return true if debit
     */
    public boolean isDebit() {
        return METHOD_DEBIT.equals(this.paymentMethod);
    }

    /**
     * Get payment method display name in Indonesian
     * 
     * @return Localized method name
     */
    public String getPaymentMethodDisplayName() {
        switch (paymentMethod) {
            case METHOD_CASH:
                return "Tunai";
            case METHOD_QRIS:
                return "QRIS";
            case METHOD_DEBIT:
                return "Kartu Debit";
            default:
                return paymentMethod;
        }
    }

    /**
     * Get payment method icon
     * 
     * @return Icon emoji
     */
    public String getPaymentMethodIcon() {
        switch (paymentMethod) {
            case METHOD_CASH:
                return "💵";
            case METHOD_QRIS:
                return "📱";
            case METHOD_DEBIT:
                return "💳";
            default:
                return "💰";
        }
    }

    /**
     * Get cashier name
     * 
     * @return Cashier name or empty string
     */
    public String getCashierName() {
        return cashier != null ? cashier.getDisplayName() : "";
    }

    /**
     * Get payment display ID (e.g., "PAY-001")
     * 
     * @return Formatted payment ID
     */
    public String getPaymentDisplayId() {
        return String.format("PAY-%03d", id);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", amountPaid=" + amountPaid +
                ", changeAmount=" + changeAmount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Payment payment = (Payment) o;
        return id == payment.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
