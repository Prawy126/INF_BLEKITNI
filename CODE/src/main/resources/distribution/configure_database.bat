@echo off
setlocal enabledelayedexpansion

echo Stonka - Konfigurator bazy danych
echo ================================
echo.

REM Rozszerzone sprawdzanie czy MySQL jest zainstalowany
set "MYSQL_FOUND=0"

REM Najpierw sprawdź dokładnie MySQL 9.3
set "MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 9.3\bin"
if exist "!MYSQL_PATH!\mysql.exe" (
    echo Znaleziono MySQL Server 9.3
    set "MYSQL_FOUND=1"
    goto mysql_found
)

REM Sprawdź inne potencjalne wersje, w tym 9.x
for %%v in (9.3 9.2 9.1 9.0 9 8.0 8.1 8.2 5.7 5.8) do (
    set "MYSQL_PATH=C:\Program Files\MySQL\MySQL Server %%v\bin"
    if exist "!MYSQL_PATH!\mysql.exe" (
        echo Znaleziono MySQL Server %%v w !MYSQL_PATH!
        set "MYSQL_FOUND=1"
        goto mysql_found
    )
)

REM Sprawdź instalacje MySQL w programfiles (x86)
for %%v in (9.3 9.2 9.1 9.0 9 8.0 8.1 8.2 5.7 5.8) do (
    set "MYSQL_PATH=C:\Program Files (x86)\MySQL\MySQL Server %%v\bin"
    if exist "!MYSQL_PATH!\mysql.exe" (
        echo Znaleziono MySQL Server %%v w !MYSQL_PATH!
        set "MYSQL_FOUND=1"
        goto mysql_found
    )
)

REM Sprawdź popularne katalogi instalacji XAMPP/WAMP
for %%p in ("C:\xampp\mysql\bin" "C:\wamp\bin\mysql\mysql5.7.40\bin" "C:\wamp64\bin\mysql\mysql5.7.40\bin") do (
    if exist "%%~p\mysql.exe" (
        set "MYSQL_PATH=%%~p"
        echo Znaleziono MySQL w !MYSQL_PATH!
        set "MYSQL_FOUND=1"
        goto mysql_found
    )
)

REM Automatyczne wyszukiwanie pliku mysql.exe w Program Files
echo Szukam MySQL w systemie...
for /r "C:\Program Files" %%f in (mysql.exe) do (
    set "MYSQL_PATH=%%~dpf"
    echo Znaleziono MySQL w: !MYSQL_PATH!
    set "MYSQL_FOUND=1"
    goto mysql_found
)
for /r "C:\Program Files (x86)" %%f in (mysql.exe) do (
    set "MYSQL_PATH=%%~dpf"
    echo Znaleziono MySQL w: !MYSQL_PATH!
    set "MYSQL_FOUND=1"
    goto mysql_found
)

REM Jeśli MySQL nie został znaleziony
if "!MYSQL_FOUND!"=="0" (
    echo Nie znaleziono MySQL! Zainstaluj MySQL Server lub XAMPP i spróbuj ponownie.
    echo.
    echo Jeśli masz już zainstalowany MySQL, podaj ścieżkę do katalogu bin:
    echo Przykład: C:\Program Files\MySQL\MySQL Server 9.3\bin
    set /p "MYSQL_PATH="

    if exist "!MYSQL_PATH!\mysql.exe" (
        echo Znaleziono MySQL w podanej ścieżce
        set "MYSQL_FOUND=1"
    ) else (
        echo Nie znaleziono mysql.exe w podanej ścieżce.
        pause
        exit /b 1
    )
)

:mysql_found
echo Używam MySQL z katalogu: !MYSQL_PATH!
echo.

REM Odczytaj istniejącą konfigurację, jeśli istnieje
set "CONFIG_FILE=%~dp0config\database.properties"
set "DB_HOST=localhost"
set "DB_PORT=3306"
set "DB_NAME=StonkaDB"
set "DB_USER=root"
set "DB_PASS="

if exist "%CONFIG_FILE%" (
    for /f "tokens=1,2 delims==" %%a in (%CONFIG_FILE%) do (
        set "param=%%a"
        set "value=%%b"

        REM Przycinanie spacji z parametrów i wartości
        call :TRIM param "!param!"
        call :TRIM value "!value!"

        if "!param!"=="db.host" set "DB_HOST=!value!"
        if "!param!"=="db.port" set "DB_PORT=!value!"
        if "!param!"=="db.name" set "DB_NAME=!value!"
        if "!param!"=="db.user" set "DB_USER=!value!"
        if "!param!"=="db.password" set "DB_PASS=!value!"
    )
)

echo Aktualne ustawienia:
echo Host: %DB_HOST%
echo Port: %DB_PORT%
echo Nazwa bazy: %DB_NAME%
echo Użytkownik: %DB_USER%
echo Hasło: %DB_PASS:=***%
echo.

REM Pytaj o nowe ustawienia
set /p "NEW_HOST=Nowy host [%DB_HOST%]: "
if "!NEW_HOST!"=="" set "NEW_HOST=%DB_HOST%"

set /p "NEW_PORT=Nowy port [%DB_PORT%]: "
if "!NEW_PORT!"=="" set "NEW_PORT=%DB_PORT%"

set /p "NEW_NAME=Nowa nazwa bazy [%DB_NAME%]: "
if "!NEW_NAME!"=="" set "NEW_NAME=%DB_NAME%"

set /p "NEW_USER=Nowy użytkownik [%DB_USER%]: "
if "!NEW_USER!"=="" set "NEW_USER=%DB_USER%"

set /p "NEW_PASS=Nowe hasło [zachowaj obecne]: "
if "!NEW_PASS!"=="" set "NEW_PASS=%DB_PASS%"

REM Przycinanie spacji z wprowadzonych wartości
call :TRIM NEW_HOST "!NEW_HOST!"
call :TRIM NEW_PORT "!NEW_PORT!"
call :TRIM NEW_NAME "!NEW_NAME!"
call :TRIM NEW_USER "!NEW_USER!"
call :TRIM NEW_PASS "!NEW_PASS!"

echo.
echo Testowanie połączenia...
"!MYSQL_PATH!\mysql" -h %NEW_HOST% -P %NEW_PORT% -u %NEW_USER% -p%NEW_PASS% -e "SELECT 'Połączenie działa!' AS Status;" 2>nul
if %ERRORLEVEL% neq 0 (
    echo Błąd połączenia! Sprawdź ustawienia i spróbuj ponownie.
    pause
    exit /b 1
)

echo Połączenie działa! Tworzenie bazy danych...
"!MYSQL_PATH!\mysql" -h %NEW_HOST% -P %NEW_PORT% -u %NEW_USER% -p%NEW_PASS% -e "CREATE DATABASE IF NOT EXISTS %NEW_NAME% CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;" 2>nul
if %ERRORLEVEL% neq 0 (
    echo Błąd tworzenia bazy danych! Sprawdź uprawnienia użytkownika.
    pause
    exit /b 1
)

echo Importowanie struktury bazy danych...
"!MYSQL_PATH!\mysql" -h %NEW_HOST% -P %NEW_PORT% -u %NEW_USER% -p%NEW_PASS% %NEW_NAME% < "%~dp0sql\Struktura.sql" 2>nul
if %ERRORLEVEL% neq 0 (
    echo Błąd importowania struktury bazy danych!
    pause
    exit /b 1
)

echo Importowanie danych przykładowych...
"!MYSQL_PATH!\mysql" -h %NEW_HOST% -P %NEW_PORT% -u %NEW_USER% -p%NEW_PASS% %NEW_NAME% < "%~dp0sql\Dane.sql" 2>nul
if %ERRORLEVEL% neq 0 (
    echo Błąd importowania danych przykładowych!
    pause
    exit /b 1
)

echo Zapisywanie konfiguracji...
(
echo db.host=!NEW_HOST!
echo db.port=!NEW_PORT!
echo db.name=!NEW_NAME!
echo db.user=!NEW_USER!
echo db.password=!NEW_PASS!
) > "%CONFIG_FILE%"

echo.
echo Konfiguracja bazy danych zakończona pomyślnie!
echo Aplikacja Stonka jest gotowa do użycia.
pause

REM Funkcja do przycinania spacji z początku i końca ciągu znaków
:TRIM
setlocal enabledelayedexpansion
set "str=%~2"
for /f "tokens=* delims= " %%a in ("!str!") do set "str=%%a"
for /l %%a in (1,1,100) do if "!str:~-1!"==" " set "str=!str:~0,-1!"
endlocal & set "%~1=%str%"
goto :EOF