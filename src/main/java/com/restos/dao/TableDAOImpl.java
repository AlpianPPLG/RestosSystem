package com.restos.dao;

import com.restos.config.DatabaseConfig;
import com.restos.model.Table;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of TableDAO interface
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class TableDAOImpl implements TableDAO {

    private final DatabaseConfig dbConfig;

    public TableDAOImpl() {
        this.dbConfig = DatabaseConfig.getInstance();
    }

    @Override
    public List<Table> findAll() {
        List<Table> tables = new ArrayList<>();
        String sql = "SELECT id, table_number, capacity, status, created_at FROM tables ORDER BY table_number";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tables.add(mapResultSetToTable(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all tables: " + e.getMessage());
        }

        return tables;
    }

    @Override
    public Table findById(int id) {
        String sql = "SELECT id, table_number, capacity, status, created_at FROM tables WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTable(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding table by ID: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Table findByTableNumber(String tableNumber) {
        String sql = "SELECT id, table_number, capacity, status, created_at FROM tables WHERE table_number = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tableNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTable(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding table by number: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Table> findByStatus(String status) {
        List<Table> tables = new ArrayList<>();
        String sql = "SELECT id, table_number, capacity, status, created_at FROM tables WHERE status = ? ORDER BY table_number";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tables.add(mapResultSetToTable(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding tables by status: " + e.getMessage());
        }

        return tables;
    }

    @Override
    public List<Table> findAvailable() {
        return findByStatus(Table.STATUS_AVAILABLE);
    }

    @Override
    public int insert(Table table) {
        String sql = "INSERT INTO tables (table_number, capacity, status) VALUES (?, ?, ?)";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, table.getTableNumber());
            stmt.setInt(2, table.getCapacity());
            stmt.setString(3, table.getStatus() != null ? table.getStatus() : Table.STATUS_AVAILABLE);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting table: " + e.getMessage());
        }

        return -1;
    }

    @Override
    public boolean update(Table table) {
        String sql = "UPDATE tables SET table_number = ?, capacity = ?, status = ? WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, table.getTableNumber());
            stmt.setInt(2, table.getCapacity());
            stmt.setString(3, table.getStatus());
            stmt.setInt(4, table.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating table: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE tables SET status = ? WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating table status: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM tables WHERE id = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting table: " + e.getMessage());
        }

        return false;
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM tables";

        try (Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting tables: " + e.getMessage());
        }

        return 0;
    }

    @Override
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM tables WHERE status = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting tables by status: " + e.getMessage());
        }

        return 0;
    }

    @Override
    public boolean tableNumberExists(String tableNumber) {
        String sql = "SELECT COUNT(*) FROM tables WHERE table_number = ?";

        try (Connection conn = dbConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tableNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking table number: " + e.getMessage());
        }

        return false;
    }

    /**
     * Map ResultSet row to Table object
     */
    private Table mapResultSetToTable(ResultSet rs) throws SQLException {
        Table table = new Table();
        table.setId(rs.getInt("id"));
        table.setTableNumber(rs.getString("table_number"));
        table.setCapacity(rs.getInt("capacity"));
        table.setStatus(rs.getString("status"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            table.setCreatedAt(createdAt.toLocalDateTime());
        }

        return table;
    }
}
