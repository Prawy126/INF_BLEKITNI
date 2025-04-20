package org.example.sys;

public class Person {

    private String name;
    private String surname;
    private int age;
    private String address;
    private String password;
    private String email;

    public Person() {
    }

    public Person(String name, String surname, int age, String address, String password, String email) {
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.address = address;
        this.password = password;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
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

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean matchesPassword(String password) {
        return this.password != null && this.password.equals(password);
    }

    public boolean matchesEmail(String email) {
        return this.email != null && this.email.equals(email);
    }

    public boolean isPassword(String password) {
        return this.password != null && this.password.equals(password);
    }

    /**
     * Metoda toString została nadpisana na potrzeby tej klasy
     * <p>Format:</p>
     * <p>imię, nazwisko, (wiek), adres, email</p>*/
    @Override
    public String toString() {
        return String.format("%s %s (%d), %s, %s", name, surname, age, address, email);
    }
}
