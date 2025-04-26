package org.example.sys;

import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;
import org.example.sys.Address;
import java.math.BigDecimal;

public class Admin extends Person {

    public Admin(String name, String surname, int age, String address, String password) throws PasswordException {
        super(name, surname, age, address, password);
    }

    public Admin(String name, String surname, int age, String password) throws PasswordException {
        super(name, surname, age, null, password);
    }

    public Admin() {
        super();
    }

    public boolean isAdmin() {
        return true;
    }

    public void updateName(Employee employee, String newName) {
        employee.setImie(newName);
    }

    public void updateSurname(Employee employee, String newSurname) {
        employee.setNazwisko(newSurname);
    }

    public void updateAge(Employee employee, int newAge) {
        employee.setWiek(newAge);
    }

    public void updateAddress(Employee employee, Address address) {  // zmieniono typ parametru na String
        employee.setAdres(address);
    }
    public void updatePassword(Employee employee, String newPassword)throws PasswordException {
        employee.setHaslo(newPassword);
    }

    public void updateDepartment(Employee employee, String newDepartment) {
        employee.setStanowisko(newDepartment);
    }

    public void updateSalary(Employee employee, BigDecimal newSalary)throws SalaryException {
        employee.setZarobki(newSalary);

    }
}
