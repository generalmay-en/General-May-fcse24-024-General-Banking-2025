package com.banking.model;

import com.banking.interfaces.Authenticatable;
import java.util.HashSet;
import java.util.Set;

/**
 * User class represents a Bank Teller or system user.
 * Demonstrates INTERFACE IMPLEMENTATION - implements the Authenticatable interface.
 * In a real banking system, this would handle secure authentication.
 */
public class User implements Authenticatable {
    private String userId;
    private String username;
    private String passwordHash; // In production, always store hashed passwords
    private String role; // e.g., "TELLER", "MANAGER", "ADMIN"
    private boolean authenticated;
    private Set<String> permissions;
    
    /**
     * Constructor for creating a new user.
     * @param userId Unique user identifier
     * @param username Display name for the user
     * @param passwordHash Hashed password (never store plain text!)
     * @param role User's role in the system
     */
    public User(String userId, String username, String passwordHash, String role) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.authenticated = false;
        this.permissions = new HashSet<>();
        initializePermissions();
    }
    
    /**
     * Initializes permissions based on user role.
     * Demonstrates role-based access control (RBAC).
     */
    private void initializePermissions() {
        // Different roles get different permissions
        switch (role.toUpperCase()) {
            case "ADMIN":
                permissions.add("CREATE_USER");
                permissions.add("DELETE_USER");
                permissions.add("VIEW_ALL_ACCOUNTS");
                // Fall through to add all lower permissions
            case "MANAGER":
                permissions.add("CLOSE_ACCOUNT");
                permissions.add("OVERRIDE_LIMIT");
                // Fall through to add teller permissions
            case "TELLER":
                permissions.add("CREATE_CUSTOMER");
                permissions.add("OPEN_ACCOUNT");
                permissions.add("DEPOSIT");
                permissions.add("WITHDRAW");
                permissions.add("VIEW_BALANCE");
                permissions.add("VIEW_TRANSACTIONS");
                break;
            default:
                // No permissions for unknown roles
                break;
        }
    }
    
    /**
     * Implements authenticate from Authenticatable interface.
     * In production, this would use proper password hashing (bcrypt, Argon2, etc.)
     * @param userId User identifier to authenticate
     * @param password Password to verify
     * @return true if credentials are valid
     */
    @Override
    public boolean authenticate(String userId, String password) {
        // Simple authentication for demonstration
        // In production: use BCrypt.checkpw(password, this.passwordHash)
        if (this.userId.equals(userId) && this.passwordHash.equals(hashPassword(password))) {
            this.authenticated = true;
            return true;
        }
        return false;
    }
    
    /**
     * Implements hasPermission from Authenticatable interface.
     * Checks if user has specific permission based on their role.
     * @param userId User to check
     * @param permission Permission to verify
     * @return true if user has the permission
     */
    @Override
    public boolean hasPermission(String userId, String permission) {
        if (!this.userId.equals(userId) || !authenticated) {
            return false;
        }
        return permissions.contains(permission);
    }
    
    /**
     * Implements logout from Authenticatable interface.
     * Clears the authenticated status.
     * @param userId User to logout
     */
    @Override
    public void logout(String userId) {
        if (this.userId.equals(userId)) {
            this.authenticated = false;
        }
    }
    
    /**
     * Implements isAuthenticated from Authenticatable interface.
     * @param userId User to check
     * @return true if user is currently authenticated
     */
    @Override
    public boolean isAuthenticated(String userId) {
        return this.userId.equals(userId) && this.authenticated;
    }
    
    /**
     * Simple password hashing for demonstration.
     * In production, use BCrypt, Argon2, or similar strong hashing algorithms.
     * @param password Plain text password
     * @return Hashed password
     */
    private String hashPassword(String password) {
        // This is just for demonstration - NOT secure for production!
        // In real banking systems, use: BCrypt.hashpw(password, BCrypt.gensalt(12))
        return Integer.toString(password.hashCode());
    }
    
    // Getters
    public String getUserId() {
        return userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getRole() {
        return role;
    }
    
    public Set<String> getPermissions() {
        return new HashSet<>(permissions);
    }
    
    @Override
    public String toString() {
        return String.format("User[ID=%s, Username=%s, Role=%s, Authenticated=%s]",
            userId, username, role, authenticated);
    }
}
