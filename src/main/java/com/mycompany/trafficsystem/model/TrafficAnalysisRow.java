package com.mycompany.trafficsystem.model;

public class TrafficAnalysisRow {

    private String groupName;
    private int recordCount;
    private int segmentCount;
    private Double averageVelocity;
    private Double minVelocity;
    private Double maxVelocity;
    private int congestionCount;
    private Double congestionRate;
    private Double averageVelocityRatio;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public int getSegmentCount() {
        return segmentCount;
    }

    public void setSegmentCount(int segmentCount) {
        this.segmentCount = segmentCount;
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

    public int getCongestionCount() {
        return congestionCount;
    }

    public void setCongestionCount(int congestionCount) {
        this.congestionCount = congestionCount;
    }

    public Double getCongestionRate() {
        return congestionRate;
    }

    public void setCongestionRate(Double congestionRate) {
        this.congestionRate = congestionRate;
    }

    public Double getAverageVelocityRatio() {
        return averageVelocityRatio;
    }

    public void setAverageVelocityRatio(Double averageVelocityRatio) {
        this.averageVelocityRatio = averageVelocityRatio;
    }
}
