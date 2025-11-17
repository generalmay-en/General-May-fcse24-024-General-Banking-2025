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

public class TransactionView extends Application {
    
    public enum TransactionType {
        DEPOSIT, WITHDRAWAL
    }
    
    private AccountController accountController;
    private TransactionType transactionType;
    private TextField accountNumberField;
    private TextField amountField;
    private Label messageLabel;
    private Label currentBalanceLabel;
    private Stage primaryStage;
    
    public TransactionView(AccountController accountController, TransactionType transactionType) {
        this.accountController = accountController;
        this.transactionType = transactionType;
    }
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");
        root.setPadding(new Insets(20));
        
        VBox formBox = createTransactionForm();
        root.setCenter(formBox);
        
        String title = transactionType == TransactionType.DEPOSIT ? 
            "Make Deposit" : "Make Withdrawal";
        Scene scene = new Scene(root, 500, 550);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private VBox createTransactionForm() {
        VBox container = new VBox(15);
        container.setAlignment(Pos.TOP_CENTER);
        container.setPadding(new Insets(30));
        container.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        container.setMaxWidth(450);
        
        String icon = transactionType == TransactionType.DEPOSIT ? "💰" : "💸";
        String titleText = transactionType == TransactionType.DEPOSIT ? 
            "Make Deposit" : "Make Withdrawal";
        String colorCode = transactionType == TransactionType.DEPOSIT ? 
            "#FF9800" : "#f44336";
        
        Label titleLabel = new Label(icon + " " + titleText);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web(colorCode));
        
        Separator separator = new Separator();
        
        Label accountLabel = new Label("Account Number *");
        accountLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 12));
        
        HBox accountBox = new HBox(10);
        accountNumberField = new TextField();
        accountNumberField.setPromptText("Enter account number");
        styleTextField(accountNumberField);
        HBox.setHgrow(accountNumberField, Priority.ALWAYS);
        
        Button checkButton = new Button("Check Balance");
        checkButton.setStyle(
            "-fx-background-color: " + colorCode + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 15;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        checkButton.setOnAction(e -> checkBalance());
        
        accountBox.getChildren().addAll(accountNumberField, checkButton);
        
        currentBalanceLabel = new Label();
        currentBalanceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        currentBalanceLabel.setWrapText(true);
        currentBalanceLabel.setMaxWidth(400);
        
        Label amountLabel = new Label("Amount (BWP) *");
        amountLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 12));
        
        amountField = new TextField();
        amountField.setPromptText("Enter amount");
        styleTextField(amountField);
        
        String rulesText = transactionType == TransactionType.DEPOSIT ?
            "ℹ️ Deposits can be made to any account type" :
            "⚠️ Withdrawals are NOT allowed from Savings accounts";
        
        Label rulesLabel = new Label(rulesText);
        rulesLabel.setFont(Font.font("Arial", 11));
        rulesLabel.setTextFill(transactionType == TransactionType.DEPOSIT ? 
            Color.web("#4CAF50") : Color.web("#FF9800"));
        rulesLabel.setWrapText(true);
        rulesLabel.setMaxWidth(400);
        
        messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(400);
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        String buttonText = transactionType == TransactionType.DEPOSIT ? 
            "Process Deposit" : "Process Withdrawal";
        
        Button processButton = new Button(buttonText);
        processButton.setStyle(
            "-fx-background-color: " + colorCode + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 30;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        processButton.setOnAction(e -> handleTransaction());
        
        Button clearButton = new Button("Clear");
        clearButton.setStyle(
            "-fx-background-color: #9E9E9E;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 30;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        clearButton.setOnAction(e -> clearForm());
        
        Button closeButton = new Button("Close");
        closeButton.setStyle(
            "-fx-background-color: #607D8B;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 30;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        closeButton.setOnAction(e -> primaryStage.close());
        
        buttonBox.getChildren().addAll(processButton, clearButton, closeButton);
        
        container.getChildren().addAll(
            titleLabel,
            separator,
            new VBox(5, accountLabel, accountBox),
            currentBalanceLabel,
            new VBox(5, amountLabel, amountField),
            rulesLabel,
            messageLabel,
            buttonBox
        );
        
        return container;
    }
    private void styleTextField(TextField field) {
    field.setStyle(
        "-fx-padding: 10;" +
        "-fx-background-radius: 5;" +
        "-fx-border-color: #ddd;" +
        "-fx-border-radius: 5;"
    );
    field.setFont(Font.font("Arial", 13));  
}

    
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
                " | Account Type: " + result.getAccount().getAccountType()
            );
            currentBalanceLabel.setTextFill(Color.web("#2196F3"));
        } else {
            currentBalanceLabel.setText("");
            showError(result.getMessage());
        }
    }
    
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
            currentBalanceLabel.setTextFill(Color.web("#4CAF50"));
            
            amountField.clear();
            
        } else {
            showError(result.getMessage());
        }
    }
    
    private void clearForm() {
        accountNumberField.clear();
        amountField.clear();
        currentBalanceLabel.setText("");
        messageLabel.setText("");
    }
    
    private void showError(String message) {
        messageLabel.setText("❌ " + message);
        messageLabel.setTextFill(Color.RED);
        messageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
