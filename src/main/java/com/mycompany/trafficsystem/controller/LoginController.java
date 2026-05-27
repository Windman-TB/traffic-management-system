package com.mycompany.trafficsystem.controller;

import com.mycompany.trafficsystem.database.AccountDatabase;
import com.mycompany.trafficsystem.view.AdminView;
import com.mycompany.trafficsystem.view.TechnicianView;
import com.mycompany.trafficsystem.view.AnalystView;
import com.mycompany.trafficsystem.database.NodeDatabase;
import com.mycompany.trafficsystem.database.StreetDatabase;
import com.mycompany.trafficsystem.model.Account;
import com.mycompany.trafficsystem.util.Session;
import com.mycompany.trafficsystem.util.SystemLogUtil;
import com.mycompany.trafficsystem.model.LoginResult;


import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class LoginController {

    private final AccountDatabase accountDatabase;
    private final NodeDatabase nodeDatabase;
    private final StreetDatabase streetDatabase;

    public LoginController() {
        this.accountDatabase = new AccountDatabase();
        this.nodeDatabase = new NodeDatabase();
        this.streetDatabase = new StreetDatabase();
    }
    
    public int getTotalNodes() {
    return nodeDatabase.countNodes();
    }

    public int getTotalStreets() {
        return streetDatabase.countStreets();
    }

    public void handleLogin(Stage currentStage, String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập tên đăng nhập.");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập mật khẩu.");
            return;
        }

        String cleanUsername = username.trim();
        String cleanPassword = password.trim();

        LoginResult loginResult = accountDatabase.login(cleanUsername, cleanPassword);

        if (loginResult != null) {
            Account account = loginResult.getAccount();
            String roleName = loginResult.getRoleName();

            Session.setCurrentAccount(account, roleName);
            SystemLogUtil.logSuccessByAccountId(
                    account.getAccountId(),
                    "Đăng nhập",
                    "ACCOUNT",
                    account.getAccountId(),
                    null,
                    "USERNAME=" + account.getUsername() + "; ROLE=" + roleName
            );
            openScreenByRole(currentStage, roleName);
        } else {
            String accountId = accountDatabase.findAccountIdByUsername(cleanUsername);
            if (accountId != null) {
                SystemLogUtil.logFailedByAccountId(
                        accountId,
                        "Đăng nhập",
                        "ACCOUNT",
                        accountId,
                        null,
                        "USERNAME=" + cleanUsername + "; REASON=INVALID_CREDENTIALS"
                );
            }
            showAlert(Alert.AlertType.ERROR, "Đăng nhập thất bại", "Tên đăng nhập hoặc mật khẩu không đúng.");
        }
    }

    // Giữ lại hàm này để nếu file LoginView cũ vẫn gọi 3 tham số thì không bị lỗi biên dịch.
//    public void handleLogin(String username, String password, String role) {
//        handleLogin(null, username, password, role);
//    }

    private void openScreenByRole(Stage currentStage, String role) {
        if (currentStage == null) {
            return;
        }

        switch (role) {
            case "Quản trị viên":
                // TODO: mở AdminView khi bạn đã có file AdminView.
                new AdminView().show(currentStage);
                break;

            case "Kỹ thuật viên":
                // TODO: mở TechnicianView khi bạn đã có file TechnicianView.
                new TechnicianView().show(currentStage);
                break;

            case "Phân tích viên":
                // TODO: mở AnalystView khi bạn đã có file AnalystView.
                new AnalystView().show(currentStage);
                break;

            default:
                showAlert(Alert.AlertType.ERROR, "Lỗi vai trò", "Vai trò không hợp lệ.");
                break;
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
