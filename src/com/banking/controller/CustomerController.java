package com.banking.controller;

import com.banking.dao.CustomerDAO;
import com.banking.model.Bank;
import com.banking.model.Customer;

import java.util.List;

/**
 * CustomerController handles all customer-related operations.
 * FIXED VERSION - Handles duplicate customer IDs properly
 */
public class CustomerController {
    private CustomerDAO customerDAO;
    private Bank bank;
    private LoginController loginController;
    
    public CustomerController(Bank bank) {
        this.customerDAO = new CustomerDAO();
        this.bank = bank;
        this.loginController = LoginController.getInstance();
    }
    
    /**
     * FIXED: Registers a new customer with proper ID generation
     */
    public CustomerResult registerCustomer(String firstName, String surname, String address,
                                          String phoneNumber, String email) {
        // Check permission
        if (!loginController.hasPermission("CREATE_CUSTOMER")) {
            return new CustomerResult(false, "You don't have permission to register customers", null);
        }
        
        // Input validation
        if (firstName == null || firstName.trim().isEmpty()) {
            return new CustomerResult(false, "First name is required", null);
        }
        
        if (surname == null || surname.trim().isEmpty()) {
            return new CustomerResult(false, "Surname is required", null);
        }
        
        if (address == null || address.trim().isEmpty()) {
            return new CustomerResult(false, "Address is required", null);
        }
        
        // Validate name format
        if (!isValidName(firstName)) {
            return new CustomerResult(false, "First name contains invalid characters", null);
        }
        
        if (!isValidName(surname)) {
            return new CustomerResult(false, "Surname contains invalid characters", null);
        }
        
        // Validate email format if provided
        if (email != null && !email.trim().isEmpty() && !isValidEmail(email)) {
            return new CustomerResult(false, "Invalid email format", null);
        }
        
        // Validate phone format if provided
        if (phoneNumber != null && !phoneNumber.trim().isEmpty() && !isValidPhone(phoneNumber)) {
            return new CustomerResult(false, "Invalid phone number format", null);
        }
        
        try {
            // FIXED: Generate unique customer ID that won't conflict with database
            String customerId = generateUniqueCustomerId();
            
            // Create customer manually instead of using Bank (to avoid ID conflicts)
            Customer customer = new Customer(customerId, firstName.trim(), surname.trim(), address.trim());
            
            // Set optional fields
            if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                customer.setPhoneNumber(phoneNumber.trim());
            }
            
            if (email != null && !email.trim().isEmpty()) {
                customer.setEmail(email.trim());
            }
            
            // Try to save to database
            boolean saved = customerDAO.save(customer);
            
            if (saved) {
                System.out.println("✓ Customer registered successfully: " + customerId);
                return new CustomerResult(true, "Customer registered successfully: " + customerId, customer);
            } else {
                return new CustomerResult(false, "Failed to save customer to database", null);
            }
            
        } catch (Exception e) {
            System.err.println("Error registering customer: " + e.getMessage());
            e.printStackTrace();
            return new CustomerResult(false, "Error registering customer: " + e.getMessage(), null);
        }
    }
    
    /**
     * FIXED: Generates a truly unique customer ID
     */
    private String generateUniqueCustomerId() {
        String customerId;
        int attempts = 0;
        
        do {
            // Use timestamp + random component for uniqueness
            long timestamp = System.currentTimeMillis();
            int random = (int)(Math.random() * 1000);
            customerId = String.format("CUST-%d%03d", timestamp % 100000, random);
            attempts++;
            
            // Safety check - prevent infinite loop
            if (attempts > 100) {
                // Fallback to pure timestamp
                customerId = "CUST-" + System.currentTimeMillis();
                break;
            }
            
        } while (customerDAO.exists(customerId)); // Keep generating until unique
        
        return customerId;
    }
    
    /**
     * Retrieves a customer by their ID.
     */
    public CustomerResult getCustomer(String customerId) {
        if (!loginController.hasPermission("VIEW_BALANCE")) {
            return new CustomerResult(false, "You don't have permission to view customers", null);
        }
        
        if (customerId == null || customerId.trim().isEmpty()) {
            return new CustomerResult(false, "Customer ID is required", null);
        }
        
        Customer customer = customerDAO.findById(customerId.trim());
        
        if (customer != null) {
            return new CustomerResult(true, "Customer found", customer);
        } else {
            return new CustomerResult(false, "Customer not found: " + customerId, null);
        }
    }
    
    /**
     * Updates existing customer information.
     */
    public CustomerResult updateCustomer(Customer customer) {
        if (!loginController.hasPermission("CREATE_CUSTOMER")) {
            return new CustomerResult(false, "You don't have permission to update customers", null);
        }
        
        if (customer == null) {
            return new CustomerResult(false, "Customer object is required", null);
        }
        
        if (!customerDAO.exists(customer.getCustomerId())) {
            return new CustomerResult(false, "Customer does not exist: " + customer.getCustomerId(), null);
        }
        
        boolean updated = customerDAO.update(customer);
        
        if (updated) {
            return new CustomerResult(true, "Customer updated successfully", customer);
        } else {
            return new CustomerResult(false, "Failed to update customer", null);
        }
    }
    
    /**
     * Searches for customers by name (partial match).
     */
    public List<Customer> searchCustomers(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return customerDAO.findAll();
        }
        return customerDAO.searchByName(searchTerm.trim());
    }
    
    /**
     * Retrieves all customers in the system.
     */
    public List<Customer> getAllCustomers() {
        if (!loginController.hasPermission("VIEW_BALANCE")) {
            return List.of();
        }
        return customerDAO.findAll();
    }
    
    /**
     * Deletes a customer from the system.
     */
    public CustomerResult deleteCustomer(String customerId) {
        if (!loginController.hasPermission("DELETE_USER")) {
            return new CustomerResult(false, "You don't have permission to delete customers", null);
        }
        
        if (customerId == null || customerId.trim().isEmpty()) {
            return new CustomerResult(false, "Customer ID is required", null);
        }
        
        Customer customer = customerDAO.findById(customerId.trim());
        if (customer == null) {
            return new CustomerResult(false, "Customer not found", null);
        }
        
        if (customer.getAccounts().size() > 0) {
            return new CustomerResult(false, "Cannot delete customer with existing accounts", null);
        }
        
        boolean deleted = customerDAO.delete(customerId.trim());
        
        if (deleted) {
            return new CustomerResult(true, "Customer deleted successfully", null);
        } else {
            return new CustomerResult(false, "Failed to delete customer", null);
        }
    }
    
    /**
     * Gets the total number of registered customers.
     */
    public int getCustomerCount() {
        return customerDAO.findAll().size();
    }
    
    // Validation helper methods
    
    /**
     * Validates that a name contains only letters, spaces, hyphens, and apostrophes.
     */
    private boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return name.matches("[a-zA-Z\\s'-]+");
    }
    
    /**
     * Validates email format using regex.
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
    
    /**
     * Validates phone number format.
     */
    private boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        String digitsOnly = phone.replaceAll("[^0-9]", "");
        return digitsOnly.length() >= 7 && digitsOnly.length() <= 15;
    }
    
    /**
     * Inner class to encapsulate customer operation results.
     */
    public static class CustomerResult {
        private final boolean success;
        private final String message;
        private final Customer customer;
        
        public CustomerResult(boolean success, String message, Customer customer) {
            this.success = success;
            this.message = message;
            this.customer = customer;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public Customer getCustomer() {
            return customer;
        }
    }
}