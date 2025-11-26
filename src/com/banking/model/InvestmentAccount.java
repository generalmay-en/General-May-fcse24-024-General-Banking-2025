package com.banking.model;

/**
 * InvestmentAccount class demonstrates INHERITANCE and POLYMORPHISM.
 * Extends the abstract Account class with specific investment account rules.
 * 
 * Business Rules:
 * - Allows both deposits and withdrawals
 * - Requires minimum opening balance of BWP 500.00
 * - Pays 5% monthly interest
 */
public class InvestmentAccount extends Account {
    // Class constants demonstrate good design practices
    private static final double INTEREST_RATE = 0.05; // 5% monthly
    private static final double MINIMUM_OPENING_BALANCE = 500.00;
    
    /**
     * Constructor for creating an investment account.
     * Validates minimum opening balance requirement.
     * @param accountNumber Unique account identifier
     * @param initialBalance Starting balance
     * @param branch Bank branch code
     * @param customer The customer who owns this account
     * @throws IllegalArgumentException if initial balance is below minimum
     */
    public InvestmentAccount(String accountNumber, double initialBalance, 
                             String branch, Customer customer) {
        super(accountNumber, initialBalance, branch, customer);
        
        // Enforce business rule: minimum opening balance
        if (initialBalance < MINIMUM_OPENING_BALANCE) {
            throw new IllegalArgumentException(
                String.format("Investment Account requires minimum opening balance of BWP %.2f", 
                MINIMUM_OPENING_BALANCE)
            );
        }
    }
    
    /**
     * Overrides the abstract withdraw method.
     * Investment accounts ALLOW withdrawals (unlike Savings accounts).
     * Demonstrates POLYMORPHISM - different behavior than SavingsAccount.
     * @param amount Amount to withdraw
     * @return true if withdrawal successful, false otherwise
     */
    @Override
    public boolean withdraw(double amount) {
        // Validate withdrawal amount
        if (amount <= 0) {
            System.out.println("Withdrawal amount must be positive.");
            return false;
        }
        
        // Check sufficient balance
        if (!hasSufficientBalance(amount)) {
            System.out.println("Insufficient balance for withdrawal.");
            return false;
        }
        
        // Process withdrawal
        balance -= amount;
        recordTransaction("WITHDRAWAL", amount, "Withdrawal from Investment Account");
        
        // Save updated balance to database
        dbManager.saveAccount(this);
        
        return true;
    }
    
    /**
     * Calculates monthly interest for investment account.
     * Implements the abstract method from Account class.
     * Interest = balance * 5%
     * @return The calculated interest amount
     */
    @Override
    public double calculateInterest() {
        return balance * INTEREST_RATE;
    }
    
    /**
     * Returns the account type name.
     * Implements the abstract method from Account class.
     * @return "Investment Account"
     */
    @Override
    public String getAccountType() {
        return "Investment Account";
    }
    
    /**
     * Returns the interest rate for this account type.
     * @return Interest rate as a percentage
     */
    public double getInterestRate() {
        return INTEREST_RATE * 100;
    }
    
    /**
     * Returns the minimum opening balance requirement.
     * @return Minimum balance required
     */
    public static double getMinimumOpeningBalance() {
        return MINIMUM_OPENING_BALANCE;
    }
    
    /**
     * Overrides toString to provide investment-specific information.
     */
    @Override
    public String toString() {
        return String.format("InvestmentAccount[Number=%s, Balance=BWP %.2f, Interest=%.1f%%]",
            accountNumber, balance, getInterestRate());
    }
}