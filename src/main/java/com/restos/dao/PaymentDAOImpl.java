package com.restos.dao;

import com.restos.config.DatabaseConfig;
import com.restos.model.Order;
import com.restos.model.Payment;
import com.restos.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of PaymentDAO interface
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class PaymentDAOImpl implements PaymentDAO {

    private final DatabaseConfig dbConfig;

    public PaymentDAOImpl() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    @Override
    public List<Payment> findAll() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.id, p.order_id, p.user_id, p.amount_paid, p.change_amount, " +
                "p.payment_method, p.created_at, " +
                "o.table_id, o.customer_name, o.order_type, o.status as order_status, o.total_amount, " +
                "u.username, u.full_name " +
                "FROM payments p " +
                "LEFT JOIN orders o ON p.order_id = o.id " +
                "LEFT JOIN users u ON p.user_id = u.id " +
                "ORDER BY p.created_at DESC";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all payments: " + e.getMessage());
        }

        return payments;
    }

    @Override
    public Payment findById(int id) {
        String sql = "SELECT p.id, p.order_id, p.user_id, p.amount_paid, p.change_amount, " +
                "p.payment_method, p.created_at, " +
                "o.table_id, o.customer_name, o.order_type, o.status as order_status, o.total_amount, " +
                "u.username, u.full_name " +
                "FROM payments p " +
                "LEFT JOIN orders o ON p.order_id = o.id " +
                "LEFT JOIN users u ON p.user_id = u.id " +
                "WHERE p.id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPayment(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding payment by ID: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Payment findByOrderId(int orderId) {
        String sql = "SELECT p.id, p.order_id, p.user_id, p.amount_paid, p.change_amount, " +
                "p.payment_method, p.created_at, " +
                "o.table_id, o.customer_name, o.order_type, o.status as order_status, o.total_amount, " +
                "u.username, u.full_name " +
                "FROM payments p " +
                "LEFT JOIN orders o ON p.order_id = o.id " +
                "LEFT JOIN users u ON p.user_id = u.id " +
                "WHERE p.order_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPayment(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding payment by order ID: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Payment> findByUserId(int userId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.id, p.order_id, p.user_id, p.amount_paid, p.change_amount, " +
                "p.payment_method, p.created_at, " +
                "o.table_id, o.customer_name, o.order_type, o.status as order_status, o.total_amount, " +
                "u.username, u.full_name " +
                "FROM payments p " +
                "LEFT JOIN orders o ON p.order_id = o.id " +
                "LEFT JOIN users u ON p.user_id = u.id " +
                "WHERE p.user_id = ? ORDER BY p.created_at DESC";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToPayment(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding payments by user ID: " + e.getMessage());
        }

        return payments;
    }

    @Override
    public List<Payment> findByPaymentMethod(String paymentMethod) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.id, p.order_id, p.user_id, p.amount_paid, p.change_amount, " +
                "p.payment_method, p.created_at, " +
                "o.table_id, o.customer_name, o.order_type, o.status as order_status, o.total_amount, " +
                "u.username, u.full_name " +
                "FROM payments p " +
                "LEFT JOIN orders o ON p.order_id = o.id " +
                "LEFT JOIN users u ON p.user_id = u.id " +
                "WHERE p.payment_method = ? ORDER BY p.created_at DESC";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, paymentMethod);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToPayment(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding payments by payment method: " + e.getMessage());
        }

        return payments;
    }

    @Override
    public List<Payment> findToday() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.id, p.order_id, p.user_id, p.amount_paid, p.change_amount, " +
                "p.payment_method, p.created_at, " +
                "o.table_id, o.customer_name, o.order_type, o.status as order_status, o.total_amount, " +
                "u.username, u.full_name " +
                "FROM payments p " +
                "LEFT JOIN orders o ON p.order_id = o.id " +
                "LEFT JOIN users u ON p.user_id = u.id " +
                "WHERE DATE(p.created_at) = CURDATE() ORDER BY p.created_at DESC";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding today's payments: " + e.getMessage());
        }

        return payments;
    }

    @Override
    public List<Payment> findByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.id, p.order_id, p.user_id, p.amount_paid, p.change_amount, " +
                "p.payment_method, p.created_at, " +
                "o.table_id, o.customer_name, o.order_type, o.status as order_status, o.total_amount, " +
                "u.username, u.full_name " +
                "FROM payments p " +
                "LEFT JOIN orders o ON p.order_id = o.id " +
                "LEFT JOIN users u ON p.user_id = u.id " +
                "WHERE DATE(p.created_at) BETWEEN ? AND ? ORDER BY p.created_at DESC";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToPayment(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding payments by date range: " + e.getMessage());
        }

        return payments;
    }

    @Override
    public int insert(Payment payment) {
        String sql = "INSERT INTO payments (order_id, user_id, amount_paid, change_amount, payment_method) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, payment.getOrderId());
            stmt.setInt(2, payment.getUserId());
            stmt.setBigDecimal(3, payment.getAmountPaid());
            stmt.setBigDecimal(4, payment.getChangeAmount());
            stmt.setString(5, payment.getPaymentMethod());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting payment: " + e.getMessage());
        }

        return -1;
    }

    @Override
    public boolean update(Payment payment) {
        String sql = "UPDATE payments SET order_id = ?, user_id = ?, amount_paid = ?, " +
                "change_amount = ?, payment_method = ? WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, payment.getOrderId());
            stmt.setInt(2, payment.getUserId());
            stmt.setBigDecimal(3, payment.getAmountPaid());
            stmt.setBigDecimal(4, payment.getChangeAmount());
            stmt.setString(5, payment.getPaymentMethod());
            stmt.setInt(6, payment.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating payment: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM payments WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting payment: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean orderHasPayment(int orderId) {
        String sql = "SELECT COUNT(*) FROM payments WHERE order_id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking if order has payment: " + e.getMessage());
        }

        return false;
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM payments";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting payments: " + e.getMessage());
        }

        return 0;
    }

    @Override
    public int countToday() {
        String sql = "SELECT COUNT(*) FROM payments WHERE DATE(created_at) = CURDATE()";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting today's payments: " + e.getMessage());
        }

        return 0;
    }

    @Override
    public int countByPaymentMethod(String paymentMethod) {
        String sql = "SELECT COUNT(*) FROM payments WHERE payment_method = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, paymentMethod);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting payments by payment method: " + e.getMessage());
        }

        return 0;
    }

    @Override
    public double getTodayRevenue() {
        String sql = "SELECT COALESCE(SUM(amount_paid), 0) - COALESCE(SUM(change_amount), 0) " +
                "FROM payments WHERE DATE(created_at) = CURDATE()";

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
        String sql = "SELECT COALESCE(SUM(amount_paid), 0) - COALESCE(SUM(change_amount), 0) " +
                "FROM payments WHERE DATE(created_at) BETWEEN ? AND ?";

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

    @Override
    public double getTodayRevenueByPaymentMethod(String paymentMethod) {
        String sql = "SELECT COALESCE(SUM(amount_paid), 0) - COALESCE(SUM(change_amount), 0) " +
                "FROM payments WHERE DATE(created_at) = CURDATE() AND payment_method = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, paymentMethod);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting today's revenue by payment method: " + e.getMessage());
        }

        return 0.0;
    }

    @Override
    public double getTodayChangeGiven() {
        String sql = "SELECT COALESCE(SUM(change_amount), 0) FROM payments WHERE DATE(created_at) = CURDATE()";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting today's change given: " + e.getMessage());
        }

        return 0.0;
    }

    /**
     * Map ResultSet row to Payment object
     */
    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setId(rs.getInt("id"));
        payment.setOrderId(rs.getInt("order_id"));
        payment.setUserId(rs.getInt("user_id"));
        payment.setAmountPaid(rs.getBigDecimal("amount_paid"));
        payment.setChangeAmount(rs.getBigDecimal("change_amount"));
        payment.setPaymentMethod(rs.getString("payment_method"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            payment.setCreatedAt(createdAt.toLocalDateTime());
        }

        // Map order if available
        String customerName = rs.getString("customer_name");
        if (customerName != null) {
            Order order = new Order();
            order.setId(rs.getInt("order_id"));
            order.setCustomerName(customerName);
            order.setOrderType(rs.getString("order_type"));
            order.setStatus(rs.getString("order_status"));
            order.setTotalAmount(rs.getBigDecimal("total_amount"));

            int tableId = rs.getInt("table_id");
            if (!rs.wasNull()) {
                order.setTableId(tableId);
            }
            payment.setOrder(order);
        }

        // Map user (cashier) if available
        String username = rs.getString("username");
        if (username != null) {
            User user = new User();
            user.setId(rs.getInt("user_id"));
            user.setUsername(username);
            user.setFullName(rs.getString("full_name"));
            payment.setUser(user);
        }

        return payment;
    }
}
