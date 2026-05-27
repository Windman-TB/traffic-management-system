package com.mycompany.trafficsystem.view;

import com.mycompany.trafficsystem.controller.AccountController;
import com.mycompany.trafficsystem.model.AccountManagement;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class AccountView {

    private static final String BG = "#EEF3F8";
    private static final String PRIMARY = "#123B63";
    private static final String BORDER = "#DDE5EF";

    private final AccountController accountController;
    private final TableView<AccountManagement> table;
    private final TextField searchField;

    public AccountView() {
        this.accountController = new AccountController();
        this.table = new TableView<>();
        this.searchField = new TextField();
    }

    public VBox getView() {
        VBox root = new VBox();
        root.setPadding(new Insets(22, 28, 28, 28));
        root.setStyle("-fx-background-color: " + BG + ";");

        VBox card = new VBox();
        card.setStyle(createCardStyle());

        HBox toolbar = createToolbar();
        TableView<AccountManagement> accountTable = createTable();

        card.getChildren().addAll(toolbar, accountTable);
        VBox.setVgrow(accountTable, Priority.ALWAYS);

        root.getChildren().add(card);
        VBox.setVgrow(card, Priority.ALWAYS);

        loadData();

        return root;
    }

    private HBox createToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(14, 16, 14, 16));

        Label title = new Label("Quản lý tài khoản");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        title.setTextFill(Color.web("#111827"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        searchField.setPromptText("Tìm tên đăng nhập, họ tên, vai trò...");
        searchField.setPrefWidth(290);
        searchField.setStyle(
                "-fx-background-radius: 8;" +
                "-fx-border-radius: 8;" +
                "-fx-border-color: #CBD5E1;" +
                "-fx-padding: 8 10;" +
                "-fx-background-color: white;"
        );

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            table.setItems(FXCollections.observableArrayList(accountController.searchAccounts(newValue)));
        });

        Button addButton = new Button("+  Thêm tài khoản");
        addButton.setStyle(
                "-fx-background-color: " + PRIMARY + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 7;" +
                "-fx-padding: 8 14;"
        );
        addButton.setOnAction(e -> showAddAccountDialog());

        toolbar.getChildren().addAll(title, spacer, searchField, addButton);
        return toolbar;
    }

    private TableView<AccountManagement> createTable() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPrefHeight(460);
        table.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: transparent;"
        );

        TableColumn<AccountManagement, String> usernameCol = new TableColumn<>("TÊN ĐĂNG NHẬP");
        usernameCol.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getUsername())));

        TableColumn<AccountManagement, String> fullNameCol = new TableColumn<>("HỌ VÀ TÊN");
        fullNameCol.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getFullName())));

        TableColumn<AccountManagement, String> roleCol = new TableColumn<>("VAI TRÒ");
        roleCol.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getRoleName())));

        TableColumn<AccountManagement, String> statusCol = new TableColumn<>("TRẠNG THÁI");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getStatus())));

        TableColumn<AccountManagement, Void> actionCol = new TableColumn<>("");
        actionCol.setPrefWidth(120);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button viewButton = new Button("👁");
            private final Button editButton = new Button("✎");
            private final Button deleteButton = new Button("🗑");
            private final HBox box = new HBox(8, viewButton, editButton, deleteButton);

            {
                box.setAlignment(Pos.CENTER);
                styleActionButton(viewButton);
                styleActionButton(editButton);
                styleActionButton(deleteButton);

                viewButton.setOnAction(e -> {
                    AccountManagement account = getTableView().getItems().get(getIndex());
                    showAccountDetail(account);
                });

                editButton.setOnAction(e -> {
                    AccountManagement account = getTableView().getItems().get(getIndex());
                    showEditAccountDialog(account);
                });

                deleteButton.setOnAction(e -> {
                    AccountManagement account = getTableView().getItems().get(getIndex());
                    handleDelete(account);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(usernameCol, fullNameCol, roleCol, statusCol, actionCol);
        return table;
    }

    private void loadData() {
        table.setItems(FXCollections.observableArrayList(accountController.getAllAccountsForManagement()));
    }

    private void showAccountDetail(AccountManagement account) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi tiết tài khoản");
        alert.setHeaderText(account.getUsername());
        alert.setContentText(
                "Mã tài khoản: " + nullToEmpty(account.getAccountId()) + "\n" +
                "Mã nhân viên: " + nullToEmpty(account.getEmployeeId()) + "\n" +
                "Họ và tên: " + nullToEmpty(account.getFullName()) + "\n" +
                "Tên đăng nhập: " + nullToEmpty(account.getUsername()) + "\n" +
                "Vai trò: " + nullToEmpty(account.getRoleName()) + "\n" +
                "Trạng thái: " + nullToEmpty(account.getStatus()) + "\n" +
                "Ngày tạo: " + formatDateTime(account.getCreatedAt()) + "\n" +
                "Ngày cập nhật: " + formatDateTime(account.getUpdatedAt())
        );
        alert.showAndWait();
    }

    private void showAddAccountDialog() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Thêm tài khoản");
        dialog.setHeaderText("Nhập thông tin tài khoản mới");

        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        ComboBox<String> employeeBox = new ComboBox<>();
        List<String> employees = accountController.getEmployeesWithoutAccount();
        employeeBox.setItems(FXCollections.observableArrayList(employees));
        employeeBox.setPromptText("Chọn nhân viên");
        employeeBox.setMaxWidth(Double.MAX_VALUE);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Tên đăng nhập");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mật khẩu");

        ComboBox<String> roleBox = createRoleComboBox();
        ComboBox<String> statusBox = createStatusComboBox();
        statusBox.setValue("ACTIVE");

        GridPane grid = createFormGrid();
        grid.add(new Label("Nhân viên:"), 0, 0);
        grid.add(employeeBox, 1, 0);
        grid.add(new Label("Tên đăng nhập:"), 0, 1);
        grid.add(usernameField, 1, 1);
        grid.add(new Label("Mật khẩu:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(new Label("Vai trò:"), 0, 3);
        grid.add(roleBox, 1, 3);
        grid.add(new Label("Trạng thái:"), 0, 4);
        grid.add(statusBox, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String employeeId = extractEmployeeId(employeeBox.getValue());
                    boolean success = accountController.addAccount(
                            employeeId,
                            usernameField.getText(),
                            passwordField.getText(),
                            roleBox.getValue(),
                            statusBox.getValue()
                    );

                    if (success) {
                        showInfo("Thành công", "Thêm tài khoản thành công.");
                        loadData();
                        return true;
                    }

                    showError("Không thể thêm tài khoản.");
                } catch (IllegalArgumentException ex) {
                    showError(ex.getMessage());
                }
            }

            return false;
        });

        if (employees.isEmpty()) {
            showInfo("Thông báo", "Không còn nhân viên nào chưa có tài khoản.");
            return;
        }

        dialog.showAndWait();
    }

    private void showEditAccountDialog(AccountManagement account) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Sửa tài khoản");
        dialog.setHeaderText("Sửa tài khoản: " + account.getUsername());

        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField employeeField = new TextField(account.getEmployeeDisplayText());
        employeeField.setDisable(true);

        TextField usernameField = new TextField(account.getUsername());

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Để trống nếu không đổi mật khẩu");

        ComboBox<String> roleBox = createRoleComboBox();
        roleBox.setValue(account.getRoleName());

        ComboBox<String> statusBox = createStatusComboBox();
        statusBox.setValue(account.getStatus());

        GridPane grid = createFormGrid();
        grid.add(new Label("Nhân viên:"), 0, 0);
        grid.add(employeeField, 1, 0);
        grid.add(new Label("Tên đăng nhập:"), 0, 1);
        grid.add(usernameField, 1, 1);
        grid.add(new Label("Mật khẩu mới:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(new Label("Vai trò:"), 0, 3);
        grid.add(roleBox, 1, 3);
        grid.add(new Label("Trạng thái:"), 0, 4);
        grid.add(statusBox, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    boolean success = accountController.updateAccount(
                            account.getAccountId(),
                            usernameField.getText(),
                            passwordField.getText(),
                            roleBox.getValue(),
                            statusBox.getValue()
                    );

                    if (success) {
                        showInfo("Thành công", "Sửa tài khoản thành công.");
                        loadData();
                        return true;
                    }

                    showError("Không thể sửa tài khoản.");
                } catch (IllegalArgumentException ex) {
                    showError(ex.getMessage());
                }
            }

            return false;
        });

        dialog.showAndWait();
    }

    private void handleDelete(AccountManagement account) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText("Xóa tài khoản: " + account.getUsername());
        confirm.setContentText("Tài khoản sẽ được xóa mềm bằng IS_DELETED = 1. Bạn có chắc chắn không?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = accountController.deleteAccount(account.getAccountId());

            if (success) {
                showInfo("Thành công", "Xóa tài khoản thành công.");
                loadData();
            } else {
                showError("Không thể xóa tài khoản.");
            }
        }
    }

    private ComboBox<String> createRoleComboBox() {
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Quản trị viên", "Kỹ thuật viên", "Phân tích viên");
        roleBox.setPromptText("Chọn vai trò");
        roleBox.setMaxWidth(Double.MAX_VALUE);
        return roleBox;
    }

    private ComboBox<String> createStatusComboBox() {
        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("ACTIVE", "LOCKED", "INACTIVE");
        statusBox.setPromptText("Chọn trạng thái");
        statusBox.setMaxWidth(Double.MAX_VALUE);
        return statusBox;
    }

    private GridPane createFormGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(12));

        ColumnConstraints labelCol = new ColumnConstraints();
        labelCol.setMinWidth(110);

        ColumnConstraints inputCol = new ColumnConstraints();
        inputCol.setHgrow(Priority.ALWAYS);
        inputCol.setMinWidth(280);

        grid.getColumnConstraints().addAll(labelCol, inputCol);
        return grid;
    }

    private String extractEmployeeId(String employeeText) {
        if (employeeText == null || employeeText.trim().isEmpty()) {
            return "";
        }

        int index = employeeText.indexOf(" - ");
        if (index == -1) {
            return employeeText.trim();
        }

        return employeeText.substring(0, index).trim();
    }

    private void styleActionButton(Button button) {
        button.setStyle(
                "-fx-background-color: #F8FAFC;" +
                "-fx-border-color: #CBD5E1;" +
                "-fx-border-radius: 6;" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;"
        );
    }

    private String createCardStyle() {
        return "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-radius: 14;" +
                "-fx-border-color: " + BORDER + ";";
    }

    private String formatDateTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }

        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
