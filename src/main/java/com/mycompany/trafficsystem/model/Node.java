/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.model;

import java.time.LocalDateTime;

/**
 * Model ánh xạ bảng NODE.
 */
public class Node {

    private String nodeId;
    private double latitude;
    private double longitude;
    private int isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Node() {
    }

    public Node(String nodeId, double latitude, double longitude,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.nodeId = nodeId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
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

    public String getDisplayNodeId() {
        if (nodeId == null || nodeId.trim().isEmpty()) {
            return "";
        }

        try {
            int number = Integer.parseInt(nodeId.trim());
            return String.format("NG%03d", number);
        } catch (NumberFormatException e) {
            return nodeId;
        }
    }
}
