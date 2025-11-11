package com.banking.view;

import com.banking.controller.LoginController;
import com.banking.controller.CustomerController;
import com.banking.controller.AccountController;
import com.banking.model.Bank;
import com.banking.model.User;
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
 * DashboardView - Main navigation screen after login.
 * Provides access to all banking operations.
 */
public class DashboardView extends Application {
    private LoginController loginController;
    private CustomerController customerController;
    private AccountController accountController;
    private Stage primaryStage;
    private Label userInfoLabel;
    private Bank bank;
    
    public DashboardView() {
        this.loginController = LoginController.getInstance();
        this.bank = new Bank("Botswana Accountancy College Bank", "BAC");
        this.customerController = new CustomerController(bank);
        this.accountController = new AccountController(bank);
    }
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");
        
        // Top bar
        root.setTop(createTopBar());
        
        // Main content
        root.setCenter(createMainContent());
        
        // Scene
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("Banking System - Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Handle window close
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            handleLogout();
        });
    }
    
    /**
     * Creates the top navigation bar.
     */
    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setStyle("-fx-background-color: #667eea; -fx-padding: 15;");
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setSpacing(20);
        
        // Title
        Label titleLabel = new Label("🏦 Banking System");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.WHITE);
        
        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // User info
        User currentUser = loginController.getCurrentUser();
        String username = currentUser != null ? currentUser.getUsername() : "User";
        String role = currentUser != null ? currentUser.getRole() : "TELLER";
        
        userInfoLabel = new Label("👤 " + username + " (" + role + ")");
        userInfoLabel.setFont(Font.font("Arial", 14));
        userInfoLabel.setTextFill(Color.WHITE);
        
        // Logout button
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle(
            "-fx-background-color: #ff6b6b;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 20;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        logoutButton.setOnAction(e -> handleLogout());
        
        topBar.getChildren().addAll(titleLabel, spacer, userInfoLabel, logoutButton);
        return topBar;
    }
    
    /**
     * Creates the main dashboard content with operation buttons.
     */
    private VBox createMainContent() {
        VBox content = new VBox(30);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(40));
        
        // Welcome message
        Label welcomeLabel = new Label("Welcome to the Banking System");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        
        Label subtitleLabel = new Label("Select an operation to continue");
        subtitleLabel.setFont(Font.font("Arial", 16));
        subtitleLabel.setTextFill(Color.GRAY);
        
        // Operations grid
        GridPane operationsGrid = new GridPane();
        operationsGrid.setHgap(20);
        operationsGrid.setVgap(20);
        operationsGrid.setAlignment(Pos.CENTER);
        operationsGrid.setMaxWidth(800);
        
        // Row 1
        operationsGrid.add(createOperationCard("👤 Register Customer", 
            "Register a new customer", "#4CAF50", this::openCustomerRegistration), 0, 0);
        operationsGrid.add(createOperationCard("🏦 Open Account", 
            "Open new account", "#2196F3", this::openAccountManagement), 1, 0);
        operationsGrid.add(createOperationCard("💰 Make Deposit", 
            "Deposit funds", "#FF9800", this::openDepositView), 2, 0);
        
        // Row 2
        operationsGrid.add(createOperationCard("💸 Make Withdrawal", 
            "Withdraw funds", "#f44336", this::openWithdrawalView), 0, 1);
        operationsGrid.add(createOperationCard("📊 View Balance", 
            "Check account balance", "#9C27B0", this::openBalanceView), 1, 1);
        operationsGrid.add(createOperationCard("📜 Transaction History", 
            "View transactions", "#00BCD4", this::openTransactionHistory), 2, 1);
        
        // Row 3 - Admin functions
        if (loginController.hasPermission("OVERRIDE_LIMIT")) {
            operationsGrid.add(createOperationCard("💹 Process Interest", 
                "Monthly interest", "#795548", this::processMonthlyInterest), 0, 2);
        }
        
        operationsGrid.add(createOperationCard("👥 View Customers", 
            "Customer list", "#607D8B", this::openCustomerList), 1, 2);
        
        content.getChildren().addAll(welcomeLabel, subtitleLabel, operationsGrid);
        return content;
    }
    
    /**
     * Creates a styled operation card/button.
     */
    private VBox createOperationCard(String title, String description, String color, Runnable action) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(240, 140);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        card.setPadding(new Insets(20));
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.web(color));
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(Pos.CENTER);
        
        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("Arial", 12));
        descLabel.setTextFill(Color.GRAY);
        
        card.getChildren().addAll(titleLabel, descLabel);
        
        // Hover effect
        card.setOnMouseEntered(e -> 
            card.setStyle(card.getStyle() + "-fx-background-color: " + color + "0d;")
        );
        card.setOnMouseExited(e -> 
            card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: " + color + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
            )
        );
        
        card.setOnMouseClicked(e -> action.run());
        
        return card;
    }
    
    // Navigation methods
    
    private void openCustomerRegistration() {
        CustomerRegistrationView view = new CustomerRegistrationView(customerController);
        Stage stage = new Stage();
        try {
            view.start(stage);
        } catch (Exception e) {
            showAlert("Error", "Could not open customer registration: " + e.getMessage());
        }
    }
    
    private void openAccountManagement() {
        AccountManagementView view = new AccountManagementView(accountController, customerController);
        Stage stage = new Stage();
        try {
            view.start(stage);
        } catch (Exception e) {
            showAlert("Error", "Could not open account management: " + e.getMessage());
        }
    }
    
    private void openDepositView() {
        TransactionView view = new TransactionView(accountController, TransactionView.TransactionType.DEPOSIT);
        Stage stage = new Stage();
        try {
            view.start(stage);
        } catch (Exception e) {
            showAlert("Error", "Could not open deposit view: " + e.getMessage());
        }
    }
    
    private void openWithdrawalView() {
        TransactionView view = new TransactionView(accountController, TransactionView.TransactionType.WITHDRAWAL);
        Stage stage = new Stage();
        try {
            view.start(stage);
        } catch (Exception e) {
            showAlert("Error", "Could not open withdrawal view: " + e.getMessage());
        }
    }
    
    private void openBalanceView() {
        BalanceView view = new BalanceView(accountController);
        Stage stage = new Stage();
        try {
            view.start(stage);
        } catch (Exception e) {
            showAlert("Error", "Could not open balance view: " + e.getMessage());
        }
    }
    
    private void openTransactionHistory() {
        TransactionHistoryView view = new TransactionHistoryView(accountController);
        Stage stage = new Stage();
        try {
            view.start(stage);
        } catch (Exception e) {
            showAlert("Error", "Could not open transaction history: " + e.getMessage());
        }
    }
    
    private void openCustomerList() {
        CustomerListView view = new CustomerListView(customerController, accountController);
        Stage stage = new Stage();
        try {
            view.start(stage);
        } catch (Exception e) {
            showAlert("Error", "Could not open customer list: " + e.getMessage());
        }
    }
    
    private void processMonthlyInterest() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Process Monthly Interest");
        confirmation.setHeaderText("Process interest for all accounts?");
        confirmation.setContentText("This will calculate and apply monthly interest to all eligible accounts.");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                var result = accountController.processMonthlyInterest();
                if (result.isSuccess()) {
                    showAlert("Success", result.getMessage());
                } else {
                    showAlert("Error", result.getMessage());
                }
            }
        });
    }
    
    private void handleLogout() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Logout");
        confirmation.setHeaderText("Are you sure you want to logout?");
        confirmation.setContentText("You will be returned to the login screen.");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                loginController.logout();
                
                // Open login screen
                LoginView loginView = new LoginView();
                Stage loginStage = new Stage();
                try {
                    loginView.start(loginStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                // Close dashboard
                primaryStage.close();
            }
        });
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