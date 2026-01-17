package com.restos.dao;

import com.restos.model.Menu;
import java.util.List;

/**
 * Data Access Object interface for Menu operations
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public interface MenuDAO {

    /**
     * Find all menus
     * 
     * @return List of all menus
     */
    List<Menu> findAll();

    /**
     * Find all active menus
     * 
     * @return List of active menus
     */
    List<Menu> findAllActive();

    /**
     * Find menu by ID
     * 
     * @param id Menu ID
     * @return Menu or null if not found
     */
    Menu findById(int id);

    /**
     * Find menus by category ID
     * 
     * @param categoryId Category ID
     * @return List of menus in category
     */
    List<Menu> findByCategory(int categoryId);

    /**
     * Find active menus by category ID
     * 
     * @param categoryId Category ID
     * @return List of active menus in category
     */
    List<Menu> findActiveByCategoryId(int categoryId);

    /**
     * Search menus by name
     * 
     * @param keyword Search keyword
     * @return List of matching menus
     */
    List<Menu> searchByName(String keyword);

    /**
     * Insert new menu
     * 
     * @param menu Menu to insert
     * @return Generated ID or -1 if failed
     */
    int insert(Menu menu);

    /**
     * Update existing menu
     * 
     * @param menu Menu to update
     * @return true if successful
     */
    boolean update(Menu menu);

    /**
     * Update menu active status
     * 
     * @param id       Menu ID
     * @param isActive New active status
     * @return true if successful
     */
    boolean updateStatus(int id, boolean isActive);

    /**
     * Delete menu by ID
     * 
     * @param id Menu ID
     * @return true if successful
     */
    boolean delete(int id);

    /**
     * Count total menus
     * 
     * @return Total count
     */
    int count();

    /**
     * Count active menus
     * 
     * @return Active count
     */
    int countActive();

    /**
     * Count menus by category
     * 
     * @param categoryId Category ID
     * @return Count in category
     */
    int countByCategory(int categoryId);
}
