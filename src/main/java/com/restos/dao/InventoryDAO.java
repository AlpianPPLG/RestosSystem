package com.restos.dao;

import com.restos.model.Inventory;
import java.util.List;

/**
 * Data Access Object interface for Inventory operations
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public interface InventoryDAO {

    /**
     * Find all inventories
     * 
     * @return List of all inventories
     */
    List<Inventory> findAll();

    /**
     * Find inventory by ID
     * 
     * @param id Inventory ID
     * @return Inventory or null if not found
     */
    Inventory findById(int id);

    /**
     * Find inventory by menu ID
     * 
     * @param menuId Menu ID
     * @return Inventory or null if not found
     */
    Inventory findByMenuId(int menuId);

    /**
     * Find inventories with low stock
     * 
     * @param threshold Stock threshold percentage (e.g., 20 for 20%)
     * @return List of low stock inventories
     */
    List<Inventory> findLowStock(int threshold);

    /**
     * Find inventories that are out of stock
     * 
     * @return List of out of stock inventories
     */
    List<Inventory> findOutOfStock();

    /**
     * Insert new inventory
     * 
     * @param inventory Inventory to insert
     * @return Generated ID or -1 if failed
     */
    int insert(Inventory inventory);

    /**
     * Update existing inventory
     * 
     * @param inventory Inventory to update
     * @return true if successful
     */
    boolean update(Inventory inventory);

    /**
     * Update remaining stock
     * 
     * @param menuId         Menu ID
     * @param remainingStock New remaining stock
     * @return true if successful
     */
    boolean updateRemainingStock(int menuId, int remainingStock);

    /**
     * Decrease stock by quantity
     * 
     * @param menuId   Menu ID
     * @param quantity Quantity to decrease
     * @return true if successful
     */
    boolean decreaseStock(int menuId, int quantity);

    /**
     * Increase stock by quantity (e.g., order cancelled)
     * 
     * @param menuId   Menu ID
     * @param quantity Quantity to increase
     * @return true if successful
     */
    boolean increaseStock(int menuId, int quantity);

    /**
     * Reset all stocks to daily stock (new day)
     * 
     * @return Number of records updated
     */
    int resetAllStocks();

    /**
     * Delete inventory by ID
     * 
     * @param id Inventory ID
     * @return true if successful
     */
    boolean delete(int id);

    /**
     * Delete inventory by menu ID
     * 
     * @param menuId Menu ID
     * @return true if successful
     */
    boolean deleteByMenuId(int menuId);

    /**
     * Count total inventories
     * 
     * @return Total count
     */
    int count();

    /**
     * Count low stock items
     * 
     * @return Count of low stock items
     */
    int countLowStock();

    /**
     * Count out of stock items
     * 
     * @return Count of out of stock items
     */
    int countOutOfStock();
}
