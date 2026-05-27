/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.model;

/**
 *
 * @author engineer
 */
import java.time.LocalDateTime;

public class AccountRole {

    private String accountRoleId;
    private String accountId;
    private String roleName;
    private LocalDateTime assignedAt;

    public AccountRole() {
    }

    public AccountRole(String accountRoleId, String accountId, String roleName, LocalDateTime assignedAt) {
        this.accountRoleId = accountRoleId;
        this.accountId = accountId;
        this.roleName = roleName;
        this.assignedAt = assignedAt;
    }

    public String getAccountRoleId() {
        return accountRoleId;
    }

    public void setAccountRoleId(String accountRoleId) {
        this.accountRoleId = accountRoleId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }
}
