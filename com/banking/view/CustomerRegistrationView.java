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
 * CustomerRegistrationView - Form for registering new customers.
 * Satisfies F-101: Customer registration requirement.
 */
public class CustomerRegistrationView extends Application {
    private CustomerController customerController;
    private TextField firstNameField;
    private TextField surnameField;
    private TextArea addressArea;
    private TextField phoneField;
    private TextField emailField;
    private Label messageLabel;
    private Stage primaryStage;
    
    public CustomerRegistrationView(CustomerController customerController) {
        this.customerController = customerController;
    }
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");
        root.setPadding(new Insets(20));
        
        // Create form
        VBox formBox = createRegistrationForm();
        root.setCenter(formBox);
        
        Scene scene = new Scene(root, 500, 650);
        primaryStage.setTitle("Register New Customer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private VBox createRegistrationForm() {
        VBox container = new VBox(15);
        container.setAlignment(Pos.TOP_CENTER);
        container.setPadding(new Insets(30));
        container.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        container.setMaxWidth(450);
        
        // Title
        Label titleLabel = new Label("👤 Register New Customer");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#4CAF50"));
        
        Separator separator = new Separator();
        
        // First Name
        Label firstNameLabel = new Label("First Name *");
        firstNameLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 12));
        
        firstNameField = new TextField();
        firstNameField.setPromptText("Enter first name");
        styleTextField(firstNameField);
        
        // Surname
        Label surnameLabel = new Label("Surname *");
        surnameLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 12));
        
        surnameField = new TextField();
        surnameField.setPromptText("Enter surname");
        styleTextField(surnameField);
        
        // Address
        Label addressLabel = new Label("Address *");
        addressLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 12));
        
        addressArea = new TextArea();
        addressArea.setPromptText("Enter physical address");
        addressArea.setPrefRowCount(3);
        addressArea.setWrapText(true);
        styleTextField(addressArea);
        
        // Phone (optional)
        Label phoneLabel = new Label("Phone Number");
        phoneLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 12));
        
        phoneField = new TextField();
        phoneField.setPromptText("e.g., 72345678 (optional)");
        styleTextField(phoneField);
        
        // Email (optional)
        Label emailLabel = new Label("Email Address");
        emailLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 12));
        
        emailField = new TextField();
        emailField.setPromptText("e.g., customer@email.bw (optional)");
        styleTextField(emailField);
        
        // Message label
        messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(400);
        
        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button registerButton = new Button("Register Customer");
        registerButton.setStyle(
            "-fx-background-color: #4CAF50;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 30;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        registerButton.setOnAction(e -> handleRegistration());
        
        Button clearButton = new Button("Clear Form");
        clearButton.setStyle(
            "-fx-background-color: #f44336;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 30;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        clearButton.setOnAction(e -> clearForm());
        
        Button closeButton = new Button("Close");
        closeButton.setStyle(
            "-fx-background-color: #9E9E9E;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 30;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        closeButton.setOnAction(e -> primaryStage.close());
        
        buttonBox.getChildren().addAll(registerButton, clearButton, closeButton);
        
        // Required field note
        Label requiredLabel = new Label("* Required fields");
        requiredLabel.setFont(Font.font("Arial", 10));
        requiredLabel.setTextFill(Color.GRAY);
        
        // Add all components
        container.getChildren().addAll(
            titleLabel,
            separator,
            new VBox(5, firstNameLabel, firstNameField),
            new VBox(5, surnameLabel, surnameField),
            new VBox(5, addressLabel, addressArea),
            new VBox(5, phoneLabel, phoneField),
            new VBox(5, emailLabel, emailField),
            requiredLabel,
            messageLabel,
            buttonBox
        );
        
        return container;
    }
    
    private void styleTextField(Control field) {
        field.setStyle(
            "-fx-padding: 10;" +
            "-fx-background-radius: 5;" +
            "-fx-border-color: #ddd;" +
            "-fx-border-radius: 5;"
        );
        field.setFont(Font.font("Arial", 13));
    }
    
    private void handleRegistration() {
        // Clear previous messages
        messageLabel.setText("");
        
        // Get values
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
            showSuccess(result.getMessage());
            
            // Show confirmation dialog
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Customer Registered Successfully!");
            alert.setContentText(
                "Customer ID: " + result.getCustomer().getCustomerId() + "\n" +
                "Name: " + result.getCustomer().getFirstName() + " " + result.getCustomer().getSurname()
            );
            alert.showAndWait();
            
            // Clear form for next customer
            clearForm();
            
        } else {
            showError(result.getMessage());
        }
    }
    
    private void clearForm() {
        firstNameField.clear();
        surnameField.clear();
        addressArea.clear();
        phoneField.clear();
        emailField.clear();
        messageLabel.setText("");
        firstNameField.requestFocus();
    }
    
    private void showError(String message) {
        messageLabel.setText("❌ " + message);
        messageLabel.setTextFill(Color.RED);
        messageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
    }
    
    private void showSuccess(String message) {
        messageLabel.setText("✓ " + message);
        messageLabel.setTextFill(Color.GREEN);
        messageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}