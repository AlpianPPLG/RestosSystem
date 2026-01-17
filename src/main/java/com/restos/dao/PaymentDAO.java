package com.restos.dao;

import com.restos.model.Payment;
import java.time.LocalDate;
import java.util.List;

/**
 * Data Access Object interface for Payment entity
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public interface PaymentDAO {

    /**
     * Find all payments
     * 
     * @return List of all payments
     */
    List<Payment> findAll();

    /**
     * Find payment by ID
     * 
     * @param id Payment ID
     * @return Payment object or null if not found
     */
    Payment findById(int id);

    /**
     * Find payment by order ID
     * 
     * @param orderId Order ID
     * @return Payment object or null if not found
     */
    Payment findByOrderId(int orderId);

    /**
     * Find payments by cashier user ID
     * 
     * @param userId Cashier user ID
     * @return List of payments processed by the cashier
     */
    List<Payment> findByUserId(int userId);

    /**
     * Find payments by payment method
     * 
     * @param paymentMethod Payment method (cash, qris, debit)
     * @return List of payments with the specified method
     */
    List<Payment> findByPaymentMethod(String paymentMethod);

    /**
     * Find payments for today
     * 
     * @return List of today's payments
     */
    List<Payment> findToday();

    /**
     * Find payments by date range
     * 
     * @param startDate Start date
     * @param endDate   End date
     * @return List of payments within the date range
     */
    List<Payment> findByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Insert a new payment
     * 
     * @param payment Payment object to insert
     * @return Generated ID or -1 if failed
     */
    int insert(Payment payment);

    /**
     * Update a payment
     * 
     * @param payment Payment object to update
     * @return True if successful
     */
    boolean update(Payment payment);

    /**
     * Delete a payment
     * 
     * @param id Payment ID
     * @return True if successful
     */
    boolean delete(int id);

    /**
     * Check if order already has payment
     * 
     * @param orderId Order ID
     * @return True if order has payment
     */
    boolean orderHasPayment(int orderId);

    /**
     * Count all payments
     * 
     * @return Total number of payments
     */
    int count();

    /**
     * Count today's payments
     * 
     * @return Number of payments today
     */
    int countToday();

    /**
     * Count payments by payment method
     * 
     * @param paymentMethod Payment method
     * @return Number of payments with the method
     */
    int countByPaymentMethod(String paymentMethod);

    /**
     * Get total revenue today
     * 
     * @return Total amount paid today
     */
    double getTodayRevenue();

    /**
     * Get revenue by date range
     * 
     * @param startDate Start date
     * @param endDate   End date
     * @return Total amount paid in the date range
     */
    double getRevenueByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Get revenue by payment method today
     * 
     * @param paymentMethod Payment method
     * @return Total amount paid by the method today
     */
    double getTodayRevenueByPaymentMethod(String paymentMethod);

    /**
     * Get total change given today
     * 
     * @return Total change amount today
     */
    double getTodayChangeGiven();
}
