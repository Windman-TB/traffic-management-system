/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.database;

/**
 *
 * @author engineer
 */

import com.mycompany.trafficsystem.model.Area;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AreaDatabase {

    public int countAreas() {
        String sql = """
            SELECT COUNT(*) AS TOTAL
            FROM AREA
            WHERE IS_DELETED = 0
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("TOTAL");
            }

        } catch (SQLException e) {
            System.out.println("Lỗi đếm số khu vực: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public List<Area> getAllAreas() {
        List<Area> areas = new ArrayList<>();

        String sql = """
            SELECT AREA_ID,
                   AREA_NAME,
                   AREA_TYPE,
                   OLD_PROVINCE,
                   CREATED_AT,
                   UPDATED_AT,
                   IS_DELETED
            FROM AREA
            WHERE IS_DELETED = 0
            ORDER BY TO_NUMBER(AREA_ID)
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Area area = mapResultSetToArea(rs);
                areas.add(area);
            }

        } catch (SQLException e) {
            System.out.println("Lỗi lấy danh sách khu vực: " + e.getMessage());
            e.printStackTrace();
        }

        return areas;
    }

    public List<Area> searchAreas(String keyword) {
        List<Area> areas = new ArrayList<>();

        String sql = """
            SELECT AREA_ID,
                   AREA_NAME,
                   AREA_TYPE,
                   OLD_PROVINCE,
                   CREATED_AT,
                   UPDATED_AT,
                   IS_DELETED
            FROM AREA
            WHERE IS_DELETED = 0
              AND (
                    LOWER(AREA_ID) LIKE LOWER(?)
                 OR LOWER(AREA_NAME) LIKE LOWER(?)
                 OR LOWER(AREA_TYPE) LIKE LOWER(?)
                 OR LOWER(OLD_PROVINCE) LIKE LOWER(?)
              )
            ORDER BY TO_NUMBER(AREA_ID)
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
                    Area area = mapResultSetToArea(rs);
                    areas.add(area);
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi tìm kiếm khu vực: " + e.getMessage());
            e.printStackTrace();
        }

        return areas;
    }

    public boolean softDeleteArea(String areaId) {
        String sql = """
            UPDATE AREA
            SET IS_DELETED = 1,
                UPDATED_AT = SYSDATE + 7/24
            WHERE AREA_ID = ?
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, areaId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi xóa khu vực: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }


public Area getAreaById(String areaId) {
    String sql = """
        SELECT AREA_ID,
               AREA_NAME,
               AREA_TYPE,
               OLD_PROVINCE,
               CREATED_AT,
               UPDATED_AT,
               IS_DELETED
        FROM AREA
        WHERE AREA_ID = ?
    """;

    try (Connection conn = ConnectDB.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, areaId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToArea(rs);
            }
        }

    } catch (SQLException e) {
        System.out.println("Lỗi lấy khu vực theo mã: " + e.getMessage());
        e.printStackTrace();
    }

    return null;
}

    private Area mapResultSetToArea(ResultSet rs) throws SQLException {
        Area area = new Area();

        area.setAreaId(rs.getString("AREA_ID"));
        area.setAreaName(rs.getString("AREA_NAME"));
        area.setAreaType(rs.getString("AREA_TYPE"));
        area.setOldProvince(rs.getString("OLD_PROVINCE"));

        if (rs.getTimestamp("CREATED_AT") != null) {
            area.setCreatedAt(rs.getTimestamp("CREATED_AT").toLocalDateTime());
        }

        if (rs.getTimestamp("UPDATED_AT") != null) {
                area.setUpdatedAt(rs.getTimestamp("UPDATED_AT").toLocalDateTime());
        }
        return area;
    }

    public String generateNextAreaId() {
        String sql = """
            SELECT MAX(TO_NUMBER(AREA_ID)) AS MAX_ID
            FROM AREA
            WHERE REGEXP_LIKE(AREA_ID, '^[0-9]+$')
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
            System.out.println("Lỗi tự sinh mã khu vực: " + e.getMessage());
            e.printStackTrace();
        }

        return "1";
    }

    public boolean insertArea(Area area) {
        String sql = """
            INSERT INTO AREA (
                AREA_ID,
                AREA_NAME,
                AREA_TYPE,
                OLD_PROVINCE,
                CREATED_AT,
                UPDATED_AT,
                IS_DELETED
            )
            VALUES (?, ?, ?, ?, SYSDATE + 7/24, SYSDATE + 7/24, 0)
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, area.getAreaId());
            ps.setString(2, area.getAreaName());
            ps.setString(3, area.getAreaType());
            ps.setString(4, area.getOldProvince());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi thêm khu vực: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }


    public boolean updateArea(Area area) {
        String sql = """
            UPDATE AREA
            SET AREA_NAME = ?,
                AREA_TYPE = ?,
                OLD_PROVINCE = ?,
                UPDATED_AT = SYSDATE + 7/24
            WHERE AREA_ID = ?
              AND IS_DELETED = 0
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, area.getAreaName());
            ps.setString(2, area.getAreaType());
            ps.setString(3, area.getOldProvince());
            ps.setString(4, area.getAreaId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi cập nhật khu vực: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

}
