package com.banking.controller;

import com.banking.dao.UserDAO;
import com.banking.model.User;

/**
 * LoginController handles all authentication-related operations.
 * This controller mediates between the LoginView (GUI) and the User model/UserDAO.
 * 
 * Demonstrates SEPARATION OF CONCERNS:
 * - No GUI code here
 * - No direct database access (uses UserDAO)
 * - Only orchestration and validation logic
 */
public class LoginController {
    private UserDAO userDAO;
    private User currentUser; // The currently logged-in user
    private static LoginController instance; // Singleton for accessing current user globally
    
    /**
     * Constructor initializes the controller with DAO dependency.
     */
    public LoginController() {
        this.userDAO = new UserDAO();
        this.currentUser = null;
        instance = this;
    }
    
    /**
     * Gets the singleton instance of LoginController.
     * Useful for other controllers to check authentication status.
     * @return The LoginController instance
     */
    public static LoginController getInstance() {
        if (instance == null) {
            instance = new LoginController();
        }
        return instance;
    }
    
    /**
     * Attempts to log in a user with provided credentials.
     * This method satisfies F-401: User authentication requirement.
     * 
     * @param userId User identifier
     * @param password User password
     * @return LoginResult object containing status and message
     */
    public LoginResult login(String userId, String password) {
        // Input validation - demonstrates good controller practice
        if (userId == null || userId.trim().isEmpty()) {
            return new LoginResult(false, "User ID cannot be empty", null);
        }
        
        if (password == null || password.trim().isEmpty()) {
            return new LoginResult(false, "Password cannot be empty", null);
        }
        
        // Attempt authentication through DAO
        User user = userDAO.authenticate(userId.trim(), password);
        
        if (user != null) {
            // Authentication successful
            this.currentUser = user;
            // Authenticate the user object itself
            user.authenticate(userId, password);
            
            return new LoginResult(true, "Login successful", user);
        } else {
            // Authentication failed
            return new LoginResult(false, "Invalid user ID or password", null);
        }
    }
    
    /**
     * Logs out the current user.
     * Clears the current user session.
     */
    public void logout() {
        if (currentUser != null) {
            currentUser.logout(currentUser.getUserId());
            currentUser = null;
        }
    }
    
    /**
     * Gets the currently logged-in user.
     * @return Current user, or null if no one is logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Checks if a user is currently logged in.
     * @return true if user is authenticated
     */
    public boolean isLoggedIn() {
        return currentUser != null && currentUser.isAuthenticated(currentUser.getUserId());
    }
    
    /**
     * Checks if the current user has a specific permission.
     * Implements role-based access control (RBAC).
     * @param permission The permission to check
     * @return true if user has the permission
     */
    public boolean hasPermission(String permission) {
        if (currentUser == null) {
            return false;
        }
        return currentUser.hasPermission(currentUser.getUserId(), permission);
    }
    
    /**
     * Registers a new user (for admin functionality).
     * Only ADMIN users should be able to call this.
     * @param userId New user's ID
     * @param username New user's display name
     * @param password New user's password
     * @param role New user's role
     * @return RegistrationResult with status and message
     */
    public RegistrationResult registerUser(String userId, String username, String password, String role) {
        // Check if current user has permission to create users
        if (!hasPermission("CREATE_USER")) {
            return new RegistrationResult(false, "You don't have permission to create users");
        }
        
        // Input validation
        if (userId == null || userId.trim().isEmpty()) {
            return new RegistrationResult(false, "User ID cannot be empty");
        }
        
        if (username == null || username.trim().isEmpty()) {
            return new RegistrationResult(false, "Username cannot be empty");
        }
        
        if (password == null || password.length() < 6) {
            return new RegistrationResult(false, "Password must be at least 6 characters");
        }
        
        if (role == null || (!role.equals("TELLER") && !role.equals("MANAGER") && !role.equals("ADMIN"))) {
            return new RegistrationResult(false, "Invalid role. Must be TELLER, MANAGER, or ADMIN");
        }
        
        // Check if user already exists
        if (userDAO.exists(userId.trim())) {
            return new RegistrationResult(false, "User ID already exists");
        }
        
        // Create the user
        boolean success = userDAO.saveWithPassword(userId.trim(), username.trim(), password, role);
        
        if (success) {
            return new RegistrationResult(true, "User registered successfully");
        } else {
            return new RegistrationResult(false, "Failed to register user. Please try again");
        }
    }
    
    /**
     * Changes the current user's password.
     * @param currentPassword Current password for verification
     * @param newPassword New password to set
     * @return true if password change successful
     */
    public boolean changePassword(String currentPassword, String newPassword) {
        if (currentUser == null) {
            return false;
        }
        
        // Verify current password
        User verifiedUser = userDAO.authenticate(currentUser.getUserId(), currentPassword);
        if (verifiedUser == null) {
            return false;
        }
        
        // Validate new password
        if (newPassword == null || newPassword.length() < 6) {
            return false;
        }
        
        // Update password
        return userDAO.updatePassword(currentUser.getUserId(), newPassword);
    }
    
    /**
     * Inner class to encapsulate login results.
     * Makes it easy to return both status and user information.
     */
    public static class LoginResult {
        private final boolean success;
        private final String message;
        private final User user;
        
        public LoginResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public User getUser() {
            return user;
        }
    }
    
    /**
     * Inner class to encapsulate registration results.
     */
    public static class RegistrationResult {
        private final boolean success;
        private final String message;
        
        public RegistrationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
    }
}