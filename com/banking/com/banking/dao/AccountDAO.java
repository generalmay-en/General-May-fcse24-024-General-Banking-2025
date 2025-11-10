package com.banking.dao;

import com.banking.database.DatabaseManager;
import com.banking.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AccountDAO handles all database operations for Account entities.
 * This is more complex than CustomerDAO because it must handle three different account types.
 * Demonstrates polymorphism at the persistence layer.
 */
public class AccountDAO {
    private DatabaseManager dbManager;
    private CustomerDAO customerDAO;
    
    public AccountDAO() {
        this.dbManager = DatabaseManager.getInstance();
        this.customerDAO = new CustomerDAO();
    }
    
    /**
     * Saves a new account to the database (CREATE operation).
     * Handles all three account types: Savings, Investment, Cheque
     * @param account The account object to save
     * @return true if save successful
     */
    public boolean save(Account account) {
        String sql = "INSERT INTO accounts (account_number, customer_id, account_type, balance, branch, " +
                     "company_name, company_address) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, account.getAccountNumber());
            pstmt.setString(2, account.getCustomer().getCustomerId());
            pstmt.setString(3, account.getAccountType());
            pstmt.setDouble(4, account.getBalance());
            pstmt.setString(5, account.getBranch());
            
            // Handle Cheque account specific fields
            if (account instanceof ChequeAccount) {
                ChequeAccount chequeAccount = (ChequeAccount) account;
                pstmt.setString(6, chequeAccount.getCompanyName());
                pstmt.setString(7, chequeAccount.getCompanyAddress());
            } else {
                pstmt.setNull(6, Types.VARCHAR);
                pstmt.setNull(7, Types.VARCHAR);
            }
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error saving account: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Retrieves an account by account number (READ operation).
     * Factory method that creates the correct account type based on database data.
     * @param accountNumber The account number to search for
     * @return Account object of the correct type, or null if not found
     */
    public Account findByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return createAccountFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding account: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Retrieves all accounts for a specific customer (READ operation).
     * @param customerId The customer's ID
     * @return List of all accounts belonging to the customer
     */
    public List<Account> findByCustomerId(String customerId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ? ORDER BY date_opened";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Account account = createAccountFromResultSet(rs);
                if (account != null) {
                    accounts.add(account);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding accounts for customer: " + e.getMessage());
        }
        
        return accounts;
    }
    
    /**
     * Updates an account's balance in the database (UPDATE operation).
     * This is called after deposits, withdrawals, and interest application.
     * @param account The account with updated balance
     * @return true if update successful
     */
    public boolean updateBalance(Account account) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, account.getBalance());
            pstmt.setString(2, account.getAccountNumber());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating account balance: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Deletes an account from the database (DELETE operation).
     * @param accountNumber The account number to delete
     * @return true if deletion successful
     */
    public boolean delete(String accountNumber) {
        String sql = "DELETE FROM accounts WHERE account_number = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting account: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Retrieves all accounts in the system (READ operation).
     * @return List of all accounts
     */
    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts ORDER BY account_number";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Account account = createAccountFromResultSet(rs);
                if (account != null) {
                    accounts.add(account);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving all accounts: " + e.getMessage());
        }
        
        return accounts;
    }
    
    /**
     * Factory method that creates the appropriate Account object from database row.
     * This demonstrates polymorphism - we read generic data and create specific types.
     * @param rs ResultSet positioned at a valid row
     * @return Account object of the correct type
     * @throws SQLException if database access error occurs
     */
    private Account createAccountFromResultSet(ResultSet rs) throws SQLException {
        String accountNumber = rs.getString("account_number");
        String customerId = rs.getString("customer_id");
        String accountType = rs.getString("account_type");
        double balance = rs.getDouble("balance");
        String branch = rs.getString("branch");
        
        // Retrieve the customer object
        Customer customer = customerDAO.findById(customerId);
        if (customer == null) {
            System.err.println("Customer not found for account: " + accountNumber);
            return null;
        }
        
        // Create the appropriate account type based on accountType field
        Account account = null;
        
        switch (accountType) {
            case "Savings Account":
                account = new SavingsAccount(accountNumber, balance, branch, customer);
                break;
                
            case "Investment Account":
                account = new InvestmentAccount(accountNumber, balance, branch, customer);
                break;
                
            case "Cheque Account":
                String companyName = rs.getString("company_name");
                String companyAddress = rs.getString("company_address");
                account = new ChequeAccount(accountNumber, balance, branch, customer, 
                                          companyName, companyAddress);
                break;
                
            default:
                System.err.println("Unknown account type: " + accountType);
                return null;
        }
        
        return account;
    }
    
    /**
     * Checks if an account exists.
     * @param accountNumber The account number to check
     * @return true if account exists
     */
    public boolean exists(String accountNumber) {
        String sql = "SELECT COUNT(*) FROM accounts WHERE account_number = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking account existence: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Gets the total count of accounts by type.
     * Useful for reporting and statistics.
     * @param accountType The type of account to count
     * @return Number of accounts of that type
     */
    public int countByType(String accountType) {
        String sql = "SELECT COUNT(*) FROM accounts WHERE account_type = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountType);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error counting accounts: " + e.getMessage());
        }
        
        return 0;
    }
}