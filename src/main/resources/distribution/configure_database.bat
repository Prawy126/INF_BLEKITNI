@echo off
setlocal EnableDelayedExpansion

:: Katalog pliku konfiguracyjnego
set CONFIG_DIR=%~dp0config
set CONFIG_FILE=%CONFIG_DIR%\database.properties

:: Upewnij się, że katalog config istnieje
if not exist "%CONFIG_DIR%" mkdir "%CONFIG_DIR%"

:: Pobierz dane od użytkownika
set /p DB_HOST=Podaj host bazy danych [localhost]:
if "!DB_HOST!"=="" set DB_HOST=localhost

set /p DB_PORT=Podaj port bazy danych [3306]:
if "!DB_PORT!"=="" set DB_PORT=3306

set /p DB_NAME=Podaj nazwę bazy danych [StonkaDB]:
if "!DB_NAME!"=="" set DB_NAME=StonkaDB

set /p DB_USER=Podaj użytkownika bazy danych [root]:
if "!DB_USER!"=="" set DB_USER=root

set /p DB_PASS=Podaj hasło do bazy danych:

:: Zapisz konfigurację do pliku
echo # Database Configuration > "%CONFIG_FILE%"
echo # %date% %time% >> "%CONFIG_FILE%"
echo db.host=%DB_HOST% >> "%CONFIG_FILE%"
echo db.port=%DB_PORT% >> "%CONFIG_FILE%"
echo db.name=%DB_NAME% >> "%CONFIG_FILE%"
echo db.user=%DB_USER% >> "%CONFIG_FILE%"
echo db.password=%DB_PASS% >> "%CONFIG_FILE%"

echo Konfiguracja została zapisana do %CONFIG_FILE%
pause