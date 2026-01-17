package com.restos.util;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Utility class for UI feedback components
 * Provides toast notifications, loading overlays, and other feedback mechanisms
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class UIFeedback {

    // Toast types
    public enum ToastType {
        SUCCESS("✓", "toast-success"),
        ERROR("✗", "toast-error"),
        WARNING("⚠", "toast-warning"),
        INFO("ℹ", "toast-info");

        private final String icon;
        private final String styleClass;

        ToastType(String icon, String styleClass) {
            this.icon = icon;
            this.styleClass = styleClass;
        }

        public String getIcon() {
            return icon;
        }

        public String getStyleClass() {
            return styleClass;
        }
    }

    /**
     * Show a toast notification
     * 
     * @param container       Parent container to show toast in (StackPane
     *                        recommended)
     * @param message         Message to display
     * @param type            Toast type (SUCCESS, ERROR, WARNING, INFO)
     * @param durationSeconds Duration to show toast
     */
    public static void showToast(StackPane container, String message, ToastType type, double durationSeconds) {
        Platform.runLater(() -> {
            HBox toast = createToast(message, type);

            // Position at top
            StackPane.setAlignment(toast, Pos.TOP_CENTER);
            toast.setTranslateY(-50);
            toast.setOpacity(0);

            container.getChildren().add(toast);

            // Slide in animation
            TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), toast);
            slideIn.setFromY(-50);
            slideIn.setToY(20);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), toast);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            slideIn.play();
            fadeIn.play();

            // Auto-hide after duration
            PauseTransition pause = new PauseTransition(Duration.seconds(durationSeconds));
            pause.setOnFinished(e -> {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), toast);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(ev -> container.getChildren().remove(toast));
                fadeOut.play();
            });
            pause.play();
        });
    }

    /**
     * Show success toast
     */
    public static void showSuccess(StackPane container, String message) {
        showToast(container, message, ToastType.SUCCESS, 3);
    }

    /**
     * Show error toast
     */
    public static void showError(StackPane container, String message) {
        showToast(container, message, ToastType.ERROR, 4);
    }

    /**
     * Show warning toast
     */
    public static void showWarning(StackPane container, String message) {
        showToast(container, message, ToastType.WARNING, 3);
    }

    /**
     * Show info toast
     */
    public static void showInfo(StackPane container, String message) {
        showToast(container, message, ToastType.INFO, 3);
    }

    /**
     * Show info toast (for any Pane - wraps in temporary StackPane if needed)
     */
    public static void showInfo(Pane container, String message) {
        // Find the parent StackPane or root StackPane
        Node parent = container;
        while (parent != null && !(parent instanceof StackPane)) {
            parent = parent.getParent();
        }
        if (parent instanceof StackPane) {
            showInfo((StackPane) parent, message);
        } else {
            // If no StackPane found, just print info
            System.out.println("[INFO] " + message);
        }
    }

    /**
     * Create toast HBox
     */
    private static HBox createToast(String message, ToastType type) {
        HBox toast = new HBox(10);
        toast.getStyleClass().addAll("toast", type.getStyleClass());
        toast.setAlignment(Pos.CENTER_LEFT);
        toast.setMaxWidth(400);

        Label iconLabel = new Label(type.getIcon());
        iconLabel.getStyleClass().add("toast-icon");

        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("toast-message");
        messageLabel.setWrapText(true);

        toast.getChildren().addAll(iconLabel, messageLabel);

        return toast;
    }

    /**
     * Show loading overlay
     * 
     * @param container Parent container
     * @param message   Loading message
     * @return The overlay node (to remove later)
     */
    public static StackPane showLoading(StackPane container, String message) {
        StackPane overlay = new StackPane();
        overlay.getStyleClass().add("loading-overlay");
        overlay.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9);");

        VBox loadingBox = new VBox(15);
        loadingBox.getStyleClass().add("loading-container");
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setMaxWidth(200);
        loadingBox.setMaxHeight(150);
        loadingBox.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 30; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0, 0, 5);");

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.getStyleClass().add("loading-spinner");
        spinner.setPrefSize(50, 50);
        spinner.setStyle("-fx-progress-color: #F97316;");

        Label loadingLabel = new Label(message);
        loadingLabel.getStyleClass().add("loading-text");
        loadingLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B7280;");

        loadingBox.getChildren().addAll(spinner, loadingLabel);
        overlay.getChildren().add(loadingBox);

        Platform.runLater(() -> {
            container.getChildren().add(overlay);

            // Fade in
            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), overlay);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });

        return overlay;
    }

    /**
     * Hide loading overlay
     * 
     * @param container Parent container
     * @param overlay   The overlay node to remove
     */
    public static void hideLoading(StackPane container, StackPane overlay) {
        if (overlay == null)
            return;

        Platform.runLater(() -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), overlay);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> container.getChildren().remove(overlay));
            fadeOut.play();
        });
    }

    /**
     * Create empty state component
     * 
     * @param icon     Emoji icon
     * @param title    Title text
     * @param subtitle Subtitle text
     * @return VBox empty state component
     */
    public static VBox createEmptyState(String icon, String title, String subtitle) {
        VBox emptyState = new VBox(10);
        emptyState.getStyleClass().add("empty-state");
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setStyle("-fx-padding: 40;");

        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("empty-state-icon");
        iconLabel.setStyle("-fx-font-size: 64px; -fx-opacity: 0.7;");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("empty-state-title");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #374151;");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("empty-state-subtitle");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B7280;");
        subtitleLabel.setWrapText(true);

        emptyState.getChildren().addAll(iconLabel, titleLabel, subtitleLabel);

        return emptyState;
    }

    /**
     * Create badge label
     * 
     * @param text Badge text
     * @param type Badge type (primary, success, warning, danger, info, neutral)
     * @return Label with badge styling
     */
    public static Label createBadge(String text, String type) {
        Label badge = new Label(text);
        badge.getStyleClass().addAll("badge", "badge-" + type);
        badge.setStyle("-fx-padding: 4 12; -fx-background-radius: 20; -fx-font-size: 12px; -fx-font-weight: bold;");

        switch (type) {
            case "primary":
                badge.setStyle(badge.getStyle() + "-fx-background-color: #FFEDD5; -fx-text-fill: #C2410C;");
                break;
            case "success":
                badge.setStyle(badge.getStyle() + "-fx-background-color: #DCFCE7; -fx-text-fill: #166534;");
                break;
            case "warning":
                badge.setStyle(badge.getStyle() + "-fx-background-color: #FEF3C7; -fx-text-fill: #854D0E;");
                break;
            case "danger":
                badge.setStyle(badge.getStyle() + "-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B;");
                break;
            case "info":
                badge.setStyle(badge.getStyle() + "-fx-background-color: #DBEAFE; -fx-text-fill: #1E40AF;");
                break;
            default:
                badge.setStyle(badge.getStyle() + "-fx-background-color: #F3F4F6; -fx-text-fill: #374151;");
        }

        return badge;
    }

    /**
     * Apply shake animation (for error feedback)
     * 
     * @param node Node to shake
     */
    public static void shake(javafx.scene.Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setFromX(0);
        tt.setByX(10);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.setOnFinished(e -> node.setTranslateX(0));
        tt.play();
    }

    /**
     * Apply pulse animation (for attention)
     * 
     * @param node Node to pulse
     */
    public static void pulse(javafx.scene.Node node) {
        javafx.animation.ScaleTransition st = new javafx.animation.ScaleTransition(Duration.millis(200), node);
        st.setFromX(1);
        st.setFromY(1);
        st.setToX(1.05);
        st.setToY(1.05);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.play();
    }
}
