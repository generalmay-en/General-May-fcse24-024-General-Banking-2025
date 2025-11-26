package com.banking.dao;

import com.banking.database.DatabaseManager;
import com.banking.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TransactionDAO
 * Delegates all SQL operations to DatabaseManager.
 * Acts as a clean, simple layer for controllers.
 */
public class TransactionDAO {

    private final DatabaseManager db;

    public TransactionDAO() {
        this.db = DatabaseManager.getInstance();
    }

    /**
     * Save a transaction.
     */
    public boolean save(Transaction t) {
        return db.saveTransaction(t);
    }

    /**
     * Find by ID.
     */
    public Transaction findById(String id) {
        return db.getTransaction(id);
    }

    /**
     * All transactions for account.
     */
    public List<Transaction> findByAccount(String accountNumber) {
        return db.getTransactionHistory(accountNumber);
    }

    /**
     * Transactions within date range.
     */
    public List<Transaction> findByAccountAndDate(
            String account, LocalDateTime start, LocalDateTime end) {
        return db.findTransactionsByDate(account, start, end);
    }

    /**
     * Delete all transactions for account.
     */
    public boolean deleteByAccount(String accountNumber) {
        return db.deleteTransactions(accountNumber);
    }

    /**
     * All transactions in system.
     */
    public List<Transaction> findAll() {
        return db.getAllTransactions();
    }

    /**
     * Find transactions by account number.
     * Alias for findByAccount method.
     */
    public List<Transaction> findByAccountNumber(String accountNumber) {
        return db.getTransactionHistory(accountNumber);
    }
}