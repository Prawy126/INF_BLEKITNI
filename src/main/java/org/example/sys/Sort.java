/*
 * Enum: Sort
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

public enum Sort {
    NAME("Nazwa"),
    DATE("Data"),
    PRIORITY("Priorytet"),
    DEFAULT("Domyślny");

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    Sort(String name) {
        this.name = name;
    }
}
