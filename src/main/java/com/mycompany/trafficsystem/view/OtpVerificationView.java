package com.mycompany.trafficsystem.view;

import com.mycompany.trafficsystem.controller.ForgotPasswordController;
import com.mycompany.trafficsystem.model.PasswordResetRequest;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class OtpVerificationView {

    private final ForgotPasswordController controller;
    private final PasswordResetRequest request;

    public OtpVerificationView(ForgotPasswordController controller, PasswordResetRequest request) {
        this.controller = controller;
        this.request = request;
    }

    public void show(Stage stage) {
        StackPane root = new StackPane();
        root.setPrefSize(1200, 720);
        root.setStyle("-fx-background-color: #eaf1ff;");

        VBox card = new VBox(18);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(450);
        card.setPadding(new Insets(32));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 18; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 20, 0, 0, 8);");

        HBox backBox = new HBox();
        backBox.setAlignment(Pos.CENTER_LEFT);
        backBox.setMaxWidth(Double.MAX_VALUE);
        Button backButton = new Button("← Quay lại");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #6b7280; -fx-font-size: 14px; -fx-cursor: hand;");
        backButton.setOnAction(e -> new ForgotPasswordView(controller).show(stage));
        backBox.getChildren().add(backButton);

        Label icon = new Label("✉");
        icon.setStyle("-fx-font-size: 34px; -fx-background-color: #d8fbe4; -fx-background-radius: 40; -fx-padding: 14;");

        Label title = new Label("Xác thực OTP");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        Label subtitle = new Label("Mã xác thực đã được gửi đến " + request.getDestination());
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");

        VBox demoBox = new VBox(8);
        demoBox.setAlignment(Pos.CENTER);
        demoBox.setPadding(new Insets(14));
        demoBox.setMaxWidth(Double.MAX_VALUE);
        demoBox.setStyle("-fx-background-color: #fff9e8; -fx-border-color: #ffd05a; -fx-border-radius: 9; -fx-background-radius: 9;");

        Label demoTitle = new Label("Đây là sản phẩm thử nghiệm");
        demoTitle.setStyle("-fx-text-fill: #92400e; -fx-font-weight: bold;");
        Label otpLabel = new Label("OTP:  " + request.getOtpCode());
        otpLabel.setStyle("-fx-text-fill: #92400e; -fx-font-size: 18px; -fx-font-weight: bold;");
        demoBox.getChildren().addAll(demoTitle, otpLabel);

        Label inputLabel = new Label("Nhập mã OTP");
        inputLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        TextField otpField = new TextField();
        otpField.setPromptText("Nhập 6 chữ số");
        otpField.setAlignment(Pos.CENTER);
        otpField.setPrefHeight(48);
        otpField.setMaxWidth(190);
        otpField.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: #f9fafb; -fx-border-color: #d1d5db; -fx-border-radius: 9; -fx-background-radius: 9;");

        Button confirmButton = new Button("Xác nhận");
        confirmButton.setPrefHeight(48);
        confirmButton.setMaxWidth(Double.MAX_VALUE);
        confirmButton.setStyle(primaryButtonStyle());
        confirmButton.setOnAction(e -> controller.verifyOtp(stage, request, otpField.getText()));

        HBox resendBox = new HBox(4);
        resendBox.setAlignment(Pos.CENTER);
        Label resendText = new Label("Không nhận được mã?");
        resendText.setStyle("-fx-text-fill: #6b7280;");
        Button resendButton = new Button("Gửi lại");
        resendButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #4f35f5; -fx-cursor: hand;");
        resendButton.setOnAction(e -> controller.resendOtp(stage, request));
        resendBox.getChildren().addAll(resendText, resendButton);

        card.getChildren().addAll(backBox, icon, title, subtitle, demoBox, inputLabel, otpField, confirmButton, resendBox);
        root.getChildren().add(card);

        Scene scene = new Scene(root, 1200, 720);
        stage.setTitle("TP.HCM - Xác thực OTP");
        stage.setScene(scene);
        stage.show();
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
