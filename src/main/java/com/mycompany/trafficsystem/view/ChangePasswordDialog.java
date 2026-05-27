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
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class ChangePasswordDialog {

    private final AccountInfoController controller;

    public ChangePasswordDialog(AccountInfoController controller) {
        this.controller = controller;
    }

    public boolean showAndWait() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Đổi mật khẩu");
        dialog.setHeaderText("Nhập mật khẩu hiện tại và mật khẩu mới");

        ButtonType saveButtonType = new ButtonType("Đổi mật khẩu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        PasswordField currentPasswordField = new PasswordField();
        PasswordField newPasswordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();

        currentPasswordField.setPromptText("Mật khẩu hiện tại");
        newPasswordField.setPromptText("Mật khẩu mới");
        confirmPasswordField.setPromptText("Nhập lại mật khẩu mới");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(18));

        grid.add(new Label("Mật khẩu hiện tại:"), 0, 0);
        grid.add(currentPasswordField, 1, 0);

        grid.add(new Label("Mật khẩu mới:"), 0, 1);
        grid.add(newPasswordField, 1, 1);

        grid.add(new Label("Nhập lại mật khẩu:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button == saveButtonType) {
                String currentPassword = currentPasswordField.getText();
                String newPassword = newPasswordField.getText();
                String confirmPassword = confirmPasswordField.getText();

                if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập đầy đủ thông tin.");
                    return false;
                }

                if (!newPassword.equals(confirmPassword)) {
                    showAlert(Alert.AlertType.WARNING, "Mật khẩu không khớp", "Mật khẩu mới và xác nhận mật khẩu không giống nhau.");
                    return false;
                }

                if (newPassword.length() < 6) {
                    showAlert(Alert.AlertType.WARNING, "Mật khẩu yếu", "Mật khẩu mới nên có ít nhất 6 ký tự.");
                    return false;
                }

                return controller.changeCurrentPassword(currentPassword, newPassword, confirmPassword);
            }

            return false;
        });

        return dialog.showAndWait().orElse(false);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}