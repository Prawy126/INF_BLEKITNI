package org.example.sys;

import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.example.sys.Address;

public class Menager extends Employee {

    private List<Employee> employees = new ArrayList<>();

    public Menager(String name, String surname, int age, Address address,String login, String password,
                    String department, BigDecimal salary) throws PasswordException, SalaryException {
        super(name, surname, age, address, login, password, department, salary);
    }

    public Menager(String name, String surname, int age, Address address, String login, String password,
                   String department, BigDecimal salary,
                   List<Employee> employees) throws PasswordException, SalaryException {
        this(name, surname, age, address,login,  password, department, salary);
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

    public void updateAddress(Employee employee,Address newAddress) {
        employee.setAdres(newAddress);
    }

    public void updatePassword(Employee employee, String newPassword)throws PasswordException {
        employee.setPassword(newPassword);
    }

    public void updateDepartment(Employee employee, String newDepartment) {
        employee.setStanowisko(newDepartment);
    }

    public void updateSalary(Employee employee, BigDecimal newSalary) throws SalaryException {
        employee.setZarobki(newSalary);
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
