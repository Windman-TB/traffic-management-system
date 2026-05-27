package com.mycompany.trafficsystem.controller;

import com.mycompany.trafficsystem.database.TrafficDatabase;
import com.mycompany.trafficsystem.model.Traffic;
import com.mycompany.trafficsystem.util.SystemLogUtil;

import java.util.List;

public class TrafficController {

    private final TrafficDatabase trafficDatabase;

    public TrafficController() {
        this.trafficDatabase = new TrafficDatabase();
    }

    public List<Traffic> getAllTraffic() {
        return trafficDatabase.getAllTraffic();
    }

    public List<Traffic> searchTraffic(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllTraffic();
        }

        String normalizedKeyword = keyword.trim();

        if (normalizedKeyword.toUpperCase().startsWith("TT")) {
            normalizedKeyword = removeDisplayPrefix(normalizedKeyword, "TT");
        } else if (normalizedKeyword.toUpperCase().startsWith("SEG")) {
            normalizedKeyword = removeDisplayPrefix(normalizedKeyword, "SEG");
        }

        return trafficDatabase.searchTraffic(normalizedKeyword);
    }

    public boolean updateTraffic(String statusId, String segmentIdText, String velocityText) {
        if (statusId == null || statusId.trim().isEmpty()) {
            return false;
        }

        String segmentId = normalizeSegmentId(segmentIdText);
        Double velocity = parseDouble(velocityText);

        if (segmentId == null || segmentId.isEmpty() || !isValidVelocity(velocity)) {
            return false;
        }

        String cleanStatusId = statusId.trim();
        Traffic oldTraffic = trafficDatabase.getTrafficById(cleanStatusId);

        Traffic traffic = new Traffic();
        traffic.setStatusId(cleanStatusId);
        traffic.setSegmentId(segmentId);
        traffic.setVelocity(velocity);

        boolean success = trafficDatabase.updateTraffic(traffic);

        if (success) {
            SystemLogUtil.logSuccess("Cập nhật lưu lượng", "TRAFFIC", cleanStatusId, trafficToLogValue(oldTraffic), trafficToLogValue(traffic));
        } else {
            SystemLogUtil.logFailed("Cập nhật lưu lượng", "TRAFFIC", cleanStatusId, trafficToLogValue(oldTraffic), trafficToLogValue(traffic));
        }

        return success;
    }

    public boolean deleteTraffic(String statusId) {
        if (statusId == null || statusId.trim().isEmpty()) {
            return false;
        }

        String cleanStatusId = statusId.trim();
        Traffic oldTraffic = trafficDatabase.getTrafficById(cleanStatusId);
        boolean success = trafficDatabase.deleteTraffic(cleanStatusId);

        if (success) {
            SystemLogUtil.logSuccess("Xóa lưu lượng", "TRAFFIC", cleanStatusId, trafficToLogValue(oldTraffic), "DELETE");
        } else {
            SystemLogUtil.logFailed("Xóa lưu lượng", "TRAFFIC", cleanStatusId, trafficToLogValue(oldTraffic), "Xóa thất bại");
        }

        return success;
    }

    public String normalizeSegmentId(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim();

        if (normalized.toUpperCase().startsWith("SEG")) {
            normalized = removeDisplayPrefix(normalized, "SEG");
        }

        return normalized;
    }

    private String removeDisplayPrefix(String value, String prefix) {
        try {
            int number = Integer.parseInt(value.substring(prefix.length()).trim());
            return String.valueOf(number);
        } catch (NumberFormatException e) {
            return value;
        }
    }

    private Double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean isValidVelocity(Double velocity) {
        return velocity != null && velocity >= 0;
    }

    private String trafficToLogValue(Traffic traffic) {
        if (traffic == null) {
            return null;
        }

        return "STATUS_ID=" + traffic.getStatusId()
                + ", SEGMENT_ID=" + traffic.getSegmentId()
                + ", VELOCITY=" + traffic.getVelocity();
    }
}
