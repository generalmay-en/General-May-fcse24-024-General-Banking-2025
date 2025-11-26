package com.banking.dao;

import com.banking.database.DatabaseManager;
import com.banking.model.Customer;

import java.util.List;

/**
 * CustomerDAO
 * Provides data access operations for Customer entities.
 */
public class CustomerDAO {

    private final DatabaseManager db;

    public CustomerDAO() {
        this.db = DatabaseManager.getInstance();
    }

    /**
     * Saves a new customer.
     * Delegates to DatabaseManager.saveCustomer().
     */
    public boolean save(Customer customer) {
        return db.saveCustomer(customer);
    }

    /**
     * Retrieves a customer by ID.
     */
    public Customer findById(String customerId) {
        return db.getCustomer(customerId);
    }

    /**
     * Retrieves all customers.
     */
    public List<Customer> findAll() {
        return db.getAllCustomers();
    }

    /**
     * Updates a customer.
     * (MERGE via DatabaseManager will auto-update)
     */
    public boolean update(Customer customer) {
        return db.saveCustomer(customer);
    }

    /**
     * Deletes a customer by ID.
     * Handled via raw SQL inside DatabaseManager.
     */
    public boolean delete(String customerId) {
        return db.deleteCustomer(customerId);
    }

    /**
     * Checks if a customer exists.
     */
    public boolean exists(String customerId) {
        return findById(customerId) != null;
    }

    /**
     * Searches customers by name.
     */
    public List<Customer> searchByName(String searchTerm) {
        return db.searchCustomersByName(searchTerm);
    }
}