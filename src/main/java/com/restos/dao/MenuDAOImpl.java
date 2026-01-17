package com.restos.dao;

import com.restos.config.DatabaseConfig;
import com.restos.model.Category;
import com.restos.model.Menu;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of MenuDAO interface
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class MenuDAOImpl implements MenuDAO {

    private final DatabaseConfig dbConfig;

    public MenuDAOImpl() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    @Override
    public List<Menu> findAll() {
        List<Menu> menus = new ArrayList<>();
        String sql = "SELECT m.id, m.category_id, m.name, m.description, m.price, m.image_url, m.is_active, m.created_at, "
                +
                "c.name as category_name, c.icon as category_icon " +
                "FROM menus m LEFT JOIN categories c ON m.category_id = c.id " +
                "ORDER BY c.sort_order, m.name";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                menus.add(mapResultSetToMenu(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all menus: " + e.getMessage());
        }

        return menus;
    }

    @Override
    public List<Menu> findAllActive() {
        List<Menu> menus = new ArrayList<>();
        String sql = "SELECT m.id, m.category_id, m.name, m.description, m.price, m.image_url, m.is_active, m.created_at, "
                +
                "c.name as category_name, c.icon as category_icon " +
                "FROM menus m LEFT JOIN categories c ON m.category_id = c.id " +
                "WHERE m.is_active = TRUE ORDER BY c.sort_order, m.name";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                menus.add(mapResultSetToMenu(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding active menus: " + e.getMessage());
        }

        return menus;
    }

    @Override
    public Menu findById(int id) {
        String sql = "SELECT m.id, m.category_id, m.name, m.description, m.price, m.image_url, m.is_active, m.created_at, "
                +
                "c.name as category_name, c.icon as category_icon " +
                "FROM menus m LEFT JOIN categories c ON m.category_id = c.id " +
                "WHERE m.id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMenu(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding menu by ID: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Menu> findByCategory(int categoryId) {
        List<Menu> menus = new ArrayList<>();
        String sql = "SELECT m.id, m.category_id, m.name, m.description, m.price, m.image_url, m.is_active, m.created_at, "
                +
                "c.name as category_name, c.icon as category_icon " +
                "FROM menus m LEFT JOIN categories c ON m.category_id = c.id " +
                "WHERE m.category_id = ? ORDER BY m.name";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    menus.add(mapResultSetToMenu(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding menus by category: " + e.getMessage());
        }

        return menus;
    }

    @Override
    public List<Menu> findActiveByCategoryId(int categoryId) {
        List<Menu> menus = new ArrayList<>();
        String sql = "SELECT m.id, m.category_id, m.name, m.description, m.price, m.image_url, m.is_active, m.created_at, "
                +
                "c.name as category_name, c.icon as category_icon " +
                "FROM menus m LEFT JOIN categories c ON m.category_id = c.id " +
                "WHERE m.category_id = ? AND m.is_active = TRUE ORDER BY m.name";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    menus.add(mapResultSetToMenu(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding active menus by category: " + e.getMessage());
        }

        return menus;
    }

    @Override
    public List<Menu> searchByName(String keyword) {
        List<Menu> menus = new ArrayList<>();
        String sql = "SELECT m.id, m.category_id, m.name, m.description, m.price, m.image_url, m.is_active, m.created_at, "
                +
                "c.name as category_name, c.icon as category_icon " +
                "FROM menus m LEFT JOIN categories c ON m.category_id = c.id " +
                "WHERE m.name LIKE ? ORDER BY m.name";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    menus.add(mapResultSetToMenu(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching menus: " + e.getMessage());
        }

        return menus;
    }

    @Override
    public int insert(Menu menu) {
        String sql = "INSERT INTO menus (category_id, name, description, price, image_url, is_active) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, menu.getCategoryId());
            stmt.setString(2, menu.getName());
            stmt.setString(3, menu.getDescription());
            stmt.setBigDecimal(4, menu.getPrice());
            stmt.setString(5, menu.getImageUrl());
            stmt.setBoolean(6, menu.isActive());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting menu: " + e.getMessage());
        }

        return -1;
    }

    @Override
    public boolean update(Menu menu) {
        String sql = "UPDATE menus SET category_id = ?, name = ?, description = ?, price = ?, image_url = ?, is_active = ? WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, menu.getCategoryId());
            stmt.setString(2, menu.getName());
            stmt.setString(3, menu.getDescription());
            stmt.setBigDecimal(4, menu.getPrice());
            stmt.setString(5, menu.getImageUrl());
            stmt.setBoolean(6, menu.isActive());
            stmt.setInt(7, menu.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating menu: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean updateStatus(int id, boolean isActive) {
        String sql = "UPDATE menus SET is_active = ? WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, isActive);
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating menu status: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM menus WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting menu: " + e.getMessage());
        }

        return false;
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM menus";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting menus: " + e.getMessage());
        }

        return 0;
    }

    @Override
    public int countActive() {
        String sql = "SELECT COUNT(*) FROM menus WHERE is_active = TRUE";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting active menus: " + e.getMessage());
        }

        return 0;
    }

    @Override
    public int countByCategory(int categoryId) {
        String sql = "SELECT COUNT(*) FROM menus WHERE category_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting menus by category: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Map ResultSet row to Menu object
     */
    private Menu mapResultSetToMenu(ResultSet rs) throws SQLException {
        Menu menu = new Menu();
        menu.setId(rs.getInt("id"));
        menu.setCategoryId(rs.getInt("category_id"));
        menu.setName(rs.getString("name"));
        menu.setDescription(rs.getString("description"));
        menu.setPrice(rs.getBigDecimal("price"));
        menu.setImageUrl(rs.getString("image_url"));
        menu.setActive(rs.getBoolean("is_active"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            menu.setCreatedAt(createdAt.toLocalDateTime());
        }

        // Map category if available
        String categoryName = rs.getString("category_name");
        if (categoryName != null) {
            Category category = new Category();
            category.setId(rs.getInt("category_id"));
            category.setName(categoryName);
            category.setIcon(rs.getString("category_icon"));
            menu.setCategory(category);
        }

        return menu;
    }
}
