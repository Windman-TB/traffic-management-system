package com.mycompany.trafficsystem.controller;

import com.mycompany.trafficsystem.database.AreaDatabase;
import com.mycompany.trafficsystem.model.Area;
import com.mycompany.trafficsystem.util.SystemLogUtil;

import java.util.List;

public class AreaController {

    private final AreaDatabase areaDatabase;

    public AreaController() {
        this.areaDatabase = new AreaDatabase();
    }

    public List<Area> getAllAreas() {
        return areaDatabase.getAllAreas();
    }

    public List<Area> searchAreas(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllAreas();
        }

        String normalizedKeyword = keyword.trim();

        if (normalizedKeyword.toUpperCase().startsWith("KV")) {
            try {
                int number = Integer.parseInt(normalizedKeyword.substring(2));
                normalizedKeyword = String.valueOf(number);
            } catch (NumberFormatException e) {
                // Nếu nhập sai như KVABC thì giữ nguyên để search bình thường.
            }
        }

        return areaDatabase.searchAreas(normalizedKeyword);
    }

    public String generateNextAreaId() {
        return areaDatabase.generateNextAreaId();
    }

    public boolean addArea(String areaName, String areaType, String oldProvince) {
        if (areaName == null || areaName.trim().isEmpty()) {
            return false;
        }

        if (areaType == null || areaType.trim().isEmpty()) {
            return false;
        }

        if (oldProvince == null || oldProvince.trim().isEmpty()) {
            return false;
        }

        Area area = new Area();
        area.setAreaId(generateNextAreaId());
        area.setAreaName(areaName.trim());
        area.setAreaType(areaType.trim());
        area.setOldProvince(oldProvince.trim());

        boolean success = areaDatabase.insertArea(area);

        if (success) {
            SystemLogUtil.logSuccess("Thêm khu vực", "AREA", area.getAreaId(), null, areaToLogValue(area));
        } else {
            SystemLogUtil.logFailed("Thêm khu vực", "AREA", area.getAreaId(), null, areaToLogValue(area));
        }

        return success;
    }

    public boolean deleteArea(String areaId) {
        if (areaId == null || areaId.trim().isEmpty()) {
            return false;
        }

        String cleanAreaId = areaId.trim();
        Area oldArea = areaDatabase.getAreaById(cleanAreaId);
        boolean success = areaDatabase.softDeleteArea(cleanAreaId);

        if (success) {
            SystemLogUtil.logSuccess("Xóa khu vực", "AREA", cleanAreaId, areaToLogValue(oldArea), "IS_DELETED=1");
        } else {
            SystemLogUtil.logFailed("Xóa khu vực", "AREA", cleanAreaId, areaToLogValue(oldArea), "Xóa thất bại");
        }

        return success;
    }

    public String getDeleteRestrictionMessage(String areaId) {
        if (areaId != null && areaDatabase.hasActiveSegments(areaId.trim())) {
            return "Không thể xóa khu vực vì vẫn còn đoạn đường đang hoạt động.";
        }

        return "Không thể xóa khu vực.";
    }

    public boolean updateArea(String areaId, String areaName, String areaType, String oldProvince) {
        if (areaId == null || areaId.trim().isEmpty()) {
            return false;
        }

        if (areaName == null || areaName.trim().isEmpty()) {
            return false;
        }

        if (areaType == null || areaType.trim().isEmpty()) {
            return false;
        }

        if (oldProvince == null || oldProvince.trim().isEmpty()) {
            return false;
        }

        String cleanAreaId = areaId.trim();
        Area oldArea = areaDatabase.getAreaById(cleanAreaId);

        Area area = new Area();
        area.setAreaId(cleanAreaId);
        area.setAreaName(areaName.trim());
        area.setAreaType(areaType.trim());
        area.setOldProvince(oldProvince.trim());

        boolean success = areaDatabase.updateArea(area);

        if (success) {
            SystemLogUtil.logSuccess("Cập nhật khu vực", "AREA", cleanAreaId, areaToLogValue(oldArea), areaToLogValue(area));
        } else {
            SystemLogUtil.logFailed("Cập nhật khu vực", "AREA", cleanAreaId, areaToLogValue(oldArea), areaToLogValue(area));
        }

        return success;
    }

    private String areaToLogValue(Area area) {
        if (area == null) {
            return null;
        }

        return "AREA_ID=" + area.getAreaId()
                + ", AREA_NAME=" + area.getAreaName()
                + ", AREA_TYPE=" + area.getAreaType()
                + ", OLD_PROVINCE=" + area.getOldProvince();
    }
}
