package com.restos.dao;

import com.restos.config.DatabaseConfig;
import com.restos.model.Order;
import com.restos.model.Table;
import com.restos.model.User;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of OrderDAO interface
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class OrderDAOImpl implements OrderDAO {

    private final DatabaseConfig dbConfig;
    private final OrderItemDAO orderItemDAO;

    public OrderDAOImpl() {
        this.dbConfig = DatabaseConfig.getInstance();
        this.orderItemDAO = new OrderItemDAOImpl();
    }

    @Override
    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.id, o.table_id, o.user_id, o.customer_name, o.order_type, o.status, " +
                "o.total_amount, o.created_at, o.updated_at, " +
                "t.table_number, t.capacity, t.status as table_status, " +
                "u.username, u.full_name " +
                "FROM orders o " +
                "LEFT JOIN tables t ON o.table_id = t.id " +
                "LEFT JOIN users u ON o.user_id = u.id " +
                "ORDER BY o.created_at DESC";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all orders: " + e.getMessage());
        }

        return orders;
    }

    @Override
    public Order findById(int id) {
        String sql = "SELECT o.id, o.table_id, o.user_id, o.customer_name, o.order_type, o.status, " +
                "o.total_amount, o.created_at, o.updated_at, " +
                "t.table_number, t.capacity, t.status as table_status, " +
                "u.username, u.full_name " +
                "FROM orders o " +
                "LEFT JOIN tables t ON o.table_id = t.id " +
                "LEFT JOIN users u ON o.user_id = u.id " +
                "WHERE o.id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOrder(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding order by ID: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Order findByIdWithItems(int id) {
        Order order = findById(id);
        if (order != null) {
            order.setOrderItems(orderItemDAO.findByOrderId(id));
        }
        return order;
    }

    @Override
    public List<Order> findByStatus(String status) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.id, o.table_id, o.user_id, o.customer_name, o.order_type, o.status, " +
                "o.total_amount, o.created_at, o.updated_at, " +
                "t.table_number, t.capacity, t.status as table_status, " +
                "u.username, u.full_name " +
                "FROM orders o " +
                "LEFT JOIN tables t ON o.table_id = t.id " +
                "LEFT JOIN users u ON o.user_id = u.id " +
                "WHERE o.status = ? ORDER BY o.created_at DESC";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding orders by status: " + e.getMessage());
        }

        return orders;
    }

    @Override
    public List<Order> findByTableId(int tableId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.id, o.table_id, o.user_id, o.customer_name, o.order_type, o.status, " +
                "o.total_amount, o.created_at, o.updated_at, " +
                "t.table_number, t.capacity, t.status as table_status, " +
                "u.username, u.full_name " +
                "FROM orders o " +
                "LEFT JOIN tables t ON o.table_id = t.id " +
                "LEFT JOIN users u ON o.user_id = u.id " +
                "WHERE o.table_id = ? ORDER BY o.created_at DESC";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tableId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding orders by table ID: " + e.getMessage());
        }

        return orders;
    }

    @Override
    public Order findActiveByTableId(int tableId) {
        String sql = "SELECT o.id, o.table_id, o.user_id, o.customer_name, o.order_type, o.status, " +
                "o.total_amount, o.created_at, o.updated_at, " +
                "t.table_number, t.capacity, t.status as table_status, " +
                "u.username, u.full_name " +
                "FROM orders o " +
                "LEFT JOIN tables t ON o.table_id = t.id " +
                "LEFT JOIN users u ON o.user_id = u.id " +
                "WHERE o.table_id = ? AND o.status NOT IN ('completed', 'cancelled') " +
                "ORDER BY o.created_at DESC LIMIT 1";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tableId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOrder(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding active order by table ID: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Order> findByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.id, o.table_id, o.user_id, o.customer_name, o.order_type, o.status, " +
                "o.total_amount, o.created_at, o.updated_at, " +
                "t.table_number, t.capacity, t.status as table_status, " +
                "u.username, u.full_name " +
                "FROM orders o " +
                "LEFT JOIN tables t ON o.table_id = t.id " +
                "LEFT JOIN users u ON o.user_id = u.id " +
                "WHERE o.user_id = ? ORDER BY o.created_at DESC";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding orders by user ID: " + e.getMessage());
        }

        return orders;
    }

    @Override
    public List<Order> findToday() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.id, o.table_id, o.user_id, o.customer_name, o.order_type, o.status, " +
                "o.total_amount, o.created_at, o.updated_at, " +
                "t.table_number, t.capacity, t.status as table_status, " +
                "u.username, u.full_name " +
                "FROM orders o " +
                "LEFT JOIN tables t ON o.table_id = t.id " +
                "LEFT JOIN users u ON o.user_id = u.id " +
                "WHERE DATE(o.created_at) = CURDATE() ORDER BY o.created_at DESC";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding today's orders: " + e.getMessage());
        }

        return orders;
    }

    @Override
    public List<Order> findByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.id, o.table_id, o.user_id, o.customer_name, o.order_type, o.status, " +
                "o.total_amount, o.created_at, o.updated_at, " +
                "t.table_number, t.capacity, t.status as table_status, " +
                "u.username, u.full_name " +
                "FROM orders o " +
                "LEFT JOIN tables t ON o.table_id = t.id " +
                "LEFT JOIN users u ON o.user_id = u.id " +
                "WHERE DATE(o.created_at) BETWEEN ? AND ? ORDER BY o.created_at DESC";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding orders by date range: " + e.getMessage());
        }

        return orders;
    }

    @Override
    public List<Order> findForKitchen() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.id, o.table_id, o.user_id, o.customer_name, o.order_type, o.status, " +
                "o.total_amount, o.created_at, o.updated_at, " +
                "t.table_number, t.capacity, t.status as table_status, " +
                "u.username, u.full_name " +
                "FROM orders o " +
                "LEFT JOIN tables t ON o.table_id = t.id " +
                "LEFT JOIN users u ON o.user_id = u.id " +
                "WHERE o.status IN ('pending', 'processing') " +
                "ORDER BY o.created_at ASC";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                order.setOrderItems(orderItemDAO.findByOrderId(order.getId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error finding orders for kitchen: " + e.getMessage());
        }

        return orders;
    }

    @Override
    public List<Order> findAwaitingPayment() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.id, o.table_id, o.user_id, o.customer_name, o.order_type, o.status, " +
                "o.total_amount, o.created_at, o.updated_at, " +
                "t.table_number, t.capacity, t.status as table_status, " +
                "u.username, u.full_name " +
                "FROM orders o " +
                "LEFT JOIN tables t ON o.table_id = t.id " +
                "LEFT JOIN users u ON o.user_id = u.id " +
                "LEFT JOIN payments p ON o.id = p.order_id " +
                "WHERE o.status = 'delivered' AND p.id IS NULL " +
                "ORDER BY o.created_at ASC";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                order.setOrderItems(orderItemDAO.findByOrderId(order.getId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error finding orders awaiting payment: " + e.getMessage());
        }

        return orders;
    }

    @Override
    public int insert(Order order) {
        String sql = "INSERT INTO orders (table_id, user_id, customer_name, order_type, status, total_amount) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (order.getTableId() != null) {
                stmt.setInt(1, order.getTableId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setInt(2, order.getUserId());
            stmt.setString(3, order.getCustomerName());
            stmt.setString(4, order.getOrderType());
            stmt.setString(5, order.getStatus() != null ? order.getStatus() : Order.STATUS_PENDING);
            stmt.setBigDecimal(6, order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting order: " + e.getMessage());
        }

        return -1;
    }

    @Override
    public boolean update(Order order) {
        String sql = "UPDATE orders SET table_id = ?, user_id = ?, customer_name = ?, order_type = ?, " +
                "status = ?, total_amount = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (order.getTableId() != null) {
                stmt.setInt(1, order.getTableId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setInt(2, order.getUserId());
            stmt.setString(3, order.getCustomerName());
            stmt.setString(4, order.getOrderType());
            stmt.setString(5, order.getStatus());
            stmt.setBigDecimal(6, order.getTotalAmount());
            stmt.setInt(7, order.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE orders SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order status: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean updateTotalAmount(int id, BigDecimal totalAmount) {
        String sql = "UPDATE orders SET total_amount = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, totalAmount);
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order total: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM orders WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting order: " + e.getMessage());
        }

        return false;
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM orders";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting orders: " + e.getMessage());
        }

        return 0;
    }

    @Override
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM orders WHERE status = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting orders by status: " + e.getMessage());
        }

        return 0;
    }

    @Override
    public int countToday() {
        String sql = "SELECT COUNT(*) FROM orders WHERE DATE(created_at) = CURDATE()";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting today's orders: " + e.getMessage());
        }

        return 0;
    }

    @Override
    public double getTodayRevenue() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM orders " +
                "WHERE DATE(created_at) = CURDATE() AND status = 'completed'";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting today's revenue: " + e.getMessage());
        }

        return 0.0;
    }

    @Override
    public double getRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM orders " +
                "WHERE DATE(created_at) BETWEEN ? AND ? AND status = 'completed'";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting revenue by date range: " + e.getMessage());
        }

        return 0.0;
    }

    /**
     * Map ResultSet row to Order object
     */
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));

        int tableId = rs.getInt("table_id");
        if (!rs.wasNull()) {
            order.setTableId(tableId);
        }

        order.setUserId(rs.getInt("user_id"));
        order.setCustomerName(rs.getString("customer_name"));
        order.setOrderType(rs.getString("order_type"));
        order.setStatus(rs.getString("status"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            order.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            order.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        // Map table if available
        String tableNumber = rs.getString("table_number");
        if (tableNumber != null) {
            Table table = new Table();
            table.setId(tableId);
            table.setTableNumber(tableNumber);
            table.setCapacity(rs.getInt("capacity"));
            table.setStatus(rs.getString("table_status"));
            order.setTable(table);
        }

        // Map user if available
        String username = rs.getString("username");
        if (username != null) {
            User user = new User();
            user.setId(rs.getInt("user_id"));
            user.setUsername(username);
            user.setFullName(rs.getString("full_name"));
            order.setUser(user);
        }

        return order;
    }
}
