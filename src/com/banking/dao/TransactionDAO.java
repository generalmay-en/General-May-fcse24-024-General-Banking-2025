package com.banking.dao;

import com.banking.database.DatabaseManager;
import com.banking.model.Transaction;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    private DatabaseManager dbManager;
    
    public TransactionDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    public boolean save(Transaction transaction) {
        String sql = "INSERT INTO transactions (transaction_id, account_number, transaction_type, " +
                     "amount, balance_after, description, transaction_timestamp) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, transaction.getTransactionId());
            pstmt.setString(2, transaction.getAccountNumber());
            pstmt.setString(3, transaction.getTransactionType());
            pstmt.setDouble(4, transaction.getAmount());
            pstmt.setDouble(5, transaction.getBalanceAfter());
            pstmt.setString(6, transaction.getDescription());
            pstmt.setTimestamp(7, Timestamp.valueOf(transaction.getTimestamp()));
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error saving transaction: " + e.getMessage());
            return false;
        }
    }
    
    public Transaction findById(String transactionId) {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, transactionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return createTransactionFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding transaction: " + e.getMessage());
        }
        
        return null;
    }
    
    public List<Transaction> findByAccountNumber(String accountNumber) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? " +
                     "ORDER BY transaction_timestamp DESC";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(createTransactionFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding transactions: " + e.getMessage());
        }
        
        return transactions;
    }
    
    public List<Transaction> findByAccountAndDateRange(String accountNumber, 
                                                       LocalDateTime startDate, 
                                                       LocalDateTime endDate) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions " +
                     "WHERE account_number = ? " +
                     "AND transaction_timestamp BETWEEN ? AND ? " +
                     "ORDER BY transaction_timestamp DESC";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            pstmt.setTimestamp(2, Timestamp.valueOf(startDate));
            pstmt.setTimestamp(3, Timestamp.valueOf(endDate));
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(createTransactionFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding transactions by date range: " + e.getMessage());
        }
        
        return transactions;
    }
    
    public List<Transaction> findByAccountAndType(String accountNumber, String transactionType) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions " +
                     "WHERE account_number = ? AND transaction_type = ? " +
                     "ORDER BY transaction_timestamp DESC";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            pstmt.setString(2, transactionType);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(createTransactionFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding transactions by type: " + e.getMessage());
        }
        
        return transactions;
    }
    
    public List<Transaction> findRecentTransactions(String accountNumber, int limit) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? " +
                     "ORDER BY transaction_timestamp DESC LIMIT ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            pstmt.setInt(2, limit);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(createTransactionFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding recent transactions: " + e.getMessage());
        }
        
        return transactions;
    }
    
    public double getTotalDeposits(String accountNumber) {
        String sql = "SELECT SUM(amount) FROM transactions " +
                     "WHERE account_number = ? AND transaction_type = 'DEPOSIT'";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error calculating total deposits: " + e.getMessage());
        }
        
        return 0.0;
    }
    
    public double getTotalWithdrawals(String accountNumber) {
        String sql = "SELECT SUM(amount) FROM transactions " +
                     "WHERE account_number = ? AND transaction_type = 'WITHDRAWAL'";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error calculating total withdrawals: " + e.getMessage());
        }
        
        return 0.0;
    }
    
    public int getTransactionCount(String accountNumber) {
        String sql = "SELECT COUNT(*) FROM transactions WHERE account_number = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error counting transactions: " + e.getMessage());
        }
        
        return 0;
    }
    
    public List<Transaction> findAll() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY transaction_timestamp DESC";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                transactions.add(createTransactionFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving all transactions: " + e.getMessage());
        }
        
        return transactions;
    }
    
    private Transaction createTransactionFromResultSet(ResultSet rs) throws SQLException {
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
    
    public boolean deleteByAccountNumber(String accountNumber) {
        String sql = "DELETE FROM transactions WHERE account_number = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting transactions: " + e.getMessage());
            return false;
        }
    }
}
