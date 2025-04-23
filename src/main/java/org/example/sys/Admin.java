package org.example.sys;

import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;

public class Admin extends Person {

    public Admin(String name, String surname, int age, String address, String login, String password) throws PasswordException {
        super(name, surname, age, address, password, login);
    }

    public Admin(String name, String surname, int age, String login, String password) throws PasswordException {
        super(name, surname, age, null, password, login);
    }

    public Admin() {
        super();
    }

    public boolean isAdmin() {
        return true;
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

    public void updatePassword(Employee employee, String newPassword)throws PasswordException {
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

    public void updateSalary(Employee employee, double newSalary)throws SalaryException {
        employee.setSalary(newSalary);

    }
}
