package com.restos.controller;

import com.restos.dao.*;
import com.restos.model.*;
import com.restos.util.AlertUtil;
import com.restos.util.CurrencyFormatter;
import com.restos.util.SessionManager;
import com.restos.util.UIFeedback;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for Cashier/POS Dashboard
 * Handles payment processing and receipt generation
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class CashierDashboardController {

    // FXML Components - Sidebar
    @FXML
    private Label userNameLabel;
    @FXML
    private Label userRoleLabel;
    @FXML
    private Label pendingCountLabel;
    @FXML
    private Label completedCountLabel;
    @FXML
    private Label todayRevenueLabel;

    // FXML Components - Main Content
    @FXML
    private TextField searchField;
    @FXML
    private VBox orderListContainer;
    @FXML
    private ScrollPane orderScrollPane;

    // FXML Components - Payment Panel
    @FXML
    private VBox paymentPanel;
    @FXML
    private VBox orderDetailContainer;
    @FXML
    private VBox itemListContainer;
    @FXML
    private Label selectedOrderIdLabel;
    @FXML
    private Label selectedTableLabel;
    @FXML
    private Label selectedWaiterLabel;
    @FXML
    private Label selectedTimeLabel;
    @FXML
    private Label subtotalLabel;
    @FXML
    private Label totalLabel;

    // FXML Components - Payment Form
    @FXML
    private HBox paymentMethodContainer;
    @FXML
    private TextField amountPaidField;
    @FXML
    private VBox changeContainer;
    @FXML
    private Label changeLabel;
    @FXML
    private Button processPaymentBtn;
    @FXML
    private HBox quickAmountContainer;

    // DAOs
    private final OrderDAO orderDAO = new OrderDAOImpl();
    private final OrderItemDAO orderItemDAO = new OrderItemDAOImpl();
    private final PaymentDAO paymentDAO = new PaymentDAOImpl();
    private final TableDAO tableDAO = new TableDAOImpl();
    private final UserDAO userDAO = new UserDAOImpl();
    private final MenuDAO menuDAO = new MenuDAOImpl();

    // State
    private Order selectedOrder;
    private String selectedPaymentMethod = Payment.METHOD_CASH;
    private VBox selectedOrderRow;
    private Timeline autoRefresh;

    // Date formatters
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        // Set user info
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            userNameLabel.setText(currentUser.getFullName());
            userRoleLabel.setText("Cashier");
        }

        // Setup search field listener
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterOrders(newVal));

        // Setup amount field listener with validation
        amountPaidField.textProperty().addListener((obs, oldVal, newVal) -> {
            // Only allow numbers
            if (!newVal.matches("\\d*")) {
                amountPaidField.setText(newVal.replaceAll("[^\\d]", ""));
            }
            calculateChange();
        });

        // Initial load
        loadDeliveredOrders();
        updateStats();

        // Show empty state in payment panel
        showEmptyPaymentPanel();

        // Setup auto-refresh every 10 seconds
        setupAutoRefresh();

        // Setup keyboard shortcuts
        setupKeyboardShortcuts();
    }

    /**
     * Setup keyboard shortcuts
     */
    private void setupKeyboardShortcuts() {
        // F5 to refresh, Enter to process payment when amount field focused
        orderListContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.F5) {
                        handleRefresh();
                        UIFeedback.showInfo(orderListContainer, "Data diperbarui");
                    }
                });
            }
        });

        // Enter on amount field to process payment
        amountPaidField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                processPayment();
            }
        });
    }

    /**
     * Setup auto-refresh timer
     */
    private void setupAutoRefresh() {
        autoRefresh = new Timeline(new KeyFrame(Duration.seconds(10), e -> {
            loadDeliveredOrders();
            updateStats();
        }));
        autoRefresh.setCycleCount(Timeline.INDEFINITE);
        autoRefresh.play();
    }

    /**
     * Load delivered orders waiting for payment
     */
    private void loadDeliveredOrders() {
        orderListContainer.getChildren().clear();

        List<Order> deliveredOrders = orderDAO.findByStatus(Order.STATUS_DELIVERED);

        if (deliveredOrders.isEmpty()) {
            showEmptyOrderList();
            return;
        }

        for (Order order : deliveredOrders) {
            VBox orderRow = createOrderRow(order);
            orderListContainer.getChildren().add(orderRow);
        }
    }

    /**
     * Filter orders by search text
     */
    private void filterOrders(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            loadDeliveredOrders();
            return;
        }

        String search = searchText.toLowerCase().trim();
        orderListContainer.getChildren().clear();

        List<Order> deliveredOrders = orderDAO.findByStatus(Order.STATUS_DELIVERED);

        for (Order order : deliveredOrders) {
            Table table = tableDAO.findById(order.getTableId());
            User waiter = userDAO.findById(order.getUserId());

            String tableNum = table != null ? String.valueOf(table.getTableNumber()) : "";
            String waiterName = waiter != null ? waiter.getFullName().toLowerCase() : "";
            String orderId = String.valueOf(order.getId());

            if (tableNum.contains(search) || waiterName.contains(search) || orderId.contains(search)) {
                VBox orderRow = createOrderRow(order);
                orderListContainer.getChildren().add(orderRow);
            }
        }

        if (orderListContainer.getChildren().isEmpty()) {
            showNoResultsMessage();
        }
    }

    /**
     * Create order row for the list
     */
    private VBox createOrderRow(Order order) {
        VBox row = new VBox(5);
        row.getStyleClass().add("order-row");
        row.setPadding(new Insets(12, 15, 12, 15));

        // Get related data
        Table table = tableDAO.findById(order.getTableId());
        User waiter = userDAO.findById(order.getUserId());

        // Top row: Order ID and Status
        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label orderIdLabel = new Label("#" + order.getId());
        orderIdLabel.getStyleClass().add("order-id");

        Label statusBadge = new Label("DELIVERED");
        statusBadge.getStyleClass().addAll("status-badge", "status-delivered");

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        Label timeLabel = new Label(order.getCreatedAt().format(timeFormatter));
        timeLabel.getStyleClass().add("order-time");

        topRow.getChildren().addAll(orderIdLabel, statusBadge, spacer1, timeLabel);

        // Middle row: Table info
        HBox middleRow = new HBox(10);
        middleRow.setAlignment(Pos.CENTER_LEFT);

        Label tableLabel = new Label("🍽️ Table " + (table != null ? table.getTableNumber() : "N/A"));
        tableLabel.getStyleClass().add("order-table");

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        Label amountLabel = new Label(CurrencyFormatter.format(order.getTotalAmount()));
        amountLabel.getStyleClass().add("order-amount");

        middleRow.getChildren().addAll(tableLabel, spacer2, amountLabel);

        // Bottom row: Waiter and order type
        HBox bottomRow = new HBox(10);
        bottomRow.setAlignment(Pos.CENTER_LEFT);

        Label waiterLabel = new Label("👤 " + (waiter != null ? waiter.getFullName() : "Unknown"));
        waiterLabel.getStyleClass().add("order-waiter");

        Label typeLabel = new Label(order.getOrderType().equals(Order.TYPE_DINE_IN) ? "Dine In" : "Take Away");
        typeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #9CA3AF;");

        bottomRow.getChildren().addAll(waiterLabel, typeLabel);

        row.getChildren().addAll(topRow, middleRow, bottomRow);

        // Click handler
        row.setOnMouseClicked(e -> selectOrder(order, row));

        return row;
    }

    /**
     * Select an order for payment
     */
    private void selectOrder(Order order, VBox orderRow) {
        // Deselect previous
        if (selectedOrderRow != null) {
            selectedOrderRow.getStyleClass().remove("order-row-selected");
        }

        // Select new
        selectedOrder = order;
        selectedOrderRow = orderRow;
        orderRow.getStyleClass().add("order-row-selected");

        // Show order details in payment panel
        showOrderDetails(order);
    }

    /**
     * Show order details in payment panel
     */
    private void showOrderDetails(Order order) {
        // Show payment panel content
        paymentPanel.setVisible(true);

        // Get related data
        Table table = tableDAO.findById(order.getTableId());
        User waiter = userDAO.findById(order.getUserId());

        // Update header info
        selectedOrderIdLabel.setText("#" + order.getId());
        selectedTableLabel.setText("Table " + (table != null ? table.getTableNumber() : "N/A"));
        selectedWaiterLabel.setText(waiter != null ? waiter.getFullName() : "Unknown");
        selectedTimeLabel.setText(order.getCreatedAt().format(dateTimeFormatter));

        // Load order items
        loadOrderItems(order);

        // Update totals
        subtotalLabel.setText(CurrencyFormatter.format(order.getTotalAmount()));
        totalLabel.setText(CurrencyFormatter.format(order.getTotalAmount()));

        // Reset payment form
        resetPaymentForm();

        // Setup quick amount buttons
        setupQuickAmountButtons(order.getTotalAmount());
    }

    /**
     * Load order items into the detail panel
     */
    private void loadOrderItems(Order order) {
        itemListContainer.getChildren().clear();

        List<OrderItem> items = orderItemDAO.findByOrderId(order.getId());

        for (OrderItem item : items) {
            com.restos.model.Menu menu = menuDAO.findById(item.getMenuId());
            if (menu != null) {
                HBox itemRow = createItemRow(item, menu);
                itemListContainer.getChildren().add(itemRow);
            }
        }
    }

    /**
     * Create item row for order detail
     */
    private HBox createItemRow(OrderItem item, com.restos.model.Menu menu) {
        HBox row = new HBox(10);
        row.getStyleClass().add("item-row");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 0, 8, 0));

        // Item name and qty
        VBox nameBox = new VBox(2);
        Label nameLabel = new Label(menu.getName());
        nameLabel.getStyleClass().add("item-name");

        Label qtyLabel = new Label(item.getQuantity() + " x " + CurrencyFormatter.format(item.getPriceAtTime()));
        qtyLabel.getStyleClass().add("item-qty");

        nameBox.getChildren().addAll(nameLabel, qtyLabel);
        HBox.setHgrow(nameBox, Priority.ALWAYS);

        // Subtotal
        BigDecimal subtotal = item.getPriceAtTime().multiply(BigDecimal.valueOf(item.getQuantity()));
        Label priceLabel = new Label(CurrencyFormatter.format(subtotal));
        priceLabel.getStyleClass().add("item-price");

        row.getChildren().addAll(nameBox, priceLabel);

        return row;
    }

    /**
     * Setup quick amount buttons based on total
     */
    private void setupQuickAmountButtons(BigDecimal total) {
        quickAmountContainer.getChildren().clear();

        // Calculate suggested amounts
        long totalValue = total.longValue();
        long[] suggestions = calculateSuggestedAmounts(totalValue);

        for (long amount : suggestions) {
            Button btn = new Button(CurrencyFormatter.formatShort(BigDecimal.valueOf(amount)));
            btn.getStyleClass().add("quick-amount-btn");
            btn.setOnAction(e -> {
                amountPaidField.setText(String.valueOf(amount));
                calculateChange();
            });
            quickAmountContainer.getChildren().add(btn);
        }

        // Add exact amount button
        Button exactBtn = new Button("Exact");
        exactBtn.getStyleClass().add("quick-amount-btn");
        exactBtn.setOnAction(e -> {
            amountPaidField.setText(String.valueOf(totalValue));
            calculateChange();
        });
        quickAmountContainer.getChildren().add(exactBtn);
    }

    /**
     * Calculate suggested payment amounts
     */
    private long[] calculateSuggestedAmounts(long total) {
        // Round up to nearest nice numbers
        long[] suggestions = new long[3];

        if (total <= 50000) {
            suggestions[0] = roundUpToNearest(total, 10000);
            suggestions[1] = roundUpToNearest(total, 20000);
            suggestions[2] = roundUpToNearest(total, 50000);
        } else if (total <= 100000) {
            suggestions[0] = roundUpToNearest(total, 50000);
            suggestions[1] = 100000;
            suggestions[2] = 150000;
        } else {
            suggestions[0] = roundUpToNearest(total, 50000);
            suggestions[1] = roundUpToNearest(total, 100000);
            suggestions[2] = roundUpToNearest(suggestions[1] + 50000, 50000);
        }

        return suggestions;
    }

    /**
     * Round up to nearest value
     */
    private long roundUpToNearest(long value, long nearest) {
        return ((value + nearest - 1) / nearest) * nearest;
    }

    /**
     * Handle payment method selection
     */
    @FXML
    private void selectCash() {
        selectPaymentMethod(Payment.METHOD_CASH);
    }

    @FXML
    private void selectQris() {
        selectPaymentMethod(Payment.METHOD_QRIS);
    }

    @FXML
    private void selectDebit() {
        selectPaymentMethod(Payment.METHOD_DEBIT);
    }

    /**
     * Select payment method
     */
    private void selectPaymentMethod(String method) {
        selectedPaymentMethod = method;

        // Update button styles
        for (var node : paymentMethodContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox btn = (VBox) node;
                btn.getStyleClass().remove("payment-method-btn-selected");

                String btnMethod = (String) btn.getUserData();
                if (method.equals(btnMethod)) {
                    btn.getStyleClass().add("payment-method-btn-selected");
                }
            }
        }

        // For non-cash payments, auto-fill exact amount
        if (!Payment.METHOD_CASH.equals(method) && selectedOrder != null) {
            amountPaidField.setText(String.valueOf(selectedOrder.getTotalAmount().longValue()));
            calculateChange();
        }
    }

    /**
     * Calculate and display change
     */
    private void calculateChange() {
        if (selectedOrder == null)
            return;

        String amountText = amountPaidField.getText();
        if (amountText == null || amountText.isEmpty()) {
            changeLabel.setText("Rp 0");
            changeContainer.getStyleClass().remove("change-error");
            processPaymentBtn.setDisable(true);
            return;
        }

        try {
            BigDecimal amountPaid = new BigDecimal(amountText);
            BigDecimal total = selectedOrder.getTotalAmount();
            BigDecimal change = amountPaid.subtract(total);

            if (change.compareTo(BigDecimal.ZERO) >= 0) {
                changeLabel.setText(CurrencyFormatter.format(change));
                changeContainer.getStyleClass().remove("change-error");
                processPaymentBtn.setDisable(false);
            } else {
                changeLabel.setText("- " + CurrencyFormatter.format(change.abs()));
                changeContainer.getStyleClass().add("change-error");
                processPaymentBtn.setDisable(true);
            }
        } catch (NumberFormatException e) {
            changeLabel.setText("Rp 0");
            processPaymentBtn.setDisable(true);
        }
    }

    /**
     * Process payment
     */
    @FXML
    private void processPayment() {
        if (selectedOrder == null) {
            AlertUtil.showWarning("No Order Selected", "Please select an order to process payment.");
            return;
        }

        String amountText = amountPaidField.getText();
        if (amountText == null || amountText.isEmpty()) {
            AlertUtil.showWarning("Amount Required", "Please enter the amount paid.");
            return;
        }

        BigDecimal amountPaid;
        try {
            amountPaid = new BigDecimal(amountText);
        } catch (NumberFormatException e) {
            AlertUtil.showError("Invalid Amount", "Please enter a valid amount.");
            return;
        }

        BigDecimal total = selectedOrder.getTotalAmount();
        if (amountPaid.compareTo(total) < 0) {
            AlertUtil.showError("Insufficient Amount", "The amount paid is less than the total.");
            return;
        }

        // Calculate change
        BigDecimal change = amountPaid.subtract(total);

        // Create payment
        Payment payment = new Payment();
        payment.setOrderId(selectedOrder.getId());
        payment.setCashierId(SessionManager.getInstance().getCurrentUser().getId());
        payment.setPaymentMethod(selectedPaymentMethod);
        payment.setAmountPaid(amountPaid);
        payment.setChangeAmount(change);
        payment.setTransactionDate(LocalDateTime.now());

        // Save payment
        int paymentId = paymentDAO.insert(payment);

        if (paymentId > 0) {
            // Update order status to completed
            orderDAO.updateStatus(selectedOrder.getId(), Order.STATUS_COMPLETED);

            // Update table status to available (for dine-in)
            if (Order.TYPE_DINE_IN.equals(selectedOrder.getOrderType())) {
                tableDAO.updateStatus(selectedOrder.getTableId(), Table.STATUS_AVAILABLE);
            }

            payment.setId(paymentId);

            // Show success and offer receipt
            boolean showReceipt = AlertUtil.showConfirm(
                    "Payment Successful",
                    "Payment processed successfully!\nChange: " + CurrencyFormatter.format(change) +
                            "\n\nWould you like to view the receipt?");

            if (showReceipt) {
                showReceiptModal(selectedOrder, payment);
            }

            // Refresh list
            loadDeliveredOrders();
            updateStats();

            // Clear selection
            selectedOrder = null;
            selectedOrderRow = null;
            showEmptyPaymentPanel();

        } else {
            AlertUtil.showError("Payment Failed", "Failed to process payment. Please try again.");
        }
    }

    /**
     * Show receipt modal
     */
    private void showReceiptModal(Order order, Payment payment) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);
        modal.setTitle("Receipt");

        // Get related data
        Table table = tableDAO.findById(order.getTableId());
        User waiter = userDAO.findById(order.getUserId());
        User cashier = userDAO.findById(payment.getCashierId());
        List<OrderItem> items = orderItemDAO.findByOrderId(order.getId());

        // Build receipt content
        VBox receiptContainer = new VBox();
        receiptContainer.getStyleClass().add("receipt-container");
        receiptContainer.setMaxWidth(380);

        // Header
        VBox header = new VBox(5);
        header.getStyleClass().add("receipt-header");
        header.setAlignment(Pos.CENTER);

        Label logoLabel = new Label("🍽️ RESTOS");
        logoLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label addressLabel = new Label("Jl. Restoran No. 123, Jakarta");
        addressLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: rgba(255,255,255,0.8);");

        Label phoneLabel = new Label("Tel: (021) 123-4567");
        phoneLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: rgba(255,255,255,0.8);");

        header.getChildren().addAll(logoLabel, addressLabel, phoneLabel);

        // Body
        VBox body = new VBox(10);
        body.getStyleClass().add("receipt-body");
        body.setPadding(new Insets(20));

        // Order info
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(10);
        infoGrid.setVgap(5);

        addReceiptRow(infoGrid, 0, "Order #", String.valueOf(order.getId()));
        addReceiptRow(infoGrid, 1, "Date", payment.getTransactionDate().format(dateTimeFormatter));
        addReceiptRow(infoGrid, 2, "Table", table != null ? String.valueOf(table.getTableNumber()) : "N/A");
        addReceiptRow(infoGrid, 3, "Waiter", waiter != null ? waiter.getFullName() : "Unknown");
        addReceiptRow(infoGrid, 4, "Cashier", cashier != null ? cashier.getFullName() : "Unknown");

        body.getChildren().add(infoGrid);

        // Divider
        body.getChildren().add(createDivider());

        // Items
        VBox itemsBox = new VBox(5);
        for (OrderItem item : items) {
            com.restos.model.Menu menu = menuDAO.findById(item.getMenuId());
            if (menu != null) {
                HBox itemRow = new HBox(10);
                itemRow.setAlignment(Pos.CENTER_LEFT);

                VBox nameBox = new VBox();
                Label nameLbl = new Label(menu.getName());
                nameLbl.setStyle("-fx-font-size: 13px;");
                Label qtyLbl = new Label(item.getQuantity() + " x " + CurrencyFormatter.format(item.getPriceAtTime()));
                qtyLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7280;");
                nameBox.getChildren().addAll(nameLbl, qtyLbl);
                HBox.setHgrow(nameBox, Priority.ALWAYS);

                BigDecimal subtotal = item.getPriceAtTime().multiply(BigDecimal.valueOf(item.getQuantity()));
                Label priceLbl = new Label(CurrencyFormatter.format(subtotal));
                priceLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

                itemRow.getChildren().addAll(nameBox, priceLbl);
                itemsBox.getChildren().add(itemRow);
            }
        }
        body.getChildren().add(itemsBox);

        // Divider
        body.getChildren().add(createDivider());

        // Totals
        GridPane totalsGrid = new GridPane();
        totalsGrid.setHgap(10);
        totalsGrid.setVgap(8);

        Label totalLbl = new Label("TOTAL");
        totalLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        Label totalVal = new Label(CurrencyFormatter.format(order.getTotalAmount()));
        totalVal.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #F97316;");
        totalsGrid.add(totalLbl, 0, 0);
        totalsGrid.add(totalVal, 1, 0);
        GridPane.setHgrow(totalVal, Priority.ALWAYS);
        totalVal.setMaxWidth(Double.MAX_VALUE);
        totalVal.setAlignment(Pos.CENTER_RIGHT);

        String methodText = payment.getPaymentMethod().toUpperCase();
        addReceiptRow(totalsGrid, 1, "Payment", methodText);
        addReceiptRow(totalsGrid, 2, "Paid", CurrencyFormatter.format(payment.getAmountPaid()));

        Label changeLbl = new Label("Change");
        changeLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        Label changeVal = new Label(CurrencyFormatter.format(payment.getChangeAmount()));
        changeVal.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #22C55E;");
        changeVal.setMaxWidth(Double.MAX_VALUE);
        changeVal.setAlignment(Pos.CENTER_RIGHT);
        totalsGrid.add(changeLbl, 0, 3);
        totalsGrid.add(changeVal, 1, 3);

        body.getChildren().add(totalsGrid);

        // Footer
        VBox footer = new VBox(10);
        footer.getStyleClass().add("receipt-footer");
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(15));

        Label thankYou = new Label("Thank you for dining with us!");
        thankYou.setStyle("-fx-font-size: 12px; -fx-text-fill: #6B7280;");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button printBtn = new Button("🖨️ Print");
        printBtn.getStyleClass().add("receipt-btn");
        printBtn.setOnAction(e -> printReceipt(order, payment, items));

        Button saveBtn = new Button("💾 Save PDF");
        saveBtn.getStyleClass().add("receipt-btn");
        saveBtn.setOnAction(e -> saveReceiptAsPDF(order, payment, items, modal));

        Button closeBtn = new Button("Close");
        closeBtn.getStyleClass().add("cancel-btn");
        closeBtn.setOnAction(e -> modal.close());

        buttonBox.getChildren().addAll(printBtn, saveBtn, closeBtn);
        footer.getChildren().addAll(thankYou, buttonBox);

        receiptContainer.getChildren().addAll(header, body, footer);

        // Wrap in overlay
        StackPane overlay = new StackPane(receiptContainer);
        overlay.getStyleClass().add("receipt-overlay");
        overlay.setAlignment(Pos.CENTER);
        overlay.setOnMouseClicked(e -> {
            if (e.getTarget() == overlay)
                modal.close();
        });

        Scene scene = new Scene(overlay, 500, 650);
        scene.setFill(null);
        scene.getStylesheets().add(getClass().getResource("/css/cashier.css").toExternalForm());

        modal.setScene(scene);
        modal.showAndWait();
    }

    /**
     * Add row to receipt grid
     */
    private void addReceiptRow(GridPane grid, int row, String label, String value) {
        Label lblNode = new Label(label);
        lblNode.setStyle("-fx-font-size: 12px; -fx-text-fill: #6B7280;");
        Label valNode = new Label(value);
        valNode.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        valNode.setMaxWidth(Double.MAX_VALUE);
        valNode.setAlignment(Pos.CENTER_RIGHT);
        GridPane.setHgrow(valNode, Priority.ALWAYS);

        grid.add(lblNode, 0, row);
        grid.add(valNode, 1, row);
    }

    /**
     * Create divider
     */
    private HBox createDivider() {
        HBox divider = new HBox();
        divider.getStyleClass().add("receipt-divider");
        divider.setPrefHeight(1);
        divider.setStyle("-fx-border-color: #E5E7EB; -fx-border-style: dashed; -fx-border-width: 1 0 0 0;");
        return divider;
    }

    /**
     * Print receipt (placeholder - would need printer integration)
     */
    private void printReceipt(Order order, Payment payment, List<OrderItem> items) {
        AlertUtil.showInfo("Print",
                "Receipt would be sent to thermal printer.\n(Printer integration not implemented in demo)");
    }

    /**
     * Save receipt as text file (simulated PDF)
     */
    private void saveReceiptAsPDF(Order order, Payment payment, List<OrderItem> items, Stage modal) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Receipt");
        fileChooser.setInitialFileName("receipt_" + order.getId() + ".txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File file = fileChooser.showSaveDialog(modal);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                Table table = tableDAO.findById(order.getTableId());
                User waiter = userDAO.findById(order.getUserId());
                User cashier = userDAO.findById(payment.getCashierId());

                writer.println("================================");
                writer.println("          🍽️ RESTOS            ");
                writer.println("   Jl. Restoran No. 123, Jakarta");
                writer.println("      Tel: (021) 123-4567       ");
                writer.println("================================");
                writer.println();
                writer.println("Order #: " + order.getId());
                writer.println("Date: " + payment.getTransactionDate().format(dateTimeFormatter));
                writer.println("Table: " + (table != null ? table.getTableNumber() : "N/A"));
                writer.println("Waiter: " + (waiter != null ? waiter.getFullName() : "Unknown"));
                writer.println("Cashier: " + (cashier != null ? cashier.getFullName() : "Unknown"));
                writer.println();
                writer.println("--------------------------------");
                writer.println("Items:");
                writer.println("--------------------------------");

                for (OrderItem item : items) {
                    com.restos.model.Menu menu = menuDAO.findById(item.getMenuId());
                    if (menu != null) {
                        BigDecimal subtotal = item.getPriceAtTime().multiply(BigDecimal.valueOf(item.getQuantity()));
                        writer.printf("%s%n", menu.getName());
                        writer.printf("  %d x %s = %s%n",
                                item.getQuantity(),
                                CurrencyFormatter.format(item.getPriceAtTime()),
                                CurrencyFormatter.format(subtotal));
                    }
                }

                writer.println();
                writer.println("--------------------------------");
                writer.println("TOTAL: " + CurrencyFormatter.format(order.getTotalAmount()));
                writer.println("Payment: " + payment.getPaymentMethod().toUpperCase());
                writer.println("Paid: " + CurrencyFormatter.format(payment.getAmountPaid()));
                writer.println("Change: " + CurrencyFormatter.format(payment.getChangeAmount()));
                writer.println("================================");
                writer.println();
                writer.println("  Thank you for dining with us!");
                writer.println();

                AlertUtil.showInfo("Saved", "Receipt saved successfully!");

            } catch (Exception e) {
                AlertUtil.showError("Error", "Failed to save receipt: " + e.getMessage());
            }
        }
    }

    /**
     * Cancel current selection
     */
    @FXML
    private void cancelSelection() {
        if (selectedOrderRow != null) {
            selectedOrderRow.getStyleClass().remove("order-row-selected");
        }
        selectedOrder = null;
        selectedOrderRow = null;
        showEmptyPaymentPanel();
    }

    /**
     * Reset payment form
     */
    private void resetPaymentForm() {
        selectedPaymentMethod = Payment.METHOD_CASH;
        amountPaidField.clear();
        changeLabel.setText("Rp 0");
        changeContainer.getStyleClass().remove("change-error");
        processPaymentBtn.setDisable(true);

        // Reset payment method buttons
        for (var node : paymentMethodContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox btn = (VBox) node;
                btn.getStyleClass().remove("payment-method-btn-selected");
                if (Payment.METHOD_CASH.equals(btn.getUserData())) {
                    btn.getStyleClass().add("payment-method-btn-selected");
                }
            }
        }
    }

    /**
     * Show empty state in payment panel
     */
    private void showEmptyPaymentPanel() {
        orderDetailContainer.setVisible(false);
        orderDetailContainer.setManaged(false);

        // Show empty message if not already showing
        if (paymentPanel.lookup(".empty-state") == null) {
            VBox emptyState = UIFeedback.createEmptyState(
                    "🧾",
                    "Pilih Pesanan",
                    "Klik pesanan dari daftar untuk memproses pembayaran");
            emptyState.getStyleClass().add("empty-state");

            // Add as first child after panel title
            if (paymentPanel.getChildren().size() > 0) {
                paymentPanel.getChildren().add(1, emptyState);
            } else {
                paymentPanel.getChildren().add(emptyState);
            }
        }
    }

    /**
     * Show empty order list message
     */
    private void showEmptyOrderList() {
        VBox emptyState = UIFeedback.createEmptyState(
                "✅",
                "Semua Selesai!",
                "Tidak ada pembayaran yang menunggu");
        emptyState.setPadding(new Insets(40));
        orderListContainer.getChildren().add(emptyState);
    }

    /**
     * Show no results message
     */
    private void showNoResultsMessage() {
        VBox noResults = UIFeedback.createEmptyState(
                "🔍",
                "Tidak Ditemukan",
                "Tidak ada pesanan yang cocok dengan pencarian");
        noResults.setPadding(new Insets(40));
        orderListContainer.getChildren().add(noResults);
    }

    /**
     * Update dashboard stats
     */
    private void updateStats() {
        // Pending orders (delivered, awaiting payment)
        List<Order> pending = orderDAO.findByStatus(Order.STATUS_DELIVERED);
        pendingCountLabel.setText(String.valueOf(pending.size()));

        // Completed today
        List<Payment> todayPayments = paymentDAO.findToday();
        completedCountLabel.setText(String.valueOf(todayPayments.size()));

        // Today's revenue
        BigDecimal revenue = BigDecimal.ZERO;
        for (Payment p : todayPayments) {
            Order order = orderDAO.findById(p.getOrderId());
            if (order != null) {
                revenue = revenue.add(order.getTotalAmount());
            }
        }
        todayRevenueLabel.setText(CurrencyFormatter.formatShort(revenue));
    }

    /**
     * Refresh data
     */
    @FXML
    private void refreshData() {
        loadDeliveredOrders();
        updateStats();
    }

    /**
     * Logout handler
     */
    @FXML
    private void handleLogout() {
        if (autoRefresh != null) {
            autoRefresh.stop();
        }

        boolean confirm = AlertUtil.showConfirm("Logout", "Are you sure you want to logout?");
        if (confirm) {
            SessionManager.getInstance().clearSession();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) userNameLabel.getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/css/login.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("Restos - Login");
                stage.centerOnScreen();
            } catch (Exception e) {
                e.printStackTrace();
                AlertUtil.showError("Error", "Failed to logout: " + e.getMessage());
            }
        }
    }

    /**
     * Handle refresh button click
     */
    @FXML
    private void handleRefresh() {
        loadDeliveredOrders();
        updateStats();
    }

    /**
     * Cleanup on window close
     */
    public void cleanup() {
        if (autoRefresh != null) {
            autoRefresh.stop();
        }
    }
}
