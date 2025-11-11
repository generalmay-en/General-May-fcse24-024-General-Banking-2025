package com.banking.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseManager handles all database connectivity for the banking system.
 * This class implements the Singleton pattern to ensure only one database connection manager exists.
 * It manages the database connection lifecycle and provides methods for initialization.
 */
public class DatabaseManager {
    // Singleton instance - only one DatabaseManager should exist
    private static DatabaseManager instance;
    
    // Database connection details - using H2 in-memory database for simplicity
    // Can easily be switched to MySQL, PostgreSQL, or SQLite
    private static final String DB_URL = "jdbc:h2:./bankingdb;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    
    // For MySQL, I would use:
    // private static final String DB_URL = "jdbc:mysql://localhost:3306/banking_system";
    // private static final String DB_USER = "root";
    // private static final String DB_PASSWORD = "password";
    
    /**
     * Private constructor implements Singleton pattern.
     * Prevents external instantiation - use getInstance() instead.
     */
    private DatabaseManager() {
        try {
            // Load the JDBC driver
            Class.forName("org.h2.Driver");
            // For MySQL: Class.forName("com.mysql.cj.jdbc.Driver");
            
            System.out.println("Database driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("Database driver not found: " + e.getMessage());
        }
    }
    
    /**
     * Gets the singleton instance of DatabaseManager.
     * Thread-safe implementation using synchronized.
     * @return The DatabaseManager instance
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * Establishes and returns a database connection.
     * Callers are responsible for closing the connection after use.
     * @return Active database connection
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    /**
     * Initializes the database schema by creating all necessary tables.
     * This method should be called once when the application starts.
     * Creates tables for: Users, Customers, Accounts, and Transactions
     */
    public void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create Users table for authentication
            String createUsersTable = 
                "CREATE TABLE IF NOT EXISTS users (" +
                "user_id VARCHAR(50) PRIMARY KEY, " +
                "username VARCHAR(100) NOT NULL, " +
                "password_hash VARCHAR(255) NOT NULL, " +
                "role VARCHAR(50) NOT NULL, " +
                "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
            stmt.execute(createUsersTable);
            
            // Create Customers table
            String createCustomersTable = 
                "CREATE TABLE IF NOT EXISTS customers (" +
                "customer_id VARCHAR(50) PRIMARY KEY, " +
                "first_name VARCHAR(100) NOT NULL, " +
                "surname VARCHAR(100) NOT NULL, " +
                "address VARCHAR(255), " +
                "phone_number VARCHAR(20), " +
                "email VARCHAR(100), " +
                "registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
            stmt.execute(createCustomersTable);
            
            // Create Accounts table with account type discrimination
            String createAccountsTable = 
                "CREATE TABLE IF NOT EXISTS accounts (" +
                "account_number VARCHAR(50) PRIMARY KEY, " +
                "customer_id VARCHAR(50) NOT NULL, " +
                "account_type VARCHAR(20) NOT NULL, " + // SAVINGS, INVESTMENT, CHEQUE
                "balance DECIMAL(15, 2) NOT NULL, " +
                "branch VARCHAR(50), " +
                "date_opened TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "company_name VARCHAR(200), " + // For Cheque accounts only
                "company_address VARCHAR(255), " + // For Cheque accounts only
                "FOREIGN KEY (customer_id) REFERENCES customers(customer_id)" +
                ")";
            stmt.execute(createAccountsTable);
            
            // Create Transactions table
            String createTransactionsTable = 
                "CREATE TABLE IF NOT EXISTS transactions (" +
                "transaction_id VARCHAR(50) PRIMARY KEY, " +
                "account_number VARCHAR(50) NOT NULL, " +
                "transaction_type VARCHAR(20) NOT NULL, " + // DEPOSIT, WITHDRAWAL, INTEREST, SALARY
                "amount DECIMAL(15, 2) NOT NULL, " +
                "balance_after DECIMAL(15, 2) NOT NULL, " +
                "description VARCHAR(255), " +
                "transaction_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (account_number) REFERENCES accounts(account_number)" +
                ")";
            stmt.execute(createTransactionsTable);
            
            System.out.println("Database schema initialized successfully");
            
            // Insert default admin user if not exists
            insertDefaultUser(conn);
            
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Inserts a default admin user for initial system access.
     * Username: admin, Password: admin123
     * This allows the system to be used immediately after setup.
     * @param conn Active database connection
     */
    private void insertDefaultUser(Connection conn) {
        String checkUserSQL = "SELECT COUNT(*) FROM users WHERE user_id = 'admin'";
        String insertUserSQL = 
            "INSERT INTO users (user_id, username, password_hash, role) " +
            "VALUES ('admin', 'Administrator', ?, 'ADMIN')";
        
        try (Statement stmt = conn.createStatement();
             var rs = stmt.executeQuery(checkUserSQL)) {
            
            rs.next();
            if (rs.getInt(1) == 0) {
                // User doesn't exist, create default admin
                try (var pstmt = conn.prepareStatement(insertUserSQL)) {
                    // Hash the default password "admin123"
                    String passwordHash = String.valueOf("admin123".hashCode());
                    pstmt.setString(1, passwordHash);
                    pstmt.executeUpdate();
                    System.out.println("Default admin user created (username: admin, password: admin123)");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating default user: " + e.getMessage());
        }
    }
    
    /**
     * Tests the database connection.
     * Useful for troubleshooting connectivity issues.
     * @return true if connection successful
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Closes a database connection safely.
     * Handles null connections gracefully.
     * @param conn Connection to close
     */
    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}