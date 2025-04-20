package org.example.sys;

import java.util.Date;

public class Employee extends Person {
    private String employeeId;
    private String department;
    private String position;
    private double salary;
    private boolean onSickLeave;
    private Date sickLeaveStartDate;

    public Employee(String name, String surname, int age, String address,
                    String password, String email,
                    String employeeId, String department,
                    String position, double salary) {
        super(name, surname, age, address, password, email);
        this.employeeId = employeeId;
        this.department = department;
        this.position = position;
        this.salary = salary;
    }

    public Employee() {
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public boolean isOnSickLeave() {
        return onSickLeave;
    }

    public void startSickLeave(Date startDate) {
        this.sickLeaveStartDate = startDate;
        this.onSickLeave = true;
    }

    public Date getSickLeaveStartDate() {
        return sickLeaveStartDate;
    }
}
