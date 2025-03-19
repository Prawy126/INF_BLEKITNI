package org.example.sys;

public class Admin extends Person {
    private boolean admin = true;
    public Admin(String name, String surname, String login, String password) {
        super(name, surname, login, password);
    }
    public Admin(){

    }
    public boolean isAdmin() {
        return admin;
    }
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

}
