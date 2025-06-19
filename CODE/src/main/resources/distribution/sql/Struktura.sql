USE StonkaDB;

-- =============================================================
-- TABELA: Adresy
-- =============================================================
CREATE TABLE IF NOT EXISTS Adresy (
    Id INT PRIMARY KEY AUTO_INCREMENT,
    Miejscowosc       VARCHAR(100),
    Numer_domu        VARCHAR(10),
    Numer_mieszkania  VARCHAR(10),
    Kod_pocztowy      VARCHAR(10),
    Miasto            VARCHAR(100)
    );

-- =============================================================
-- TABELA: Pracownicy
-- =============================================================
CREATE TABLE IF NOT EXISTS Pracownicy (
    Id                   INT PRIMARY KEY AUTO_INCREMENT,
    Imie                 VARCHAR(100),
    Nazwisko             VARCHAR(100),
    Wiek                 INT,
    Id_adresu            INT,
    Login                VARCHAR(100),
    Haslo                VARCHAR(100),
    Email                VARCHAR(100),
    Zarobki              DECIMAL(10,2),
    Stanowisko           VARCHAR(100),
    onSickLeave          BOOLEAN DEFAULT FALSE,
    sickLeaveStartDate   DATE,
    usuniety             BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (Id_adresu) REFERENCES Adresy(Id)
    );

-- =============================================================
-- TABELA: Zgłoszenia techniczne (kaskadowe usuwanie)
-- =============================================================
CREATE TABLE IF NOT EXISTS Zgloszenia_techniczne (
    Id               INT PRIMARY KEY AUTO_INCREMENT,
    Typ              VARCHAR(100),
    Opis             TEXT,
    Data_zgloszenia  DATE DEFAULT (CURRENT_DATE),
    Id_pracownika    INT,
    Status           VARCHAR(50) DEFAULT 'Nowe',
    FOREIGN KEY (Id_pracownika) REFERENCES Pracownicy(Id) ON DELETE CASCADE
    );

-- =============================================================
-- TABELA: Zadania
-- =============================================================
CREATE TABLE IF NOT EXISTS Zadania (
    Id     INT PRIMARY KEY AUTO_INCREMENT,
    Nazwa  VARCHAR(100),
    Data   DATE,
    Status VARCHAR(50),
    Opis   TEXT,
    czas_trwania_zmiany TIME NULL COMMENT 'Czas trwania zmiany pracownika przy zadaniu',
    Priorytet ENUM('HIGH', 'MEDIUM', 'LOW') NULL
    );

-- =============================================================
-- TABELA: Wnioski o nieobecność (kaskadowe usuwanie)
-- =============================================================
CREATE TABLE IF NOT EXISTS Wnioski_o_nieobecnosc (
    Id                 INT PRIMARY KEY AUTO_INCREMENT,
    Typ_wniosku        VARCHAR(100),
    Data_rozpoczecia   DATE,
    Data_zakonczenia   DATE,
    Opis               TEXT,
    Status             ENUM('PENDING','ACCEPTED','REJECTED') DEFAULT 'PENDING',
    Id_pracownika      INT,
    FOREIGN KEY (Id_pracownika) REFERENCES Pracownicy(Id) ON DELETE CASCADE
    );

-- =============================================================
-- TABELA: Produkty
-- =============================================================
CREATE TABLE IF NOT EXISTS Produkty (
    Id        INT PRIMARY KEY AUTO_INCREMENT,
    Nazwa     VARCHAR(100),
    Kategoria VARCHAR(100),
    Cena      DECIMAL(10,2)
    );

-- =============================================================
-- TABELA: Stany magazynowe
-- =============================================================
CREATE TABLE IF NOT EXISTS StanyMagazynowe (
    Id_produktu INT PRIMARY KEY,
    Ilosc       INT NOT NULL,
    FOREIGN KEY (Id_produktu) REFERENCES Produkty(Id) ON DELETE CASCADE
    );

-- =============================================================
-- TABELA: Zamówienia (bez kaskadowego usuwania)
-- =============================================================
CREATE TABLE IF NOT EXISTS Zamowienia (
    Id            INT PRIMARY KEY AUTO_INCREMENT,
    Id_produktu   INT,
    Id_pracownika INT,
    Ilosc         INT,
    Cena          DECIMAL(10,2),
    Data          DATE,
    FOREIGN KEY (Id_produktu) REFERENCES Produkty(Id),
    FOREIGN KEY (Id_pracownika) REFERENCES Pracownicy(Id)
    );

-- =============================================================
-- TABELA: Transakcje (bez kaskadowego usuwania)
-- =============================================================
CREATE TABLE IF NOT EXISTS Transakcje (
    Id             INT PRIMARY KEY AUTO_INCREMENT,
    Id_pracownika  INT,
    Data           DATE,
    FOREIGN KEY (Id_pracownika) REFERENCES Pracownicy(Id)
    );

-- =============================================================
-- TABELA: Relacja Transakcje <-> Produkty
-- =============================================================
CREATE TABLE IF NOT EXISTS Transakcje_Produkty (
    Id_transakcji  INT,
    Id_produktu    INT,
    Ilosc          INT NOT NULL,
    PRIMARY KEY (Id_transakcji, Id_produktu),
    FOREIGN KEY (Id_transakcji) REFERENCES Transakcje(Id),
    FOREIGN KEY (Id_produktu)   REFERENCES Produkty(Id)
    );

-- =============================================================
-- TABELA: Raporty (bez kaskadowego usuwania)
-- =============================================================
CREATE TABLE IF NOT EXISTS Raporty (
    Id              INT PRIMARY KEY AUTO_INCREMENT,
    Typ_raportu     VARCHAR(100),
    Data_poczatku   DATE,
    Data_zakonczenia DATE,
    Id_pracownika   INT,
    Plik            TEXT,
    FOREIGN KEY (Id_pracownika) REFERENCES Pracownicy(Id)
    );

-- =============================================================
-- TABELA: Relacja Zadania <-> Pracownicy (wiele-do-wielu)
-- =============================================================
CREATE TABLE IF NOT EXISTS Zadania_Pracownicy (
    Id_pracownika INT,
    Id_zadania    INT,
    PRIMARY KEY (Id_pracownika, Id_zadania),
    FOREIGN KEY (Id_pracownika) REFERENCES Pracownicy(Id) ON DELETE CASCADE,
    FOREIGN KEY (Id_zadania)    REFERENCES Zadania(Id)
    );

CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pracownik_id INT NOT NULL,
    reset_code_hash VARCHAR(60) NOT NULL,
    expiration_time DATETIME NOT NULL,
    used TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (pracownik_id) REFERENCES Pracownicy(Id) ON DELETE CASCADE,
    INDEX idx_pracownik (pracownik_id),
    INDEX idx_code (reset_code_hash)
    );

ALTER TABLE Zadania
    ADD COLUMN usuniety BOOLEAN DEFAULT FALSE;

