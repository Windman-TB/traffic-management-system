package com.mycompany.trafficsystem.view;

import com.mycompany.trafficsystem.controller.ForgotPasswordController;
import com.mycompany.trafficsystem.model.PasswordResetRequest;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ResetPasswordView {

    private final ForgotPasswordController controller;
    private final PasswordResetRequest request;

    public ResetPasswordView(ForgotPasswordController controller, PasswordResetRequest request) {
        this.controller = controller;
        this.request = request;
    }

    public void show(Stage stage) {
        StackPane root = new StackPane();
        root.setPrefSize(1200, 720);
        root.setStyle("-fx-background-color: #eaf1ff;");

        VBox card = new VBox(16);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(450);
        card.setPadding(new Insets(32));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 18; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 20, 0, 0, 8);");

        Label icon = new Label("🔒");
        icon.setStyle("-fx-font-size: 34px; -fx-background-color: #e1e7ff; -fx-background-radius: 40; -fx-padding: 14;");

        Label title = new Label("Đặt mật khẩu mới");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        Label subtitle = new Label("Tạo mật khẩu mới cho tài khoản của bạn");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");

        Label newPasswordLabel = createFieldLabel("Mật khẩu mới");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Nhập mật khẩu mới");
        newPasswordField.setPrefHeight(50);
        newPasswordField.setMaxWidth(Double.MAX_VALUE);
        newPasswordField.setStyle(inputStyle());

        Label confirmPasswordLabel = createFieldLabel("Xác nhận mật khẩu");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Nhập lại mật khẩu mới");
        confirmPasswordField.setPrefHeight(50);
        confirmPasswordField.setMaxWidth(Double.MAX_VALUE);
        confirmPasswordField.setStyle(inputStyle());

        Label note = new Label("Mật khẩu phải có ít nhất 6 ký tự");
        note.setMaxWidth(Double.MAX_VALUE);
        note.setPadding(new Insets(14));
        note.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #1d4ed8; -fx-background-radius: 9; -fx-border-color: #bfdbfe; -fx-border-radius: 9;");

        Button resetButton = new Button("Đặt lại mật khẩu");
        resetButton.setPrefHeight(48);
        resetButton.setMaxWidth(Double.MAX_VALUE);
        resetButton.setStyle(primaryButtonStyle());
        resetButton.setOnAction(e -> controller.resetPassword(stage, request, newPasswordField.getText(), confirmPasswordField.getText()));

        card.getChildren().addAll(icon, title, subtitle, newPasswordLabel, newPasswordField,
                confirmPasswordLabel, confirmPasswordField, note, resetButton);
        root.getChildren().add(card);

        Scene scene = new Scene(root, 1200, 720);
        stage.setTitle("TP.HCM - Đặt mật khẩu mới");
        stage.setScene(scene);
        stage.show();
    }

    private Label createFieldLabel(String text) {
        Label label = new Label(text);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        return label;
    }

    private String inputStyle() {
        return "-fx-background-color: #f9fafb;"
                + "-fx-border-color: #d1d5db;"
                + "-fx-border-radius: 9;"
                + "-fx-background-radius: 9;"
                + "-fx-padding: 0 14 0 14;"
                + "-fx-font-size: 14px;";
    }

    private String primaryButtonStyle() {
        return "-fx-background-color: #4f35f5;"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 15px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 9;"
                + "-fx-cursor: hand;";
    }
}
