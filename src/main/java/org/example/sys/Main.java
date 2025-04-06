package org.example.sys;

public class Main {

    public static void main(String[] args) {

        Employee employee = new Employee(
                "Jan",
                "Nazwisko",
                25,
                "Kraków",
                "1234",
                "email@gmail.com",
                "123",
                "IT",
                "Developer",
                5000
        );

        System.out.println("Is password correct? " + employee.isPassword("123a"));
    }
}
