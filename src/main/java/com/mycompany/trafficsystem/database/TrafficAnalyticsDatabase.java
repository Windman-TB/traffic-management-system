package com.mycompany.trafficsystem.database;

import com.mycompany.trafficsystem.model.TrafficAnalysisRow;
import com.mycompany.trafficsystem.model.TrafficAnalysisSummary;
import com.mycompany.trafficsystem.model.TrafficMonitoringRow;
import com.mycompany.trafficsystem.model.Traffic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TrafficAnalyticsDatabase {

    public List<TrafficMonitoringRow> getCurrentTraffic(String areaId,
                                                        String streetId,
                                                        String status,
                                                        Double minVelocity,
                                                        Double maxVelocity,
                                                        String keyword) {
        List<TrafficMonitoringRow> rows = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            WITH latest_traffic AS (
                SELECT t.STATUS_ID,
                       t.SEGMENT_ID,
                       t.VELOCITY,
                       t.RECORDED_AT,
                       ROW_NUMBER() OVER (
                           PARTITION BY t.SEGMENT_ID
                           ORDER BY t.RECORDED_AT DESC, TO_NUMBER(t.STATUS_ID) DESC
                       ) AS RN
                FROM TRAFFIC t
            )
            SELECT s.SEGMENT_ID,
                   s.STREET_ID,
                   st.STREET_NAME,
                   st.STREET_TYPE,
                   st.ROAD_LEVEL,
                   s.AREA_ID,
                   a.AREA_NAME,
                   s.START_NODE_ID,
                   s.END_NODE_ID,
                   sn.LATITUDE AS START_LATITUDE,
                   sn.LONGITUDE AS START_LONGITUDE,
                   en.LATITUDE AS END_LATITUDE,
                   en.LONGITUDE AS END_LONGITUDE,
                   s.SEGMENT_LENGTH,
                   s.MAX_VELOCITY,
                   lt.VELOCITY,
                   lt.RECORDED_AT
            FROM latest_traffic lt
            JOIN SEGMENT s ON lt.SEGMENT_ID = s.SEGMENT_ID
            LEFT JOIN STREET st ON s.STREET_ID = st.STREET_ID AND st.IS_DELETED = 0
            LEFT JOIN AREA a ON s.AREA_ID = a.AREA_ID AND a.IS_DELETED = 0
            LEFT JOIN NODE sn ON s.START_NODE_ID = sn.NODE_ID AND sn.IS_DELETED = 0
            LEFT JOIN NODE en ON s.END_NODE_ID = en.NODE_ID AND en.IS_DELETED = 0
            WHERE lt.RN = 1
              AND s.IS_DELETED = 0
        """);

        appendCurrentFilters(sql, params, areaId, streetId, minVelocity, maxVelocity, keyword);
        sql.append(" ORDER BY lt.RECORDED_AT DESC, TO_NUMBER(s.SEGMENT_ID) ");

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            setParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TrafficMonitoringRow row = mapMonitoringRow(rs);

                    if (status == null || status.trim().isEmpty() || status.equals("Tất cả")
                            || row.getStatus().equals(status.trim())) {
                        rows.add(row);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi lấy dữ liệu giám sát giao thông: " + e.getMessage());
            e.printStackTrace();
        }

        return rows;
    }

    public List<Traffic> getSegmentHistory(String segmentId, LocalDate fromDate, LocalDate toDate) {
        List<Traffic> history = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT t.STATUS_ID,
                   t.SEGMENT_ID,
                   t.VELOCITY,
                   t.RECORDED_AT
            FROM TRAFFIC t
            WHERE t.SEGMENT_ID = ?
        """);
        params.add(segmentId);

        appendDateFilters(sql, params, fromDate, toDate);
        sql.append(" ORDER BY t.RECORDED_AT ");

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            setParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Traffic traffic = new Traffic();
                    traffic.setStatusId(rs.getString("STATUS_ID"));
                    traffic.setSegmentId(rs.getString("SEGMENT_ID"));
                    traffic.setVelocity(rs.getDouble("VELOCITY"));

                    Timestamp recordedAt = rs.getTimestamp("RECORDED_AT");
                    if (recordedAt != null) {
                        traffic.setRecordedAt(recordedAt.toLocalDateTime());
                    }

                    history.add(traffic);
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi lấy lịch sử tốc độ: " + e.getMessage());
            e.printStackTrace();
        }

        return history;
    }

    public TrafficAnalysisSummary getSummary(LocalDate fromDate, LocalDate toDate) {
        TrafficAnalysisSummary summary = new TrafficAnalysisSummary();
        List<Object> params = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*) AS TOTAL_RECORDS,
                   COUNT(DISTINCT t.SEGMENT_ID) AS TOTAL_SEGMENTS,
                   AVG(t.VELOCITY) AS AVG_VELOCITY,
                   MIN(t.VELOCITY) AS MIN_VELOCITY,
                   MAX(t.VELOCITY) AS MAX_VELOCITY,
                   SUM(CASE
                       WHEN s.MAX_VELOCITY IS NOT NULL AND s.MAX_VELOCITY > 0 AND t.VELOCITY / s.MAX_VELOCITY < 0.4 THEN 1
                       WHEN (s.MAX_VELOCITY IS NULL OR s.MAX_VELOCITY <= 0) AND t.VELOCITY < 20 THEN 1
                       ELSE 0
                   END) AS CONGESTION_RECORDS
            FROM TRAFFIC t
            LEFT JOIN SEGMENT s ON t.SEGMENT_ID = s.SEGMENT_ID AND s.IS_DELETED = 0
            WHERE 1 = 1
        """);

        appendDateFilters(sql, params, fromDate, toDate);

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            setParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    summary.setTotalRecords(rs.getInt("TOTAL_RECORDS"));
                    summary.setTotalSegments(rs.getInt("TOTAL_SEGMENTS"));
                    summary.setAverageVelocity(getNullableDouble(rs, "AVG_VELOCITY"));
                    summary.setMinVelocity(getNullableDouble(rs, "MIN_VELOCITY"));
                    summary.setMaxVelocity(getNullableDouble(rs, "MAX_VELOCITY"));
                    summary.setCongestionRecords(rs.getInt("CONGESTION_RECORDS"));
                    summary.setCongestionRate(summary.getTotalRecords() == 0
                            ? 0.0
                            : summary.getCongestionRecords() * 100.0 / summary.getTotalRecords());
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi thống kê tổng quan lưu lượng: " + e.getMessage());
            e.printStackTrace();
        }

        return summary;
    }

    public List<TrafficAnalysisRow> analyze(String mode, LocalDate fromDate, LocalDate toDate) {
        String groupExpression = getGroupExpression(mode);
        String groupAlias = "GROUP_NAME";
        List<Object> params = new ArrayList<>();
        List<TrafficAnalysisRow> rows = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT %s AS %s,
                   COUNT(*) AS RECORD_COUNT,
                   COUNT(DISTINCT t.SEGMENT_ID) AS SEGMENT_COUNT,
                   AVG(t.VELOCITY) AS AVG_VELOCITY,
                   MIN(t.VELOCITY) AS MIN_VELOCITY,
                   MAX(t.VELOCITY) AS MAX_VELOCITY,
                   SUM(CASE
                       WHEN s.MAX_VELOCITY IS NOT NULL AND s.MAX_VELOCITY > 0 AND t.VELOCITY / s.MAX_VELOCITY < 0.4 THEN 1
                       WHEN (s.MAX_VELOCITY IS NULL OR s.MAX_VELOCITY <= 0) AND t.VELOCITY < 20 THEN 1
                       ELSE 0
                   END) AS CONGESTION_COUNT,
                   AVG(CASE
                       WHEN s.MAX_VELOCITY IS NOT NULL AND s.MAX_VELOCITY > 0 THEN t.VELOCITY / s.MAX_VELOCITY
                       ELSE NULL
                   END) AS AVG_RATIO
            FROM TRAFFIC t
            LEFT JOIN SEGMENT s ON t.SEGMENT_ID = s.SEGMENT_ID AND s.IS_DELETED = 0
            LEFT JOIN STREET st ON s.STREET_ID = st.STREET_ID AND st.IS_DELETED = 0
            LEFT JOIN AREA a ON s.AREA_ID = a.AREA_ID AND a.IS_DELETED = 0
            WHERE 1 = 1
        """.formatted(groupExpression, groupAlias));

        appendDateFilters(sql, params, fromDate, toDate);
        sql.append(" GROUP BY ").append(groupExpression);
        sql.append(" ORDER BY CONGESTION_COUNT DESC, AVG_VELOCITY ASC ");

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            setParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TrafficAnalysisRow row = new TrafficAnalysisRow();
                    row.setGroupName(rs.getString(groupAlias));
                    row.setRecordCount(rs.getInt("RECORD_COUNT"));
                    row.setSegmentCount(rs.getInt("SEGMENT_COUNT"));
                    row.setAverageVelocity(getNullableDouble(rs, "AVG_VELOCITY"));
                    row.setMinVelocity(getNullableDouble(rs, "MIN_VELOCITY"));
                    row.setMaxVelocity(getNullableDouble(rs, "MAX_VELOCITY"));
                    row.setCongestionCount(rs.getInt("CONGESTION_COUNT"));
                    row.setCongestionRate(row.getRecordCount() == 0
                            ? 0.0
                            : row.getCongestionCount() * 100.0 / row.getRecordCount());
                    row.setAverageVelocityRatio(getNullableDouble(rs, "AVG_RATIO"));
                    rows.add(row);
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi phân tích dữ liệu lưu lượng: " + e.getMessage());
            e.printStackTrace();
        }

        return rows;
    }

    private String getGroupExpression(String mode) {
        if ("Theo khu vực".equals(mode)) {
            return "NVL(a.AREA_NAME, s.AREA_ID)";
        }
        if ("Theo tuyến đường".equals(mode)) {
            return "NVL(st.STREET_NAME, s.STREET_ID)";
        }
        if ("Theo loại đường".equals(mode)) {
            return "NVL(st.STREET_TYPE, 'Không xác định')";
        }
        if ("Theo cấp đường".equals(mode)) {
            return "'Cấp ' || TO_CHAR(st.ROAD_LEVEL)";
        }
        if ("Theo ngày".equals(mode)) {
            return "TO_CHAR(t.RECORDED_AT, 'YYYY-MM-DD')";
        }
        return "TO_CHAR(t.RECORDED_AT, 'HH24') || ':00'";
    }

    private void appendCurrentFilters(StringBuilder sql, List<Object> params,
                                      String areaId, String streetId,
                                      Double minVelocity, Double maxVelocity,
                                      String keyword) {
        if (areaId != null && !areaId.trim().isEmpty() && !areaId.equals("Tất cả")) {
            sql.append(" AND s.AREA_ID = ? ");
            params.add(areaId.trim());
        }

        if (streetId != null && !streetId.trim().isEmpty() && !streetId.equals("Tất cả")) {
            sql.append(" AND s.STREET_ID = ? ");
            params.add(streetId.trim());
        }

        if (minVelocity != null) {
            sql.append(" AND lt.VELOCITY >= ? ");
            params.add(minVelocity);
        }

        if (maxVelocity != null) {
            sql.append(" AND lt.VELOCITY <= ? ");
            params.add(maxVelocity);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("""
                AND (
                    LOWER(s.SEGMENT_ID) LIKE ?
                    OR LOWER(NVL(st.STREET_NAME, '')) LIKE ?
                    OR LOWER(NVL(a.AREA_NAME, '')) LIKE ?
                )
            """);
            String searchValue = "%" + keyword.trim().toLowerCase() + "%";
            params.add(searchValue);
            params.add(searchValue);
            params.add(searchValue);
        }
    }

    private void appendDateFilters(StringBuilder sql, List<Object> params, LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null) {
            sql.append(" AND t.RECORDED_AT >= ? ");
            params.add(Timestamp.valueOf(fromDate.atStartOfDay()));
        }

        if (toDate != null) {
            sql.append(" AND t.RECORDED_AT < ? ");
            params.add(Timestamp.valueOf(toDate.plusDays(1).atStartOfDay()));
        }
    }

    private TrafficMonitoringRow mapMonitoringRow(ResultSet rs) throws SQLException {
        TrafficMonitoringRow row = new TrafficMonitoringRow();
        row.setSegmentId(rs.getString("SEGMENT_ID"));
        row.setStreetId(rs.getString("STREET_ID"));
        row.setStreetName(rs.getString("STREET_NAME"));
        row.setStreetType(rs.getString("STREET_TYPE"));
        row.setRoadLevel(getNullableInteger(rs, "ROAD_LEVEL"));
        row.setAreaId(rs.getString("AREA_ID"));
        row.setAreaName(rs.getString("AREA_NAME"));
        row.setStartNodeId(rs.getString("START_NODE_ID"));
        row.setEndNodeId(rs.getString("END_NODE_ID"));
        row.setStartLatitude(getNullableDouble(rs, "START_LATITUDE"));
        row.setStartLongitude(getNullableDouble(rs, "START_LONGITUDE"));
        row.setEndLatitude(getNullableDouble(rs, "END_LATITUDE"));
        row.setEndLongitude(getNullableDouble(rs, "END_LONGITUDE"));
        row.setSegmentLength(getNullableDouble(rs, "SEGMENT_LENGTH"));
        row.setMaxVelocity(getNullableInteger(rs, "MAX_VELOCITY"));
        row.setVelocity(getNullableDouble(rs, "VELOCITY"));

        Timestamp recordedAt = rs.getTimestamp("RECORDED_AT");
        if (recordedAt != null) {
            row.setRecordedAt(recordedAt.toLocalDateTime());
        }

        return row;
    }

    private Double getNullableDouble(ResultSet rs, String columnName) throws SQLException {
        double value = rs.getDouble(columnName);
        return rs.wasNull() ? null : value;
    }

    private Integer getNullableInteger(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? null : value;
    }

    private void setParams(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }
}
