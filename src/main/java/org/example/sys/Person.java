package org.example.sys;

import org.example.wyjatki.AgeException;
import org.example.wyjatki.NameException;
import org.example.wyjatki.PasswordException;

public class Person {

    private String name;
    private String surname;
    private int age;
    private String address;
    private String password;
    private String email;

    public Person() {
    }

    public Person(String name, String surname, int age, String address, String password, String email) throws PasswordException, AgeException, NameException {
        if(name == null || name.isEmpty()) {
            throw new NameException("Imię nie może być puste");
        }else if(name.length() < 2) {
            throw new NameException("Imię musi mieć co najmniej 2 znaki");
        } else {
            this.name = name;
        }
        if(surname == null || surname.isEmpty()) {
            throw new NameException("Nazwisko nie może być puste");
        }else if(surname.length() < 2) {
            throw new NameException("Nazwisko musi mieć co najmniej 2 znaki");
        } else {
            this.surname = surname;
        }
        if( age < 0 || age > 120) {
            throw new AgeException("Wiek musi być liczbą całkowitą z przedziału 0-120");
        } else if (age < 18) {
            throw new AgeException("Osoba musi mieć co najmniej 18 lat");
        } else{
            this.age = age;
        }
        this.address = address;
        if(password == null || password.length() < 8) {
            throw new PasswordException("Hasło musi mieć co najmniej 8 znaków");
        }
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

    public void setName(String name) throws NullPointerException, AgeException {
        if(name == null) {
            throw new NullPointerException("Imię nie może być puste");
        }else if(name.length() < 2) {
            throw new NameException("Imię musi mieć co najmniej 2 znaki");
        } else {
            this.name = name;
        }
    }

    public void setSurname(String surname) throws NullPointerException, AgeException{
        if(surname == null) {
            throw new NullPointerException("Nazwisko nie może być puste");
        }else if(surname.length() < 2) {
            throw new NameException("Nazwisko musi mieć co najmniej 2 znaki");
        } else {
            this.surname = surname;
        }
    }

    public void setAge(int age) throws AgeException{
        if( age < 0 || age > 120) {
            throw new AgeException("Wiek musi być liczbą całkowitą z przedziału 0-120");
        } else if (age < 18) {
            //Musimy jeszcze uzgodnić czy możemy przepuścić osoby młodsze ni ż 18 lat
            throw new AgeException("Osoba musi mieć co najmniej 18 lat");
        } else{
            this.age = age;
        }
    }

    public void setAddress(String address) throws NullPointerException{
        if(address == null) {
            throw new NullPointerException("Adres nie może być pusty");
        }
        this.address = address;
    }

    public void setPassword(String password) throws PasswordException{
        if(password.length() < 8){
            throw new PasswordException("Hasło musi mieć co najmniej 8 znaków");
        } else {
            this.password = password;
        }
    }

    public void setEmail(String email) throws NullPointerException{
        if(email == null) {
            throw new NullPointerException("Email nie może być pusty");
        }
        this.email = email;
    }

    public boolean matchesPassword(String password) {
        return this.password != null && this.password.equals(password);
    }

    public boolean matchesEmail(String email) {
        return this.email != null && this.email.equals(email);
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
