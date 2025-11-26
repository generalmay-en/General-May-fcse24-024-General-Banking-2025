package com.banking.view;

import com.banking.controller.AccountController;
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
 * BalanceView  
 * DESIGN FEATURES:
 * - Clean, focused design
 * - Large, clear balance display
 * - Card-based result presentation
 * - Purple theme for financial data
 * - Smooth animations
 */
public class BalanceView extends Application {
    // Controller 
    private AccountController accountController;
    
    // UI Components
    private TextField accountNumberField;
    private VBox resultBox;
    private Stage primaryStage;
    
    /**
     * Constructor
     * @param accountController Account operations controller
     */
    public BalanceView(AccountController accountController) {
        this.accountController = accountController;
    }
    
    /**
     * Starts the balance view window
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f8fafc;");
        root.setPadding(new Insets(25));
        
        VBox content = createContent();
        root.setCenter(content);
        
        Scene scene = new Scene(root, 540, 520);
        primaryStage.setTitle("View Account Balance");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Creates the main content layout
     * @return VBox containing all elements
     */
    private VBox createContent() {
        VBox container = new VBox(25);
        container.setAlignment(Pos.TOP_CENTER);
        container.setPadding(new Insets(35));
        container.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );
        container.setMaxWidth(480);
        
        // Header
        VBox headerBox = new VBox(8);
        headerBox.setAlignment(Pos.CENTER);
        
        // Icon container
        StackPane iconContainer = new StackPane();
        iconContainer.setPrefSize(70, 70);
        iconContainer.setStyle(
            "-fx-background-color: #ede9fe;" +
            "-fx-background-radius: 15;"
        );
        
        Label iconLabel = new Label("📊");
        iconLabel.setFont(Font.font("Arial", 40));
        iconContainer.getChildren().add(iconLabel);
        
        Label titleLabel = new Label("Account Balance");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        titleLabel.setTextFill(Color.web("#8b5cf6"));
        
        Label subtitleLabel = new Label("Check current account balance");
        subtitleLabel.setFont(Font.font("Arial", 13));
        subtitleLabel.setTextFill(Color.web("#64748b"));
        
        headerBox.getChildren().addAll(iconContainer, titleLabel, subtitleLabel);
        
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 15, 0));
        
        // Search
        VBox searchSection = new VBox(10);
        
        Label searchLabel = new Label("Account Number");
        searchLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
        searchLabel.setTextFill(Color.web("#1e293b"));
        
        HBox searchBox = new HBox(10);
        accountNumberField = new TextField();
        accountNumberField.setPromptText("Enter account number to check");
        accountNumberField.setPrefHeight(42);
        accountNumberField.setStyle(
            "-fx-padding: 12 15;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 8;" +
            "-fx-font-size: 13px;" +
            "-fx-background-color: #f8fafc;"
        );
        
        // Focus effects
        accountNumberField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                accountNumberField.setStyle(
                    accountNumberField.getStyle() + 
                    "-fx-border-color: #8b5cf6; -fx-background-color: white;"
                );
            } else {
                accountNumberField.setStyle(
                    "-fx-padding: 12 15;" +
                    "-fx-background-radius: 8;" +
                    "-fx-border-color: #e2e8f0;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 8;" +
                    "-fx-font-size: 13px;" +
                    "-fx-background-color: #f8fafc;"
                );
            }
        });
        
        // Allow Enter key to search
        accountNumberField.setOnAction(e -> viewBalance());
        
        HBox.setHgrow(accountNumberField, Priority.ALWAYS);
        
        Button searchButton = new Button("View Balance");
        searchButton.setPrefHeight(42);
        searchButton.setStyle(
            "-fx-background-color: #8b5cf6;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 10 25;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(139, 92, 246, 0.3), 8, 0, 0, 3);"
        );
        
        searchButton.setOnMouseEntered(e ->
            searchButton.setStyle(
                "-fx-background-color: #7c3aed;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 10 25;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(139, 92, 246, 0.5), 12, 0, 0, 5);"
            )
        );
        
        searchButton.setOnMouseExited(e ->
            searchButton.setStyle(
                "-fx-background-color: #8b5cf6;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 10 25;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(139, 92, 246, 0.3), 8, 0, 0, 3);"
            )
        );
        
        searchButton.setOnAction(e -> viewBalance());
        
        searchBox.getChildren().addAll(accountNumberField, searchButton);
        searchSection.getChildren().addAll(searchLabel, searchBox);
        
        // Result
        resultBox = new VBox(15);
        resultBox.setAlignment(Pos.CENTER);
        resultBox.setPadding(new Insets(40));
        resultBox.setPrefHeight(180);
        resultBox.setStyle(
            "-fx-background-color: #fafaf9;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #e7e5e4;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 10;"
        );
        
        // Initial placeholder
        Label placeholderLabel = new Label("Enter account number and click 'View Balance'");
        placeholderLabel.setFont(Font.font("Arial", 13));
        placeholderLabel.setTextFill(Color.web("#94a3b8"));
        resultBox.getChildren().add(placeholderLabel);
        
        // Close button
        Button closeButton = new Button("Close");
        closeButton.setPrefWidth(120);
        closeButton.setPrefHeight(42);
        closeButton.setStyle(
            "-fx-background-color: #94a3b8;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
        closeButton.setOnMouseEntered(e ->
            closeButton.setStyle(
                "-fx-background-color: #64748b;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            )
        );
        
        closeButton.setOnMouseExited(e ->
            closeButton.setStyle(
                "-fx-background-color: #94a3b8;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            )
        );
        
        closeButton.setOnAction(e -> primaryStage.close());
        
        HBox closeButtonBox = new HBox(closeButton);
        closeButtonBox.setAlignment(Pos.CENTER);
        
        // Add all components
        container.getChildren().addAll(
            headerBox,
            separator,
            searchSection,
            resultBox,
            closeButtonBox
        );
        
        return container;
    }
    
    /**
     * Views the account balance
     */
    private void viewBalance() {
        String accountNumber = accountNumberField.getText().trim();
        
        if (accountNumber.isEmpty()) {
            showError("Please enter an account number");
            return;
        }
        
        // Call controller 
        var result = accountController.getBalance(accountNumber);
        
        if (result.isSuccess()) {
            displayBalance(
                result.getAccount().getAccountType(), 
                result.getBalance(), 
                accountNumber,
                result.getAccount().getCustomer().getFirstName() + " " + 
                result.getAccount().getCustomer().getSurname()
            );
        } else {
            showError(result.getMessage());
        }
    }
    
    /**
     * Displays balance information in a card
     * @param accountType Type of account
     * @param balance Current balance
     * @param accountNumber Account number
     * @param customerName Customer name
     */
    private void displayBalance(String accountType, double balance, 
                                String accountNumber, String customerName) {
        resultBox.getChildren().clear();
        resultBox.setStyle(
            "-fx-background-color: linear-gradient(135deg, #8b5cf6 0%, #a78bfa 100%);" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: transparent;" +
            "-fx-border-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(139, 92, 246, 0.4), 15, 0, 0, 5);"
        );
        
        // Success icon
        Label successIcon = new Label("✓");
        successIcon.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        successIcon.setTextFill(Color.WHITE);
        successIcon.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.2);" +
            "-fx-background-radius: 25;" +
            "-fx-padding: 10;"
        );
        
        // Account type
        Label typeLabel = new Label(accountType);
        typeLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        typeLabel.setTextFill(Color.web("#f5f3ff"));
        
        // Customer name
        Label nameLabel = new Label(customerName);
        nameLabel.setFont(Font.font("Arial", 13));
        nameLabel.setTextFill(Color.web("#e9d5ff"));
        
        // Separator
        Separator balanceSeparator = new Separator();
        balanceSeparator.setMaxWidth(200);
        balanceSeparator.setStyle("-fx-background-color: rgba(255, 255, 255, 0.3);");
        
        // Balance amount - large and prominent
        Label balanceLabel = new Label("BWP " + String.format("%.2f", balance));
        balanceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        balanceLabel.setTextFill(Color.WHITE);
        
        // Balance label
        Label balanceTextLabel = new Label("Current Balance");
        balanceTextLabel.setFont(Font.font("Arial", 12));
        balanceTextLabel.setTextFill(Color.web("#e9d5ff"));
        
        resultBox.getChildren().addAll(
            successIcon,
            typeLabel,
            nameLabel,
            balanceSeparator,
            balanceLabel,
            balanceTextLabel
        );
    }
    
    /**
     * Shows error message in result box
     * @param message Error message to display
     */
    private void showError(String message) {
        resultBox.getChildren().clear();
        resultBox.setStyle(
            "-fx-background-color: #fafaf9;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #e7e5e4;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 10;"
        );
        
        Label errorIcon = new Label("✕");
        errorIcon.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        errorIcon.setTextFill(Color.web("#ef4444"));
        
        Label errorLabel = new Label(message);
        errorLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        errorLabel.setTextFill(Color.web("#ef4444"));
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(380);
        errorLabel.setAlignment(Pos.CENTER);
        
        resultBox.getChildren().addAll(errorIcon, errorLabel);
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        launch(args);
    }
}