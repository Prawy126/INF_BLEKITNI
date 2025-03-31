package org.example.sys;

import java.time.LocalDate;

public class Registration {
    String message;
    Person person;
    LocalDate date;

    public Registration(String message, Person person, LocalDate date) {
        this.message = message;
        this.person = person;
        this.date = date;
    }

    public Registration(String message, LocalDate date) {
        this.message = message;
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public Person getPerson() {
        return person;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

}
