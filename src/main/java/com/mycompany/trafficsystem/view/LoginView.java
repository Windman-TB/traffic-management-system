package com.mycompany.trafficsystem.view;

import com.mycompany.trafficsystem.controller.LoginController;
import com.mycompany.trafficsystem.controller.ForgotPasswordController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView {

    private final LoginController loginController;

    public LoginView() {
        this.loginController = new LoginController();
    }

    public void show(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPrefSize(1200, 720);
        root.setStyle("-fx-background-color: #eef4fa;");

        VBox leftPanel = createLeftPanel();
        VBox rightPanel = createRightPanel(stage);

        root.setLeft(leftPanel);
        root.setCenter(rightPanel);

        Scene scene = new Scene(root, 1200, 720);
        stage.setTitle("TP.HCM - Đăng nhập");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createLeftPanel() {
        VBox left = new VBox();
        left.setPrefWidth(600);
        left.setPadding(new Insets(38, 45, 35, 45));
        left.setStyle("-fx-background-color: #143e6d;");

        HBox logoBox = new HBox(12);
        logoBox.setAlignment(Pos.CENTER_LEFT);

        StackPane logoIcon = new StackPane();
        logoIcon.setPrefSize(42, 42);
        logoIcon.setMaxSize(42, 42);
        logoIcon.setStyle("-fx-background-color: #f9aa18; -fx-background-radius: 12;");

        Label carIcon = new Label("🚗");
        carIcon.setStyle("-fx-font-size: 18px;");
        logoIcon.getChildren().add(carIcon);

        VBox logoTextBox = new VBox(3);
        Label appName = new Label("TP.HCM");
        appName.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Label appSubName = new Label("Hệ thống quản lý giao thông");
        appSubName.setStyle("-fx-text-fill: #9fb6cf; -fx-font-size: 13px;");

        logoTextBox.getChildren().addAll(appName, appSubName);
        logoBox.getChildren().addAll(logoIcon, logoTextBox);

        Region topSpace = new Region();
        topSpace.setPrefHeight(180);

        Label mainTitle = new Label("Quản lý và phân tích\nlưu lượng giao thông\nTP. Hồ Chí Minh");
        mainTitle.setStyle("-fx-text-fill: white; -fx-font-size: 34px; -fx-font-weight: bold; -fx-line-spacing: 3;");

        Label titleHighlight = new Label("");
        titleHighlight.setStyle("-fx-text-fill: #f9aa18;");

        int totalNodes = loginController.getTotalNodes();
        int totalStreets = loginController.getTotalStreets();

        Label description = new Label(
        """
        N\u1ec1n t\u1ea3ng gi\u00e1m s\u00e1t th\u1eddi gian th\u1ef1c cho h\u1ec7 th\u1ed1ng
        giao th\u00f4ng to\u00e0n th\u00e0nh ph\u1ed1 v\u1edbi \t""" + totalNodes + " nút giao và "
        + totalStreets + "\n"
        + "tuyến đường trọng yếu."
        );
        
        description.setStyle("-fx-text-fill: #b5c8dc; -fx-font-size: 15px; -fx-line-spacing: 6;");

        HBox statsBox = new HBox(12);
        statsBox.setPadding(new Insets(35, 0, 0, 0));
        statsBox.getChildren().addAll(
        createStatCard(String.valueOf(totalNodes), "Nút giao"),
        createStatCard(String.valueOf(totalStreets), "Tuyến đường"),
        createStatCard("24/7", "Giám sát")
        );

        Region bottomSpace = new Region();
        VBox.setVgrow(bottomSpace, Priority.ALWAYS);

        Label copyright = new Label("© Khoa HTTT - UIT nhóm DAS");
        copyright.setStyle("-fx-text-fill: #6f8cac; -fx-font-size: 12px;");

        left.getChildren().addAll(logoBox, topSpace, mainTitle, titleHighlight, description, statsBox, bottomSpace, copyright);
        return left;
    }

    private VBox createRightPanel(Stage stage) {
        VBox wrapper = new VBox();
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPadding(new Insets(40));
        wrapper.setStyle("-fx-background-color: #eef4fa;");

        VBox formBox = new VBox(14);
        formBox.setMaxWidth(360);
        formBox.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("Đăng nhập");
        titleLabel.setStyle("-fx-text-fill: #111827; -fx-font-size: 26px; -fx-font-weight: bold;");

        Label subtitleLabel = new Label("Nhập thông tin tài khoản để tiếp tục");
        subtitleLabel.setStyle("-fx-text-fill: #58708d; -fx-font-size: 13px;");

        Label usernameLabel = createFieldLabel("Tên đăng nhập");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Nhập tên đăng nhập");
        usernameField.setPrefHeight(40);
        usernameField.setStyle(getInputStyle());

        Label passwordLabel = createFieldLabel("Mật khẩu");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Nhập mật khẩu");
        passwordField.setPrefHeight(40);
        passwordField.setStyle(getInputStyle());

        Button forgotPasswordButton = new Button("Quên mật khẩu?");
        forgotPasswordButton.setMaxWidth(Double.MAX_VALUE);
        forgotPasswordButton.setAlignment(Pos.CENTER_RIGHT);
        forgotPasswordButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #143e6d; -fx-font-size: 13px; -fx-cursor: hand;");
        forgotPasswordButton.setOnAction(event -> new ForgotPasswordController().openForgotPassword(stage));

        Button loginButton = new Button("Đăng nhập hệ thống");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setPrefHeight(40);
        loginButton.setStyle("-fx-background-color: #143e6d; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 7; -fx-cursor: hand;");

        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            loginController.handleLogin(stage, username, password);
        });

        VBox demoBox = new VBox(4);
        demoBox.setPadding(new Insets(12));
        demoBox.setStyle("-fx-background-color: #e8f0f8; -fx-background-radius: 8;");

        Label demoTitle = new Label("Tài khoản demo:");
        demoTitle.setStyle("-fx-text-fill: #4f75a0; -fx-font-size: 12px; -fx-font-weight: bold;");

        Label demoText = new Label("Nhập tài khoản và mật khẩu. Hệ thống sẽ tự xác định vai trò và chuyển đến giao diện phù hợp.");
        demoText.setWrapText(true);
        demoText.setStyle("-fx-text-fill: #4f75a0; -fx-font-size: 12px;");

        demoBox.getChildren().addAll(demoTitle, demoText);

        formBox.getChildren().addAll(
                titleLabel,
                subtitleLabel,
                createSpacer(18),
                usernameLabel,
                usernameField,
                passwordLabel,
                passwordField,
                forgotPasswordButton,
                createSpacer(8),
                loginButton,
                createSpacer(8),
                demoBox
        );

        wrapper.getChildren().add(formBox);
        return wrapper;
    }

    private VBox createStatCard(String number, String text) {
        VBox card = new VBox(5);
        card.setPrefSize(160, 78);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: transparent; -fx-border-color: #2b5988; -fx-border-radius: 9; -fx-background-radius: 9;");

        Label numberLabel = new Label(number);
        numberLabel.setStyle("-fx-text-fill: #f9aa18; -fx-font-size: 24px; -fx-font-weight: bold;");

        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-text-fill: #b5c8dc; -fx-font-size: 13px;");

        card.getChildren().addAll(numberLabel, textLabel);
        return card;
    }

    private Label createFieldLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #111827; -fx-font-size: 13px; -fx-font-weight: bold;");
        return label;
    }

    private Region createSpacer(double height) {
        Region region = new Region();
        region.setPrefHeight(height);
        return region;
    }

    private String getInputStyle() {
        return "-fx-background-color: #f7fbff;"
                + "-fx-border-color: #cbd8e6;"
                + "-fx-border-radius: 7;"
                + "-fx-background-radius: 7;"
                + "-fx-padding: 0 12 0 12;"
                + "-fx-font-size: 13px;";
    }
}
