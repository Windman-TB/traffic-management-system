package com.mycompany.trafficsystem.model;

import java.time.LocalDateTime;

public class PasswordResetToken {
    private String resetId;
    private String accountId;
    private String otpCode;
    private String channel;
    private String destination;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private String status;

    public PasswordResetToken() {
    }

    public PasswordResetToken(String resetId, String accountId, String otpCode, String channel,
                              String destination, LocalDateTime createdAt, LocalDateTime expiredAt,
                              String status) {
        this.resetId = resetId;
        this.accountId = accountId;
        this.otpCode = otpCode;
        this.channel = channel;
        this.destination = destination;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
        this.status = status;
    }

    public String getResetId() {
        return resetId;
    }

    public void setResetId(String resetId) {
        this.resetId = resetId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
