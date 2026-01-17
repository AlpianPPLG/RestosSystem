package com.restos.dao;

import com.restos.model.Order;
import java.time.LocalDate;
import java.util.List;

/**
 * Data Access Object interface for Order operations
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public interface OrderDAO {

    /**
     * Find all orders
     * 
     * @return List of all orders
     */
    List<Order> findAll();

    /**
     * Find order by ID
     * 
     * @param id Order ID
     * @return Order or null if not found
     */
    Order findById(int id);

    /**
     * Find order by ID with items loaded
     * 
     * @param id Order ID
     * @return Order with items or null if not found
     */
    Order findByIdWithItems(int id);

    /**
     * Find orders by status
     * 
     * @param status Order status
     * @return List of orders with specified status
     */
    List<Order> findByStatus(String status);

    /**
     * Find orders by table ID
     * 
     * @param tableId Table ID
     * @return List of orders for table
     */
    List<Order> findByTableId(int tableId);

    /**
     * Find active order for a table (not completed/cancelled)
     * 
     * @param tableId Table ID
     * @return Active order or null
     */
    Order findActiveByTableId(int tableId);

    /**
     * Find orders by user (waiter) ID
     * 
     * @param userId User ID
     * @return List of orders created by user
     */
    List<Order> findByUserId(int userId);

    /**
     * Find orders created today
     * 
     * @return List of today's orders
     */
    List<Order> findToday();

    /**
     * Find orders by date range
     * 
     * @param startDate Start date
     * @param endDate   End date
     * @return List of orders in date range
     */
    List<Order> findByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Find pending orders for kitchen display
     * 
     * @return List of pending/processing orders
     */
    List<Order> findForKitchen();

    /**
     * Find delivered orders awaiting payment
     * 
     * @return List of delivered orders
     */
    List<Order> findAwaitingPayment();

    /**
     * Insert new order
     * 
     * @param order Order to insert
     * @return Generated ID or -1 if failed
     */
    int insert(Order order);

    /**
     * Update existing order
     * 
     * @param order Order to update
     * @return true if successful
     */
    boolean update(Order order);

    /**
     * Update order status
     * 
     * @param id     Order ID
     * @param status New status
     * @return true if successful
     */
    boolean updateStatus(int id, String status);

    /**
     * Update order total amount
     * 
     * @param id          Order ID
     * @param totalAmount New total amount
     * @return true if successful
     */
    boolean updateTotalAmount(int id, java.math.BigDecimal totalAmount);

    /**
     * Delete order by ID
     * 
     * @param id Order ID
     * @return true if successful
     */
    boolean delete(int id);

    /**
     * Count total orders
     * 
     * @return Total count
     */
    int count();

    /**
     * Count orders by status
     * 
     * @param status Order status
     * @return Count with specified status
     */
    int countByStatus(String status);

    /**
     * Count today's orders
     * 
     * @return Today's order count
     */
    int countToday();

    /**
     * Get total revenue for today
     * 
     * @return Today's total revenue
     */
    double getTodayRevenue();

    /**
     * Get total revenue for date range
     * 
     * @param startDate Start date
     * @param endDate   End date
     * @return Total revenue in date range
     */
    double getRevenueByDateRange(LocalDate startDate, LocalDate endDate);
}
