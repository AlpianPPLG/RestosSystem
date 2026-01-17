package com.restos.dao;

import com.restos.model.OrderItem;
import java.util.List;

/**
 * Data Access Object interface for OrderItem entity
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public interface OrderItemDAO {

    /**
     * Find all order items
     * 
     * @return List of all order items
     */
    List<OrderItem> findAll();

    /**
     * Find order item by ID
     * 
     * @param id Order item ID
     * @return OrderItem object or null if not found
     */
    OrderItem findById(int id);

    /**
     * Find order items by order ID
     * 
     * @param orderId Order ID
     * @return List of order items for the order
     */
    List<OrderItem> findByOrderId(int orderId);

    /**
     * Find order items by status
     * 
     * @param status Order item status (pending, cooking, served)
     * @return List of order items with the specified status
     */
    List<OrderItem> findByStatus(String status);

    /**
     * Find order items for kitchen display (pending and cooking)
     * 
     * @return List of order items pending or being cooked
     */
    List<OrderItem> findForKitchen();

    /**
     * Find order items by menu ID
     * 
     * @param menuId Menu ID
     * @return List of order items for the menu
     */
    List<OrderItem> findByMenuId(int menuId);

    /**
     * Insert a new order item
     * 
     * @param orderItem OrderItem object to insert
     * @return Generated ID or -1 if failed
     */
    int insert(OrderItem orderItem);

    /**
     * Insert multiple order items for an order
     * 
     * @param orderId    Order ID
     * @param orderItems List of order items to insert
     * @return True if all items inserted successfully
     */
    boolean insertBatch(int orderId, List<OrderItem> orderItems);

    /**
     * Update an order item
     * 
     * @param orderItem OrderItem object to update
     * @return True if successful
     */
    boolean update(OrderItem orderItem);

    /**
     * Update order item status
     * 
     * @param id     Order item ID
     * @param status New status
     * @return True if successful
     */
    boolean updateStatus(int id, String status);

    /**
     * Update order item quantity
     * 
     * @param id       Order item ID
     * @param quantity New quantity
     * @return True if successful
     */
    boolean updateQuantity(int id, int quantity);

    /**
     * Delete an order item
     * 
     * @param id Order item ID
     * @return True if successful
     */
    boolean delete(int id);

    /**
     * Delete all order items for an order
     * 
     * @param orderId Order ID
     * @return True if successful
     */
    boolean deleteByOrderId(int orderId);

    /**
     * Count order items by order ID
     * 
     * @param orderId Order ID
     * @return Number of items in the order
     */
    int countByOrderId(int orderId);

    /**
     * Count pending items for kitchen
     * 
     * @return Number of pending order items
     */
    int countPending();

    /**
     * Get total menu items sold today
     * 
     * @param menuId Menu ID
     * @return Total quantity sold today
     */
    int getTodaySoldByMenuId(int menuId);
}
