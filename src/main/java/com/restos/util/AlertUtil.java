package com.restos.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * Utility class for displaying alerts and dialogs
 * Provides consistent UI dialogs throughout the application
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class AlertUtil {

    /**
     * Show information alert
     * @param title Alert title
     * @param message Alert message
     */
    public static void showInfo(String title, String message) {
        showAlert(AlertType.INFORMATION, title, message);
    }

    /**
     * Show success alert
     * @param title Alert title
     * @param message Alert message
     */
    public static void showSuccess(String title, String message) {
        showAlert(AlertType.INFORMATION, title, message);
    }

    /**
     * Show warning alert
     * @param title Alert title
     * @param message Alert message
     */
    public static void showWarning(String title, String message) {
        showAlert(AlertType.WARNING, title, message);
    }

    /**
     * Show error alert
     * @param title Alert title
     * @param message Alert message
     */
    public static void showError(String title, String message) {
        showAlert(AlertType.ERROR, title, message);
    }

    /**
     * Show generic alert
     * @param type Alert type
     * @param title Alert title
     * @param message Alert message
     */
    public static void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        styleAlert(alert);
        alert.showAndWait();
    }

    /**
     * Show confirmation dialog
     * @param title Dialog title
     * @param message Confirmation message
     * @return true if user clicks OK/Yes, false otherwise
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        styleAlert(alert);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Show confirmation dialog with custom buttons
     * @param title Dialog title
     * @param message Confirmation message
     * @param yesText Text for Yes/OK button
     * @param noText Text for No/Cancel button
     * @return true if user clicks Yes, false otherwise
     */
    public static boolean showConfirmation(String title, String message, String yesText, String noText) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType yesButton = new ButtonType(yesText, ButtonData.YES);
        ButtonType noButton = new ButtonType(noText, ButtonData.NO);

        alert.getButtonTypes().setAll(yesButton, noButton);
        styleAlert(alert);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == yesButton;
    }

    /**
     * Show delete confirmation dialog
     * @param itemName Name of the item to delete
     * @return true if user confirms deletion, false otherwise
     */
    public static boolean showDeleteConfirmation(String itemName) {
        return showConfirmation(
            "Konfirmasi Hapus",
            "Apakah Anda yakin ingin menghapus " + itemName + "?\n\nTindakan ini tidak dapat dibatalkan.",
            "Hapus",
            "Batal"
        );
    }

    /**
     * Show text input dialog
     * @param title Dialog title
     * @param message Input prompt message
     * @param defaultValue Default value for input
     * @return User input or null if cancelled
     */
    public static String showInputDialog(String title, String message, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(message);

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    /**
     * Show text input dialog without default value
     * @param title Dialog title
     * @param message Input prompt message
     * @return User input or null if cancelled
     */
    public static String showInputDialog(String title, String message) {
        return showInputDialog(title, message, "");
    }

    /**
     * Apply custom styling to alert dialog
     * @param alert Alert to style
     */
    private static void styleAlert(Alert alert) {
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);
        
        // Add custom CSS if needed
        // alert.getDialogPane().getStylesheets().add(
        //     AlertUtil.class.getResource("/css/dialogs.css").toExternalForm()
        // );
    }
}
