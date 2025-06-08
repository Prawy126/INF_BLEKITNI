; Skrypt instalacyjny dla aplikacji Stonka
; Wygenerowany: 2025-06-08

#define MyAppName "Stonka"
#define MyAppVersion "1.0"
#define MyAppPublisher "BŁĘKITNI"
#define MyAppURL "https://www.example.com/"
#define MyAppExeName "stonka.exe"
#define MySQLVersion "8.0.35"
#define MySQLInstallerURL "https://dev.mysql.com/get/Downloads/MySQLInstaller/mysql-installer-community-8.0.35.0.msi"
#define MySQLInstallerFilename "mysql-installer-community.msi"

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
AppId={{DCB98A55-5294-4A72-9A26-2A5CF5DA4885}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppVerName={#MyAppName} {#MyAppVersion} (wymaga Java 22)
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={autopf}\{#MyAppName}
UninstallDisplayIcon={app}\{#MyAppExeName}
DisableProgramGroupPage=no
; Komentuje te pliki, jeśli nie istnieją usuń średnika jeśli masz te pliki
LicenseFile=C:\Users\jakub\Pliki\GIT\INF_BLEKITNI\license.txt
InfoBeforeFile=C:\Users\jakub\Pliki\GIT\INF_BLEKITNI\przed.txt
InfoAfterFile=C:\Users\jakub\Pliki\GIT\INF_BLEKITNI\po.txt
OutputDir=C:\Users\jakub\Pliki\GIT\INF_BLEKITNI\instalator
OutputBaseFilename=stonka-setup
SetupIconFile=C:\Users\jakub\Pliki\GIT\INF_BLEKITNI\src\main\resources\distribution\stonka.ico
Compression=lzma
SolidCompression=yes
WizardStyle=modern

; Minimalne wymagania
MinVersion=10.0
PrivilegesRequired=admin

; Dodatkowe opcje dla wyświetlania strony konfiguracji DB
DisableWelcomePage=no
DisableDirPage=no
ShowTasksTreeLines=yes

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "polish"; MessagesFile: "compiler:Languages\Polish.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked
Name: "installmysql"; Description: "Zainstaluj MySQL Community Edition {#MySQLVersion}"; GroupDescription: "Opcjonalne komponenty:"; Flags: checkedonce

[Dirs]
; Katalogi w lokalizacji aplikacji
Name: "{app}\config"; Permissions: users-full
Name: "{app}\resources"; Permissions: users-full
Name: "{app}\sql"; Permissions: users-full
Name: "{app}\lib"; Permissions: users-full
Name: "{app}\logs"; Permissions: users-full
Name: "{app}\temp"; Permissions: users-full; Tasks: installmysql

; Katalogi danych użytkownika w AppData
Name: "{localappdata}\{#MyAppName}"; Permissions: users-full
Name: "{localappdata}\{#MyAppName}\logs"; Permissions: users-full
Name: "{localappdata}\{#MyAppName}\reports"; Permissions: users-full
Name: "{localappdata}\{#MyAppName}\backups"; Permissions: users-full
Name: "{localappdata}\{#MyAppName}\backup-csv"; Permissions: users-full

; Upewnij się, że katalog resources istnieje
Name: "{app}\resources\images"; Permissions: users-full
Name: "{app}\resources\templates"; Permissions: users-full

[Files]
; Główne pliki aplikacji
Source: "C:\Users\jakub\Pliki\GIT\INF_BLEKITNI\target\dist\stonka.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\jakub\Pliki\GIT\INF_BLEKITNI\target\dist\stonka.ico"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\jakub\Pliki\GIT\INF_BLEKITNI\target\dist\run.bat"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\jakub\Pliki\GIT\INF_BLEKITNI\target\dist\Projekt-1.0-SNAPSHOT.jar"; DestDir: "{app}"; Flags: ignoreversion skipifsourcedoesntexist

; Biblioteki (wszystkie pliki JAR)
Source: "C:\Users\jakub\Pliki\GIT\INF_BLEKITNI\target\dist\lib\*"; DestDir: "{app}\lib"; Flags: ignoreversion recursesubdirs createallsubdirs

; Pliki konfiguracyjne
Source: "C:\Users\jakub\Pliki\GIT\INF_BLEKITNI\target\dist\config\*"; DestDir: "{app}\config"; Flags: ignoreversion recursesubdirs createallsubdirs

; Skrypty SQL
Source: "C:\Users\jakub\Pliki\GIT\INF_BLEKITNI\target\dist\sql\*"; DestDir: "{app}\sql"; Flags: ignoreversion recursesubdirs createallsubdirs

; Pliki zasobów
Source: "C:\Users\jakub\Pliki\GIT\INF_BLEKITNI\src\main\resources\*"; DestDir: "{app}\resources"; Flags: ignoreversion recursesubdirs createallsubdirs skipifsourcedoesntexist
Source: "C:\Users\jakub\Pliki\GIT\INF_BLEKITNI\src\main\resources\images\*"; DestDir: "{app}\resources\images"; Flags: ignoreversion recursesubdirs createallsubdirs skipifsourcedoesntexist
Source: "C:\Users\jakub\Pliki\GIT\INF_BLEKITNI\src\main\resources\templates\*"; DestDir: "{app}\resources\templates"; Flags: ignoreversion recursesubdirs createallsubdirs skipifsourcedoesntexist

; Domyślny plik konfiguracyjny - opcjonalnie, jeśli masz gotowy szablon
Source: "C:\Users\jakub\Pliki\GIT\INF_BLEKITNI\src\main\resources\default_config.properties"; DestName: "config.properties"; DestDir: "{app}\config"; Flags: ignoreversion onlyifdoesntexist skipifsourcedoesntexist

; Log4j konfiguracja
Source: "C:\Users\jakub\Pliki\GIT\INF_BLEKITNI\src\main\resources\log4j2.xml"; DestDir: "{app}\config"; Flags: ignoreversion skipifsourcedoesntexist

; Pliki dla konfiguracji MySQL
Source: "C:\Users\jakub\Pliki\GIT\INF_BLEKITNI\src\main\resources\my-custom.ini"; DestDir: "{app}\temp"; Flags: ignoreversion skipifsourcedoesntexist; Tasks: installmysql

[Icons]
Name: "{autoprograms}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; IconFilename: "{app}\stonka.ico"
Name: "{autoprograms}\{#MyAppName}\Konfiguracja bazy danych"; Filename: "{sys}\control.exe"; Parameters: "appwiz.cpl"; WorkingDir: "{app}"; Comment: "Konfigurację bazy danych można zmienić bezpośrednio w aplikacji"; 
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; IconFilename: "{app}\stonka.ico"; Tasks: desktopicon

[Run]
; Domyślna konfiguracja, jeśli nie istnieje
Filename: "{cmd}"; Parameters: "/c if not exist ""{app}\config\database.properties"" echo db.host=localhost> ""{app}\config\database.properties"""; Flags: runhidden

; Pobierz i zainstaluj MySQL jeśli wybrany i nie istnieje
Filename: "{tmp}\mysql_download.bat"; StatusMsg: "Pobieranie MySQL (może potrwać kilka minut)..."; Tasks: installmysql; Flags: shellexec waituntilterminated runhidden; Check: ShouldInstallMySQL

; Zainstaluj MySQL
Filename: "msiexec.exe"; Parameters: "/i ""{tmp}\{#MySQLInstallerFilename}"" /qr ALLUSERS=1"; StatusMsg: "Instalowanie MySQL..."; Tasks: installmysql; Flags: waituntilterminated; Check: ShouldInstallMySQL

; Konfiguruj MySQL (tylko jeśli właśnie zainstalowano)
Filename: "{tmp}\configure_mysql.bat"; StatusMsg: "Konfigurowanie MySQL..."; Tasks: installmysql; Flags: waituntilterminated runhidden; Check: ShouldInstallMySQL

; Uruchomienie po instalacji
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent

[Code]
var
  MySQLPage: TInputQueryWizardPage;
  DBConfigPage: TInputQueryWizardPage;
  TestConnectionButton: TNewButton;
  ConnectionStatusLabel: TNewStaticText;
  MySQLRootPassword: String;
  MySQLAppUsername: String;
  MySQLAppPassword: String;
  UseExistingMySQL: Boolean;
  MySQLDownloadPath: String;
  MySQLConfigPath: String;

// Funkcja do testowania połączenia z bazą danych
function TestDatabaseConnection(Host, Port, User, Password: String): Boolean;
var
  ResultCode: Integer;
  TempBatPath: String;
  TempOutputPath: String;
  FileContents: TStringList;
begin
  Result := False;
  // Utwórz tymczasowy skrypt do testowania połączenia
  TempBatPath := ExpandConstant('{tmp}\test_connection.bat');
  TempOutputPath := ExpandConstant('{tmp}\connection_result.txt');
  
  FileContents := TStringList.Create;
  try
    FileContents.Add('@echo off');
    FileContents.Add('setlocal enabledelayedexpansion');
    
    // Wykrywanie MySQL
    FileContents.Add('set "MYSQL_FOUND=0"');
    FileContents.Add('for %%v in (9.3 9.2 9.1 9.0 9 8.0 8.1 8.2 5.7 5.8) do (');
    FileContents.Add('  set "MYSQL_PATH=C:\Program Files\MySQL\MySQL Server %%v\bin"');
    FileContents.Add('  if exist "!MYSQL_PATH!\mysql.exe" set "MYSQL_FOUND=1" & goto mysql_found');
    FileContents.Add('  set "MYSQL_PATH=C:\Program Files (x86)\MySQL\MySQL Server %%v\bin"');
    FileContents.Add('  if exist "!MYSQL_PATH!\mysql.exe" set "MYSQL_FOUND=1" & goto mysql_found');
    FileContents.Add(')');
    FileContents.Add('for %%p in ("C:\xampp\mysql\bin" "C:\wamp\bin\mysql\mysql5.7.40\bin" "C:\wamp64\bin\mysql\mysql5.7.40\bin") do (');
    FileContents.Add('  if exist "%%~p\mysql.exe" set "MYSQL_PATH=%%~p" & set "MYSQL_FOUND=1" & goto mysql_found');
    FileContents.Add(')');
    
    // Wyszukiwanie ręczne
    FileContents.Add('for /r "C:\Program Files" %%f in (mysql.exe) do (');
    FileContents.Add('  set "MYSQL_PATH=%%~dpf" & set "MYSQL_FOUND=1" & goto mysql_found');
    FileContents.Add(')');
    FileContents.Add('for /r "C:\Program Files (x86)" %%f in (mysql.exe) do (');
    FileContents.Add('  set "MYSQL_PATH=%%~dpf" & set "MYSQL_FOUND=1" & goto mysql_found');
    FileContents.Add(')');
    
    FileContents.Add(':mysql_found');
    FileContents.Add('if "%MYSQL_FOUND%"=="0" (echo ERROR: MySQL nie został znaleziony) & exit 1');
    
    // Test połączenia
    FileContents.Add('"%MYSQL_PATH%\mysql" -h ' + Host + ' -P ' + Port + ' -u ' + User + ' -p' + Password + ' -e "SELECT 1;" >nul 2>&1');
    FileContents.Add('if %ERRORLEVEL% equ 0 (');
    FileContents.Add('  echo CONNECTION_OK > "' + TempOutputPath + '"');
    FileContents.Add(') else (');
    FileContents.Add('  echo CONNECTION_FAILED > "' + TempOutputPath + '"');
    FileContents.Add(')');
    
    FileContents.SaveToFile(TempBatPath);
    
    // Uruchom skrypt testujący
    if Exec(ExpandConstant('{cmd}'), '/C "' + TempBatPath + '"', '', SW_HIDE, ewWaitUntilTerminated, ResultCode) then
    begin
      if FileExists(TempOutputPath) then
      begin
        FileContents.LoadFromFile(TempOutputPath);
        if (FileContents.Count > 0) and (Trim(FileContents[0]) = 'CONNECTION_OK') then
          Result := True;
      end;
    end;
  finally
    FileContents.Free;
    DeleteFile(TempBatPath);
    DeleteFile(TempOutputPath);
  end;
end;

// Obsługa przycisku testowania połączenia
procedure TestButtonClick(Sender: TObject);
begin
  ConnectionStatusLabel.Caption := 'Testowanie połączenia...';
  ConnectionStatusLabel.Font.Color := clBlack;
  ConnectionStatusLabel.Show;
  WizardForm.Refresh;
  
  if TestDatabaseConnection(
      Trim(DBConfigPage.Values[0]),
      Trim(DBConfigPage.Values[1]),
      Trim(DBConfigPage.Values[3]),
      Trim(DBConfigPage.Values[4])) then
  begin
    ConnectionStatusLabel.Caption := 'Połączenie udane!';
    ConnectionStatusLabel.Font.Color := clGreen;
  end else begin
    ConnectionStatusLabel.Caption := 'Błąd połączenia. Sprawdź ustawienia.';
    ConnectionStatusLabel.Font.Color := clRed;
  end;
end;

// Sprawdzenie, czy Java jest zainstalowana
function IsJavaInstalled(): Boolean;
var
  JavaPath: String;
begin
  Result := RegQueryStringValue(HKLM, 'SOFTWARE\JavaSoft\JDK', 'CurrentVersion', JavaPath);
  if not Result then
    Result := RegQueryStringValue(HKLM, 'SOFTWARE\JavaSoft\Java Runtime Environment', 'CurrentVersion', JavaPath);
end;

// Sprawdzenie wersji Javy (minimum 22)
function CheckJavaVersion(): Boolean;
var
  JavaVersion, MajorVersion: String;
  VersionNum: Integer;
begin
  Result := False;
  
  if RegQueryStringValue(HKLM, 'SOFTWARE\JavaSoft\JDK', 'CurrentVersion', JavaVersion) then
  begin
    MajorVersion := Copy(JavaVersion, 1, Pos('.', JavaVersion) - 1);
    VersionNum := StrToIntDef(MajorVersion, 0);
    Result := (VersionNum >= 22);
  end;
  
  if not Result then
  begin
    if RegQueryStringValue(HKLM, 'SOFTWARE\JavaSoft\Java Runtime Environment', 'CurrentVersion', JavaVersion) then
    begin
      MajorVersion := Copy(JavaVersion, 1, Pos('.', JavaVersion) - 1);
      VersionNum := StrToIntDef(MajorVersion, 0);
      Result := (VersionNum >= 22);
    end;
  end;
end;

// Sprawdzenie czy MySQL jest już zainstalowany
function IsMySQLInstalled(): Boolean;
begin
  Result := RegKeyExists(HKLM, 'SOFTWARE\MySQL AB') or
            RegKeyExists(HKLM64, 'SOFTWARE\MySQL AB');
end;

// Funkcja określająca czy należy instalować MySQL
function ShouldInstallMySQL(): Boolean;
begin
  Result := WizardIsTaskSelected('installmysql') and not IsMySQLInstalled();
end;

// Tworzenie skryptu pobierania MySQL
procedure CreateMySQLDownloadScript();
var
  FilePath: String;
  FileContents: TStringList;
begin
  FilePath := ExpandConstant('{tmp}\mysql_download.bat');
  FileContents := TStringList.Create;
  try
    FileContents.Add('@echo off');
    FileContents.Add('echo Pobieranie MySQL Installer...');
    FileContents.Add('powershell -Command "& {');
    FileContents.Add('    $url = ''' + ExpandConstant('{#MySQLInstallerURL}') + ''';');
    FileContents.Add('    $output = ''' + MySQLDownloadPath + ''';');
    FileContents.Add('    $wc = New-Object System.Net.WebClient;');
    FileContents.Add('    $wc.DownloadFile($url, $output);');
    FileContents.Add('    Write-Host ''Pobieranie zakończone.'';');
    FileContents.Add('}"');
    FileContents.SaveToFile(FilePath);
  finally
    FileContents.Free;
  end;
end;

procedure CreateMySQLConfigScript();
var
  FileContents: TStringList;
  AdminSQLContents: TStringList;
  TrimmedRootPassword: String;
  TrimmedAppUsername: String;
  TrimmedAppPassword: String;
  AdminSQLPath: String;
begin
  // Przycinanie wszystkich wartości na początku aby zapobiec błędom z dodatkową spacją
  TrimmedRootPassword := Trim(MySQLRootPassword);
  TrimmedAppUsername := Trim(MySQLAppUsername);
  TrimmedAppPassword := Trim(MySQLAppPassword);
  
  // Ścieżka do tymczasowego pliku SQL dla administratora
  AdminSQLPath := ExpandConstant('{tmp}\admin_user.sql');
  
  // Utworzenie pliku SQL dla administratora bezpośrednio z wstawionymi wartościami
  AdminSQLContents := TStringList.Create;
  try
    AdminSQLContents.Add('USE StonkaDB;');
    AdminSQLContents.Add('-- Wyświetl diagnostykę');
    AdminSQLContents.Add('SELECT ''Rozpoczynam dodawanie administratora'' AS ''Status'';');
    AdminSQLContents.Add('');
    AdminSQLContents.Add('-- Dodaj adres administratora');
    AdminSQLContents.Add('INSERT INTO Adresy (Miejscowosc, Numer_domu, Kod_pocztowy, Miasto)');
    AdminSQLContents.Add('SELECT ''Administrator'', ''1'', ''00-000'', ''System''');
    AdminSQLContents.Add('WHERE NOT EXISTS (');
    AdminSQLContents.Add('    SELECT 1 FROM Adresy ');
    AdminSQLContents.Add('    WHERE Miejscowosc = ''Administrator'' AND Kod_pocztowy = ''00-000''');
    AdminSQLContents.Add(');');
    AdminSQLContents.Add('');
    AdminSQLContents.Add('-- Pobierz ID adresu');
    AdminSQLContents.Add('SELECT @adres_id := Id FROM Adresy');
    AdminSQLContents.Add('WHERE Miejscowosc = ''Administrator'' AND Kod_pocztowy = ''00-000'' LIMIT 1;');
    AdminSQLContents.Add('');
    AdminSQLContents.Add('SELECT CONCAT(''Użyję ID adresu: '', @adres_id) AS ''Info'';');
    AdminSQLContents.Add('');
    AdminSQLContents.Add('-- Sprawdź czy użytkownik już istnieje');
    AdminSQLContents.Add('SELECT @user_exists := COUNT(*) FROM Pracownicy WHERE Login = ''' + TrimmedAppUsername + ''';');
    AdminSQLContents.Add('');
    AdminSQLContents.Add('SELECT CONCAT(''Czy użytkownik istnieje: '', @user_exists) AS ''Info'';');
    AdminSQLContents.Add('');
    AdminSQLContents.Add('-- Usuń użytkownika jeśli istnieje');
    AdminSQLContents.Add('DELETE FROM Pracownicy WHERE Login = ''' + TrimmedAppUsername + ''';');
    AdminSQLContents.Add('');
    AdminSQLContents.Add('-- Dodaj nowego użytkownika');
    AdminSQLContents.Add('INSERT INTO Pracownicy (Imie, Nazwisko, Wiek, Id_adresu, Login, Haslo, Email, Zarobki, Stanowisko, onSickLeave, sickLeaveStartDate, usuniety)');
    AdminSQLContents.Add('VALUES (''root'', ''root'', 35, @adres_id, ''' + TrimmedAppUsername + ''', ''' + TrimmedAppPassword + ''', ''root.root@example.com'', 4500.00, ''root'', FALSE, NULL, FALSE);');
    AdminSQLContents.Add('');
    AdminSQLContents.Add('-- Weryfikacja');
    AdminSQLContents.Add('SELECT COUNT(*) AS ''Liczba administratorów'' FROM Pracownicy WHERE Login = ''' + TrimmedAppUsername + ''';');
    
    // Zapisz plik SQL z wartościami już wstawionymi (nie używamy %USER% i %PASS%)
    AdminSQLContents.SaveToFile(AdminSQLPath);
  finally
    AdminSQLContents.Free;
  end;
  
  // Teraz tworzymy główny skrypt konfiguracyjny
  FileContents := TStringList.Create;
  try
    FileContents.Add('@echo off');
    FileContents.Add('echo Konfigurowanie MySQL...');
    
    // Kopiowanie pliku konfiguracyjnego
    FileContents.Add('if exist "' + ExpandConstant('{app}\temp\my-custom.ini') + '" (');
    FileContents.Add('  echo Kopiowanie pliku konfiguracyjnego MySQL...');
    FileContents.Add('  copy "' + ExpandConstant('{app}\temp\my-custom.ini') + '" "C:\ProgramData\MySQL\MySQL Server 8.0\my.ini" /Y');
    FileContents.Add(')');
    
    // Konfigurowanie MySQL
    FileContents.Add('echo Ustawianie hasła root...');
    FileContents.Add('set "MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 8.0\bin"');
    FileContents.Add('if exist "%MYSQL_PATH%\mysql.exe" (');
    FileContents.Add('  "%MYSQL_PATH%\mysqladmin" -u root password "' + TrimmedRootPassword + '"');
    
    // Tworzenie użytkownika dla aplikacji
    FileContents.Add('  "%MYSQL_PATH%\mysql" -u root -p' + TrimmedRootPassword + ' -e "CREATE USER IF NOT EXISTS ''"' + TrimmedAppUsername + '"''@''localhost'' IDENTIFIED BY ''"' + TrimmedAppPassword + '"''; GRANT ALL PRIVILEGES ON *.* TO ''"' + TrimmedAppUsername + '"''@''localhost'';"');
    FileContents.Add('  echo Utworzono użytkownika ' + TrimmedAppUsername);
    
    // Tworzenie bazy danych i importowanie struktury
    FileContents.Add('  "%MYSQL_PATH%\mysql" -u root -p' + TrimmedRootPassword + ' -e "CREATE DATABASE IF NOT EXISTS StonkaDB CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"');
    FileContents.Add('  "%MYSQL_PATH%\mysql" -u root -p' + TrimmedRootPassword + ' StonkaDB < "' + ExpandConstant('{app}\sql\Struktura.sql') + '"');
    
    // Dodanie użytkownika root do aplikacji za pomocą przygotowanego pliku
    FileContents.Add('  echo Tworzenie użytkownika administratora aplikacji...');
    FileContents.Add('  "%MYSQL_PATH%\mysql" -u root -p' + TrimmedRootPassword + ' StonkaDB < "' + AdminSQLPath + '" > "%TEMP%\admin_output.txt" 2>&1');
    FileContents.Add('  echo Wyświetlam wynik tworzenia administratora:');
    FileContents.Add('  type "%TEMP%\admin_output.txt"');
    
    // Dodawanie przykładowych danych jeśli istnieją
    FileContents.Add('  if exist "' + ExpandConstant('{app}\sql\Dane.sql') + '" (');
    FileContents.Add('    "%MYSQL_PATH%\mysql" -u root -p' + TrimmedRootPassword + ' StonkaDB < "' + ExpandConstant('{app}\sql\Dane.sql') + '"');
    FileContents.Add('    echo Zaimportowano przykładowe dane.');
    FileContents.Add('  )');
    
    // Zapisanie danych połączenia dla aplikacji
    FileContents.Add('  echo Zapisywanie konfiguracji połączenia dla aplikacji Stonka...');
    FileContents.Add('  (');
    FileContents.Add('    echo db.host=localhost');
    FileContents.Add('    echo db.port=3306');
    FileContents.Add('    echo db.name=StonkaDB');
    FileContents.Add('    echo db.user=' + TrimmedAppUsername);
    FileContents.Add('    echo db.password=' + TrimmedAppPassword);
    FileContents.Add('  ) > "' + ExpandConstant('{app}\config\database.properties') + '"');
    FileContents.Add(')');
    
    FileContents.Add('echo Konfiguracja MySQL zakończona.');
    FileContents.SaveToFile(MySQLConfigPath);
  finally
    FileContents.Free;
  end;
end;

// Ostrzeżenie, jeśli Java nie jest zainstalowana lub wersja jest za niska
function InitializeSetup(): Boolean;
begin
  Result := True;
  
  if not IsJavaInstalled then
  begin
    if MsgBox('Aplikacja Stonka wymaga Java 22 lub nowszej do działania. Nie wykryto Javy na tym komputerze. Czy chcesz kontynuować instalację?', mbConfirmation, MB_YESNO) = IDNO then
      Result := False;
  end
  else if not CheckJavaVersion then
  begin
    if MsgBox('Aplikacja Stonka wymaga Java 22 lub nowszej. Wykryto starszą wersję Javy. Czy chcesz kontynuować instalację?', mbConfirmation, MB_YESNO) = IDNO then
      Result := False;
  end;
  
  UseExistingMySQL := IsMySQLInstalled();
  if UseExistingMySQL then
  begin
    MsgBox('Wykryto istniejącą instalację MySQL na tym komputerze. Aplikacja Stonka spróbuje użyć tej instalacji.', mbInformation, MB_OK);
  end;
end;

// Przygotowanie instalacji
procedure InitializeWizard;
begin
  // Sprawdź czy MySQL jest zainstalowany
  UseExistingMySQL := IsMySQLInstalled();

  // Tworzenie strony konfiguracji MySQL
  MySQLPage := CreateInputQueryPage(wpSelectTasks,
    'Konfiguracja MySQL',
    'Ustawienia bazy danych dla aplikacji Stonka',
    'Wprowadź dane dostępowe do bazy danych MySQL:');
    
  // Jeśli nie ma zainstalowanego MySQL i wybrano jego instalację
  if not UseExistingMySQL then
  begin
    MySQLPage.Add('Hasło administratora MySQL (root):', True);
    MySQLPage.Add('Nazwa użytkownika dla aplikacji:', False);
    MySQLPage.Add('Hasło użytkownika dla aplikacji:', True);
    
    // Domyślne wartości
    MySQLPage.Values[0] := 'StrongP@ssw0rd123';
    MySQLPage.Values[1] := 'root';
    MySQLPage.Values[2] := 'StrongP@ssw0rd123';
  end
  else
  begin
    // Jeśli MySQL jest już zainstalowany, pytaj tylko o dane dla aplikacji
    MySQLPage.Add('Nazwa użytkownika dla aplikacji:', False);
    MySQLPage.Add('Hasło użytkownika dla aplikacji:', True);
    
    // Domyślne wartości
    MySQLPage.Values[0] := 'root';
    MySQLPage.Values[1] := 'StrongP@ssw0rd123';
  end;
  
  // Dodanie strony konfiguracji bazy danych
  DBConfigPage := CreateInputQueryPage(MySQLPage.ID,
    'Konfiguracja połączenia z bazą danych', 
    'Wprowadź dane połączenia z bazą MySQL',
    'Te ustawienia zostaną zapisane w pliku database.properties.');
    
  // Dodanie pól formularza
  DBConfigPage.Add('Host:', False);
  DBConfigPage.Add('Port:', False);
  DBConfigPage.Add('Nazwa bazy danych:', False);
  DBConfigPage.Add('Użytkownik:', False);
  DBConfigPage.Add('Hasło:', True); // True oznacza pole hasła
  
  // Ustawienie domyślnych wartości
  DBConfigPage.Values[0] := 'localhost';
  DBConfigPage.Values[1] := '3306';
  DBConfigPage.Values[2] := 'StonkaDB';
  DBConfigPage.Values[3] := 'root';
  DBConfigPage.Values[4] := '';
  
  // Dodanie przycisku testowania połączenia
  TestConnectionButton := TNewButton.Create(WizardForm);
  TestConnectionButton.Caption := 'Testuj połączenie';
  TestConnectionButton.Width := 120;
  TestConnectionButton.Height := 30;
  TestConnectionButton.OnClick := @TestButtonClick;
  TestConnectionButton.Parent := DBConfigPage.Surface;
  TestConnectionButton.Top := 160;
  TestConnectionButton.Left := 80;
  
  // Dodanie etykiety statusu połączenia
  ConnectionStatusLabel := TNewStaticText.Create(WizardForm);
  ConnectionStatusLabel.Caption := '';
  ConnectionStatusLabel.Width := 250;
  ConnectionStatusLabel.Parent := DBConfigPage.Surface;
  ConnectionStatusLabel.Top := TestConnectionButton.Top + 5;
  ConnectionStatusLabel.Left := TestConnectionButton.Left + TestConnectionButton.Width + 10;
  ConnectionStatusLabel.Hide;
  
  // Ustawienie ścieżek tymczasowych
  MySQLDownloadPath := ExpandConstant('{tmp}\{#MySQLInstallerFilename}');
  MySQLConfigPath := ExpandConstant('{tmp}\configure_mysql.bat');
end;

// Funkcja wywoływana przy przejściu do nowej strony
procedure CurPageChanged(CurPageID: Integer);
var
  TaskIndex: Integer;
begin
  // Odznacz opcję instalacji MySQL, gdy jest już zainstalowany
  if (CurPageID = wpSelectTasks) and UseExistingMySQL then
  begin
    // Bezpieczne znajdowanie i odznaczanie zadania installmysql
    for TaskIndex := 0 to WizardForm.TasksList.Items.Count - 1 do
    begin
      if Pos('installmysql', WizardForm.TasksList.ItemCaption[TaskIndex]) > 0 then
      begin
        WizardForm.TasksList.Checked[TaskIndex] := False;
        Break;
      end;
    end;
  end;
  
  // Przy wejściu na stronę konfiguracji bazy danych, wypełnij domyślne wartości
  if CurPageID = DBConfigPage.ID then
  begin
    // Ustaw domyślne wartości na podstawie poprzedniej strony lub domyślne
    if not UseExistingMySQL and WizardIsTaskSelected('installmysql') then
    begin
      // Jeśli instalujemy MySQL, użyj danych z MySQLPage
      DBConfigPage.Values[3] := Trim(MySQLAppUsername); // Użytkownik
      DBConfigPage.Values[4] := Trim(MySQLAppPassword); // Hasło
    end;
  end;
end;

// Walidacja danych MySQL i konfiguracji DB
function NextButtonClick(CurPageID: Integer): Boolean;
begin
  Result := True;
  
  // Walidacja danych MySQL
  if CurPageID = MySQLPage.ID then
  begin
    if not UseExistingMySQL and WizardIsTaskSelected('installmysql') then
    begin
      MySQLRootPassword := Trim(MySQLPage.Values[0]);  // Używamy Trim()
      MySQLAppUsername := Trim(MySQLPage.Values[1]);   // Używamy Trim()
      MySQLAppPassword := Trim(MySQLPage.Values[2]);   // Używamy Trim()
      
      if Length(MySQLRootPassword) < 8 then
      begin
        MsgBox('Hasło administratora MySQL musi mieć co najmniej 8 znaków.', mbError, MB_OK);
        Result := False;
      end;
    end
    else
    begin
      // Gdy MySQL jest już zainstalowany, indeksy są inne
      MySQLAppUsername := Trim(MySQLPage.Values[0]);  // Używamy Trim()
      MySQLAppPassword := Trim(MySQLPage.Values[1]);  // Używamy Trim()
    end;
    
    // Walidacja danych dla obu przypadków
    if Length(MySQLAppUsername) = 0 then
    begin
      MsgBox('Nazwa użytkownika aplikacji nie może być pusta.', mbError, MB_OK);
      Result := False;
    end;
    
    if Length(MySQLAppPassword) < 8 then
    begin
      MsgBox('Hasło użytkownika aplikacji musi mieć co najmniej 8 znaków.', mbError, MB_OK);
      Result := False;
    end;
  end;
  
  // Walidacja konfiguracji DB
  if CurPageID = DBConfigPage.ID then
  begin
    // Sprawdź czy wszystkie pola są wypełnione
    if (Length(Trim(DBConfigPage.Values[0])) = 0) or
       (Length(Trim(DBConfigPage.Values[1])) = 0) or
       (Length(Trim(DBConfigPage.Values[2])) = 0) or
       (Length(Trim(DBConfigPage.Values[3])) = 0) then
    begin
      MsgBox('Wszystkie pola konfiguracji bazy danych muszą być wypełnione.', mbError, MB_OK);
      Result := False;
    end;
  end;
end;

// Przygotowanie do instalacji - ten kod zostanie wykonany PO wyborze folderu instalacji
function PrepareToInstall(var NeedsRestart: Boolean): String;
begin
  Result := '';
  
  // Teraz bezpiecznie możemy tworzyć skrypty używające {app}
  if ShouldInstallMySQL() then
  begin
    CreateMySQLDownloadScript();  
  end;
end;

// Akcje podczas instalacji
procedure CurStepChanged(CurStep: TSetupStep);
var
  ResultCode: Integer;
  Host, Port, DBName, User, Password: String;
begin
  if CurStep = ssInstall then
  begin
    if ShouldInstallMySQL() then
    begin
      // Tutaj tworzymy skrypt konfiguracyjny - już po wybraniu folderu instalacji
      CreateMySQLConfigScript();
    end;
  end;
  
  if CurStep = ssPostInstall then
  begin
    // Zapisz konfigurację bazy danych
    Host := Trim(DBConfigPage.Values[0]);
    Port := Trim(DBConfigPage.Values[1]);
    DBName := Trim(DBConfigPage.Values[2]);
    User := Trim(DBConfigPage.Values[3]);
    Password := Trim(DBConfigPage.Values[4]);
    
    // Zapisz do pliku database.properties
    SaveStringToFile(ExpandConstant('{app}\config\database.properties'),
      'db.host=' + Host + #13#10 +
      'db.port=' + Port + #13#10 +
      'db.name=' + DBName + #13#10 +
      'db.user=' + User + #13#10 +
      'db.password=' + Password + #13#10,
      False);
      
    // Informacja dla użytkownika
    MsgBox('Konfiguracja bazy danych została zapisana. Aplikacja Stonka jest gotowa do użycia.', mbInformation, MB_OK);
  end;
end;