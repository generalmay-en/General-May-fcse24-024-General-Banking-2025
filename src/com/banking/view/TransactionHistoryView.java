package com.banking.view;

import com.banking.controller.AccountController;
import com.banking.model.Transaction;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

/**
 * TransactionHistoryView - Displays transaction history for an account.
 * Satisfies F-403: Display comprehensive transaction history.
 */
public class TransactionHistoryView extends Application {
    private AccountController accountController;
    private TextField accountNumberField;
    private TableView<Transaction> transactionTable;
    private Label summaryLabel;
    private Stage primaryStage;
    
    public TransactionHistoryView(AccountController accountController) {
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
        
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Transaction History");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private VBox createContent() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        Label titleLabel = new Label("📜 Transaction History");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#00BCD4"));
        
        HBox searchBox = new HBox(10);
        searchBox.setPadding(new Insets(10, 0, 10, 0));
        
        accountNumberField = new TextField();
        accountNumberField.setPromptText("Enter account number");
        accountNumberField.setPrefWidth(250);
        accountNumberField.setStyle(
            "-fx-padding: 10;" +
            "-fx-background-radius: 5;" +
            "-fx-border-color: #ddd;" +
            "-fx-border-radius: 5;"
        );
        
        Button searchButton = new Button("Load History");
        searchButton.setStyle(
            "-fx-background-color: #00BCD4;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 20;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        searchButton.setOnAction(e -> loadTransactionHistory());
        
        Button closeButton = new Button("Close");
        closeButton.setStyle(
            "-fx-background-color: #607D8B;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 20;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        closeButton.setOnAction(e -> primaryStage.close());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        searchBox.getChildren().addAll(accountNumberField, searchButton, spacer, closeButton);
        
        // Create table
        transactionTable = createTransactionTable();
        
        summaryLabel = new Label();
        summaryLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        summaryLabel.setTextFill(Color.GRAY);
        
        container.getChildren().addAll(titleLabel, searchBox, transactionTable, summaryLabel);
        VBox.setVgrow(transactionTable, Priority.ALWAYS);
        
        return container;
    }
    
    private TableView<Transaction> createTransactionTable() {
        TableView<Transaction> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("Enter an account number and click 'Load History'"));
        
        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date & Time");
        dateCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getFormattedTimestamp()));
        dateCol.setPrefWidth(150);
        
        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        typeCol.setPrefWidth(100);
        
        TableColumn<Transaction, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                String.format("BWP %.2f", data.getValue().getAmount())));
        amountCol.setPrefWidth(120);
        
        TableColumn<Transaction, String> balanceCol = new TableColumn<>("Balance After");
        balanceCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                String.format("BWP %.2f", data.getValue().getBalanceAfter())));
        balanceCol.setPrefWidth(120);
        
        TableColumn<Transaction, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(300);
        
        table.getColumns().addAll(dateCol, typeCol, amountCol, balanceCol, descCol);
        
        return table;
    }
    
    private void loadTransactionHistory() {
        String accountNumber = accountNumberField.getText().trim();
        
        if (accountNumber.isEmpty()) {
            showAlert("Error", "Please enter an account number");
            return;
        }
        
        List<Transaction> transactions = accountController.getTransactionHistory(accountNumber);
        
        if (transactions.isEmpty()) {
            transactionTable.setPlaceholder(new Label("No transactions found for this account"));
            summaryLabel.setText("");
        } else {
            ObservableList<Transaction> data = FXCollections.observableArrayList(transactions);
            transactionTable.setItems(data);
            
            summaryLabel.setText(String.format(
                "Total Transactions: %d | Account: %s", 
                transactions.size(), accountNumber
            ));
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}