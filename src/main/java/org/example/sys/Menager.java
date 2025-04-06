package org.example.sys;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Menager extends Employee {

    private List<Employee> employees = new ArrayList<>();

    public Menager(String name, String surname, int age, String address, String password,
                   String email, String employeeId, String department, String position, double salary) {
        super(name, surname, age, address, password, email, employeeId, department, position, salary);
    }

    public Menager(String name, String surname, int age, String address, String password,
                   String email, String employeeId, String department, String position, double salary,
                   List<Employee> employees) {
        this(name, surname, age, address, password, email, employeeId, department, position, salary);
        this.employees = employees != null ? employees : new ArrayList<>();
    }

    public Menager() {
        super();
    }

    public void addEmployee(Employee employee) {
        if (employee != null && !employees.contains(employee)) {
            employees.add(employee);
        }
    }

    public void removeEmployee(Employee employee) {
        employees.remove(employee);
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees != null ? employees : new ArrayList<>();
    }

    public void updateName(Employee employee, String newName) {
        employee.setName(newName);
    }

    public void updateSurname(Employee employee, String newSurname) {
        employee.setSurname(newSurname);
    }

    public void updateAge(Employee employee, int newAge) {
        employee.setAge(newAge);
    }

    public void updateAddress(Employee employee, String newAddress) {
        employee.setAddress(newAddress);
    }

    public void updatePassword(Employee employee, String newPassword) {
        employee.setPassword(newPassword);
    }

    public void updateEmail(Employee employee, String newEmail) {
        employee.setEmail(newEmail);
    }

    public void updateEmployeeId(Employee employee, String newEmployeeId) {
        employee.setEmployeeId(newEmployeeId);
    }

    public void updateDepartment(Employee employee, String newDepartment) {
        employee.setDepartment(newDepartment);
    }

    public void updatePosition(Employee employee, String newPosition) {
        employee.setPosition(newPosition);
    }

    public void updateSalary(Employee employee, double newSalary) {
        employee.setSalary(newSalary);
    }

    public String getPosition(Employee employee) {
        return employee.getPosition();
    }

    public Employee getEmployee(Employee employee) {
        for (Employee e : employees) {
            if (e.equals(employee)) {
                return e;
            }
        }
        return null;
    }
}
