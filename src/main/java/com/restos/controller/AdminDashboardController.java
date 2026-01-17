package com.restos.controller;

import com.restos.App;
import com.restos.dao.*;
import com.restos.model.Order;
import com.restos.model.Table;
import com.restos.model.User;
import com.restos.util.AlertUtil;
import com.restos.util.CurrencyFormatter;
import com.restos.util.DateTimeUtil;
import com.restos.util.SessionManager;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for Admin Dashboard
 * Handles navigation, stats display, and admin operations
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class AdminDashboardController implements Initializable {

    // Sidebar Navigation Buttons
    @FXML
    private Button navDashboard;
    @FXML
    private Button navMenu;
    @FXML
    private Button navTable;
    @FXML
    private Button navUser;
    @FXML
    private Button navInventory;
    @FXML
    private Button navReport;

    // User Info Labels
    @FXML
    private Label avatarLabel;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label userRoleLabel;

    // Header Labels
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
    private VBox dashboardContent;

    // Stats Labels
    @FXML
    private Label revenueLabel;
    @FXML
    private Label revenueChangeLabel;
    @FXML
    private Label ordersLabel;
    @FXML
    private Label ordersChangeLabel;
    @FXML
    private Label tablesLabel;
    @FXML
    private Label tablesChangeLabel;
    @FXML
    private Label pendingLabel;
    @FXML
    private Label pendingChangeLabel;

    // Recent Orders Table
    @FXML
    private TableView<Order> recentOrdersTable;
    @FXML
    private TableColumn<Order, String> orderIdColumn;
    @FXML
    private TableColumn<Order, String> orderTableColumn;
    @FXML
    private TableColumn<Order, String> orderCustomerColumn;
    @FXML
    private TableColumn<Order, String> orderTotalColumn;
    @FXML
    private TableColumn<Order, String> orderStatusColumn;
    @FXML
    private TableColumn<Order, String> orderTimeColumn;

    // Popular Menus Container
    @FXML
    private VBox popularMenusContainer;

    // DAOs
    private OrderDAO orderDAO;
    private TableDAO tableDAO;
    private MenuDAO menuDAO;
    private PaymentDAO paymentDAO;

    // Timeline for auto-refresh
    private Timeline refreshTimeline;

    // Navigation buttons list for styling
    private Button[] navButtons;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize DAOs
        orderDAO = new OrderDAOImpl();
        tableDAO = new TableDAOImpl();
        menuDAO = new MenuDAOImpl();
        paymentDAO = new PaymentDAOImpl();

        // Store nav buttons
        navButtons = new Button[] { navDashboard, navMenu, navTable, navUser, navInventory, navReport };

        // Setup user info
        setupUserInfo();

        // Setup table columns
        setupTableColumns();

        // Load initial data
        loadDashboardData();

        // Setup auto-refresh (every 30 seconds)
        setupAutoRefresh();

        // Update date/time
        updateDateTime();
    }

    /**
     * Setup user info in sidebar
     */
    private void setupUserInfo() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            userNameLabel.setText(currentUser.getFullName());
            userRoleLabel.setText(capitalizeFirst(currentUser.getRole()));
            avatarLabel.setText(currentUser.getFullName().substring(0, 1).toUpperCase());
        }
    }

    /**
     * Setup table columns for recent orders
     */
    private void setupTableColumns() {
        orderIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty("#" + cellData.getValue().getId()));

        orderTableColumn.setCellValueFactory(cellData -> {
            Order order = cellData.getValue();
            if (order.getTable() != null) {
                return new SimpleStringProperty(order.getTable().getTableNumber());
            }
            return new SimpleStringProperty(order.getOrderType());
        });

        orderCustomerColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerName()));

        orderTotalColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(CurrencyFormatter.format(cellData.getValue().getTotalAmount())));

        orderStatusColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatusDisplayName()));

        // Custom cell factory for status with styling
        orderStatusColumn.setCellFactory(column -> new TableCell<Order, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(status);
                    badge.getStyleClass().add(getStatusStyleClass(getTableRow().getItem()));
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        orderTimeColumn.setCellValueFactory(cellData -> {
            LocalDateTime createdAt = cellData.getValue().getCreatedAt();
            if (createdAt != null) {
                return new SimpleStringProperty(createdAt.format(DateTimeFormatter.ofPattern("HH:mm")));
            }
            return new SimpleStringProperty("-");
        });
    }

    /**
     * Get style class for order status
     */
    private String getStatusStyleClass(Order order) {
        if (order == null)
            return "status-pending";
        switch (order.getStatus()) {
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

    /**
     * Load dashboard data (stats, recent orders, popular menus)
     */
    private void loadDashboardData() {
        // Load stats
        loadStats();

        // Load recent orders
        loadRecentOrders();

        // Load popular menus
        loadPopularMenus();
    }

    /**
     * Load dashboard statistics
     */
    private void loadStats() {
        // Revenue
        double todayRevenue = paymentDAO.getTodayRevenue();
        revenueLabel.setText(CurrencyFormatter.format(todayRevenue));
        revenueChangeLabel.setText("Hari ini");

        // Orders
        int todayOrders = orderDAO.countToday();
        ordersLabel.setText(String.valueOf(todayOrders));
        ordersChangeLabel.setText(todayOrders + " pesanan hari ini");

        // Tables
        List<Table> allTables = tableDAO.findAll();
        List<Table> occupiedTables = tableDAO.findByStatus(Table.STATUS_OCCUPIED);
        int total = allTables.size();
        int occupied = occupiedTables.size();
        tablesLabel.setText(occupied + "/" + total);
        tablesChangeLabel.setText((total - occupied) + " meja tersedia");

        // Pending orders
        int pendingCount = orderDAO.countByStatus(Order.STATUS_PENDING);
        int processingCount = orderDAO.countByStatus(Order.STATUS_PROCESSING);
        pendingLabel.setText(String.valueOf(pendingCount + processingCount));
        pendingChangeLabel.setText("Menunggu diproses");
    }

    /**
     * Load recent orders into table
     */
    private void loadRecentOrders() {
        List<Order> todayOrders = orderDAO.findToday();
        // Limit to 10 recent orders
        if (todayOrders.size() > 10) {
            todayOrders = todayOrders.subList(0, 10);
        }
        ObservableList<Order> orderList = FXCollections.observableArrayList(todayOrders);
        recentOrdersTable.setItems(orderList);
    }

    /**
     * Load popular menus
     */
    private void loadPopularMenus() {
        popularMenusContainer.getChildren().clear();

        // For now, show placeholder - in real implementation, query most sold items
        List<com.restos.model.Menu> menus = menuDAO.findAll();

        if (menus.isEmpty()) {
            Label emptyLabel = new Label("Belum ada data menu");
            emptyLabel.getStyleClass().add("text-secondary");
            popularMenusContainer.getChildren().add(emptyLabel);
            return;
        }

        // Show top 5 menus (in real app, sort by sales)
        int count = Math.min(5, menus.size());
        for (int i = 0; i < count; i++) {
            com.restos.model.Menu menu = menus.get(i);
            HBox itemBox = createPopularMenuItem(i + 1, menu.getName(), "Tersedia");
            popularMenusContainer.getChildren().add(itemBox);
        }
    }

    /**
     * Create popular menu item UI
     */
    private HBox createPopularMenuItem(int rank, String name, String soldInfo) {
        HBox container = new HBox(12);
        container.setAlignment(Pos.CENTER_LEFT);
        container.getStyleClass().add("popular-item");

        Label rankLabel = new Label(String.valueOf(rank));
        rankLabel.getStyleClass().add("popular-rank");

        VBox infoBox = new VBox(2);
        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("popular-name");
        Label soldLabel = new Label(soldInfo);
        soldLabel.getStyleClass().add("popular-sold");
        infoBox.getChildren().addAll(nameLabel, soldLabel);

        container.getChildren().addAll(rankLabel, infoBox);
        return container;
    }

    /**
     * Setup auto-refresh timeline
     */
    private void setupAutoRefresh() {
        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(30), event -> {
            loadDashboardData();
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
        activeButton.getStyleClass().add("nav-btn-active");
    }

    // ==================== NAVIGATION HANDLERS ====================

    @FXML
    private void showDashboard() {
        setActiveNavButton(navDashboard);
        pageTitle.setText("Dashboard");
        pageSubtitle.setText("Selamat datang di Admin Panel");

        // Show dashboard content
        contentArea.getChildren().clear();
        contentArea.getChildren().add(dashboardContent);

        // Reload data
        loadDashboardData();
    }

    @FXML
    private void showMenuManagement() {
        setActiveNavButton(navMenu);
        pageTitle.setText("Manajemen Menu");
        pageSubtitle.setText("Kelola menu restoran");
        loadPage("/fxml/admin/menu-management.fxml");
    }

    @FXML
    private void showTableManagement() {
        setActiveNavButton(navTable);
        pageTitle.setText("Manajemen Meja");
        pageSubtitle.setText("Kelola meja restoran");
        loadPage("/fxml/admin/table-management.fxml");
    }

    @FXML
    private void showUserManagement() {
        setActiveNavButton(navUser);
        pageTitle.setText("Manajemen Pengguna");
        pageSubtitle.setText("Kelola akun pengguna");
        loadPage("/fxml/admin/user-management.fxml");
    }

    @FXML
    private void showInventoryManagement() {
        setActiveNavButton(navInventory);
        pageTitle.setText("Manajemen Inventaris");
        pageSubtitle.setText("Kelola stok harian");
        loadPage("/fxml/admin/inventory-management.fxml");
    }

    @FXML
    private void showReports() {
        setActiveNavButton(navReport);
        pageTitle.setText("Laporan");
        pageSubtitle.setText("Lihat laporan penjualan");

        // Show placeholder for now
        VBox placeholder = new VBox(20);
        placeholder.setAlignment(Pos.CENTER);
        placeholder.getStyleClass().add("empty-state");

        Label icon = new Label("📊");
        icon.getStyleClass().add("empty-state-icon");
        Label text = new Label("Fitur laporan akan segera hadir");
        text.getStyleClass().add("empty-state-text");

        placeholder.getChildren().addAll(icon, text);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(placeholder);
    }

    /**
     * Load a page into content area
     */
    private void loadPage(String fxmlPath) {
        try {
            URL resource = getClass().getResource(fxmlPath);
            if (resource != null) {
                Parent page = FXMLLoader.load(resource);
                contentArea.getChildren().clear();
                contentArea.getChildren().add(page);
            } else {
                showComingSoon(fxmlPath);
            }
        } catch (IOException e) {
            System.err.println("Error loading page: " + fxmlPath);
            e.printStackTrace();
            showComingSoon(fxmlPath);
        }
    }

    /**
     * Show coming soon placeholder
     */
    private void showComingSoon(String pageName) {
        VBox placeholder = new VBox(20);
        placeholder.setAlignment(Pos.CENTER);
        placeholder.getStyleClass().add("empty-state");

        Label icon = new Label("🚧");
        icon.getStyleClass().add("empty-state-icon");
        Label text = new Label("Halaman ini sedang dalam pengembangan");
        text.getStyleClass().add("empty-state-text");

        placeholder.getChildren().addAll(icon, text);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(placeholder);
    }

    // ==================== ACTION HANDLERS ====================

    @FXML
    private void viewAllOrders() {
        AlertUtil.showInfo("Info", "Fitur lihat semua pesanan akan segera hadir");
    }

    @FXML
    private void addNewMenu() {
        showMenuManagement();
        // TODO: Open add menu dialog
    }

    @FXML
    private void addNewUser() {
        showUserManagement();
        // TODO: Open add user dialog
    }

    @FXML
    private void handleLogout() {
        // Stop refresh timeline
        if (refreshTimeline != null) {
            refreshTimeline.stop();
        }

        // Confirm logout
        boolean confirm = AlertUtil.showConfirmation("Konfirmasi Logout",
                "Apakah Anda yakin ingin keluar?");

        if (confirm) {
            SessionManager.getInstance().clearSession();
            App.switchScene("/fxml/login.fxml", "Login - Restos POS");
        }
    }

    /**
     * Capitalize first letter
     */
    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty())
            return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
