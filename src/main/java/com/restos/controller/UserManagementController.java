package com.restos.controller;

import com.restos.dao.UserDAO;
import com.restos.dao.UserDAOImpl;
import com.restos.model.User;
import com.restos.service.AuthService;
import com.restos.util.AlertUtil;
import com.restos.util.SessionManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for User Management Page
 * Handles CRUD operations for system users
 * 
 * @author Restos Team
 * @version 1.0.0
 */
public class UserManagementController implements Initializable {

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> roleFilter;
    @FXML
    private ComboBox<String> statusFilter;
    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, String> colId;
    @FXML
    private TableColumn<User, String> colName;
    @FXML
    private TableColumn<User, String> colEmail;
    @FXML
    private TableColumn<User, String> colPhone;
    @FXML
    private TableColumn<User, String> colRole;
    @FXML
    private TableColumn<User, String> colStatus;
    @FXML
    private TableColumn<User, String> colCreatedAt;
    @FXML
    private TableColumn<User, Void> colActions;

    @FXML
    private Label totalUserLabel;
    @FXML
    private Label adminCountLabel;
    @FXML
    private Label kasirCountLabel;
    @FXML
    private Label waiterCountLabel;
    @FXML
    private Label chefCountLabel;
    @FXML
    private Label activeCountLabel;
    @FXML
    private Label inactiveCountLabel;

    private UserDAO userDAO;
    private AuthService authService;
    private ObservableList<User> userList;
    private List<User> allUsers;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userDAO = new UserDAOImpl();
        authService = new AuthService();
        userList = FXCollections.observableArrayList();

        setupFilters();
        setupTableColumns();
        loadUserData();
    }

    /**
     * Setup filter dropdowns
     */
    private void setupFilters() {
        // Role filter
        ObservableList<String> roles = FXCollections.observableArrayList(
                "Semua Role",
                "Admin",
                "Kasir",
                "Waiter",
                "Chef");
        roleFilter.setItems(roles);
        roleFilter.getSelectionModel().selectFirst();

        // Status filter
        ObservableList<String> statuses = FXCollections.observableArrayList(
                "Semua Status",
                "Aktif",
                "Non-Aktif");
        statusFilter.setItems(statuses);
        statusFilter.getSelectionModel().selectFirst();
    }

    /**
     * Setup table columns
     */
    private void setupTableColumns() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        colPhone.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getPhone() != null ? data.getValue().getPhone() : "-"));

        // Role with badge styling
        colRole.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    User user = getTableRow().getItem();
                    Label badge = new Label(getRoleDisplayName(user.getRole()));
                    badge.getStyleClass().add(getRoleBadgeStyle(user.getRole()));
                    setGraphic(badge);
                }
            }
        });
        colRole.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRole()));

        // Status with badge styling
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    User user = getTableRow().getItem();
                    boolean isActive = user.getIsActive();
                    Label badge = new Label(isActive ? "Aktif" : "Non-Aktif");
                    badge.getStyleClass().add(isActive ? "status-completed" : "status-cancelled");
                    setGraphic(badge);
                }
            }
        });
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getIsActive() ? "active" : "inactive"));

        colCreatedAt.setCellValueFactory(data -> {
            if (data.getValue().getCreatedAt() != null) {
                return new SimpleStringProperty(data.getValue().getCreatedAt().format(DATE_FORMATTER));
            }
            return new SimpleStringProperty("-");
        });

        // Action buttons
        colActions.setCellFactory(col -> new TableCell<>() {
            private final HBox container = new HBox(4);
            private final Button editBtn = new Button("✏️");
            private final Button toggleBtn = new Button("🔄");
            private final Button resetPwdBtn = new Button("🔑");
            private final Button deleteBtn = new Button("🗑️");

            {
                editBtn.getStyleClass().addAll("btn", "btn-sm", "btn-secondary");
                editBtn.setTooltip(new Tooltip("Edit"));

                toggleBtn.getStyleClass().addAll("btn", "btn-sm", "btn-warning");
                toggleBtn.setTooltip(new Tooltip("Toggle Aktif"));

                resetPwdBtn.getStyleClass().addAll("btn", "btn-sm", "btn-info");
                resetPwdBtn.setTooltip(new Tooltip("Reset Password"));

                deleteBtn.getStyleClass().addAll("btn", "btn-sm", "btn-danger");
                deleteBtn.setTooltip(new Tooltip("Hapus"));

                container.getChildren().addAll(editBtn, toggleBtn, resetPwdBtn, deleteBtn);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());

                    // Don't allow deleting own account
                    User currentUser = SessionManager.getInstance().getCurrentUser();
                    deleteBtn.setDisable(currentUser != null && currentUser.getId() == user.getId());
                    toggleBtn.setDisable(currentUser != null && currentUser.getId() == user.getId());

                    editBtn.setOnAction(e -> handleEditUser(user));
                    toggleBtn.setOnAction(e -> handleToggleStatus(user));
                    resetPwdBtn.setOnAction(e -> handleResetPassword(user));
                    deleteBtn.setOnAction(e -> handleDeleteUser(user));
                    setGraphic(container);
                }
            }
        });
    }

    private String getRoleDisplayName(String role) {
        if (role == null)
            return "-";
        switch (role.toLowerCase()) {
            case "admin":
                return "Admin";
            case "kasir":
                return "Kasir";
            case "waiter":
                return "Pelayan";
            case "chef":
                return "Chef";
            default:
                return role;
        }
    }

    private String getRoleBadgeStyle(String role) {
        if (role == null)
            return "status-pending";
        switch (role.toLowerCase()) {
            case "admin":
                return "status-cancelled";
            case "kasir":
                return "status-completed";
            case "waiter":
                return "status-processing";
            case "chef":
                return "status-pending";
            default:
                return "status-pending";
        }
    }

    /**
     * Load user data from database
     */
    private void loadUserData() {
        allUsers = userDAO.findAll();
        userList.setAll(allUsers);
        userTable.setItems(userList);
        updateStats();
    }

    /**
     * Update statistics labels
     */
    private void updateStats() {
        int total = allUsers.size();
        long admins = allUsers.stream().filter(u -> "admin".equalsIgnoreCase(u.getRole())).count();
        long kasirs = allUsers.stream().filter(u -> "kasir".equalsIgnoreCase(u.getRole())).count();
        long waiters = allUsers.stream().filter(u -> "waiter".equalsIgnoreCase(u.getRole())).count();
        long chefs = allUsers.stream().filter(u -> "chef".equalsIgnoreCase(u.getRole())).count();
        long active = allUsers.stream().filter(User::getIsActive).count();
        long inactive = total - active;

        totalUserLabel.setText("Total: " + total + " pengguna");
        adminCountLabel.setText("Admin: " + admins);
        kasirCountLabel.setText("Kasir: " + kasirs);
        waiterCountLabel.setText("Pelayan: " + waiters);
        chefCountLabel.setText("Chef: " + chefs);
        activeCountLabel.setText("Aktif: " + active);
        inactiveCountLabel.setText("Non-Aktif: " + inactive);
    }

    // ==================== EVENT HANDLERS ====================

    @FXML
    private void handleSearch() {
        filterUsers();
    }

    @FXML
    private void handleRoleFilter() {
        filterUsers();
    }

    @FXML
    private void handleStatusFilter() {
        filterUsers();
    }

    @FXML
    private void handleRefresh() {
        loadUserData();
    }

    /**
     * Filter users based on search and filters
     */
    private void filterUsers() {
        String searchText = searchField.getText().toLowerCase().trim();
        String roleFilterValue = roleFilter.getValue();
        String statusFilterValue = statusFilter.getValue();

        List<User> filtered = allUsers.stream()
                .filter(user -> {
                    boolean matchesSearch = searchText.isEmpty() ||
                            user.getName().toLowerCase().contains(searchText) ||
                            user.getEmail().toLowerCase().contains(searchText) ||
                            (user.getPhone() != null && user.getPhone().contains(searchText));

                    boolean matchesRole = roleFilterValue == null ||
                            "Semua Role".equals(roleFilterValue) ||
                            (roleFilterValue.equalsIgnoreCase("Waiter") && "waiter".equalsIgnoreCase(user.getRole())) ||
                            roleFilterValue.equalsIgnoreCase(user.getRole());

                    boolean matchesStatus = statusFilterValue == null ||
                            "Semua Status".equals(statusFilterValue) ||
                            (statusFilterValue.equals("Aktif") && user.getIsActive()) ||
                            (statusFilterValue.equals("Non-Aktif") && !user.getIsActive());

                    return matchesSearch && matchesRole && matchesStatus;
                })
                .toList();

        userList.setAll(filtered);
    }

    @FXML
    private void handleAddUser() {
        showUserDialog(null);
    }

    private void handleEditUser(User user) {
        showUserDialog(user);
    }

    private void handleToggleStatus(User user) {
        String action = user.getIsActive() ? "menonaktifkan" : "mengaktifkan";
        boolean confirm = AlertUtil.showConfirmation("Konfirmasi",
                "Apakah Anda yakin ingin " + action + " user \"" + user.getName() + "\"?");

        if (confirm) {
            if (userDAO.setActive(user.getId(), !user.getIsActive())) {
                AlertUtil.showInfo("Sukses", "Status user berhasil diubah");
                loadUserData();
            } else {
                AlertUtil.showError("Error", "Gagal mengubah status user");
            }
        }
    }

    private void handleResetPassword(User user) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reset Password");
        dialog.setHeaderText("Reset password untuk: " + user.getName());
        dialog.setContentText("Password baru:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newPassword -> {
            if (newPassword.length() < 6) {
                AlertUtil.showWarning("Peringatan", "Password minimal 6 karakter");
                return;
            }

            if (authService.resetPassword(user.getId(), newPassword)) {
                AlertUtil.showInfo("Sukses", "Password berhasil direset");
            } else {
                AlertUtil.showError("Error", "Gagal mereset password");
            }
        });
    }

    private void handleDeleteUser(User user) {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getId() == user.getId()) {
            AlertUtil.showWarning("Peringatan", "Tidak dapat menghapus akun sendiri");
            return;
        }

        boolean confirm = AlertUtil.showConfirmation("Konfirmasi Hapus",
                "Apakah Anda yakin ingin menghapus user \"" + user.getName()
                        + "\"?\nTindakan ini tidak dapat dibatalkan.");

        if (confirm) {
            if (userDAO.delete(user.getId())) {
                AlertUtil.showInfo("Sukses", "User berhasil dihapus");
                loadUserData();
            } else {
                AlertUtil.showError("Error", "Gagal menghapus user. User mungkin memiliki data terkait.");
            }
        }
    }

    /**
     * Show add/edit user dialog
     */
    private void showUserDialog(User existingUser) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle(existingUser == null ? "Tambah User Baru" : "Edit User");
        dialog.setHeaderText(existingUser == null ? "Masukkan data user baru" : "Ubah data user");

        ButtonType saveButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("Nama lengkap");
        nameField.setPrefWidth(300);

        TextField emailField = new TextField();
        emailField.setPromptText("email@example.com");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(existingUser == null ? "Minimal 6 karakter" : "Kosongkan jika tidak diubah");

        TextField phoneField = new TextField();
        phoneField.setPromptText("08xxxxxxxxxx");

        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.setItems(FXCollections.observableArrayList("admin", "kasir", "waiter", "chef"));
        roleCombo.getSelectionModel().selectFirst();
        roleCombo.setPrefWidth(300);

        CheckBox activeCheck = new CheckBox("Aktif");
        activeCheck.setSelected(true);

        if (existingUser != null) {
            nameField.setText(existingUser.getName());
            emailField.setText(existingUser.getEmail());
            phoneField.setText(existingUser.getPhone());
            roleCombo.setValue(existingUser.getRole());
            activeCheck.setSelected(existingUser.getIsActive());
        }

        grid.add(new Label("Nama:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(new Label("Telepon:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(new Label("Role:"), 0, 4);
        grid.add(roleCombo, 1, 4);
        grid.add(new Label("Status:"), 0, 5);
        grid.add(activeCheck, 1, 5);

        dialog.getDialogPane().setContent(grid);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Validation
        Runnable validateForm = () -> {
            boolean valid = !nameField.getText().trim().isEmpty() &&
                    !emailField.getText().trim().isEmpty() &&
                    emailField.getText().contains("@") &&
                    (existingUser != null || passwordField.getText().length() >= 6);
            saveButton.setDisable(!valid);
        };

        nameField.textProperty().addListener((obs, o, n) -> validateForm.run());
        emailField.textProperty().addListener((obs, o, n) -> validateForm.run());
        passwordField.textProperty().addListener((obs, o, n) -> validateForm.run());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                User user = existingUser != null ? existingUser : new User();
                user.setName(nameField.getText().trim());
                user.setEmail(emailField.getText().trim());
                user.setPhone(phoneField.getText().trim().isEmpty() ? null : phoneField.getText().trim());
                user.setRole(roleCombo.getValue());
                user.setIsActive(activeCheck.isSelected());

                // Password handling
                if (!passwordField.getText().isEmpty()) {
                    user.setPassword(passwordField.getText()); // Will be hashed in DAO
                }

                return user;
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(user -> {
            // Check for duplicate email
            if (existingUser == null && userDAO.emailExists(user.getEmail())) {
                AlertUtil.showError("Error", "Email sudah digunakan");
                return;
            }
            if (existingUser != null && !existingUser.getEmail().equals(user.getEmail())
                    && userDAO.emailExists(user.getEmail())) {
                AlertUtil.showError("Error", "Email sudah digunakan");
                return;
            }

            boolean success;
            if (existingUser == null) {
                // New user - register via AuthService to hash password
                int id = authService.register(
                        user.getName(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getPhone(),
                        user.getRole());
                success = id > 0;

                if (success && !user.getIsActive()) {
                    userDAO.setActive(id, false);
                }
            } else {
                // Update existing user
                success = userDAO.update(user);

                // If password was changed
                if (success && user.getPassword() != null && !user.getPassword().isEmpty()) {
                    authService.resetPassword(user.getId(), user.getPassword());
                }
            }

            if (success) {
                AlertUtil.showInfo("Sukses", "User berhasil " + (existingUser == null ? "ditambahkan" : "diperbarui"));
                loadUserData();
            } else {
                AlertUtil.showError("Error", "Gagal menyimpan user");
            }
        });
    }
}
