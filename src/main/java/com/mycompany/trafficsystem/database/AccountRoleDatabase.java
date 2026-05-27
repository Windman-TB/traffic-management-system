/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.database;

/**
 *
 * @author engineer
 */
import com.mycompany.trafficsystem.model.AccountRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountRoleDatabase {

    public AccountRole getAccountRoleByAccountId(String accountId) {
        String sql = """
            SELECT ACCOUNT_ROLE_ID,
                   ACCOUNT_ID,
                   ROLE_NAME,
                   ASSIGNED_AT
            FROM ACCOUNT_ROLE
            WHERE ACCOUNT_ID = ?
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AccountRole accountRole = new AccountRole();

                    accountRole.setAccountRoleId(rs.getString("ACCOUNT_ROLE_ID"));
                    accountRole.setAccountId(rs.getString("ACCOUNT_ID"));
                    accountRole.setRoleName(rs.getString("ROLE_NAME"));

                    if (rs.getTimestamp("ASSIGNED_AT") != null) {
                        accountRole.setAssignedAt(rs.getTimestamp("ASSIGNED_AT").toLocalDateTime());
                    }

                    return accountRole;
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi lấy thông tin phân quyền tài khoản: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}