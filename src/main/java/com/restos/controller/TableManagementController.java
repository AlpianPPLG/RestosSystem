package com.restos.controller;

import com.restos.dao.TableDAO;
import com.restos.dao.TableDAOImpl;
import com.restos.model.Table;
import com.restos.util.AlertUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for Table Management Page
 * Handles CRUD operations for restaurant tables
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class TableManagementController implements Initializable {

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> statusFilter;
    @FXML
    private FlowPane tableGrid;

    @FXML
    private Label totalTableLabel;
    @FXML
    private Label availableLabel;
    @FXML
    private Label occupiedLabel;
    @FXML
    private Label reservedLabel;

    private TableDAO tableDAO;
    private List<Table> allTables;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableDAO = new TableDAOImpl();

        setupStatusFilter();
        loadTableData();
    }

    /**
     * Setup status filter dropdown
     */
    private void setupStatusFilter() {
        ObservableList<String> statuses = FXCollections.observableArrayList(
                "Semua Status",
                "Tersedia",
                "Terisi",
                "Dipesan");
        statusFilter.setItems(statuses);
        statusFilter.getSelectionModel().selectFirst();
    }

    /**
     * Load table data and create grid
     */
    private void loadTableData() {
        allTables = tableDAO.findAll();
        displayTables(allTables);
        updateStats();
    }

    /**
     * Display tables as cards in grid
     */
    private void displayTables(List<Table> tables) {
        tableGrid.getChildren().clear();

        if (tables.isEmpty()) {
            VBox emptyState = new VBox(12);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setPrefWidth(600);
            emptyState.setPadding(new Insets(40));

            Label icon = new Label("🪑");
            icon.setStyle("-fx-font-size: 48px;");
            Label text = new Label("Belum ada meja");
            text.getStyleClass().add("text-secondary");
            Button addBtn = new Button("Tambah Meja Pertama");
            addBtn.getStyleClass().addAll("btn", "btn-primary");
            addBtn.setOnAction(e -> handleAddTable());

            emptyState.getChildren().addAll(icon, text, addBtn);
            tableGrid.getChildren().add(emptyState);
            return;
        }

        for (Table table : tables) {
            VBox card = createTableCard(table);
            tableGrid.getChildren().add(card);
        }
    }

    /**
     * Create a table card UI component
     */
    private VBox createTableCard(Table table) {
        VBox card = new VBox(8);
        card.setPrefWidth(180);
        card.setPrefHeight(160);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(16));
        card.getStyleClass().add("card");

        // Set border color based on status
        String borderColor;
        String statusText;
        String statusStyle;

        switch (table.getStatus()) {
            case Table.STATUS_OCCUPIED:
                borderColor = "#ef4444";
                statusText = "Terisi";
                statusStyle = "status-cancelled";
                break;
            case Table.STATUS_RESERVED:
                borderColor = "#f97316";
                statusText = "Dipesan";
                statusStyle = "status-pending";
                break;
            default:
                borderColor = "#22c55e";
                statusText = "Tersedia";
                statusStyle = "status-completed";
        }

        card.setStyle("-fx-border-color: " + borderColor + "; -fx-border-width: 2px; -fx-border-radius: 12px;");

        // Table icon
        Label icon = new Label("🪑");
        icon.setStyle("-fx-font-size: 32px;");

        // Table number
        Label numberLabel = new Label(table.getTableNumber());
        numberLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        // Capacity
        Label capacityLabel = new Label("👥 " + table.getCapacity() + " orang");
        capacityLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");

        // Status badge
        Label statusLabel = new Label(statusText);
        statusLabel.getStyleClass().add(statusStyle);

        // Action buttons
        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER);

        Button editBtn = new Button("✏️");
        editBtn.getStyleClass().addAll("btn", "btn-sm", "btn-secondary");
        editBtn.setTooltip(new Tooltip("Edit"));
        editBtn.setOnAction(e -> handleEditTable(table));

        Button deleteBtn = new Button("🗑️");
        deleteBtn.getStyleClass().addAll("btn", "btn-sm", "btn-danger");
        deleteBtn.setTooltip(new Tooltip("Hapus"));
        deleteBtn.setOnAction(e -> handleDeleteTable(table));

        Button statusBtn = new Button("🔄");
        statusBtn.getStyleClass().addAll("btn", "btn-sm", "btn-warning");
        statusBtn.setTooltip(new Tooltip("Ubah Status"));
        statusBtn.setOnAction(e -> handleChangeStatus(table));

        actions.getChildren().addAll(editBtn, statusBtn, deleteBtn);

        card.getChildren().addAll(icon, numberLabel, capacityLabel, statusLabel, actions);

        return card;
    }

    /**
     * Update statistics labels
     */
    private void updateStats() {
        int total = allTables.size();
        long available = allTables.stream().filter(t -> Table.STATUS_AVAILABLE.equals(t.getStatus())).count();
        long occupied = allTables.stream().filter(t -> Table.STATUS_OCCUPIED.equals(t.getStatus())).count();
        long reserved = allTables.stream().filter(t -> Table.STATUS_RESERVED.equals(t.getStatus())).count();

        totalTableLabel.setText("Total: " + total + " meja");
        availableLabel.setText("Tersedia: " + available);
        occupiedLabel.setText("Terisi: " + occupied);
        reservedLabel.setText("Dipesan: " + reserved);
    }

    // ==================== EVENT HANDLERS ====================

    @FXML
    private void handleSearch() {
        filterTables();
    }

    @FXML
    private void handleStatusFilter() {
        filterTables();
    }

    /**
     * Filter tables based on search and status
     */
    private void filterTables() {
        String searchText = searchField.getText().toLowerCase().trim();
        String statusFilterValue = statusFilter.getValue();

        List<Table> filtered = allTables.stream()
                .filter(table -> {
                    boolean matchesSearch = searchText.isEmpty() ||
                            table.getTableNumber().toLowerCase().contains(searchText);

                    boolean matchesStatus = statusFilterValue == null ||
                            "Semua Status".equals(statusFilterValue) ||
                            (statusFilterValue.equals("Tersedia") && Table.STATUS_AVAILABLE.equals(table.getStatus()))
                            ||
                            (statusFilterValue.equals("Terisi") && Table.STATUS_OCCUPIED.equals(table.getStatus())) ||
                            (statusFilterValue.equals("Dipesan") && Table.STATUS_RESERVED.equals(table.getStatus()));

                    return matchesSearch && matchesStatus;
                })
                .toList();

        displayTables(filtered);
    }

    @FXML
    private void handleAddTable() {
        showTableDialog(null);
    }

    private void handleEditTable(Table table) {
        showTableDialog(table);
    }

    private void handleDeleteTable(Table table) {
        if (!Table.STATUS_AVAILABLE.equals(table.getStatus())) {
            AlertUtil.showWarning("Peringatan", "Tidak dapat menghapus meja yang sedang terisi atau dipesan");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation("Konfirmasi Hapus",
                "Apakah Anda yakin ingin menghapus meja \"" + table.getTableNumber() + "\"?");

        if (confirm) {
            if (tableDAO.delete(table.getId())) {
                AlertUtil.showInfo("Sukses", "Meja berhasil dihapus");
                loadTableData();
            } else {
                AlertUtil.showError("Error", "Gagal menghapus meja");
            }
        }
    }

    private void handleChangeStatus(Table table) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(
                getStatusDisplayName(table.getStatus()),
                "Tersedia", "Terisi", "Dipesan");
        dialog.setTitle("Ubah Status Meja");
        dialog.setHeaderText("Pilih status baru untuk meja " + table.getTableNumber());
        dialog.setContentText("Status:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(statusDisplay -> {
            String newStatus;
            switch (statusDisplay) {
                case "Terisi":
                    newStatus = Table.STATUS_OCCUPIED;
                    break;
                case "Dipesan":
                    newStatus = Table.STATUS_RESERVED;
                    break;
                default:
                    newStatus = Table.STATUS_AVAILABLE;
            }

            if (tableDAO.updateStatus(table.getId(), newStatus)) {
                AlertUtil.showInfo("Sukses", "Status meja berhasil diubah");
                loadTableData();
            } else {
                AlertUtil.showError("Error", "Gagal mengubah status meja");
            }
        });
    }

    private String getStatusDisplayName(String status) {
        switch (status) {
            case Table.STATUS_OCCUPIED:
                return "Terisi";
            case Table.STATUS_RESERVED:
                return "Dipesan";
            default:
                return "Tersedia";
        }
    }

    /**
     * Show add/edit table dialog
     */
    private void showTableDialog(Table existingTable) {
        Dialog<Table> dialog = new Dialog<>();
        dialog.setTitle(existingTable == null ? "Tambah Meja Baru" : "Edit Meja");
        dialog.setHeaderText(existingTable == null ? "Masukkan data meja baru" : "Ubah data meja");

        ButtonType saveButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField tableNumberField = new TextField();
        tableNumberField.setPromptText("Nomor meja (contoh: T01)");
        tableNumberField.setPrefWidth(250);

        Spinner<Integer> capacitySpinner = new Spinner<>(1, 20, 4);
        capacitySpinner.setEditable(true);
        capacitySpinner.setPrefWidth(250);

        if (existingTable != null) {
            tableNumberField.setText(existingTable.getTableNumber());
            capacitySpinner.getValueFactory().setValue(existingTable.getCapacity());
        }

        grid.add(new Label("Nomor Meja:"), 0, 0);
        grid.add(tableNumberField, 1, 0);
        grid.add(new Label("Kapasitas:"), 0, 1);
        grid.add(capacitySpinner, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        tableNumberField.textProperty().addListener((obs, oldVal, newVal) -> {
            saveButton.setDisable(newVal.trim().isEmpty());
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Table table = existingTable != null ? existingTable : new Table();
                table.setTableNumber(tableNumberField.getText().trim());
                table.setCapacity(capacitySpinner.getValue());
                if (existingTable == null) {
                    table.setStatus(Table.STATUS_AVAILABLE);
                }
                return table;
            }
            return null;
        });

        Optional<Table> result = dialog.showAndWait();
        result.ifPresent(table -> {
            // Check for duplicate table number
            if (existingTable == null && tableDAO.tableNumberExists(table.getTableNumber())) {
                AlertUtil.showError("Error", "Nomor meja sudah digunakan");
                return;
            }

            boolean success;
            if (existingTable == null) {
                int id = tableDAO.insert(table);
                success = id > 0;
            } else {
                success = tableDAO.update(table);
            }

            if (success) {
                AlertUtil.showInfo("Sukses", "Meja berhasil " + (existingTable == null ? "ditambahkan" : "diperbarui"));
                loadTableData();
            } else {
                AlertUtil.showError("Error", "Gagal menyimpan meja");
            }
        });
    }
}
