package com.mycompany.trafficsystem.model;

public class TrafficAnalysisSummary {

    private int totalRecords;
    private int totalSegments;
    private Double averageVelocity;
    private Double minVelocity;
    private Double maxVelocity;
    private int congestionRecords;
    private Double congestionRate;

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getTotalSegments() {
        return totalSegments;
    }

    public void setTotalSegments(int totalSegments) {
        this.totalSegments = totalSegments;
    }

    public Double getAverageVelocity() {
        return averageVelocity;
    }

    public void setAverageVelocity(Double averageVelocity) {
        this.averageVelocity = averageVelocity;
    }

    public Double getMinVelocity() {
        return minVelocity;
    }

    public void setMinVelocity(Double minVelocity) {
        this.minVelocity = minVelocity;
    }

    public Double getMaxVelocity() {
        return maxVelocity;
    }

    public void setMaxVelocity(Double maxVelocity) {
        this.maxVelocity = maxVelocity;
    }

    public int getCongestionRecords() {
        return congestionRecords;
    }

    public void setCongestionRecords(int congestionRecords) {
        this.congestionRecords = congestionRecords;
    }

    public Double getCongestionRate() {
        return congestionRate;
    }

    public void setCongestionRate(Double congestionRate) {
        this.congestionRate = congestionRate;
    }
}
