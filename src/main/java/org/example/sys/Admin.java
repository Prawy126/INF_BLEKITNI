package org.example.sys;

import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;

import java.math.BigDecimal;

/**
 * Klasa Admin rozszerzająca Employee, reprezentuje użytkownika z uprawnieniami administratora.
 */
public class Admin extends Employee {

    public Admin(String name, String surname, int age, String email,
                 String login, String password, Address adres,
                 String stanowisko, BigDecimal zarobki)
            throws Exception {
        super(name, surname, age, email, login, password, adres, stanowisko, zarobki);
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

    public void updateAddress(Employee employee, Address address) {
        employee.setAdres(address);
    }

    public void updatePassword(Employee employee, String newPassword)
            throws PasswordException {
        employee.setPassword(newPassword);
    }

    public void updateDepartment(Employee employee, String newDepartment) {
        employee.setStanowisko(newDepartment);
    }

    public void updateSalary(Employee employee, BigDecimal newSalary)
            throws SalaryException {
        employee.setZarobki(newSalary);
    }
}
