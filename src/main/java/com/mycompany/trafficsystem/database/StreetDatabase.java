/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.database;

import com.mycompany.trafficsystem.model.Street;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StreetDatabase {

    public int countStreets() {
        String sql = """
            SELECT COUNT(*) AS TOTAL
            FROM STREET
            WHERE IS_DELETED = 0
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("TOTAL");
            }

        } catch (SQLException e) {
            System.out.println("Lỗi đếm số tuyến đường: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public List<Street> getAllStreets() {
        List<Street> streets = new ArrayList<>();

        String sql = """
            SELECT STREET_ID,
                   STREET_NAME,
                   STREET_TYPE,
                   ROAD_LEVEL,
                   CREATED_AT,
                   IS_DELETED,
                   UPDATED_AT
            FROM STREET
            WHERE IS_DELETED = 0
            ORDER BY TO_NUMBER(STREET_ID)
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                streets.add(mapResultSetToStreet(rs));
            }

        } catch (SQLException e) {
            System.out.println("Lỗi lấy danh sách tuyến đường: " + e.getMessage());
            e.printStackTrace();
        }

        return streets;
    }

    public List<Street> searchStreets(String keyword) {
        List<Street> streets = new ArrayList<>();

        String sql = """
            SELECT STREET_ID,
                   STREET_NAME,
                   STREET_TYPE,
                   ROAD_LEVEL,
                   CREATED_AT,
                   IS_DELETED,
                   UPDATED_AT
            FROM STREET
            WHERE IS_DELETED = 0
              AND (
                    LOWER(STREET_ID) LIKE LOWER(?)
                 OR LOWER(STREET_NAME) LIKE LOWER(?)
                 OR LOWER(STREET_TYPE) LIKE LOWER(?)
                 OR TO_CHAR(ROAD_LEVEL) LIKE ?
              )
            ORDER BY TO_NUMBER(STREET_ID)
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchValue = "%" + keyword.trim() + "%";

            ps.setString(1, searchValue);
            ps.setString(2, searchValue);
            ps.setString(3, searchValue);
            ps.setString(4, searchValue);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    streets.add(mapResultSetToStreet(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi tìm kiếm tuyến đường: " + e.getMessage());
            e.printStackTrace();
        }

        return streets;
    }

    public String generateNextStreetId() {
        String sql = """
            SELECT MAX(TO_NUMBER(STREET_ID)) AS MAX_ID
            FROM STREET
            WHERE REGEXP_LIKE(STREET_ID, '^[0-9]+$')
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int maxId = rs.getInt("MAX_ID");

                if (rs.wasNull()) {
                    return "1";
                }

                return String.valueOf(maxId + 1);
            }

        } catch (SQLException e) {
            System.out.println("Lỗi tự sinh mã tuyến đường: " + e.getMessage());
            e.printStackTrace();
        }

        return "1";
    }

    public boolean insertStreet(Street street) {
        Street deletedStreet = findDeletedStreetByContent(street);

        if (deletedStreet != null) {
            street.setStreetId(deletedStreet.getStreetId());
            return restoreStreet(deletedStreet.getStreetId());
        }

        String sql = """
            INSERT INTO STREET (
                STREET_ID,
                STREET_NAME,
                STREET_TYPE,
                ROAD_LEVEL,
                CREATED_AT,
                IS_DELETED,
                UPDATED_AT
            )
            VALUES (?, ?, ?, ?, SYSDATE + 7/24, 0, SYSDATE + 7/24)
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, street.getStreetId());
            ps.setString(2, street.getStreetName());
            ps.setString(3, street.getStreetType());
            ps.setInt(4, street.getRoadLevel());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi thêm tuyến đường: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    private Street findDeletedStreetByContent(Street street) {
        String sql = """
            SELECT STREET_ID,
                   STREET_NAME,
                   STREET_TYPE,
                   ROAD_LEVEL,
                   CREATED_AT,
                   IS_DELETED,
                   UPDATED_AT
            FROM STREET
            WHERE IS_DELETED = 1
              AND LOWER(TRIM(STREET_NAME)) = LOWER(TRIM(?))
              AND LOWER(TRIM(STREET_TYPE)) = LOWER(TRIM(?))
              AND ROAD_LEVEL = ?
            ORDER BY TO_NUMBER(STREET_ID)
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, street.getStreetName());
            ps.setString(2, street.getStreetType());
            ps.setInt(3, street.getRoadLevel());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStreet(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi tìm tuyến đường đã xóa mềm: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private boolean restoreStreet(String streetId) {
        String sql = """
            UPDATE STREET
            SET IS_DELETED = 0,
                UPDATED_AT = SYSDATE + 7/24
            WHERE STREET_ID = ?
              AND IS_DELETED = 1
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, streetId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi khôi phục tuyến đường đã xóa mềm: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateStreet(Street street) {
        String sql = """
            UPDATE STREET
            SET STREET_NAME = ?,
                STREET_TYPE = ?,
                ROAD_LEVEL = ?,
                UPDATED_AT = SYSDATE + 7/24
            WHERE STREET_ID = ?
              AND IS_DELETED = 0
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, street.getStreetName());
            ps.setString(2, street.getStreetType());
            ps.setInt(3, street.getRoadLevel());
            ps.setString(4, street.getStreetId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi cập nhật tuyến đường: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean softDeleteStreet(String streetId) {
        if (hasActiveSegments(streetId)) {
            System.out.println("Không thể xóa tuyến đường vì vẫn còn đoạn đường đang hoạt động.");
            return false;
        }

        String sql = """
            UPDATE STREET
            SET IS_DELETED = 1,
                UPDATED_AT = SYSDATE + 7/24
            WHERE STREET_ID = ?
              AND IS_DELETED = 0
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, streetId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi xóa tuyến đường: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean hasActiveSegments(String streetId) {
        String sql = """
            SELECT COUNT(*) AS TOTAL
            FROM SEGMENT
            WHERE STREET_ID = ?
              AND IS_DELETED = 0
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, streetId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("TOTAL") > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi kiểm tra đoạn đường thuộc tuyến đường: " + e.getMessage());
            e.printStackTrace();
            return true;
        }

        return false;
    }


public Street getStreetById(String streetId) {
    String sql = """
        SELECT STREET_ID,
               STREET_NAME,
               STREET_TYPE,
               ROAD_LEVEL,
               IS_DELETED,
               CREATED_AT,
               UPDATED_AT
        FROM STREET
        WHERE STREET_ID = ?
    """;

    try (Connection conn = ConnectDB.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, streetId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToStreet(rs);
            }
        }

    } catch (SQLException e) {
        System.out.println("Lỗi lấy tuyến đường theo mã: " + e.getMessage());
        e.printStackTrace();
    }

    return null;
}

    private Street mapResultSetToStreet(ResultSet rs) throws SQLException {
        Street street = new Street();

        street.setStreetId(rs.getString("STREET_ID"));
        street.setStreetName(rs.getString("STREET_NAME"));
        street.setStreetType(rs.getString("STREET_TYPE"));
        street.setRoadLevel(rs.getInt("ROAD_LEVEL"));
        street.setIsDeleted(rs.getInt("IS_DELETED"));

        if (rs.getTimestamp("CREATED_AT") != null) {
            street.setCreatedAt(rs.getTimestamp("CREATED_AT").toLocalDateTime());
        }

        if (rs.getTimestamp("UPDATED_AT") != null) {
            street.setUpdatedAt(rs.getTimestamp("UPDATED_AT").toLocalDateTime());
        }

        return street;
    }
}
