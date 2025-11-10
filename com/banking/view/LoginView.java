package com.banking.view;

import com.banking.controller.LoginController;
import com.banking.controller.LoginController.LoginResult;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * LoginView - Login screen for bank tellers.
 * This is the FIRST screen users see when launching the application.
 * Demonstrates proper GUI design with NO business logic.
 */
public class LoginView extends Application {
    private LoginController loginController;
    private TextField userIdField;
    private PasswordField passwordField;
    private Label messageLabel;
    private Stage primaryStage;
    
    public LoginView() {
        this.loginController = new LoginController();
    }
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // Create main container
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%);");
        
        // Create login form
        VBox loginBox = createLoginForm();
        root.setCenter(loginBox);
        
        // Create scene
        Scene scene = new Scene(root, 400, 500);
        
        // Configure stage
        primaryStage.setTitle("Banking System - Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    /**
     * Creates the login form UI components.
     */
    private VBox createLoginForm() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(40));
        container.setMaxWidth(350);
        container.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5);"
        );
        
        // Title
        Label titleLabel = new Label("🏦 Banking System");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#667eea"));
        
        Label subtitleLabel = new Label("Bank Teller Login");
        subtitleLabel.setFont(Font.font("Arial", 14));
        subtitleLabel.setTextFill(Color.GRAY);
        
        // User ID field
        Label userIdLabel = new Label("User ID");
        userIdLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 12));
        
        userIdField = new TextField();
        userIdField.setPromptText("Enter your user ID");
        userIdField.setStyle(
            "-fx-padding: 10;" +
            "-fx-background-radius: 5;" +
            "-fx-border-color: #ddd;" +
            "-fx-border-radius: 5;"
        );
        userIdField.setFont(Font.font("Arial", 14));
        
        // Password field
        Label passwordLabel = new Label("Password");
        passwordLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 12));
        
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setStyle(
            "-fx-padding: 10;" +
            "-fx-background-radius: 5;" +
            "-fx-border-color: #ddd;" +
            "-fx-border-radius: 5;"
        );
        passwordField.setFont(Font.font("Arial", 14));
        
        // Message label for errors/success
        messageLabel = new Label();
        messageLabel.setFont(Font.font("Arial", 12));
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(300);
        messageLabel.setAlignment(Pos.CENTER);
        
        // Login button
        Button loginButton = new Button("Login");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setStyle(
            "-fx-background-color: #667eea;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        
        // Hover effect
        loginButton.setOnMouseEntered(e -> 
            loginButton.setStyle(loginButton.getStyle() + "-fx-background-color: #5568d3;")
        );
        loginButton.setOnMouseExited(e -> 
            loginButton.setStyle(
                "-fx-background-color: #667eea;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 12;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;"
            )
        );
        
        loginButton.setOnAction(e -> handleLogin());
        
        // Allow Enter key to login
        passwordField.setOnAction(e -> handleLogin());
        
        // Help text
        Label helpLabel = new Label("Default login: admin / admin123");
        helpLabel.setFont(Font.font("Arial", 10));
        helpLabel.setTextFill(Color.GRAY);
        helpLabel.setStyle("-fx-padding: 10 0 0 0;");
        
        // Add all components
        container.getChildren().addAll(
            titleLabel,
            subtitleLabel,
            new VBox(5, userIdLabel, userIdField),
            new VBox(5, passwordLabel, passwordField),
            messageLabel,
            loginButton,
            helpLabel
        );
        
        return container;
    }
    
    /**
     * Handles the login button click.
     * Validates input and calls the controller.
     */
    private void handleLogin() {
        // Clear previous messages
        messageLabel.setText("");
        
        // Get input values
        String userId = userIdField.getText().trim();
        String password = passwordField.getText();
        
        // Basic client-side validation
        if (userId.isEmpty()) {
            showError("Please enter your User ID");
            userIdField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showError("Please enter your password");
            passwordField.requestFocus();
            return;
        }
        
        // Call controller to authenticate
        LoginResult result = loginController.login(userId, password);
        
        if (result.isSuccess()) {
            showSuccess("Login successful! Welcome " + result.getUser().getUsername());
            
            // Wait a moment then open dashboard
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    javafx.application.Platform.runLater(this::openDashboard);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            
        } else {
            showError(result.getMessage());
            passwordField.clear();
            passwordField.requestFocus();
        }
    }
    
    /**
     * Opens the main dashboard after successful login.
     */
    private void openDashboard() {
        try {
            DashboardView dashboard = new DashboardView();
            Stage dashboardStage = new Stage();
            dashboard.start(dashboardStage);
            
            // Close login window
            primaryStage.close();
            
        } catch (Exception e) {
            showError("Error opening dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Displays an error message in red.
     */
    private void showError(String message) {
        messageLabel.setText("❌ " + message);
        messageLabel.setTextFill(Color.RED);
    }
    
    /**
     * Displays a success message in green.
     */
    private void showSuccess(String message) {
        messageLabel.setText("✓ " + message);
        messageLabel.setTextFill(Color.GREEN);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}