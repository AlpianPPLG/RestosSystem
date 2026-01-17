package com.restos.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database Configuration and Connection Manager
 * Handles MySQL database connections for the Restos application
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class DatabaseConfig {

    private static DatabaseConfig instance;
    private Properties properties;
    private Connection connection;

    // Configuration keys
    private static final String PROPS_FILE = "/config/database.properties";
    private static final String KEY_URL = "db.url";
    private static final String KEY_USERNAME = "db.username";
    private static final String KEY_PASSWORD = "db.password";
    private static final String KEY_DRIVER = "db.driver";

    /**
     * Private constructor for Singleton pattern
     */
    private DatabaseConfig() {
        loadProperties();
    }

    /**
     * Get singleton instance of DatabaseConfig
     * @return DatabaseConfig instance
     */
    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    /**
     * Load database properties from configuration file
     */
    private void loadProperties() {
        properties = new Properties();
        try (InputStream input = getClass().getResourceAsStream(PROPS_FILE)) {
            if (input == null) {
                System.err.println("Unable to find " + PROPS_FILE);
                // Set default values
                setDefaultProperties();
                return;
            }
            properties.load(input);
        } catch (IOException e) {
            System.err.println("Error loading database properties: " + e.getMessage());
            setDefaultProperties();
        }
    }

    /**
     * Set default database properties
     */
    private void setDefaultProperties() {
        properties.setProperty(KEY_URL, "jdbc:mysql://localhost:3306/restaus_db");
        properties.setProperty(KEY_USERNAME, "root");
        properties.setProperty(KEY_PASSWORD, "");
        properties.setProperty(KEY_DRIVER, "com.mysql.cj.jdbc.Driver");
    }

    /**
     * Get database connection
     * Creates a new connection if none exists or if existing connection is closed
     * 
     * @return Active database connection
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load JDBC driver
                Class.forName(properties.getProperty(KEY_DRIVER));
                
                // Build connection URL with additional parameters
                String url = properties.getProperty(KEY_URL);
                if (!url.contains("?")) {
                    url += "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta";
                }
                
                // Create connection
                connection = DriverManager.getConnection(
                    url,
                    properties.getProperty(KEY_USERNAME),
                    properties.getProperty(KEY_PASSWORD)
                );
                
                System.out.println("Database connection established successfully.");
                
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found: " + e.getMessage());
            }
        }
        return connection;
    }

    /**
     * Close the database connection
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    /**
     * Test database connection
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("Database connection test: SUCCESS");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Database connection test: FAILED - " + e.getMessage());
        }
        return false;
    }

    /**
     * Get a property value
     * @param key Property key
     * @return Property value or null
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Get database URL
     * @return Database URL string
     */
    public String getDatabaseUrl() {
        return properties.getProperty(KEY_URL);
    }

    /**
     * Get database username
     * @return Database username
     */
    public String getUsername() {
        return properties.getProperty(KEY_USERNAME);
    }
}
