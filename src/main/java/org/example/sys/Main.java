package org.example.sys;

// Jakub
// klasa typowo do testów aktualnie jest puta

import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;

public class Main {

    public static void main(String[] args) {
        try{
            Logistician logistician =  new Logistician("Michał", "Pilecki", 21, "adres", "hasło321312", "email", "dział", "stanowisko", -5000);
        }catch (PasswordException e){
            System.out.println("Hasło musi mieć co najmniej 8 znaków");
        } catch (SalaryException e) {
            throw new RuntimeException(e);
        }
    }
}

