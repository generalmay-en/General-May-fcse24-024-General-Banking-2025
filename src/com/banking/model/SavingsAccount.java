package com.banking.model;

/**
 * SavingsAccount class demonstrates INHERITANCE and POLYMORPHISM. It extends the abstract Account class with specific savings account rules.
 * 
 * Business Rules:
 * - Allows deposits only (no withdrawals)
 * - Pays 0.05% monthly interest
 */
public class SavingsAccount extends Account {
    // Class constant for interest rate.
    private static final double INTEREST_RATE = 0.0005; // 0.05% monthly
    
    /**
     * Constructor for creating a savings account.
     * Calls the parent constructor using super().
     * @param accountNumber Unique account identifier
     * @param initialBalance Starting balance
     * @param branch Bank branch code
     * @param customer The customer who owns this account
     */
    public SavingsAccount(String accountNumber, double initialBalance, 
                          String branch, Customer customer) {
        super(accountNumber, initialBalance, branch, customer);
    }
    
    /**
     * Overrides the abstract withdraw method.
     * Savings accounts do NOT allow withdrawals per requirements.
     * This demonstrates POLYMORPHISM - different behavior than other account types.
     * @param amount Amount requested for withdrawal
     * @return false always, as withdrawals are not permitted
     */
    @Override
    public boolean withdraw(double amount) {
        // Savings accounts don't allow withdrawals
        System.out.println("Withdrawals are not permitted on Savings Accounts.");
        return false;
    }
    
    /**
     * Calculates monthly interest for savings account.
     * Implements the abstract method from Account class.
     * Interest = balance * 0.05%
     * @return The calculated interest amount
     */
    @Override
    public double calculateInterest() {
        return balance * INTEREST_RATE;
    }
    
    /**
     * Returns the account type name.
     * Implements the abstract method from Account class.
     * @return "Savings Account"
     */
    @Override
    public String getAccountType() {
        return "Savings Account";
    }
    
    /**
     * Returns the interest rate for this account type.
     * Useful for display and reporting.
     * @return Interest rate as a percentage
     */
    public double getInterestRate() {
        return INTEREST_RATE * 100; // Return as percentage
    }
    
    /**
     * Overrides toString to provide savings-specific information.
     * Demonstrates method overriding.
     */
    @Override
    public String toString() {
        return String.format("SavingsAccount[Number=%s, Balance=BWP %.2f, Interest=%.3f%%]",
            accountNumber, balance, getInterestRate());
    }
}
