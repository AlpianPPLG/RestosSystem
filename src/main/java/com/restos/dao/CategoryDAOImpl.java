package com.restos.dao;

import com.restos.config.DatabaseConfig;
import com.restos.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of CategoryDAO interface
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class CategoryDAOImpl implements CategoryDAO {

    private final DatabaseConfig dbConfig;

    public CategoryDAOImpl() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    @Override
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT id, name, icon, sort_order FROM categories ORDER BY sort_order, name";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all categories: " + e.getMessage());
        }

        return categories;
    }

    @Override
    public Category findById(int id) {
        String sql = "SELECT id, name, icon, sort_order FROM categories WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategory(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding category by ID: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Category findByName(String name) {
        String sql = "SELECT id, name, icon, sort_order FROM categories WHERE name = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategory(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding category by name: " + e.getMessage());
        }

        return null;
    }

    @Override
    public int insert(Category category) {
        String sql = "INSERT INTO categories (name, icon, sort_order) VALUES (?, ?, ?)";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, category.getName());
            stmt.setString(2, category.getIcon());
            stmt.setInt(3, category.getSortOrder());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting category: " + e.getMessage());
        }

        return -1;
    }

    @Override
    public boolean update(Category category) {
        String sql = "UPDATE categories SET name = ?, icon = ?, sort_order = ? WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category.getName());
            stmt.setString(2, category.getIcon());
            stmt.setInt(3, category.getSortOrder());
            stmt.setInt(4, category.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating category: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean updateSortOrder(int id, int sortOrder) {
        String sql = "UPDATE categories SET sort_order = ? WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sortOrder);
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating sort order: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM categories WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting category: " + e.getMessage());
        }

        return false;
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM categories";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting categories: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Map ResultSet row to Category object
     */
    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setId(rs.getInt("id"));
        category.setName(rs.getString("name"));
        category.setIcon(rs.getString("icon"));
        category.setSortOrder(rs.getInt("sort_order"));
        return category;
    }
}
