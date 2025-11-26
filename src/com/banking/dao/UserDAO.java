package com.banking.dao;

import com.banking.database.DatabaseManager;
import com.banking.model.User;

import java.util.List;

/**
 * UserDAO
 * Handles authentication and user CRUD operations.
 */
public class UserDAO {

    private final DatabaseManager db;

    public UserDAO() {
        this.db = DatabaseManager.getInstance();
    }

    /**
     * Saves a user object.
     */
    public boolean save(User user) {
        return db.saveUser(user);
    }

    /**
     * Saves a user with individual parameters.
     * Automatically hashes the password for secure storage.
     */
    public boolean saveWithPassword(String id, String username, String pass, String role) {
        // Hash the password before creating the User object
        String passwordHash = Integer.toString(pass.hashCode());
        User user = new User(id, username, passwordHash, role);
        return db.saveUser(user);
    }

    /**
     * Finds a user by ID.
     */
    public User findById(String id) {
        return db.getUser(id);
    }

    /**
     * Authenticates a user by ID and password.
     * Returns the user object if authentication succeeds, null otherwise.
     */
    public User authenticate(String id, String password) {
        boolean authResult = db.authenticate(id, password);
        if (authResult) {
            return db.getUser(id);
        }
        return null;
    }

    /**
     * Retrieves all users.
     */
    public List<User> findAll() {
        return db.getAllUsers();
    }

    /**
     * Finds users by role.
     */
    public List<User> findByRole(String role) {
        return db.getUsersByRole(role);
    }

    /**
     * Updates a user.
     */
    public boolean update(User user) {
        return db.updateUser(user);
    }

    /**
     * Updates user password.
     */
    public boolean updatePassword(String id, String newPass) {
        return db.updateUserPassword(id, newPass);
    }

    /**
     * Deletes a user by ID.
     */
    public boolean delete(String id) {
        return db.deleteUser(id);
    }

    /**
     * Checks if a user exists.
     */
    public boolean exists(String id) {
        return db.getUser(id) != null;
    }
}