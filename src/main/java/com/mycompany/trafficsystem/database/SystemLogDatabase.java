package com.mycompany.trafficsystem.database;

import com.mycompany.trafficsystem.model.SystemLog;
import com.mycompany.trafficsystem.model.SystemLogSummary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SystemLogDatabase {

    public boolean insertLog(SystemLog log) {
        String sql = """
            INSERT INTO SYSTEM_LOG (
                ACCOUNT_ID,
                BEHAVIOUR,
                TARGET_TABLE,
                TARGET_ID,
                OLD_VALUE,
                NEW_VALUE,
                LOG_STATUS,
                CREATED_AT
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, SYSDATE + 7/24)
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, log.getAccountId());
            ps.setString(2, log.getBehaviour());
            ps.setString(3, log.getTargetTable());
            ps.setString(4, log.getTargetId());
            ps.setString(5, log.getOldValue());
            ps.setString(6, log.getNewValue());
            ps.setString(7, log.getLogStatus());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi ghi nhật ký hệ thống: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public List<SystemLog> getAllLogs() {
        return searchLogs(null, null, null, null, null, null, null);
    }

    public SystemLog getLogById(String logId) {
        String sql = """
            SELECT LOG_ID,
                   ACCOUNT_ID,
                   BEHAVIOUR,
                   TARGET_TABLE,
                   TARGET_ID,
                   OLD_VALUE,
                   NEW_VALUE,
                   LOG_STATUS,
                   CREATED_AT
            FROM SYSTEM_LOG
            WHERE LOG_ID = ?
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, logId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSystemLog(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi lấy chi tiết nhật ký: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public List<SystemLog> searchLogs(String keyword,
                                      String accountId,
                                      String targetTable,
                                      String behaviour,
                                      String status,
                                      LocalDate fromDate,
                                      LocalDate toDate) {
        List<SystemLog> logs = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT LOG_ID,
                   ACCOUNT_ID,
                   BEHAVIOUR,
                   TARGET_TABLE,
                   TARGET_ID,
                   OLD_VALUE,
                   NEW_VALUE,
                   LOG_STATUS,
                   CREATED_AT
            FROM SYSTEM_LOG
            WHERE 1 = 1
        """);

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("""
                AND (
                    LOWER(LOG_ID) LIKE ?
                    OR LOWER(ACCOUNT_ID) LIKE ?
                    OR LOWER(BEHAVIOUR) LIKE ?
                    OR LOWER(TARGET_TABLE) LIKE ?
                    OR LOWER(TARGET_ID) LIKE ?
                    OR LOWER(NVL(OLD_VALUE, '')) LIKE ?
                    OR LOWER(NVL(NEW_VALUE, '')) LIKE ?
                )
            """);

            String kw = "%" + keyword.trim().toLowerCase() + "%";
            for (int i = 0; i < 7; i++) {
                params.add(kw);
            }
        }

        if (accountId != null && !accountId.trim().isEmpty()) {
            sql.append(" AND LOWER(ACCOUNT_ID) LIKE ? ");
            params.add("%" + accountId.trim().toLowerCase() + "%");
        }

        if (targetTable != null && !targetTable.trim().isEmpty() && !targetTable.equals("Tất cả")) {
            sql.append(" AND TARGET_TABLE = ? ");
            params.add(targetTable.trim());
        }

        if (behaviour != null && !behaviour.trim().isEmpty()) {
            sql.append(" AND LOWER(BEHAVIOUR) LIKE ? ");
            params.add("%" + behaviour.trim().toLowerCase() + "%");
        }

        if (status != null && !status.trim().isEmpty() && !status.equals("Tất cả")) {
            sql.append(" AND LOG_STATUS = ? ");
            params.add(status.trim());
        }

        if (fromDate != null) {
            sql.append(" AND CREATED_AT >= ? ");
            params.add(Timestamp.valueOf(fromDate.atStartOfDay()));
        }

        if (toDate != null) {
            sql.append(" AND CREATED_AT < ? ");
            params.add(Timestamp.valueOf(toDate.plusDays(1).atStartOfDay()));
        }

        sql.append(" ORDER BY CREATED_AT DESC, LOG_ID DESC ");

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToSystemLog(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi tra cứu nhật ký hệ thống: " + e.getMessage());
            e.printStackTrace();
        }

        return logs;
    }

    public SystemLogSummary getSummary() {
        SystemLogSummary summary = new SystemLogSummary();
        summary.setTotalLogs(countByCondition(null));
        summary.setSuccessLogs(countByCondition("SUCCESS"));
        summary.setFailedLogs(countByCondition("FAILED"));
        summary.setTodayLogs(countToday());
        summary.setLogsByTable(countGroupBy("TARGET_TABLE"));
        summary.setLogsByBehaviour(countGroupBy("BEHAVIOUR"));
        return summary;
    }

    private int countByCondition(String status) {
        String sql = status == null
                ? "SELECT COUNT(*) FROM SYSTEM_LOG"
                : "SELECT COUNT(*) FROM SYSTEM_LOG WHERE LOG_STATUS = ?";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (status != null) {
                ps.setString(1, status);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi đếm nhật ký hệ thống: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    private int countToday() {
        String sql = """
            SELECT COUNT(*)
            FROM SYSTEM_LOG
            WHERE CREATED_AT >= TRUNC(SYSDATE + 7/24)
              AND CREATED_AT < TRUNC(SYSDATE + 7/24) + 1
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Lỗi đếm nhật ký hôm nay: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    private Map<String, Integer> countGroupBy(String columnName) {
        Map<String, Integer> result = new LinkedHashMap<>();

        if (!columnName.equals("TARGET_TABLE") && !columnName.equals("BEHAVIOUR")) {
            return result;
        }

        String sql = "SELECT " + columnName + ", COUNT(*) AS TOTAL FROM SYSTEM_LOG GROUP BY "
                + columnName + " ORDER BY TOTAL DESC";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.put(rs.getString(1), rs.getInt(2));
            }

        } catch (SQLException e) {
            System.out.println("Lỗi thống kê nhật ký theo " + columnName + ": " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    private SystemLog mapResultSetToSystemLog(ResultSet rs) throws SQLException {
        SystemLog log = new SystemLog();

        log.setLogId(rs.getString("LOG_ID"));
        log.setAccountId(rs.getString("ACCOUNT_ID"));
        log.setBehaviour(rs.getString("BEHAVIOUR"));
        log.setTargetTable(rs.getString("TARGET_TABLE"));
        log.setTargetId(rs.getString("TARGET_ID"));
        log.setOldValue(rs.getString("OLD_VALUE"));
        log.setNewValue(rs.getString("NEW_VALUE"));
        log.setLogStatus(rs.getString("LOG_STATUS"));

        Timestamp createdAt = rs.getTimestamp("CREATED_AT");
        if (createdAt != null) {
            log.setCreatedAt(createdAt.toLocalDateTime());
        }

        return log;
    }
}
