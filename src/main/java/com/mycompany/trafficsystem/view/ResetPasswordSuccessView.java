package com.mycompany.trafficsystem.view;

import com.mycompany.trafficsystem.controller.ForgotPasswordController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ResetPasswordSuccessView {

    private final ForgotPasswordController controller;

    public ResetPasswordSuccessView(ForgotPasswordController controller) {
        this.controller = controller;
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

        Label icon = new Label("✓");
        icon.setStyle("-fx-font-size: 36px; -fx-text-fill: #16a34a; -fx-background-color: #dcfce7; -fx-background-radius: 44; -fx-padding: 14 22 14 22;");

        Label title = new Label("Thành công!");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        Label subtitle = new Label("Mật khẩu của bạn đã được đặt lại thành công");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");

        Button loginButton = new Button("Đăng nhập ngay");
        loginButton.setPrefHeight(48);
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setStyle("-fx-background-color: #4f35f5; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 9; -fx-cursor: hand;");
        loginButton.setOnAction(e -> controller.backToLogin(stage));

        card.getChildren().addAll(icon, title, subtitle, loginButton);
        root.getChildren().add(card);

        Scene scene = new Scene(root, 1200, 720);
        stage.setTitle("TP.HCM - Đặt lại mật khẩu thành công");
        stage.setScene(scene);
        stage.show();
    }
}
