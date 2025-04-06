package org.example.sys;

enum StatusRegistration {
    OCZEKUJACY("Oczekujący"),
    ZAAKCEPTOWANY("Zaakceptowany"),
    ODRZUCONY("Odrzucony"),
    ZREALIZOWANY("Zrealizowany");

    private final String displayName;

    StatusRegistration(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
