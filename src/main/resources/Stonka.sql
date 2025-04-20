-- Tworzenie bazy danych
CREATE DATABASE IF NOT EXISTS StonkaDB;
USE StonkaDB;

-- Tabela Adresy
CREATE TABLE IF NOT EXISTS Adresy (
    Id INT PRIMARY KEY AUTO_INCREMENT,
    Miejscowosc VARCHAR(100),
    Numer_domu VARCHAR(10),
    Numer_mieszkania VARCHAR(10),
    Kod_pocztowy VARCHAR(10),
    Miasto VARCHAR(100)
);

-- Tabela Pracownicy
CREATE TABLE IF NOT EXISTS Pracownicy (
    Id INT PRIMARY KEY AUTO_INCREMENT,
    Imie VARCHAR(100),
    Nazwisko VARCHAR(100),
    Wiek INT,
    Id_adresu INT,
    Login VARCHAR(100),
    Haslo VARCHAR(100),
    Zarobki DECIMAL(10,2),
    Stanowisko VARCHAR(100),
    FOREIGN KEY (Id_adresu) REFERENCES Adresy(Id)
);

-- Tabela Zadania
CREATE TABLE IF NOT EXISTS Zadania (
    Id INT PRIMARY KEY AUTO_INCREMENT,
    Nazwa VARCHAR(100),
    Data DATE,
    Status VARCHAR(50),
    Opis TEXT
);

-- Tabela Wnioski o nieobecnosc
CREATE TABLE IF NOT EXISTS Wnioski_o_nieobecnosc (
    Id INT PRIMARY KEY AUTO_INCREMENT,
    Typ_wniosku VARCHAR(100),
    Data_rozpoczecia DATE,
    Data_zakonczenia DATE,
    Opis TEXT,
    Id_pracownika INT,
    FOREIGN KEY (Id_pracownika) REFERENCES Pracownicy(Id)
);

-- Tabela Produkty
CREATE TABLE IF NOT EXISTS Produkty (
    Id INT PRIMARY KEY AUTO_INCREMENT,
    Nazwa VARCHAR(100),
    Cena DECIMAL(10,2),
    IloscWmagazynie INT
);

-- Tabela Zamowienia
CREATE TABLE IF NOT EXISTS Zamowienia (
    Id INT PRIMARY KEY AUTO_INCREMENT,
    Id_produktu INT,
    Id_pracownika INT,
    Ilosc INT,
    Cena DECIMAL(10,2),
    Data DATE,
    FOREIGN KEY (Id_produktu) REFERENCES Produkty(Id),
    FOREIGN KEY (Id_pracownika) REFERENCES Pracownicy(Id)
);

-- Tabela Transakcje
CREATE TABLE IF NOT EXISTS Transakcje (
    Id INT PRIMARY KEY AUTO_INCREMENT,
    Id_pracownika INT,
    Data DATE,
    FOREIGN KEY (Id_pracownika) REFERENCES Pracownicy(Id)
);

-- Tabela relacji Transakcje <-> Produkty
CREATE TABLE IF NOT EXISTS Transakcje_Produkty (
    Id_transakcji INT,
    Id_produktu INT,
    PRIMARY KEY (Id_transakcji, Id_produktu),
    FOREIGN KEY (Id_transakcji) REFERENCES Transakcje(Id),
    FOREIGN KEY (Id_produktu) REFERENCES Produkty(Id)
);

-- Tabela Raporty
CREATE TABLE IF NOT EXISTS Raporty (
    Id INT PRIMARY KEY AUTO_INCREMENT,
    Typ_raportu VARCHAR(100),
    Data_poczatku DATE,
    Data_zakonczenia DATE,
    Id_pracownika INT,
    Plik TEXT,
    FOREIGN KEY (Id_pracownika) REFERENCES Pracownicy(Id)
);

-- Tabela relacji wiele-do-wielu dla zadań i pracowników
CREATE TABLE IF NOT EXISTS Zadania_Pracownicy (
    Id_pracownika INT,
    Id_zadania INT,
    PRIMARY KEY (Id_pracownika, Id_zadania),
    FOREIGN KEY (Id_pracownika) REFERENCES Pracownicy(Id),
    FOREIGN KEY (Id_zadania) REFERENCES Zadania(Id)
);

-- === Wstawianie danych z zabezpieczeniem przed duplikatami ===

-- Adresy
INSERT IGNORE INTO Adresy (Id, Miejscowosc, Numer_domu, Numer_mieszkania, Kod_pocztowy, Miasto)
VALUES
    (1, 'Warszawa', '12A', '3', '00-001', 'Warszawa'),
    (2, 'Kraków', '10', '5', '30-001', 'Kraków'),
    (3, 'Poznań', '20', NULL, '60-002', 'Poznań'),
    (4, 'Gdańsk', '8', '2A', '80-003', 'Gdańsk');

-- Pracownicy
INSERT IGNORE INTO Pracownicy (Id, Imie, Nazwisko, Wiek, Id_adresu, Login, Haslo, Zarobki, Stanowisko)
VALUES
    (1, 'Jan', 'Kowalski', 35, 1, 'admin', 'admin123', 4500.00, 'kierownik'),
    (2, 'Anna', 'Adminowska', 30, 2, 'admin', 'admin123', 6000.00, 'admin'),
    (3, 'Piotr', 'Logistykiewicz', 28, 3, 'logistyk', 'log123', 4500.00, 'logistyk'),
    (4, 'Kasia', 'Kasjerka', 22, 4, 'kasjer', 'kasjer123', 3500.00, 'kasjer');

-- Produkty
INSERT IGNORE INTO Produkty (Id, Nazwa, Cena, IloscWmagazynie)
VALUES
    (1, 'Mleko', 2.99, 150),
    (2, 'Chleb', 3.49, 200);

-- Zadania
INSERT IGNORE INTO Zadania (Id, Nazwa, Data, Status, Opis)
VALUES
    (1, 'Sprawdzenie stanu magazynu', '2025-04-01', 'Nowe', 'Weryfikacja towaru przed dostawą');

-- Powiązanie zadania z pracownikiem
INSERT IGNORE INTO Zadania_Pracownicy (Id_pracownika, Id_zadania)
VALUES
    (1, 1);

