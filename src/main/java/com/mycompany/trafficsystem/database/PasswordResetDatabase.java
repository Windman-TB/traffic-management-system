package com.mycompany.trafficsystem.database;

import com.mycompany.trafficsystem.model.PasswordResetRequest;
import com.mycompany.trafficsystem.util.SystemLogUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PasswordResetDatabase {

    // Nếu bảng của bạn đang đặt tên khác, chỉ cần đổi hằng số này.
    private static final String TABLE_NAME = "PASSWORD_RESET";

    public PasswordResetRequest createResetRequest(String channel, String destination, String otpCode) {
        String accountId = findAccountIdByDestination(channel, destination);
        if (accountId == null) {
            return null;
        }

        String resetId = generateNextResetId();
        int expiredRows = expireOldActiveRequests(accountId);

        String sql = "INSERT INTO " + TABLE_NAME + " ("
                + "RESET_ID, ACCOUNT_ID, OTP_CODE, CHANNEL, DESTINATION, CREATED_AT, EXPIRED_AT, STATUS"
                + ") VALUES (?, ?, ?, ?, ?, SYSDATE + 7/24, SYSDATE + 7/24 + (45/86400), 'ACTIVE')";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, resetId);
            ps.setString(2, accountId);
            ps.setString(3, otpCode);
            ps.setString(4, channel);
            ps.setString(5, destination);

            if (ps.executeUpdate() > 0) {
                if (expiredRows > 0) {
                    SystemLogUtil.logSuccessByAccountId(
                            accountId,
                            "Hủy OTP cũ",
                            TABLE_NAME,
                            accountId,
                            "STATUS=ACTIVE",
                            "STATUS=EXPIRED; COUNT=" + expiredRows
                    );
                }
                SystemLogUtil.logSuccessByAccountId(
                        accountId,
                        "Tạo OTP đặt lại mật khẩu",
                        TABLE_NAME,
                        resetId,
                        null,
                        "CHANNEL=" + channel + "; DESTINATION=" + destination + "; STATUS=ACTIVE"
                );
                return new PasswordResetRequest(resetId, accountId, otpCode, channel, destination);
            }

        } catch (SQLException e) {
            System.out.println("Lỗi tạo yêu cầu quên mật khẩu: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public boolean verifyOtp(String resetId, String otpCode) {
        String sql = "SELECT COUNT(*) AS TOTAL "
                + "FROM " + TABLE_NAME + " "
                + "WHERE RESET_ID = ? "
                + "AND OTP_CODE = ? "
                + "AND STATUS = 'ACTIVE' "
                + "AND EXPIRED_AT >= SYSDATE + 7/24";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, resetId);
            ps.setString(2, otpCode);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("TOTAL") > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi xác thực OTP: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public String getAccountIdByResetId(String resetId) {
        String sql = "SELECT ACCOUNT_ID "
                + "FROM " + TABLE_NAME + " "
                + "WHERE RESET_ID = ? "
                + "AND STATUS = 'ACTIVE' "
                + "AND EXPIRED_AT >= SYSDATE + 7/24";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, resetId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("ACCOUNT_ID");
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi lấy ACCOUNT_ID từ RESET_ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public boolean markUsed(String resetId) {
        String sql = "UPDATE " + TABLE_NAME + " SET STATUS = 'USED' WHERE RESET_ID = ?";
        String accountId = getAccountIdByResetId(resetId);

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, resetId);
            boolean success = ps.executeUpdate() > 0;
            if (success && accountId != null) {
                SystemLogUtil.logSuccessByAccountId(
                        accountId,
                        "Đánh dấu OTP đã dùng",
                        TABLE_NAME,
                        resetId,
                        "STATUS=ACTIVE",
                        "STATUS=USED"
                );
            }
            return success;

        } catch (SQLException e) {
            System.out.println("Lỗi cập nhật trạng thái OTP đã dùng: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public int expireOldActiveRequests(String accountId) {
        String sql = "UPDATE " + TABLE_NAME + " "
                + "SET STATUS = 'EXPIRED' "
                + "WHERE ACCOUNT_ID = ? "
                + "AND STATUS = 'ACTIVE'";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accountId);
            return ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Lỗi hủy OTP cũ: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public String findAccountIdByDestination(String channel, String destination) {
        String destinationColumn;

        if ("EMAIL".equalsIgnoreCase(channel)) {
            destinationColumn = "e.EMAIL";
        } else if ("PHONE".equalsIgnoreCase(channel)) {
            destinationColumn = "e.PHONENUMBER";
        } else {
            return null;
        }

        String sql = "SELECT a.ACCOUNT_ID "
                + "FROM ACCOUNT a "
                + "JOIN EMPLOYEE e ON a.EMPLOYEE_ID = e.EMPLOYEE_ID "
                + "WHERE " + destinationColumn + " = ? "
                + "AND a.STATUS = 'ACTIVE' "
                + "AND a.IS_DELETED = 0 "
                + "AND e.IS_DELETED = 0";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, destination);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("ACCOUNT_ID");
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi tìm tài khoản theo email/số điện thoại: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public String generateNextResetId() {
        String sql = "SELECT NVL(MAX(TO_NUMBER(SUBSTR(RESET_ID, 4))), 0) + 1 AS NEXT_ID "
                + "FROM " + TABLE_NAME + " "
                + "WHERE REGEXP_LIKE(RESET_ID, '^RST[0-9]+$')";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return String.format("RST%03d", rs.getInt("NEXT_ID"));
            }

        } catch (SQLException e) {
            System.out.println("Lỗi sinh RESET_ID: " + e.getMessage());
            e.printStackTrace();
        }

        return "RST001";
    }
}
