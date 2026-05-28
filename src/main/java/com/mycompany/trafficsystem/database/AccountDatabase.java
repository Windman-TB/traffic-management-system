package com.mycompany.trafficsystem.database;

import com.mycompany.trafficsystem.model.Account;
import com.mycompany.trafficsystem.model.AccountManagement;
import com.mycompany.trafficsystem.model.LoginResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountDatabase {

    // Chỉ xử lý nghiệp vụ liên quan đến bảng ACCOUNT.
    // Kết nối Oracle lấy từ file ConnectDB.java đã có sẵn.


    public boolean checkLogin(String username, String password, String role) {
        String sql = """
        SELECT COUNT(*) AS TOTAL
        FROM ACCOUNT a
        JOIN ACCOUNT_ROLE ar
            ON a.ACCOUNT_ID = ar.ACCOUNT_ID
        WHERE a.USERNAME = ?
          AND a.PASSWORD = ?
          AND ar.ROLE_NAME = ?
          AND a.STATUS = 'ACTIVE'
          AND a.IS_DELETED = 0
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("TOTAL") > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi kiểm tra đăng nhập tài khoản: " + e.getMessage());
            e.printStackTrace();
    }

    return false;
    }

    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) AS TOTAL "
        + "FROM ACCOUNT "
        + "WHERE USERNAME = ? "
        + "AND IS_DELETED = 0";

        try (Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("TOTAL") > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi kiểm tra tên đăng nhập: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

//    public Account login(String username, String password, String role) {
//        String sql = """
//            SELECT a.ACCOUNT_ID,
//                   a.EMPLOYEE_ID,
//                   a.USERNAME,
//                   a.STATUS
//            FROM ACCOUNT a
//            JOIN ACCOUNT_ROLE ar
//                ON a.ACCOUNT_ID = ar.ACCOUNT_ID
//            WHERE a.USERNAME = ?
//              AND a.PASSWORD = ?
//              AND ar.ROLE_NAME = ?
//              AND a.STATUS = 'ACTIVE'
//              AND a.IS_DELETED = 0
//            """;
//
//        try (Connection conn = ConnectDB.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setString(1, username);
//            ps.setString(2, password);
//            ps.setString(3, role);
//
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    Account account = new Account();
//
//                    account.setAccountId(rs.getString("ACCOUNT_ID"));
//                    account.setEmployeeId(rs.getString("EMPLOYEE_ID"));
//                    account.setUsername(rs.getString("USERNAME"));
//                    account.setStatus(rs.getString("STATUS"));
//
//                    return account;
//                }
//            }
//
//        } catch (SQLException e) {
//            System.out.println("Lỗi đăng nhập tài khoản: " + e.getMessage());
//            e.printStackTrace();
//        }
//
//        return null;
//    }

    public LoginResult login(String username, String password) {
        String sql = """
            SELECT a.ACCOUNT_ID,
                   a.EMPLOYEE_ID,
                   a.USERNAME,
                   a.STATUS,
                   ar.ROLE_NAME
            FROM ACCOUNT a
            JOIN ACCOUNT_ROLE ar
                ON a.ACCOUNT_ID = ar.ACCOUNT_ID
            WHERE a.USERNAME = ?
              AND a.PASSWORD = ?
              AND a.STATUS = 'ACTIVE'
              AND a.IS_DELETED = 0
            """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Account account = new Account();

                    account.setAccountId(rs.getString("ACCOUNT_ID"));
                    account.setEmployeeId(rs.getString("EMPLOYEE_ID"));
                    account.setUsername(rs.getString("USERNAME"));
                    account.setStatus(rs.getString("STATUS"));

                    String roleName = rs.getString("ROLE_NAME");

                    return new LoginResult(account, roleName);
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi đăng nhập tài khoản: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public String findAccountIdByUsername(String username) {
        String sql = """
            SELECT ACCOUNT_ID
            FROM ACCOUNT
            WHERE USERNAME = ?
              AND IS_DELETED = 0
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("ACCOUNT_ID");
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi tìm tài khoản theo tên đăng nhập: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public Account getAccountById(String accountId) {
        String sql = """
            SELECT ACCOUNT_ID,
                   USERNAME,
                   STATUS,
                   CREATED_AT,
                   UPDATED_AT
            FROM ACCOUNT
            WHERE ACCOUNT_ID = ?
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Account account= new Account();

                    account.setAccountId(rs.getString("ACCOUNT_ID"));
                    account.setUsername(rs.getString("USERNAME"));
                    account.setStatus(rs.getString("STATUS"));
                    if (rs.getTimestamp("CREATED_AT") != null) {
                        account.setCreatedAt(rs.getTimestamp("CREATED_AT").toLocalDateTime());
                    }

                    if (rs.getTimestamp("UPDATED_AT") != null) {
                        account.setUpdatedAt(rs.getTimestamp("UPDATED_AT").toLocalDateTime());
                    }

//                    if (rs.getTimestamp("ASSIGNED_AT") != null) {
//                        account.setAssignedAt(rs.getTimestamp("ASSIGNED_AT").toLocalDateTime());
//                    }

                    return account;
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi lấy thông tin phân quyền tài khoản: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public boolean checkCurrentPassword(String accountId, String currentPassword) {
        String sql = """
            SELECT COUNT(*) AS TOTAL
            FROM ACCOUNT
            WHERE ACCOUNT_ID = ?
              AND PASSWORD = ?
              AND IS_DELETED = 0
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accountId);
            ps.setString(2, currentPassword);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("TOTAL") > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi kiểm tra mật khẩu hiện tại: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean updatePassword(String accountId, String newPassword) {
        String sql = """
            UPDATE ACCOUNT
            SET PASSWORD = ?,
                UPDATED_AT = SYSDATE + 7/24
            WHERE ACCOUNT_ID = ?
              AND IS_DELETED = 0
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setString(2, accountId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi đổi mật khẩu: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public int countAccounts() {
        String sql = """
            SELECT COUNT(*) AS TOTAL
            FROM ACCOUNT
            WHERE IS_DELETED = 0
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("TOTAL");
            }

        } catch (SQLException e) {
            System.out.println("Lỗi đếm số tài khoản: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public List<AccountManagement> getAllAccountsForManagement() {
        String sql = """
            SELECT a.ACCOUNT_ID,
                   a.EMPLOYEE_ID,
                   a.USERNAME,
                   e.FULLNAME,
                   ar.ROLE_NAME,
                   a.STATUS,
                   a.CREATED_AT,
                   a.UPDATED_AT
            FROM ACCOUNT a
            JOIN EMPLOYEE e
                ON a.EMPLOYEE_ID = e.EMPLOYEE_ID
            LEFT JOIN ACCOUNT_ROLE ar
                ON a.ACCOUNT_ID = ar.ACCOUNT_ID
            WHERE a.IS_DELETED = 0
            ORDER BY TO_NUMBER(SUBSTR(a.ACCOUNT_ID, 4))
        """;

        List<AccountManagement> accounts = new ArrayList<>();

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                accounts.add(mapResultSetToAccountManagement(rs));
            }

        } catch (SQLException e) {
            System.out.println("Lỗi lấy danh sách tài khoản: " + e.getMessage());
            e.printStackTrace();
        }

        return accounts;
    }

    public List<AccountManagement> searchAccountsForManagement(String keyword) {
        String sql = """
            SELECT a.ACCOUNT_ID,
                   a.EMPLOYEE_ID,
                   a.USERNAME,
                   e.FULLNAME,
                   ar.ROLE_NAME,
                   a.STATUS,
                   a.CREATED_AT,
                   a.UPDATED_AT
            FROM ACCOUNT a
            JOIN EMPLOYEE e
                ON a.EMPLOYEE_ID = e.EMPLOYEE_ID
            LEFT JOIN ACCOUNT_ROLE ar
                ON a.ACCOUNT_ID = ar.ACCOUNT_ID
            WHERE a.IS_DELETED = 0
              AND (
                    LOWER(a.ACCOUNT_ID) LIKE LOWER(?)
                 OR LOWER(a.EMPLOYEE_ID) LIKE LOWER(?)
                 OR LOWER(a.USERNAME) LIKE LOWER(?)
                 OR LOWER(e.FULLNAME) LIKE LOWER(?)
                 OR LOWER(ar.ROLE_NAME) LIKE LOWER(?)
                 OR LOWER(a.STATUS) LIKE LOWER(?)
              )
            ORDER BY TO_NUMBER(SUBSTR(a.ACCOUNT_ID, 4))
        """;

        List<AccountManagement> accounts = new ArrayList<>();
        String searchValue = "%" + keyword + "%";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 1; i <= 6; i++) {
                ps.setString(i, searchValue);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapResultSetToAccountManagement(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi tìm kiếm tài khoản: " + e.getMessage());
            e.printStackTrace();
        }

        return accounts;
    }

    public List<String> getEmployeesWithoutAccount() {
        String sql = """
            SELECT e.EMPLOYEE_ID,
                   e.FULLNAME
            FROM EMPLOYEE e
            WHERE NVL(e.IS_DELETED, 0) = 0
              AND NOT EXISTS (
                    SELECT 1
                    FROM ACCOUNT a
                    WHERE a.EMPLOYEE_ID = e.EMPLOYEE_ID
                      AND a.IS_DELETED = 0
              )
            ORDER BY e.EMPLOYEE_ID
        """;

        List<String> employees = new ArrayList<>();

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                employees.add(rs.getString("EMPLOYEE_ID") + " - " + rs.getString("FULLNAME"));
            }

        } catch (SQLException e) {
            System.out.println("Lỗi lấy danh sách nhân viên chưa có tài khoản: " + e.getMessage());
            e.printStackTrace();
        }

        return employees;
    }

    public String generateNextAccountId() {
        String sql = """
            SELECT NVL(MAX(TO_NUMBER(SUBSTR(ACCOUNT_ID, 4))), 0) + 1 AS NEXT_ID
            FROM ACCOUNT
            WHERE REGEXP_LIKE(ACCOUNT_ID, '^ACC[0-9]+$')
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return String.format("ACC%03d", rs.getInt("NEXT_ID"));
            }

        } catch (SQLException e) {
            System.out.println("Lỗi sinh mã tài khoản: " + e.getMessage());
            e.printStackTrace();
        }

        return "ACC001";
    }

    public String generateNextAccountRoleId(Connection conn) throws SQLException {
        String sql = """
            SELECT NVL(MAX(TO_NUMBER(SUBSTR(ACCOUNT_ROLE_ID, 3))), 0) + 1 AS NEXT_ID
            FROM ACCOUNT_ROLE
            WHERE REGEXP_LIKE(ACCOUNT_ROLE_ID, '^AR[0-9]+$')
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return String.format("AR%03d", rs.getInt("NEXT_ID"));
            }
        }

        return "AR001";
    }

    public boolean isEmployeeAlreadyHasAccount(String employeeId) {
        String sql = """
            SELECT COUNT(*) AS TOTAL
            FROM ACCOUNT
            WHERE EMPLOYEE_ID = ?
              AND IS_DELETED = 0
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("TOTAL") > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi kiểm tra nhân viên đã có tài khoản: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean isUsernameExistsForOtherAccount(String username, String accountId) {
        String sql = """
            SELECT COUNT(*) AS TOTAL
            FROM ACCOUNT
            WHERE USERNAME = ?
              AND ACCOUNT_ID <> ?
              AND IS_DELETED = 0
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("TOTAL") > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi kiểm tra trùng tên đăng nhập khi sửa: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean insertAccountForManagement(AccountManagement account, String password) {
        String insertAccountSql = """
            INSERT INTO ACCOUNT (
                ACCOUNT_ID,
                EMPLOYEE_ID,
                USERNAME,
                PASSWORD,
                STATUS,
                CREATED_AT,
                UPDATED_AT,
                IS_DELETED
            ) VALUES (?, ?, ?, ?, ?, SYSDATE + 7/24, SYSDATE + 7/24, 0)
        """;

        String insertRoleSql = """
            INSERT INTO ACCOUNT_ROLE (
                ACCOUNT_ROLE_ID,
                ACCOUNT_ID,
                ROLE_NAME,
                ASSIGNED_AT
            ) VALUES (?, ?, ?, SYSDATE + 7/24)
        """;

        Connection conn = null;

        try {
            conn = ConnectDB.getConnection();
            conn.setAutoCommit(false);

            AccountManagement deletedAccount = findDeletedAccountForRestore(conn, account);
            if (deletedAccount != null) {
                account.setAccountId(deletedAccount.getAccountId());
                boolean restored = restoreAccountForManagement(conn, account, password);

                if (!restored) {
                    conn.rollback();
                    return false;
                }

                conn.commit();
                return true;
            }

            try (PreparedStatement ps = conn.prepareStatement(insertAccountSql)) {
                ps.setString(1, account.getAccountId());
                ps.setString(2, account.getEmployeeId());
                ps.setString(3, account.getUsername());
                ps.setString(4, password);
                ps.setString(5, account.getStatus());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(insertRoleSql)) {
                ps.setString(1, generateNextAccountRoleId(conn));
                ps.setString(2, account.getAccountId());
                ps.setString(3, account.getRoleName());
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.out.println("Lỗi thêm tài khoản: " + e.getMessage());
            e.printStackTrace();

            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackException) {
                    rollbackException.printStackTrace();
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeException) {
                    closeException.printStackTrace();
                }
            }
        }

        return false;
    }

    private AccountManagement findDeletedAccountForRestore(Connection conn, AccountManagement account) throws SQLException {
        String sql = """
            SELECT a.ACCOUNT_ID,
                   a.EMPLOYEE_ID,
                   a.USERNAME,
                   e.FULLNAME,
                   ar.ROLE_NAME,
                   a.STATUS,
                   a.CREATED_AT,
                   a.UPDATED_AT
            FROM ACCOUNT a
            JOIN EMPLOYEE e
                ON a.EMPLOYEE_ID = e.EMPLOYEE_ID
            LEFT JOIN ACCOUNT_ROLE ar
                ON a.ACCOUNT_ID = ar.ACCOUNT_ID
            WHERE a.IS_DELETED = 1
              AND (a.USERNAME = ? OR a.EMPLOYEE_ID = ?)
            ORDER BY CASE WHEN a.USERNAME = ? THEN 0 ELSE 1 END,
                     a.ACCOUNT_ID
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getEmployeeId());
            ps.setString(3, account.getUsername());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAccountManagement(rs);
                }
            }
        }

        return null;
    }

    private boolean restoreAccountForManagement(Connection conn, AccountManagement account, String password) throws SQLException {
        String updateAccountSql = """
            UPDATE ACCOUNT
            SET EMPLOYEE_ID = ?,
                USERNAME = ?,
                PASSWORD = ?,
                STATUS = ?,
                UPDATED_AT = SYSDATE + 7/24,
                IS_DELETED = 0
            WHERE ACCOUNT_ID = ?
              AND IS_DELETED = 1
        """;

        String updateRoleSql = """
            UPDATE ACCOUNT_ROLE
            SET ROLE_NAME = ?,
                ASSIGNED_AT = SYSDATE + 7/24
            WHERE ACCOUNT_ID = ?
        """;

        String insertRoleSql = """
            INSERT INTO ACCOUNT_ROLE (
                ACCOUNT_ROLE_ID,
                ACCOUNT_ID,
                ROLE_NAME,
                ASSIGNED_AT
            ) VALUES (?, ?, ?, SYSDATE + 7/24)
        """;

        int updatedAccountRows;
        try (PreparedStatement ps = conn.prepareStatement(updateAccountSql)) {
            ps.setString(1, account.getEmployeeId());
            ps.setString(2, account.getUsername());
            ps.setString(3, password);
            ps.setString(4, account.getStatus());
            ps.setString(5, account.getAccountId());
            updatedAccountRows = ps.executeUpdate();
        }

        if (updatedAccountRows == 0) {
            return false;
        }

        int updatedRoleRows;
        try (PreparedStatement ps = conn.prepareStatement(updateRoleSql)) {
            ps.setString(1, account.getRoleName());
            ps.setString(2, account.getAccountId());
            updatedRoleRows = ps.executeUpdate();
        }

        if (updatedRoleRows == 0) {
            try (PreparedStatement ps = conn.prepareStatement(insertRoleSql)) {
                ps.setString(1, generateNextAccountRoleId(conn));
                ps.setString(2, account.getAccountId());
                ps.setString(3, account.getRoleName());
                ps.executeUpdate();
            }
        }

        return true;
    }

    public boolean updateAccountForManagement(AccountManagement account, String newPassword) {
        String updateAccountWithoutPasswordSql = """
            UPDATE ACCOUNT
            SET USERNAME = ?,
                STATUS = ?,
                UPDATED_AT = SYSDATE + 7/24
            WHERE ACCOUNT_ID = ?
              AND IS_DELETED = 0
        """;

        String updateAccountWithPasswordSql = """
            UPDATE ACCOUNT
            SET USERNAME = ?,
                PASSWORD = ?,
                STATUS = ?,
                UPDATED_AT = SYSDATE + 7/24
            WHERE ACCOUNT_ID = ?
              AND IS_DELETED = 0
        """;

        String updateRoleSql = """
            UPDATE ACCOUNT_ROLE
            SET ROLE_NAME = ?,
                ASSIGNED_AT = SYSDATE + 7/24
            WHERE ACCOUNT_ID = ?
        """;

        String insertRoleSql = """
            INSERT INTO ACCOUNT_ROLE (
                ACCOUNT_ROLE_ID,
                ACCOUNT_ID,
                ROLE_NAME,
                ASSIGNED_AT
            ) VALUES (?, ?, ?, SYSDATE + 7/24)
        """;

        Connection conn = null;

        try {
            conn = ConnectDB.getConnection();
            conn.setAutoCommit(false);

            int updatedAccountRows;
            if (newPassword == null || newPassword.isEmpty()) {
                try (PreparedStatement ps = conn.prepareStatement(updateAccountWithoutPasswordSql)) {
                    ps.setString(1, account.getUsername());
                    ps.setString(2, account.getStatus());
                    ps.setString(3, account.getAccountId());
                    updatedAccountRows = ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(updateAccountWithPasswordSql)) {
                    ps.setString(1, account.getUsername());
                    ps.setString(2, newPassword);
                    ps.setString(3, account.getStatus());
                    ps.setString(4, account.getAccountId());
                    updatedAccountRows = ps.executeUpdate();
                }
            }

            if (updatedAccountRows == 0) {
                conn.rollback();
                return false;
            }

            int updatedRoleRows;
            try (PreparedStatement ps = conn.prepareStatement(updateRoleSql)) {
                ps.setString(1, account.getRoleName());
                ps.setString(2, account.getAccountId());
                updatedRoleRows = ps.executeUpdate();
            }

            if (updatedRoleRows == 0) {
                try (PreparedStatement ps = conn.prepareStatement(insertRoleSql)) {
                    ps.setString(1, generateNextAccountRoleId(conn));
                    ps.setString(2, account.getAccountId());
                    ps.setString(3, account.getRoleName());
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.out.println("Lỗi sửa tài khoản: " + e.getMessage());
            e.printStackTrace();

            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackException) {
                    rollbackException.printStackTrace();
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeException) {
                    closeException.printStackTrace();
                }
            }
        }

        return false;
    }

    public boolean softDeleteAccount(String accountId) {
        String sql = """
            UPDATE ACCOUNT
            SET IS_DELETED = 1,
                UPDATED_AT = SYSDATE + 7/24
            WHERE ACCOUNT_ID = ?
              AND IS_DELETED = 0
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accountId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi xóa mềm tài khoản: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }


public AccountManagement getAccountManagementById(String accountId) {
    String sql = """
        SELECT a.ACCOUNT_ID,
               a.EMPLOYEE_ID,
               a.USERNAME,
               e.FULLNAME,
               ar.ROLE_NAME,
               a.STATUS,
               a.CREATED_AT,
               a.UPDATED_AT
        FROM ACCOUNT a
        JOIN EMPLOYEE e
            ON a.EMPLOYEE_ID = e.EMPLOYEE_ID
        LEFT JOIN ACCOUNT_ROLE ar
            ON a.ACCOUNT_ID = ar.ACCOUNT_ID
        WHERE a.ACCOUNT_ID = ?
    """;

    try (Connection conn = ConnectDB.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, accountId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToAccountManagement(rs);
            }
        }

    } catch (SQLException e) {
        System.out.println("Lỗi lấy tài khoản quản lý theo mã: " + e.getMessage());
        e.printStackTrace();
    }

    return null;
}

    private AccountManagement mapResultSetToAccountManagement(ResultSet rs) throws SQLException {
        AccountManagement account = new AccountManagement();

        account.setAccountId(rs.getString("ACCOUNT_ID"));
        account.setEmployeeId(rs.getString("EMPLOYEE_ID"));
        account.setUsername(rs.getString("USERNAME"));
        account.setFullName(rs.getString("FULLNAME"));
        account.setRoleName(rs.getString("ROLE_NAME"));
        account.setStatus(rs.getString("STATUS"));

        if (rs.getTimestamp("CREATED_AT") != null) {
            account.setCreatedAt(rs.getTimestamp("CREATED_AT").toLocalDateTime());
        }

        if (rs.getTimestamp("UPDATED_AT") != null) {
            account.setUpdatedAt(rs.getTimestamp("UPDATED_AT").toLocalDateTime());
        }

        return account;
    }

}
