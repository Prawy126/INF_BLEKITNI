package org.example.sys;

import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;

import java.util.Date;
import java.util.UUID;

public class Employee extends Person {
    private String employeeId;
    private String department;
    private String position;
    private double salary;
    private boolean onSickLeave;
    private Date sickLeaveStartDate;

    public Employee(String name, String surname, int age, String address,
                    String password, String email,
                    String department,
                    String position, double salary) throws PasswordException , SalaryException {
        super(name, surname, age, address, password, email);
        this.employeeId = generateEmployeeId();
        this.department = department;
        if (salary < 0) {
            throw new SalaryException("Pensja nie może być ujemna oraz nie może być zerowa.");
        }else {
            this.salary = salary;
        }
        this.position = position;
    }

    public Employee() {
    }

    public String getEmployeeId() {
        return employeeId;
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

    public void setSalary(double salary) throws SalaryException {
        if(salary < 0) {
            throw new SalaryException("Pensja nie może być ujemna oraz nie może być zerowa.");
        }
        else{
            this.salary = salary;
        }
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

    private String generateEmployeeId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
