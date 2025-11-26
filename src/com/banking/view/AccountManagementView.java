package com.banking.view;

import com.banking.controller.AccountController;
import com.banking.controller.CustomerController;
import com.banking.model.Customer;
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
 * AccountManagementView  
 * DESIGN FEATURES:
 * - Clean, intuitive form layout
 * - Dynamic fields based on account type
 * - Enhanced validation feedback
 * 
 * Handles all three account types: Savings, Investment, Cheque
 * 
 * @author General May
 * @version Redesigned UI
 */
public class AccountManagementView extends Application {
    // Controllers 
    private AccountController accountController;
    private CustomerController customerController;
    
    // UI Components
    private TextField customerIdField;
    private ComboBox<String> accountTypeCombo;
    private TextField initialBalanceField;
    private TextField branchField;
    private TextField companyNameField;
    private TextField companyAddressField;
    private VBox employmentBox;
    private Label messageLabel;
    private Label minBalanceLabel;
    private Label customerInfoLabel;
    private Stage primaryStage;
    
    /**
     * Constructor
     * @param accountController Controller for account operations
     * @param customerController Controller for customer operations
     */
    public AccountManagementView(AccountController accountController, CustomerController customerController) {
        this.accountController = accountController;
        this.customerController = customerController;
    }
    
    /**
     * Starts the account management window
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #404244ff;");
        root.setPadding(new Insets(25));
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(createAccountForm());
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        
        root.setCenter(scrollPane);
        
        Scene scene = new Scene(root, 680, 780);
        primaryStage.setTitle("Open New Account");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Creates the complete account opening form
     * @return VBox containing all form elements
     */
    private VBox createAccountForm() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.TOP_CENTER);
        container.setPadding(new Insets(35));
        container.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );
        container.setMaxWidth(520);
        
        // Header
        VBox headerBox = new VBox(8);
        headerBox.setAlignment(Pos.CENTER);
        
        Label iconLabel = new Label("🏦");
        iconLabel.setFont(Font.font("Arial", 40));
        
        Label titleLabel = new Label("Open New Account");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        titleLabel.setTextFill(Color.web("#3b82f6"));
        
        Label subtitleLabel = new Label("Select account type and enter details");
        subtitleLabel.setFont(Font.font("Arial", 13));
        subtitleLabel.setTextFill(Color.web("#64748b"));
        
        headerBox.getChildren().addAll(iconLabel, titleLabel, subtitleLabel);
        
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));
        
        // Customer lookup
        VBox customerSection = new VBox(10);
        
        Label customerLabel = new Label("Customer Lookup *");
        customerLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
        customerLabel.setTextFill(Color.web("#1e293b"));
        
        HBox customerBox = new HBox(10);
        customerIdField = new TextField();
        customerIdField.setPromptText("Enter customer ID");
        customerIdField.setPrefHeight(42);
        styleTextField(customerIdField);
        HBox.setHgrow(customerIdField, Priority.ALWAYS);
        
        Button searchButton = new Button("🔍 Search");
        searchButton.setPrefHeight(42);
        searchButton.setStyle(
            "-fx-background-color: #3b82f6;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 10 20;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
        searchButton.setOnMouseEntered(e ->
            searchButton.setStyle(
                "-fx-background-color: #2563eb;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 10 20;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            )
        );
        
        searchButton.setOnMouseExited(e ->
            searchButton.setStyle(
                "-fx-background-color: #3b82f6;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 10 20;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            )
        );
        
        searchButton.setOnAction(e -> searchCustomer());
        
        customerBox.getChildren().addAll(customerIdField, searchButton);
        
        // Customer info display
        customerInfoLabel = new Label();
        customerInfoLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 12));
        customerInfoLabel.setWrapText(true);
        customerInfoLabel.setMaxWidth(480);
        
        customerSection.getChildren().addAll(customerLabel, customerBox, customerInfoLabel);
        
        // Account type
        VBox accountTypeBox = new VBox(8);
        Label accountTypeLabel = new Label("Account Type *");
        accountTypeLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
        accountTypeLabel.setTextFill(Color.web("#1e293b"));
        
        accountTypeCombo = new ComboBox<>();
        accountTypeCombo.getItems().addAll("Savings Account", "Investment Account", "Cheque Account");
        accountTypeCombo.setPromptText("Select account type");
        accountTypeCombo.setMaxWidth(Double.MAX_VALUE);
        accountTypeCombo.setPrefHeight(42);
        styleComboBox(accountTypeCombo);
        accountTypeCombo.setOnAction(e -> handleAccountTypeChange());
        
        accountTypeBox.getChildren().addAll(accountTypeLabel, accountTypeCombo);
        
        // Initial Balance
        VBox balanceBox = new VBox(8);
        Label balanceLabel = new Label("Initial Balance (BWP) *");
        balanceLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
        balanceLabel.setTextFill(Color.web("#1e293b"));
        
        initialBalanceField = new TextField();
        initialBalanceField.setPromptText("Enter opening deposit amount");
        initialBalanceField.setPrefHeight(42);
        styleTextField(initialBalanceField);
        
        minBalanceLabel = new Label();
        minBalanceLabel.setFont(Font.font("Arial", 12));
        minBalanceLabel.setTextFill(Color.web("#f59e0b"));
        minBalanceLabel.setWrapText(true);
        
        balanceBox.getChildren().addAll(balanceLabel, initialBalanceField, minBalanceLabel);
        
        //  Branch 
        VBox branchBox = new VBox(8);
        Label branchLabel = new Label("Branch Location *");
        branchLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
        branchLabel.setTextFill(Color.web("#1e293b"));
        
        branchField = new TextField();
        branchField.setText("Gaborone Main");
        branchField.setPromptText("Enter branch name");
        branchField.setPrefHeight(42);
        styleTextField(branchField);
        
        branchBox.getChildren().addAll(branchLabel, branchField);
        
        // Employment field for cheque accounts only.
        employmentBox = new VBox(15);
        employmentBox.setVisible(false);
        employmentBox.setManaged(false);
        employmentBox.setStyle(
            "-fx-background-color: #eff6ff;" +
            "-fx-padding: 15;" +
            "-fx-background-radius: 10;"
        );
        
        Label employmentTitle = new Label("📋 Employment Information");
        employmentTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        employmentTitle.setTextFill(Color.web("#3b82f6"));
        
        VBox companyNameBox = new VBox(8);
        Label companyNameLabel = new Label("Company Name *");
        companyNameLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
        companyNameLabel.setTextFill(Color.web("#1e293b"));
        
        companyNameField = new TextField();
        companyNameField.setPromptText("Employer's company name");
        companyNameField.setPrefHeight(42);
        styleTextField(companyNameField);
        
        companyNameBox.getChildren().addAll(companyNameLabel, companyNameField);
        
        VBox companyAddressBox = new VBox(8);
        Label companyAddressLabel = new Label("Company Address *");
        companyAddressLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
        companyAddressLabel.setTextFill(Color.web("#1e293b"));
        
        companyAddressField = new TextField();
        companyAddressField.setPromptText("Employer's business address");
        companyAddressField.setPrefHeight(42);
        styleTextField(companyAddressField);
        
        companyAddressBox.getChildren().addAll(companyAddressLabel, companyAddressField);
        
        employmentBox.getChildren().addAll(
            employmentTitle,
            companyNameBox,
            companyAddressBox
        );
        
        // Message label.
        messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(480);
        messageLabel.setAlignment(Pos.CENTER);
        
        //Buttons
        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        Button openButton = new Button("Open Account");
        openButton.setPrefWidth(150);
        openButton.setPrefHeight(42);
        openButton.setStyle(
            "-fx-background-color: #3b82f6;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.3), 8, 0, 0, 3);"
        );
        
        openButton.setOnMouseEntered(e ->
            openButton.setStyle(
                "-fx-background-color: #2563eb;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.5), 12, 0, 0, 5);"
            )
        );
        
        openButton.setOnMouseExited(e ->
            openButton.setStyle(
                "-fx-background-color: #3b82f6;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.3), 8, 0, 0, 3);"
            )
        );
        
        openButton.setOnAction(e -> handleOpenAccount());
        
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
        
        buttonBox.getChildren().addAll(openButton, clearButton, closeButton);
        
        // Add all components
        container.getChildren().addAll(
            headerBox,
            separator,
            customerSection,
            accountTypeBox,
            balanceBox,
            branchBox,
            employmentBox,
            messageLabel,
            buttonBox
        );
        
        return container;
    }
    
    /**
     * Applies styling to text fields
     */
    private void styleTextField(TextField field) {
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
                    "-fx-border-color: #3b82f6; -fx-background-color: white;"
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
     * Applies styling to combo boxes
     */
    private void styleComboBox(ComboBox<?> comboBox) {
        comboBox.setStyle(
            "-fx-padding: 8 12;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 8;" +
            "-fx-font-size: 13px;" +
            "-fx-background-color: #f8fafc;"
        );
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
     * Handles account type selection changes
     */
    private void handleAccountTypeChange() {
        String selectedType = accountTypeCombo.getValue();
        
        if (selectedType == null) return;
        
        // Show/hide employment fields for Cheque accounts
        boolean isCheque = selectedType.equals("Cheque Account");
        employmentBox.setVisible(isCheque);
        employmentBox.setManaged(isCheque);
        
        // Show minimum balance warning for Investment accounts
        if (selectedType.equals("Investment Account")) {
            minBalanceLabel.setText("⚠️ Minimum opening balance: BWP 500.00");
            minBalanceLabel.setStyle(
                "-fx-background-color: #fef3c7;" +
                "-fx-padding: 8;" +
                "-fx-background-radius: 6;"
            );
        } else {
            minBalanceLabel.setText("");
            minBalanceLabel.setStyle("");
        }
    }
    
    /**
     * Searches for customer by ID
     */
    private void searchCustomer() {
        String customerId = customerIdField.getText().trim();
        
        if (customerId.isEmpty()) {
            showError("Please enter a customer ID");
            return;
        }
        
        var result = customerController.getCustomer(customerId);
        
        if (result.isSuccess()) {
            Customer customer = result.getCustomer();
            customerInfoLabel.setText("✓ Customer: " + customer.getFirstName() + 
                " " + customer.getSurname());
            customerInfoLabel.setTextFill(Color.web("#10b981"));
            customerInfoLabel.setStyle(
                "-fx-background-color: #d1fae5;" +
                "-fx-padding: 8;" +
                "-fx-background-radius: 6;"
            );
        } else {
            customerInfoLabel.setText("✕ " + result.getMessage());
            customerInfoLabel.setTextFill(Color.web("#ef4444"));
            customerInfoLabel.setStyle(
                "-fx-background-color: #fee2e2;" +
                "-fx-padding: 8;" +
                "-fx-background-radius: 6;"
            );
        }
    }
    
    /**
     * Handles account opening
     */
    private void handleOpenAccount() {
        messageLabel.setText("");
        
        String customerId = customerIdField.getText().trim();
        String accountType = accountTypeCombo.getValue();
        String balanceStr = initialBalanceField.getText().trim();
        String branch = branchField.getText().trim();
        
        // Validation
        if (customerId.isEmpty() || accountType == null || balanceStr.isEmpty() || branch.isEmpty()) {
            showError("Please fill in all required fields");
            return;
        }
        
        double initialBalance;
        try {
            initialBalance = Double.parseDouble(balanceStr);
        } catch (NumberFormatException e) {
            showError("Invalid balance amount. Please enter a valid number");
            return;
        }
        
        // Open account based on type
        var result = switch (accountType) {
            case "Savings Account" -> 
                accountController.openSavingsAccount(customerId, initialBalance, branch);
            case "Investment Account" -> 
                accountController.openInvestmentAccount(customerId, initialBalance, branch);
            case "Cheque Account" -> {
                String companyName = companyNameField.getText().trim();
                String companyAddress = companyAddressField.getText().trim();
                
                if (companyName.isEmpty() || companyAddress.isEmpty()) {
                    showError("Company name and address are required for Cheque accounts");
                    yield null;
                }
                
                yield accountController.openChequeAccount(customerId, initialBalance, branch, 
                    companyName, companyAddress);
            }
            default -> null;
        };
        
        if (result != null && result.isSuccess()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Account Opened Successfully!");
            alert.setContentText(
                "Account Number: " + result.getAccount().getAccountNumber() + "\n" +
                "Account Type: " + result.getAccount().getAccountType() + "\n" +
                "Initial Balance: BWP " + String.format("%.2f", result.getAccount().getBalance())
            );
            alert.showAndWait();
            
            clearForm();
        } else if (result != null) {
            showError(result.getMessage());
        }
    }
    
    /**
     * Clears all form fields
     */
    private void clearForm() {
        customerIdField.clear();
        accountTypeCombo.setValue(null);
        initialBalanceField.clear();
        branchField.setText("Gaborone Main");
        companyNameField.clear();
        companyAddressField.clear();
        messageLabel.setText("");
        minBalanceLabel.setText("");
        customerInfoLabel.setText("");
        employmentBox.setVisible(false);
        employmentBox.setManaged(false);
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