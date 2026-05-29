package com.mycompany.trafficsystem.controller;

import com.mycompany.trafficsystem.database.SegmentDatabase;
import com.mycompany.trafficsystem.model.Segment;
import com.mycompany.trafficsystem.service.GoongSpeedLimitService;
import com.mycompany.trafficsystem.util.SystemLogUtil;

import java.util.List;

public class SegmentController {

    private final SegmentDatabase segmentDatabase;
    private final GoongSpeedLimitService goongSpeedLimitService;

    public SegmentController() {
        this.segmentDatabase = new SegmentDatabase();
        this.goongSpeedLimitService = new GoongSpeedLimitService();
    }

    public List<Segment> getAllSegments() {
        return segmentDatabase.getAllSegments();
    }

    public List<Segment> searchSegments(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllSegments();
        }

        String normalizedKeyword = keyword.trim();
        String upperKeyword = normalizedKeyword.toUpperCase();

        if (upperKeyword.startsWith("SEG")) {
            normalizedKeyword = removeDisplayPrefix(normalizedKeyword, "SEG");
        } else if (upperKeyword.startsWith("KV")) {
            normalizedKeyword = removeDisplayPrefix(normalizedKeyword, "KV");
        } else if (upperKeyword.startsWith("NG")) {
            normalizedKeyword = removeDisplayPrefix(normalizedKeyword, "NG");
        }

        return segmentDatabase.searchSegments(normalizedKeyword);
    }

    public String generateNextSegmentId() {
        return segmentDatabase.generateNextSegmentId();
    }

    public boolean addSegment(String streetIdText, String startNodeIdText,
                              String endNodeIdText, String maxVelocityText) {
        String streetId = normalizePlainId(streetIdText);
        String startNodeId = normalizeNodeId(startNodeIdText);
        String endNodeId = normalizeNodeId(endNodeIdText);
        Integer maxVelocity = parseIntegerAllowEmpty(maxVelocityText);

        if (!isValidRequiredId(streetId) || !isValidRequiredId(startNodeId) || !isValidRequiredId(endNodeId)) {
            return false;
        }

        if (startNodeId.equals(endNodeId)) {
            return false;
        }

        if (!isValidOptionalVelocity(maxVelocity)) {
            return false;
        }

        Segment segment = new Segment();
        segment.setSegmentId(generateNextSegmentId());
        segment.setStreetId(streetId);
        segment.setStartNodeId(startNodeId);
        segment.setEndNodeId(endNodeId);
        segment.setMaxVelocity(maxVelocity);

        if (!segmentDatabase.enrichSegmentGeometry(segment)) {
            SystemLogUtil.logFailed("Thêm đoạn đường", "SEGMENT", segment.getSegmentId(), null, segmentToLogValue(segment));
            return false;
        }

        autoFillMaxVelocityIfEmpty(segment);

        boolean success = segmentDatabase.insertSegment(segment);

        if (success) {
            SystemLogUtil.logSuccess("Thêm đoạn đường", "SEGMENT", segment.getSegmentId(), null, segmentToLogValue(segment));
        } else {
            SystemLogUtil.logFailed("Thêm đoạn đường", "SEGMENT", segment.getSegmentId(), null, segmentToLogValue(segment));
        }

        return success;
    }

    public boolean updateSegment(String segmentId, String streetIdText,
                                 String startNodeIdText, String endNodeIdText,
                                 String maxVelocityText) {
        if (segmentId == null || segmentId.trim().isEmpty()) {
            return false;
        }

        String streetId = normalizePlainId(streetIdText);
        String startNodeId = normalizeNodeId(startNodeIdText);
        String endNodeId = normalizeNodeId(endNodeIdText);
        Integer maxVelocity = parseIntegerAllowEmpty(maxVelocityText);

        if (!isValidRequiredId(streetId) || !isValidRequiredId(startNodeId) || !isValidRequiredId(endNodeId)) {
            return false;
        }

        if (startNodeId.equals(endNodeId)) {
            return false;
        }

        if (!isValidOptionalVelocity(maxVelocity)) {
            return false;
        }

        String cleanSegmentId = segmentId.trim();
        Segment oldSegment = segmentDatabase.getSegmentById(cleanSegmentId);

        Segment segment = new Segment();
        segment.setSegmentId(cleanSegmentId);
        segment.setStreetId(streetId);
        segment.setStartNodeId(startNodeId);
        segment.setEndNodeId(endNodeId);
        segment.setMaxVelocity(maxVelocity);

        if (!segmentDatabase.enrichSegmentGeometry(segment)) {
            SystemLogUtil.logFailed("Cập nhật đoạn đường", "SEGMENT", cleanSegmentId, segmentToLogValue(oldSegment), segmentToLogValue(segment));
            return false;
        }

        autoFillMaxVelocityIfEmpty(segment);

        boolean success = segmentDatabase.updateSegment(segment);

        if (success) {
            SystemLogUtil.logSuccess("Cập nhật đoạn đường", "SEGMENT", cleanSegmentId, segmentToLogValue(oldSegment), segmentToLogValue(segment));
        } else {
            SystemLogUtil.logFailed("Cập nhật đoạn đường", "SEGMENT", cleanSegmentId, segmentToLogValue(oldSegment), segmentToLogValue(segment));
        }

        return success;
    }

    public boolean deleteSegment(String segmentId) {
        if (segmentId == null || segmentId.trim().isEmpty()) {
            return false;
        }

        String cleanSegmentId = segmentId.trim();
        Segment oldSegment = segmentDatabase.getSegmentById(cleanSegmentId);
        boolean success = segmentDatabase.softDeleteSegment(cleanSegmentId);

        if (success) {
            SystemLogUtil.logSuccess("Xóa đoạn đường", "SEGMENT", cleanSegmentId, segmentToLogValue(oldSegment), "IS_DELETED=1");
        } else {
            SystemLogUtil.logFailed("Xóa đoạn đường", "SEGMENT", cleanSegmentId, segmentToLogValue(oldSegment), "Xóa thất bại");
        }

        return success;
    }

    public String getDeleteRestrictionMessage(String segmentId) {
        if (segmentId != null && segmentDatabase.hasTrafficInLast30Days(segmentId.trim())) {
            return "Không thể xóa đoạn đường vì có dữ liệu lưu lượng trong 30 ngày gần đây.";
        }

        return "Không thể xóa đoạn đường.";
    }

    public String normalizeAreaId(String value) {
        return normalizePrefixedId(value, "KV");
    }

    public String normalizeNodeId(String value) {
        return normalizePrefixedId(value, "NG");
    }

    public String normalizeSegmentId(String value) {
        return normalizePrefixedId(value, "SEG");
    }

    public String normalizePlainId(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private String normalizePrefixedId(String value, String prefix) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim();

        if (normalized.toUpperCase().startsWith(prefix)) {
            normalized = removeDisplayPrefix(normalized, prefix);
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

    private Integer parseIntegerAllowEmpty(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private boolean isValidRequiredId(String id) {
        return id != null && !id.trim().isEmpty();
    }

    private boolean isValidOptionalVelocity(Integer maxVelocity) {
        return maxVelocity == null || maxVelocity >= 0;
    }

    private void autoFillMaxVelocityIfEmpty(Segment segment) {
        if (segment.getMaxVelocity() != null) {
            return;
        }

        double[] midpoint = segmentDatabase.getSegmentMidpoint(
                segment.getStartNodeId(),
                segment.getEndNodeId()
        );

        if (midpoint == null) {
            return;
        }

        Integer speedLimit = goongSpeedLimitService.getSpeedLimit(midpoint[0], midpoint[1]);

        if (speedLimit != null && speedLimit >= 0) {
            segment.setMaxVelocity(speedLimit);
        }
    }

    private String segmentToLogValue(Segment segment) {
        if (segment == null) {
            return null;
        }

        return "SEGMENT_ID=" + segment.getSegmentId()
                + ", STREET_ID=" + segment.getStreetId()
                + ", AREA_ID=" + segment.getAreaId()
                + ", START_NODE_ID=" + segment.getStartNodeId()
                + ", END_NODE_ID=" + segment.getEndNodeId()
                + ", SEGMENT_LENGTH=" + segment.getSegmentLength()
                + ", MAX_VELOCITY=" + segment.getMaxVelocity();
    }
}
