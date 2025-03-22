package org.example.sys;

public class Admin extends Person {
    private boolean admin = true;
    public Admin(String name, String surname, String login, String password) {
        super(name, surname, login, password);
    }
    public Admin(){

    }
    public boolean isAdmin() {
        return admin;
    }
    public void setAdmin(boolean admin) {
        this.admin = admin;
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
    public void updateDepartmentEmployee(Employee employee, String newDepartment){
        employee.setDepartment(newDepartment);
    }
    public void updatePositionEmployee(Employee employee, String newPosition){
        employee.setPosition(newPosition);
    }
    public void updateSalaryEmployee(Employee employee, double newSalary){
        employee.setSalary(newSalary);
    }
}
