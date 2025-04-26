package org.example.database;

import org.example.sys.Employee;

import java.util.List;

public class Test {
    public static void main(String[] args) {
        UserRepository repo = new UserRepository();
        List<Employee> kasjerzy = repo.pobierzKasjerow();

        for (Employee k : kasjerzy) {
            System.out.println("ID: " + k.getId());
            System.out.println("Imię: " + k.getImie());
            //System.out.println("Stanowisko: " + k.getPosition());
            System.out.println("-------------------");
        }
    }
}
