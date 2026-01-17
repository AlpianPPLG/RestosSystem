package com.restos.dao;

import com.restos.model.Category;
import java.util.List;

/**
 * Data Access Object interface for Category operations
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public interface CategoryDAO {

    /**
     * Find all categories ordered by sort_order
     * 
     * @return List of all categories
     */
    List<Category> findAll();

    /**
     * Find category by ID
     * 
     * @param id Category ID
     * @return Category or null if not found
     */
    Category findById(int id);

    /**
     * Find category by name
     * 
     * @param name Category name
     * @return Category or null if not found
     */
    Category findByName(String name);

    /**
     * Insert new category
     * 
     * @param category Category to insert
     * @return Generated ID or -1 if failed
     */
    int insert(Category category);

    /**
     * Update existing category
     * 
     * @param category Category to update
     * @return true if successful
     */
    boolean update(Category category);

    /**
     * Update sort order
     * 
     * @param id        Category ID
     * @param sortOrder New sort order
     * @return true if successful
     */
    boolean updateSortOrder(int id, int sortOrder);

    /**
     * Delete category by ID
     * 
     * @param id Category ID
     * @return true if successful
     */
    boolean delete(int id);

    /**
     * Count total categories
     * 
     * @return Total count
     */
    int count();
}
