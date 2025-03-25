package org.example.sys;

import java.util.Date;
import java.util.List;

public class Menager extends Employee{
    private String department;
    private String position;
    private double salary;
    private boolean l4 = false;
    private Date date = null;
    List<Employee> employees;

    public Menager(String name, int age, String address, String password, String email, String employeeId, String department, String position, double salary) {
        super(name, age, address, password, email, employeeId, department, position, salary);
    }

    public Menager(String name, int age, String address, String password, String email, String employeeId, String department, String position, double salary, List<Employee> employees) {
        super(name, age, address, password, email, employeeId, department, position, salary);
        this.employees = employees;
    }

    public Menager() {
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
    }
    public Date getDate() {
        return date;
    }
    public void setL4(boolean l4) {
        this.l4 = l4;
    }
    public void updateName(Employee employee, String newName){
        employee.setName(newName);
    }
    public void updateSurname(Employee employee, String newSurname){
        employee.setSurname(newSurname);
    }
    public void updateAge(Employee employee, int newAge){
        employee.setAge(newAge);
    }
    public void updateAddress(Employee employee, String newAddress){
        employee.setAddress(newAddress);
    }
    public void updatePassword(Employee employee, String newPassword){
        employee.setPassword(newPassword);
    }
    public void updateEmail(Employee employee, String newEmail){
        employee.setEmail(newEmail);
    }
    public void updateEmployeeId(Employee employee, String newEmployeeId){
        employee.setEmployeeId(newEmployeeId);
    }
    public void updateDepartment(Employee employee, String newDepartment){
        employee.setDepartment(newDepartment);
    }
    public void updatePosition(Employee employee, String newPosition){
        employee.setPosition(newPosition);
    }
    public void updateSalary(Employee employee, double newSalary){
        employee.setSalary(newSalary);
    }
    public void updateNameEmployee(Employee employee, String newName){
        employee.setName(newName);
    }
    public void updateSurnameEmployee(Employee employee, String newSurname){
        employee.setSurname(newSurname);
    }
    public void updateAgeEmployee(Employee employee, int newAge){
        employee.setAge(newAge);
    }
    public void updateAddressEmployee(Employee employee, String newAddress){
        employee.setAddress(newAddress);
    }
    public void updatePasswordEmployee(Employee employee, String newPassword){
        employee.setPassword(newPassword);
    }
    public void updateEmailEmployee(Employee employee, String newEmail){
        employee.setEmail(newEmail);
    }
    public void updateEmployeeIdEmployee(Employee employee, String newEmployeeId){
        employee.setEmployeeId(newEmployeeId);
    }
    public String getPosition(Employee employee){
        return employee.getPosition();
    }

    public void addEmployee(Employee employee){
        employees.add(employee);
    }

    public void removeEmployee(Employee employee){
        employees.remove(employee);
    }

    public List<Employee> getEmployees(){
        return employees;
    }

    public void updateEmlpoyess(Employee employee){
        getEmployee(employee).setName(employee.getName());
    }

    public void setEmployees(List<Employee> employees){
        this.employees = employees;
    }

    public void updateDepartmentEmployee(Employee employee, String newDepartment){
        employee.setDepartment(newDepartment);
    }
    public Employee getEmployee(Employee employee){
        for (Employee e : employees){
            if (e.equals(employee)){
                return e;
            }
        }
        return null;
    }
}
