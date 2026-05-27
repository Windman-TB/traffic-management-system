/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.view;

/**
 *
 * @author engineer
 */
import com.mycompany.trafficsystem.controller.AccountInfoController;
import com.mycompany.trafficsystem.model.Employee;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class UpdateAccountInfoDialog {

    private final AccountInfoController controller;
    private final Employee employee;

    public UpdateAccountInfoDialog(AccountInfoController controller, Employee employee) {
        this.controller = controller;
        this.employee = employee;
    }

    public Boolean showAndWait() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Cập nhật thông tin");
        dialog.setHeaderText("Cập nhật thông tin liên hệ");

        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField phoneField = new TextField(employee == null ? "" : employee.getPhoneNumber());
        TextField emailField = new TextField(employee == null ? "" : employee.getEmail());
        TextField addressField = new TextField(employee == null ? "" : employee.getAddress());

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(18));

        grid.add(new Label("Số điện thoại:"), 0, 0);
        grid.add(phoneField, 1, 0);

        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);

        grid.add(new Label("Địa chỉ:"), 0, 2);
        grid.add(addressField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == saveButtonType) {
                String phone = phoneField.getText().trim();
                String email = emailField.getText().trim();
                String address = addressField.getText().trim();

                if (phone.isEmpty() || email.isEmpty() || address.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập đầy đủ thông tin.");
                    return false;
                }

                return controller.updateCurrentEmployeeContactInfo(phone, email, address);
            }

            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
