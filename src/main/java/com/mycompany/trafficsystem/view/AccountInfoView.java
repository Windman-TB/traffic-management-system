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
import com.mycompany.trafficsystem.model.Account;
import com.mycompany.trafficsystem.model.AccountRole;
import com.mycompany.trafficsystem.model.Employee;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Alert;


import java.time.format.DateTimeFormatter;

public class AccountInfoView {

    private static final String BG = "#EEF3F8";
    private static final String CARD_BG = "#FFFFFF";
    private static final String TEXT_MUTED = "#5E7EA5";

    private final AccountInfoController accountInfoController;

    private Account account;
    private Employee employee;
    private AccountRole accountRole;
    private ScrollPane rootScrollPane;
    

    public AccountInfoView(AccountInfoController accountInfoController) {
        this.accountInfoController = accountInfoController;
        this.account = accountInfoController.getCurrentAccount();
        this.employee = accountInfoController.getCurrentEmployee();
        this.accountRole = accountInfoController.getCurrentAccountRole();
    }
    
    
    private String getAccountId() {
        return account == null || account.getAccountId() == null ? "N/A" : account.getAccountId();
    }

    private String getEmployeeId() {
        if (employee != null && employee.getEmployeeId() != null) {
            return employee.getEmployeeId();
        }

        if (account != null && account.getEmployeeId() != null) {
            return account.getEmployeeId();
        }

        return "N/A";
    }

    private String getUsername() {
        return account == null || account.getUsername() == null ? "N/A" : account.getUsername();
    }

    private String getFullName() {
        return employee == null || employee.getFullName() == null ? "N/A" : employee.getFullName();
    }

    private String getEmail() {
        return employee == null || employee.getEmail() == null ? "N/A" : employee.getEmail();
    }

    private String getPhoneNumber() {
        return employee == null || employee.getPhoneNumber() == null ? "N/A" : employee.getPhoneNumber();
    }

    private String getGender() {
        return employee == null || employee.getGender() == null ? "N/A" : employee.getGender();
    }

    private String getAddress() {
        return employee == null || employee.getAddress() == null ? "N/A" : employee.getAddress();
    }

    private String getRoleName() {
        if (accountRole != null && accountRole.getRoleName() != null) {
            return accountRole.getRoleName();
        }

        String role = accountInfoController.getCurrentRole();
        return role == null ? "N/A" : role;
    }

    private String getAccountRoleId() {
        return accountRole == null || accountRole.getAccountRoleId() == null ? "N/A" : accountRole.getAccountRoleId();
    }
    
    private String getCreatedAt() {
        if (account == null || account.getCreatedAt() == null) {
            return "N/A";
        }

        return account.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private String getUpdatedAt() {
        if (account == null || account.getUpdatedAt() == null) {
            return "N/A";
        }

        return account.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private String getAssignedAt() {
        if (accountRole == null || accountRole.getAssignedAt() == null) {
            return "N/A";
        }

        return accountRole.getAssignedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    private String getDateOfBirth() {
        if (employee == null || employee.getDateOfBirth() == null) {
            return "N/A";
        }

        return employee.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private String getSalary() {
        if (employee == null || employee.getSalary() == null) {
            return "N/A";
        }

        return String.format("%,.0f", employee.getSalary());
    }
    
    
    private String getAccountStatusText() {
        if (account == null || account.getStatus() == null) {
            return "N/A";
        }

        switch (account.getStatus()) {
            case "ACTIVE":
                return "Hoạt động";
            case "LOCKED":
                return "Đã khóa";
            case "INACTIVE":
                return "Ngừng hoạt động";
            default:
                return account.getStatus();
        }
    }

    private String getEmployeeStatusText() {
        if (employee == null || employee.getStatus() == null) {
            return "N/A";
        }

        switch (employee.getStatus()) {
            case "ACTIVE":
                return "Đang làm";
            case "INACTIVE":
                return "Nghỉ việc";
            default:
                return employee.getStatus();
        }
    }
    
    public ScrollPane getView() {
        rootScrollPane = new ScrollPane(createContent());
        rootScrollPane.setFitToWidth(true);
        rootScrollPane.setStyle("-fx-background-color: transparent;");
        return rootScrollPane;
    }

    private VBox createContent() {
        VBox content = new VBox(18);
        content.setPadding(new Insets(22, 28, 28, 28));
        content.setStyle("-fx-background-color: " + BG + ";");

        Label pageTitle = new Label("Thông tin tài khoản");
        pageTitle.setTextFill(Color.web("#111827"));
        pageTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        VBox profileHeader = createProfileHeader();

        HBox infoCards = new HBox(18);
        infoCards.setAlignment(Pos.TOP_LEFT);

        VBox accountCard = createInfoCard(
                "Hồ sơ tài khoản",
                new String[][]{
                        {"Mã tài khoản", getAccountId()},
                        {"Họ tên", getFullName()},
                        {"Email", getEmail()},
                        {"Tên đăng nhập", getUsername()},
                        {"Trạng thái", getAccountStatusText()},
                        {"Ngày tạo", getCreatedAt()},
                        {"Ngày cập nhật", getUpdatedAt()}
                }
        );

        VBox roleCard = createInfoCard(
                "Phân quyền",
                new String[][]{
                        {"Mã phân quyền", getAccountRoleId()},
                        {"Vai trò", getRoleName()},
                        {"Ngày gán quyền", getAssignedAt()},
                        {"Trạng thái quyền", "Đang hiệu lực"}
                }
        );

        VBox employeeCard = createInfoCard(
                "Thông tin nhân viên",
                new String[][]{
                        {"Mã nhân viên", getEmployeeId()},
                        {"Số điện thoại", getPhoneNumber()},
                        {"Ngày sinh", getDateOfBirth()},
                        {"Giới tính", getGender()},
                        {"Địa chỉ", getAddress()},
                        {"Lương", getSalary()},
                        {"Trạng thái", getEmployeeStatusText()}
                }
        );

        infoCards.getChildren().addAll(accountCard, roleCard, employeeCard);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox actionBox = createActionButtons();

        content.getChildren().addAll(pageTitle, profileHeader, infoCards, spacer, actionBox);

        return content;
    }

    private VBox createProfileHeader() {
        VBox wrapper = new VBox();
        wrapper.setPadding(new Insets(18, 22, 18, 22));
        wrapper.setStyle(createCardStyle());

        HBox profileBox = new HBox(16);
        profileBox.setAlignment(Pos.CENTER_LEFT);

        Label avatar = new Label(getAvatarText());
        avatar.setAlignment(Pos.CENTER);
        avatar.setTextFill(Color.WHITE);
        avatar.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        avatar.setPrefSize(58, 58);
        avatar.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #0EA5E9, #14B8A6);" +
                "-fx-background-radius: 50;"
        );

        VBox textBox = new VBox(5);

        Label fullName = new Label(getFullName());
        fullName.setTextFill(Color.web("#123B63"));
        fullName.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        Label username = new Label("@" + getUsername());
        username.setTextFill(Color.web("#6B7280"));
        username.setFont(Font.font("Arial", 13));

        HBox badges = new HBox(6);
        badges.setAlignment(Pos.CENTER_LEFT);

        Label roleBadge = createSmallBadge(getRoleName(), "#DBEAFE", "#1D4ED8");
        Label statusBadge = createSmallBadge(getAccountStatusText(), "#DCFCE7", "#15803D");

        badges.getChildren().addAll(roleBadge, statusBadge);

        textBox.getChildren().addAll(fullName, username, badges);
        profileBox.getChildren().addAll(avatar, textBox);

        wrapper.getChildren().add(profileBox);
        return wrapper;
    }

    private VBox createInfoCard(String title, String[][] rows) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(18));
        card.setPrefWidth(260);
        card.setMinHeight(250);
        card.setStyle(createCardStyle());

        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.web("#123B63"));
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        VBox rowBox = new VBox(11);

        for (String[] row : rows) {
            HBox item = new HBox(12);
            item.setAlignment(Pos.CENTER_LEFT);

            Label key = new Label(row[0]);
            key.setTextFill(Color.web(TEXT_MUTED));
            key.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            key.setPrefWidth(105);

            Label value = new Label(row[1]);
            value.setTextFill(Color.web("#111827"));
            value.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            value.setMaxWidth(125);
            value.setWrapText(false);

            item.getChildren().addAll(key, value);
            rowBox.getChildren().add(item);
        }

        card.getChildren().addAll(titleLabel, rowBox);
        return card;
    }

    private HBox createActionButtons() {
        HBox actionBox = new HBox(12);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        Button updateInfoBtn = new Button("✎  Cập nhật thông tin");
        updateInfoBtn.setPrefHeight(38);
        updateInfoBtn.setPrefWidth(170);
        updateInfoBtn.setStyle(
                "-fx-background-color: #0EA5E9;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 7;"
        );

        Button changePasswordBtn = new Button("🔒  Đổi mật khẩu");
        changePasswordBtn.setPrefHeight(38);
        changePasswordBtn.setPrefWidth(140);
        changePasswordBtn.setStyle(
                "-fx-background-color: #22C55E;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 7;"
        );

        updateInfoBtn.setOnAction(e -> {
            UpdateAccountInfoDialog dialog = new UpdateAccountInfoDialog(accountInfoController, employee);
            Boolean success = dialog.showAndWait();

            if (success == null) {
                return;
            }

            if (success) {
                reloadData();
                refreshView();
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật thông tin thành công.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Thất bại", "Không thể cập nhật thông tin.");
            }
        });

        changePasswordBtn.setOnAction(e -> {
            ChangePasswordDialog dialog = new ChangePasswordDialog(accountInfoController);
            boolean success = dialog.showAndWait();

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đổi mật khẩu thành công.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Thất bại", "Không thể đổi mật khẩu. Vui lòng kiểm tra lại mật khẩu hiện tại.");
            }
        });

        actionBox.getChildren().addAll(updateInfoBtn, changePasswordBtn);

        return actionBox;
    }

    private Label createSmallBadge(String text, String bgColor, String textColor) {
        Label badge = new Label(text);
        badge.setPadding(new Insets(4, 9, 4, 9));
        badge.setTextFill(Color.web(textColor));
        badge.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        badge.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                "-fx-background-radius: 12;"
        );
        return badge;
    }

    private String createCardStyle() {
        return "-fx-background-color: " + CARD_BG + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #DDE5EF;" +
                "-fx-border-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(15, 23, 42, 0.12), 8, 0.2, 0, 2);";
    }


    private String getAvatarText() {
        String username = getUsername();

        if (username == null || username.equals("N/A") || username.trim().isEmpty()) {
            return "?";
        }

        if (username.length() == 1) {
            return username.toUpperCase();
        }

        return username.substring(0, 2).toUpperCase();
    }
    
    private void reloadData() {
        this.account = accountInfoController.getCurrentAccount();
        this.employee = accountInfoController.getCurrentEmployee();
        this.accountRole = accountInfoController.getCurrentAccountRole();
    }
    
    private void refreshView() {
        if (rootScrollPane != null) {
            rootScrollPane.setContent(createContent());
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}

