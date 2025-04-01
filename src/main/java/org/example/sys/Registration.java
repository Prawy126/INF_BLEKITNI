package org.example.sys;

import java.time.LocalDate;

public class Registration {
    String message;
    String name;
    String surname;
    String title;
    LocalDate date;
    StatusRegistration status = StatusRegistration.OCZEKUJACY;

    public Registration(String message, String name, String surname, LocalDate date, String title) {
        this.message = message;
        this.title = title;
        this.name = name;
        this.surname = surname;
        this.date = date;
    }

    public Registration(String message, String name, String surname, LocalDate date, String title, StatusRegistration status) {
        this.message = message;
        this.title = title;
        this.name = name;
        this.surname = surname;
        this.date = date;
        this.status = status;
    }

    public Registration(String message, LocalDate date) {
        this.message = message;
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public StatusRegistration getStatus() {
        return status;
    }

    public void setStatusAccept(){
        this.status = StatusRegistration.ZAAKCEPTOWANY;
    }

    public void setStatusReject(){
        this.status = StatusRegistration.ODRZUCONY;
    }

    public void setStatusRealized(){
        this.status = StatusRegistration.ZREALIZOWANY;
    }
}
