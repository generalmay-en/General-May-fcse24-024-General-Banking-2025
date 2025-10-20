package com.banking.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bank class serves as the central management system.
 * Demonstrates COMPOSITION - a Bank contains many Customers.
 * This class manages the relationship between customers and accounts.
 */
public class Bank {
    private String bankName;
    private String bankCode;
    
    // Uses composition to manage customers
    private Map<String, Customer> customers; // Key: customerId, Value: Customer object
    private Map<String, Account> accounts; // Key: accountNumber, Value: Account object
    
    // Counters for generating unique IDs
    private int customerCounter;
    private int accountCounter;
    
    /**
     * Constructor for creating a Bank instance.
     * @param bankName Name of the bank
     * @param bankCode Unique bank identification code
     */
    public Bank(String bankName, String bankCode) {
        this.bankName = bankName;
        this.bankCode = bankCode;
        this.customers = new HashMap<>();
        this.accounts = new HashMap<>();
        this.customerCounter = 1000; // Start customer IDs from 1000
        this.accountCounter = 10000; // Start account numbers from 10000
    }
    
    /**
     * Registers a new customer with the bank.
     * Generates a unique customer ID automatically.
     * @param firstName Customer's first name
     * @param surname Customer's surname
     * @param address Customer's address
     * @return The newly created Customer object
     */
    public Customer registerCustomer(String firstName, String surname, String address) {
        String customerId = generateCustomerId();
        Customer customer = new Customer(customerId, firstName, surname, address);
        customers.put(customerId, customer);
        return customer;
    }
    
    /**
     * Opens a new Savings Account for a customer.
     * @param customerId ID of the customer
     * @param initialBalance Starting balance
     * @param branch Branch code
     * @return The newly created SavingsAccount
     * @throws IllegalArgumentException if customer not found
     */
    public SavingsAccount openSavingsAccount(String customerId, double initialBalance, String branch) {
        Customer customer = getCustomer(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found: " + customerId);
        }
        
        String accountNumber = generateAccountNumber();
        SavingsAccount account = new SavingsAccount(accountNumber, initialBalance, branch, customer);
        customer.addAccount(account);
        accounts.put(accountNumber, account);
        return account;
    }
    
    /**
     * Opens a new Investment Account for a customer.
     * @param customerId ID of the customer
     * @param initialBalance Starting balance (must be >= BWP 500)
     * @param branch Branch code
     * @return The newly created InvestmentAccount
     * @throws IllegalArgumentException if customer not found or balance too low
     */
    public InvestmentAccount openInvestmentAccount(String customerId, double initialBalance, String branch) {
        Customer customer = getCustomer(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found: " + customerId);
        }
        
        String accountNumber = generateAccountNumber();
        InvestmentAccount account = new InvestmentAccount(accountNumber, initialBalance, branch, customer);
        customer.addAccount(account);
        accounts.put(accountNumber, account);
        return account;
    }
    
    /**
     * Opens a new Cheque Account for a customer.
     * @param customerId ID of the customer
     * @param initialBalance Starting balance
     * @param branch Branch code
     * @param companyName Employer's name
     * @param companyAddress Employer's address
     * @return The newly created ChequeAccount
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
        customer.addAccount(account);
        accounts.put(accountNumber, account);
        return account;
    }
    
    /**
     * Processes monthly interest for all eligible accounts.
     * This should be called automatically at the end of each month.
     * Demonstrates the automated interest calculation requirement.
     */
    public void processMonthlyInterest() {
        for (Account account : accounts.values()) {
            // Only Savings and Investment accounts earn interest
            if (account instanceof SavingsAccount || account instanceof InvestmentAccount) {
                account.applyInterest();
                System.out.println("Interest applied to account: " + account.getAccountNumber());
            }
        }
    }
    
    /**
     * Retrieves a customer by their ID.
     * @param customerId The customer ID to search for
     * @return The Customer object, or null if not found
     */
    public Customer getCustomer(String customerId) {
        return customers.get(customerId);
    }
    
    /**
     * Retrieves an account by its account number.
     * @param accountNumber The account number to search for
     * @return The Account object, or null if not found
     */
    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }
    
    /**
     * Returns all customers registered with the bank.
     * @return List of all customers
     */
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }
    
    /**
     * Returns all accounts in the bank.
     * @return List of all accounts
     */
    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
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
     * @return Customer count
     */
    public int getCustomerCount() {
        return customers.size();
    }
    
    /**
     * Returns total number of accounts.
     * @return Account count
     */
    public int getAccountCount() {
        return accounts.size();
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
            bankName, bankCode, customers.size(), accounts.size());
    }
}
