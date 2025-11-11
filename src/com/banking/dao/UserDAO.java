package com.banking.dao;

import com.banking.database.DatabaseManager;
import com.banking.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO handles all database operations for User/Teller entities.
 * Critical for F-401 requirement: user authentication.
 * Manages bank teller accounts and their permissions.
 */
public class UserDAO {
    private DatabaseManager dbManager;
    
    public UserDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Saves a new user to the database (CREATE operation).
     * @param user The user object to save
     * @return true if save successful
     */
    public boolean save(User user) {
        String sql = "INSERT INTO users (user_id, username, password_hash, role) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getUsername());
            // In production, password should already be hashed before reaching DAO
            pstmt.setString(3, hashPassword("temp")); // Placeholder
            pstmt.setString(4, user.getRole());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Saves a new user with explicit password (for registration).
     * @param userId Unique user identifier
     * @param username Display name
     * @param password Plain text password (will be hashed)
     * @param role User role (TELLER, MANAGER, ADMIN)
     * @return true if save successful
     */
    public boolean saveWithPassword(String userId, String username, String password, String role) {
        String sql = "INSERT INTO users (user_id, username, password_hash, role) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            pstmt.setString(2, username);
            pstmt.setString(3, hashPassword(password));
            pstmt.setString(4, role);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error saving user with password: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Retrieves a user by their ID (READ operation).
     * @param userId The user ID to search for
     * @return User object if found, null otherwise
     */
    public User findById(String userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return createUserFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding user: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Authenticates a user by checking their credentials.
     * Critical for F-401: user login requirement.
     * @param userId The user ID attempting to login
     * @param password The password to verify
     * @return User object if authentication successful, null otherwise
     */
    public User authenticate(String userId, String password) {
        String sql = "SELECT * FROM users WHERE user_id = ? AND password_hash = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            pstmt.setString(2, hashPassword(password));
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = createUserFromResultSet(rs);
                // Mark user as authenticated (this would be handled differently in production)
                System.out.println("User authenticated: " + user.getUsername());
                return user;
            }
            
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }
        
        return null; // Authentication failed
    }
    
    /**
     * Retrieves all users in the system (READ operation).
     * Useful for user management by administrators.
     * @return List of all users
     */
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY username";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(createUserFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving all users: " + e.getMessage());
        }
        
        return users;
    }
    
    /**
     * Retrieves users by role (READ operation).
     * Useful for finding all tellers, managers, etc.
     * @param role The role to filter by
     * @return List of users with that role
     */
    public List<User> findByRole(String role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ? ORDER BY username";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, role);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                users.add(createUserFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding users by role: " + e.getMessage());
        }
        
        return users;
    }
    
    /**
     * Updates a user's information (UPDATE operation).
     * @param user The user with updated information
     * @return true if update successful
     */
    public boolean update(User user) {
        String sql = "UPDATE users SET username = ?, role = ? WHERE user_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getRole());
            pstmt.setString(3, user.getUserId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Updates a user's password (UPDATE operation).
     * @param userId The user ID
     * @param newPassword The new password (plain text, will be hashed)
     * @return true if update successful
     */
    public boolean updatePassword(String userId, String newPassword) {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, hashPassword(newPassword));
            pstmt.setString(2, userId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Deletes a user from the database (DELETE operation).
     * @param userId The user ID to delete
     * @return true if deletion successful
     */
    public boolean delete(String userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Checks if a user ID already exists.
     * Useful for preventing duplicate registrations.
     * @param userId The user ID to check
     * @return true if user exists
     */
    public boolean exists(String userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking user existence: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Gets the total count of users in the system.
     * @return Number of users
     */
    public int getUserCount() {
        String sql = "SELECT COUNT(*) FROM users";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error counting users: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Helper method to create User object from database row.
     * @param rs ResultSet positioned at a valid row
     * @return User object
     * @throws SQLException if database access error occurs
     */
    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        return new User(
            rs.getString("user_id"),
            rs.getString("username"),
            rs.getString("password_hash"),
            rs.getString("role")
        );
    }
    
    /**
     * Simple password hashing for demonstration.
     * IMPORTANT: In production, use BCrypt, Argon2, or PBKDF2 with salt!
     * @param password Plain text password
     * @return Hashed password
     */
    private String hashPassword(String password) {
        // This is NOT secure - only for demonstration!
        // In production: return BCrypt.hashpw(password, BCrypt.gensalt(12));
        return Integer.toString(password.hashCode());
    }
}