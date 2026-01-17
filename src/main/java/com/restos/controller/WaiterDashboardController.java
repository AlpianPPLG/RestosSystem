package com.restos.controller;

import com.restos.App;
import com.restos.dao.*;
import com.restos.model.Order;
import com.restos.model.Table;
import com.restos.model.User;
import com.restos.util.AlertUtil;
import com.restos.util.DateTimeUtil;
import com.restos.util.SessionManager;
import com.restos.util.UIFeedback;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for Waiter Dashboard
 * Handles table selection and order management for waiters
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class WaiterDashboardController implements Initializable {

    // Navigation Buttons
    @FXML
    private Button navTables;
    @FXML
    private Button navOrders;
    @FXML
    private Button navHistory;

    // User Info
    @FXML
    private Label avatarLabel;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label userRoleLabel;

    // Header
    @FXML
    private Label pageTitle;
    @FXML
    private Label pageSubtitle;
    @FXML
    private Label dateTimeLabel;

    // Content Area
    @FXML
    private StackPane contentArea;
    @FXML
    private VBox tableGridContent;
    @FXML
    private FlowPane tableGrid;

    // Stats Labels
    @FXML
    private Label totalTablesLabel;
    @FXML
    private Label availableTablesLabel;
    @FXML
    private Label occupiedTablesLabel;
    @FXML
    private Label reservedTablesLabel;

    // DAOs
    private TableDAO tableDAO;
    private OrderDAO orderDAO;

    // Auto-refresh timeline
    private Timeline refreshTimeline;

    // Navigation buttons array
    private Button[] navButtons;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableDAO = new TableDAOImpl();
        orderDAO = new OrderDAOImpl();

        navButtons = new Button[] { navTables, navOrders, navHistory };

        setupUserInfo();
        loadTableGrid();
        setupAutoRefresh();
        updateDateTime();
        setupKeyboardShortcuts();
    }

    /**
     * Setup keyboard shortcuts
     */
    private void setupKeyboardShortcuts() {
        // F5 to refresh
        contentArea.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.F5) {
                        handleRefresh();
                        UIFeedback.showInfo(contentArea, "Data diperbarui");
                    }
                });
            }
        });
    }

    /**
     * Setup user info in sidebar
     */
    private void setupUserInfo() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            userNameLabel.setText(currentUser.getFullName());
            userRoleLabel.setText("Pelayan");
            avatarLabel.setText(currentUser.getFullName().substring(0, 1).toUpperCase());
        }
    }

    /**
     * Load table grid with all tables
     */
    private void loadTableGrid() {
        tableGrid.getChildren().clear();

        List<Table> tables = tableDAO.findAll();

        // Update stats
        int total = tables.size();
        long available = tables.stream().filter(t -> Table.STATUS_AVAILABLE.equals(t.getStatus())).count();
        long occupied = tables.stream().filter(t -> Table.STATUS_OCCUPIED.equals(t.getStatus())).count();
        long reserved = tables.stream().filter(t -> Table.STATUS_RESERVED.equals(t.getStatus())).count();

        totalTablesLabel.setText(String.valueOf(total));
        availableTablesLabel.setText(String.valueOf(available));
        occupiedTablesLabel.setText(String.valueOf(occupied));
        reservedTablesLabel.setText(String.valueOf(reserved));

        // Create table cards
        for (Table table : tables) {
            VBox card = createTableCard(table);
            tableGrid.getChildren().add(card);
        }

        if (tables.isEmpty()) {
            Label emptyLabel = new Label("Belum ada meja tersedia");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #6b7280;");
            tableGrid.getChildren().add(emptyLabel);
        }
    }

    /**
     * Create a table card UI component
     */
    private VBox createTableCard(Table table) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("table-card");

        // Set card style based on status
        String statusStyle;
        String statusText;
        String statusLabelStyle;

        switch (table.getStatus()) {
            case Table.STATUS_OCCUPIED:
                statusStyle = "table-card-occupied";
                statusText = "Terisi";
                statusLabelStyle = "table-status-occupied";
                break;
            case Table.STATUS_RESERVED:
                statusStyle = "table-card-reserved";
                statusText = "Dipesan";
                statusLabelStyle = "table-status-reserved";
                break;
            default:
                statusStyle = "table-card-available";
                statusText = "Tersedia";
                statusLabelStyle = "table-status-available";
        }

        card.getStyleClass().add(statusStyle);

        // Table icon
        Label icon = new Label("🪑");
        icon.getStyleClass().add("table-icon");

        // Table number
        Label numberLabel = new Label(table.getTableNumber());
        numberLabel.getStyleClass().add("table-number");

        // Capacity
        Label capacityLabel = new Label("👥 " + table.getCapacity() + " orang");
        capacityLabel.getStyleClass().add("table-capacity");

        // Status badge
        Label statusLabel = new Label(statusText);
        statusLabel.getStyleClass().addAll("table-status", statusLabelStyle);

        card.getChildren().addAll(icon, numberLabel, capacityLabel, statusLabel);

        // Click handler
        card.setOnMouseClicked(e -> handleTableClick(table));

        return card;
    }

    /**
     * Handle table card click
     */
    private void handleTableClick(Table table) {
        if (Table.STATUS_AVAILABLE.equals(table.getStatus())) {
            // Create new order for this table
            openNewOrderPage(table);
        } else if (Table.STATUS_OCCUPIED.equals(table.getStatus())) {
            // Show existing order options
            showOccupiedTableOptions(table);
        } else if (Table.STATUS_RESERVED.equals(table.getStatus())) {
            // Ask if want to start order for reserved table
            boolean confirm = AlertUtil.showConfirmation("Meja Dipesan",
                    "Meja " + table.getTableNumber()
                            + " sudah dipesan.\nApakah tamu sudah datang dan ingin memulai pesanan?");
            if (confirm) {
                openNewOrderPage(table);
            }
        }
    }

    /**
     * Show options for occupied table
     */
    private void showOccupiedTableOptions(Table table) {
        // Find active order for this table
        List<Order> orders = orderDAO.findByTableId(table.getId());
        Order activeOrder = orders.stream()
                .filter(o -> !Order.STATUS_COMPLETED.equals(o.getStatus())
                        && !Order.STATUS_CANCELLED.equals(o.getStatus()))
                .findFirst()
                .orElse(null);

        if (activeOrder != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Meja Terisi");
            alert.setHeaderText("Meja " + table.getTableNumber() + " - Order #" + activeOrder.getId());
            alert.setContentText("Pilih aksi:");

            ButtonType viewOrder = new ButtonType("Lihat Pesanan");
            ButtonType addItems = new ButtonType("Tambah Item");
            ButtonType cancel = new ButtonType("Batal", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(viewOrder, addItems, cancel);

            alert.showAndWait().ifPresent(response -> {
                if (response == viewOrder) {
                    showOrderDetail(activeOrder);
                } else if (response == addItems) {
                    openNewOrderPage(table, activeOrder);
                }
            });
        } else {
            AlertUtil.showInfo("Info", "Tidak ada pesanan aktif untuk meja ini.");
        }
    }

    /**
     * Open new order page
     */
    private void openNewOrderPage(Table table) {
        openNewOrderPage(table, null);
    }

    /**
     * Open new order page with optional existing order
     */
    private void openNewOrderPage(Table table, Order existingOrder) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/waiter/new-order.fxml"));
            Parent orderPage = loader.load();

            NewOrderController controller = loader.getController();
            controller.setTable(table);
            controller.setParentController(this);
            if (existingOrder != null) {
                controller.setExistingOrder(existingOrder);
            }

            contentArea.getChildren().clear();
            contentArea.getChildren().add(orderPage);

            pageTitle.setText("Pesanan Baru");
            pageSubtitle.setText("Meja " + table.getTableNumber());

            setActiveNavButton(null); // No nav button active
        } catch (IOException e) {
            System.err.println("Error loading new order page: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Gagal membuka halaman pesanan");
        }
    }

    /**
     * Show order detail
     */
    private void showOrderDetail(Order order) {
        AlertUtil.showInfo("Detail Pesanan",
                "Order #" + order.getId() + "\n" +
                        "Status: " + order.getStatusDisplayName() + "\n" +
                        "Customer: " + order.getCustomerName());
    }

    /**
     * Setup auto-refresh timeline (every 10 seconds)
     */
    private void setupAutoRefresh() {
        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(10), event -> {
            loadTableGrid();
            updateDateTime();
        }));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    /**
     * Update date/time label
     */
    private void updateDateTime() {
        String formattedDateTime = DateTimeUtil.formatDateTime(LocalDateTime.now());
        dateTimeLabel.setText(formattedDateTime);
    }

    /**
     * Set active navigation button
     */
    private void setActiveNavButton(Button activeButton) {
        for (Button btn : navButtons) {
            btn.getStyleClass().remove("nav-btn-active");
        }
        if (activeButton != null) {
            activeButton.getStyleClass().add("nav-btn-active");
        }
    }

    /**
     * Return to table grid view
     */
    public void returnToTableGrid() {
        showTables();
    }

    // ==================== NAVIGATION HANDLERS ====================

    @FXML
    private void showTables() {
        setActiveNavButton(navTables);
        pageTitle.setText("Pilih Meja");
        pageSubtitle.setText("Klik meja untuk membuat pesanan baru");

        contentArea.getChildren().clear();
        contentArea.getChildren().add(tableGridContent);

        loadTableGrid();
    }

    @FXML
    private void showMyOrders() {
        setActiveNavButton(navOrders);
        pageTitle.setText("Pesanan Saya");
        pageSubtitle.setText("Daftar pesanan yang Anda buat hari ini");

        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null)
            return;

        List<Order> myOrders = orderDAO.findByUserId(currentUser.getId());

        VBox ordersContent = new VBox(16);
        ordersContent.setPadding(new Insets(24));

        if (myOrders.isEmpty()) {
            // Use UIFeedback for empty state
            VBox emptyState = UIFeedback.createEmptyState(
                    "📋",
                    "Belum ada pesanan",
                    "Anda belum membuat pesanan hari ini");
            ordersContent.getChildren().add(emptyState);
        } else {
            for (Order order : myOrders) {
                HBox orderCard = createOrderCard(order);
                ordersContent.getChildren().add(orderCard);
            }
        }

        ScrollPane scrollPane = new ScrollPane(ordersContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        contentArea.getChildren().clear();
        contentArea.getChildren().add(scrollPane);
    }

    /**
     * Create order card for my orders list
     */
    private HBox createOrderCard(Order order) {
        HBox card = new HBox(16);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("order-card");
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12px;");

        VBox info = new VBox(4);
        Label idLabel = new Label("Order #" + order.getId());
        idLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        String tableInfo = order.getTable() != null ? "Meja " + order.getTable().getTableNumber()
                : order.getOrderType();
        Label tableLabel = new Label(tableInfo + " • " + order.getCustomerName());
        tableLabel.setStyle("-fx-text-fill: #6b7280;");

        info.getChildren().addAll(idLabel, tableLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusLabel = new Label(order.getStatusDisplayName());
        statusLabel.getStyleClass().add(getStatusStyleClass(order.getStatus()));

        card.getChildren().addAll(info, spacer, statusLabel);
        return card;
    }

    private String getStatusStyleClass(String status) {
        switch (status) {
            case Order.STATUS_PENDING:
                return "status-pending";
            case Order.STATUS_PROCESSING:
                return "status-processing";
            case Order.STATUS_DELIVERED:
                return "status-delivered";
            case Order.STATUS_COMPLETED:
                return "status-completed";
            case Order.STATUS_CANCELLED:
                return "status-cancelled";
            default:
                return "status-pending";
        }
    }

    @FXML
    private void showHistory() {
        setActiveNavButton(navHistory);
        pageTitle.setText("Riwayat Pesanan");
        pageSubtitle.setText("Riwayat pesanan yang sudah selesai");

        // Use UIFeedback for coming soon state
        VBox historyContent = UIFeedback.createEmptyState(
                "📜",
                "Segera Hadir",
                "Fitur riwayat pesanan akan segera tersedia");
        historyContent.setPadding(new Insets(48));

        contentArea.getChildren().clear();
        contentArea.getChildren().add(historyContent);
    }

    @FXML
    private void handleRefresh() {
        loadTableGrid();
        updateDateTime();
    }

    @FXML
    private void handleLogout() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
        }

        boolean confirm = AlertUtil.showConfirmation("Konfirmasi Logout",
                "Apakah Anda yakin ingin keluar?");

        if (confirm) {
            SessionManager.getInstance().clearSession();
            App.switchScene("/fxml/login.fxml", "Login - Restos POS");
        }
    }
}
