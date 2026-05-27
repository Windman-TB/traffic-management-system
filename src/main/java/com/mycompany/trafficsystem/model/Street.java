/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.model;

import java.time.LocalDateTime;

public class Street {

    private String streetId;
    private String streetName;
    private String streetType;
    private int roadLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int isDeleted;

    public Street() {
    }

    public Street(String streetId, String streetName, String streetType, int roadLevel,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.streetId = streetId;
        this.streetName = streetName;
        this.streetType = streetType;
        this.roadLevel = roadLevel;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public int getRoadLevel() {
        return roadLevel;
    }

    public void setRoadLevel(int roadLevel) {
        this.roadLevel = roadLevel;
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

    public String getDisplayStreetId() {
        if (streetId == null || streetId.trim().isEmpty()) {
            return "";
        }

        return streetId.trim();
    }
}
