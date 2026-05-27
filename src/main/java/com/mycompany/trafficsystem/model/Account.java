/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.model;

import java.time.LocalDateTime;
/**
 *
 * @author engineer
 */
public class Account {
    private String accountId;
    private String employeeId;
    private String username;
    private String password;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int isDeleted;

    // Ham khoi tao
    public Account(){
    }
    
    public Account(String accountId, String employeeId, String username, String status) {
    this.accountId = accountId;
    this.employeeId = employeeId;
    this.username = username;
    this.status = status;
    }
    
    public Account(String accountId, String employeeId, String username, String password,
                   String status, LocalDateTime createdAt, LocalDateTime updatedAt,
                   int isDeleted ) {
        this.accountId = accountId;
        this.employeeId = employeeId;
        this.username = username;
        this.password = password;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDeleted = isDeleted;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        if (!status.equals("ACTIVE") &&
            !status.equals("LOCKED") &&
            !status.equals("INACTIVE")) {
            throw new IllegalArgumentException("Status không hợp lệ");
        }
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

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        if (isDeleted != 0 && isDeleted != 1) {
            throw new IllegalArgumentException("isDeleted chỉ được là 0 hoặc 1");
        }
        this.isDeleted = isDeleted;
    }
    
    @Override
    public String toString() {
        return "Account{" +
                "accountId='" + accountId + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", username='" + username + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isDeleted=" + isDeleted +
                '}';
    }

}
