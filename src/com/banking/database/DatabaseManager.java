package com.banking.database;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.banking.model.*;

/**
 * DatabaseManager with permanent file-based storage.
 * Data persists across application restarts.
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    
    // Data persists permanently in banking.mv.db file
    private static final String DB_URL = "jdbc:h2:./banking;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    
    private DatabaseManager() {
        try {
            Class.forName("org.h2.Driver");
            System.out.println("✓ H2 Database driver loaded (File-based mode)");
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Database driver not found: " + e.getMessage());
        }
    }
    
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    public void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create users table
            String createUsersTable = 
                "CREATE TABLE IF NOT EXISTS users (" +
                "user_id VARCHAR(50) PRIMARY KEY, " +
                "username VARCHAR(100) NOT NULL, " +
                "password_hash VARCHAR(255) NOT NULL, " +
                "role VARCHAR(50) NOT NULL, " +
                "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
            stmt.execute(createUsersTable);
            
            // Create customers table
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
            
            // Create accounts table
            String createAccountsTable = 
                "CREATE TABLE IF NOT EXISTS accounts (" +
                "account_number VARCHAR(50) PRIMARY KEY, " +
                "customer_id VARCHAR(50) NOT NULL, " +
                "account_type VARCHAR(20) NOT NULL, " +
                "balance DECIMAL(15, 2) NOT NULL, " +
                "branch VARCHAR(50), " +
                "date_opened TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "company_name VARCHAR(200), " +
                "company_address VARCHAR(255), " +
                "FOREIGN KEY (customer_id) REFERENCES customers(customer_id)" +
                ")";
            stmt.execute(createAccountsTable);
            
            // Create transactions table
            String createTransactionsTable = 
                "CREATE TABLE IF NOT EXISTS transactions (" +
                "transaction_id VARCHAR(50) PRIMARY KEY, " +
                "account_number VARCHAR(50) NOT NULL, " +
                "transaction_type VARCHAR(20) NOT NULL, " +
                "amount DECIMAL(15, 2) NOT NULL, " +
                "balance_after DECIMAL(15, 2) NOT NULL, " +
                "description VARCHAR(255), " +
                "transaction_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (account_number) REFERENCES accounts(account_number)" +
                ")";
            stmt.execute(createTransactionsTable);
            
            System.out.println("✓ Database schema initialized (File: banking.mv.db)");
            insertDefaultUser(conn);
            
        } catch (SQLException e) {
            System.err.println("✗ Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void insertDefaultUser(Connection conn) {
        String checkUserSQL = "SELECT COUNT(*) FROM users WHERE user_id = 'admin'";
        String insertUserSQL = 
            "INSERT INTO users (user_id, username, password_hash, role) " +
            "VALUES ('admin', 'Administrator', ?, 'ADMIN')";
        
        try (Statement stmt = conn.createStatement();
             var rs = stmt.executeQuery(checkUserSQL)) {
            
            rs.next();
            if (rs.getInt(1) == 0) {
                try (var pstmt = conn.prepareStatement(insertUserSQL)) {
                    String passwordHash = String.valueOf("admin123".hashCode());
                    pstmt.setString(1, passwordHash);
                    pstmt.executeUpdate();
                    System.out.println("✓ Default admin user created (username: admin, password: admin123)");
                }
            } else {
                System.out.println("✓ Admin user already exists");
            }
        } catch (SQLException e) {
            System.err.println("✗ Error creating default user: " + e.getMessage());
        }
    }
    
    
    /**
     * Saves a customer to the database (PERMANENT STORAGE).
     */
    public boolean saveCustomer(Customer customer) {
        String sql = "MERGE INTO customers (customer_id, first_name, surname, address, phone_number, email) " +
                    "KEY(customer_id) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customer.getCustomerId());
            pstmt.setString(2, customer.getFirstName());
            pstmt.setString(3, customer.getSurname());
            pstmt.setString(4, customer.getAddress());
            pstmt.setString(5, customer.getPhoneNumber());
            pstmt.setString(6, customer.getEmail());
            
            pstmt.executeUpdate();
            System.out.println("✓ Customer saved to disk: " + customer.getCustomerId());
            return true;
            
        } catch (SQLException e) {
            System.err.println("✗ Failed to save customer: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Retrieves a customer by ID from permanent storage.
     */
    public Customer getCustomer(String customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Customer customer = new Customer(
                    rs.getString("customer_id"),
                    rs.getString("first_name"),
                    rs.getString("surname"),
                    rs.getString("address")
                );
                customer.setPhoneNumber(rs.getString("phone_number"));
                customer.setEmail(rs.getString("email"));
                return customer;
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Failed to retrieve customer: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Retrieves all customers from permanent storage.
     */
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY customer_id";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Customer customer = new Customer(
                    rs.getString("customer_id"),
                    rs.getString("first_name"),
                    rs.getString("surname"),
                    rs.getString("address")
                );
                customer.setPhoneNumber(rs.getString("phone_number"));
                customer.setEmail(rs.getString("email"));
                customers.add(customer);
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Failed to retrieve customers: " + e.getMessage());
        }
        return customers;
    }
    
    /**
     * Deletes a customer from permanent storage.
     */
    public boolean deleteCustomer(String customerId) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("✗ Failed to delete customer: " + e.getMessage());
            return false;
        }
    }

    /**
     * Searches customers by first name or surname.
     */
    public List<Customer> searchCustomersByName(String name) {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE LOWER(first_name) LIKE ? OR LOWER(surname) LIKE ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String pattern = "%" + name.toLowerCase() + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Customer c = new Customer(
                    rs.getString("customer_id"),
                    rs.getString("first_name"),
                    rs.getString("surname"),
                    rs.getString("address")
                );
                c.setPhoneNumber(rs.getString("phone_number"));
                c.setEmail(rs.getString("email"));
                list.add(c);
            }

        } catch (SQLException e) {
            System.err.println("✗ Failed to search customers: " + e.getMessage());
        }

        return list;
    }
    
    
    /**
     * Saves an account to permanent storage.
     */
    public boolean saveAccount(Account account) {
        String sql = "MERGE INTO accounts " +
                    "(account_number, customer_id, account_type, balance, branch, " +
                    "company_name, company_address) " +
                    "KEY(account_number) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, account.getAccountNumber());
            pstmt.setString(2, account.getCustomer().getCustomerId());
            pstmt.setString(3, account.getAccountType());
            pstmt.setDouble(4, account.getBalance());
            pstmt.setString(5, account.getBranch());
            
            // Handle ChequeAccount specific fields
            if (account instanceof ChequeAccount) {
                ChequeAccount cheque = (ChequeAccount) account;
                pstmt.setString(6, cheque.getCompanyName());
                pstmt.setString(7, cheque.getCompanyAddress());
            } else {
                pstmt.setString(6, null);
                pstmt.setString(7, null);
            }
            
            pstmt.executeUpdate();
            System.out.println("✓ Account saved to disk: " + account.getAccountNumber() + 
                             " (Balance: BWP " + String.format("%.2f", account.getBalance()) + ")");
            return true;
            
        } catch (SQLException e) {
            System.err.println("✗ Failed to save account: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Retrieves an account by account number from permanent storage.
     */
    public Account getAccount(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String customerId = rs.getString("customer_id");
                Customer customer = getCustomer(customerId);
                
                if (customer == null) {
                    System.err.println("✗ Customer not found for account: " + accountNumber);
                    return null;
                }
                
                return reconstructAccount(rs, customer);
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Failed to retrieve account: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Retrieves all accounts for a specific customer from permanent storage.
     */
    public List<Account> getCustomerAccounts(String customerId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ? ORDER BY account_number";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            Customer customer = getCustomer(customerId);
            if (customer == null) return accounts;
            
            while (rs.next()) {
                Account account = reconstructAccount(rs, customer);
                if (account != null) {
                    accounts.add(account);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Failed to retrieve customer accounts: " + e.getMessage());
        }
        return accounts;
    }
    
    /**
     * Deletes an account from permanent storage.
     */
    public boolean deleteAccount(String accountNumber) {
        String sql = "DELETE FROM accounts WHERE account_number = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("✗ Failed to delete account: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all accounts from permanent storage.
     */
    public List<Account> getAllAccounts() {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Customer customer = getCustomer(rs.getString("customer_id"));
                if (customer != null) {
                    Account account = reconstructAccount(rs, customer);
                    list.add(account);
                }
            }

        } catch (SQLException e) {
            System.err.println("✗ Failed to retrieve all accounts: " + e.getMessage());
        }

        return list;
    }

    /**
     * Counts accounts by type.
     */
    public int countByType(String type) {
        String sql = "SELECT COUNT(*) FROM accounts WHERE account_type = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, type);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            System.err.println("✗ Failed to count accounts: " + e.getMessage());
        }
        return 0;
    }
    
    /**
     * Reconstructs an Account object from database ResultSet.
     */
    private Account reconstructAccount(ResultSet rs, Customer customer) throws SQLException {
        String accountType = rs.getString("account_type");
        String accountNumber = rs.getString("account_number");
        double balance = rs.getDouble("balance");
        String branch = rs.getString("branch");
        
        Account account = null;
        
        try {
            switch (accountType) {
                case "Savings Account":
                    account = new SavingsAccount(accountNumber, 0, branch, customer);
                    break;
                case "Investment Account":
                    account = new InvestmentAccount(accountNumber, balance, branch, customer);
                    break;
                case "Cheque Account":
                    String companyName = rs.getString("company_name");
                    String companyAddress = rs.getString("company_address");
                    account = new ChequeAccount(accountNumber, 0, branch, customer, 
                                               companyName, companyAddress);
                    break;
            }
            
            // Set the actual balance after construction to bypass validation
            if (account != null) {
                account.setBalance(balance);
            }
            
        } catch (Exception e) {
            System.err.println("✗ Error reconstructing account: " + e.getMessage());
        }
        
        return account;
    }
    
    
    /**
     * Saves a transaction to permanent storage.
     */
    public boolean saveTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions " +
                    "(transaction_id, account_number, transaction_type, amount, " +
                    "balance_after, description, transaction_timestamp) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, transaction.getTransactionId());
            pstmt.setString(2, transaction.getAccountNumber());
            pstmt.setString(3, transaction.getTransactionType());
            pstmt.setDouble(4, transaction.getAmount());
            pstmt.setDouble(5, transaction.getBalanceAfter());
            pstmt.setString(6, transaction.getDescription());
            pstmt.setTimestamp(7, Timestamp.valueOf(transaction.getTimestamp()));
            
            pstmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.err.println("✗ Failed to save transaction: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Retrieves transaction history for an account from permanent storage.
     */
    public List<Transaction> getTransactionHistory(String accountNumber) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? " +
                    "ORDER BY transaction_timestamp DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Transaction transaction = new Transaction(
                    rs.getString("transaction_id"),
                    rs.getString("account_number"),
                    rs.getString("transaction_type"),
                    rs.getDouble("amount"),
                    rs.getDouble("balance_after"),
                    rs.getString("description"),
                    rs.getTimestamp("transaction_timestamp").toLocalDateTime()
                );
                transactions.add(transaction);
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Failed to retrieve transactions: " + e.getMessage());
        }
        return transactions;
    }

    /**
     * Retrieves a single transaction by ID.
     */
    public Transaction getTransaction(String id) {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Transaction(
                    rs.getString("transaction_id"),
                    rs.getString("account_number"),
                    rs.getString("transaction_type"),
                    rs.getDouble("amount"),
                    rs.getDouble("balance_after"),
                    rs.getString("description"),
                    rs.getTimestamp("transaction_timestamp").toLocalDateTime()
                );
            }

        } catch (SQLException e) {
            System.err.println("✗ Failed to retrieve transaction: " + e.getMessage());
        }
        return null;
    }

    /**
     * Finds transactions within a specific date range.
     */
    public List<Transaction> findTransactionsByDate(String accountNumber, LocalDateTime start, LocalDateTime end) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? AND transaction_timestamp BETWEEN ? AND ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            pstmt.setTimestamp(2, Timestamp.valueOf(start));
            pstmt.setTimestamp(3, Timestamp.valueOf(end));

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Transaction(
                    rs.getString("transaction_id"),
                    rs.getString("account_number"),
                    rs.getString("transaction_type"),
                    rs.getDouble("amount"),
                    rs.getDouble("balance_after"),
                    rs.getString("description"),
                    rs.getTimestamp("transaction_timestamp").toLocalDateTime()
                ));
            }

        } catch (SQLException e) {
            System.err.println("✗ Failed to search transactions: " + e.getMessage());
        }

        return list;
    }

    /**
     * Deletes all transactions for a given account.
     */
    public boolean deleteTransactions(String accountNumber) {
        String sql = "DELETE FROM transactions WHERE account_number = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("✗ Failed to delete transactions: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all transactions.
     */
    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY transaction_timestamp DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Transaction(
                    rs.getString("transaction_id"),
                    rs.getString("account_number"),
                    rs.getString("transaction_type"),
                    rs.getDouble("amount"),
                    rs.getDouble("balance_after"),
                    rs.getString("description"),
                    rs.getTimestamp("transaction_timestamp").toLocalDateTime()
                ));
            }

        } catch (SQLException e) {
            System.err.println("✗ Failed to retrieve all transactions: " + e.getMessage());
        }

        return list;
    }
    
    
    /**
     * Saves a user to permanent storage.
     */
    public boolean saveUser(User user) {
        String sql = "MERGE INTO users (user_id, username, password_hash, role) " +
                    "KEY(user_id) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, String.valueOf("password".hashCode())); // Placeholder
            pstmt.setString(4, user.getRole());
            
            pstmt.executeUpdate();
            System.out.println("✓ User saved to disk: " + user.getUsername());
            return true;
            
        } catch (SQLException e) {
            System.err.println("✗ Failed to save user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Retrieves a user by username from permanent storage.
     */
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getString("user_id"),
                    rs.getString("username"),
                    rs.getString("password_hash"),
                    rs.getString("role")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Failed to retrieve user: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves a user by user ID.
     */
    public User getUser(String id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                    rs.getString("user_id"),
                    rs.getString("username"),
                    rs.getString("password_hash"),
                    rs.getString("role")
                );
            }

        } catch (SQLException e) {
            System.err.println("✗ Failed to retrieve user: " + e.getMessage());
        }
        return null;
    }

    /**
     * Authenticates a user using ID and password.
     */
    public boolean authenticate(String id, String password) {
        String sql = "SELECT password_hash FROM users WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String stored = rs.getString("password_hash");
                return stored.equals(String.valueOf(password.hashCode()));
            }

        } catch (SQLException e) {
            System.err.println("✗ Authentication failed: " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieves all users from permanent storage.
     */
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY user_id";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new User(
                    rs.getString("user_id"),
                    rs.getString("username"),
                    rs.getString("password_hash"),
                    rs.getString("role")
                ));
            }

        } catch (SQLException e) {
            System.err.println("✗ Failed to retrieve users: " + e.getMessage());
        }

        return list;
    }

    /**
     * Retrieves users filtered by role.
     */
    public List<User> getUsersByRole(String role) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, role);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(new User(
                    rs.getString("user_id"),
                    rs.getString("username"),
                    rs.getString("password_hash"),
                    rs.getString("role")
                ));
            }

        } catch (SQLException e) {
            System.err.println("✗ Failed to retrieve users by role: " + e.getMessage());
        }

        return list;
    }

    /**
     * Updates a user in the database.
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, password_hash = ?, role = ? WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getUserId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("✗ Failed to update user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates only the user's password.
     */
    public boolean updateUserPassword(String id, String newPass) {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, String.valueOf(newPass.hashCode()));
            pstmt.setString(2, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("✗ Failed to update password: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a user by ID.
     */
    public boolean deleteUser(String id) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("✗ Failed to delete user: " + e.getMessage());
            return false;
        }
    }
    
    // Utility methods
    
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            boolean connected = conn != null && !conn.isClosed();
            if (connected) {
                System.out.println("✓ Database connection OK (File: banking.mv.db)");
            }
            return connected;
        } catch (SQLException e) {
            System.err.println("✗ Connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("✗ Error closing connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Gets the database file location for reference.
     */
    public String getDatabaseLocation() {
        return System.getProperty("user.dir") + "/banking.mv.db";
    }
}