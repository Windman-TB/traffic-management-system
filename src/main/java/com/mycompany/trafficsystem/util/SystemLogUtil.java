package com.mycompany.trafficsystem.util;

import com.mycompany.trafficsystem.database.SystemLogDatabase;
import com.mycompany.trafficsystem.model.Account;
import com.mycompany.trafficsystem.model.SystemLog;

public class SystemLogUtil {

    private static final SystemLogDatabase systemLogDatabase = new SystemLogDatabase();

    public static void logSuccess(String behaviour, String targetTable, String targetId,
                                  String oldValue, String newValue) {
        writeLog(null, behaviour, targetTable, targetId, oldValue, newValue, "SUCCESS");
    }

    public static void logFailed(String behaviour, String targetTable, String targetId,
                                 String oldValue, String newValue) {
        writeLog(null, behaviour, targetTable, targetId, oldValue, newValue, "FAILED");
    }

    public static void logSuccessByAccountId(String accountId, String behaviour, String targetTable,
                                             String targetId, String oldValue, String newValue) {
        writeLog(accountId, behaviour, targetTable, targetId, oldValue, newValue, "SUCCESS");
    }

    public static void logFailedByAccountId(String accountId, String behaviour, String targetTable,
                                            String targetId, String oldValue, String newValue) {
        writeLog(accountId, behaviour, targetTable, targetId, oldValue, newValue, "FAILED");
    }

    public static String getCurrentAccountId() {
        Account currentAccount = Session.getCurrentAccount();
        if (currentAccount == null || currentAccount.getAccountId() == null
                || currentAccount.getAccountId().trim().isEmpty()) {
            return null;
        }

        return currentAccount.getAccountId().trim();
    }

    private static void writeLog(String accountId, String behaviour, String targetTable, String targetId,
                                 String oldValue, String newValue, String status) {
        String actorAccountId = accountId;

        if (actorAccountId == null || actorAccountId.trim().isEmpty()) {
            actorAccountId = getCurrentAccountId();
            if (actorAccountId == null) {
                System.out.println("Không thể ghi log vì chưa có tài khoản đăng nhập.");
                return;
            }
        }

        SystemLog log = new SystemLog();
        log.setAccountId(actorAccountId);
        log.setBehaviour(limitText(behaviour, 100));
        log.setTargetTable(limitText(targetTable, 50));
        log.setTargetId(limitText(targetId, 20));
        log.setOldValue(limitText(oldValue, 250));
        log.setNewValue(limitText(newValue, 250));
        log.setLogStatus(status);

        systemLogDatabase.insertLog(log);
    }

    private static String limitText(String value, int maxLength) {
        if (value == null) {
            return null;
        }

        if (value.length() <= maxLength) {
            return value;
        }

        return value.substring(0, maxLength);
    }
}
