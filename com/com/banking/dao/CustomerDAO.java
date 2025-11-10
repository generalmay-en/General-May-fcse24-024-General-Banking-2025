package com.banking.dao;

import com.banking.database.DatabaseManager;
import com.banking.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CustomerDAO (Data Access Object) handles all database operations for Customer entities.
 * This class demonstrates the DAO pattern - separating persistence logic from business logic.
 * Implements full CRUD operations: Create, Read, Update, Delete
 */
public class CustomerDAO {
    private DatabaseManager dbManager;
    
    /**
     * Constructor initializes the DAO with database manager.
     */
    public CustomerDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Saves a new customer to the database (CREATE operation).
     * @param customer The customer object to save
     * @return true if save successful, false otherwise
     */
    public boolean save(Customer customer) {
        String sql = "INSERT INTO customers (customer_id, first_name, surname, address, phone_number, email) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Use PreparedStatement to prevent SQL injection attacks
            pstmt.setString(1, customer.getCustomerId());
            pstmt.setString(2, customer.getFirstName());
            pstmt.setString(3, customer.getSurname());
            pstmt.setString(4, customer.getAddress());
            pstmt.setString(5, customer.getPhoneNumber());
            pstmt.setString(6, customer.getEmail());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error saving customer: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Retrieves a customer by ID from the database (READ operation).
     * @param customerId The customer ID to search for
     * @return Customer object if found, null otherwise
     */
    public Customer findById(String customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Reconstruct Customer object from database row
                Customer customer = new Customer(
                    rs.getString("customer_id"),
                    rs.getString("first_name"),
                    rs.getString("surname"),
                    rs.getString("address")
                );
                customer.setPhoneNumber(rs.getString("phone_number"));
                customer.setEmail(rs.getString("email"));
                return customer;
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding customer: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Retrieves all customers from the database (READ operation).
     * @return List of all customers
     */
    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY surname, first_name";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Customer customer = new Customer(
                    rs.getString("customer_id"),
                    rs.getString("first_name"),
                    rs.getString("surname"),
                    rs.getString("address")
                );
                customer.setPhoneNumber(rs.getString("phone_number"));
                customer.setEmail(rs.getString("email"));
                customers.add(customer);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving customers: " + e.getMessage());
        }
        
        return customers;
    }
    
    /**
     * Updates an existing customer in the database (UPDATE operation).
     * @param customer The customer with updated information
     * @return true if update successful, false otherwise
     */
    public boolean update(Customer customer) {
        String sql = "UPDATE customers SET first_name = ?, surname = ?, address = ?, " +
                     "phone_number = ?, email = ? WHERE customer_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getSurname());
            pstmt.setString(3, customer.getAddress());
            pstmt.setString(4, customer.getPhoneNumber());
            pstmt.setString(5, customer.getEmail());
            pstmt.setString(6, customer.getCustomerId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating customer: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Deletes a customer from the database (DELETE operation).
     * Note: This should check for existing accounts before deletion in production.
     * @param customerId The ID of the customer to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean delete(String customerId) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customerId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting customer: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Searches for customers by name (partial match).
     * Useful for the GUI search functionality.
     * @param searchTerm Name to search for
     * @return List of matching customers
     */
    public List<Customer> searchByName(String searchTerm) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE " +
                     "LOWER(first_name) LIKE ? OR LOWER(surname) LIKE ? " +
                     "ORDER BY surname, first_name";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Customer customer = new Customer(
                    rs.getString("customer_id"),
                    rs.getString("first_name"),
                    rs.getString("surname"),
                    rs.getString("address")
                );
                customer.setPhoneNumber(rs.getString("phone_number"));
                customer.setEmail(rs.getString("email"));
                customers.add(customer);
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching customers: " + e.getMessage());
        }
        
        return customers;
    }
    
    /**
     * Checks if a customer exists in the database.
     * @param customerId The customer ID to check
     * @return true if customer exists
     */
    public boolean exists(String customerId) {
        String sql = "SELECT COUNT(*) FROM customers WHERE customer_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking customer existence: " + e.getMessage());
        }
        
        return false;
    }
}