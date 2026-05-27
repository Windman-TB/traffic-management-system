/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.view;

/**
 *
 * @author engineer
 */
import com.mycompany.trafficsystem.controller.EmployeeController;
import com.mycompany.trafficsystem.model.Employee;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

public class EmployeeView {

    private static final String GMAIL_REGEX = "^[A-Za-z0-9+_.-]+@gmail\\.com$";
    private static final String VIETNAM_PHONE_REGEX = "^(0\\d{9}|\\+84\\d{9})$";

    private static final String BG = "#EEF3F8";
    private static final String CARD_BG = "#FFFFFF";
    private static final String TEXT_MUTED = "#5E7EA5";
    private static final String PRIMARY = "#123B63";
    private static final String BORDER = "#DDE5EF";

    private final EmployeeController employeeController;
    private final TableView<Employee> table;
    private final TextField searchField;

    public EmployeeView() {
        this.employeeController = new EmployeeController();
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
        TableView<Employee> employeeTable = createTable();

        card.getChildren().addAll(toolbar, employeeTable);
        VBox.setVgrow(employeeTable, Priority.ALWAYS);

        root.getChildren().add(card);
        VBox.setVgrow(card, Priority.ALWAYS);

        loadData();

        return root;
    }

    private HBox createToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(14, 16, 14, 16));

        Label title = new Label("Quản lý nhân viên");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        title.setTextFill(Color.web("#111827"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        searchField.setPromptText("Tìm kiếm...");
        searchField.setPrefWidth(250);
        searchField.setStyle(
                "-fx-background-radius: 8;" +
                "-fx-border-radius: 8;" +
                "-fx-border-color: #CBD5E1;" +
                "-fx-padding: 8 10;" +
                "-fx-background-color: white;"
        );

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            table.setItems(FXCollections.observableArrayList(employeeController.searchEmployees(newValue)));
        });

        Button addButton = new Button("+  Thêm nhân viên");
        addButton.setStyle(
                "-fx-background-color: " + PRIMARY + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 7;" +
                "-fx-padding: 8 14;"
        );

        addButton.setOnAction(e -> showAddEmployeeDialog());

        toolbar.getChildren().addAll(title, spacer, searchField, addButton);

        return toolbar;
    }

    private TableView<Employee> createTable() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPrefHeight(460);
        table.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: transparent;"
        );

        TableColumn<Employee, String> idCol = new TableColumn<>("MÃ");
        idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDisplayEmployeeId()));

        TableColumn<Employee, String> nameCol = new TableColumn<>("HỌ TÊN");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getFullName())));

        TableColumn<Employee, String> phoneCol = new TableColumn<>("SỐ ĐIỆN THOẠI");
        phoneCol.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getPhoneNumber())));

        TableColumn<Employee, String> emailCol = new TableColumn<>("EMAIL");
        emailCol.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getEmail())));

        TableColumn<Employee, String> dobCol = new TableColumn<>("NGÀY SINH");
        dobCol.setCellValueFactory(data -> new SimpleStringProperty(formatDate(data.getValue().getDateOfBirth())));

        TableColumn<Employee, String> genderCol = new TableColumn<>("GIỚI TÍNH");
        genderCol.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getGender())));

        TableColumn<Employee, String> salaryCol = new TableColumn<>("LƯƠNG");
        salaryCol.setCellValueFactory(data -> new SimpleStringProperty(formatSalary(data.getValue().getSalary())));

        TableColumn<Employee, String> statusCol = new TableColumn<>("TRẠNG THÁI");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getStatus())));

        TableColumn<Employee, Void> actionCol = new TableColumn<>("");
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
                    Employee employee = getTableView().getItems().get(getIndex());
                    showEmployeeDetail(employee);
                });

                editButton.setOnAction(e -> {
                    Employee employee = getTableView().getItems().get(getIndex());
                    showEditEmployeeDialog(employee);
                });

                deleteButton.setOnAction(e -> {
                    Employee employee = getTableView().getItems().get(getIndex());
                    handleDelete(employee);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                }
            }
        });

        table.getColumns().addAll(
                idCol,
                nameCol,
                phoneCol,
                emailCol,
                dobCol,
                genderCol,
                salaryCol,
                statusCol,
                actionCol
        );

        return table;
    }

    private void loadData() {
        table.setItems(FXCollections.observableArrayList(employeeController.getAllEmployees()));
    }

    private void showEmployeeDetail(Employee employee) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi tiết nhân viên");
        alert.setHeaderText(employee.getFullName());
        alert.setContentText(
                "Mã: " + employee.getDisplayEmployeeId() + "\n" +
                "Họ tên: " + nullToEmpty(employee.getFullName()) + "\n" +
                "Số điện thoại: " + nullToEmpty(employee.getPhoneNumber()) + "\n" +
                "Email: " + nullToEmpty(employee.getEmail()) + "\n" +
                "Ngày sinh: " + formatDate(employee.getDateOfBirth()) + "\n" +
                "Giới tính: " + nullToEmpty(employee.getGender()) + "\n" +
                "Địa chỉ: " + nullToEmpty(employee.getAddress()) + "\n" +
                "Lương: " + formatSalary(employee.getSalary()) + "\n" +
                "Trạng thái: " + nullToEmpty(employee.getStatus())
        );
        alert.showAndWait();
    }

    private void handleDelete(Employee employee) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc chắn muốn xóa nhân viên " + employee.getFullName() + " không?");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = employeeController.deleteEmployee(employee.getEmployeeId());

            if (success) {
                loadData();
            } else {
                showErrorAlert("Không thể xóa nhân viên.");
            }
        }
    }

    private void showAddEmployeeDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Thêm nhân viên");
        dialog.setHeaderText("Nhập thông tin nhân viên mới");

        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        EmployeeFormControls controls = createEmployeeForm(null);
        dialog.getDialogPane().setContent(controls.form);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String errorMessage = validateEmployeeInput(controls);

            if (errorMessage != null) {
                showErrorAlert(errorMessage);
                event.consume();
                return;
            }

            boolean success = employeeController.addEmployee(
                    controls.fullNameField.getText(),
                    controls.phoneNumberField.getText(),
                    controls.emailField.getText(),
                    controls.dateOfBirthPicker.getValue(),
                    controls.genderBox.getValue(),
                    controls.addressField.getText(),
                    parseSalary(controls.salaryField.getText()),
                    controls.statusBox.getValue()
            );

            if (success) {
                loadData();
                showInfoAlert("Thêm nhân viên thành công.");
            } else {
                showErrorAlert("Không thể thêm nhân viên.");
                event.consume();
            }
        });

        dialog.showAndWait();
    }

    private void showEditEmployeeDialog(Employee employee) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Sửa nhân viên");
        dialog.setHeaderText("Cập nhật thông tin nhân viên");

        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        EmployeeFormControls controls = createEmployeeForm(employee);
        dialog.getDialogPane().setContent(controls.form);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String errorMessage = validateEmployeeInput(controls);

            if (errorMessage != null) {
                showErrorAlert(errorMessage);
                event.consume();
                return;
            }

            boolean success = employeeController.updateEmployee(
                    employee.getEmployeeId(),
                    controls.fullNameField.getText(),
                    controls.phoneNumberField.getText(),
                    controls.emailField.getText(),
                    controls.dateOfBirthPicker.getValue(),
                    controls.genderBox.getValue(),
                    controls.addressField.getText(),
                    parseSalary(controls.salaryField.getText()),
                    controls.statusBox.getValue()
            );

            if (success) {
                loadData();
                showInfoAlert("Cập nhật nhân viên thành công.");
            } else {
                showErrorAlert("Không thể cập nhật nhân viên.");
                event.consume();
            }
        });

        dialog.showAndWait();
    }

    private EmployeeFormControls createEmployeeForm(Employee employee) {
        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(12);
        form.setPadding(new Insets(14));

        TextField employeeIdField = new TextField();
        employeeIdField.setEditable(false);
        employeeIdField.setText(employee == null ? employeeController.generateNextEmployeeId() : employee.getEmployeeId());

        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Nhập họ tên");
        fullNameField.setText(employee == null ? "" : nullToEmpty(employee.getFullName()));

        TextField phoneNumberField = new TextField();
        phoneNumberField.setPromptText("Nhập số điện thoại");
        phoneNumberField.setText(employee == null ? "" : nullToEmpty(employee.getPhoneNumber()));

        TextField emailField = new TextField();
        emailField.setPromptText("Nhập email");
        emailField.setText(employee == null ? "" : nullToEmpty(employee.getEmail()));

        DatePicker dateOfBirthPicker = new DatePicker();
        dateOfBirthPicker.setMaxWidth(Double.MAX_VALUE);
        dateOfBirthPicker.setValue(employee == null ? null : employee.getDateOfBirth());

        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("Nam", "Nữ", "Khác");
        genderBox.setValue(employee == null ? "Nam" : employee.getGender());
        genderBox.setMaxWidth(Double.MAX_VALUE);

        TextField addressField = new TextField();
        addressField.setPromptText("Nhập địa chỉ");
        addressField.setText(employee == null ? "" : nullToEmpty(employee.getAddress()));

        TextField salaryField = new TextField();
        salaryField.setPromptText("Ví dụ: 12000000");
        salaryField.setText(employee == null || employee.getSalary() == null ? "" : employee.getSalary().toPlainString());

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("ACTIVE", "INACTIVE");
        statusBox.setValue(employee == null || employee.getStatus() == null ? "ACTIVE" : employee.getStatus());
        statusBox.setMaxWidth(Double.MAX_VALUE);

        form.add(new Label("Mã nhân viên:"), 0, 0);
        form.add(employeeIdField, 1, 0);

        form.add(new Label("Họ tên:"), 0, 1);
        form.add(fullNameField, 1, 1);

        form.add(new Label("Số điện thoại:"), 0, 2);
        form.add(phoneNumberField, 1, 2);

        form.add(new Label("Email:"), 0, 3);
        form.add(emailField, 1, 3);

        form.add(new Label("Ngày sinh:"), 0, 4);
        form.add(dateOfBirthPicker, 1, 4);

        form.add(new Label("Giới tính:"), 0, 5);
        form.add(genderBox, 1, 5);

        form.add(new Label("Địa chỉ:"), 0, 6);
        form.add(addressField, 1, 6);

        form.add(new Label("Lương:"), 0, 7);
        form.add(salaryField, 1, 7);

        form.add(new Label("Trạng thái:"), 0, 8);
        form.add(statusBox, 1, 8);

        ColumnConstraints labelCol = new ColumnConstraints();
        labelCol.setMinWidth(120);
        ColumnConstraints inputCol = new ColumnConstraints();
        inputCol.setMinWidth(280);
        form.getColumnConstraints().addAll(labelCol, inputCol);

        return new EmployeeFormControls(
                form,
                employeeIdField,
                fullNameField,
                phoneNumberField,
                emailField,
                dateOfBirthPicker,
                genderBox,
                addressField,
                salaryField,
                statusBox
        );
    }

    private String validateEmployeeInput(EmployeeFormControls controls) {
        String fullName = controls.fullNameField.getText();
        String phoneNumber = controls.phoneNumberField.getText();
        String email = controls.emailField.getText();
        String salary = controls.salaryField.getText();
        String status = controls.statusBox.getValue();
        LocalDate dateOfBirth = controls.dateOfBirthPicker.getValue();

        if (fullName == null || fullName.trim().isEmpty()) {
            return "Họ tên không được để trống.";
        }

        if (fullName.trim().length() < 2) {
            return "Họ tên phải có ít nhất 2 ký tự.";
        }

        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return "Số điện thoại không được để trống.";
        }

        if (!phoneNumber.trim().matches(VIETNAM_PHONE_REGEX)) {
            return "Số điện thoại phải có dạng 0xxxxxxxxx hoặc +84xxxxxxxxx.";
        }

        if (email == null || email.trim().isEmpty()) {
            return "Email không được để trống.";
        }

        if (!email.trim().matches(GMAIL_REGEX)) {
            return "Email phải là địa chỉ Gmail. Ví dụ: example@gmail.com";
        }

        if (dateOfBirth != null && dateOfBirth.isAfter(LocalDate.now())) {
            return "Ngày sinh không được lớn hơn ngày hiện tại.";
        }

        if (salary != null && !salary.trim().isEmpty()) {
            try {
                BigDecimal parsedSalary = parseSalary(salary);

                if (parsedSalary.compareTo(BigDecimal.ZERO) < 0) {
                    return "Lương không được nhỏ hơn 0.";
                }
            } catch (NumberFormatException e) {
                return "Lương phải là số hợp lệ. Ví dụ: 12000000";
            }
        }

        if (status == null || status.trim().isEmpty()) {
            return "Vui lòng chọn trạng thái.";
        }

        return null;
    }

    private BigDecimal parseSalary(String salaryText) {
        if (salaryText == null || salaryText.trim().isEmpty()) {
            return null;
        }

        String normalizedSalary = salaryText.trim().replace(",", "");
        return new BigDecimal(normalizedSalary);
    }

    private String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }

    private String formatSalary(BigDecimal salary) {
        if (salary == null) {
            return "";
        }

        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return numberFormat.format(salary);
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private void styleActionButton(Button button) {
        button.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + TEXT_MUTED + ";" +
                "-fx-font-size: 13;" +
                "-fx-cursor: hand;"
        );
    }

    private String createCardStyle() {
        return "-fx-background-color: " + CARD_BG + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(15, 23, 42, 0.12), 8, 0.2, 0, 2);";
    }

    private void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static class EmployeeFormControls {
        private final GridPane form;
        private final TextField employeeIdField;
        private final TextField fullNameField;
        private final TextField phoneNumberField;
        private final TextField emailField;
        private final DatePicker dateOfBirthPicker;
        private final ComboBox<String> genderBox;
        private final TextField addressField;
        private final TextField salaryField;
        private final ComboBox<String> statusBox;

        private EmployeeFormControls(GridPane form,
                                     TextField employeeIdField,
                                     TextField fullNameField,
                                     TextField phoneNumberField,
                                     TextField emailField,
                                     DatePicker dateOfBirthPicker,
                                     ComboBox<String> genderBox,
                                     TextField addressField,
                                     TextField salaryField,
                                     ComboBox<String> statusBox) {
            this.form = form;
            this.employeeIdField = employeeIdField;
            this.fullNameField = fullNameField;
            this.phoneNumberField = phoneNumberField;
            this.emailField = emailField;
            this.dateOfBirthPicker = dateOfBirthPicker;
            this.genderBox = genderBox;
            this.addressField = addressField;
            this.salaryField = salaryField;
            this.statusBox = statusBox;
        }
    }
}
