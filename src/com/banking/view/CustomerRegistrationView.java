package com.banking.view;

import com.banking.controller.CustomerController;
import com.banking.controller.CustomerController.CustomerResult;
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
 * CustomerRegistrationView * 
 * DESIGN FEATURES:
 * - Clean, spacious form layout
 * - Enhanced input fields with focus effects
 * - Clear validation feedback
 * - Success confirmation dialogs
 * 
 * Satisfies F-101: Customer registration requirement
 * 
 */
public class CustomerRegistrationView extends Application {
    // Controller
    private CustomerController customerController;
    
    // UI Components
    private TextField firstNameField;
    private TextField surnameField;
    private TextArea addressArea;
    private TextField phoneField;
    private TextField emailField;
    private Label messageLabel;
    private Stage primaryStage;
    
    /**
     * Constructor - initializes the customer controller
     * @param customerController Controller for customer operations
     */
    public CustomerRegistrationView(CustomerController customerController) {
        this.customerController = customerController;
    }
    
    /**
     * Starts the customer registration window
     * @param primaryStage The window stage
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // Main container with light background
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #35393dff;");
        root.setPadding(new Insets(25));
        
        // Create registration form
        VBox formBox = createRegistrationForm();
        root.setCenter(formBox);
        
        Scene scene = new Scene(root, 550, 720);
        primaryStage.setTitle("Register New Customer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * @return VBox containing all form elements
     */
    private VBox createRegistrationForm() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.TOP_CENTER);
        container.setPadding(new Insets(35));
        container.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );
        container.setMaxWidth(500);
        
        // Header
        VBox headerBox = new VBox(8);
        headerBox.setAlignment(Pos.CENTER);
        
        Label iconLabel = new Label("👤");
        iconLabel.setFont(Font.font("Arial", 40));
        
        Label titleLabel = new Label("Register New Customer");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        titleLabel.setTextFill(Color.web("#10b981"));
        
        Label subtitleLabel = new Label("Fill in customer information below");
        subtitleLabel.setFont(Font.font("Arial", 13));
        subtitleLabel.setTextFill(Color.web("#64748b"));
        
        headerBox.getChildren().addAll(iconLabel, titleLabel, subtitleLabel);
        
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));
        
        // Form fields
        VBox fieldsBox = new VBox(18);
        
        // First Name
        VBox firstNameBox = createFormField(
            "First Name *", 
            "Enter customer's first name", 
            false
        );
        firstNameField = (TextField) ((VBox) firstNameBox.getChildren().get(1)).getChildren().get(0);
        fieldsBox.getChildren().add(firstNameBox);
        
        // Surname
        VBox surnameBox = createFormField(
            "Surname *", 
            "Enter customer's surname", 
            false
        );
        surnameField = (TextField) ((VBox) surnameBox.getChildren().get(1)).getChildren().get(0);
        fieldsBox.getChildren().add(surnameBox);
        
        // Address (TextArea)
        VBox addressBox = new VBox(8);
        Label addressLabel = new Label("Physical Address *");
        addressLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
        addressLabel.setTextFill(Color.web("#1e293b"));
        
        VBox addressFieldBox = new VBox();
        addressArea = new TextArea();
        addressArea.setPromptText("Enter complete physical address");
        addressArea.setPrefRowCount(3);
        addressArea.setWrapText(true);
        addressArea.setStyle(
            "-fx-padding: 12;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 8;" +
            "-fx-font-family: 'Arial';" +
            "-fx-font-size: 13px;" +
            "-fx-background-color: #f8fafc;"
        );
        
        // Focus effects for address field
        addressArea.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                addressArea.setStyle(
                    addressArea.getStyle() + 
                    "-fx-border-color: #10b981; -fx-background-color: white;"
                );
            } else {
                addressArea.setStyle(
                    "-fx-padding: 12;" +
                    "-fx-background-radius: 8;" +
                    "-fx-border-color: #e2e8f0;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 8;" +
                    "-fx-font-family: 'Arial';" +
                    "-fx-font-size: 13px;" +
                    "-fx-background-color: #f8fafc;"
                );
            }
        });
        
        addressFieldBox.getChildren().add(addressArea);
        addressBox.getChildren().addAll(addressLabel, addressFieldBox);
        fieldsBox.getChildren().add(addressBox);
        
        // Phone (optional)
        VBox phoneBox = createFormField(
            "Phone Number", 
            "e.g., 72345678 (optional)", 
            false
        );
        phoneField = (TextField) ((VBox) phoneBox.getChildren().get(1)).getChildren().get(0);
        
        Label phoneHint = new Label("Optional field");
        phoneHint.setFont(Font.font("Arial", 11));
        phoneHint.setTextFill(Color.web("#94a3b8"));
        phoneBox.getChildren().add(phoneHint);
        
        fieldsBox.getChildren().add(phoneBox);
        
        // Email (optional)
        VBox emailBox = createFormField(
            "Email Address", 
            "e.g., customer@email.bw (optional)", 
            false
        );
        emailField = (TextField) ((VBox) emailBox.getChildren().get(1)).getChildren().get(0);
        
        Label emailHint = new Label("Optional field");
        emailHint.setFont(Font.font("Arial", 11));
        emailHint.setTextFill(Color.web("#94a3b8"));
        emailBox.getChildren().add(emailHint);
        
        fieldsBox.getChildren().add(emailBox);
        
        // Message label
        messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(440);
        messageLabel.setAlignment(Pos.CENTER);
        
        // Buttons
        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        Button registerButton = new Button("Register Customer");
        registerButton.setPrefWidth(160);
        registerButton.setPrefHeight(42);
        registerButton.setStyle(
            "-fx-background-color: #10b981;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(16, 185, 129, 0.3), 8, 0, 0, 3);"
        );
        
        registerButton.setOnMouseEntered(e ->
            registerButton.setStyle(
                "-fx-background-color: #059669;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(16, 185, 129, 0.5), 12, 0, 0, 5);"
            )
        );
        
        registerButton.setOnMouseExited(e ->
            registerButton.setStyle(
                "-fx-background-color: #10b981;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(16, 185, 129, 0.3), 8, 0, 0, 3);"
            )
        );
        
        registerButton.setOnAction(e -> handleRegistration());
        
        Button clearButton = new Button("Clear Form");
        clearButton.setPrefWidth(120);
        clearButton.setPrefHeight(42);
        clearButton.setStyle(
            "-fx-background-color: #f1f5f9;" +
            "-fx-text-fill: #475569;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
        clearButton.setOnMouseEntered(e ->
            clearButton.setStyle(
                "-fx-background-color: #e2e8f0;" +
                "-fx-text-fill: #334155;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            )
        );
        
        clearButton.setOnMouseExited(e ->
            clearButton.setStyle(
                "-fx-background-color: #f1f5f9;" +
                "-fx-text-fill: #475569;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            )
        );
        
        clearButton.setOnAction(e -> clearForm());
        
        Button closeButton = new Button("Close");
        closeButton.setPrefWidth(100);
        closeButton.setPrefHeight(42);
        closeButton.setStyle(
            "-fx-background-color: #94a3b8;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        
        closeButton.setOnMouseEntered(e ->
            closeButton.setStyle(
                "-fx-background-color: #64748b;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
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
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            )
        );
        
        closeButton.setOnAction(e -> primaryStage.close());
        
        buttonBox.getChildren().addAll(registerButton, clearButton, closeButton);
        
        // Required field notes
        Label requiredLabel = new Label("* Required fields");
        requiredLabel.setFont(Font.font("Arial", 11));
        requiredLabel.setTextFill(Color.web("#ef4444"));
        requiredLabel.setAlignment(Pos.CENTER);
        
        // Add all components to container
        container.getChildren().addAll(
            headerBox,
            separator,
            fieldsBox,
            messageLabel,
            buttonBox,
            requiredLabel
        );
        
        return container;
    }
    
    /**
     * Helper method to create styled form fields
     * @param labelText Label for the field
     * @param promptText Placeholder text
     * @param isPassword Whether it's a password field
     * @return VBox containing label and field
     */
    private VBox createFormField(String labelText, String promptText, boolean isPassword) {
        VBox fieldBox = new VBox(8);
        
        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
        label.setTextFill(Color.web("#1e293b"));
        
        VBox inputBox = new VBox();
        TextField field = isPassword ? new PasswordField() : new TextField();
        field.setPromptText(promptText);
        field.setPrefHeight(42);
        field.setStyle(
            "-fx-padding: 12 15;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 8;" +
            "-fx-font-size: 13px;" +
            "-fx-background-color: #f8fafc;"
        );
        
        // Focus effects
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle(
                    field.getStyle() + 
                    "-fx-border-color: #10b981; -fx-background-color: white;"
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
        
        inputBox.getChildren().add(field);
        fieldBox.getChildren().addAll(label, inputBox);
        
        return fieldBox;
    }
    
    /**
     * Handles customer registration
     */
    private void handleRegistration() {
        // Clear previous messages
        messageLabel.setText("");
        
        // Get trimmed values from fields
        String firstName = firstNameField.getText().trim();
        String surname = surnameField.getText().trim();
        String address = addressArea.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        
        // Call controller 
        CustomerResult result = customerController.registerCustomer(
            firstName, surname, address, phone, email
        );
        
        if (result.isSuccess()) {
            // Show success message
            showSuccess(result.getMessage());
            
            // Show detailed confirmation dialog
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registration Successful");
            alert.setHeaderText("Customer Registered Successfully!");
            alert.setContentText(
                "Customer ID: " + result.getCustomer().getCustomerId() + "\n" +
                "Name: " + result.getCustomer().getFirstName() + " " + 
                result.getCustomer().getSurname() + "\n\n" +
                "Customer can now open bank accounts."
            );
            alert.showAndWait();
            
            // Clear form for next customer
            clearForm();
            
        } else {
            // Show error message from controller
            showError(result.getMessage());
        }
    }
    
    /**
     * Clears all form fields
     */
    private void clearForm() {
        firstNameField.clear();
        surnameField.clear();
        addressArea.clear();
        phoneField.clear();
        emailField.clear();
        messageLabel.setText("");
        firstNameField.requestFocus();
    }
    
    /**
     * Displays error message with red styling
     * @param message Error message to display
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
     * Displays success message with green styling
     * @param message Success message to display
     */
    private void showSuccess(String message) {
        messageLabel.setText("✓ " + message);
        messageLabel.setTextFill(Color.web("#10b981"));
        messageLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
        messageLabel.setStyle(
            "-fx-background-color: #d1fae5;" +
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