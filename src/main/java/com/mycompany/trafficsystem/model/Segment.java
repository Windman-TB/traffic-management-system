/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.model;

import java.time.LocalDateTime;

/**
 * Model ánh xạ bảng SEGMENT.
 */
public class Segment {

    private String segmentId;
    private String streetId;
    private String areaId;
    private String startNodeId;
    private String endNodeId;
    private Double segmentLength;
    private Integer maxVelocity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int isDeleted;

    public Segment() {
    }

    public Segment(String segmentId, String streetId, String areaId,
                   String startNodeId, String endNodeId, Double segmentLength,
                   Integer maxVelocity, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.segmentId = segmentId;
        this.streetId = streetId;
        this.areaId = areaId;
        this.startNodeId = startNodeId;
        this.endNodeId = endNodeId;
        this.segmentLength = segmentLength;
        this.maxVelocity = maxVelocity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(String segmentId) {
        this.segmentId = segmentId;
    }

    public String getStreetId() {
        return streetId;
    }

    public void setStreetId(String streetId) {
        this.streetId = streetId;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getStartNodeId() {
        return startNodeId;
    }

    public void setStartNodeId(String startNodeId) {
        this.startNodeId = startNodeId;
    }

    public String getEndNodeId() {
        return endNodeId;
    }

    public void setEndNodeId(String endNodeId) {
        this.endNodeId = endNodeId;
    }

    public Double getSegmentLength() {
        return segmentLength;
    }

    public void setSegmentLength(Double segmentLength) {
        this.segmentLength = segmentLength;
    }

    public Integer getMaxVelocity() {
        return maxVelocity;
    }

    public void setMaxVelocity(Integer maxVelocity) {
        this.maxVelocity = maxVelocity;
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
        this.isDeleted = isDeleted;
    }

    public String getDisplaySegmentId() {
        return formatId(segmentId, "SEG");
    }

    public String getDisplayAreaId() {
        return formatId(areaId, "KV");
    }

    public String getDisplayStartNodeId() {
        return formatId(startNodeId, "NG");
    }

    public String getDisplayEndNodeId() {
        return formatId(endNodeId, "NG");
    }

    public String getDisplayStreetId() {
        if (streetId == null || streetId.trim().isEmpty()) {
            return "";
        }
        return streetId.trim();
    }

    private String formatId(String value, String prefix) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }

        try {
            int number = Integer.parseInt(value.trim());
            return String.format(prefix + "%03d", number);
        } catch (NumberFormatException e) {
            return value;
        }
    }
}
