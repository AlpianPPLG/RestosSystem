package com.restos.dao;

import com.restos.config.DatabaseConfig;
import com.restos.model.Menu;
import com.restos.model.OrderItem;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of OrderItemDAO interface
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class OrderItemDAOImpl implements OrderItemDAO {

    private final DatabaseConfig dbConfig;

    public OrderItemDAOImpl() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    @Override
    public List<OrderItem> findAll() {
        List<OrderItem> orderItems = new ArrayList<>();
        String sql = "SELECT oi.id, oi.order_id, oi.menu_id, oi.quantity, oi.price, oi.subtotal, " +
                "oi.notes, oi.status, oi.created_at, " +
                "m.name as menu_name, m.description as menu_description, m.price as menu_price, " +
                "m.image_url, m.is_available " +
                "FROM order_items oi " +
                "LEFT JOIN menus m ON oi.menu_id = m.id " +
                "ORDER BY oi.created_at DESC";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                orderItems.add(mapResultSetToOrderItem(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all order items: " + e.getMessage());
        }

        return orderItems;
    }

    @Override
    public OrderItem findById(int id) {
        String sql = "SELECT oi.id, oi.order_id, oi.menu_id, oi.quantity, oi.price, oi.subtotal, " +
                "oi.notes, oi.status, oi.created_at, " +
                "m.name as menu_name, m.description as menu_description, m.price as menu_price, " +
                "m.image_url, m.is_available " +
                "FROM order_items oi " +
                "LEFT JOIN menus m ON oi.menu_id = m.id " +
                "WHERE oi.id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOrderItem(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding order item by ID: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<OrderItem> findByOrderId(int orderId) {
        List<OrderItem> orderItems = new ArrayList<>();
        String sql = "SELECT oi.id, oi.order_id, oi.menu_id, oi.quantity, oi.price, oi.subtotal, " +
                "oi.notes, oi.status, oi.created_at, " +
                "m.name as menu_name, m.description as menu_description, m.price as menu_price, " +
                "m.image_url, m.is_available " +
                "FROM order_items oi " +
                "LEFT JOIN menus m ON oi.menu_id = m.id " +
                "WHERE oi.order_id = ? " +
                "ORDER BY oi.created_at ASC";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orderItems.add(mapResultSetToOrderItem(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding order items by order ID: " + e.getMessage());
        }

        return orderItems;
    }

    @Override
    public List<OrderItem> findByStatus(String status) {
        List<OrderItem> orderItems = new ArrayList<>();
        String sql = "SELECT oi.id, oi.order_id, oi.menu_id, oi.quantity, oi.price, oi.subtotal, " +
                "oi.notes, oi.status, oi.created_at, " +
                "m.name as menu_name, m.description as menu_description, m.price as menu_price, " +
                "m.image_url, m.is_available " +
                "FROM order_items oi " +
                "LEFT JOIN menus m ON oi.menu_id = m.id " +
                "WHERE oi.status = ? " +
                "ORDER BY oi.created_at ASC";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orderItems.add(mapResultSetToOrderItem(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding order items by status: " + e.getMessage());
        }

        return orderItems;
    }

    @Override
    public List<OrderItem> findForKitchen() {
        List<OrderItem> orderItems = new ArrayList<>();
        String sql = "SELECT oi.id, oi.order_id, oi.menu_id, oi.quantity, oi.price, oi.subtotal, " +
                "oi.notes, oi.status, oi.created_at, " +
                "m.name as menu_name, m.description as menu_description, m.price as menu_price, " +
                "m.image_url, m.is_available, " +
                "o.table_id, o.customer_name, o.order_type, " +
                "t.table_number " +
                "FROM order_items oi " +
                "LEFT JOIN menus m ON oi.menu_id = m.id " +
                "LEFT JOIN orders o ON oi.order_id = o.id " +
                "LEFT JOIN tables t ON o.table_id = t.id " +
                "WHERE oi.status IN ('pending', 'cooking') " +
                "ORDER BY oi.created_at ASC";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                orderItems.add(mapResultSetToOrderItem(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding order items for kitchen: " + e.getMessage());
        }

        return orderItems;
    }

    @Override
    public List<OrderItem> findByMenuId(int menuId) {
        List<OrderItem> orderItems = new ArrayList<>();
        String sql = "SELECT oi.id, oi.order_id, oi.menu_id, oi.quantity, oi.price, oi.subtotal, " +
                "oi.notes, oi.status, oi.created_at, " +
                "m.name as menu_name, m.description as menu_description, m.price as menu_price, " +
                "m.image_url, m.is_available " +
                "FROM order_items oi " +
                "LEFT JOIN menus m ON oi.menu_id = m.id " +
                "WHERE oi.menu_id = ? " +
                "ORDER BY oi.created_at DESC";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, menuId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orderItems.add(mapResultSetToOrderItem(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding order items by menu ID: " + e.getMessage());
        }

        return orderItems;
    }

    @Override
    public int insert(OrderItem orderItem) {
        String sql = "INSERT INTO order_items (order_id, menu_id, quantity, price, subtotal, notes, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, orderItem.getOrderId());
            stmt.setInt(2, orderItem.getMenuId());
            stmt.setInt(3, orderItem.getQuantity());
            stmt.setBigDecimal(4, orderItem.getPrice());
            stmt.setBigDecimal(5, orderItem.getSubtotal());
            stmt.setString(6, orderItem.getNotes());
            stmt.setString(7, orderItem.getStatus() != null ? orderItem.getStatus() : OrderItem.STATUS_PENDING);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting order item: " + e.getMessage());
        }

        return -1;
    }

    @Override
    public boolean insertBatch(int orderId, List<OrderItem> orderItems) {
        String sql = "INSERT INTO order_items (order_id, menu_id, quantity, price, subtotal, notes, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (OrderItem item : orderItems) {
                stmt.setInt(1, orderId);
                stmt.setInt(2, item.getMenuId());
                stmt.setInt(3, item.getQuantity());
                stmt.setBigDecimal(4, item.getPrice());
                stmt.setBigDecimal(5, item.getSubtotal());
                stmt.setString(6, item.getNotes());
                stmt.setString(7, item.getStatus() != null ? item.getStatus() : OrderItem.STATUS_PENDING);
                stmt.addBatch();
            }

            int[] results = stmt.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);

            for (int result : results) {
                if (result < 0) {
                    return false;
                }
            }
            return true;

        } catch (SQLException e) {
            System.err.println("Error batch inserting order items: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean update(OrderItem orderItem) {
        String sql = "UPDATE order_items SET menu_id = ?, quantity = ?, price = ?, subtotal = ?, " +
                "notes = ?, status = ? WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderItem.getMenuId());
            stmt.setInt(2, orderItem.getQuantity());
            stmt.setBigDecimal(3, orderItem.getPrice());
            stmt.setBigDecimal(4, orderItem.getSubtotal());
            stmt.setString(5, orderItem.getNotes());
            stmt.setString(6, orderItem.getStatus());
            stmt.setInt(7, orderItem.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order item: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE order_items SET status = ? WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order item status: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean updateQuantity(int id, int quantity) {
        // First get current item to recalculate subtotal
        OrderItem item = findById(id);
        if (item == null)
            return false;

        BigDecimal newSubtotal = item.getPrice().multiply(BigDecimal.valueOf(quantity));

        String sql = "UPDATE order_items SET quantity = ?, subtotal = ? WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantity);
            stmt.setBigDecimal(2, newSubtotal);
            stmt.setInt(3, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order item quantity: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM order_items WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting order item: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean deleteByOrderId(int orderId) {
        String sql = "DELETE FROM order_items WHERE order_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            return stmt.executeUpdate() >= 0; // May have 0 items
        } catch (SQLException e) {
            System.err.println("Error deleting order items by order ID: " + e.getMessage());
        }

        return false;
    }

    @Override
    public int countByOrderId(int orderId) {
        String sql = "SELECT COUNT(*) FROM order_items WHERE order_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting order items: " + e.getMessage());
        }

        return 0;
    }

    @Override
    public int countPending() {
        String sql = "SELECT COUNT(*) FROM order_items WHERE status = 'pending'";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting pending order items: " + e.getMessage());
        }

        return 0;
    }

    @Override
    public int getTodaySoldByMenuId(int menuId) {
        String sql = "SELECT COALESCE(SUM(oi.quantity), 0) FROM order_items oi " +
                "INNER JOIN orders o ON oi.order_id = o.id " +
                "WHERE oi.menu_id = ? AND DATE(o.created_at) = CURDATE() " +
                "AND o.status IN ('completed', 'delivered', 'processing')";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, menuId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting today's sold quantity: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Map ResultSet row to OrderItem object
     */
    private OrderItem mapResultSetToOrderItem(ResultSet rs) throws SQLException {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(rs.getInt("id"));
        orderItem.setOrderId(rs.getInt("order_id"));
        orderItem.setMenuId(rs.getInt("menu_id"));
        orderItem.setQuantity(rs.getInt("quantity"));
        orderItem.setPrice(rs.getBigDecimal("price"));
        orderItem.setSubtotal(rs.getBigDecimal("subtotal"));
        orderItem.setNotes(rs.getString("notes"));
        orderItem.setStatus(rs.getString("status"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            orderItem.setCreatedAt(createdAt.toLocalDateTime());
        }

        // Map menu if available
        String menuName = rs.getString("menu_name");
        if (menuName != null) {
            Menu menu = new Menu();
            menu.setId(rs.getInt("menu_id"));
            menu.setName(menuName);
            menu.setDescription(rs.getString("menu_description"));
            menu.setPrice(rs.getBigDecimal("menu_price"));
            menu.setImageUrl(rs.getString("image_url"));
            menu.setIsAvailable(rs.getBoolean("is_available"));
            orderItem.setMenu(menu);
        }

        return orderItem;
    }
}
