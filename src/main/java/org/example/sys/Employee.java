package org.example.sys;

import java.util.Date;

public class Employee extends Person{
    private String employeeId;
    private String department;
    private String position;
    private double salary;
    private boolean l4 = false;
    private Date date = null;

    public Employee(String name,String lastName, int age, String address, String password, String email, String employeeId, String department, String position, double salary) {
        super(name,lastName, age,address,password,email);
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

    public String getDepartment() {
        return department;
    }

    public String getPosition() {
        return position;
    }

    public double getSalary() {
        return salary;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public boolean isL4() {
        return l4;
    }

    public void setDate(Date date) {
        this.date = date;
        this.l4 = true;
    }

}
