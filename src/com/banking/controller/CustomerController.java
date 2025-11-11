package com.banking.controller;

import com.banking.dao.CustomerDAO;
import com.banking.model.Bank;
import com.banking.model.Customer;

import java.util.List;

/**
 * CustomerController handles all customer-related operations.
 * Mediates between the GUI and the Customer model/CustomerDAO.
 * 
 * Responsibilities:
 * - Customer registration (F-101)
 * - Customer information updates
 * - Customer search and retrieval
 * - Input validation
 */
public class CustomerController {
    private CustomerDAO customerDAO;
    private Bank bank;
    private LoginController loginController;
    
    /**
     * Constructor initializes the controller with dependencies.
     * @param bank The Bank instance managing customers
     */
    public CustomerController(Bank bank) {
        this.customerDAO = new CustomerDAO();
        this.bank = bank;
        this.loginController = LoginController.getInstance();
    }
    
    /**
     * Registers a new customer with the bank.
     * Satisfies F-101: Customer registration requirement.
     * 
     * @param firstName Customer's first name
     * @param surname Customer's surname
     * @param address Customer's address
     * @param phoneNumber Customer's phone (optional)
     * @param email Customer's email (optional)
     * @return CustomerResult with status, message, and customer object
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
        
        // Validate name format (no numbers or special characters)
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
            // Create customer through Bank (generates unique ID)
            Customer customer = bank.registerCustomer(
                firstName.trim(),
                surname.trim(),
                address.trim()
            );
            
            // Set optional fields
            if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                customer.setPhoneNumber(phoneNumber.trim());
            }
            
            if (email != null && !email.trim().isEmpty()) {
                customer.setEmail(email.trim());
            }
            
            // Persist to database
            boolean saved = customerDAO.save(customer);
            
            if (saved) {
                return new CustomerResult(true, "Customer registered successfully: " + 
                    customer.getCustomerId(), customer);
            } else {
                return new CustomerResult(false, "Failed to save customer to database", null);
            }
            
        } catch (Exception e) {
            return new CustomerResult(false, "Error registering customer: " + e.getMessage(), null);
        }
    }
    
    /**
     * Retrieves a customer by their ID.
     * @param customerId The customer ID to search for
     * @return CustomerResult with customer if found
     */
    public CustomerResult getCustomer(String customerId) {
        // Check permission
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
     * @param customer The customer with updated information
     * @return CustomerResult with success status
     */
    public CustomerResult updateCustomer(Customer customer) {
        // Check permission
        if (!loginController.hasPermission("CREATE_CUSTOMER")) {
            return new CustomerResult(false, "You don't have permission to update customers", null);
        }
        
        if (customer == null) {
            return new CustomerResult(false, "Customer object is required", null);
        }
        
        // Validate the customer exists
        if (!customerDAO.exists(customer.getCustomerId())) {
            return new CustomerResult(false, "Customer does not exist: " + customer.getCustomerId(), null);
        }
        
        // Update in database
        boolean updated = customerDAO.update(customer);
        
        if (updated) {
            return new CustomerResult(true, "Customer updated successfully", customer);
        } else {
            return new CustomerResult(false, "Failed to update customer", null);
        }
    }
    
    /**
     * Searches for customers by name (partial match).
     * Useful for GUI search functionality.
     * @param searchTerm Name to search for
     * @return List of matching customers
     */
    public List<Customer> searchCustomers(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return customerDAO.findAll();
        }
        return customerDAO.searchByName(searchTerm.trim());
    }
    
    /**
     * Retrieves all customers in the system.
     * Useful for displaying customer lists.
     * @return List of all customers
     */
    public List<Customer> getAllCustomers() {
        // Check permission
        if (!loginController.hasPermission("VIEW_BALANCE")) {
            return List.of(); // Return empty list if no permission
        }
        return customerDAO.findAll();
    }
    
    /**
     * Deletes a customer from the system.
     * Should check for existing accounts before deletion.
     * @param customerId The customer ID to delete
     * @return CustomerResult with success status
     */
    public CustomerResult deleteCustomer(String customerId) {
        // Check permission (only admins should delete)
        if (!loginController.hasPermission("DELETE_USER")) {
            return new CustomerResult(false, "You don't have permission to delete customers", null);
        }
        
        if (customerId == null || customerId.trim().isEmpty()) {
            return new CustomerResult(false, "Customer ID is required", null);
        }
        
        // Check if customer exists
        Customer customer = customerDAO.findById(customerId.trim());
        if (customer == null) {
            return new CustomerResult(false, "Customer not found", null);
        }
        
        // Check if customer has accounts (business rule: don't delete customers with accounts)
        if (customer.getAccounts().size() > 0) {
            return new CustomerResult(false, "Cannot delete customer with existing accounts", null);
        }
        
        // Delete from database
        boolean deleted = customerDAO.delete(customerId.trim());
        
        if (deleted) {
            return new CustomerResult(true, "Customer deleted successfully", null);
        } else {
            return new CustomerResult(false, "Failed to delete customer", null);
        }
    }
    
    /**
     * Gets the total number of registered customers.
     * Useful for dashboard statistics.
     * @return Number of customers
     */
    public int getCustomerCount() {
        return customerDAO.findAll().size();
    }
    
    // Validation helper methods
    
    /**
     * Validates that a name contains only letters, spaces, hyphens, and apostrophes.
     * @param name The name to validate
     * @return true if valid
     */
    private boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        // Allow letters, spaces, hyphens, and apostrophes (for names like O'Brien, Mary-Jane)
        return name.matches("[a-zA-Z\\s'-]+");
    }
    
    /**
     * Validates email format using simple regex.
     * @param email The email to validate
     * @return true if valid format
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        // Simple email validation regex
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
    
    /**
     * Validates phone number format.
     * Accepts formats like: 1234567890, 123-456-7890, (123) 456-7890, +267 1234 5678
     * @param phone The phone number to validate
     * @return true if valid format
     */
    private boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        // Remove all non-digit characters for validation
        String digitsOnly = phone.replaceAll("[^0-9]", "");
        // Should have between 7 and 15 digits
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