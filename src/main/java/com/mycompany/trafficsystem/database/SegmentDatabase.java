/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.database;

import com.mycompany.trafficsystem.model.Segment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Truy vấn CSDL cho bảng SEGMENT.
 */
public class SegmentDatabase {

    private static final double EARTH_RADIUS_METERS = 6371000.0;

    public int countSegments() {
        String sql = """
            SELECT COUNT(*) AS TOTAL
            FROM SEGMENT
            WHERE IS_DELETED = 0
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("TOTAL");
            }

        } catch (SQLException e) {
            System.out.println("Lỗi đếm số đoạn đường: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public List<Segment> getAllSegments() {
        List<Segment> segments = new ArrayList<>();

        String sql = """
            SELECT SEGMENT_ID,
                   STREET_ID,
                   AREA_ID,
                   START_NODE_ID,
                   END_NODE_ID,
                   SEGMENT_LENGTH,
                   MAX_VELOCITY,
                   CREATED_AT,
                   UPDATED_AT,
                   IS_DELETED
            FROM SEGMENT
            WHERE IS_DELETED = 0
            ORDER BY TO_NUMBER(SEGMENT_ID)
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                segments.add(mapResultSetToSegment(rs));
            }

        } catch (SQLException e) {
            System.out.println("Lỗi lấy danh sách đoạn đường: " + e.getMessage());
            e.printStackTrace();
        }

        return segments;
    }

    public List<Segment> searchSegments(String keyword) {
        List<Segment> segments = new ArrayList<>();

        String sql = """
            SELECT SEGMENT_ID,
                   STREET_ID,
                   AREA_ID,
                   START_NODE_ID,
                   END_NODE_ID,
                   SEGMENT_LENGTH,
                   MAX_VELOCITY,
                   CREATED_AT,
                   UPDATED_AT,
                   IS_DELETED
            FROM SEGMENT
            WHERE IS_DELETED = 0
              AND (
                    LOWER(SEGMENT_ID) LIKE LOWER(?)
                 OR LOWER(STREET_ID) LIKE LOWER(?)
                 OR LOWER(AREA_ID) LIKE LOWER(?)
                 OR LOWER(START_NODE_ID) LIKE LOWER(?)
                 OR LOWER(END_NODE_ID) LIKE LOWER(?)
                 OR TO_CHAR(SEGMENT_LENGTH) LIKE ?
                 OR TO_CHAR(MAX_VELOCITY) LIKE ?
              )
            ORDER BY TO_NUMBER(SEGMENT_ID)
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchValue = "%" + keyword.trim() + "%";

            for (int i = 1; i <= 7; i++) {
                ps.setString(i, searchValue);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    segments.add(mapResultSetToSegment(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi tìm kiếm đoạn đường: " + e.getMessage());
            e.printStackTrace();
        }

        return segments;
    }

    public String generateNextSegmentId() {
        String sql = """
            SELECT MAX(TO_NUMBER(SEGMENT_ID)) AS MAX_ID
            FROM SEGMENT
            WHERE REGEXP_LIKE(SEGMENT_ID, '^[0-9]+$')
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int maxId = rs.getInt("MAX_ID");

                if (rs.wasNull()) {
                    return "0";
                }

                return String.valueOf(maxId + 1);
            }

        } catch (SQLException e) {
            System.out.println("Lỗi tự sinh mã đoạn đường: " + e.getMessage());
            e.printStackTrace();
        }

        return "0";
    }

    public boolean enrichSegmentGeometry(Segment segment) {
        Coordinate start = getNodeCoordinate(segment.getStartNodeId());
        Coordinate end = getNodeCoordinate(segment.getEndNodeId());

        if (start == null || end == null) {
            return false;
        }

        double distanceMeters = calculateDistanceMeters(start, end);
        Coordinate midpoint = new Coordinate(
                (start.latitude + end.latitude) / 2.0,
                (start.longitude + end.longitude) / 2.0
        );

        segment.setSegmentLength(roundTwoDecimals(distanceMeters));
        segment.setAreaId(findAreaIdContainingPoint(midpoint));
        return true;
    }

    public boolean insertSegment(Segment segment) {
        Segment deletedSegment = findDeletedSegmentByContent(segment);

        if (deletedSegment != null) {
            segment.setSegmentId(deletedSegment.getSegmentId());
            return restoreSegment(deletedSegment.getSegmentId());
        }

        String sql = """
            INSERT INTO SEGMENT (
                SEGMENT_ID,
                STREET_ID,
                AREA_ID,
                START_NODE_ID,
                END_NODE_ID,
                SEGMENT_LENGTH,
                MAX_VELOCITY,
                CREATED_AT,
                UPDATED_AT,
                IS_DELETED
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, SYSDATE + 7/24, SYSDATE + 7/24, 0)
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, segment.getSegmentId());
            ps.setString(2, segment.getStreetId());
            setNullableString(ps, 3, segment.getAreaId());
            ps.setString(4, segment.getStartNodeId());
            ps.setString(5, segment.getEndNodeId());
            setNullableDouble(ps, 6, segment.getSegmentLength());
            setNullableInteger(ps, 7, segment.getMaxVelocity());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi thêm đoạn đường: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    private Segment findDeletedSegmentByContent(Segment segment) {
        String sql = """
            SELECT SEGMENT_ID,
                   STREET_ID,
                   AREA_ID,
                   START_NODE_ID,
                   END_NODE_ID,
                   SEGMENT_LENGTH,
                   MAX_VELOCITY,
                   CREATED_AT,
                   UPDATED_AT,
                   IS_DELETED
            FROM SEGMENT
            WHERE IS_DELETED = 1
              AND STREET_ID = ?
              AND NVL(AREA_ID, '#NULL#') = NVL(?, '#NULL#')
              AND START_NODE_ID = ?
              AND END_NODE_ID = ?
              AND NVL(SEGMENT_LENGTH, -1) = NVL(?, -1)
              AND NVL(MAX_VELOCITY, -1) = NVL(?, -1)
            ORDER BY TO_NUMBER(SEGMENT_ID)
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, segment.getStreetId());
            setNullableString(ps, 2, segment.getAreaId());
            ps.setString(3, segment.getStartNodeId());
            ps.setString(4, segment.getEndNodeId());
            setNullableDouble(ps, 5, segment.getSegmentLength());
            setNullableInteger(ps, 6, segment.getMaxVelocity());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSegment(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi tìm đoạn đường đã xóa mềm: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private boolean restoreSegment(String segmentId) {
        String sql = """
            UPDATE SEGMENT
            SET IS_DELETED = 0,
                UPDATED_AT = SYSDATE + 7/24
            WHERE SEGMENT_ID = ?
              AND IS_DELETED = 1
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, segmentId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi khôi phục đoạn đường đã xóa mềm: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    private Coordinate getNodeCoordinate(String nodeId) {
        String sql = """
            SELECT LATITUDE,
                   LONGITUDE
            FROM NODE
            WHERE NODE_ID = ?
              AND IS_DELETED = 0
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nodeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Coordinate(rs.getDouble("LATITUDE"), rs.getDouble("LONGITUDE"));
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi lấy tọa độ nút giao: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private String findAreaIdContainingPoint(Coordinate point) {
        String sql = """
            SELECT ab.AREA_ID,
                   ab.BOUNDARY_WKT
            FROM AREA_BOUNDARY ab
            JOIN AREA a
                ON ab.AREA_ID = a.AREA_ID
            WHERE a.IS_DELETED = 0
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String wkt = rs.getString("BOUNDARY_WKT");

                if (containsPoint(wkt, point)) {
                    return rs.getString("AREA_ID");
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi xác định khu vực từ trung điểm đoạn đường: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private double calculateDistanceMeters(Coordinate start, Coordinate end) {
        double lat1 = Math.toRadians(start.latitude);
        double lat2 = Math.toRadians(end.latitude);
        double deltaLat = Math.toRadians(end.latitude - start.latitude);
        double deltaLon = Math.toRadians(end.longitude - start.longitude);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_METERS * c;
    }

    private Double roundTwoDecimals(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private boolean containsPoint(String wkt, Coordinate point) {
        if (wkt == null || wkt.trim().isEmpty()) {
            return false;
        }

        String normalized = wkt.trim().toUpperCase();
        if (!normalized.startsWith("POLYGON") && !normalized.startsWith("MULTIPOLYGON")) {
            return false;
        }

        List<List<Coordinate>> rings = parseWktRings(wkt);
        for (List<Coordinate> ring : rings) {
            if (isPointInRing(point, ring)) {
                return true;
            }
        }

        return false;
    }

    private List<List<Coordinate>> parseWktRings(String wkt) {
        List<List<Coordinate>> rings = new ArrayList<>();
        int ringStart = -1;
        int depth = 0;

        for (int i = 0; i < wkt.length(); i++) {
            char ch = wkt.charAt(i);

            if (ch == '(') {
                depth++;
                if (depth >= 2 && ringStart == -1 && isCoordinateListStart(wkt, i + 1)) {
                    ringStart = i + 1;
                }
            } else if (ch == ')') {
                if (ringStart != -1 && depth >= 2) {
                    String ringText = wkt.substring(ringStart, i);
                    List<Coordinate> ring = parseCoordinateRing(ringText);
                    if (ring.size() >= 3) {
                        rings.add(ring);
                    }
                    ringStart = -1;
                }
                depth--;
            }
        }

        return rings;
    }

    private boolean isCoordinateListStart(String text, int startIndex) {
        for (int i = startIndex; i < text.length(); i++) {
            char ch = text.charAt(i);

            if (Character.isWhitespace(ch)) {
                continue;
            }

            return ch != '(';
        }

        return false;
    }

    private List<Coordinate> parseCoordinateRing(String ringText) {
        List<Coordinate> ring = new ArrayList<>();
        String[] coordinatePairs = ringText.split(",");

        for (String pair : coordinatePairs) {
            String[] parts = pair.trim().split("\\s+");

            if (parts.length < 2) {
                continue;
            }

            try {
                double longitude = Double.parseDouble(parts[0]);
                double latitude = Double.parseDouble(parts[1]);
                ring.add(new Coordinate(latitude, longitude));
            } catch (NumberFormatException e) {
                return new ArrayList<>();
            }
        }

        return ring;
    }

    private boolean isPointInRing(Coordinate point, List<Coordinate> ring) {
        boolean inside = false;

        for (int i = 0, j = ring.size() - 1; i < ring.size(); j = i++) {
            Coordinate current = ring.get(i);
            Coordinate previous = ring.get(j);

            boolean intersects = ((current.latitude > point.latitude) != (previous.latitude > point.latitude))
                    && (point.longitude < (previous.longitude - current.longitude)
                    * (point.latitude - current.latitude)
                    / (previous.latitude - current.latitude)
                    + current.longitude);

            if (intersects) {
                inside = !inside;
            }
        }

        return inside;
    }

    public boolean updateSegment(Segment segment) {
        String sql = """
            UPDATE SEGMENT
            SET STREET_ID = ?,
                AREA_ID = ?,
                START_NODE_ID = ?,
                END_NODE_ID = ?,
                SEGMENT_LENGTH = ?,
                MAX_VELOCITY = ?,
                UPDATED_AT = SYSDATE + 7/24
            WHERE SEGMENT_ID = ?
              AND IS_DELETED = 0
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, segment.getStreetId());
            setNullableString(ps, 2, segment.getAreaId());
            ps.setString(3, segment.getStartNodeId());
            ps.setString(4, segment.getEndNodeId());
            setNullableDouble(ps, 5, segment.getSegmentLength());
            setNullableInteger(ps, 6, segment.getMaxVelocity());
            ps.setString(7, segment.getSegmentId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi cập nhật đoạn đường: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean softDeleteSegment(String segmentId) {
        if (hasTrafficInLast30Days(segmentId)) {
            System.out.println("Không thể xóa đoạn đường vì có dữ liệu lưu lượng trong 30 ngày gần đây.");
            return false;
        }

        String sql = """
            UPDATE SEGMENT
            SET IS_DELETED = 1,
                UPDATED_AT = SYSDATE + 7/24
            WHERE SEGMENT_ID = ?
              AND IS_DELETED = 0
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, segmentId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi xóa đoạn đường: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean hasTrafficInLast30Days(String segmentId) {
        String sql = """
            SELECT COUNT(*) AS TOTAL
            FROM TRAFFIC
            WHERE SEGMENT_ID = ?
              AND RECORDED_AT >= (SYSDATE + 7/24) - 30
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, segmentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("TOTAL") > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi kiểm tra lưu lượng 30 ngày gần đây của đoạn đường: " + e.getMessage());
            e.printStackTrace();
            return true;
        }

        return false;
    }


public Segment getSegmentById(String segmentId) {
    String sql = """
        SELECT SEGMENT_ID,
               STREET_ID,
               AREA_ID,
               START_NODE_ID,
               END_NODE_ID,
               SEGMENT_LENGTH,
               MAX_VELOCITY,
               CREATED_AT,
               UPDATED_AT,
               IS_DELETED
        FROM SEGMENT
        WHERE SEGMENT_ID = ?
    """;

    try (Connection conn = ConnectDB.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, segmentId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToSegment(rs);
            }
        }

    } catch (SQLException e) {
        System.out.println("Lỗi lấy đoạn đường theo mã: " + e.getMessage());
        e.printStackTrace();
    }

    return null;
}

    private Segment mapResultSetToSegment(ResultSet rs) throws SQLException {
        Segment segment = new Segment();

        segment.setSegmentId(rs.getString("SEGMENT_ID"));
        segment.setStreetId(rs.getString("STREET_ID"));
        segment.setAreaId(rs.getString("AREA_ID"));
        segment.setStartNodeId(rs.getString("START_NODE_ID"));
        segment.setEndNodeId(rs.getString("END_NODE_ID"));

        double segmentLength = rs.getDouble("SEGMENT_LENGTH");
        segment.setSegmentLength(rs.wasNull() ? null : segmentLength);

        int maxVelocity = rs.getInt("MAX_VELOCITY");
        segment.setMaxVelocity(rs.wasNull() ? null : maxVelocity);

        if (rs.getTimestamp("CREATED_AT") != null) {
            segment.setCreatedAt(rs.getTimestamp("CREATED_AT").toLocalDateTime());
        }

        if (rs.getTimestamp("UPDATED_AT") != null) {
            segment.setUpdatedAt(rs.getTimestamp("UPDATED_AT").toLocalDateTime());
        }

        segment.setIsDeleted(rs.getInt("IS_DELETED"));

        return segment;
    }

    private void setNullableString(PreparedStatement ps, int index, String value) throws SQLException {
        if (value == null || value.trim().isEmpty()) {
            ps.setNull(index, Types.VARCHAR);
        } else {
            ps.setString(index, value.trim());
        }
    }

    private void setNullableDouble(PreparedStatement ps, int index, Double value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.NUMERIC);
        } else {
            ps.setDouble(index, value);
        }
    }

    private void setNullableInteger(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.NUMERIC);
        } else {
            ps.setInt(index, value);
        }
    }

    private static class Coordinate {
        private final double latitude;
        private final double longitude;

        private Coordinate(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
