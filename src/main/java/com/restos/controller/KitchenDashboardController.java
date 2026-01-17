package com.restos.controller;

import com.restos.App;
import com.restos.dao.*;
import com.restos.model.*;
import com.restos.util.AlertUtil;
import com.restos.util.DateTimeUtil;
import com.restos.util.SessionManager;
import com.restos.util.UIFeedback;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for Kitchen Display System (KDS)
 * Handles order queue display and status updates
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class KitchenDashboardController implements Initializable {

    // Filter Buttons
    @FXML
    private Button filterAll;
    @FXML
    private Button filterPending;
    @FXML
    private Button filterCooking;
    @FXML
    private Button filterServed;

    // Stats Labels
    @FXML
    private Label pendingCountLabel;
    @FXML
    private Label cookingCountLabel;
    @FXML
    private Label servedCountLabel;

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

    // Order Cards Container
    @FXML
    private FlowPane orderCardsContainer;

    // DAOs
    private OrderDAO orderDAO;
    private OrderItemDAO orderItemDAO;
    private MenuDAO menuDAO;

    // Auto-refresh timeline
    private Timeline refreshTimeline;

    // Filter buttons array
    private Button[] filterButtons;

    // Current filter
    private String currentFilter = "all"; // all, pending, cooking, served

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        orderDAO = new OrderDAOImpl();
        orderItemDAO = new OrderItemDAOImpl();
        menuDAO = new MenuDAOImpl();

        filterButtons = new Button[] { filterAll, filterPending, filterCooking, filterServed };

        setupUserInfo();
        loadOrderQueue();
        updateStats();
        setupAutoRefresh();
        updateDateTime();
        setupKeyboardShortcuts();
    }

    /**
     * Setup keyboard shortcuts
     */
    private void setupKeyboardShortcuts() {
        // F5 to refresh
        orderCardsContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.F5) {
                        handleRefresh();
                        UIFeedback.showInfo(orderCardsContainer, "Data diperbarui");
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
            userRoleLabel.setText("Staff Dapur");
            avatarLabel.setText(currentUser.getFullName().substring(0, 1).toUpperCase());
        }
    }

    /**
     * Load order queue based on current filter
     */
    private void loadOrderQueue() {
        orderCardsContainer.getChildren().clear();

        // Get orders for kitchen (pending and processing)
        List<Order> kitchenOrders = orderDAO.findForKitchen();

        // Sort by created_at (oldest first - FIFO)
        kitchenOrders.sort(Comparator.comparing(Order::getCreatedAt));

        // Filter based on current filter
        List<Order> filteredOrders = filterOrders(kitchenOrders);

        if (filteredOrders.isEmpty()) {
            showEmptyState();
            return;
        }

        // Create order cards
        for (Order order : filteredOrders) {
            // Load order items
            List<OrderItem> items = orderItemDAO.findByOrderId(order.getId());

            // Filter items based on current filter
            List<OrderItem> filteredItems = filterItems(items);

            if (!filteredItems.isEmpty()) {
                VBox card = createOrderCard(order, filteredItems);
                orderCardsContainer.getChildren().add(card);
            }
        }

        if (orderCardsContainer.getChildren().isEmpty()) {
            showEmptyState();
        }
    }

    /**
     * Filter orders based on current filter
     */
    private List<Order> filterOrders(List<Order> orders) {
        if ("all".equals(currentFilter)) {
            return orders;
        }

        return orders.stream()
                .filter(order -> {
                    List<OrderItem> items = orderItemDAO.findByOrderId(order.getId());
                    return items.stream().anyMatch(item -> matchesFilter(item.getStatus()));
                })
                .collect(Collectors.toList());
    }

    /**
     * Filter items based on current filter
     */
    private List<OrderItem> filterItems(List<OrderItem> items) {
        if ("all".equals(currentFilter)) {
            return items;
        }

        return items.stream()
                .filter(item -> matchesFilter(item.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * Check if status matches current filter
     */
    private boolean matchesFilter(String status) {
        switch (currentFilter) {
            case "pending":
                return OrderItem.STATUS_PENDING.equals(status);
            case "cooking":
                return OrderItem.STATUS_COOKING.equals(status);
            case "served":
                return OrderItem.STATUS_SERVED.equals(status);
            default:
                return true;
        }
    }

    /**
     * Create an order card
     */
    private VBox createOrderCard(Order order, List<OrderItem> items) {
        VBox card = new VBox(0);
        card.getStyleClass().add("order-card");

        // Determine overall status for card header
        String overallStatus = determineOverallStatus(items);

        // Card Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().addAll("order-card-header", "order-card-header-" + overallStatus);

        VBox headerInfo = new VBox(2);
        Label orderIdLabel = new Label("Order #" + order.getId());
        orderIdLabel.getStyleClass().add("order-card-title");

        String tableInfo = order.getTable() != null ? "Meja " + order.getTable().getTableNumber()
                : order.getOrderType();
        Label tableLabel = new Label(tableInfo + " • " + order.getCustomerName());
        tableLabel.getStyleClass().add("order-card-table");

        headerInfo.getChildren().addAll(orderIdLabel, tableLabel);
        HBox.setHgrow(headerInfo, Priority.ALWAYS);

        // Time elapsed
        Label timeLabel = createTimeLabel(order.getCreatedAt());

        header.getChildren().addAll(headerInfo, timeLabel);

        // Card Body - Items List
        VBox body = new VBox(0);
        body.getStyleClass().add("order-card-body");

        for (OrderItem item : items) {
            HBox itemRow = createItemRow(item);
            body.getChildren().add(itemRow);
        }

        // Card Footer - Action Buttons
        HBox footer = new HBox(8);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.getStyleClass().add("order-card-footer");

        // Add action buttons based on status
        if (hasStatus(items, OrderItem.STATUS_PENDING)) {
            Button startAllBtn = new Button("🔥 Mulai Masak Semua");
            startAllBtn.getStyleClass().add("action-btn-start");
            startAllBtn.setOnAction(e -> startCookingAll(order.getId(), items));
            footer.getChildren().add(startAllBtn);
        }

        if (hasStatus(items, OrderItem.STATUS_COOKING) && !hasStatus(items, OrderItem.STATUS_PENDING)) {
            Button completeBtn = new Button("✅ Semua Siap");
            completeBtn.getStyleClass().add("action-btn-complete");
            completeBtn.setOnAction(e -> completeAllItems(order.getId(), items));
            footer.getChildren().add(completeBtn);
        }

        if (allItemsServed(items)) {
            Button deliverBtn = new Button("🚀 Siap Diantar");
            deliverBtn.getStyleClass().add("action-btn-deliver");
            deliverBtn.setOnAction(e -> markOrderDelivered(order));
            footer.getChildren().add(deliverBtn);
        }

        card.getChildren().addAll(header, body, footer);
        return card;
    }

    /**
     * Create time elapsed label with color coding
     */
    private Label createTimeLabel(LocalDateTime createdAt) {
        long minutes = ChronoUnit.MINUTES.between(createdAt, LocalDateTime.now());

        String timeText;
        if (minutes < 60) {
            timeText = minutes + " menit";
        } else {
            long hours = minutes / 60;
            long mins = minutes % 60;
            timeText = hours + "j " + mins + "m";
        }

        Label timeLabel = new Label("⏱️ " + timeText);
        timeLabel.getStyleClass().add("order-card-time");

        // Color coding based on elapsed time
        if (minutes >= 30) {
            timeLabel.getStyleClass().add("order-card-time-danger");
        } else if (minutes >= 15) {
            timeLabel.getStyleClass().add("order-card-time-warning");
        }

        return timeLabel;
    }

    /**
     * Create an item row in order card
     */
    private HBox createItemRow(OrderItem item) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("order-item-row");
        row.setPadding(new Insets(8, 0, 8, 0));

        // Quantity
        Label qtyLabel = new Label(item.getQuantity() + "x");
        qtyLabel.getStyleClass().add("order-item-qty");

        // Item info
        VBox itemInfo = new VBox(2);
        HBox.setHgrow(itemInfo, Priority.ALWAYS);

        // Get menu name
        com.restos.model.Menu menu = menuDAO.findById(item.getMenuId());
        String menuName = menu != null ? menu.getName() : "Item #" + item.getMenuId();

        Label nameLabel = new Label(menuName);
        nameLabel.getStyleClass().add("order-item-name");
        nameLabel.setWrapText(true);

        itemInfo.getChildren().add(nameLabel);

        // Notes if any
        if (item.getNotes() != null && !item.getNotes().isEmpty()) {
            Label notesLabel = new Label("📝 " + item.getNotes());
            notesLabel.getStyleClass().add("order-item-notes");
            notesLabel.setWrapText(true);
            itemInfo.getChildren().add(notesLabel);
        }

        // Status button
        Button statusBtn = createStatusButton(item);

        row.getChildren().addAll(qtyLabel, itemInfo, statusBtn);
        return row;
    }

    /**
     * Create status toggle button for item
     */
    private Button createStatusButton(OrderItem item) {
        Button btn = new Button();
        btn.getStyleClass().add("item-status-btn");

        switch (item.getStatus()) {
            case OrderItem.STATUS_PENDING:
                btn.setText("⏳");
                btn.getStyleClass().add("item-status-pending");
                btn.setTooltip(new Tooltip("Klik untuk mulai masak"));
                btn.setOnAction(e -> updateItemStatus(item, OrderItem.STATUS_COOKING));
                break;
            case OrderItem.STATUS_COOKING:
                btn.setText("🔥");
                btn.getStyleClass().add("item-status-cooking");
                btn.setTooltip(new Tooltip("Klik jika sudah siap"));
                btn.setOnAction(e -> updateItemStatus(item, OrderItem.STATUS_SERVED));
                break;
            case OrderItem.STATUS_SERVED:
                btn.setText("✅");
                btn.getStyleClass().add("item-status-served");
                btn.setTooltip(new Tooltip("Sudah siap diantar"));
                btn.setDisable(true);
                break;
        }

        return btn;
    }

    /**
     * Determine overall status for card header color
     */
    private String determineOverallStatus(List<OrderItem> items) {
        boolean allServed = items.stream().allMatch(i -> OrderItem.STATUS_SERVED.equals(i.getStatus()));
        boolean hasCooking = items.stream().anyMatch(i -> OrderItem.STATUS_COOKING.equals(i.getStatus()));

        if (allServed)
            return "served";
        if (hasCooking)
            return "cooking";
        return "pending";
    }

    /**
     * Check if items list has specific status
     */
    private boolean hasStatus(List<OrderItem> items, String status) {
        return items.stream().anyMatch(i -> status.equals(i.getStatus()));
    }

    /**
     * Check if all items are served
     */
    private boolean allItemsServed(List<OrderItem> items) {
        return items.stream().allMatch(i -> OrderItem.STATUS_SERVED.equals(i.getStatus()));
    }

    /**
     * Update single item status
     */
    private void updateItemStatus(OrderItem item, String newStatus) {
        boolean success = orderItemDAO.updateStatus(item.getId(), newStatus);
        if (success) {
            loadOrderQueue();
            updateStats();
        } else {
            AlertUtil.showError("Error", "Gagal mengubah status item");
        }
    }

    /**
     * Start cooking all pending items in order
     */
    private void startCookingAll(int orderId, List<OrderItem> items) {
        for (OrderItem item : items) {
            if (OrderItem.STATUS_PENDING.equals(item.getStatus())) {
                orderItemDAO.updateStatus(item.getId(), OrderItem.STATUS_COOKING);
            }
        }

        // Update order status to processing
        orderDAO.updateStatus(orderId, Order.STATUS_PROCESSING);

        loadOrderQueue();
        updateStats();
    }

    /**
     * Complete all cooking items in order
     */
    private void completeAllItems(int orderId, List<OrderItem> items) {
        for (OrderItem item : items) {
            if (OrderItem.STATUS_COOKING.equals(item.getStatus())) {
                orderItemDAO.updateStatus(item.getId(), OrderItem.STATUS_SERVED);
            }
        }
        loadOrderQueue();
        updateStats();
    }

    /**
     * Mark order as delivered
     */
    private void markOrderDelivered(Order order) {
        boolean success = orderDAO.updateStatus(order.getId(), Order.STATUS_DELIVERED);
        if (success) {
            AlertUtil.showInfo("Sukses", "Order #" + order.getId() + " siap diantar ke pelanggan!");
            loadOrderQueue();
            updateStats();
        } else {
            AlertUtil.showError("Error", "Gagal mengubah status pesanan");
        }
    }

    /**
     * Show empty state when no orders
     */
    private void showEmptyState() {
        String message = "all".equals(currentFilter) ? "Tidak ada pesanan yang perlu diproses"
                : "Tidak ada pesanan dengan status ini";

        VBox emptyState = UIFeedback.createEmptyState(
                "👨‍🍳",
                "Semua Beres!",
                message);
        emptyState.getStyleClass().add("empty-state-kitchen");
        emptyState.setPrefWidth(Double.MAX_VALUE);

        orderCardsContainer.getChildren().add(emptyState);
    }

    /**
     * Update statistics counts
     */
    private void updateStats() {
        List<OrderItem> kitchenItems = orderItemDAO.findForKitchen();

        long pending = kitchenItems.stream()
                .filter(i -> OrderItem.STATUS_PENDING.equals(i.getStatus())).count();
        long cooking = kitchenItems.stream()
                .filter(i -> OrderItem.STATUS_COOKING.equals(i.getStatus())).count();
        long served = kitchenItems.stream()
                .filter(i -> OrderItem.STATUS_SERVED.equals(i.getStatus())).count();

        pendingCountLabel.setText(String.valueOf(pending));
        cookingCountLabel.setText(String.valueOf(cooking));
        servedCountLabel.setText(String.valueOf(served));
    }

    /**
     * Setup auto-refresh timeline (every 5 seconds)
     */
    private void setupAutoRefresh() {
        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            loadOrderQueue();
            updateStats();
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
     * Set active filter button
     */
    private void setActiveFilterButton(Button activeButton) {
        for (Button btn : filterButtons) {
            btn.getStyleClass().remove("nav-btn-active");
        }
        activeButton.getStyleClass().add("nav-btn-active");
    }

    // ==================== FILTER HANDLERS ====================

    @FXML
    private void showAllOrders() {
        currentFilter = "all";
        setActiveFilterButton(filterAll);
        pageSubtitle.setText("Semua pesanan dapur");
        loadOrderQueue();
    }

    @FXML
    private void showPendingOrders() {
        currentFilter = "pending";
        setActiveFilterButton(filterPending);
        pageSubtitle.setText("Pesanan menunggu diproses");
        loadOrderQueue();
    }

    @FXML
    private void showCookingOrders() {
        currentFilter = "cooking";
        setActiveFilterButton(filterCooking);
        pageSubtitle.setText("Pesanan sedang dimasak");
        loadOrderQueue();
    }

    @FXML
    private void showServedOrders() {
        currentFilter = "served";
        setActiveFilterButton(filterServed);
        pageSubtitle.setText("Pesanan siap diantar");
        loadOrderQueue();
    }

    @FXML
    private void handleRefresh() {
        loadOrderQueue();
        updateStats();
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
