package com.restos.controller;

import com.restos.dao.*;
import com.restos.model.Category;
import com.restos.model.Inventory;
import com.restos.model.Menu;
import com.restos.util.AlertUtil;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for Inventory Management Page
 * Handles CRUD operations for daily stock inventory
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class InventoryManagementController implements Initializable {

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<Category> categoryFilter;
    @FXML
    private ComboBox<String> stockFilter;
    @FXML
    private TableView<Inventory> inventoryTable;

    @FXML
    private TableColumn<Inventory, String> colId;
    @FXML
    private TableColumn<Inventory, String> colMenuName;
    @FXML
    private TableColumn<Inventory, String> colCategory;
    @FXML
    private TableColumn<Inventory, String> colDailyStock;
    @FXML
    private TableColumn<Inventory, String> colRemainingStock;
    @FXML
    private TableColumn<Inventory, String> colSold;
    @FXML
    private TableColumn<Inventory, String> colStatus;
    @FXML
    private TableColumn<Inventory, Void> colActions;

    @FXML
    private Label totalItemLabel;
    @FXML
    private Label lowStockLabel;
    @FXML
    private Label outOfStockLabel;
    @FXML
    private Label lastUpdateLabel;

    private InventoryDAO inventoryDAO;
    private MenuDAO menuDAO;
    private CategoryDAO categoryDAO;
    private ObservableList<Inventory> inventoryList;
    private List<Inventory> allInventory;

    private static final int LOW_STOCK_THRESHOLD = 5;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inventoryDAO = new InventoryDAOImpl();
        menuDAO = new MenuDAOImpl();
        categoryDAO = new CategoryDAOImpl();
        inventoryList = FXCollections.observableArrayList();

        setupFilters();
        setupTableColumns();
        loadInventoryData();
    }

    /**
     * Setup filter dropdowns
     */
    private void setupFilters() {
        // Category filter
        List<Category> categories = categoryDAO.findAll();
        Category allCategory = new Category();
        allCategory.setId(0);
        allCategory.setName("Semua Kategori");

        ObservableList<Category> categoryList = FXCollections.observableArrayList();
        categoryList.add(allCategory);
        categoryList.addAll(categories);

        categoryFilter.setItems(categoryList);
        categoryFilter.setConverter(new javafx.util.StringConverter<Category>() {
            @Override
            public String toString(Category cat) {
                return cat != null ? cat.getName() : "";
            }

            @Override
            public Category fromString(String string) {
                return null;
            }
        });
        categoryFilter.getSelectionModel().selectFirst();

        // Stock filter
        ObservableList<String> stockOptions = FXCollections.observableArrayList(
                "Semua Stok",
                "Tersedia",
                "Stok Rendah",
                "Habis");
        stockFilter.setItems(stockOptions);
        stockFilter.getSelectionModel().selectFirst();
    }

    /**
     * Setup table columns
     */
    private void setupTableColumns() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));

        colMenuName.setCellValueFactory(data -> {
            Menu menu = data.getValue().getMenu();
            return new SimpleStringProperty(menu != null ? menu.getName() : "Menu #" + data.getValue().getMenuId());
        });

        colCategory.setCellValueFactory(data -> {
            Menu menu = data.getValue().getMenu();
            return new SimpleStringProperty(menu != null ? menu.getCategoryName() : "-");
        });

        colDailyStock
                .setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getDailyStock())));

        colRemainingStock.setCellValueFactory(
                data -> new SimpleStringProperty(String.valueOf(data.getValue().getRemainingStock())));

        colSold.setCellValueFactory(data -> {
            int sold = data.getValue().getDailyStock() - data.getValue().getRemainingStock();
            return new SimpleStringProperty(String.valueOf(Math.max(0, sold)));
        });

        // Status with badge styling
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Inventory inv = getTableRow().getItem();
                    int remaining = inv.getRemainingStock();

                    Label badge;
                    if (remaining <= 0) {
                        badge = new Label("Habis");
                        badge.getStyleClass().add("status-cancelled");
                    } else if (remaining <= LOW_STOCK_THRESHOLD) {
                        badge = new Label("Rendah");
                        badge.getStyleClass().add("status-pending");
                    } else {
                        badge = new Label("Tersedia");
                        badge.getStyleClass().add("status-completed");
                    }
                    setGraphic(badge);
                }
            }
        });
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getRemainingStock() <= 0 ? "out"
                        : data.getValue().getRemainingStock() <= LOW_STOCK_THRESHOLD ? "low" : "ok"));

        // Action buttons
        colActions.setCellFactory(col -> new TableCell<>() {
            private final HBox container = new HBox(8);
            private final Button editBtn = new Button("✏️");
            private final Button addStockBtn = new Button("➕");
            private final Button deleteBtn = new Button("🗑️");

            {
                editBtn.getStyleClass().addAll("btn", "btn-sm", "btn-secondary");
                editBtn.setTooltip(new Tooltip("Edit Stok"));

                addStockBtn.getStyleClass().addAll("btn", "btn-sm", "btn-success");
                addStockBtn.setTooltip(new Tooltip("Tambah Stok"));

                deleteBtn.getStyleClass().addAll("btn", "btn-sm", "btn-danger");
                deleteBtn.setTooltip(new Tooltip("Hapus"));

                container.getChildren().addAll(editBtn, addStockBtn, deleteBtn);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Inventory inv = getTableView().getItems().get(getIndex());
                    editBtn.setOnAction(e -> handleEditInventory(inv));
                    addStockBtn.setOnAction(e -> handleQuickAddStock(inv));
                    deleteBtn.setOnAction(e -> handleDeleteInventory(inv));
                    setGraphic(container);
                }
            }
        });
    }

    /**
     * Load inventory data from database
     */
    private void loadInventoryData() {
        allInventory = inventoryDAO.findAll();

        // Load menu info for each inventory
        for (Inventory inv : allInventory) {
            Menu menu = menuDAO.findById(inv.getMenuId());
            inv.setMenu(menu);
        }

        inventoryList.setAll(allInventory);
        inventoryTable.setItems(inventoryList);
        updateStats();
        updateLastUpdate();
    }

    /**
     * Update statistics labels
     */
    private void updateStats() {
        int total = allInventory.size();
        long outOfStock = allInventory.stream().filter(i -> i.getRemainingStock() <= 0).count();
        long lowStock = allInventory.stream()
                .filter(i -> i.getRemainingStock() > 0 && i.getRemainingStock() <= LOW_STOCK_THRESHOLD)
                .count();

        totalItemLabel.setText("Total: " + total + " item");
        lowStockLabel.setText("⚠️ Stok Rendah: " + lowStock);
        outOfStockLabel.setText("❌ Habis: " + outOfStock);
    }

    /**
     * Update last update timestamp
     */
    private void updateLastUpdate() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        lastUpdateLabel.setText("Terakhir diupdate: " + timestamp);
    }

    // ==================== EVENT HANDLERS ====================

    @FXML
    private void handleSearch() {
        filterInventory();
    }

    @FXML
    private void handleCategoryFilter() {
        filterInventory();
    }

    @FXML
    private void handleStockFilter() {
        filterInventory();
    }

    @FXML
    private void handleRefresh() {
        loadInventoryData();
    }

    /**
     * Filter inventory based on search and filters
     */
    private void filterInventory() {
        String searchText = searchField.getText().toLowerCase().trim();
        Category selectedCategory = categoryFilter.getValue();
        String stockFilterValue = stockFilter.getValue();

        List<Inventory> filtered = allInventory.stream()
                .filter(inv -> {
                    Menu menu = inv.getMenu();
                    String menuName = menu != null ? menu.getName().toLowerCase() : "";

                    boolean matchesSearch = searchText.isEmpty() || menuName.contains(searchText);

                    boolean matchesCategory = selectedCategory == null ||
                            selectedCategory.getId() == 0 ||
                            (menu != null && menu.getCategoryId() == selectedCategory.getId());

                    boolean matchesStock = stockFilterValue == null || "Semua Stok".equals(stockFilterValue) ||
                            (stockFilterValue.equals("Tersedia") && inv.getRemainingStock() > LOW_STOCK_THRESHOLD) ||
                            (stockFilterValue.equals("Stok Rendah") && inv.getRemainingStock() > 0
                                    && inv.getRemainingStock() <= LOW_STOCK_THRESHOLD)
                            ||
                            (stockFilterValue.equals("Habis") && inv.getRemainingStock() <= 0);

                    return matchesSearch && matchesCategory && matchesStock;
                })
                .toList();

        inventoryList.setAll(filtered);
    }

    @FXML
    private void handleAddInventory() {
        showInventoryDialog(null);
    }

    @FXML
    private void handleResetDailyStock() {
        boolean confirm = AlertUtil.showConfirmation("Konfirmasi Reset",
                "Apakah Anda yakin ingin mereset semua stok ke nilai stok harian?\nTindakan ini akan mengatur ulang sisa stok semua menu.");

        if (confirm) {
            int resetCount = 0;
            for (Inventory inv : allInventory) {
                if (inventoryDAO.updateStock(inv.getId(), inv.getDailyStock())) {
                    resetCount++;
                }
            }
            AlertUtil.showInfo("Sukses", resetCount + " stok berhasil direset");
            loadInventoryData();
        }
    }

    private void handleEditInventory(Inventory inventory) {
        showInventoryDialog(inventory);
    }

    private void handleQuickAddStock(Inventory inventory) {
        TextInputDialog dialog = new TextInputDialog("10");
        dialog.setTitle("Tambah Stok");
        dialog.setHeaderText(
                "Tambah stok untuk: " + (inventory.getMenu() != null ? inventory.getMenu().getName() : "Menu"));
        dialog.setContentText("Jumlah yang ditambahkan:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(value -> {
            try {
                int addAmount = Integer.parseInt(value);
                if (addAmount <= 0) {
                    AlertUtil.showWarning("Peringatan", "Jumlah harus lebih dari 0");
                    return;
                }

                int newStock = inventory.getRemainingStock() + addAmount;
                if (inventoryDAO.updateStock(inventory.getId(), newStock)) {
                    AlertUtil.showInfo("Sukses", "Stok berhasil ditambahkan");
                    loadInventoryData();
                } else {
                    AlertUtil.showError("Error", "Gagal menambahkan stok");
                }
            } catch (NumberFormatException e) {
                AlertUtil.showError("Error", "Format angka tidak valid");
            }
        });
    }

    private void handleDeleteInventory(Inventory inventory) {
        String menuName = inventory.getMenu() != null ? inventory.getMenu().getName()
                : "Menu #" + inventory.getMenuId();
        boolean confirm = AlertUtil.showConfirmation("Konfirmasi Hapus",
                "Apakah Anda yakin ingin menghapus inventaris \"" + menuName + "\"?");

        if (confirm) {
            if (inventoryDAO.delete(inventory.getId())) {
                AlertUtil.showInfo("Sukses", "Inventaris berhasil dihapus");
                loadInventoryData();
            } else {
                AlertUtil.showError("Error", "Gagal menghapus inventaris");
            }
        }
    }

    /**
     * Show add/edit inventory dialog
     */
    private void showInventoryDialog(Inventory existingInventory) {
        Dialog<Inventory> dialog = new Dialog<>();
        dialog.setTitle(existingInventory == null ? "Tambah Inventaris Baru" : "Edit Inventaris");
        dialog.setHeaderText(existingInventory == null ? "Pilih menu dan atur stok" : "Ubah data inventaris");

        ButtonType saveButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        // Menu selection (only for new inventory)
        ComboBox<Menu> menuCombo = new ComboBox<>();
        if (existingInventory == null) {
            List<Menu> menus = menuDAO.findAll();
            // Filter out menus that already have inventory
            List<Integer> existingMenuIds = allInventory.stream()
                    .map(Inventory::getMenuId)
                    .toList();

            List<Menu> availableMenus = menus.stream()
                    .filter(m -> !existingMenuIds.contains(m.getId()))
                    .toList();

            menuCombo.setItems(FXCollections.observableArrayList(availableMenus));
            menuCombo.setConverter(new javafx.util.StringConverter<Menu>() {
                @Override
                public String toString(Menu menu) {
                    return menu != null ? menu.getName() : "";
                }

                @Override
                public Menu fromString(String string) {
                    return null;
                }
            });
            menuCombo.setPrefWidth(300);
        }

        Spinner<Integer> dailyStockSpinner = new Spinner<>(0, 999,
                existingInventory != null ? existingInventory.getDailyStock() : 50);
        dailyStockSpinner.setEditable(true);
        dailyStockSpinner.setPrefWidth(300);

        Spinner<Integer> remainingStockSpinner = new Spinner<>(0, 999,
                existingInventory != null ? existingInventory.getRemainingStock() : 50);
        remainingStockSpinner.setEditable(true);
        remainingStockSpinner.setPrefWidth(300);

        int row = 0;
        if (existingInventory == null) {
            grid.add(new Label("Menu:"), 0, row);
            grid.add(menuCombo, 1, row);
            row++;
        } else {
            Label menuLabel = new Label(existingInventory.getMenu() != null ? existingInventory.getMenu().getName()
                    : "Menu #" + existingInventory.getMenuId());
            menuLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            grid.add(new Label("Menu:"), 0, row);
            grid.add(menuLabel, 1, row);
            row++;
        }

        grid.add(new Label("Stok Harian:"), 0, row);
        grid.add(dailyStockSpinner, 1, row);
        row++;

        grid.add(new Label("Sisa Stok:"), 0, row);
        grid.add(remainingStockSpinner, 1, row);

        dialog.getDialogPane().setContent(grid);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        if (existingInventory == null) {
            saveButton.setDisable(true);
            menuCombo.valueProperty().addListener((obs, o, n) -> saveButton.setDisable(n == null));
        }

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Inventory inv = existingInventory != null ? existingInventory : new Inventory();
                if (existingInventory == null && menuCombo.getValue() != null) {
                    inv.setMenuId(menuCombo.getValue().getId());
                }
                inv.setDailyStock(dailyStockSpinner.getValue());
                inv.setRemainingStock(remainingStockSpinner.getValue());
                return inv;
            }
            return null;
        });

        Optional<Inventory> result = dialog.showAndWait();
        result.ifPresent(inv -> {
            boolean success;
            if (existingInventory == null) {
                int id = inventoryDAO.insert(inv);
                success = id > 0;
            } else {
                success = inventoryDAO.update(inv);
            }

            if (success) {
                AlertUtil.showInfo("Sukses",
                        "Inventaris berhasil " + (existingInventory == null ? "ditambahkan" : "diperbarui"));
                loadInventoryData();
            } else {
                AlertUtil.showError("Error", "Gagal menyimpan inventaris");
            }
        });
    }
}
