package com.mycompany.trafficsystem.model;

public class PasswordResetRequest {
    private String resetId;
    private String accountId;
    private String otpCode;
    private String channel;
    private String destination;

    public PasswordResetRequest() {
    }

    public PasswordResetRequest(String resetId, String accountId, String otpCode, String channel, String destination) {
        this.resetId = resetId;
        this.accountId = accountId;
        this.otpCode = otpCode;
        this.channel = channel;
        this.destination = destination;
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
}
