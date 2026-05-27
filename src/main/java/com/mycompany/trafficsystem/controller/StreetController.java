package com.mycompany.trafficsystem.controller;

import com.mycompany.trafficsystem.database.StreetDatabase;
import com.mycompany.trafficsystem.model.Street;
import com.mycompany.trafficsystem.util.SystemLogUtil;

import java.util.List;

public class StreetController {

    private final StreetDatabase streetDatabase;

    public StreetController() {
        this.streetDatabase = new StreetDatabase();
    }

    public List<Street> getAllStreets() {
        return streetDatabase.getAllStreets();
    }

    public List<Street> searchStreets(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllStreets();
        }

        return streetDatabase.searchStreets(keyword.trim());
    }

    public String generateNextStreetId() {
        return streetDatabase.generateNextStreetId();
    }

    public boolean addStreet(String streetName, String streetType, Integer roadLevel) {
        if (!isValidStreetName(streetName)) {
            return false;
        }

        if (!isValidStreetType(streetType)) {
            return false;
        }

        if (!isValidRoadLevel(roadLevel)) {
            return false;
        }

        Street street = new Street();
        street.setStreetId(generateNextStreetId());
        street.setStreetName(streetName.trim());
        street.setStreetType(streetType.trim());
        street.setRoadLevel(roadLevel);

        boolean success = streetDatabase.insertStreet(street);

        if (success) {
            SystemLogUtil.logSuccess("Thêm tuyến đường", "STREET", street.getStreetId(), null, streetToLogValue(street));
        } else {
            SystemLogUtil.logFailed("Thêm tuyến đường", "STREET", street.getStreetId(), null, streetToLogValue(street));
        }

        return success;
    }

    public boolean updateStreet(String streetId, String streetName, String streetType, Integer roadLevel) {
        if (streetId == null || streetId.trim().isEmpty()) {
            return false;
        }

        if (!isValidStreetName(streetName)) {
            return false;
        }

        if (!isValidStreetType(streetType)) {
            return false;
        }

        if (!isValidRoadLevel(roadLevel)) {
            return false;
        }

        String cleanStreetId = streetId.trim();
        Street oldStreet = streetDatabase.getStreetById(cleanStreetId);

        Street street = new Street();
        street.setStreetId(cleanStreetId);
        street.setStreetName(streetName.trim());
        street.setStreetType(streetType.trim());
        street.setRoadLevel(roadLevel);

        boolean success = streetDatabase.updateStreet(street);

        if (success) {
            SystemLogUtil.logSuccess("Cập nhật tuyến đường", "STREET", cleanStreetId, streetToLogValue(oldStreet), streetToLogValue(street));
        } else {
            SystemLogUtil.logFailed("Cập nhật tuyến đường", "STREET", cleanStreetId, streetToLogValue(oldStreet), streetToLogValue(street));
        }

        return success;
    }

    public boolean deleteStreet(String streetId) {
        if (streetId == null || streetId.trim().isEmpty()) {
            return false;
        }

        String cleanStreetId = streetId.trim();
        Street oldStreet = streetDatabase.getStreetById(cleanStreetId);
        boolean success = streetDatabase.softDeleteStreet(cleanStreetId);

        if (success) {
            SystemLogUtil.logSuccess("Xóa tuyến đường", "STREET", cleanStreetId, streetToLogValue(oldStreet), "IS_DELETED=1");
        } else {
            SystemLogUtil.logFailed("Xóa tuyến đường", "STREET", cleanStreetId, streetToLogValue(oldStreet), "Xóa thất bại");
        }

        return success;
    }

    private boolean isValidStreetName(String streetName) {
        return streetName != null && !streetName.trim().isEmpty();
    }

    private boolean isValidStreetType(String streetType) {
        return streetType != null && !streetType.trim().isEmpty();
    }

    private boolean isValidRoadLevel(Integer roadLevel) {
        return roadLevel != null && roadLevel >= 1 && roadLevel <= 6;
    }

    private String streetToLogValue(Street street) {
        if (street == null) {
            return null;
        }

        return "STREET_ID=" + street.getStreetId()
                + ", STREET_NAME=" + street.getStreetName()
                + ", STREET_TYPE=" + street.getStreetType()
                + ", ROAD_LEVEL=" + street.getRoadLevel();
    }
}
