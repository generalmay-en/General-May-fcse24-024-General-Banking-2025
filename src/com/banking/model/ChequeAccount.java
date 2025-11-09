package com.banking.model;

/**
 * ChequeAccount class demonstrates INHERITANCE and POLYMORPHISM.
 * Extends the abstract Account class with specific cheque account rules.
 * 
 * Business Rules:
 * - Allows both deposits and withdrawals
 * - Used for salary crediting
 * - Requires proof of employment (company name and address)
 * - Does NOT earn interest
 */
public class ChequeAccount extends Account {
    // Additional attributes specific to cheque accounts
    private String companyName;
    private String companyAddress;
    
    /**
     * Constructor for creating a cheque account.
     * Requires employment information as per business rules.
     * @param accountNumber Unique account identifier
     * @param initialBalance Starting balance
     * @param branch Bank branch code
     * @param customer The customer who owns this account
     * @param companyName Name of employer
     * @param companyAddress Address of employer
     * @throws IllegalArgumentException if employment details are missing
     */
    public ChequeAccount(String accountNumber, double initialBalance, String branch, 
                         Customer customer, String companyName, String companyAddress) {
        super(accountNumber, initialBalance, branch, customer);
        
        // Enforce business rule: employment details required
        if (companyName == null || companyName.trim().isEmpty() ||
            companyAddress == null || companyAddress.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Cheque Account requires valid employment information (company name and address)"
            );
        }
        
        this.companyName = companyName;
        this.companyAddress = companyAddress;
    }
    
    /**
     * Overrides the abstract withdraw method.
     * Cheque accounts ALLOW withdrawals.
     * Demonstrates POLYMORPHISM - similar to InvestmentAccount but different from SavingsAccount.
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
        recordTransaction("WITHDRAWAL", amount, "Withdrawal from Cheque Account");
        return true;
    }
    
    /**
     * Calculates interest for cheque account.
     * Cheque accounts do NOT earn interest per requirements.
     * Demonstrates POLYMORPHISM - different behavior than other account types.
     * @return 0.0 always, as cheque accounts don't earn interest
     */
    @Override
    public double calculateInterest() {
        return 0.0; // Cheque accounts don't earn interest
    }
    
    /**
     * Returns the account type name.
     * Implements the abstract method from Account class.
     * @return "Cheque Account"
     */
    @Override
    public String getAccountType() {
        return "Cheque Account";
    }
    
    /**
     * Credits salary to the account.
     * Specialized method for cheque accounts (demonstrates additional functionality).
     * @param amount Salary amount to credit
     * @param employerReference Reference from employer
     * @return true if successful
     */
    public boolean creditSalary(double amount, String employerReference) {
        if (amount <= 0) {
            return false;
        }
        balance += amount;
        recordTransaction("SALARY", amount, 
            String.format("Salary credit from %s (Ref: %s)", companyName, employerReference));
        return true;
    }
    
    /**
     * Updates employment information.
     * Allows customer to update their employment details if they change jobs.
     * @param companyName New employer name
     * @param companyAddress New employer address
     */
    public void updateEmploymentInfo(String companyName, String companyAddress) {
        if (companyName != null && !companyName.trim().isEmpty()) {
            this.companyName = companyName;
        }
        if (companyAddress != null && !companyAddress.trim().isEmpty()) {
            this.companyAddress = companyAddress;
        }
    }
    
    // Getters for employment information
    public String getCompanyName() {
        return companyName;
    }
    
    public String getCompanyAddress() {
        return companyAddress;
    }
    
    /**
     * Overrides toString to provide cheque-specific information.
     */
    @Override
    public String toString() {
        return String.format("ChequeAccount[Number=%s, Balance=BWP %.2f, Employer=%s]",
            accountNumber, balance, companyName);
    }
}
