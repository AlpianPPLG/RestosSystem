package com.restos;

import com.restos.config.DatabaseConfig;
import com.restos.util.AlertUtil;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import java.io.InputStream;

/**
 * Main Application Entry Point for Restos Desktop POS
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class App extends Application {

    private static Stage primaryStage;

    // Window dimensions
    private static final double MIN_WIDTH = 1024;
    private static final double MIN_HEIGHT = 768;
    private static final double PREF_WIDTH = 1280;
    private static final double PREF_HEIGHT = 800;

    // Application info
    public static final String APP_NAME = "Restos";
    public static final String APP_VERSION = "1.0.0";
    public static final String APP_TITLE = "Restos - Restaurant POS System";

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        try {
            // Test database connection first
            if (!testDatabaseConnection()) {
                AlertUtil.showError("Database Error",
                        "Tidak dapat terhubung ke database.\n" +
                                "Pastikan MySQL server berjalan dan konfigurasi database benar.");
                Platform.exit();
                return;
            }

            // Load the login view
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));

            Scene scene = new Scene(root, PREF_WIDTH, PREF_HEIGHT);
            scene.getStylesheets().addAll(
                    getClass().getResource("/css/main.css").toExternalForm(),
                    getClass().getResource("/css/components.css").toExternalForm());

            // Setup keyboard shortcuts
            setupGlobalShortcuts(scene);

            // Setup stage
            stage.setTitle(APP_TITLE);
            stage.setScene(scene);
            stage.setMinWidth(MIN_WIDTH);
            stage.setMinHeight(MIN_HEIGHT);

            // Try to load app icon
            loadAppIcon(stage);

            // Handle close request
            stage.setOnCloseRequest(event -> {
                event.consume();
                handleAppClose();
            });

            stage.centerOnScreen();
            stage.show();

            System.out.println(APP_NAME + " v" + APP_VERSION + " started successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("Startup Error",
                    "Gagal memulai aplikasi: " + e.getMessage());
            Platform.exit();
        }
    }

    /**
     * Test database connection on startup
     */
    private boolean testDatabaseConnection() {
        try {
            return DatabaseConfig.getInstance().testConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Load application icon
     */
    private void loadAppIcon(Stage stage) {
        try {
            InputStream iconStream = getClass().getResourceAsStream("/images/logo.png");
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));
            }
        } catch (Exception e) {
            // Icon not critical, continue without it
            System.out.println("Could not load app icon: " + e.getMessage());
        }
    }

    /**
     * Setup global keyboard shortcuts
     */
    private void setupGlobalShortcuts(Scene scene) {
        // F11 - Toggle fullscreen
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.F11),
                () -> primaryStage.setFullScreen(!primaryStage.isFullScreen()));

        // Ctrl+Q - Quit application
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN),
                this::handleAppClose);

        // F5 - Refresh (will be handled by individual controllers)
        // Escape - Cancel/Close dialogs (handled by individual components)
    }

    /**
     * Handle application close request
     */
    private void handleAppClose() {
        boolean confirm = AlertUtil.showConfirmation(
                "Keluar Aplikasi",
                "Apakah Anda yakin ingin keluar dari " + APP_NAME + "?");

        if (confirm) {
            // Close database connection
            DatabaseConfig.getInstance().closeConnection();

            System.out.println(APP_NAME + " closed.");
            Platform.exit();
            System.exit(0);
        }
    }

    /**
     * Get the primary stage for scene switching
     * 
     * @return Primary Stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Switch to a new scene
     * 
     * @param fxmlPath Path to the FXML file
     * @param title    Window title
     */
    public static void switchScene(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(App.class.getResource(fxmlPath));
            Scene scene = new Scene(root, PREF_WIDTH, PREF_HEIGHT);
            scene.getStylesheets().addAll(
                    App.class.getResource("/css/main.css").toExternalForm(),
                    App.class.getResource("/css/components.css").toExternalForm());

            // Add module-specific CSS if exists
            String moduleCss = getModuleCss(fxmlPath);
            if (moduleCss != null) {
                try {
                    scene.getStylesheets().add(App.class.getResource(moduleCss).toExternalForm());
                } catch (Exception e) {
                    // Module CSS not found, continue without it
                }
            }

            primaryStage.setTitle(APP_NAME + " - " + title);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("Navigation Error",
                    "Gagal memuat halaman: " + e.getMessage());
        }
    }

    /**
     * Get module-specific CSS path based on FXML path
     */
    private static String getModuleCss(String fxmlPath) {
        if (fxmlPath.contains("/admin/")) {
            return "/css/dashboard.css";
        } else if (fxmlPath.contains("/waiter/")) {
            return "/css/waiter.css";
        } else if (fxmlPath.contains("/kitchen/")) {
            return "/css/kitchen.css";
        } else if (fxmlPath.contains("/cashier/")) {
            return "/css/cashier.css";
        } else if (fxmlPath.contains("login")) {
            return "/css/login.css";
        }
        return null;
    }

    /**
     * Switch scene with custom CSS
     * 
     * @param fxmlPath Path to the FXML file
     * @param title    Window title
     * @param cssPath  Path to CSS file
     */
    public static void switchScene(String fxmlPath, String title, String cssPath) {
        try {
            Parent root = FXMLLoader.load(App.class.getResource(fxmlPath));
            Scene scene = new Scene(root, PREF_WIDTH, PREF_HEIGHT);
            scene.getStylesheets().addAll(
                    App.class.getResource("/css/main.css").toExternalForm(),
                    App.class.getResource("/css/components.css").toExternalForm());

            if (cssPath != null) {
                scene.getStylesheets().add(App.class.getResource(cssPath).toExternalForm());
            }

            primaryStage.setTitle(APP_NAME + " - " + title);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showError("Navigation Error",
                    "Gagal memuat halaman: " + e.getMessage());
        }
    }

    /**
     * Get preferred width
     */
    public static double getPrefWidth() {
        return PREF_WIDTH;
    }

    /**
     * Get preferred height
     */
    public static double getPrefHeight() {
        return PREF_HEIGHT;
    }

    @Override
    public void stop() {
        // Cleanup on application stop
        DatabaseConfig.getInstance().closeConnection();
        System.out.println(APP_NAME + " stopped.");
    }

    /**
     * Main method - Application entry point
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
