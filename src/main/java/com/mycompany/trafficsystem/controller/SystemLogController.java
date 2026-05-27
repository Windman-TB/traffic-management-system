package com.mycompany.trafficsystem.controller;

import com.mycompany.trafficsystem.database.SystemLogDatabase;
import com.mycompany.trafficsystem.model.SystemLog;
import com.mycompany.trafficsystem.model.SystemLogSummary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SystemLogController {

    private final SystemLogDatabase systemLogDatabase;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public SystemLogController() {
        this.systemLogDatabase = new SystemLogDatabase();
    }

    public List<SystemLog> getAllLogs() {
        return systemLogDatabase.getAllLogs();
    }

    public SystemLog getLogById(String logId) {
        if (logId == null || logId.trim().isEmpty()) {
            return null;
        }

        return systemLogDatabase.getLogById(logId.trim());
    }

    public List<SystemLog> searchLogs(String keyword,
                                      String accountId,
                                      String targetTable,
                                      String behaviour,
                                      String status,
                                      LocalDate fromDate,
                                      LocalDate toDate) {
        return systemLogDatabase.searchLogs(keyword, accountId, targetTable, behaviour, status, fromDate, toDate);
    }

    public SystemLogSummary getSummary() {
        return systemLogDatabase.getSummary();
    }

    public boolean exportLogsToCsv(List<SystemLog> logs, File file) {
        if (logs == null || file == null) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            writer.write('\uFEFF');
            writer.write("LOG_ID,ACCOUNT_ID,BEHAVIOUR,TARGET_TABLE,TARGET_ID,OLD_VALUE,NEW_VALUE,LOG_STATUS,CREATED_AT");
            writer.newLine();

            for (SystemLog log : logs) {
                writer.write(toCsv(log.getLogId()));
                writer.write(",");
                writer.write(toCsv(log.getAccountId()));
                writer.write(",");
                writer.write(toCsv(log.getBehaviour()));
                writer.write(",");
                writer.write(toCsv(log.getTargetTable()));
                writer.write(",");
                writer.write(toCsv(log.getTargetId()));
                writer.write(",");
                writer.write(toCsv(log.getOldValue()));
                writer.write(",");
                writer.write(toCsv(log.getNewValue()));
                writer.write(",");
                writer.write(toCsv(log.getLogStatus()));
                writer.write(",");
                writer.write(toCsv(formatDateTime(log)));
                writer.newLine();
            }

            return true;

        } catch (IOException e) {
            System.out.println("Lỗi xuất file nhật ký hệ thống: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public String formatDateTime(SystemLog log) {
        if (log == null || log.getCreatedAt() == null) {
            return "";
        }

        return log.getCreatedAt().format(DATE_TIME_FORMATTER);
    }

    private String toCsv(String value) {
        if (value == null) {
            return "";
        }

        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}
