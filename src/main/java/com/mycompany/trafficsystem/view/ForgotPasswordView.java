package com.mycompany.trafficsystem.view;

import com.mycompany.trafficsystem.controller.ForgotPasswordController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ForgotPasswordView {

    private final ForgotPasswordController controller;

    public ForgotPasswordView(ForgotPasswordController controller) {
        this.controller = controller;
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

        Label title = new Label("Quên mật khẩu");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        Label subtitle = new Label("Nhập số điện thoại hoặc email để khôi phục tài khoản");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");

        ToggleGroup channelGroup = new ToggleGroup();
        RadioButton phoneRadio = new RadioButton("Số điện thoại");
        RadioButton emailRadio = new RadioButton("Email");
        phoneRadio.setToggleGroup(channelGroup);
        emailRadio.setToggleGroup(channelGroup);
        emailRadio.setSelected(true);

        HBox channelBox = new HBox(18, phoneRadio, emailRadio);
        channelBox.setAlignment(Pos.CENTER);
        channelBox.setPadding(new Insets(8));
        channelBox.setStyle("-fx-background-color: #f1f2f6; -fx-background-radius: 9;");

        Label destinationLabel = new Label("Địa chỉ Email");
        destinationLabel.setMaxWidth(Double.MAX_VALUE);
        destinationLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TextField destinationField = new TextField();
        destinationField.setPromptText("name@example.com");
        destinationField.setPrefHeight(50);
        destinationField.setMaxWidth(Double.MAX_VALUE);
        destinationField.setStyle(inputStyle());

        phoneRadio.setOnAction(e -> {
            destinationLabel.setText("Số điện thoại");
            destinationField.setPromptText("0999999999");
        });

        emailRadio.setOnAction(e -> {
            destinationLabel.setText("Địa chỉ Email");
            destinationField.setPromptText("name@example.com");
        });

        Button continueButton = new Button("Tiếp tục");
        continueButton.setPrefHeight(48);
        continueButton.setMaxWidth(Double.MAX_VALUE);
        continueButton.setStyle(primaryButtonStyle());
        continueButton.setOnAction(e -> {
            String channel = emailRadio.isSelected() ? "EMAIL" : "PHONE";
            controller.sendOtp(stage, channel, destinationField.getText());
        });

        Button backButton = new Button("Quay lại đăng nhập");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #4f46e5; -fx-cursor: hand;");
        backButton.setOnAction(e -> controller.backToLogin(stage));

        card.getChildren().addAll(icon, title, subtitle, channelBox, destinationLabel, destinationField, continueButton, backButton);
        root.getChildren().add(card);

        Scene scene = new Scene(root, 1200, 720);
        stage.setTitle("TP.HCM - Quên mật khẩu");
        stage.setScene(scene);
        stage.show();
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
