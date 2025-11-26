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
 * DashboardView  
 * - Sidebar navigation and large content area
 * - Left sidebar with vertical menu
 * - Large welcome section
 * - Quick stats cards
 * - Glassmorphism effects
 */
public class DashboardView extends Application {
    // Controllers
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
        
        // BorderPane with sidebar
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #393f4dff;");
        
        // Left sidebar
        root.setLeft(createSidebar());
        
        // Main content area
        root.setCenter(createMainArea());
        
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setTitle("𝐆𝐄𝐍𝐄𝐑𝐀𝐋 ₿𝐀𝐍𝐊 𓃵");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            handleLogout();
        });
    }
    
    /**
     * Creates sidebar navigation
     */
    private VBox createSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.setPrefWidth(280);
        sidebar.setStyle(
            "-fx-background-color: #1e293b;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 20, 0, 5, 0);"
        );
        
        // Logo section
        VBox logoSection = new VBox(8);
        logoSection.setAlignment(Pos.CENTER);
        logoSection.setPadding(new Insets(30, 20, 30, 20));
        logoSection.setStyle("-fx-background-color: #0f172a;");
        
        Label logoIcon = new Label("🏦");
        logoIcon.setFont(Font.font("Arial", 42));
        
        Label appName = new Label("𝐆𝐄𝐍𝐄𝐑𝐀𝐋 ₿𝐀𝐍𝐊 𓃵 ");
        appName.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        appName.setTextFill(Color.WHITE);
        
        Label appSubtitle = new Label("Financially goated.");
        appSubtitle.setFont(Font.font("Arial", 11));
        appSubtitle.setTextFill(Color.web("#94a3b8"));
        
        logoSection.getChildren().addAll(logoIcon, appName, appSubtitle);
        
        // User info card
        VBox userCard = new VBox(5);
        userCard.setAlignment(Pos.CENTER);
        userCard.setPadding(new Insets(15));
        userCard.setStyle(
            "-fx-background-color: #334155;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 2);"
        );
        
        User currentUser = loginController.getCurrentUser();
        String username = currentUser != null ? currentUser.getUsername() : "User";
        String role = currentUser != null ? currentUser.getRole() : "TELLER";
        
        Label userIcon = new Label("👤");
        userIcon.setFont(Font.font("Arial", 32));
        
        Label userName = new Label(username);
        userName.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        userName.setTextFill(Color.WHITE);
        
        Label userRole = new Label(role);
        userRole.setFont(Font.font("Arial", 12));
        userRole.setTextFill(Color.web("#94a3b8"));
        
        userCard.getChildren().addAll(userIcon, userName, userRole);
        
        VBox userSection = new VBox(userCard);
        userSection.setPadding(new Insets(0, 20, 20, 20));
        
        // Menu items
        VBox menuSection = new VBox(8);
        menuSection.setPadding(new Insets(20));
        
        Label menuTitle = new Label("OPERATIONS");
        menuTitle.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        menuTitle.setTextFill(Color.web("#64748b"));
        menuTitle.setPadding(new Insets(0, 0, 10, 15));
        
        menuSection.getChildren().addAll(
            menuTitle,
            createMenuItem("👤", "Register Customer", "#10b981", this::openCustomerRegistration),
            createMenuItem("🏦", "Open Account", "#3b82f6", this::openAccountManagement),
            createMenuItem("💰", "Make Deposit", "#f59e0b", this::openDepositView),
            createMenuItem("💸", "Make Withdrawal", "#ef4444", this::openWithdrawalView),
            createMenuItem("📊", "View Balance", "#8b5cf6", this::openBalanceView),
            createMenuItem("📜", "Transaction History", "#06b6d4", this::openTransactionHistory)
        );
        
        // Admin section
        if (loginController.hasPermission("OVERRIDE_LIMIT")) {
            Label adminTitle = new Label("ADMIN");
            adminTitle.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            adminTitle.setTextFill(Color.web("#64748b"));
            adminTitle.setPadding(new Insets(15, 0, 10, 15));
            
            menuSection.getChildren().addAll(
                adminTitle,
                createMenuItem("💹", "Process Interest", "#78716c", this::processMonthlyInterest)
            );
        }
        
        Label reportTitle = new Label("REPORTS");
        reportTitle.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        reportTitle.setTextFill(Color.web("#64748b"));
        reportTitle.setPadding(new Insets(15, 0, 10, 15));
        
        menuSection.getChildren().addAll(
            reportTitle,
            createMenuItem("👥", "View Customers", "#64748b", this::openCustomerList)
        );
        
        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        // Logout button at bottom
        Button logoutBtn = new Button("🚪 Logout");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setStyle(
            "-fx-background-color: #ef4444;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 15;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        );
        
        logoutBtn.setOnMouseEntered(e ->
            logoutBtn.setStyle(
                "-fx-background-color: #dc2626;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 15;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(239, 68, 68, 0.6), 12, 0, 0, 4);"
            )
        );
        
        logoutBtn.setOnMouseExited(e ->
            logoutBtn.setStyle(
                "-fx-background-color: #ef4444;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 15;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;"
            )
        );
        
        logoutBtn.setOnAction(e -> handleLogout());
        
        VBox logoutSection = new VBox(logoutBtn);
        logoutSection.setPadding(new Insets(20));
        
        // Add all sections
        sidebar.getChildren().addAll(logoSection, userSection, menuSection, spacer, logoutSection);
        
        return sidebar;
    }
    
    /**
     * Creates sidebar menu item
     */
    private HBox createMenuItem(String icon, String text, String accentColor, Runnable action) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(15, 20, 15, 20));
        item.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        );
        
        // Icon
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Arial", 22));
        
        // Text
        Label textLabel = new Label(text);
        textLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        textLabel.setTextFill(Color.WHITE);
        
        item.getChildren().addAll(iconLabel, textLabel);
        
        // Hover effects
        item.setOnMouseEntered(e -> {
            item.setStyle(
                "-fx-background-color: " + accentColor + ";" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, " + accentColor + "80, 10, 0, 0, 3);"
            );
        });
        
        item.setOnMouseExited(e -> {
            item.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;"
            );
        });
        
        item.setOnMouseClicked(e -> action.run());
        
        return item;
    }
    
    /**
     * Creates main content area with welcome message and stats
     */
    private StackPane createMainArea() {
        StackPane mainArea = new StackPane();
        mainArea.setStyle("-fx-background-color: #0f172a;");
        
        // Gradient overlay
        VBox content = new VBox(40);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(60));
        content.setStyle(
            "-fx-background-color: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);"
        );
        
        // Welcome section
        VBox welcomeBox = new VBox(15);
        welcomeBox.setAlignment(Pos.CENTER);
        
        Label welcomeTitle = new Label("Welcome to 𝐆𝐄𝐍𝐄𝐑𝐀𝐋 ₿𝐀𝐍𝐊 𓃵 ");
        welcomeTitle.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        welcomeTitle.setTextFill(Color.WHITE);
        
        Label welcomeSubtitle = new Label("Select an operation from the sidebar to get started");
        welcomeSubtitle.setFont(Font.font("Arial", 18));
        welcomeSubtitle.setTextFill(Color.web("#94a3b8"));
        
        welcomeBox.getChildren().addAll(welcomeTitle, welcomeSubtitle);
        
        // Quick stats cards
        HBox statsBox = new HBox(30);
        statsBox.setAlignment(Pos.CENTER);
        
        statsBox.getChildren().addAll(
            createStatCard("👤", "Customer\nManagement", "#10b981"),
            createStatCard("🏦", "Account\nServices", "#3b82f6"),
            createStatCard("💰", "Transaction\nProcessing", "#f59e0b"),
            createStatCard("📊", "Reports &\nAnalytics", "#8b5cf6")
        );
        
        content.getChildren().addAll(welcomeBox, statsBox);
        
        mainArea.getChildren().add(content);
        return mainArea;
    }
    
    /**
     * Creates stat card
     */
    private VBox createStatCard(String icon, String text, String color) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(200, 180);
        card.setStyle(
            "-fx-background-color: rgba(30, 41, 59, 0.8);" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 5);"
        );
        card.setPadding(new Insets(30));
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Arial", 48));
        
        Label textLabel = new Label(text);
        textLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        textLabel.setTextFill(Color.web(color));
        textLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        card.getChildren().addAll(iconLabel, textLabel);
        
        // Hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle(
                "-fx-background-color: " + color + "20;" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: " + color + ";" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, " + color + "80, 20, 0, 0, 8);" +
                "-fx-scale-x: 1.05;" +
                "-fx-scale-y: 1.05;"
            );
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle(
                "-fx-background-color: rgba(30, 41, 59, 0.8);" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: " + color + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 5);"
            );
        });
        
        return card;
    }
    
    
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
                
                LoginView loginView = new LoginView();
                Stage loginStage = new Stage();
                try {
                    loginView.start(loginStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
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