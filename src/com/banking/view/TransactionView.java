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
 * TransactionView 
 * 
 * DESIGN FEATURES:
 * - Color-coded by transaction type (orange for deposits, red for withdrawals)
 * - Clean, focused form design
 * - Real-time balance checking
 * - Clear validation feedback
 * - Smooth transitions and effects
 * 
 */
public class TransactionView extends Application {
    
    /**
     * Enum for transaction types
     */
    public enum TransactionType {
        DEPOSIT, WITHDRAWAL
    }
    
    // Controller and type 
    private AccountController accountController;
    private TransactionType transactionType;
    
    // UI Components
    private TextField accountNumberField;
    private TextField amountField;
    private Label messageLabel;
    private Label currentBalanceLabel;
    private Stage primaryStage;
    
    /**
     * Constructor
     * @param accountController Account operations controller
     * @param transactionType Type of transaction (DEPOSIT or WITHDRAWAL)
     */
    public TransactionView(AccountController accountController, TransactionType transactionType) {
        this.accountController = accountController;
        this.transactionType = transactionType;
    }
    
    /**
     * Starts the transaction window
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f8fafc;");
        root.setPadding(new Insets(25));
        
        VBox formBox = createTransactionForm();
        root.setCenter(formBox);
        
        String title = transactionType == TransactionType.DEPOSIT ? 
            "Make Deposit" : "Make Withdrawal";
        Scene scene = new Scene(root, 540, 600);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Creates the complete transaction form
     * @return VBox containing all form elements
     */
    private VBox createTransactionForm() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.TOP_CENTER);
        container.setPadding(new Insets(35));
        container.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );
        container.setMaxWidth(480);
        
        // Dynamic colors based on transaction type
        String icon = transactionType == TransactionType.DEPOSIT ? "💰" : "💸";
        String titleText = transactionType == TransactionType.DEPOSIT ? 
            "Make Deposit" : "Make Withdrawal";
        String primaryColor = transactionType == TransactionType.DEPOSIT ? 
            "#f59e0b" : "#ef4444";
        String lightColor = transactionType == TransactionType.DEPOSIT ? 
            "#fef3c7" : "#fee2e2";
        
        // Header
        VBox headerBox = new VBox(8);
        headerBox.setAlignment(Pos.CENTER);
        
        // Icon container
        StackPane iconContainer = new StackPane();
        iconContainer.setPrefSize(70, 70);
        iconContainer.setStyle(
            "-fx-background-color: " + lightColor + ";" +
            "-fx-background-radius: 15;"
        );
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Arial", 40));
        iconContainer.getChildren().add(iconLabel);
        
        Label titleLabel = new Label(titleText);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        titleLabel.setTextFill(Color.web(primaryColor));
        
        Label subtitleLabel = new Label("Process " + 
            (transactionType == TransactionType.DEPOSIT ? "deposit" : "withdrawal") + 
            " transaction");
        subtitleLabel.setFont(Font.font("Arial", 13));
        subtitleLabel.setTextFill(Color.web("#64748b"));
        
        headerBox.getChildren().addAll(iconContainer, titleLabel, subtitleLabel);
        
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));
        
        // Account section
        VBox accountSection = new VBox(10);
        
        Label accountLabel = new Label("Account Number *");
        accountLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
        accountLabel.setTextFill(Color.web("#1e293b"));
        
        HBox accountBox = new HBox(10);
        accountNumberField = new TextField();
        accountNumberField.setPromptText("Enter account number");
        accountNumberField.setPrefHeight(42);
        styleTextField(accountNumberField, primaryColor);
        HBox.setHgrow(accountNumberField, Priority.ALWAYS);
        
        Button checkButton = new Button("Check Balance");
        checkButton.setPrefHeight(42);
        checkButton.setStyle(
            "-fx-background-color: " + primaryColor + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 10 15;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
        // Button hover effects
        checkButton.setOnMouseEntered(e -> {
            String hoverColor = transactionType == TransactionType.DEPOSIT ? 
                "#d97706" : "#dc2626";
            checkButton.setStyle(
                "-fx-background-color: " + hoverColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 10 15;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            );
        });
        
        checkButton.setOnMouseExited(e ->
            checkButton.setStyle(
                "-fx-background-color: " + primaryColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 10 15;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            )
        );
        
        checkButton.setOnAction(e -> checkBalance());
        
        accountBox.getChildren().addAll(accountNumberField, checkButton);
        
        // Balance display
        currentBalanceLabel = new Label();
        currentBalanceLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
        currentBalanceLabel.setWrapText(true);
        currentBalanceLabel.setMaxWidth(440);
        
        accountSection.getChildren().addAll(accountLabel, accountBox, currentBalanceLabel);
        
        // Amount section
        VBox amountBox = new VBox(8);
        
        Label amountLabel = new Label("Amount (BWP) *");
        amountLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
        amountLabel.setTextFill(Color.web("#1e293b"));
        
        amountField = new TextField();
        amountField.setPromptText("Enter amount");
        amountField.setPrefHeight(42);
        styleTextField(amountField, primaryColor);
        
        amountBox.getChildren().addAll(amountLabel, amountField);
        
        // Rules notice
        String rulesText = transactionType == TransactionType.DEPOSIT ?
            "ℹ️ Deposits can be made to any account type" :
            "⚠️ Withdrawals are NOT allowed from Savings accounts";
        
        Label rulesLabel = new Label(rulesText);
        rulesLabel.setFont(Font.font("Arial", 12));
        rulesLabel.setTextFill(Color.web(
            transactionType == TransactionType.DEPOSIT ? "#10b981" : "#f59e0b"
        ));
        rulesLabel.setWrapText(true);
        rulesLabel.setMaxWidth(440);
        rulesLabel.setAlignment(Pos.CENTER);
        rulesLabel.setStyle(
            "-fx-background-color: " + 
            (transactionType == TransactionType.DEPOSIT ? "#d1fae5" : "#fef3c7") + ";" +
            "-fx-padding: 10;" +
            "-fx-background-radius: 8;"
        );
        
        // Message label
        messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(440);
        messageLabel.setAlignment(Pos.CENTER);
        
        // Buttons
        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        String buttonText = transactionType == TransactionType.DEPOSIT ? 
            "Process Deposit" : "Process Withdrawal";
        
        Button processButton = new Button(buttonText);
        processButton.setPrefWidth(160);
        processButton.setPrefHeight(42);
        processButton.setStyle(
            "-fx-background-color: " + primaryColor + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, " + 
            (transactionType == TransactionType.DEPOSIT ? 
                "rgba(245, 158, 11, 0.3)" : "rgba(239, 68, 68, 0.3)") + 
            ", 8, 0, 0, 3);"
        );
        
        processButton.setOnMouseEntered(e -> {
            String hoverColor = transactionType == TransactionType.DEPOSIT ? 
                "#d97706" : "#dc2626";
            processButton.setStyle(
                "-fx-background-color: " + hoverColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, " + 
                (transactionType == TransactionType.DEPOSIT ? 
                    "rgba(245, 158, 11, 0.5)" : "rgba(239, 68, 68, 0.5)") + 
                ", 12, 0, 0, 5);"
            );
        });
        
        processButton.setOnMouseExited(e ->
            processButton.setStyle(
                "-fx-background-color: " + primaryColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, " + 
                (transactionType == TransactionType.DEPOSIT ? 
                    "rgba(245, 158, 11, 0.3)" : "rgba(239, 68, 68, 0.3)") + 
                ", 8, 0, 0, 3);"
            )
        );
        
        processButton.setOnAction(e -> handleTransaction());
        
        Button clearButton = new Button("Clear");
        clearButton.setPrefWidth(100);
        clearButton.setPrefHeight(42);
        styleSecondaryButton(clearButton);
        clearButton.setOnAction(e -> clearForm());
        
        Button closeButton = new Button("Close");
        closeButton.setPrefWidth(100);
        closeButton.setPrefHeight(42);
        styleTertiaryButton(closeButton);
        closeButton.setOnAction(e -> primaryStage.close());
        
        buttonBox.getChildren().addAll(processButton, clearButton, closeButton);
        
        // Add all components
        container.getChildren().addAll(
            headerBox,
            separator,
            accountSection,
            amountBox,
            rulesLabel,
            messageLabel,
            buttonBox
        );
        
        return container;
    }
    
    /**
     * Applies styling to text fields with dynamic color
     */
    private void styleTextField(TextField field, String focusColor) {
        field.setStyle(
            "-fx-padding: 12 15;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 8;" +
            "-fx-font-size: 13px;" +
            "-fx-background-color: #f8fafc;"
        );
        
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle(
                    field.getStyle() + 
                    "-fx-border-color: " + focusColor + "; -fx-background-color: white;"
                );
            } else {
                field.setStyle(
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
    }
    
    /**
     * Styles secondary buttons
     */
    private void styleSecondaryButton(Button button) {
        button.setStyle(
            "-fx-background-color: #f1f5f9;" +
            "-fx-text-fill: #475569;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
        button.setOnMouseEntered(e ->
            button.setStyle(
                "-fx-background-color: #e2e8f0;" +
                "-fx-text-fill: #334155;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            )
        );
        
        button.setOnMouseExited(e ->
            button.setStyle(
                "-fx-background-color: #f1f5f9;" +
                "-fx-text-fill: #475569;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            )
        );
    }
    
    /**
     * Styles tertiary buttons
     */
    private void styleTertiaryButton(Button button) {
        button.setStyle(
            "-fx-background-color: #94a3b8;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
        button.setOnMouseEntered(e ->
            button.setStyle(
                "-fx-background-color: #64748b;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            )
        );
        
        button.setOnMouseExited(e ->
            button.setStyle(
                "-fx-background-color: #94a3b8;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            )
        );
    }
    
    /**
     * Checks account balance
     */
    private void checkBalance() {
        String accountNumber = accountNumberField.getText().trim();
        
        if (accountNumber.isEmpty()) {
            showError("Please enter an account number");
            return;
        }
        
        var result = accountController.getBalance(accountNumber);
        
        if (result.isSuccess()) {
            currentBalanceLabel.setText(
                "💳 Current Balance: BWP " + String.format("%.2f", result.getBalance()) +
                " | " + result.getAccount().getAccountType()
            );
            currentBalanceLabel.setTextFill(Color.web("#3b82f6"));
            currentBalanceLabel.setStyle(
                "-fx-background-color: #dbeafe;" +
                "-fx-padding: 10;" +
                "-fx-background-radius: 8;"
            );
        } else {
            currentBalanceLabel.setText("");
            showError(result.getMessage());
        }
    }
    
    /**
     * Handles transaction processing
     */
    private void handleTransaction() {
        messageLabel.setText("");
        
        String accountNumber = accountNumberField.getText().trim();
        String amountStr = amountField.getText().trim();
        
        if (accountNumber.isEmpty() || amountStr.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            showError("Invalid amount. Please enter a valid number");
            return;
        }
        
        if (amount <= 0) {
            showError("Amount must be greater than zero");
            return;
        }
        
        // Call controller 
        var result = transactionType == TransactionType.DEPOSIT ?
            accountController.deposit(accountNumber, amount) :
            accountController.withdraw(accountNumber, amount);
        
        if (result.isSuccess()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(transactionType == TransactionType.DEPOSIT ? 
                "Deposit Successful!" : "Withdrawal Successful!");
            alert.setContentText(
                "Account: " + accountNumber + "\n" +
                "Amount: BWP " + String.format("%.2f", amount) + "\n" +
                "New Balance: BWP " + String.format("%.2f", result.getNewBalance())
            );
            alert.showAndWait();
            
            currentBalanceLabel.setText(
                "💳 New Balance: BWP " + String.format("%.2f", result.getNewBalance())
            );
            currentBalanceLabel.setTextFill(Color.web("#10b981"));
            currentBalanceLabel.setStyle(
                "-fx-background-color: #d1fae5;" +
                "-fx-padding: 10;" +
                "-fx-background-radius: 8;"
            );
            
            amountField.clear();
            
        } else {
            showError(result.getMessage());
        }
    }
    
    /**
     * Clears all form fields
     */
    private void clearForm() {
        accountNumberField.clear();
        amountField.clear();
        currentBalanceLabel.setText("");
        messageLabel.setText("");
    }
    
    /**
     * Shows error message
     */
    private void showError(String message) {
        messageLabel.setText("✕ " + message);
        messageLabel.setTextFill(Color.web("#ef4444"));
        messageLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
        messageLabel.setStyle(
            "-fx-background-color: #fee2e2;" +
            "-fx-padding: 12;" +
            "-fx-background-radius: 8;"
        );
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        launch(args);
    }
}