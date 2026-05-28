/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.model;

import java.time.LocalDateTime;

/**
 * Model ánh xạ bảng TRAFFIC.
 */
public class Traffic {

    private String statusId;
    private String segmentId;
    private double velocity;
    private LocalDateTime recordedAt;

    public Traffic() {
    }

    public Traffic(String statusId, String segmentId, double velocity, LocalDateTime recordedAt) {
        this.statusId = statusId;
        this.segmentId = segmentId;
        this.velocity = velocity;
        this.recordedAt = recordedAt;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(String segmentId) {
        this.segmentId = segmentId;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    public String getDisplayStatusId() {
        if (statusId == null || statusId.trim().isEmpty()) {
            return "";
        }

        try {
            int number = Integer.parseInt(statusId.trim());
            return String.format("TT%03d", number);
        } catch (NumberFormatException e) {
            return statusId;
        }
    }

    public String getDisplaySegmentId() {
        if (segmentId == null || segmentId.trim().isEmpty()) {
            return "";
        }

        try {
            int number = Integer.parseInt(segmentId.trim());
            return String.format("SEG%03d", number);
        } catch (NumberFormatException e) {
            return segmentId;
        }
    }
}
