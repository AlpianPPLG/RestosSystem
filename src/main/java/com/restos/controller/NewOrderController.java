package com.restos.controller;

import com.restos.dao.*;
import com.restos.model.*;
import com.restos.model.Menu;
import com.restos.util.AlertUtil;
import com.restos.util.CurrencyFormatter;
import com.restos.util.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

/**
 * Controller for New Order Page
 * Handles menu catalog, cart management, and order submission
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class NewOrderController implements Initializable {

    // Category Tabs
    @FXML
    private HBox categoryTabs;

    // Search and Filter
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> sortCombo;

    // Menu Grid
    @FXML
    private FlowPane menuGrid;

    // Cart
    @FXML
    private Label cartTableLabel;
    @FXML
    private Label cartCustomerLabel;
    @FXML
    private VBox cartItemsContainer;
    @FXML
    private VBox emptyCartState;
    @FXML
    private TextArea orderNotesArea;

    // Order Type Toggles
    @FXML
    private ToggleButton dineInToggle;
    @FXML
    private ToggleButton takeAwayToggle;

    // Totals
    @FXML
    private Label subtotalLabel;
    @FXML
    private Label taxLabel;
    @FXML
    private Label totalLabel;

    // Submit Button
    @FXML
    private Button submitButton;

    // DAOs
    private MenuDAO menuDAO;
    private CategoryDAO categoryDAO;
    private OrderDAO orderDAO;
    private OrderItemDAO orderItemDAO;
    private TableDAO tableDAO;
    private InventoryDAO inventoryDAO;

    // State
    private Table currentTable;
    private Order existingOrder;
    private WaiterDashboardController parentController;
    private String customerName = "Tamu";
    private List<Menu> allMenus;
    private List<Category> allCategories;
    private String selectedCategory = null; // null = all

    // Cart items: Map<menuId, CartItem>
    private Map<Integer, CartItem> cartItems = new LinkedHashMap<>();

    // Toggle Group for order type
    private ToggleGroup orderTypeGroup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuDAO = new MenuDAOImpl();
        categoryDAO = new CategoryDAOImpl();
        orderDAO = new OrderDAOImpl();
        orderItemDAO = new OrderItemDAOImpl();
        tableDAO = new TableDAOImpl();
        inventoryDAO = new InventoryDAOImpl();

        // Setup toggle group
        orderTypeGroup = new ToggleGroup();
        dineInToggle.setToggleGroup(orderTypeGroup);
        takeAwayToggle.setToggleGroup(orderTypeGroup);
        dineInToggle.setSelected(true);

        // Setup search listener
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterMenus());

        // Setup sort combo
        sortCombo.getItems().addAll("Nama A-Z", "Nama Z-A", "Harga Terendah", "Harga Tertinggi");
        sortCombo.setValue("Nama A-Z");
        sortCombo.setOnAction(e -> filterMenus());

        // Load data
        loadCategories();
        loadMenus();
        updateCartUI();
    }

    /**
     * Set the table for this order
     */
    public void setTable(Table table) {
        this.currentTable = table;
        cartTableLabel.setText("Meja " + table.getTableNumber());
        cartCustomerLabel.setText(customerName);
    }

    /**
     * Set parent controller for navigation back
     */
    public void setParentController(WaiterDashboardController controller) {
        this.parentController = controller;
    }

    /**
     * Set existing order (for adding items to existing order)
     */
    public void setExistingOrder(Order order) {
        this.existingOrder = order;
        this.customerName = order.getCustomerName();
        cartCustomerLabel.setText(customerName);

        // Load existing items into cart
        List<OrderItem> items = orderItemDAO.findByOrderId(order.getId());
        for (OrderItem item : items) {
            Menu menu = menuDAO.findById(item.getMenuId());
            if (menu != null) {
                CartItem cartItem = new CartItem(menu, item.getQuantity(), item.getNotes());
                cartItems.put(menu.getId(), cartItem);
            }
        }
        updateCartUI();
    }

    /**
     * Load categories for tabs
     */
    private void loadCategories() {
        allCategories = categoryDAO.findAll();

        // Clear existing tabs (except "All")
        categoryTabs.getChildren().clear();

        // Add "All" tab
        Button allTab = new Button("🍽️ Semua");
        allTab.getStyleClass().addAll("category-tab", "category-tab-active");
        allTab.setOnAction(e -> selectCategory(null, allTab));
        categoryTabs.getChildren().add(allTab);

        // Add category tabs
        for (Category cat : allCategories) {
            Button tab = new Button(getCategoryIcon(cat.getName()) + " " + cat.getName());
            tab.getStyleClass().add("category-tab");
            tab.setOnAction(e -> selectCategory(cat.getName(), tab));
            categoryTabs.getChildren().add(tab);
        }
    }

    /**
     * Get icon for category
     */
    private String getCategoryIcon(String categoryName) {
        String name = categoryName.toLowerCase();
        if (name.contains("makanan") || name.contains("food"))
            return "🍔";
        if (name.contains("minuman") || name.contains("drink") || name.contains("beverage"))
            return "🥤";
        if (name.contains("snack") || name.contains("camilan"))
            return "🍿";
        if (name.contains("dessert") || name.contains("penutup"))
            return "🍰";
        if (name.contains("appetizer") || name.contains("pembuka"))
            return "🥗";
        return "📦";
    }

    /**
     * Select category tab
     */
    private void selectCategory(String category, Button selectedTab) {
        this.selectedCategory = category;

        // Update tab styles
        for (var node : categoryTabs.getChildren()) {
            if (node instanceof Button btn) {
                btn.getStyleClass().remove("category-tab-active");
            }
        }
        selectedTab.getStyleClass().add("category-tab-active");

        filterMenus();
    }

    /**
     * Load menus
     */
    private void loadMenus() {
        allMenus = menuDAO.findAllActive();
        filterMenus();
    }

    /**
     * Filter menus based on search, category, and sort
     */
    private void filterMenus() {
        String searchText = searchField.getText().toLowerCase().trim();
        String sortOption = sortCombo.getValue();

        List<Menu> filtered = new ArrayList<>();
        for (Menu menu : allMenus) {
            // Category filter
            if (selectedCategory != null && !selectedCategory.equals(menu.getCategoryName())) {
                continue;
            }

            // Search filter
            if (!searchText.isEmpty() && !menu.getName().toLowerCase().contains(searchText)) {
                continue;
            }

            filtered.add(menu);
        }

        // Sort
        if (sortOption != null) {
            switch (sortOption) {
                case "Nama A-Z":
                    filtered.sort(Comparator.comparing(Menu::getName));
                    break;
                case "Nama Z-A":
                    filtered.sort(Comparator.comparing(Menu::getName).reversed());
                    break;
                case "Harga Terendah":
                    filtered.sort(Comparator.comparing(Menu::getPrice));
                    break;
                case "Harga Tertinggi":
                    filtered.sort(Comparator.comparing(Menu::getPrice).reversed());
                    break;
            }
        }

        displayMenus(filtered);
    }

    /**
     * Display menus in grid
     */
    private void displayMenus(List<Menu> menus) {
        menuGrid.getChildren().clear();

        if (menus.isEmpty()) {
            VBox emptyState = new VBox(12);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setPadding(new Insets(48));

            Label icon = new Label("🔍");
            icon.setStyle("-fx-font-size: 48px; -fx-opacity: 0.5;");
            Label text = new Label("Tidak ada menu ditemukan");
            text.setStyle("-fx-font-size: 16px; -fx-text-fill: #6b7280;");

            emptyState.getChildren().addAll(icon, text);
            menuGrid.getChildren().add(emptyState);
            return;
        }

        for (Menu menu : menus) {
            VBox card = createMenuCard(menu);
            menuGrid.getChildren().add(card);
        }
    }

    /**
     * Create a menu card
     */
    private VBox createMenuCard(Menu menu) {
        VBox card = new VBox(0);
        card.getStyleClass().add("menu-card");

        // Check stock
        int stock = getMenuStock(menu.getId());
        boolean isOutOfStock = stock <= 0;

        if (isOutOfStock) {
            card.getStyleClass().add("menu-card-disabled");
        }

        // Image placeholder
        StackPane imagePlaceholder = new StackPane();
        imagePlaceholder.getStyleClass().add("menu-image-placeholder");
        Label foodIcon = new Label(getCategoryIcon(menu.getCategoryName()));
        foodIcon.setStyle("-fx-font-size: 40px;");
        imagePlaceholder.getChildren().add(foodIcon);

        // Stock badge
        if (isOutOfStock) {
            Label outLabel = new Label("Habis");
            outLabel.setStyle(
                    "-fx-background-color: #ef4444; -fx-text-fill: white; -fx-padding: 4 8; -fx-background-radius: 4; -fx-font-size: 11px;");
            StackPane.setAlignment(outLabel, Pos.TOP_RIGHT);
            StackPane.setMargin(outLabel, new Insets(8));
            imagePlaceholder.getChildren().add(outLabel);
        } else if (stock <= 5) {
            Label lowLabel = new Label("Sisa " + stock);
            lowLabel.setStyle(
                    "-fx-background-color: #f97316; -fx-text-fill: white; -fx-padding: 4 8; -fx-background-radius: 4; -fx-font-size: 11px;");
            StackPane.setAlignment(lowLabel, Pos.TOP_RIGHT);
            StackPane.setMargin(lowLabel, new Insets(8));
            imagePlaceholder.getChildren().add(lowLabel);
        }

        // Info section
        VBox infoSection = new VBox(4);
        infoSection.getStyleClass().add("menu-card-info");

        Label nameLabel = new Label(menu.getName());
        nameLabel.getStyleClass().add("menu-name");
        nameLabel.setWrapText(true);

        Label priceLabel = new Label(CurrencyFormatter.format(menu.getPrice()));
        priceLabel.getStyleClass().add("menu-price");

        infoSection.getChildren().addAll(nameLabel, priceLabel);

        card.getChildren().addAll(imagePlaceholder, infoSection);

        // Click handler
        if (!isOutOfStock) {
            card.setOnMouseClicked(e -> addToCart(menu));
        }

        return card;
    }

    /**
     * Get menu stock from inventory
     */
    private int getMenuStock(int menuId) {
        Inventory inv = inventoryDAO.findByMenuId(menuId);
        if (inv != null) {
            return inv.getRemainingStock();
        }
        return 999; // If no inventory record, assume available
    }

    /**
     * Add menu to cart
     */
    private void addToCart(Menu menu) {
        if (cartItems.containsKey(menu.getId())) {
            // Increase quantity
            CartItem item = cartItems.get(menu.getId());
            int stock = getMenuStock(menu.getId());
            if (item.quantity < stock) {
                item.quantity++;
            } else {
                AlertUtil.showWarning("Stok Terbatas", "Stok " + menu.getName() + " hanya tersedia " + stock);
                return;
            }
        } else {
            // Add new item
            cartItems.put(menu.getId(), new CartItem(menu, 1, ""));
        }
        updateCartUI();
    }

    /**
     * Update cart UI
     */
    private void updateCartUI() {
        // Clear cart container except empty state
        cartItemsContainer.getChildren().clear();

        if (cartItems.isEmpty()) {
            cartItemsContainer.getChildren().add(emptyCartState);
            emptyCartState.setVisible(true);
            emptyCartState.setManaged(true);
        } else {
            emptyCartState.setVisible(false);
            emptyCartState.setManaged(false);

            for (CartItem item : cartItems.values()) {
                HBox cartItemUI = createCartItemUI(item);
                cartItemsContainer.getChildren().add(cartItemUI);
            }
        }

        // Update totals
        updateTotals();
    }

    /**
     * Create cart item UI
     */
    private HBox createCartItemUI(CartItem item) {
        VBox container = new VBox(8);
        container.getStyleClass().add("cart-item");

        // Top row: name, remove button
        HBox topRow = new HBox(8);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(item.menu.getName());
        nameLabel.getStyleClass().add("cart-item-name");
        nameLabel.setWrapText(true);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        Button removeBtn = new Button("🗑️");
        removeBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        removeBtn.setOnAction(e -> {
            cartItems.remove(item.menu.getId());
            updateCartUI();
        });

        topRow.getChildren().addAll(nameLabel, removeBtn);

        // Middle row: price per item
        Label priceLabel = new Label(CurrencyFormatter.format(item.menu.getPrice()) + " / item");
        priceLabel.getStyleClass().add("cart-item-price");

        // Quantity row
        HBox qtyRow = new HBox(8);
        qtyRow.setAlignment(Pos.CENTER_LEFT);

        Button minusBtn = new Button("-");
        minusBtn.getStyleClass().addAll("btn", "btn-secondary", "qty-btn");
        minusBtn.setOnAction(e -> {
            if (item.quantity > 1) {
                item.quantity--;
                updateCartUI();
            }
        });

        Label qtyLabel = new Label(String.valueOf(item.quantity));
        qtyLabel.getStyleClass().add("qty-label");

        Button plusBtn = new Button("+");
        plusBtn.getStyleClass().addAll("btn", "btn-primary", "qty-btn");
        plusBtn.setOnAction(e -> {
            int stock = getMenuStock(item.menu.getId());
            if (item.quantity < stock) {
                item.quantity++;
                updateCartUI();
            } else {
                AlertUtil.showWarning("Stok Terbatas", "Stok maksimal: " + stock);
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label subtotalLabel = new Label(CurrencyFormatter.format(item.getSubtotal()));
        subtotalLabel.getStyleClass().add("cart-item-subtotal");

        qtyRow.getChildren().addAll(minusBtn, qtyLabel, plusBtn, spacer, subtotalLabel);

        // Notes field
        TextField notesField = new TextField(item.notes);
        notesField.setPromptText("Catatan item...");
        notesField.setStyle("-fx-font-size: 11px;");
        notesField.textProperty().addListener((obs, old, newVal) -> item.notes = newVal);

        container.getChildren().addAll(topRow, priceLabel, qtyRow, notesField);

        HBox wrapper = new HBox(container);
        HBox.setHgrow(container, Priority.ALWAYS);
        return wrapper;
    }

    /**
     * Update totals
     */
    private void updateTotals() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : cartItems.values()) {
            subtotal = subtotal.add(item.getSubtotal());
        }

        BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.10));
        BigDecimal total = subtotal.add(tax);

        subtotalLabel.setText(CurrencyFormatter.format(subtotal));
        taxLabel.setText(CurrencyFormatter.format(tax));
        totalLabel.setText(CurrencyFormatter.format(total));

        // Enable/disable submit button
        submitButton.setDisable(cartItems.isEmpty());
    }

    @FXML
    private void filterAll() {
        selectCategory(null, (Button) categoryTabs.getChildren().get(0));
    }

    @FXML
    private void editCustomerName() {
        TextInputDialog dialog = new TextInputDialog(customerName);
        dialog.setTitle("Nama Pelanggan");
        dialog.setHeaderText("Masukkan nama pelanggan");
        dialog.setContentText("Nama:");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                customerName = name.trim();
                cartCustomerLabel.setText(customerName);
            }
        });
    }

    @FXML
    private void handleCancel() {
        if (!cartItems.isEmpty()) {
            boolean confirm = AlertUtil.showConfirmation("Batalkan Pesanan",
                    "Apakah Anda yakin ingin membatalkan pesanan?\nSemua item di keranjang akan hilang.");
            if (!confirm)
                return;
        }

        // Return to table grid
        if (parentController != null) {
            parentController.returnToTableGrid();
        }
    }

    @FXML
    private void handleSubmitOrder() {
        if (cartItems.isEmpty()) {
            AlertUtil.showWarning("Keranjang Kosong", "Tambahkan item ke keranjang terlebih dahulu");
            return;
        }

        // Confirm submission
        boolean confirm = AlertUtil.showConfirmation("Kirim Pesanan",
                "Kirim pesanan untuk " + customerName + " di Meja " + currentTable.getTableNumber() + "?");
        if (!confirm)
            return;

        try {
            User currentUser = SessionManager.getInstance().getCurrentUser();

            // Calculate totals
            BigDecimal subtotal = BigDecimal.ZERO;
            for (CartItem item : cartItems.values()) {
                subtotal = subtotal.add(item.getSubtotal());
            }
            BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.10));
            BigDecimal total = subtotal.add(tax);

            // Create or update order
            Order order;
            if (existingOrder != null) {
                order = existingOrder;
                order.setTotalAmount(total);
                // Notes stored in first order item if needed
                orderDAO.update(order);
            } else {
                order = new Order();
                order.setTableId(currentTable.getId());
                order.setUserId(currentUser.getId());
                order.setCustomerName(customerName);
                order.setOrderType(dineInToggle.isSelected() ? Order.TYPE_DINE_IN : Order.TYPE_TAKE_AWAY);
                order.setStatus(Order.STATUS_PENDING);
                order.setTotalAmount(total);

                int orderId = orderDAO.insert(order);
                order.setId(orderId);

                // Update table status
                currentTable.setStatus(Table.STATUS_OCCUPIED);
                tableDAO.update(currentTable);
            }

            // Insert/update order items
            if (existingOrder != null) {
                // Delete old items first
                orderItemDAO.deleteByOrderId(order.getId());
            }

            for (CartItem item : cartItems.values()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(order.getId());
                orderItem.setMenuId(item.menu.getId());
                orderItem.setQuantity(item.quantity);
                orderItem.setPrice(item.menu.getPrice());
                orderItem.setNotes(item.notes);
                orderItem.setStatus(OrderItem.STATUS_PENDING);

                orderItemDAO.insert(orderItem);

                // Update inventory stock
                Inventory inv = inventoryDAO.findByMenuId(item.menu.getId());
                if (inv != null) {
                    int newStock = Math.max(0, inv.getRemainingStock() - item.quantity);
                    inventoryDAO.updateStock(inv.getId(), newStock);
                }
            }

            AlertUtil.showInfo("Sukses", "Pesanan berhasil dikirim!\nOrder #" + order.getId());

            // Return to table grid
            if (parentController != null) {
                parentController.returnToTableGrid();
            }

        } catch (Exception e) {
            System.err.println("Error creating order: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Error", "Gagal membuat pesanan: " + e.getMessage());
        }
    }

    /**
     * Cart item helper class
     */
    private static class CartItem {
        Menu menu;
        int quantity;
        String notes;

        CartItem(Menu menu, int quantity, String notes) {
            this.menu = menu;
            this.quantity = quantity;
            this.notes = notes;
        }

        BigDecimal getSubtotal() {
            return menu.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
    }
}
