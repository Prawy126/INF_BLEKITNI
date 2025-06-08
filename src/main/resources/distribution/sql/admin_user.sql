-- admin_user.sql
SET @adres_check := (SELECT COUNT(*) FROM Adresy WHERE Miejscowosc = 'Administrator' AND Kod_pocztowy = '00-000');

-- Dodaj adres tylko jeśli nie istnieje
INSERT INTO Adresy (Miejscowosc, Numer_domu, Kod_pocztowy, Miasto)
SELECT 'Administrator', '1', '00-000', 'System'
    WHERE @adres_check = 0;

-- Pobierz ID adresu (jeśli już istniał lub został właśnie dodany)
SET @adres_id := (SELECT Id FROM Adresy WHERE Miejscowosc = 'Administrator' AND Kod_pocztowy = '00-000' LIMIT 1);

-- Dodaj użytkownika root
INSERT INTO Pracownicy (Imie, Nazwisko, Wiek, Id_adresu, Login, Haslo, Email, Zarobki, Stanowisko, onSickLeave, sickLeaveStartDate, usuniety)
VALUES ('root', 'root', 35, @adres_id, '%USER%', '%PASS%', 'root.root@example.com', 4500.00, 'root', FALSE, NULL, FALSE)
    ON DUPLICATE KEY UPDATE Haslo='%PASS%';