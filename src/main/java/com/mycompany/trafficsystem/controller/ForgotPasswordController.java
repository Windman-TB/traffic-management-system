package com.mycompany.trafficsystem.controller;

import com.mycompany.trafficsystem.database.AccountDatabase;
import com.mycompany.trafficsystem.database.PasswordResetDatabase;
import com.mycompany.trafficsystem.model.PasswordResetRequest;
import com.mycompany.trafficsystem.util.SystemLogUtil;
import com.mycompany.trafficsystem.view.ForgotPasswordView;
import com.mycompany.trafficsystem.view.LoginView;
import com.mycompany.trafficsystem.view.OtpVerificationView;
import com.mycompany.trafficsystem.view.ResetPasswordSuccessView;
import com.mycompany.trafficsystem.view.ResetPasswordView;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.security.SecureRandom;

public class ForgotPasswordController {

    private final PasswordResetDatabase passwordResetDatabase;
    private final AccountDatabase accountDatabase;
    private final SecureRandom random;

    public ForgotPasswordController() {
        this.passwordResetDatabase = new PasswordResetDatabase();
        this.accountDatabase = new AccountDatabase();
        this.random = new SecureRandom();
    }

    public void openForgotPassword(Stage stage) {
        new ForgotPasswordView(this).show(stage);
    }

    public void backToLogin(Stage stage) {
        new LoginView().show(stage);
    }

    public void sendOtp(Stage stage, String channel, String destination) {
        if (destination == null || destination.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập email hoặc số điện thoại.");
            return;
        }

        String cleanDestination = destination.trim();
        String cleanChannel = channel == null ? "EMAIL" : channel.trim().toUpperCase();

        if ("EMAIL".equals(cleanChannel) && !cleanDestination.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showAlert(Alert.AlertType.WARNING, "Email không hợp lệ", "Vui lòng nhập đúng định dạng email.");
            return;
        }

        if ("PHONE".equals(cleanChannel) && !cleanDestination.matches("^[0-9]{9,11}$")) {
            showAlert(Alert.AlertType.WARNING, "Số điện thoại không hợp lệ", "Số điện thoại chỉ nên gồm 9 đến 11 chữ số.");
            return;
        }

        String otpCode = generateOtpCode();
        PasswordResetRequest request = passwordResetDatabase.createResetRequest(cleanChannel, cleanDestination, otpCode);

        if (request == null) {
            showAlert(Alert.AlertType.ERROR, "Không tìm thấy tài khoản", "Không có tài khoản ACTIVE nào khớp với thông tin bạn nhập.");
            return;
        }

        // Đồ án thử nghiệm: OTP hiển thị trực tiếp trên màn hình thay vì gửi thật qua email/SMS.
        new OtpVerificationView(this, request).show(stage);
    }

    public void resendOtp(Stage stage, PasswordResetRequest oldRequest) {
        if (oldRequest == null) {
            new ForgotPasswordView(this).show(stage);
            return;
        }

        sendOtp(stage, oldRequest.getChannel(), oldRequest.getDestination());
    }

    public void verifyOtp(Stage stage, PasswordResetRequest request, String otpCode) {
        if (request == null) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Phiên đặt lại mật khẩu không hợp lệ.");
            new ForgotPasswordView(this).show(stage);
            return;
        }

        if (otpCode == null || otpCode.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu OTP", "Vui lòng nhập mã OTP.");
            return;
        }

        String cleanOtp = otpCode.trim();
        if (!cleanOtp.matches("^[0-9]{6}$")) {
            showAlert(Alert.AlertType.WARNING, "OTP không hợp lệ", "Mã OTP phải gồm 6 chữ số.");
            return;
        }

        boolean valid = passwordResetDatabase.verifyOtp(request.getResetId(), cleanOtp);
        if (!valid) {
            showAlert(Alert.AlertType.ERROR, "OTP không đúng", "Mã OTP sai hoặc đã hết hạn.");
            return;
        }

        new ResetPasswordView(this, request).show(stage);
    }

    public void resetPassword(Stage stage, PasswordResetRequest request, String newPassword, String confirmPassword) {
        if (request == null) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Phiên đặt lại mật khẩu không hợp lệ.");
            new ForgotPasswordView(this).show(stage);
            return;
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu mật khẩu", "Vui lòng nhập mật khẩu mới.");
            return;
        }

        if (newPassword.length() < 6) {
            showAlert(Alert.AlertType.WARNING, "Mật khẩu quá ngắn", "Mật khẩu phải có ít nhất 6 ký tự.");
            return;
        }

        if (confirmPassword == null || !newPassword.equals(confirmPassword)) {
            showAlert(Alert.AlertType.WARNING, "Mật khẩu không khớp", "Xác nhận mật khẩu chưa trùng với mật khẩu mới.");
            return;
        }

        String accountId = passwordResetDatabase.getAccountIdByResetId(request.getResetId());
        if (accountId == null) {
            showAlert(Alert.AlertType.ERROR, "OTP hết hạn", "Phiên đặt lại mật khẩu đã hết hạn. Vui lòng gửi lại OTP.");
            new ForgotPasswordView(this).show(stage);
            return;
        }

        boolean passwordUpdated = accountDatabase.updatePassword(accountId, newPassword);
        if (!passwordUpdated) {
            SystemLogUtil.logFailedByAccountId(
                    accountId,
                    "Đặt lại mật khẩu",
                    "ACCOUNT",
                    accountId,
                    null,
                    "Không thể cập nhật mật khẩu qua OTP"
            );
            showAlert(Alert.AlertType.ERROR, "Thất bại", "Không thể cập nhật mật khẩu.");
            return;
        }

        passwordResetDatabase.markUsed(request.getResetId());

        SystemLogUtil.logSuccessByAccountId(
                accountId,
                "Đặt lại mật khẩu",
                "ACCOUNT",
                accountId,
                "PASSWORD=OLD",
                "PASSWORD=CHANGED_BY_OTP"
        );

        new ResetPasswordSuccessView(this).show(stage);
    }

    private String generateOtpCode() {
        int value = random.nextInt(900000) + 100000;
        return String.valueOf(value);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
