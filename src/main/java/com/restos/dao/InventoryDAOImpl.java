package com.restos.dao;

import com.restos.config.DatabaseConfig;
import com.restos.model.Inventory;
import com.restos.model.Menu;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of InventoryDAO interface
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class InventoryDAOImpl implements InventoryDAO {

    private final DatabaseConfig dbConfig;

    public InventoryDAOImpl() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    @Override
    public List<Inventory> findAll() {
        List<Inventory> inventories = new ArrayList<>();
        String sql = "SELECT i.id, i.menu_id, i.daily_stock, i.remaining_stock, i.last_updated, " +
                "m.name as menu_name, m.price as menu_price " +
                "FROM inventories i LEFT JOIN menus m ON i.menu_id = m.id " +
                "ORDER BY m.name";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                inventories.add(mapResultSetToInventory(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all inventories: " + e.getMessage());
        }

        return inventories;
    }

    @Override
    public Inventory findById(int id) {
        String sql = "SELECT i.id, i.menu_id, i.daily_stock, i.remaining_stock, i.last_updated, " +
                "m.name as menu_name, m.price as menu_price " +
                "FROM inventories i LEFT JOIN menus m ON i.menu_id = m.id " +
                "WHERE i.id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInventory(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding inventory by ID: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Inventory findByMenuId(int menuId) {
        String sql = "SELECT i.id, i.menu_id, i.daily_stock, i.remaining_stock, i.last_updated, " +
                "m.name as menu_name, m.price as menu_price " +
                "FROM inventories i LEFT JOIN menus m ON i.menu_id = m.id " +
                "WHERE i.menu_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, menuId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInventory(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding inventory by menu ID: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Inventory> findLowStock(int threshold) {
        List<Inventory> inventories = new ArrayList<>();
        String sql = "SELECT i.id, i.menu_id, i.daily_stock, i.remaining_stock, i.last_updated, " +
                "m.name as menu_name, m.price as menu_price " +
                "FROM inventories i LEFT JOIN menus m ON i.menu_id = m.id " +
                "WHERE i.daily_stock > 0 AND (i.remaining_stock * 100 / i.daily_stock) <= ? " +
                "ORDER BY i.remaining_stock";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, threshold);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    inventories.add(mapResultSetToInventory(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding low stock inventories: " + e.getMessage());
        }

        return inventories;
    }

    @Override
    public List<Inventory> findOutOfStock() {
        List<Inventory> inventories = new ArrayList<>();
        String sql = "SELECT i.id, i.menu_id, i.daily_stock, i.remaining_stock, i.last_updated, " +
                "m.name as menu_name, m.price as menu_price " +
                "FROM inventories i LEFT JOIN menus m ON i.menu_id = m.id " +
                "WHERE i.remaining_stock <= 0 ORDER BY m.name";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                inventories.add(mapResultSetToInventory(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding out of stock inventories: " + e.getMessage());
        }

        return inventories;
    }

    @Override
    public int insert(Inventory inventory) {
        String sql = "INSERT INTO inventories (menu_id, daily_stock, remaining_stock) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE daily_stock = VALUES(daily_stock), remaining_stock = VALUES(remaining_stock)";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, inventory.getMenuId());
            stmt.setInt(2, inventory.getDailyStock());
            stmt.setInt(3, inventory.getRemainingStock());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting inventory: " + e.getMessage());
        }

        return -1;
    }

    @Override
    public boolean update(Inventory inventory) {
        String sql = "UPDATE inventories SET daily_stock = ?, remaining_stock = ? WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, inventory.getDailyStock());
            stmt.setInt(2, inventory.getRemainingStock());
            stmt.setInt(3, inventory.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating inventory: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean updateRemainingStock(int menuId, int remainingStock) {
        String sql = "UPDATE inventories SET remaining_stock = ? WHERE menu_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, remainingStock);
            stmt.setInt(2, menuId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating remaining stock: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean decreaseStock(int menuId, int quantity) {
        String sql = "UPDATE inventories SET remaining_stock = remaining_stock - ? WHERE menu_id = ? AND remaining_stock >= ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantity);
            stmt.setInt(2, menuId);
            stmt.setInt(3, quantity);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error decreasing stock: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean increaseStock(int menuId, int quantity) {
        String sql = "UPDATE inventories SET remaining_stock = LEAST(remaining_stock + ?, daily_stock) WHERE menu_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantity);
            stmt.setInt(2, menuId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error increasing stock: " + e.getMessage());
        }

        return false;
    }

    @Override
    public int resetAllStocks() {
        String sql = "UPDATE inventories SET remaining_stock = daily_stock";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement()) {

            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Error resetting all stocks: " + e.getMessage());
        }

        return 0;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM inventories WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting inventory: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean deleteByMenuId(int menuId) {
        String sql = "DELETE FROM inventories WHERE menu_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, menuId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting inventory by menu ID: " + e.getMessage());
        }

        return false;
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM inventories";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting inventories: " + e.getMessage());
        }

        return 0;
    }

    @Override
    public int countLowStock() {
        String sql = "SELECT COUNT(*) FROM inventories WHERE daily_stock > 0 AND (remaining_stock * 100 / daily_stock) <= 20";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting low stock: " + e.getMessage());
        }

        return 0;
    }

    @Override
    public int countOutOfStock() {
        String sql = "SELECT COUNT(*) FROM inventories WHERE remaining_stock <= 0";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting out of stock: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Map ResultSet row to Inventory object
     */
    private Inventory mapResultSetToInventory(ResultSet rs) throws SQLException {
        Inventory inventory = new Inventory();
        inventory.setId(rs.getInt("id"));
        inventory.setMenuId(rs.getInt("menu_id"));
        inventory.setDailyStock(rs.getInt("daily_stock"));
        inventory.setRemainingStock(rs.getInt("remaining_stock"));

        Timestamp lastUpdated = rs.getTimestamp("last_updated");
        if (lastUpdated != null) {
            inventory.setLastUpdated(lastUpdated.toLocalDateTime());
        }

        // Map menu if available
        String menuName = rs.getString("menu_name");
        if (menuName != null) {
            Menu menu = new Menu();
            menu.setId(rs.getInt("menu_id"));
            menu.setName(menuName);
            menu.setPrice(rs.getBigDecimal("menu_price"));
            inventory.setMenu(menu);
        }

        return inventory;
    }
}
