package com.mycompany.trafficsystem.model;

import java.time.LocalDateTime;

public class AccountManagement {
    private String accountId;
    private String employeeId;
    private String username;
    private String fullName;
    private String roleName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AccountManagement() {
    }

    public AccountManagement(String accountId, String employeeId, String username, String fullName,
                             String roleName, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.accountId = accountId;
        this.employeeId = employeeId;
        this.username = username;
        this.fullName = fullName;
        this.roleName = roleName;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDisplayAccountId() {
        return accountId == null ? "" : accountId;
    }

    public String getEmployeeDisplayText() {
        String id = employeeId == null ? "" : employeeId;
        String name = fullName == null ? "" : fullName;

        if (name.isBlank()) {
            return id;
        }

        return id + " - " + name;
    }
}
