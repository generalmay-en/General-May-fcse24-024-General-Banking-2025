package com.banking.model;

import com.banking.database.DatabaseManager;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Abstract Account class demonstrates ABSTRACTION.
 * Defines the common structure and behavior for all account types.
 * Cannot be instantiated directly - must be extended by concrete classes.
 * 
 */
public abstract class Account {
    // Protected allows subclasses to access these fields directly
    protected String accountNumber;
    protected double balance;
    protected String branch;
    protected LocalDateTime dateOpened;
    protected Customer customer; // An account cannot exist without a customer
    
    // Database manager for persistence
    protected DatabaseManager dbManager;
    
    /**
     * Constructor for creating a new account.
     * Protected because only subclasses can call it.
     * @param accountNumber Unique account identifier
     * @param initialBalance Starting balance
     * @param branch Bank branch code
     * @param customer The customer who owns this account
     */
    protected Account(String accountNumber, double initialBalance, String branch, Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Account cannot exist without a customer");
        }
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.branch = branch;
        this.customer = customer;
        this.dateOpened = LocalDateTime.now();
        
        // Initialize database manager for persistence
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Deposits money into the account.
     * This is a concrete method available to all account types.
     * @param amount Amount to deposit
     * @return true if successful, false otherwise
     */
    public boolean deposit(double amount) {
        if (amount <= 0) {
            return false;
        }
        balance += amount;
        recordTransaction("DEPOSIT", amount, "Deposit to account");
        
        // Save updated balance to database
        dbManager.saveAccount(this);
        
        return true;
    }
    
    /**
     * Abstract method for withdrawing money.
     * Different account types have different withdrawal rules.
     * Forces all subclasses to implement this method.
     * NOTE: Subclasses must call dbManager.saveAccount(this) after withdrawal!
     * @param amount Amount to withdraw
     * @return true if successful, false otherwise
     */
    public abstract boolean withdraw(double amount);
    
    /**
     * Abstract method for calculating interest.
     * Each account type has its own interest calculation logic.
     * Demonstrates POLYMORPHISM - each subclass provides its own implementation.
     * @return The calculated interest amount
     */
    public abstract double calculateInterest();
    
    /**
     * Applies the calculated interest to the account balance.
     * This is the automated monthly process mentioned in requirements.
     */
    public void applyInterest() {
        double interest = calculateInterest();
        if (interest > 0) {
            balance += interest;
            recordTransaction("INTEREST", interest, "Monthly interest applied");
            
            // Save updated balance to database
            dbManager.saveAccount(this);
        }
    }
    
    /**
     * Records a transaction in the account's history.
     * Internal method to maintain transaction log.
     * Saves transaction to database instead of ArrayList.
     * @param type Type of transaction
     * @param amount Transaction amount
     * @param description Transaction description
     */
    protected void recordTransaction(String type, double amount, String description) {
        Transaction transaction = new Transaction(
            generateTransactionId(),
            this.accountNumber,
            type,
            amount,
            balance,
            description,
            LocalDateTime.now()
        );
        
        dbManager.saveTransaction(transaction);
    }
    
    /**
     * Generates a unique transaction ID.
     * Simple implementation using timestamp and account number.
     * @return Unique transaction ID
     */
    private String generateTransactionId() {
        // Safe substring to avoid index out of bounds
        int startIndex = Math.max(0, accountNumber.length() - 3);
        return "TXN" + System.currentTimeMillis() + accountNumber.substring(startIndex);
    }
    
    /**
     * Retrieves the transaction history for this account.
     * @return List of transactions from database
     */
    public List<Transaction> getTransactionHistory() {
        // NEW: Load transaction history from database
        return dbManager.getTransactionHistory(accountNumber);
    }
    
    /**
     * Abstract method to get account type name.
     * Used for display and reporting purposes.
     * @return String representing the account type
     */
    public abstract String getAccountType();
    
    // Getters and Setters
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public double getBalance() {
        return balance;
    }
    
    /**
     * Sets the balance directly.
     * Used when loading accounts from database.
     * @param balance New balance value
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    public String getBranch() {
        return branch;
    }
    
    public void setBranch(String branch) {
        this.branch = branch;
    }
    
    public LocalDateTime getDateOpened() {
        return dateOpened;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    /**
     * Checks if the account has sufficient balance for a transaction.
     * @param amount Amount to check
     * @return true if sufficient balance exists
     */
    protected boolean hasSufficientBalance(double amount) {
        return balance >= amount;
    }
    
    @Override
    public String toString() {
        return String.format("%s[Number=%s, Balance=BWP %.2f, Customer=%s]",
            getAccountType(), accountNumber, balance, 
            customer.getFirstName() + " " + customer.getSurname());
    }
}