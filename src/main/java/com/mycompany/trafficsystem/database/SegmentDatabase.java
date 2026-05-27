/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.database;

import com.mycompany.trafficsystem.model.Segment;

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

    public boolean insertSegment(Segment segment) {
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
}
