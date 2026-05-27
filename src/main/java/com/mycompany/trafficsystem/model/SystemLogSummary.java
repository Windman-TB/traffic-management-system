package com.mycompany.trafficsystem.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class SystemLogSummary {

    private int totalLogs;
    private int successLogs;
    private int failedLogs;
    private int todayLogs;
    private Map<String, Integer> logsByTable;
    private Map<String, Integer> logsByBehaviour;

    public SystemLogSummary() {
        this.logsByTable = new LinkedHashMap<>();
        this.logsByBehaviour = new LinkedHashMap<>();
    }

    public int getTotalLogs() {
        return totalLogs;
    }

    public void setTotalLogs(int totalLogs) {
        this.totalLogs = totalLogs;
    }

    public int getSuccessLogs() {
        return successLogs;
    }

    public void setSuccessLogs(int successLogs) {
        this.successLogs = successLogs;
    }

    public int getFailedLogs() {
        return failedLogs;
    }

    public void setFailedLogs(int failedLogs) {
        this.failedLogs = failedLogs;
    }

    public int getTodayLogs() {
        return todayLogs;
    }

    public void setTodayLogs(int todayLogs) {
        this.todayLogs = todayLogs;
    }

    public Map<String, Integer> getLogsByTable() {
        return logsByTable;
    }

    public void setLogsByTable(Map<String, Integer> logsByTable) {
        this.logsByTable = logsByTable;
    }

    public Map<String, Integer> getLogsByBehaviour() {
        return logsByBehaviour;
    }

    public void setLogsByBehaviour(Map<String, Integer> logsByBehaviour) {
        this.logsByBehaviour = logsByBehaviour;
    }
}
