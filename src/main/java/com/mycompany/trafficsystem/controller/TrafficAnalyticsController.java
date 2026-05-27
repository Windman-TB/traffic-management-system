package com.mycompany.trafficsystem.controller;

import com.mycompany.trafficsystem.database.TrafficAnalyticsDatabase;
import com.mycompany.trafficsystem.model.Traffic;
import com.mycompany.trafficsystem.model.TrafficAnalysisRow;
import com.mycompany.trafficsystem.model.TrafficAnalysisSummary;
import com.mycompany.trafficsystem.model.TrafficMonitoringRow;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TrafficAnalyticsController {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final TrafficAnalyticsDatabase trafficAnalyticsDatabase = new TrafficAnalyticsDatabase();

    public List<TrafficMonitoringRow> getCurrentTraffic(String areaId,
                                                        String streetId,
                                                        String status,
                                                        Double minVelocity,
                                                        Double maxVelocity,
                                                        String keyword) {
        return trafficAnalyticsDatabase.getCurrentTraffic(areaId, streetId, status, minVelocity, maxVelocity, keyword);
    }

    public List<Traffic> getSegmentHistory(String segmentId, LocalDate fromDate, LocalDate toDate) {
        return trafficAnalyticsDatabase.getSegmentHistory(segmentId, fromDate, toDate);
    }

    public TrafficAnalysisSummary getSummary(LocalDate fromDate, LocalDate toDate) {
        return trafficAnalyticsDatabase.getSummary(fromDate, toDate);
    }

    public List<TrafficAnalysisRow> analyze(String mode, LocalDate fromDate, LocalDate toDate) {
        return trafficAnalyticsDatabase.analyze(mode, fromDate, toDate);
    }

    public String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? "" : dateTime.format(DATE_TIME_FORMATTER);
    }

    public String formatNumber(Double value) {
        return value == null ? "" : String.format("%.2f", value);
    }

    public String formatPercent(Double value) {
        return value == null ? "" : String.format("%.1f%%", value);
    }

    public boolean exportMonitoringCsv(File file, List<TrafficMonitoringRow> rows) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("SEGMENT_ID,STREET_NAME,AREA_NAME,VELOCITY,MAX_VELOCITY,RATIO,STATUS,CREATED_AT");
            writer.newLine();

            for (TrafficMonitoringRow row : rows) {
                writer.write(csv(row.getSegmentId()) + ","
                        + csv(row.getStreetName()) + ","
                        + csv(row.getAreaName()) + ","
                        + csv(formatNumber(row.getVelocity())) + ","
                        + csv(row.getMaxVelocity() == null ? "" : row.getMaxVelocity().toString()) + ","
                        + csv(formatNumber(row.getVelocityRatio())) + ","
                        + csv(row.getStatus()) + ","
                        + csv(formatDateTime(row.getCreatedAt())));
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.println("Lỗi xuất CSV giám sát giao thông: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean exportAnalysisCsv(File file, List<TrafficAnalysisRow> rows) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("GROUP_NAME,RECORD_COUNT,SEGMENT_COUNT,AVG_VELOCITY,MIN_VELOCITY,MAX_VELOCITY,CONGESTION_COUNT,CONGESTION_RATE,AVG_VELOCITY_RATIO");
            writer.newLine();

            for (TrafficAnalysisRow row : rows) {
                writer.write(csv(row.getGroupName()) + ","
                        + row.getRecordCount() + ","
                        + row.getSegmentCount() + ","
                        + csv(formatNumber(row.getAverageVelocity())) + ","
                        + csv(formatNumber(row.getMinVelocity())) + ","
                        + csv(formatNumber(row.getMaxVelocity())) + ","
                        + row.getCongestionCount() + ","
                        + csv(formatPercent(row.getCongestionRate())) + ","
                        + csv(formatNumber(row.getAverageVelocityRatio())));
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.println("Lỗi xuất CSV phân tích dữ liệu: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String csv(String value) {
        if (value == null) {
            return "";
        }

        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
