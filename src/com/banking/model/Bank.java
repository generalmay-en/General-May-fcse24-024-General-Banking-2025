package com.banking.model;

import com.banking.database.DatabaseManager;
import java.util.List;

/**
 * Bank class serves as the central management system.
 * Demonstrates COMPOSITION - a Bank contains many Customers.
 * This class manages the relationship between customers and accounts.
 * All customer and account data persists across application restarts.
 */
public class Bank {
    private String bankName;
    private String bankCode;
    
    // Database manager.
    private DatabaseManager dbManager;
    
    // private Map<String, Customer> customers;
    // private Map<String, Account> accounts;
    
    // Counters for generating unique IDs
    private int customerCounter;
    private int accountCounter;
    
    /**
     * Constructor for creating a Bank instance.
     * Loads existing data from database on startup.
     * @param bankName Name of the bank
     * @param bankCode Unique bank identification code
     */
    public Bank(String bankName, String bankCode) {
        this.bankName = bankName;
        this.bankCode = bankCode;
        
        // Initialize database manager
        this.dbManager = DatabaseManager.getInstance();
        
        // Load existing customers from database to set counter
        List<Customer> existingCustomers = dbManager.getAllCustomers();
        this.customerCounter = 1000 + existingCustomers.size();
        
        // Count existing accounts to set counter
        int totalAccounts = 0;
        for (Customer c : existingCustomers) {
            totalAccounts += dbManager.getCustomerAccounts(c.getCustomerId()).size();
        }
        this.accountCounter = 10000 + totalAccounts;
        
        // Display startup information
        System.out.println("✓ Bank initialized with H2 database persistence");
        System.out.println("  📊 Existing customers loaded: " + existingCustomers.size());
        System.out.println("  💰 Existing accounts loaded: " + totalAccounts);
    }
    
    /**
     * Registers a new customer with the bank.
     * Generates a unique customer ID automatically.
     * Saves customer to database immediately.
     * @param firstName Customer's first name
     * @param surname Customer's surname
     * @param address Customer's address
     * @return The newly created Customer object, or null if save failed
     */
    public Customer registerCustomer(String firstName, String surname, String address) {
        String customerId = generateCustomerId();
        Customer customer = new Customer(customerId, firstName, surname, address);
        
        if (dbManager.saveCustomer(customer)) {
            return customer;
        } else {
            System.err.println("❌ Failed to register customer in database");
            return null;
        }
    }
    
    /**
     * Opens a new Savings Account for a customer.
     * @param customerId ID of the customer
     * @param initialBalance Starting balance
     * @param branch Branch code
     * @return The newly created SavingsAccount, or null if save failed
     * @throws IllegalArgumentException if customer not found
     */
    public SavingsAccount openSavingsAccount(String customerId, double initialBalance, String branch) {
        Customer customer = getCustomer(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found: " + customerId);
        }
        
        String accountNumber = generateAccountNumber();
        SavingsAccount account = new SavingsAccount(accountNumber, initialBalance, branch, customer);
        
        if (dbManager.saveAccount(account)) {
            customer.addAccount(account);
            return account;
        } else {
            System.err.println("❌ Failed to open savings account in database");
            return null;
        }
    }
    
    /**
     * Opens a new Investment Account for a customer.
     * @param customerId ID of the customer
     * @param initialBalance Starting balance (must be >= BWP 500)
     * @param branch Branch code
     * @return The newly created InvestmentAccount, or null if save failed
     * @throws IllegalArgumentException if customer not found or balance too low
     */
    public InvestmentAccount openInvestmentAccount(String customerId, double initialBalance, String branch) {
        Customer customer = getCustomer(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found: " + customerId);
        }
        
        String accountNumber = generateAccountNumber();
        InvestmentAccount account = new InvestmentAccount(accountNumber, initialBalance, branch, customer);
        
        if (dbManager.saveAccount(account)) {
            customer.addAccount(account);
            return account;
        } else {
            System.err.println("❌ Failed to open investment account in database");
            return null;
        }
    }
    
    /**
     * Opens a new Cheque Account for a customer.
     * @param customerId ID of the customer
     * @param initialBalance Starting balance
     * @param branch Branch code
     * @param companyName Employer's name
     * @param companyAddress Employer's address
     * @return The newly created ChequeAccount, or null if save failed
     * @throws IllegalArgumentException if customer not found or employment info invalid
     */
    public ChequeAccount openChequeAccount(String customerId, double initialBalance, String branch,
                                           String companyName, String companyAddress) {
        Customer customer = getCustomer(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found: " + customerId);
        }
        
        String accountNumber = generateAccountNumber();
        ChequeAccount account = new ChequeAccount(accountNumber, initialBalance, branch, 
                                                   customer, companyName, companyAddress);
        
        if (dbManager.saveAccount(account)) {
            customer.addAccount(account);
            return account;
        } else {
            System.err.println("❌ Failed to open cheque account in database");
            return null;
        }
    }
    
    /**
     * Processes monthly interest for all eligible accounts.
     * This should be called automatically at the end of each month.
     * Demonstrates the automated interest calculation requirement.
     */
    public void processMonthlyInterest() {
        List<Customer> customers = getAllCustomers();
        int accountsProcessed = 0;
        
        for (Customer customer : customers) {
            List<Account> accounts = dbManager.getCustomerAccounts(customer.getCustomerId());
            
            for (Account account : accounts) {
                // Only Savings and Investment accounts earn interest
                if (account instanceof SavingsAccount || account instanceof InvestmentAccount) {
                    double oldBalance = account.getBalance();
                    account.applyInterest(); // This calls dbManager.saveAccount() internally
                    double newBalance = account.getBalance();
                    
                    System.out.println(String.format(
                        "✓ Interest applied: %s - BWP %.2f → BWP %.2f",
                        account.getAccountNumber(), oldBalance, newBalance
                    ));
                    accountsProcessed++;
                }
            }
        }
        
        System.out.println("✅ Monthly interest processed for " + accountsProcessed + " accounts");
    }
    
    /**
     * Retrieves a customer by their ID.
     * @param customerId The customer ID to search for
     * @return The Customer object, or null if not found
     */
    public Customer getCustomer(String customerId) {
        // Load customer from database
        Customer customer = dbManager.getCustomer(customerId);
        
        // Load customer's accounts and add them to customer object
        if (customer != null) {
            List<Account> accounts = dbManager.getCustomerAccounts(customerId);
            for (Account account : accounts) {
                customer.addAccount(account);
            }
        }
        
        return customer;
    }
    
    /**
     * Retrieves an account by its account number.
     * @param accountNumber The account number to search for
     * @return The Account object, or null if not found
     */
    public Account getAccount(String accountNumber) {
        // Load account from database
        return dbManager.getAccount(accountNumber);
    }
    
    /**
     * Returns all customers registered with the bank.
     * @return List of all customers from database
     */
    public List<Customer> getAllCustomers() {
        return dbManager.getAllCustomers();
    }
    
    /**
     * Returns all accounts in the bank.
     * @return List of all accounts from database
     */
    public List<Account> getAllAccounts() {
        List<Account> allAccounts = new java.util.ArrayList<>();
        
        List<Customer> customers = getAllCustomers();
        
        for (Customer customer : customers) {
            List<Account> customerAccounts = dbManager.getCustomerAccounts(customer.getCustomerId());
            allAccounts.addAll(customerAccounts);
        }
        
        return allAccounts;
    }
    
    /**
     * Generates a unique customer ID.
     * Format: CUST-xxxx where xxxx is an incrementing number
     * @return Unique customer ID
     */
    private String generateCustomerId() {
        return String.format("CUST-%04d", customerCounter++);
    }
    
    /**
     * Generates a unique account number.
     * Format: bankCode-xxxxx where xxxxx is an incrementing number
     * @return Unique account number
     */
    private String generateAccountNumber() {
        return String.format("%s-%05d", bankCode, accountCounter++);
    }
    
    /**
     * Returns total number of customers.
     * @return Customer count from database
     */
    public int getCustomerCount() {
        return getAllCustomers().size();
    }
    
    /**
     * Returns total number of accounts.
     * @return Account count from database
     */
    public int getAccountCount() {
        return getAllAccounts().size();
    }
    
    // Getters
    public String getBankName() {
        return bankName;
    }
    
    public String getBankCode() {
        return bankCode;
    }
    
    @Override
    public String toString() {
        return String.format("Bank[Name=%s, Code=%s, Customers=%d, Accounts=%d]",
            bankName, bankCode, getCustomerCount(), getAccountCount());
    }
}