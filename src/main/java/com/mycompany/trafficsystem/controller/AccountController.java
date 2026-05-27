package com.mycompany.trafficsystem.controller;

import com.mycompany.trafficsystem.database.AccountDatabase;
import com.mycompany.trafficsystem.model.AccountManagement;
import com.mycompany.trafficsystem.util.SystemLogUtil;

import java.util.List;

public class AccountController {
    private final AccountDatabase accountDatabase;

    public AccountController() {
        this.accountDatabase = new AccountDatabase();
    }

    public List<AccountManagement> getAllAccountsForManagement() {
        return accountDatabase.getAllAccountsForManagement();
    }

    public List<AccountManagement> searchAccounts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllAccountsForManagement();
        }

        return accountDatabase.searchAccountsForManagement(keyword.trim());
    }

    public List<String> getEmployeesWithoutAccount() {
        return accountDatabase.getEmployeesWithoutAccount();
    }

    public String generateNextAccountId() {
        return accountDatabase.generateNextAccountId();
    }

    public boolean addAccount(String employeeId, String username, String password, String roleName, String status) {
        validateEmployeeId(employeeId);
        validateUsername(username);
        validatePassword(password);
        validateRole(roleName);
        validateStatus(status);

        if (accountDatabase.isUsernameExists(username.trim())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại.");
        }

        if (accountDatabase.isEmployeeAlreadyHasAccount(employeeId.trim())) {
            throw new IllegalArgumentException("Nhân viên này đã có tài khoản.");
        }

        AccountManagement account = new AccountManagement();
        account.setAccountId(generateNextAccountId());
        account.setEmployeeId(employeeId.trim());
        account.setUsername(username.trim());
        account.setRoleName(roleName.trim());
        account.setStatus(status.trim());

        boolean success = accountDatabase.insertAccountForManagement(account, password.trim());

        if (success) {
            SystemLogUtil.logSuccess("Thêm tài khoản", "ACCOUNT", account.getAccountId(), null, accountToLogValue(account, false));
        } else {
            SystemLogUtil.logFailed("Thêm tài khoản", "ACCOUNT", account.getAccountId(), null, accountToLogValue(account, false));
        }

        return success;
    }

    public boolean updateAccount(String accountId, String username, String newPassword, String roleName, String status) {
        if (accountId == null || accountId.trim().isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy mã tài khoản cần sửa.");
        }

        validateUsername(username);
        validateRole(roleName);
        validateStatus(status);

        String passwordValue = newPassword == null ? "" : newPassword.trim();
        if (!passwordValue.isEmpty()) {
            validatePassword(passwordValue);
        }

        String cleanAccountId = accountId.trim();

        if (accountDatabase.isUsernameExistsForOtherAccount(username.trim(), cleanAccountId)) {
            throw new IllegalArgumentException("Tên đăng nhập đã được tài khoản khác sử dụng.");
        }

        AccountManagement oldAccount = accountDatabase.getAccountManagementById(cleanAccountId);

        AccountManagement account = new AccountManagement();
        account.setAccountId(cleanAccountId);
        account.setUsername(username.trim());
        account.setRoleName(roleName.trim());
        account.setStatus(status.trim());

        boolean success = accountDatabase.updateAccountForManagement(account, passwordValue);

        if (success) {
            SystemLogUtil.logSuccess("Cập nhật tài khoản", "ACCOUNT", cleanAccountId, accountToLogValue(oldAccount, false), accountToLogValue(account, !passwordValue.isEmpty()));
        } else {
            SystemLogUtil.logFailed("Cập nhật tài khoản", "ACCOUNT", cleanAccountId, accountToLogValue(oldAccount, false), accountToLogValue(account, !passwordValue.isEmpty()));
        }

        return success;
    }

    public boolean deleteAccount(String accountId) {
        if (accountId == null || accountId.trim().isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy mã tài khoản cần xóa.");
        }

        String cleanAccountId = accountId.trim();
        AccountManagement oldAccount = accountDatabase.getAccountManagementById(cleanAccountId);
        boolean success = accountDatabase.softDeleteAccount(cleanAccountId);

        if (success) {
            SystemLogUtil.logSuccess("Xóa tài khoản", "ACCOUNT", cleanAccountId, accountToLogValue(oldAccount, false), "IS_DELETED=1");
        } else {
            SystemLogUtil.logFailed("Xóa tài khoản", "ACCOUNT", cleanAccountId, accountToLogValue(oldAccount, false), "Xóa thất bại");
        }

        return success;
    }

    private void validateEmployeeId(String employeeId) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn nhân viên.");
        }
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống.");
        }

        if (username.trim().length() > 50) {
            throw new IllegalArgumentException("Tên đăng nhập không được vượt quá 50 ký tự.");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống.");
        }

        if (password.trim().length() > 200) {
            throw new IllegalArgumentException("Mật khẩu không được vượt quá 200 ký tự.");
        }
    }

    private void validateRole(String roleName) {
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new IllegalArgumentException("Vai trò không được để trống.");
        }

        String role = roleName.trim();
        if (!role.equals("Quản trị viên") && !role.equals("Kỹ thuật viên") && !role.equals("Phân tích viên")) {
            throw new IllegalArgumentException("Vai trò không hợp lệ.");
        }
    }

    private void validateStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái không được để trống.");
        }

        String value = status.trim();
        if (!value.equals("ACTIVE") && !value.equals("LOCKED") && !value.equals("INACTIVE")) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ.");
        }
    }

    private String accountToLogValue(AccountManagement account, boolean passwordChanged) {
        if (account == null) {
            return null;
        }

        return "ACCOUNT_ID=" + account.getAccountId()
                + ", EMPLOYEE_ID=" + account.getEmployeeId()
                + ", USERNAME=" + account.getUsername()
                + ", ROLE_NAME=" + account.getRoleName()
                + ", STATUS=" + account.getStatus()
                + (passwordChanged ? ", PASSWORD=CHANGED" : "");
    }
}
