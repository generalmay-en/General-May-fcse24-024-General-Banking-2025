package com.banking.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Transaction class represents a single financial transaction.
 * Demonstrates ENCAPSULATION and maintains an immutable transaction record.
 * Every deposit, withdrawal, and interest application creates a Transaction object.
 */
public class Transaction {
    // All fields are final to make transactions immutable once created
    private final String transactionId;
    private final String accountNumber;
    private final String transactionType; // DEPOSIT, WITHDRAWAL, INTEREST, SALARY
    private final double amount;
    private final double balanceAfter;
    private final String description;
    private final LocalDateTime timestamp;
    
    /**
     * Constructor for creating a new transaction record.
     * Transactions are immutable - once created, they cannot be modified.
     * @param transactionId Unique transaction identifier
     * @param accountNumber Account involved in the transaction
     * @param transactionType Type of transaction
     * @param amount Transaction amount
     * @param balanceAfter Account balance after transaction
     * @param description Human-readable description
     * @param timestamp When the transaction occurred
     */
    public Transaction(String transactionId, String accountNumber, String transactionType,
                      double amount, double balanceAfter, String description, 
                      LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.timestamp = timestamp;
    }
    
    // Getters only - no setters because transactions are immutable
    public String getTransactionId() {
        return transactionId;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public String getTransactionType() {
        return transactionType;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public double getBalanceAfter() {
        return balanceAfter;
    }
    
    public String getDescription() {
        return description;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    /**
     * Returns a formatted timestamp string for display.
     * @return Formatted date and time
     */
    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.format(formatter);
    }
    
    /**
     * Returns a formatted transaction record for display or reporting.
     * @return Formatted string representation
     */
    @Override
    public String toString() {
        return String.format("[%s] %s: BWP %.2f | Balance: BWP %.2f | %s",
            getFormattedTimestamp(), transactionType, amount, balanceAfter, description);
    }
    
    /**
     * Returns a detailed transaction report.
     * Useful for statements and audit trails.
     * @return Detailed transaction information
     */
    public String getDetailedReport() {
        StringBuilder report = new StringBuilder();
        report.append("Transaction ID: ").append(transactionId).append("\n");
        report.append("Account Number: ").append(accountNumber).append("\n");
        report.append("Type: ").append(transactionType).append("\n");
        report.append("Amount: BWP ").append(String.format("%.2f", amount)).append("\n");
        report.append("Balance After: BWP ").append(String.format("%.2f", balanceAfter)).append("\n");
        report.append("Date/Time: ").append(getFormattedTimestamp()).append("\n");
        report.append("Description: ").append(description).append("\n");
        return report.toString();
    }
}
