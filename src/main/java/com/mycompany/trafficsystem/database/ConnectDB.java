/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.database;

/**
 *
 * @author engineer
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {

    private static final String DEFAULT_URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String DEFAULT_USERNAME = "traffic_user";
    private static final String DEFAULT_PASSWORD = "123456";

    private static final String URL_ENV = "TRAFFIC_DB_URL";
    private static final String USERNAME_ENV = "TRAFFIC_DB_USERNAME";
    private static final String PASSWORD_ENV = "TRAFFIC_DB_PASSWORD";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Không tìm thấy Oracle JDBC Driver!");
            throw new SQLException("Không tìm thấy Oracle JDBC Driver!", e);
        }

        return DriverManager.getConnection(
                getConfigValue(URL_ENV, DEFAULT_URL),
                getConfigValue(USERNAME_ENV, DEFAULT_USERNAME),
                getConfigValue(PASSWORD_ENV, DEFAULT_PASSWORD)
        );
    }

    private static String getConfigValue(String envName, String defaultValue) {
        String value = System.getenv(envName);

        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }

        return value.trim();
    }
}
