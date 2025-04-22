package org.example.sys;


import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;

public class Logistician extends Employee{
    private boolean logistician = true;
    public Logistician(String name, String surname, int age, String address, String password, String email, String department, String position, double salary) throws PasswordException, SalaryException {
        super(name, surname, age, address, password, email, department, position, salary);
    }
    public boolean isLogistician() {
        return logistician;
    }
    public void setLogistician(boolean logistician) {
        this.logistician = logistician;

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

    public void updateDepartment(Employee employee, String newDepartment) {
        employee.setDepartment(newDepartment);
    }

    public void updatePosition(Employee employee, String newPosition) {
        employee.setPosition(newPosition);
    }

    public void updateSalary(Employee employee, double newSalary) throws SalaryException {
        employee.setSalary(newSalary);
    }
}
