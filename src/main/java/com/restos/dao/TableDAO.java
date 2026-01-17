package com.restos.dao;

import com.restos.model.Table;
import java.util.List;

/**
 * Data Access Object interface for Table operations
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public interface TableDAO {

    /**
     * Find all tables
     * 
     * @return List of all tables
     */
    List<Table> findAll();

    /**
     * Find table by ID
     * 
     * @param id Table ID
     * @return Table or null if not found
     */
    Table findById(int id);

    /**
     * Find table by table number
     * 
     * @param tableNumber Table number
     * @return Table or null if not found
     */
    Table findByTableNumber(String tableNumber);

    /**
     * Find tables by status
     * 
     * @param status Table status
     * @return List of tables with specified status
     */
    List<Table> findByStatus(String status);

    /**
     * Find all available tables
     * 
     * @return List of available tables
     */
    List<Table> findAvailable();

    /**
     * Insert new table
     * 
     * @param table Table to insert
     * @return Generated ID or -1 if failed
     */
    int insert(Table table);

    /**
     * Update existing table
     * 
     * @param table Table to update
     * @return true if successful
     */
    boolean update(Table table);

    /**
     * Update table status
     * 
     * @param id     Table ID
     * @param status New status
     * @return true if successful
     */
    boolean updateStatus(int id, String status);

    /**
     * Delete table by ID
     * 
     * @param id Table ID
     * @return true if successful
     */
    boolean delete(int id);

    /**
     * Count total tables
     * 
     * @return Total count
     */
    int count();

    /**
     * Count tables by status
     * 
     * @param status Table status
     * @return Count with specified status
     */
    int countByStatus(String status);

    /**
     * Check if table number exists
     * 
     * @param tableNumber Table number
     * @return true if exists
     */
    boolean tableNumberExists(String tableNumber);
}
