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
 * BalanceView - Displays account balance information.
 */
public class BalanceView extends Application {
    private AccountController accountController;
    private TextField accountNumberField;
    private VBox resultBox;
    private Stage primaryStage;
    
    public BalanceView(AccountController accountController) {
        this.accountController = accountController;
    }
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");
        root.setPadding(new Insets(20));
        
        VBox content = createContent();
        root.setCenter(content);
        
        Scene scene = new Scene(root, 500, 450);
        primaryStage.setTitle("View Account Balance");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private VBox createContent() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.TOP_CENTER);
        container.setPadding(new Insets(30));
        container.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        container.setMaxWidth(450);
        
        Label titleLabel = new Label("📊 View Account Balance");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#9C27B0"));
        
        Separator separator = new Separator();
        
        HBox searchBox = new HBox(10);
        accountNumberField = new TextField();
        accountNumberField.setPromptText("Enter account number");
        accountNumberField.setStyle(
            "-fx-padding: 10;" +
            "-fx-background-radius: 5;" +
            "-fx-border-color: #ddd;" +
            "-fx-border-radius: 5;"
        );
        accountNumberField.setFont(Font.font("Arial", 13));
        HBox.setHgrow(accountNumberField, Priority.ALWAYS);
        
        Button searchButton = new Button("View Balance");
        searchButton.setStyle(
            "-fx-background-color: #9C27B0;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 20;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        searchButton.setOnAction(e -> viewBalance());
        
        searchBox.getChildren().addAll(accountNumberField, searchButton);
        
        resultBox = new VBox(10);
        resultBox.setAlignment(Pos.CENTER);
        resultBox.setPadding(new Insets(20));
        resultBox.setStyle(
            "-fx-background-color: #f5f5f5;" +
            "-fx-background-radius: 5;"
        );
        
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
        
        container.getChildren().addAll(titleLabel, separator, searchBox, resultBox, closeButton);
        return container;
    }
    
    private void viewBalance() {
        String accountNumber = accountNumberField.getText().trim();
        
        if (accountNumber.isEmpty()) {
            showError("Please enter an account number");
            return;
        }
        
        var result = accountController.getBalance(accountNumber);
        
        if (result.isSuccess()) {
            displayBalance(result.getAccount().getAccountType(), 
                          result.getBalance(), 
                          accountNumber,
                          result.getAccount().getCustomer().getFirstName() + " " + 
                          result.getAccount().getCustomer().getSurname());
        } else {
            showError(result.getMessage());
        }
    }
    
    private void displayBalance(String accountType, double balance, String accountNumber, String customerName) {
        resultBox.getChildren().clear();
        
        Label successLabel = new Label("✓ Account Found");
        successLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        successLabel.setTextFill(Color.web("#4CAF50"));
        
        Label accountTypeLabel = new Label("Account Type: " + accountType);
        accountTypeLabel.setFont(Font.font("Arial", 14));
        
        Label customerLabel = new Label("Customer: " + customerName);
        customerLabel.setFont(Font.font("Arial", 14));
        
        Label balanceLabel = new Label("BWP " + String.format("%.2f", balance));
        balanceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        balanceLabel.setTextFill(Color.web("#9C27B0"));
        
        resultBox.getChildren().addAll(successLabel, new Separator(), 
            accountTypeLabel, customerLabel, balanceLabel);
    }
    
    private void showError(String message) {
        resultBox.getChildren().clear();
        Label errorLabel = new Label("❌ " + message);
        errorLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        errorLabel.setTextFill(Color.RED);
        resultBox.getChildren().add(errorLabel);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}