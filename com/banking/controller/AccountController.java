package com.banking.controller;

import com.banking.dao.AccountDAO;
import com.banking.dao.TransactionDAO;
import com.banking.model.*;

import java.util.List;

/**
 * AccountController handles all account and transaction operations.
 * This is the CORE CONTROLLER for banking operations.
 * 
 * Responsibilities:
 * - Opening accounts (F-201, F-204, F-205, F-206)
 * - Deposits (F-301)
 * - Withdrawals (F-302, F-303)
 * - Balance inquiries
 * - Transaction history (F-403)
 * - Monthly interest processing (F-304)
 */
public class AccountController {
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;
    private Bank bank;
    private LoginController loginController;
    
    /**
     * Constructor initializes the controller with dependencies.
     * @param bank The Bank instance managing accounts
     */
    public AccountController(Bank bank) {
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
        this.bank = bank;
        this.loginController = LoginController.getInstance();
    }
    
    /**
     * Opens a new Savings Account for a customer.
     * Satisfies F-201 and F-204 requirements.
     * 
     * Business Rules:
     * - Allows deposits only (enforced in SavingsAccount class)
     * - Pays 0.05% monthly interest
     * 
     * @param customerId Customer who owns the account
     * @param initialBalance Starting balance
     * @param branch Bank branch code
     * @return AccountResult with status and account object
     */
    public AccountResult openSavingsAccount(String customerId, double initialBalance, String branch) {
        // Check permission
        if (!loginController.hasPermission("OPEN_ACCOUNT")) {
            return new AccountResult(false, "You don't have permission to open accounts", null);
        }
        
        // Validate inputs
        if (customerId == null || customerId.trim().isEmpty()) {
            return new AccountResult(false, "Customer ID is required", null);
        }
        
        if (initialBalance < 0) {
            return new AccountResult(false, "Initial balance cannot be negative", null);
        }
        
        if (branch == null || branch.trim().isEmpty()) {
            return new AccountResult(false, "Branch code is required", null);
        }
        
        try {
            // Create account through Bank
            SavingsAccount account = bank.openSavingsAccount(
                customerId.trim(),
                initialBalance,
                branch.trim()
            );
            
            // Save to database
            boolean saved = accountDAO.save(account);
            
            if (saved) {
                // Record the initial deposit as a transaction
                if (initialBalance > 0) {
                    for (Transaction txn : account.getTransactionHistory()) {
                        transactionDAO.save(txn);
                    }
                }
                
                return new AccountResult(true, 
                    "Savings Account opened successfully: " + account.getAccountNumber(), 
                    account);
            } else {
                return new AccountResult(false, "Failed to save account to database", null);
            }
            
        } catch (IllegalArgumentException e) {
            return new AccountResult(false, e.getMessage(), null);
        } catch (Exception e) {
            return new AccountResult(false, "Error opening account: " + e.getMessage(), null);
        }
    }
    
    /**
     * Opens a new Investment Account for a customer.
     * Satisfies F-201 and F-205 requirements.
     * 
     * Business Rules:
     * - Allows deposits and withdrawals
     * - Requires minimum opening balance of BWP 500.00
     * - Pays 5% monthly interest
     * 
     * @param customerId Customer who owns the account
     * @param initialBalance Starting balance (must be >= 500)
     * @param branch Bank branch code
     * @return AccountResult with status and account object
     */
    public AccountResult openInvestmentAccount(String customerId, double initialBalance, String branch) {
        // Check permission
        if (!loginController.hasPermission("OPEN_ACCOUNT")) {
            return new AccountResult(false, "You don't have permission to open accounts", null);
        }
        
        // Validate inputs
        if (customerId == null || customerId.trim().isEmpty()) {
            return new AccountResult(false, "Customer ID is required", null);
        }
        
        if (initialBalance < 500.00) {
            return new AccountResult(false, 
                "Investment Account requires minimum opening balance of BWP 500.00", null);
        }
        
        if (branch == null || branch.trim().isEmpty()) {
            return new AccountResult(false, "Branch code is required", null);
        }
        
        try {
            // Create account through Bank
            InvestmentAccount account = bank.openInvestmentAccount(
                customerId.trim(),
                initialBalance,
                branch.trim()
            );
            
            // Save to database
            boolean saved = accountDAO.save(account);
            
            if (saved) {
                // Record the initial deposit as a transaction
                if (initialBalance > 0) {
                    for (Transaction txn : account.getTransactionHistory()) {
                        transactionDAO.save(txn);
                    }
                }
                
                return new AccountResult(true, 
                    "Investment Account opened successfully: " + account.getAccountNumber(), 
                    account);
            } else {
                return new AccountResult(false, "Failed to save account to database", null);
            }
            
        } catch (IllegalArgumentException e) {
            return new AccountResult(false, e.getMessage(), null);
        } catch (Exception e) {
            return new AccountResult(false, "Error opening account: " + e.getMessage(), null);
        }
    }
    
    /**
     * Opens a new Cheque Account for a customer.
     * Satisfies F-201 and F-206 requirements.
     * 
     * Business Rules:
     * - Allows deposits and withdrawals
     * - Requires proof of employment (company name and address)
     * - Does not earn interest
     * 
     * @param customerId Customer who owns the account
     * @param initialBalance Starting balance
     * @param branch Bank branch code
     * @param companyName Employer's name
     * @param companyAddress Employer's address
     * @return AccountResult with status and account object
     */
    public AccountResult openChequeAccount(String customerId, double initialBalance, String branch,
                                          String companyName, String companyAddress) {
        // Check permission
        if (!loginController.hasPermission("OPEN_ACCOUNT")) {
            return new AccountResult(false, "You don't have permission to open accounts", null);
        }
        
        // Validate inputs
        if (customerId == null || customerId.trim().isEmpty()) {
            return new AccountResult(false, "Customer ID is required", null);
        }
        
        if (initialBalance < 0) {
            return new AccountResult(false, "Initial balance cannot be negative", null);
        }
        
        if (branch == null || branch.trim().isEmpty()) {
            return new AccountResult(false, "Branch code is required", null);
        }
        
        if (companyName == null || companyName.trim().isEmpty()) {
            return new AccountResult(false, "Company name is required for Cheque Account", null);
        }
        
        if (companyAddress == null || companyAddress.trim().isEmpty()) {
            return new AccountResult(false, "Company address is required for Cheque Account", null);
        }
        
        try {
            // Create account through Bank
            ChequeAccount account = bank.openChequeAccount(
                customerId.trim(),
                initialBalance,
                branch.trim(),
                companyName.trim(),
                companyAddress.trim()
            );
            
            // Save to database
            boolean saved = accountDAO.save(account);
            
            if (saved) {
                // Record the initial deposit as a transaction
                if (initialBalance > 0) {
                    for (Transaction txn : account.getTransactionHistory()) {
                        transactionDAO.save(txn);
                    }
                }
                
                return new AccountResult(true, 
                    "Cheque Account opened successfully: " + account.getAccountNumber(), 
                    account);
            } else {
                return new AccountResult(false, "Failed to save account to database", null);
            }
            
        } catch (IllegalArgumentException e) {
            return new AccountResult(false, e.getMessage(), null);
        } catch (Exception e) {
            return new AccountResult(false, "Error opening account: " + e.getMessage(), null);
        }
    }
    
    /**
     * Deposits funds into an account.
     * Satisfies F-301: Allow deposits to any active account.
     * 
     * @param accountNumber The account to deposit into
     * @param amount Amount to deposit
     * @return TransactionResult with status and updated balance
     */
    public TransactionResult deposit(String accountNumber, double amount) {
        // Check permission
        if (!loginController.hasPermission("DEPOSIT")) {
            return new TransactionResult(false, "You don't have permission to make deposits", 0);
        }
        
        // Validate inputs
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            return new TransactionResult(false, "Account number is required", 0);
        }
        
        if (amount <= 0) {
            return new TransactionResult(false, "Deposit amount must be positive", 0);
        }
        
        try {
            // Retrieve account
            Account account = accountDAO.findByAccountNumber(accountNumber.trim());
            
            if (account == null) {
                return new TransactionResult(false, "Account not found: " + accountNumber, 0);
            }
            
            // Perform deposit (business logic in Account class)
            boolean success = account.deposit(amount);
            
            if (success) {
                // Update balance in database
                accountDAO.updateBalance(account);
                
                // Save transaction to database
                List<Transaction> transactions = account.getTransactionHistory();
                if (!transactions.isEmpty()) {
                    Transaction latestTxn = transactions.get(transactions.size() - 1);
                    transactionDAO.save(latestTxn);
                }
                
                return new TransactionResult(true, 
                    String.format("Deposit successful. New balance: BWP %.2f", account.getBalance()),
                    account.getBalance());
            } else {
                return new TransactionResult(false, "Deposit failed", account.getBalance());
            }
            
        } catch (Exception e) {
            return new TransactionResult(false, "Error processing deposit: " + e.getMessage(), 0);
        }
    }
    
    /**
     * Withdraws funds from an account.
     * Satisfies F-302 and F-303: Withdrawals only from Investment and Cheque accounts.
     * 
     * @param accountNumber The account to withdraw from
     * @param amount Amount to withdraw
     * @return TransactionResult with status and updated balance
     */
    public TransactionResult withdraw(String accountNumber, double amount) {
        // Check permission
        if (!loginController.hasPermission("WITHDRAW")) {
            return new TransactionResult(false, "You don't have permission to make withdrawals", 0);
        }
        
        // Validate inputs
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            return new TransactionResult(false, "Account number is required", 0);
        }
        
        if (amount <= 0) {
            return new TransactionResult(false, "Withdrawal amount must be positive", 0);
        }
        
        try {
            // Retrieve account
            Account account = accountDAO.findByAccountNumber(accountNumber.trim());
            
            if (account == null) {
                return new TransactionResult(false, "Account not found: " + accountNumber, 0);
            }
            
            // Check account type - Savings accounts don't allow withdrawals (F-302)
            if (account instanceof SavingsAccount) {
                return new TransactionResult(false, 
                    "Withdrawals are not permitted on Savings Accounts", 
                    account.getBalance());
            }
            
            // Perform withdrawal (business logic in Account class handles balance check)
            boolean success = account.withdraw(amount);
            
            if (success) {
                // Update balance in database
                accountDAO.updateBalance(account);
                
                // Save transaction to database
                List<Transaction> transactions = account.getTransactionHistory();
                if (!transactions.isEmpty()) {
                    Transaction latestTxn = transactions.get(transactions.size() - 1);
                    transactionDAO.save(latestTxn);
                }
                
                return new TransactionResult(true, 
                    String.format("Withdrawal successful. New balance: BWP %.2f", account.getBalance()),
                    account.getBalance());
            } else {
                return new TransactionResult(false, 
                    "Insufficient balance for withdrawal", 
                    account.getBalance());
            }
            
        } catch (Exception e) {
            return new TransactionResult(false, "Error processing withdrawal: " + e.getMessage(), 0);
        }
    }
    
    /**
     * Gets the current balance of an account.
     * @param accountNumber The account to query
     * @return BalanceResult with balance information
     */
    public BalanceResult getBalance(String accountNumber) {
        // Check permission
        if (!loginController.hasPermission("VIEW_BALANCE")) {
            return new BalanceResult(false, "You don't have permission to view balances", 0, null);
        }
        
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            return new BalanceResult(false, "Account number is required", 0, null);
        }
        
        Account account = accountDAO.findByAccountNumber(accountNumber.trim());
        
        if (account != null) {
            return new BalanceResult(true, "Balance retrieved", account.getBalance(), account);
        } else {
            return new BalanceResult(false, "Account not found", 0, null);
        }
    }
    
    /**
     * Retrieves transaction history for an account.
     * Satisfies F-403: Display comprehensive transaction history.
     * 
     * @param accountNumber The account to query
     * @return List of transactions
     */
    public List<Transaction> getTransactionHistory(String accountNumber) {
        // Check permission
        if (!loginController.hasPermission("VIEW_TRANSACTIONS")) {
            return List.of();
        }
        
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            return List.of();
        }
        
        return transactionDAO.findByAccountNumber(accountNumber.trim());
    }
    
    /**
     * Processes monthly interest for all eligible accounts.
     * Satisfies F-304: Automatically calculate and apply interest monthly.
     * 
     * This should be called automatically at month-end or manually by managers.
     * @return InterestResult with statistics
     */
    public InterestResult processMonthlyInterest() {
        // Check permission (only managers/admins should process interest)
        if (!loginController.hasPermission("OVERRIDE_LIMIT")) {
            return new InterestResult(false, "You don't have permission to process interest", 0, 0);
        }
        
        try {
            List<Account> allAccounts = accountDAO.findAll();
            int accountsProcessed = 0;
            double totalInterestPaid = 0.0;
            
            for (Account account : allAccounts) {
                // Only Savings and Investment accounts earn interest
                if (account instanceof SavingsAccount || account instanceof InvestmentAccount) {
                    double interestAmount = account.calculateInterest();
                    
                    if (interestAmount > 0) {
                        account.applyInterest();
                        accountDAO.updateBalance(account);
                        
                        // Save interest transaction
                        List<Transaction> transactions = account.getTransactionHistory();
                        if (!transactions.isEmpty()) {
                            Transaction latestTxn = transactions.get(transactions.size() - 1);
                            transactionDAO.save(latestTxn);
                        }
                        
                        accountsProcessed++;
                        totalInterestPaid += interestAmount;
                    }
                }
            }
            
            return new InterestResult(true, 
                String.format("Interest processed for %d accounts. Total interest: BWP %.2f", 
                    accountsProcessed, totalInterestPaid),
                accountsProcessed, 
                totalInterestPaid);
                
        } catch (Exception e) {
            return new InterestResult(false, 
                "Error processing interest: " + e.getMessage(), 0, 0);
        }
    }
    
    /**
     * Retrieves all accounts for a specific customer.
     * @param customerId The customer ID
     * @return List of accounts
     */
    public List<Account> getCustomerAccounts(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            return List.of();
        }
        return accountDAO.findByCustomerId(customerId.trim());
    }
    
    /**
     * Gets account statistics by type.
     * Useful for dashboard reporting.
     * @return AccountStatistics object
     */
    public AccountStatistics getAccountStatistics() {
        int savingsCount = accountDAO.countByType("Savings Account");
        int investmentCount = accountDAO.countByType("Investment Account");
        int chequeCount = accountDAO.countByType("Cheque Account");
        int totalCount = savingsCount + investmentCount + chequeCount;
        
        return new AccountStatistics(savingsCount, investmentCount, chequeCount, totalCount);
    }
    
    // Result classes for returning operation outcomes
    
    public static class AccountResult {
        private final boolean success;
        private final String message;
        private final Account account;
        
        public AccountResult(boolean success, String message, Account account) {
            this.success = success;
            this.message = message;
            this.account = account;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Account getAccount() { return account; }
    }
    
    public static class TransactionResult {
        private final boolean success;
        private final String message;
        private final double newBalance;
        
        public TransactionResult(boolean success, String message, double newBalance) {
            this.success = success;
            this.message = message;
            this.newBalance = newBalance;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public double getNewBalance() { return newBalance; }
    }
    
    public static class BalanceResult {
        private final boolean success;
        private final String message;
        private final double balance;
        private final Account account;
        
        public BalanceResult(boolean success, String message, double balance, Account account) {
            this.success = success;
            this.message = message;
            this.balance = balance;
            this.account = account;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public double getBalance() { return balance; }
        public Account getAccount() { return account; }
    }
    
    public static class InterestResult {
        private final boolean success;
        private final String message;
        private final int accountsProcessed;
        private final double totalInterest;
        
        public InterestResult(boolean success, String message, int accountsProcessed, double totalInterest) {
            this.success = success;
            this.message = message;
            this.accountsProcessed = accountsProcessed;
            this.totalInterest = totalInterest;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public int getAccountsProcessed() { return accountsProcessed; }
        public double getTotalInterest() { return totalInterest; }
    }
    
    public static class AccountStatistics {
        private final int savingsCount;
        private final int investmentCount;
        private final int chequeCount;
        private final int totalCount;
        
        public AccountStatistics(int savingsCount, int investmentCount, int chequeCount, int totalCount) {
            this.savingsCount = savingsCount;
            this.investmentCount = investmentCount;
            this.chequeCount = chequeCount;
            this.totalCount = totalCount;
        }
        
        public int getSavingsCount() { return savingsCount; }
        public int getInvestmentCount() { return investmentCount; }
        public int getChequeCount() { return chequeCount; }
        public int getTotalCount() { return totalCount; }
    }
}