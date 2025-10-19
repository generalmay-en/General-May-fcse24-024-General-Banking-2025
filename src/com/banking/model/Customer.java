package com.banking.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Customer class represents a bank customer entity.
 * Demonstrates ENCAPSULATION - all fields are private with public accessors.
 * A customer can hold multiple accounts of different types.
 */
public class Customer {
    // Private fields demonstrate encapsulation
    private String customerId;
    private String firstName;
    private String surname;
    private String address;
    private String phoneNumber;
    private String email;
    
    // Composition: A Customer owns multiple Accounts
    private List<Account> accounts;
    
    /**
     * Constructor for creating a new customer.
     * @param customerId Unique identifier for the customer
     * @param firstName Customer's first name
     * @param surname Customer's surname
     * @param address Customer's physical address
     */
    public Customer(String customerId, String firstName, String surname, String address) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.surname = surname;
        this.address = address;
        this.accounts = new ArrayList<>();
    }
    
    /**
     * Adds an account to this customer's portfolio.
     * Enforces the business rule that an account cannot exist without a customer.
     * @param account The account to add
     */
    public void addAccount(Account account) {
        if (account != null && !accounts.contains(account)) {
            accounts.add(account);
            account.setCustomer(this); // Maintain bidirectional relationship
        }
    }
    
    /**
     * Retrieves all accounts belonging to this customer.
     * @return List of customer's accounts
     */
    public List<Account> getAccounts() {
        return new ArrayList<>(accounts); // Return copy to protect internal state
    }
    
    /**
     * Finds a specific account by account number.
     * @param accountNumber The account number to search for
     * @return The account if found, null otherwise
     */
    public Account getAccountByNumber(String accountNumber) {
        for (Account account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }
    
    // Getters and Setters (Encapsulation)
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getSurname() {
        return surname;
    }
    
    public void setSurname(String surname) {
        this.surname = surname;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Returns a formatted string representation of the customer.
     * Useful for display purposes.
     */
    @Override
    public String toString() {
        return String.format("Customer[ID=%s, Name=%s %s, Accounts=%d]", 
            customerId, firstName, surname, accounts.size());
    }
}
