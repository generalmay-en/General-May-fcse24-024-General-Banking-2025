package com.banking;

import com.banking.database.DatabaseManager;
import com.banking.view.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * BankingApplication - Main entry point for the Banking System.
 * This class initializes the database and launches the JavaFX GUI.
 * 
 * Assignment: CSE202 - Object-Oriented Analysis & Design with Java
 * Student: General May (fcse24-024)
 */
public class BankingApplication extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("→💹 Banking System Starting.");
            
            // Initialize database schema.
            System.out.println("✓ Initializing database ⇌");
            DatabaseManager dbManager = DatabaseManager.getInstance();
            dbManager.initializeDatabase();
            
            // Load sample data.
            System.out.println("Loading sample data•••");
            SampleDataLoader.loadSampleData();
            
            System.out.println("Database ready ✓!");
            System.out.println("—————————————————————————————————————————————\n");
            
            // Show login screen.
            LoginView loginView = new LoginView();
            loginView.start(primaryStage);
            
        } catch (Exception e) {
            System.err.println("→ Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void stop() {
        System.out.println("\n → System going offline ✕ ===");
        System.out.println("Goodbye!");
    }
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║   GENERAL BANKING - CSE202 Assignment      ║");
        System.out.println("║   Botswana Accountancy College             ║");
        System.out.println("║   Student: General May (fcse24-024)        ║");
        System.out.println("╚════════════════════════════════════════════╝\n");
        
        launch(args);
    }
}
