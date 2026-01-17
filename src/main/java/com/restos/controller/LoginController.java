package com.restos.controller;

import com.restos.App;
import com.restos.model.User;
import com.restos.service.AuthService;
import com.restos.util.SessionManager;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for Login Page
 * Handles user authentication and role-based routing
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class LoginController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    @FXML
    private ProgressIndicator loadingIndicator;

    private AuthService authService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        authService = new AuthService();

        // Clear any previous session
        SessionManager.getInstance().clearSession();

        // Focus on username field
        Platform.runLater(() -> usernameField.requestFocus());
    }

    /**
     * Handle login button click
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            showError("Username dan password harus diisi!");
            return;
        }

        // Show loading state
        setLoading(true);
        hideError();

        // Perform login in background thread
        Task<User> loginTask = new Task<>() {
            @Override
            protected User call() throws Exception {
                return authService.login(username, password);
            }
        };

        loginTask.setOnSucceeded(event -> {
            User user = loginTask.getValue();
            if (user != null) {
                // Save user to session
                SessionManager.getInstance().setCurrentUser(user);

                // Route to appropriate dashboard based on role
                routeToDashboard(user.getRole());
            } else {
                setLoading(false);
                showError("Username atau password salah!");
            }
        });

        loginTask.setOnFailed(event -> {
            setLoading(false);
            Throwable exception = loginTask.getException();
            showError("Gagal login: " + exception.getMessage());
            exception.printStackTrace();
        });

        // Start login task
        new Thread(loginTask).start();
    }

    /**
     * Route user to appropriate dashboard based on their role
     * @param role User role
     */
    private void routeToDashboard(String role) {
        String fxmlPath;
        String title;

        switch (role.toLowerCase()) {
            case "admin":
                fxmlPath = "/fxml/admin/dashboard.fxml";
                title = "Admin Dashboard";
                break;
            case "waiter":
                fxmlPath = "/fxml/waiter/dashboard.fxml";
                title = "Waiter Dashboard";
                break;
            case "kitchen":
                fxmlPath = "/fxml/kitchen/dashboard.fxml";
                title = "Kitchen Display";
                break;
            case "cashier":
                fxmlPath = "/fxml/cashier/dashboard.fxml";
                title = "Cashier POS";
                break;
            default:
                showError("Role tidak dikenali: " + role);
                setLoading(false);
                return;
        }

        Platform.runLater(() -> {
            App.switchScene(fxmlPath, title);
        });
    }

    /**
     * Show error message
     * @param message Error message to display
     */
    private void showError(String message) {
        Platform.runLater(() -> {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        });
    }

    /**
     * Hide error message
     */
    private void hideError() {
        Platform.runLater(() -> {
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
        });
    }

    /**
     * Set loading state
     * @param loading Whether to show loading state
     */
    private void setLoading(boolean loading) {
        Platform.runLater(() -> {
            loginButton.setDisable(loading);
            loadingIndicator.setVisible(loading);
            loadingIndicator.setManaged(loading);
            usernameField.setDisable(loading);
            passwordField.setDisable(loading);
        });
    }
}
