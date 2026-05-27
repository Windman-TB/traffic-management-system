/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.model;

/**
 *
 * @author engineer
 */
import java.time.LocalDateTime;

public class Area {

    private String areaId;
    private String areaName;
    private String areaType;
    private String oldProvince;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int isDeleted;

    public Area() {
    }

    public Area(String areaId, String areaName, String areaType, String oldProvince,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.areaId = areaId;
        this.areaName = areaName;
        this.areaType = areaType;
        this.oldProvince = oldProvince;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getAreaType() {
        return areaType;
    }

    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }

    public String getOldProvince() {
        return oldProvince;
    }

    public void setOldProvince(String oldProvince) {
        this.oldProvince = oldProvince;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getIsDeleted() {
        return isDeleted;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    public String getDisplayAreaId() {
        if (areaId == null || areaId.trim().isEmpty()) {
            return "";
        }

        try {
            int number = Integer.parseInt(areaId.trim());
            return String.format("KV%03d", number);
        } catch (NumberFormatException e) {
            return areaId;
        }
    }
}