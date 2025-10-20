package com.banking.interfaces;

/**
 * Authenticatable interface demonstrates the use of INTERFACES in OOP.
 * Defines a contract for classes that require authentication capabilities.
 * This allows different authentication strategies to be implemented.
 */
public interface Authenticatable {
    /**
     * Authenticates a user based on credentials.
     * @param userId The user identifier
     * @param password The user's password
     * @return true if authentication successful, false otherwise
     */
    boolean authenticate(String userId, String password);
    
    /**
     * Validates if a user has specific permissions.
     * @param userId The user identifier
     * @param permission The permission to check
     * @return true if user has the permission
     */
    boolean hasPermission(String userId, String permission);
    
    /**
     * Logs out the current user.
     * @param userId The user identifier
     */
    void logout(String userId);
    
    /**
     * Checks if a user is currently authenticated.
     * @param userId The user identifier
     * @return true if user is authenticated
     */
    boolean isAuthenticated(String userId);
}
