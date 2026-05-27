package com.mycompany.trafficsystem.controller;

import com.mycompany.trafficsystem.database.AccountDatabase;
import com.mycompany.trafficsystem.database.AccountRoleDatabase;
import com.mycompany.trafficsystem.database.EmployeeDatabase;
import com.mycompany.trafficsystem.model.Account;
import com.mycompany.trafficsystem.model.AccountRole;
import com.mycompany.trafficsystem.model.Employee;
import com.mycompany.trafficsystem.util.Session;
import com.mycompany.trafficsystem.util.SystemLogUtil;

public class AccountInfoController {

    private final EmployeeDatabase employeeDatabase;
    private final AccountRoleDatabase accountRoleDatabase;
    private final AccountDatabase accountDatabase;

    public AccountInfoController() {
        this.employeeDatabase = new EmployeeDatabase();
        this.accountRoleDatabase = new AccountRoleDatabase();
        this.accountDatabase = new AccountDatabase();
    }

    public String getCurrentRole() {
        return Session.getCurrentRole();
    }

    public Account getCurrentAccount() {
        Account account = Session.getCurrentAccount();

        if (account == null || account.getAccountId() == null) {
            return null;
        }

        return accountDatabase.getAccountById(account.getAccountId());
    }

    public Employee getCurrentEmployee() {
        Account account = Session.getCurrentAccount();

        if (account == null || account.getEmployeeId() == null) {
            return null;
        }

        return employeeDatabase.getEmployeeById(account.getEmployeeId());
    }

    public AccountRole getCurrentAccountRole() {
        Account account = Session.getCurrentAccount();

        if (account == null || account.getAccountId() == null) {
            return null;
        }

        return accountRoleDatabase.getAccountRoleByAccountId(account.getAccountId());
    }

    public boolean updateCurrentEmployeeContactInfo(String phoneNumber, String email, String address) {
        Account account = Session.getCurrentAccount();

        if (account == null || account.getEmployeeId() == null) {
            return false;
        }

        Employee oldEmployee = employeeDatabase.getEmployeeById(account.getEmployeeId());

        boolean success = employeeDatabase.updateEmployeeContactInfo(
                account.getEmployeeId(),
                phoneNumber,
                email,
                address
        );

        if (success) {
            String newValue = "PHONENUMBER=" + phoneNumber
                    + ", EMAIL=" + email
                    + ", ADDRESS=" + address;

            SystemLogUtil.logSuccess(
                    "Cập nhật thông tin cá nhân",
                    "EMPLOYEE",
                    account.getEmployeeId(),
                    employeeContactToLogValue(oldEmployee),
                    newValue
            );
        } else {
            SystemLogUtil.logFailed(
                    "Cập nhật thông tin cá nhân",
                    "EMPLOYEE",
                    account.getEmployeeId(),
                    employeeContactToLogValue(oldEmployee),
                    "Cập nhật thất bại"
            );
        }

        return success;
    }

    public boolean changeCurrentPassword(String currentPassword, String newPassword, String confirmPassword) {
        Account account = Session.getCurrentAccount();

        if (account == null || account.getAccountId() == null) {
            return false;
        }

        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            return false;
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            return false;
        }

        boolean currentPasswordCorrect = accountDatabase.checkCurrentPassword(
                account.getAccountId(),
                currentPassword
        );

        if (!currentPasswordCorrect) {
            SystemLogUtil.logFailed(
                    "Đổi mật khẩu",
                    "ACCOUNT",
                    account.getAccountId(),
                    null,
                    "Mật khẩu hiện tại không đúng"
            );
            return false;
        }

        boolean success = accountDatabase.updatePassword(account.getAccountId(), newPassword);

        if (success) {
            SystemLogUtil.logSuccess(
                    "Đổi mật khẩu",
                    "ACCOUNT",
                    account.getAccountId(),
                    "PASSWORD=OLD",
                    "PASSWORD=CHANGED"
            );
        } else {
            SystemLogUtil.logFailed(
                    "Đổi mật khẩu",
                    "ACCOUNT",
                    account.getAccountId(),
                    "PASSWORD=OLD",
                    "Đổi mật khẩu thất bại"
            );
        }

        return success;
    }

    private String employeeContactToLogValue(Employee employee) {
        if (employee == null) {
            return null;
        }

        return "PHONENUMBER=" + employee.getPhoneNumber()
                + ", EMAIL=" + employee.getEmail()
                + ", ADDRESS=" + employee.getAddress();
    }
}
