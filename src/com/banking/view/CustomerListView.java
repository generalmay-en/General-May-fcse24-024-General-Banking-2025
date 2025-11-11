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

public class CustomerListView extends Application {
    private CustomerController customerController;
    private AccountController accountController;
    private TableView<Customer> customerTable;
    private TextArea detailsArea;
    private Stage primaryStage;
    
    public CustomerListView(CustomerController customerController, AccountController accountController) {
        this.customerController = customerController;
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
        
        Scene scene = new Scene(root, 1000, 650);
        primaryStage.setTitle("Customer List");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        loadCustomers();
    }
    
    private VBox createContent() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        Label titleLabel = new Label("👥 Customer List");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#607D8B"));
        
        HBox toolBar = new HBox(10);
        toolBar.setPadding(new Insets(10, 0, 10, 0));
        
        Button refreshButton = new Button("🔄 Refresh");
        refreshButton.setStyle(
            "-fx-background-color: #607D8B;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 20;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        refreshButton.setOnAction(e -> loadCustomers());
        
        Button closeButton = new Button("Close");
        closeButton.setStyle(
            "-fx-background-color: #9E9E9E;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 20;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        closeButton.setOnAction(e -> primaryStage.close());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        toolBar.getChildren().addAll(refreshButton, spacer, closeButton);
        
        customerTable = createCustomerTable();
        
        Label detailsLabel = new Label("Customer Details:");
        detailsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        detailsArea = new TextArea();
        detailsArea.setEditable(false);
        detailsArea.setPrefRowCount(6);
        detailsArea.setWrapText(true);
        detailsArea.setStyle(
            "-fx-control-inner-background: #f9f9f9;" +
            "-fx-font-family: 'Courier New';" +
            "-fx-font-size: 12px;"
        );
        
        container.getChildren().addAll(titleLabel, toolBar, customerTable, detailsLabel, detailsArea);
        VBox.setVgrow(customerTable, Priority.ALWAYS);
        
        return container;
    }
    
    private TableView<Customer> createCustomerTable() {
        TableView<Customer> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Customer, String> idCol = new TableColumn<>("Customer ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        idCol.setPrefWidth(120);
        
        TableColumn<Customer, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstNameCol.setPrefWidth(150);
        
        TableColumn<Customer, String> surnameCol = new TableColumn<>("Surname");
        surnameCol.setCellValueFactory(new PropertyValueFactory<>("surname"));
        surnameCol.setPrefWidth(150);
        
        TableColumn<Customer, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        addressCol.setPrefWidth(200);
        
        TableColumn<Customer, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        phoneCol.setPrefWidth(120);
        
        TableColumn<Customer, String> accountsCol = new TableColumn<>("# Accounts");
        accountsCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                String.valueOf(data.getValue().getAccounts().size())));
        accountsCol.setPrefWidth(100);
        
        table.getColumns().addAll(idCol, firstNameCol, surnameCol, addressCol, phoneCol, accountsCol);
        
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayCustomerDetails(newSelection);
            }
        });
        
        return table;
    }
    
    private void loadCustomers() {
        List<Customer> customers = customerController.getAllCustomers();
        ObservableList<Customer> data = FXCollections.observableArrayList(customers);
        customerTable.setItems(data);
        
        if (customers.isEmpty()) {
            detailsArea.setText("No customers found in the system.");
        } else {
            detailsArea.setText("Click on a customer to view details...");
        }
    }
    
    private void displayCustomerDetails(Customer customer) {
        StringBuilder details = new StringBuilder();
        details.append("═══════════════════════════════════════════════\n");
        details.append(String.format("Customer ID:  %s\n", customer.getCustomerId()));
        details.append(String.format("Name:         %s %s\n", customer.getFirstName(), customer.getSurname()));
        details.append(String.format("Address:      %s\n", customer.getAddress()));
        details.append(String.format("Phone:        %s\n", 
            customer.getPhoneNumber() != null ? customer.getPhoneNumber() : "N/A"));
        details.append(String.format("Email:        %s\n", 
            customer.getEmail() != null ? customer.getEmail() : "N/A"));
        details.append("═══════════════════════════════════════════════\n\n");
        
        List<Account> accounts = accountController.getCustomerAccounts(customer.getCustomerId());
        
        if (accounts.isEmpty()) {
            details.append("No accounts found for this customer.\n");
        } else {
            details.append("ACCOUNTS:\n\n");
            for (Account account : accounts) {
                details.append(String.format("  • %s\n", account.getAccountType()));
                details.append(String.format("    Account Number: %s\n", account.getAccountNumber()));
                details.append(String.format("    Balance:        BWP %.2f\n", account.getBalance()));
                details.append(String.format("    Branch:         %s\n\n", account.getBranch()));
            }
        }
        
        detailsArea.setText(details.toString());
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
