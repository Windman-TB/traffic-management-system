/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.database;

/**
 *
 * @author engineer
 */
import com.mycompany.trafficsystem.model.Employee;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDatabase {

    public Employee getEmployeeById(String employeeId) {
        String sql = """
            SELECT EMPLOYEE_ID,
                   FULLNAME,
                   PHONENUMBER,
                   EMAIL,
                   DATEOFBIRTH,
                   GENDER,
                   ADDRESS,
                   SALARY,
                   STATUS,
                   IS_DELETED
            FROM EMPLOYEE
            WHERE EMPLOYEE_ID = ?
              AND IS_DELETED = 0
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEmployee(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi lấy thông tin nhân viên: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();

        String sql = """
            SELECT EMPLOYEE_ID,
                   FULLNAME,
                   PHONENUMBER,
                   EMAIL,
                   DATEOFBIRTH,
                   GENDER,
                   ADDRESS,
                   SALARY,
                   STATUS,
                   IS_DELETED
            FROM EMPLOYEE
            WHERE IS_DELETED = 0
            ORDER BY EMPLOYEE_ID
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }

        } catch (SQLException e) {
            System.out.println("Lỗi lấy danh sách nhân viên: " + e.getMessage());
            e.printStackTrace();
        }

        return employees;
    }

    public List<Employee> searchEmployees(String keyword) {
        List<Employee> employees = new ArrayList<>();

        String sql = """
            SELECT EMPLOYEE_ID,
                   FULLNAME,
                   PHONENUMBER,
                   EMAIL,
                   DATEOFBIRTH,
                   GENDER,
                   ADDRESS,
                   SALARY,
                   STATUS,
                   IS_DELETED
            FROM EMPLOYEE
            WHERE IS_DELETED = 0
              AND (
                    UPPER(EMPLOYEE_ID) LIKE UPPER(?)
                 OR UPPER(FULLNAME) LIKE UPPER(?)
                 OR UPPER(PHONENUMBER) LIKE UPPER(?)
                 OR UPPER(EMAIL) LIKE UPPER(?)
                 OR UPPER(GENDER) LIKE UPPER(?)
                 OR UPPER(ADDRESS) LIKE UPPER(?)
                 OR UPPER(STATUS) LIKE UPPER(?)
                 OR TO_CHAR(DATEOFBIRTH, 'DD/MM/YYYY') LIKE ?
                 OR TO_CHAR(SALARY) LIKE ?
              )
            ORDER BY EMPLOYEE_ID
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String likeKeyword = "%" + keyword + "%";

            for (int i = 1; i <= 9; i++) {
                ps.setString(i, likeKeyword);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapResultSetToEmployee(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi tìm kiếm nhân viên: " + e.getMessage());
            e.printStackTrace();
        }

        return employees;
    }

    public String generateNextEmployeeId() {
        String sql = """
            SELECT NVL(MAX(TO_NUMBER(SUBSTR(EMPLOYEE_ID, 4))), 0) + 1 AS NEXT_ID
            FROM EMPLOYEE
            WHERE REGEXP_LIKE(EMPLOYEE_ID, '^EMP[0-9]+$')
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int nextId = rs.getInt("NEXT_ID");
                return String.format("EMP%03d", nextId);
            }

        } catch (SQLException e) {
            System.out.println("Lỗi sinh mã nhân viên: " + e.getMessage());
            e.printStackTrace();
        }

        return "EMP001";
    }

    public boolean insertEmployee(Employee employee) {
        String sql = """
            INSERT INTO EMPLOYEE (
                EMPLOYEE_ID,
                FULLNAME,
                PHONENUMBER,
                EMAIL,
                DATEOFBIRTH,
                GENDER,
                ADDRESS,
                SALARY,
                STATUS,
                IS_DELETED
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 0)
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employee.getEmployeeId());
            ps.setString(2, employee.getFullName());
            ps.setString(3, employee.getPhoneNumber());
            ps.setString(4, employee.getEmail());

            if (employee.getDateOfBirth() == null) {
                ps.setDate(5, null);
            } else {
                ps.setDate(5, Date.valueOf(employee.getDateOfBirth()));
            }

            ps.setString(6, employee.getGender());
            ps.setString(7, employee.getAddress());
            ps.setBigDecimal(8, employee.getSalary());
            ps.setString(9, employee.getStatus());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi thêm nhân viên: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateEmployee(Employee employee) {
        String sql = """
            UPDATE EMPLOYEE
            SET FULLNAME = ?,
                PHONENUMBER = ?,
                EMAIL = ?,
                DATEOFBIRTH = ?,
                GENDER = ?,
                ADDRESS = ?,
                SALARY = ?,
                STATUS = ?
            WHERE EMPLOYEE_ID = ?
              AND IS_DELETED = 0
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employee.getFullName());
            ps.setString(2, employee.getPhoneNumber());
            ps.setString(3, employee.getEmail());

            if (employee.getDateOfBirth() == null) {
                ps.setDate(4, null);
            } else {
                ps.setDate(4, Date.valueOf(employee.getDateOfBirth()));
            }

            ps.setString(5, employee.getGender());
            ps.setString(6, employee.getAddress());
            ps.setBigDecimal(7, employee.getSalary());
            ps.setString(8, employee.getStatus());
            ps.setString(9, employee.getEmployeeId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi cập nhật nhân viên: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean softDeleteEmployee(String employeeId) {
        String sql = """
            UPDATE EMPLOYEE
            SET IS_DELETED = 1
            WHERE EMPLOYEE_ID = ?
              AND IS_DELETED = 0
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi xóa nhân viên: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateEmployeeContactInfo(String employeeId, String phoneNumber, String email, String address) {
        String sql = """
            UPDATE EMPLOYEE
            SET PHONENUMBER = ?,
                EMAIL = ?,
                ADDRESS = ?
            WHERE EMPLOYEE_ID = ?
              AND IS_DELETED = 0
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, phoneNumber);
            ps.setString(2, email);
            ps.setString(3, address);
            ps.setString(4, employeeId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi cập nhật thông tin nhân viên: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public int countEmployees() {
        String sql = """
            SELECT COUNT(*) AS TOTAL
            FROM EMPLOYEE
            WHERE IS_DELETED = 0
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("TOTAL");
            }

        } catch (SQLException e) {
            System.out.println("Lỗi đếm số nhân viên: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();

        employee.setEmployeeId(rs.getString("EMPLOYEE_ID"));
        employee.setFullName(rs.getString("FULLNAME"));
        employee.setPhoneNumber(rs.getString("PHONENUMBER"));
        employee.setEmail(rs.getString("EMAIL"));

        if (rs.getDate("DATEOFBIRTH") != null) {
            employee.setDateOfBirth(rs.getDate("DATEOFBIRTH").toLocalDate());
        }

        employee.setGender(rs.getString("GENDER"));
        employee.setAddress(rs.getString("ADDRESS"));
        employee.setSalary(rs.getBigDecimal("SALARY"));
        employee.setStatus(rs.getString("STATUS"));
        employee.setIsDeleted(rs.getInt("IS_DELETED"));

        return employee;
    }
}
