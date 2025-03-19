package org.example.sys;

public class Person {
    private String name;
    private String surname;
    private int age;
    private String address;
    private String password;
    private String  email;

    public Person(String name, String address, String password, String email) {
        this.name = name;
        this.age = age;
        this.address = address;
        this.password = password;
        this.email = email;
    }
    public Person(){

    }

    public String getSurname() {
        return surname;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getAddress() {
        return address;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public boolean isPassword(String password){
        return this.password.equals(password);
    }
    public boolean isEmail(String email){
        return this.email.equals(email);
    }
}
