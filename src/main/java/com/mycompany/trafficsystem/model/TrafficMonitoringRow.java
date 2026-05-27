package com.mycompany.trafficsystem.model;

import java.time.LocalDateTime;

public class TrafficMonitoringRow {

    private String segmentId;
    private String streetId;
    private String streetName;
    private String streetType;
    private Integer roadLevel;
    private String areaId;
    private String areaName;
    private String startNodeId;
    private String endNodeId;
    private Double startLatitude;
    private Double startLongitude;
    private Double endLatitude;
    private Double endLongitude;
    private Double segmentLength;
    private Integer maxVelocity;
    private Double velocity;
    private LocalDateTime createdAt;

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

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetType() {
        return streetType;
    }

    public void setStreetType(String streetType) {
        this.streetType = streetType;
    }

    public Integer getRoadLevel() {
        return roadLevel;
    }

    public void setRoadLevel(Integer roadLevel) {
        this.roadLevel = roadLevel;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
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

    public Double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(Double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public Double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(Double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public Double getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(Double endLatitude) {
        this.endLatitude = endLatitude;
    }

    public Double getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(Double endLongitude) {
        this.endLongitude = endLongitude;
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

    public Double getVelocity() {
        return velocity;
    }

    public void setVelocity(Double velocity) {
        this.velocity = velocity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Double getVelocityRatio() {
        if (velocity == null || maxVelocity == null || maxVelocity <= 0) {
            return null;
        }

        return velocity / maxVelocity;
    }

    public String getStatus() {
        Double ratio = getVelocityRatio();

        if (ratio != null) {
            if (ratio >= 0.7) {
                return "Thông thoáng";
            }
            if (ratio >= 0.4) {
                return "Đông đúc";
            }
            return "Ùn tắc";
        }

        if (velocity == null) {
            return "Không có dữ liệu";
        }
        if (velocity >= 40) {
            return "Thông thoáng";
        }
        if (velocity >= 20) {
            return "Đông đúc";
        }
        return "Ùn tắc";
    }
}
