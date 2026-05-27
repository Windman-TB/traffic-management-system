package com.mycompany.trafficsystem.model;

import java.time.LocalDateTime;

public class SystemLog {

    private String logId;
    private String accountId;
    private String behaviour;
    private String targetTable;
    private String targetId;
    private String oldValue;
    private String newValue;
    private String logStatus;
    private LocalDateTime createdAt;

    public SystemLog() {
    }

    public SystemLog(String accountId, String behaviour, String targetTable,
                     String targetId, String oldValue, String newValue, String logStatus) {
        this.accountId = accountId;
        this.behaviour = behaviour;
        this.targetTable = targetTable;
        this.targetId = targetId;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.logStatus = logStatus;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getBehaviour() {
        return behaviour;
    }

    public void setBehaviour(String behaviour) {
        this.behaviour = behaviour;
    }

    public String getTargetTable() {
        return targetTable;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getLogStatus() {
        return logStatus;
    }

    public void setLogStatus(String logStatus) {
        this.logStatus = logStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
