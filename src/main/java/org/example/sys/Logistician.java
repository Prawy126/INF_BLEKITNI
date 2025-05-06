package org.example.sys;


import org.example.wyjatki.AgeException;
import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;
import org.example.sys.Address;

import java.math.BigDecimal;

public class Logistician extends Employee{
    private boolean logistician = true;
    public Logistician(String name, String surname, int age, String login,Address address, String password, String department, BigDecimal salary) throws PasswordException, SalaryException, AgeException {
        super(name, surname, age,address, login, password, department,salary);
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

    public void updateAddress(Employee employee, Address newAddress) {
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
}
