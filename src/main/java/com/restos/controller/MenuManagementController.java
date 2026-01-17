package com.restos.controller;

import com.restos.dao.*;
import com.restos.model.Category;
import com.restos.model.Menu;
import com.restos.util.AlertUtil;
import com.restos.util.CurrencyFormatter;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for Menu Management Page
 * Handles CRUD operations for menu items
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class MenuManagementController implements Initializable {

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<Category> categoryFilter;
    @FXML
    private TableView<Menu> menuTable;
    @FXML
    private TableColumn<Menu, String> idColumn;
    @FXML
    private TableColumn<Menu, String> nameColumn;
    @FXML
    private TableColumn<Menu, String> categoryColumn;
    @FXML
    private TableColumn<Menu, String> priceColumn;
    @FXML
    private TableColumn<Menu, String> statusColumn;
    @FXML
    private TableColumn<Menu, Void> actionColumn;

    @FXML
    private Label totalMenuLabel;
    @FXML
    private Label activeMenuLabel;
    @FXML
    private Label inactiveMenuLabel;

    private MenuDAO menuDAO;
    private CategoryDAO categoryDAO;
    private ObservableList<Menu> menuList;
    private FilteredList<Menu> filteredList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuDAO = new MenuDAOImpl();
        categoryDAO = new CategoryDAOImpl();

        setupTableColumns();
        setupCategoryFilter();
        loadMenuData();
    }

    /**
     * Setup table columns
     */
    private void setupTableColumns() {
        idColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));

        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        categoryColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategoryName()));

        priceColumn.setCellValueFactory(
                data -> new SimpleStringProperty(CurrencyFormatter.format(data.getValue().getPrice())));

        statusColumn.setCellValueFactory(
                data -> new SimpleStringProperty(data.getValue().isActive() ? "Aktif" : "Nonaktif"));

        // Status column with badge styling
        statusColumn.setCellFactory(column -> new TableCell<Menu, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(status);
                    badge.getStyleClass().add(status.equals("Aktif") ? "status-completed" : "status-cancelled");
                    setGraphic(badge);
                }
            }
        });

        // Action column with buttons
        actionColumn.setCellFactory(column -> new TableCell<Menu, Void>() {
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑️");
            private final Button toggleBtn = new Button("🔄");
            private final HBox container = new HBox(8);

            {
                editBtn.getStyleClass().addAll("btn", "btn-sm", "btn-secondary");
                deleteBtn.getStyleClass().addAll("btn", "btn-sm", "btn-danger");
                toggleBtn.getStyleClass().addAll("btn", "btn-sm", "btn-warning");

                editBtn.setTooltip(new Tooltip("Edit"));
                deleteBtn.setTooltip(new Tooltip("Hapus"));
                toggleBtn.setTooltip(new Tooltip("Toggle Status"));

                container.setAlignment(Pos.CENTER);
                container.getChildren().addAll(editBtn, toggleBtn, deleteBtn);

                editBtn.setOnAction(event -> {
                    Menu menu = getTableView().getItems().get(getIndex());
                    handleEditMenu(menu);
                });

                deleteBtn.setOnAction(event -> {
                    Menu menu = getTableView().getItems().get(getIndex());
                    handleDeleteMenu(menu);
                });

                toggleBtn.setOnAction(event -> {
                    Menu menu = getTableView().getItems().get(getIndex());
                    handleToggleStatus(menu);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

    /**
     * Setup category filter dropdown
     */
    private void setupCategoryFilter() {
        List<Category> categories = categoryDAO.findAll();

        // Add "All Categories" option
        Category allCategory = new Category();
        allCategory.setId(0);
        allCategory.setName("Semua Kategori");

        ObservableList<Category> categoryList = FXCollections.observableArrayList();
        categoryList.add(allCategory);
        categoryList.addAll(categories);

        categoryFilter.setItems(categoryList);
        categoryFilter.setConverter(new javafx.util.StringConverter<Category>() {
            @Override
            public String toString(Category category) {
                return category != null ? category.getName() : "";
            }

            @Override
            public Category fromString(String string) {
                return null;
            }
        });

        categoryFilter.getSelectionModel().selectFirst();
    }

    /**
     * Load menu data from database
     */
    private void loadMenuData() {
        List<Menu> menus = menuDAO.findAll();
        menuList = FXCollections.observableArrayList(menus);
        filteredList = new FilteredList<>(menuList, p -> true);
        menuTable.setItems(filteredList);

        updateStats();
    }

    /**
     * Update statistics labels
     */
    private void updateStats() {
        int total = menuList.size();
        long active = menuList.stream().filter(Menu::isActive).count();
        long inactive = total - active;

        totalMenuLabel.setText("Total: " + total + " menu");
        activeMenuLabel.setText("Aktif: " + active);
        inactiveMenuLabel.setText("Nonaktif: " + inactive);
    }

    // ==================== EVENT HANDLERS ====================

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase().trim();
        Category selectedCategory = categoryFilter.getValue();

        filteredList.setPredicate(menu -> {
            boolean matchesSearch = searchText.isEmpty() ||
                    menu.getName().toLowerCase().contains(searchText) ||
                    menu.getDescription() != null && menu.getDescription().toLowerCase().contains(searchText);

            boolean matchesCategory = selectedCategory == null ||
                    selectedCategory.getId() == 0 ||
                    menu.getCategoryId() == selectedCategory.getId();

            return matchesSearch && matchesCategory;
        });
    }

    @FXML
    private void handleCategoryFilter() {
        handleSearch(); // Reuse search logic with category filter
    }

    @FXML
    private void handleAddMenu() {
        showMenuDialog(null);
    }

    private void handleEditMenu(Menu menu) {
        showMenuDialog(menu);
    }

    private void handleDeleteMenu(Menu menu) {
        boolean confirm = AlertUtil.showConfirmation("Konfirmasi Hapus",
                "Apakah Anda yakin ingin menghapus menu \"" + menu.getName() + "\"?");

        if (confirm) {
            if (menuDAO.delete(menu.getId())) {
                AlertUtil.showInfo("Sukses", "Menu berhasil dihapus");
                loadMenuData();
            } else {
                AlertUtil.showError("Error", "Gagal menghapus menu");
            }
        }
    }

    private void handleToggleStatus(Menu menu) {
        boolean newStatus = !menu.isActive();
        if (menuDAO.updateStatus(menu.getId(), newStatus)) {
            menu.setActive(newStatus);
            menuTable.refresh();
            updateStats();
            AlertUtil.showInfo("Sukses", "Status menu berhasil diubah");
        } else {
            AlertUtil.showError("Error", "Gagal mengubah status menu");
        }
    }

    /**
     * Show add/edit menu dialog
     */
    private void showMenuDialog(Menu existingMenu) {
        Dialog<Menu> dialog = new Dialog<>();
        dialog.setTitle(existingMenu == null ? "Tambah Menu Baru" : "Edit Menu");
        dialog.setHeaderText(existingMenu == null ? "Masukkan data menu baru" : "Ubah data menu");

        // Set buttons
        ButtonType saveButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("Nama menu");
        nameField.setPrefWidth(300);

        TextArea descField = new TextArea();
        descField.setPromptText("Deskripsi menu");
        descField.setPrefRowCount(3);

        ComboBox<Category> categoryCombo = new ComboBox<>();
        List<Category> categories = categoryDAO.findAll();
        categoryCombo.setItems(FXCollections.observableArrayList(categories));
        categoryCombo.setConverter(new javafx.util.StringConverter<Category>() {
            @Override
            public String toString(Category cat) {
                return cat != null ? cat.getName() : "";
            }

            @Override
            public Category fromString(String string) {
                return null;
            }
        });
        categoryCombo.setPrefWidth(300);

        TextField priceField = new TextField();
        priceField.setPromptText("Harga (contoh: 25000)");

        TextField imageField = new TextField();
        imageField.setPromptText("URL gambar (opsional)");

        CheckBox activeCheck = new CheckBox("Menu aktif");
        activeCheck.setSelected(true);

        // Populate if editing
        if (existingMenu != null) {
            nameField.setText(existingMenu.getName());
            descField.setText(existingMenu.getDescription());
            priceField.setText(existingMenu.getPrice().toString());
            imageField.setText(existingMenu.getImageUrl());
            activeCheck.setSelected(existingMenu.isActive());

            // Select category
            for (Category cat : categories) {
                if (cat.getId() == existingMenu.getCategoryId()) {
                    categoryCombo.setValue(cat);
                    break;
                }
            }
        }

        grid.add(new Label("Nama Menu:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Kategori:"), 0, 1);
        grid.add(categoryCombo, 1, 1);
        grid.add(new Label("Harga:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Deskripsi:"), 0, 3);
        grid.add(descField, 1, 3);
        grid.add(new Label("URL Gambar:"), 0, 4);
        grid.add(imageField, 1, 4);
        grid.add(activeCheck, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Enable/Disable save button based on validation
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            saveButton.setDisable(newVal.trim().isEmpty() ||
                    categoryCombo.getValue() == null ||
                    priceField.getText().trim().isEmpty());
        });

        categoryCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            saveButton.setDisable(nameField.getText().trim().isEmpty() ||
                    newVal == null ||
                    priceField.getText().trim().isEmpty());
        });

        priceField.textProperty().addListener((obs, oldVal, newVal) -> {
            saveButton.setDisable(nameField.getText().trim().isEmpty() ||
                    categoryCombo.getValue() == null ||
                    newVal.trim().isEmpty());
        });

        // Convert result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    Menu menu = existingMenu != null ? existingMenu : new Menu();
                    menu.setName(nameField.getText().trim());
                    menu.setDescription(descField.getText().trim());
                    menu.setCategoryId(categoryCombo.getValue().getId());
                    menu.setPrice(new BigDecimal(priceField.getText().trim()));
                    menu.setImageUrl(imageField.getText().trim());
                    menu.setActive(activeCheck.isSelected());
                    return menu;
                } catch (NumberFormatException e) {
                    AlertUtil.showError("Error", "Format harga tidak valid");
                    return null;
                }
            }
            return null;
        });

        Optional<Menu> result = dialog.showAndWait();
        result.ifPresent(menu -> {
            boolean success;
            if (existingMenu == null) {
                int insertedId = menuDAO.insert(menu);
                success = insertedId > 0;
            } else {
                success = menuDAO.update(menu);
            }

            if (success) {
                AlertUtil.showInfo("Sukses", "Menu berhasil " + (existingMenu == null ? "ditambahkan" : "diperbarui"));
                loadMenuData();
            } else {
                AlertUtil.showError("Error", "Gagal menyimpan menu");
            }
        });
    }
}
