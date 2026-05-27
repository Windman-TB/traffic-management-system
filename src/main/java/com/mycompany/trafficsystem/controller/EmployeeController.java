package com.mycompany.trafficsystem.controller;

import com.mycompany.trafficsystem.database.EmployeeDatabase;
import com.mycompany.trafficsystem.model.Employee;
import com.mycompany.trafficsystem.util.SystemLogUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class EmployeeController {

    private static final String GMAIL_REGEX = "^[A-Za-z0-9+_.-]+@gmail\\.com$";
    private static final String VIETNAM_PHONE_REGEX = "^(0\\d{9}|\\+84\\d{9})$";

    private final EmployeeDatabase employeeDatabase;

    public EmployeeController() {
        this.employeeDatabase = new EmployeeDatabase();
    }

    public List<Employee> getAllEmployees() {
        return employeeDatabase.getAllEmployees();
    }

    public List<Employee> searchEmployees(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllEmployees();
        }

        return employeeDatabase.searchEmployees(keyword.trim());
    }

    public String generateNextEmployeeId() {
        return employeeDatabase.generateNextEmployeeId();
    }

    public boolean addEmployee(String fullName, String phoneNumber, String email,
                               LocalDate dateOfBirth, String gender, String address,
                               BigDecimal salary, String status) {
        if (!isValidEmployeeInput(fullName, phoneNumber, email, salary, status)) {
            return false;
        }

        Employee employee = new Employee();
        employee.setEmployeeId(generateNextEmployeeId());
        employee.setFullName(fullName.trim());
        employee.setPhoneNumber(phoneNumber.trim());
        employee.setEmail(email.trim());
        employee.setDateOfBirth(dateOfBirth);
        employee.setGender(normalizeNullableText(gender));
        employee.setAddress(normalizeNullableText(address));
        employee.setSalary(salary);
        employee.setStatus(status.trim());

        boolean success = employeeDatabase.insertEmployee(employee);

        if (success) {
            SystemLogUtil.logSuccess( "Thêm nhân viên", "EMPLOYEE", employee.getEmployeeId(), null, employeeToLogValue(employee));
        } else {
            SystemLogUtil.logFailed( "Thêm nhân viên", "EMPLOYEE", employee.getEmployeeId(), null, employeeToLogValue(employee));
        }

        return success;
    }

    public boolean updateEmployee(String employeeId, String fullName, String phoneNumber, String email,
                                  LocalDate dateOfBirth, String gender, String address,
                                  BigDecimal salary, String status) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            return false;
        }

        if (!isValidEmployeeInput(fullName, phoneNumber, email, salary, status)) {
            return false;
        }

        String cleanEmployeeId = employeeId.trim();
        Employee oldEmployee = employeeDatabase.getEmployeeById(cleanEmployeeId);

        Employee employee = new Employee();
        employee.setEmployeeId(cleanEmployeeId);
        employee.setFullName(fullName.trim());
        employee.setPhoneNumber(phoneNumber.trim());
        employee.setEmail(email.trim());
        employee.setDateOfBirth(dateOfBirth);
        employee.setGender(normalizeNullableText(gender));
        employee.setAddress(normalizeNullableText(address));
        employee.setSalary(salary);
        employee.setStatus(status.trim());

        boolean success = employeeDatabase.updateEmployee(employee);


        if (success) {
            SystemLogUtil.logSuccess( "Cập nhật nhân viên", "EMPLOYEE", cleanEmployeeId, employeeToLogValue(oldEmployee), employeeToLogValue(employee));
        } else {
            SystemLogUtil.logFailed( "Cập nhật nhân viên", "EMPLOYEE", cleanEmployeeId, employeeToLogValue(oldEmployee), employeeToLogValue(employee));
        }

        return success;
    }

    public boolean deleteEmployee(String employeeId) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            return false;
        }

        String cleanEmployeeId = employeeId.trim();
        Employee oldEmployee = employeeDatabase.getEmployeeById(cleanEmployeeId);
        boolean success = employeeDatabase.softDeleteEmployee(cleanEmployeeId);


        if (success) {
            SystemLogUtil.logSuccess( "Xóa nhân viên", "EMPLOYEE", cleanEmployeeId, employeeToLogValue(oldEmployee), "IS_DELETED=1");
        } else {
            SystemLogUtil.logFailed( "Xóa nhân viên", "EMPLOYEE", cleanEmployeeId, employeeToLogValue(oldEmployee), "Xóa thất bại");
        }

        return success;
    }

    private boolean isValidEmployeeInput(String fullName, String phoneNumber, String email,
                                         BigDecimal salary, String status) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return false;
        }

        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }

        if (!phoneNumber.trim().matches(VIETNAM_PHONE_REGEX)) {
            return false;
        }

        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        if (!email.trim().matches(GMAIL_REGEX)) {
            return false;
        }

        if (salary != null && salary.compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }

        return status != null && !status.trim().isEmpty();
    }

    private String normalizeNullableText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }

    private String employeeToLogValue(Employee employee) {
        if (employee == null) {
            return null;
        }

        return "EMPLOYEE_ID=" + employee.getEmployeeId()
                + ", FULLNAME=" + employee.getFullName()
                + ", PHONENUMBER=" + employee.getPhoneNumber()
                + ", EMAIL=" + employee.getEmail()
                + ", DATEOFBIRTH=" + employee.getDateOfBirth()
                + ", GENDER=" + employee.getGender()
                + ", ADDRESS=" + employee.getAddress()
                + ", SALARY=" + employee.getSalary()
                + ", STATUS=" + employee.getStatus();
    }
}
