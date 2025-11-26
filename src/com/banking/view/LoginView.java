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
import javafx.scene.shape.Rectangle;

/**
 * LoginView 
 * DESIGN FEATURES:
 * - Ultra-clean, minimal design with ample whitespace
 * - Subtle shadows and crisp rounded corners
 * - Monochromatic color scheme with strategic accent colors
 * - Smooth, refined animations and transitions
 * - Perfect visual hierarchy with optimized typography
 * - Enhanced focus states and accessibility
 */
public class LoginView extends Application {
    // Controllers
    private LoginController loginController;
    
    // UI Components
    private TextField userIdField;
    private PasswordField passwordField;
    private Label messageLabel;
    private Stage primaryStage;
    
    /**
     * Constructor - initializes the login controller
     */
    public LoginView() {
        this.loginController = new LoginController();
    }
    
    /**
     * Starts the JavaFX application and builds the UI
     * @param primaryStage The main application window
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // Main container with subtle gradient background
        BorderPane root = new BorderPane();
        root.setStyle(
            "-fx-background-color: linear-gradient(135deg, #606d7aff 0%, #e2e8f0 100%);"
        );
        
        // Center the login form
        VBox loginBox = createLoginForm();
        root.setCenter(loginBox);
        
        // Create scene with appropriate size
        Scene scene = new Scene(root, 480, 540);
        
        // Configure stage properties
        primaryStage.setTitle("𝐆𝐄𝐍𝐄𝐑𝐀𝐋 ₿𝐀𝐍𝐊 𓃵 LOGIN");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }
    
    private VBox createLoginForm() {
        // Main container for the login card
        VBox container = new VBox(30);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(50, 40, 50, 40));
        container.setMaxWidth(340);
        container.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 25, 0, 0, 8);" +
            "-fx-border-color: #f1f5f9;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 16;"
        );
        
        // Header
        VBox headerBox = new VBox(12);
        headerBox.setAlignment(Pos.CENTER);
        
        // Bank icon/logo with subtle design
        StackPane iconContainer = new StackPane();
        Rectangle iconBackground = new Rectangle(60, 60);
        iconBackground.setArcWidth(16);
        iconBackground.setArcHeight(16);
        iconBackground.setFill(Color.web("#3b82f6"));
        
        Label iconLabel = new Label("🏦");
        iconLabel.setFont(Font.font("Arial", 24));
        
        iconContainer.getChildren().addAll(iconBackground, iconLabel);
        
        // Main title
        Label titleLabel = new Label("Welcome Back");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#1e293b"));
        
        // Subtitle
        Label subtitleLabel = new Label("Sign in to your teller account");
        subtitleLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
        subtitleLabel.setTextFill(Color.web("#64748b"));
        
        headerBox.getChildren().addAll(iconContainer, titleLabel, subtitleLabel);
        
        // Form field selection
        VBox formFields = new VBox(16);
        formFields.setMaxWidth(260);
        
        // User ID field
        userIdField = new TextField();
        userIdField.setPromptText("User ID");
        userIdField.setPrefHeight(48);
        userIdField.setStyle(
            "-fx-padding: 0 16;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 12;" +
            "-fx-font-size: 14px;" +
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #1e293b;"
        );
        
        // Focus effect for user ID field
        userIdField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                userIdField.setStyle(
                    "-fx-padding: 0 16;" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: #3b82f6;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 12;" +
                    "-fx-font-size: 14px;" +
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: #1e293b;"
                );
            } else {
                userIdField.setStyle(
                    "-fx-padding: 0 16;" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: #e2e8f0;" +
                    "-fx-border-width: 1.5;" +
                    "-fx-border-radius: 12;" +
                    "-fx-font-size: 14px;" +
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: #1e293b;"
                );
            }
        });
        
        // Password field
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefHeight(48);
        passwordField.setStyle(
            "-fx-padding: 0 16;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 12;" +
            "-fx-font-size: 14px;" +
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #1e293b;"
        );
        
        // Focus effect for password field
        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                passwordField.setStyle(
                    "-fx-padding: 0 16;" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: #3b82f6;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 12;" +
                    "-fx-font-size: 14px;" +
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: #1e293b;"
                );
            } else {
                passwordField.setStyle(
                    "-fx-padding: 0 16;" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: #e2e8f0;" +
                    "-fx-border-width: 1.5;" +
                    "-fx-border-radius: 12;" +
                    "-fx-font-size: 14px;" +
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: #1e293b;"
                );
            }
        });
        
        // Allow Enter key to trigger login
        passwordField.setOnAction(e -> handleLogin());
        
        formFields.getChildren().addAll(userIdField, passwordField);
        
        // Message label
        messageLabel = new Label();
        messageLabel.setFont(Font.font("System", FontWeight.MEDIUM, 13));
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(260);
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setPadding(new Insets(8));
        
        // Login Button
        Button loginButton = new Button("Sign In");
        loginButton.setMaxWidth(260);
        loginButton.setPrefHeight(48);
        loginButton.setStyle(
            "-fx-background-color: #1e293b;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 12;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(30, 41, 59, 0.2), 8, 0, 0, 2);"
        );
        
        // Hover effect for login button
        loginButton.setOnMouseEntered(e -> {
            loginButton.setStyle(
                "-fx-background-color: #334155;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 12;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(30, 41, 59, 0.3), 12, 0, 0, 4);"
            );
        });
        
        loginButton.setOnMouseExited(e -> {
            loginButton.setStyle(
                "-fx-background-color: #1e293b;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 12;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(30, 41, 59, 0.2), 8, 0, 0, 2);"
            );
        });
        
        // Login button action
        loginButton.setOnAction(e -> handleLogin());
        
        // Help text
        Label helpLabel = new Label("→en.generalmay@gmail.com");
        helpLabel.setFont(Font.font("System", 11));
        helpLabel.setTextFill(Color.web("#94a3b8"));
        helpLabel.setAlignment(Pos.CENTER);
        
        // Footer
        Label footerLabel = new Label("𓃵𓃵𓃵𓃵𓃵𓃵𓃵𓃵𓃵𓃵𓃵𓃵𓃵𓃵𓃵");
        footerLabel.setFont(Font.font("System", 10));
        footerLabel.setTextFill(Color.web("#cbd5e1"));
        
        // Add all components to container
        container.getChildren().addAll(
            headerBox,
            formFields,
            messageLabel,
            loginButton,
            helpLabel,
            footerLabel
        );
        
        return container;
    }
    
    /**
     * Handles the login action when user clicks Sign In or presses Enter
     */
    private void handleLogin() {
        // Clear any previous messages
        messageLabel.setText("");
        
        // Get trimmed input values
        String userId = userIdField.getText().trim();
        String password = passwordField.getText();
        
        // CLIENT-SIDE VALIDATION
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
            // Show success message
            showSuccess("Login successful! Welcome " + result.getUser().getUsername());
            
            // Brief delay before opening dashboard for better UX
            new Thread(() -> {
                try {
                    Thread.sleep(800);
                    javafx.application.Platform.runLater(this::openDashboard);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            
        } else {
            // Show error message from controller
            showError(result.getMessage());
            passwordField.clear();
            passwordField.requestFocus();
        }
    }
    
    /**
     * Opens the main dashboard after successful login
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
     * Displays an error message with red styling
     * @param message The error message to display
     */
    private void showError(String message) {
        messageLabel.setText("✕ " + message);
        messageLabel.setTextFill(Color.web("#dc2626"));
        messageLabel.setStyle(
            "-fx-background-color: #fef2f2;" +
            "-fx-padding: 12;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #fecaca;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;"
        );
    }
    
    /**
     * Displays a success message with green styling
     * @param message The success message to display
     */
    private void showSuccess(String message) {
        messageLabel.setText("✓ " + message);
        messageLabel.setTextFill(Color.web("#059669"));
        messageLabel.setStyle(
            "-fx-background-color: #f0fdf4;" +
            "-fx-padding: 12;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #bbf7d0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;"
        );
    }
    
    /**
     * Main method to launch the application
     */
    public static void main(String[] args) {
        launch(args);
    }
}