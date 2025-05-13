package org.example.sys;

import java.time.LocalDate;

public class Report {
    private String name;
    private LocalDate date;
    private String criteria;

    public Report() {}

    public Report(String name, LocalDate date, String criteria) {
        this.name = name;
        this.date = date;
        this.criteria = criteria;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCriteria() {
        return criteria;
    }
    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }
}