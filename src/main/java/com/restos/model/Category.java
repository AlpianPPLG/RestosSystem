package com.restos.model;

/**
 * Category Model - Represents menu category
 * Maps to 'categories' table in database
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class Category {

    private int id;
    private String name;
    private String icon;
    private int sortOrder;

    /**
     * Default constructor
     */
    public Category() {
    }

    /**
     * Constructor with basic fields
     * 
     * @param id   Category ID
     * @param name Category name
     */
    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Full constructor
     * 
     * @param id        Category ID
     * @param name      Category name
     * @param icon      Icon name or URL
     * @param sortOrder Display order
     */
    public Category(int id, String name, String icon, int sortOrder) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.sortOrder = sortOrder;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public String toString() {
        return name; // For ComboBox display
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Category category = (Category) o;
        return id == category.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
