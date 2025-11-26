package com.banking.dao;

import com.banking.database.DatabaseManager;
import com.banking.model.Account;

import java.util.List;

/**
 * AccountDAO
 * Provides access to Account records using delegation.
 */
public class AccountDAO {

    private final DatabaseManager db;

    public AccountDAO() {
        this.db = DatabaseManager.getInstance();
    }

    /**
     * Saves any account type.
     * (Savings, Investment, Cheque)
     */
    public boolean save(Account account) {
        return db.saveAccount(account);
    }

    /**
     * Finds an account by its account number.
     */
    public Account findByAccountNumber(String accountNumber) {
        return db.getAccount(accountNumber);
    }

    /**
     * Returns all accounts for a customer.
     */
    public List<Account> findByCustomerId(String customerId) {
        return db.getCustomerAccounts(customerId);
    }

    /**
     * Updates account balance after deposit/withdraw.
     */
    public boolean updateBalance(Account account) {
        return db.saveAccount(account);
    }

    /**
     * Deletes an account.
     */
    public boolean delete(String accountNumber) {
        return db.deleteAccount(accountNumber);
    }

    /**
     * Returns all accounts in the bank.
     */
    public List<Account> findAll() {
        return db.getAllAccounts();
    }

    /**
     * Checks if an account exists.
     */
    public boolean exists(String accountNumber) {
        return db.getAccount(accountNumber) != null;
    }

    /**
     * Counts accounts by type.
     */
    public int countByType(String accountType) {
        return db.countByType(accountType);
    }
}