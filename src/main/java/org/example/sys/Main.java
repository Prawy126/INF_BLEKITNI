package org.example.sys;

// Jakub

public class Main {

    public static void main(String[] args) {
        Employee employee = new Employee("Jan", 25, "Kraków", "1234", "email@gamil.com,", "123", "IT", "Developer", 5000);
        System.out.println(employee.isPassword("123a"));
    }

}
