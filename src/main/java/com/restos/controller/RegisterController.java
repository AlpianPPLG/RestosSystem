package com.restos.controller;

import com.restos.App;
import com.restos.dao.UserDAO;
import com.restos.dao.UserDAOImpl;
import com.restos.model.User;
import com.restos.util.UIFeedback;
import com.restos.util.ValidationUtil;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

/**
 * Controller for Registration Page
 * Handles new user registration
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class RegisterController implements Initializable {

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button registerButton;

    @FXML
    private Label messageLabel;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private Hyperlink loginLink;

    private UserDAO userDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userDAO = new UserDAOImpl();

        // Setup role combobox
        setupRoleComboBox();

        // Focus on first field
        Platform.runLater(() -> fullNameField.requestFocus());

        // Setup keyboard navigation
        setupKeyboardShortcuts();
    }

    /**
     * Setup role selection combobox
     */
    private void setupRoleComboBox() {
        roleComboBox.setItems(FXCollections.observableArrayList(
                "Admin",
                "Waiter",
                "Kitchen",
                "Cashier"));
    }

    /**
     * Setup keyboard shortcuts
     */
    private void setupKeyboardShortcuts() {
        // Enter on each field moves to next
        fullNameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER)
                usernameField.requestFocus();
        });
        usernameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER)
                emailField.requestFocus();
        });
        emailField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER)
                phoneField.requestFocus();
        });
        phoneField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER)
                roleComboBox.requestFocus();
        });
        roleComboBox.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER)
                passwordField.requestFocus();
        });
        passwordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER)
                confirmPasswordField.requestFocus();
        });
    }

    /**
     * Handle register button click
     */
    @FXML
    private void handleRegister() {
        // Get form values
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String role = roleComboBox.getValue();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validate required fields
        if (ValidationUtil.isEmpty(fullName)) {
            showError("Nama lengkap harus diisi!");
            UIFeedback.shake(fullNameField);
            fullNameField.requestFocus();
            return;
        }

        if (ValidationUtil.isEmpty(username)) {
            showError("Username harus diisi!");
            UIFeedback.shake(usernameField);
            usernameField.requestFocus();
            return;
        }

        if (!ValidationUtil.isValidUsername(username)) {
            showError("Username minimal 4 karakter, hanya huruf, angka, dan underscore!");
            UIFeedback.shake(usernameField);
            usernameField.requestFocus();
            return;
        }

        // Validate email if provided
        if (!ValidationUtil.isEmpty(email) && !ValidationUtil.isValidEmail(email)) {
            showError("Format email tidak valid!");
            UIFeedback.shake(emailField);
            emailField.requestFocus();
            return;
        }

        // Validate phone if provided
        if (!ValidationUtil.isEmpty(phone) && !ValidationUtil.isValidPhone(phone)) {
            showError("Format nomor telepon tidak valid!");
            UIFeedback.shake(phoneField);
            phoneField.requestFocus();
            return;
        }

        if (role == null) {
            showError("Silakan pilih posisi/role!");
            UIFeedback.shake(roleComboBox);
            roleComboBox.requestFocus();
            return;
        }

        if (ValidationUtil.isEmpty(password)) {
            showError("Password harus diisi!");
            UIFeedback.shake(passwordField);
            passwordField.requestFocus();
            return;
        }

        if (!ValidationUtil.isValidPassword(password)) {
            showError("Password minimal 6 karakter!");
            UIFeedback.shake(passwordField);
            passwordField.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Konfirmasi password tidak cocok!");
            UIFeedback.shake(confirmPasswordField);
            confirmPasswordField.requestFocus();
            return;
        }

        // Show loading state
        setLoading(true);
        hideMessage();

        // Perform registration in background
        Task<Boolean> registerTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                // Check if username already exists
                User existingUser = userDAO.findByUsername(username);
                if (existingUser != null) {
                    throw new Exception("Username sudah digunakan!");
                }

                // Create new user
                User newUser = new User();
                newUser.setFullName(fullName);
                newUser.setUsername(username);
                newUser.setEmail(ValidationUtil.isEmpty(email) ? null : email);
                newUser.setPhone(ValidationUtil.isEmpty(phone) ? null : phone);
                newUser.setRole(role.toLowerCase());
                newUser.setPasswordHash(password); // Store plain text password
                newUser.setActive(true);

                // Insert user and return success status
                int result = userDAO.insert(newUser);
                return result > 0;
            }
        };

        registerTask.setOnSucceeded(event -> {
            setLoading(false);
            Boolean result = registerTask.getValue();
            if (result != null && result) {
                showSuccess("Registrasi berhasil! Silakan login.");
                clearForm();

                // Auto redirect to login after 2 seconds
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        Platform.runLater(this::goToLogin);
                    } catch (InterruptedException e) {
                        // Ignore
                    }
                }).start();
            } else {
                showError("Gagal menyimpan data. Silakan coba lagi.");
            }
        });

        registerTask.setOnFailed(event -> {
            setLoading(false);
            Throwable exception = registerTask.getException();
            showError(exception.getMessage());
            UIFeedback.shake(registerButton);
        });

        // Start task
        new Thread(registerTask).start();
    }

    /**
     * Navigate to login page
     */
    @FXML
    private void goToLogin() {
        App.switchScene("/fxml/login.fxml", "Login");
    }

    /**
     * Hash password using SHA-256
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Fallback to plain text if hashing fails
            return password;
        }
    }

    /**
     * Clear all form fields
     */
    private void clearForm() {
        fullNameField.clear();
        usernameField.clear();
        emailField.clear();
        phoneField.clear();
        roleComboBox.setValue(null);
        passwordField.clear();
        confirmPasswordField.clear();
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        Platform.runLater(() -> {
            messageLabel.setText(message);
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
            messageLabel.setVisible(true);
            messageLabel.setManaged(true);
        });
    }

    /**
     * Show success message
     */
    private void showSuccess(String message) {
        Platform.runLater(() -> {
            messageLabel.setText(message);
            messageLabel.setStyle("-fx-text-fill: #22c55e;");
            messageLabel.setVisible(true);
            messageLabel.setManaged(true);
        });
    }

    /**
     * Hide message
     */
    private void hideMessage() {
        Platform.runLater(() -> {
            messageLabel.setVisible(false);
            messageLabel.setManaged(false);
        });
    }

    /**
     * Set loading state
     */
    private void setLoading(boolean loading) {
        Platform.runLater(() -> {
            registerButton.setDisable(loading);
            loadingIndicator.setVisible(loading);
            loadingIndicator.setManaged(loading);
            fullNameField.setDisable(loading);
            usernameField.setDisable(loading);
            emailField.setDisable(loading);
            phoneField.setDisable(loading);
            roleComboBox.setDisable(loading);
            passwordField.setDisable(loading);
            confirmPasswordField.setDisable(loading);
        });
    }
}
