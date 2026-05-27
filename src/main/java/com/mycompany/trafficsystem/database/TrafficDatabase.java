/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.database;

import com.mycompany.trafficsystem.model.Traffic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Truy vấn CSDL cho bảng TRAFFIC.
 */
public class TrafficDatabase {

    public int countTraffic() {
        String sql = """
            SELECT COUNT(*) AS TOTAL
            FROM TRAFFIC
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("TOTAL");
            }

        } catch (SQLException e) {
            System.out.println("Lỗi đếm dữ liệu lưu lượng: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public List<Traffic> getAllTraffic() {
        List<Traffic> trafficList = new ArrayList<>();

        String sql = """
            SELECT STATUS_ID,
                   SEGMENT_ID,
                   VELOCITY,
                   CREATED_AT
            FROM TRAFFIC
            ORDER BY TO_NUMBER(STATUS_ID)
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                trafficList.add(mapResultSetToTraffic(rs));
            }

        } catch (SQLException e) {
            System.out.println("Lỗi lấy danh sách lưu lượng: " + e.getMessage());
            e.printStackTrace();
        }

        return trafficList;
    }

    public List<Traffic> searchTraffic(String keyword) {
        List<Traffic> trafficList = new ArrayList<>();

        String sql = """
            SELECT STATUS_ID,
                   SEGMENT_ID,
                   VELOCITY,
                   CREATED_AT
            FROM TRAFFIC
            WHERE LOWER(STATUS_ID) LIKE LOWER(?)
               OR LOWER(SEGMENT_ID) LIKE LOWER(?)
               OR TO_CHAR(VELOCITY) LIKE ?
               OR TO_CHAR(CREATED_AT, 'DD/MM/YYYY HH24:MI') LIKE ?
            ORDER BY TO_NUMBER(STATUS_ID)
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
                    trafficList.add(mapResultSetToTraffic(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi tìm kiếm lưu lượng: " + e.getMessage());
            e.printStackTrace();
        }

        return trafficList;
    }

    public boolean updateTraffic(Traffic traffic) {
        String sql = """
            UPDATE TRAFFIC
            SET SEGMENT_ID = ?,
                VELOCITY = ?,
                CREATED_AT = SYSDATE + 7/24
            WHERE STATUS_ID = ?
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, traffic.getSegmentId());
            ps.setDouble(2, traffic.getVelocity());
            ps.setString(3, traffic.getStatusId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi cập nhật lưu lượng: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteTraffic(String statusId) {
        String sql = """
            DELETE FROM TRAFFIC
            WHERE STATUS_ID = ?
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, statusId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi xóa lưu lượng: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }


public Traffic getTrafficById(String statusId) {
    String sql = """
        SELECT STATUS_ID,
               SEGMENT_ID,
               VELOCITY,
               CREATED_AT
        FROM TRAFFIC
        WHERE STATUS_ID = ?
    """;

    try (Connection conn = ConnectDB.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, statusId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToTraffic(rs);
            }
        }

    } catch (SQLException e) {
        System.out.println("Lỗi lấy lưu lượng theo mã: " + e.getMessage());
        e.printStackTrace();
    }

    return null;
}

    private Traffic mapResultSetToTraffic(ResultSet rs) throws SQLException {
        Traffic traffic = new Traffic();

        traffic.setStatusId(rs.getString("STATUS_ID"));
        traffic.setSegmentId(rs.getString("SEGMENT_ID"));
        traffic.setVelocity(rs.getDouble("VELOCITY"));

        Timestamp createdAt = rs.getTimestamp("CREATED_AT");
        if (createdAt != null) {
            traffic.setCreatedAt(createdAt.toLocalDateTime());
        }

        return traffic;
    }
}
