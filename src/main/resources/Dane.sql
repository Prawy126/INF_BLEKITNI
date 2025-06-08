USE StonkaDB;

-- Rozpocznij transakcję
START TRANSACTION;

-- Sprawdź czy istnieje adres dla administratora
SET @adres_exists = (SELECT COUNT(*) FROM Adresy WHERE Miejscowosc = 'Administrator' AND Kod_pocztowy = '00-000');

-- Jeśli nie ma adresu administratora, dodaj go
INSERT INTO Adresy (Miejscowosc, Numer_domu, Kod_pocztowy, Miasto)
SELECT 'Administrator', '1', '00-000', 'System'
    WHERE @adres_exists = 0;

-- Pobierz ID adresu administratora
SET @adres_id = (SELECT Id FROM Adresy WHERE Miejscowosc = 'Administrator' AND Kod_pocztowy = '00-000' LIMIT 1);

-- Wyświetl informacje o adresie
SELECT @adres_id AS 'ID_Adresu_Administratora';

-- Sprawdź czy użytkownik root już istnieje
SET @user_exists = (SELECT COUNT(*) FROM Pracownicy WHERE Login = 'root');

-- Wyświetl informacje o istnieniu użytkownika
SELECT @user_exists AS 'Czy_Root_Istnieje';

-- Jeśli użytkownik nie istnieje, dodaj go
INSERT INTO Pracownicy (Imie, Nazwisko, Wiek, Id_adresu, Login, Haslo, Email, Zarobki, Stanowisko, onSickLeave, sickLeaveStartDate, usuniety)
SELECT 'root', 'root', 35, @adres_id, 'root', 'root', 'root.root@example.com', 4500.00, 'root', FALSE, NULL, FALSE
    WHERE @user_exists = 0;

-- Jeśli istnieje, zaktualizuj dane
UPDATE Pracownicy
SET Haslo = 'root',
    Email = 'root.root@example.com',
    Stanowisko = 'root',
    usuniety = FALSE
WHERE Login = 'root' AND @user_exists > 0;

-- Potwierdź transakcję
COMMIT;

-- Wyświetl informacje o użytkowniku root
SELECT Id, Imie, Nazwisko, Login, Haslo, Email, Stanowisko
FROM Pracownicy
WHERE Login = 'root';