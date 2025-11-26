package com.banking.view;

import com.banking.controller.AccountController;
import com.banking.model.Transaction;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

/**
 * TransactionHistoryView
 * DESIGN FEATURES:
 * - Clean table design with modern styling
 * - Smooth search interface
 * - Color-coded transaction types
 * - Professional cyan theme
 * - Summary statistics
 * Satisfies F-403: Display comprehensive transaction history
 */
public class TransactionHistoryView extends Application {
    // Controller 
    private AccountController accountController;
    
    // UI Components
    private TextField accountNumberField;
    private TableView<Transaction> transactionTable;
    private Label summaryLabel;
    private Stage primaryStage;
    
    /**
     * Constructor
     * @param accountController Account operations controller
     */
    public TransactionHistoryView(AccountController accountController) {
        this.accountController = accountController;
    }
    
    /**
     * Starts the transaction history window
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f8fafc;");
        root.setPadding(new Insets(25));
        
        VBox content = createContent();
        root.setCenter(content);
        
        Scene scene = new Scene(root, 980, 680);
        primaryStage.setTitle("Transaction History");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Creates the main content layout
     * @return VBox containing all elements
     */
    private VBox createContent() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(30));
        container.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );
        
        // Header
        HBox headerBox = new HBox(15);
        headerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        // Icon container
        StackPane iconContainer = new StackPane();
        iconContainer.setPrefSize(60, 60);
        iconContainer.setStyle(
            "-fx-background-color: #cffafe;" +
            "-fx-background-radius: 12;"
        );
        
        Label iconLabel = new Label("📜");
        iconLabel.setFont(Font.font("Arial", 32));
        iconContainer.getChildren().add(iconLabel);
        
        VBox titleBox = new VBox(5);
        Label titleLabel = new Label("Transaction History");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        titleLabel.setTextFill(Color.web("#06b6d4"));
        
        Label subtitleLabel = new Label("View complete transaction records");
        subtitleLabel.setFont(Font.font("Arial", 13));
        subtitleLabel.setTextFill(Color.web("#64748b"));
        
        titleBox.getChildren().addAll(titleLabel, subtitleLabel);
        headerBox.getChildren().addAll(iconContainer, titleBox);
        
        // Search 
        HBox searchBox = new HBox(12);
        searchBox.setPadding(new Insets(15, 0, 15, 0));
        searchBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label searchLabel = new Label("Account:");
        searchLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
        searchLabel.setTextFill(Color.web("#1e293b"));
        
        accountNumberField = new TextField();
        accountNumberField.setPromptText("Enter account number");
        accountNumberField.setPrefWidth(250);
        accountNumberField.setPrefHeight(40);
        accountNumberField.setStyle(
            "-fx-padding: 10 15;" +
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
                    "-fx-border-color: #06b6d4; -fx-background-color: white;"
                );
            } else {
                accountNumberField.setStyle(
                    "-fx-padding: 10 15;" +
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
        accountNumberField.setOnAction(e -> loadTransactionHistory());
        
        Button searchButton = new Button("Load History");
        searchButton.setPrefHeight(40);
        searchButton.setStyle(
            "-fx-background-color: #06b6d4;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 10 25;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(6, 182, 212, 0.3), 8, 0, 0, 3);"
        );
        
        searchButton.setOnMouseEntered(e ->
            searchButton.setStyle(
                "-fx-background-color: #0891b2;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 10 25;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(6, 182, 212, 0.5), 12, 0, 0, 5);"
            )
        );
        
        searchButton.setOnMouseExited(e ->
            searchButton.setStyle(
                "-fx-background-color: #06b6d4;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 10 25;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(6, 182, 212, 0.3), 8, 0, 0, 3);"
            )
        );
        
        searchButton.setOnAction(e -> loadTransactionHistory());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button closeButton = new Button("Close");
        closeButton.setPrefHeight(40);
        closeButton.setStyle(
            "-fx-background-color: #94a3b8;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 10 20;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
        closeButton.setOnMouseEntered(e ->
            closeButton.setStyle(
                "-fx-background-color: #64748b;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 10 20;" +
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
                "-fx-padding: 10 20;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            )
        );
        
        closeButton.setOnAction(e -> primaryStage.close());
        
        searchBox.getChildren().addAll(searchLabel, accountNumberField, searchButton, spacer, closeButton);
        
        // Transaction table
        transactionTable = createTransactionTable();
        VBox.setVgrow(transactionTable, Priority.ALWAYS);
        
        // Summary
        summaryLabel = new Label();
        summaryLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
        summaryLabel.setTextFill(Color.web("#475569"));
        summaryLabel.setPadding(new Insets(10, 0, 0, 0));
        
        // Add all components
        container.getChildren().addAll(headerBox, searchBox, transactionTable, summaryLabel);
        
        return container;
    }
    
    /**
     * Creates the styled transaction table
     * @return Configured TableView
     */
    private TableView<Transaction> createTransactionTable() {
        TableView<Transaction> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;"
        );
        
        Label placeholder = new Label("Enter an account number and click 'Load History'");
        placeholder.setFont(Font.font("Arial", 13));
        placeholder.setTextFill(Color.web("#94a3b8"));
        table.setPlaceholder(placeholder);
        
        // Date & Time Column
        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date & Time");
        dateCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getFormattedTimestamp()));
        dateCol.setPrefWidth(160);
        dateCol.setStyle("-fx-alignment: CENTER-LEFT; -fx-font-size: 12px;");
        
        // Type Column
        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("transactionType"));
        typeCol.setPrefWidth(110);
        typeCol.setStyle("-fx-alignment: CENTER; -fx-font-size: 12px; -fx-font-weight: bold;");
        
        // Custom cell factory for colored transaction types
        typeCol.setCellFactory(column -> new TableCell<Transaction, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("DEPOSIT")) {
                        setTextFill(Color.web("#10b981"));
                        setStyle("-fx-font-weight: bold; -fx-alignment: CENTER;");
                    } else if (item.equals("WITHDRAWAL")) {
                        setTextFill(Color.web("#ef4444"));
                        setStyle("-fx-font-weight: bold; -fx-alignment: CENTER;");
                    } else {
                        setTextFill(Color.web("#06b6d4"));
                        setStyle("-fx-font-weight: bold; -fx-alignment: CENTER;");
                    }
                }
            }
        });
        
        // Amount Column
        TableColumn<Transaction, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                String.format("BWP %.2f", data.getValue().getAmount())));
        amountCol.setPrefWidth(130);
        amountCol.setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-size: 12px; -fx-font-weight: bold;");
        
        // Balance After Column
        TableColumn<Transaction, String> balanceCol = new TableColumn<>("Balance After");
        balanceCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                String.format("BWP %.2f", data.getValue().getBalanceAfter())));
        balanceCol.setPrefWidth(130);
        balanceCol.setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-size: 12px;");
        
        // Description Column
        TableColumn<Transaction, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("description"));
        descCol.setPrefWidth(300);
        descCol.setStyle("-fx-alignment: CENTER-LEFT; -fx-font-size: 12px;");
        
        table.getColumns().addAll(dateCol, typeCol, amountCol, balanceCol, descCol);
        
        return table;
    }
    
    /**
     * Loads transaction history for the specified account
     */
    private void loadTransactionHistory() {
        String accountNumber = accountNumberField.getText().trim();
        
        if (accountNumber.isEmpty()) {
            showAlert("Validation Error", "Please enter an account number");
            return;
        }
        
        // Call controller 
        List<Transaction> transactions = accountController.getTransactionHistory(accountNumber);
        
        if (transactions.isEmpty()) {
            Label emptyLabel = new Label("No transactions found for this account");
            emptyLabel.setFont(Font.font("Arial", 13));
            emptyLabel.setTextFill(Color.web("#94a3b8"));
            transactionTable.setPlaceholder(emptyLabel);
            summaryLabel.setText("");
        } else {
            ObservableList<Transaction> data = FXCollections.observableArrayList(transactions);
            transactionTable.setItems(data);
            
            summaryLabel.setText(String.format(
                "📊 Total Transactions: %d | Account: %s", 
                transactions.size(), 
                accountNumber
            ));
            summaryLabel.setStyle(
                "-fx-background-color: #f0fdfa;" +
                "-fx-padding: 10;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #99f6e4;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 8;"
            );
        }
    }
    
    /**
     * Shows an alert dialog
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        launch(args);
    }
}