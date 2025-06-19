@echo off
setlocal

REM =========================================================
REM Skrypt uruchamiający aplikację Stonka
REM Autor: Jakub Opar
REM Data: 2025-06-07
REM =========================================================

title Uruchamianie Stonka...
echo Uruchamianie aplikacji Stonka...
echo.

REM Sprawdzenie czy Java jest zainstalowana
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [BŁĄD] Java nie została znaleziona w systemie.
    echo Zainstaluj Java Runtime Environment w wersji 17 lub nowszej.
    echo Możesz pobrać ją ze strony: https://adoptium.net/
    echo.
    pause
    exit /b 1
)

REM Sprawdzenie, czy folder lib istnieje
if not exist "lib\" (
    echo [BŁĄD] Nie znaleziono folderu 'lib' z bibliotekami JavaFX.
    echo Upewnij się, że folder 'lib' znajduje się w tym samym katalogu co ten skrypt.
    echo.
    pause
    exit /b 1
)

REM Sprawdzenie, czy plik JAR istnieje
if not exist "Projekt-1.0-SNAPSHOT.jar" (
    echo [BŁĄD] Nie znaleziono pliku JAR aplikacji.
    echo Upewnij się, że plik 'Projekt-1.0-SNAPSHOT.jar' znajduje się w tym samym katalogu co ten skrypt.
    echo.
    pause
    exit /b 1
)

echo Uruchamianie aplikacji z parametrami JavaFX...

REM Uruchomienie aplikacji z odpowiednimi modułami JavaFX
java --enable-preview ^
     --module-path "lib" ^
     --add-modules javafx.controls,javafx.fxml,javafx.web,javafx.swing,javafx.media ^
     -jar Projekt-1.0-SNAPSHOT.jar

REM Sprawdź czy aplikacja zakończyła się prawidłowo
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [UWAGA] Aplikacja zakończyła działanie z błędem (kod: %ERRORLEVEL%).
    echo Sprawdź logi aplikacji lub skontaktuj się z pomocą techniczną.
    echo.
    pause
    exit /b %ERRORLEVEL%
)

exit /b 0