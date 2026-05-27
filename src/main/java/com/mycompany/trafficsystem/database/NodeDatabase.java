/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.database;

import com.mycompany.trafficsystem.model.Node;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Truy vấn CSDL cho bảng NODE.
 */
public class NodeDatabase {

    public int countNodes() {
        String sql = """
            SELECT COUNT(*) AS TOTAL
            FROM NODE
            WHERE IS_DELETED = 0
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("TOTAL");
            }

        } catch (SQLException e) {
            System.out.println("Lỗi đếm số nút giao: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public List<Node> getAllNodes() {
        List<Node> nodes = new ArrayList<>();

        String sql = """
            SELECT NODE_ID,
                   LATITUDE,
                   LONGITUDE,
                   IS_DELETED,
                   CREATED_AT,
                   UPDATED_AT
            FROM NODE
            WHERE IS_DELETED = 0
            ORDER BY TO_NUMBER(NODE_ID)
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                nodes.add(mapResultSetToNode(rs));
            }

        } catch (SQLException e) {
            System.out.println("Lỗi lấy danh sách nút giao: " + e.getMessage());
            e.printStackTrace();
        }

        return nodes;
    }

    public List<Node> searchNodes(String keyword) {
        List<Node> nodes = new ArrayList<>();

        String sql = """
            SELECT NODE_ID,
                   LATITUDE,
                   LONGITUDE,
                   IS_DELETED,
                   CREATED_AT,
                   UPDATED_AT
            FROM NODE
            WHERE IS_DELETED = 0
              AND (
                    LOWER(NODE_ID) LIKE LOWER(?)
                 OR TO_CHAR(LATITUDE) LIKE ?
                 OR TO_CHAR(LONGITUDE) LIKE ?
              )
            ORDER BY TO_NUMBER(NODE_ID)
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchValue = "%" + keyword.trim() + "%";

            ps.setString(1, searchValue);
            ps.setString(2, searchValue);
            ps.setString(3, searchValue);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    nodes.add(mapResultSetToNode(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi tìm kiếm nút giao: " + e.getMessage());
            e.printStackTrace();
        }

        return nodes;
    }

    public String generateNextNodeId() {
        String sql = """
            SELECT MAX(TO_NUMBER(NODE_ID)) AS MAX_ID
            FROM NODE
            WHERE REGEXP_LIKE(NODE_ID, '^[0-9]+$')
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
            System.out.println("Lỗi tự sinh mã nút giao: " + e.getMessage());
            e.printStackTrace();
        }

        return "1";
    }

    public boolean insertNode(Node node) {
        String sql = """
            INSERT INTO NODE (
                NODE_ID,
                LATITUDE,
                LONGITUDE,
                IS_DELETED,
                CREATED_AT,
                UPDATED_AT
            )
            VALUES (?, ?, ?, 0, SYSDATE + 7/24, SYSDATE + 7/24)
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, node.getNodeId());
            ps.setDouble(2, node.getLatitude());
            ps.setDouble(3, node.getLongitude());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi thêm nút giao: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateNode(Node node) {
        String sql = """
            UPDATE NODE
            SET LATITUDE = ?,
                LONGITUDE = ?,
                UPDATED_AT = SYSDATE + 7/24
            WHERE NODE_ID = ?
              AND IS_DELETED = 0
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, node.getLatitude());
            ps.setDouble(2, node.getLongitude());
            ps.setString(3, node.getNodeId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi cập nhật nút giao: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean softDeleteNode(String nodeId) {
        String sql = """
            UPDATE NODE
            SET IS_DELETED = 1,
                UPDATED_AT = SYSDATE + 7/24
            WHERE NODE_ID = ?
              AND IS_DELETED = 0
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nodeId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi xóa nút giao: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }


public Node getNodeById(String nodeId) {
    String sql = """
        SELECT NODE_ID,
               LATITUDE,
               LONGITUDE,
               IS_DELETED,
               CREATED_AT,
               UPDATED_AT
        FROM NODE
        WHERE NODE_ID = ?
    """;

    try (Connection conn = ConnectDB.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, nodeId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToNode(rs);
            }
        }

    } catch (SQLException e) {
        System.out.println("Lỗi lấy nút giao theo mã: " + e.getMessage());
        e.printStackTrace();
    }

    return null;
}

    private Node mapResultSetToNode(ResultSet rs) throws SQLException {
        Node node = new Node();

        node.setNodeId(rs.getString("NODE_ID"));
        node.setLatitude(rs.getDouble("LATITUDE"));
        node.setLongitude(rs.getDouble("LONGITUDE"));
        node.setIsDeleted(rs.getInt("IS_DELETED"));

        if (rs.getTimestamp("CREATED_AT") != null) {
            node.setCreatedAt(rs.getTimestamp("CREATED_AT").toLocalDateTime());
        }

        if (rs.getTimestamp("UPDATED_AT") != null) {
            node.setUpdatedAt(rs.getTimestamp("UPDATED_AT").toLocalDateTime());
        }

        return node;
    }
}
