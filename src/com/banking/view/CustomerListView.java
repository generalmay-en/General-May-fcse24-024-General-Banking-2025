package com.banking.view;

import com.banking.controller.AccountController;
import com.banking.controller.CustomerController;
import com.banking.model.Account;
import com.banking.model.Customer;
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
 * CustomerListView 
 * DESIGN FEATURES:
 * - Clean table design with selection
 * - Detailed customer information panel
 * - Account summary cards
 * - Refresh functionality
 */
public class CustomerListView extends Application {
    // Controllers 
    private CustomerController customerController;
    private AccountController accountController;
    
    // UI Components
    private TableView<Customer> customerTable;
    private TextArea detailsArea;
    private Stage primaryStage;
    
    /**
     * Constructor
     * @param customerController Customer operations controller
     * @param accountController Account operations controller
     */
    public CustomerListView(CustomerController customerController, AccountController accountController) {
        this.customerController = customerController;
        this.accountController = accountController;
    }
    
    /**
     * Starts the customer list window
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #3f4246ff;");
        root.setPadding(new Insets(25));
        
        VBox content = createContent();
        root.setCenter(content);
        
        Scene scene = new Scene(root, 1050, 720);
        primaryStage.setTitle("Customer Directory");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Load customers on start
        loadCustomers();
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
            "-fx-background-color: #e2e8f0;" +
            "-fx-background-radius: 12;"
        );
        
        Label iconLabel = new Label("👥");
        iconLabel.setFont(Font.font("Arial", 32));
        iconContainer.getChildren().add(iconLabel);
        
        VBox titleBox = new VBox(5);
        Label titleLabel = new Label("👥Customer Directory");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        titleLabel.setTextFill(Color.web("#64748b"));
        
        Label subtitleLabel = new Label("View all registered.");
        subtitleLabel.setFont(Font.font("Arial", 13));
        subtitleLabel.setTextFill(Color.web("#94a3b8"));
        
        titleBox.getChildren().addAll(titleLabel, subtitleLabel);
        headerBox.getChildren().addAll(iconContainer, titleBox);
        
        // Toolbar
        HBox toolBar = new HBox(12);
        toolBar.setPadding(new Insets(15, 0, 15, 0));
        toolBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Button refreshButton = new Button("🔄 Refresh");
        refreshButton.setPrefHeight(38);
        refreshButton.setStyle(
            "-fx-background-color: #64748b;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 8 20;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
        refreshButton.setOnMouseEntered(e ->
            refreshButton.setStyle(
                "-fx-background-color: #475569;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 8 20;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(100, 116, 139, 0.4), 8, 0, 0, 3);"
            )
        );
        
        refreshButton.setOnMouseExited(e ->
            refreshButton.setStyle(
                "-fx-background-color: #64748b;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 8 20;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            )
        );
        
        refreshButton.setOnAction(e -> loadCustomers());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button closeButton = new Button("Close");
        closeButton.setPrefHeight(38);
        closeButton.setStyle(
            "-fx-background-color: #94a3b8;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 8 20;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
        closeButton.setOnMouseEntered(e ->
            closeButton.setStyle(
                "-fx-background-color: #64748b;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 8 20;" +
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
                "-fx-padding: 8 20;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            )
        );
        
        closeButton.setOnAction(e -> primaryStage.close());
        
        toolBar.getChildren().addAll(refreshButton, spacer, closeButton);
        
        // Customer table
        customerTable = createCustomerTable();
        VBox.setVgrow(customerTable, Priority.ALWAYS);
        
        // Deatails section
        VBox detailsSection = new VBox(10);
        
        Label detailsLabel = new Label("📋 Customer Details");
        detailsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        detailsLabel.setTextFill(Color.web("#334155"));
        
        detailsArea = new TextArea();
        detailsArea.setEditable(false);
        detailsArea.setPrefRowCount(7);
        detailsArea.setWrapText(true);
        detailsArea.setStyle(
            "-fx-control-inner-background: #f8fafc;" +
            "-fx-font-family: 'Courier New';" +
            "-fx-font-size: 12px;" +
            "-fx-text-fill: #1e293b;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;"
        );
        detailsArea.setText("Select a customer from the table above to view details...");
        
        detailsSection.getChildren().addAll(detailsLabel, detailsArea);
        
        // Add all components
        container.getChildren().addAll(headerBox, toolBar, customerTable, detailsSection);
        
        return container;
    }
    
    /**
     * Creates the styled customer table
     * @return Configured TableView
     */
    private TableView<Customer> createCustomerTable() {
        TableView<Customer> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;"
        );
        
        // Customer ID Column
        TableColumn<Customer, String> idCol = new TableColumn<>("Customer ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        idCol.setPrefWidth(130);
        idCol.setStyle("-fx-alignment: CENTER; -fx-font-size: 12px; -fx-font-weight: bold;");
        
        // First Name Column
        TableColumn<Customer, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstNameCol.setPrefWidth(150);
        firstNameCol.setStyle("-fx-alignment: CENTER-LEFT; -fx-font-size: 12px;");
        
        // Surname Column
        TableColumn<Customer, String> surnameCol = new TableColumn<>("Surname");
        surnameCol.setCellValueFactory(new PropertyValueFactory<>("surname"));
        surnameCol.setPrefWidth(150);
        surnameCol.setStyle("-fx-alignment: CENTER-LEFT; -fx-font-size: 12px;");
        
        // Address Column
        TableColumn<Customer, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        addressCol.setPrefWidth(220);
        addressCol.setStyle("-fx-alignment: CENTER-LEFT; -fx-font-size: 12px;");
        
        // Phone Column
        TableColumn<Customer, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        phoneCol.setPrefWidth(120);
        phoneCol.setStyle("-fx-alignment: CENTER; -fx-font-size: 12px;");
        
        // Accounts Count Column
        TableColumn<Customer, String> accountsCol = new TableColumn<>("Accounts");
        accountsCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                String.valueOf(data.getValue().getAccounts().size())));
        accountsCol.setPrefWidth(100);
        accountsCol.setStyle("-fx-alignment: CENTER; -fx-font-size: 12px; -fx-font-weight: bold;");
        
        // Color code account count
        accountsCol.setCellFactory(column -> new TableCell<Customer, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    int count = Integer.parseInt(item);
                    if (count == 0) {
                        setTextFill(Color.web("#94a3b8"));
                    } else if (count <= 2) {
                        setTextFill(Color.web("#10b981"));
                    } else {
                        setTextFill(Color.web("#3b82f6"));
                    }
                    setStyle("-fx-font-weight: bold; -fx-alignment: CENTER;");
                }
            }
        });
        
        table.getColumns().addAll(idCol, firstNameCol, surnameCol, addressCol, phoneCol, accountsCol);
        
        // Selection listener
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayCustomerDetails(newSelection);
            }
        });
        
        return table;
    }
    
    /**
     * Loads all customers from the controller
     */
    private void loadCustomers() {
        // Call controller
        List<Customer> customers = customerController.getAllCustomers();
        ObservableList<Customer> data = FXCollections.observableArrayList(customers);
        customerTable.setItems(data);
        
        if (customers.isEmpty()) {
            detailsArea.setText("ℹ️ No customers found in the system.\n\nRegister new customers to see them here.");
        } else {
            detailsArea.setText("✓ " + customers.size() + " customer(s) loaded.\n\nClick on a customer to view details...");
        }
    }
    
    /**
     * Displays detailed customer information
     * @param customer Selected customer
     */
    private void displayCustomerDetails(Customer customer) {
        StringBuilder details = new StringBuilder();
        
        // Customer header
        details.append("═══════════════════════════════════════════════════\n");
        details.append("              👥  CUSTOMER INFORMATION\n");
        details.append("—————————————————————————————————————————————————————————\n\n");
        
        // Basic info
        details.append(String.format("  Customer ID:  %s\n", customer.getCustomerId()));
        details.append(String.format("  Full Name:    %s %s\n", customer.getFirstName(), customer.getSurname()));
        details.append(String.format("  Address:      %s\n", customer.getAddress()));
        details.append(String.format("  Phone:        %s\n", 
            customer.getPhoneNumber() != null ? customer.getPhoneNumber() : "Not provided"));
        details.append(String.format("  Email:        %s\n", 
            customer.getEmail() != null ? customer.getEmail() : "Not provided"));
        
        details.append("\n───────────────────────────────────────────────────\n\n");
        
        // Get accounts
        List<Account> accounts = accountController.getCustomerAccounts(customer.getCustomerId());
        
        if (accounts.isEmpty()) {
            details.append("  ℹ️  No accounts opened yet for this customer.\n");
        } else {
            details.append("  BANK ACCOUNTS (" + accounts.size() + "):\n\n");
            
            for (int i = 0; i < accounts.size(); i++) {
                Account account = accounts.get(i);
                details.append(String.format("  %d. %s\n", (i + 1), account.getAccountType()));
                details.append(String.format("     Account No:  %s\n", account.getAccountNumber()));
                details.append(String.format("     Balance:     BWP %.2f\n", account.getBalance()));
                details.append(String.format("     Branch:      %s\n", account.getBranch()));
                
                if (i < accounts.size() - 1) {
                    details.append("\n");
                }
            }
        }
        
        details.append("\n═══════════════════════════════════════════════════");
        
        detailsArea.setText(details.toString());
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        launch(args);
    }
}