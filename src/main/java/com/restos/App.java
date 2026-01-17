package com.restos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        
        // Load the login view
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        
        Scene scene = new Scene(root, PREF_WIDTH, PREF_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
        
        stage.setTitle("Restos - Restaurant POS System");
        stage.setScene(scene);
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * Get the primary stage for scene switching
     * @return Primary Stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Switch to a new scene
     * @param fxmlPath Path to the FXML file
     * @param title Window title
     */
    public static void switchScene(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(App.class.getResource(fxmlPath));
            Scene scene = new Scene(root, PREF_WIDTH, PREF_HEIGHT);
            scene.getStylesheets().add(App.class.getResource("/css/main.css").toExternalForm());
            primaryStage.setTitle("Restos - " + title);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading scene: " + fxmlPath);
        }
    }

    /**
     * Main method - Application entry point
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
