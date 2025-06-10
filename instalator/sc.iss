; Skrypt instalacyjny dla aplikacji Stonka

#define MyAppName "Stonka"
#define MyAppVersion "1.0"
#define MyAppPublisher "BŁĘKITNI"
#define MyAppURL "https://www.example.com/"
#define MyAppExeName "stonka.exe"

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

LicenseFile=C:\Users\jakub\Pliki\INF_BLEKITNI\license.txt
InfoBeforeFile=C:\Users\jakub\Pliki\INF_BLEKITNI\przed.txt
InfoAfterFile=C:\Users\jakub\Pliki\INF_BLEKITNI\po.txt
OutputDir=C:\Users\jakub\Pliki\INF_BLEKITNI\instalator
OutputBaseFilename=stonka-setup
SetupIconFile=C:\Users\jakub\Pliki\GIT\INF_BLEKITNI\src\main\resources\distribution\stonka.ico
Compression=lzma
SolidCompression=yes
WizardStyle=modern

; Minimalne wymagania
MinVersion=10.0
PrivilegesRequired=admin

; Dodatkowe opcje
DisableWelcomePage=no
DisableDirPage=no
ShowTasksTreeLines=yes

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "polish"; MessagesFile: "compiler:Languages\Polish.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Dirs]
; Katalogi w lokalizacji aplikacji
Name: "{app}\config"; Permissions: users-full
Name: "{app}\resources"; Permissions: users-full
Name: "{app}\sql"; Permissions: users-full
Name: "{app}\lib"; Permissions: users-full
Name: "{app}\logs"; Permissions: users-full

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
Source: "C:\Users\jakub\Pliki\INF_BLEKITNI\CODE\target\dist\stonka.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\jakub\Pliki\INF_BLEKITNI\CODE\target\dist\stonka.ico"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\jakub\Pliki\INF_BLEKITNI\CODE\target\dist\run.bat"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\jakub\Pliki\INF_BLEKITNI\CODE\target\dist\Projekt-1.0-SNAPSHOT.jar"; DestDir: "{app}"; Flags: ignoreversion skipifsourcedoesntexist

; Środowisko uruchomieniowe Java (JRE)
Source: "C:\Users\jakub\Pliki\INF_BLEKITNI\CODE\target\dist\jre\*"; DestDir: "{app}\jre"; Flags: ignoreversion recursesubdirs createallsubdirs

; Biblioteki (wszystkie pliki JAR)
Source: "C:\Users\jakub\Pliki\INF_BLEKITNI\CODE\target\dist\lib\*"; DestDir: "{app}\lib"; Flags: ignoreversion recursesubdirs createallsubdirs

; Pliki konfiguracyjne
Source: "C:\Users\jakub\Pliki\INF_BLEKITNI\CODE\target\dist\config\*"; DestDir: "{app}\config"; Flags: ignoreversion recursesubdirs createallsubdirs

; Skrypty SQL
Source: "C:\Users\jakub\Pliki\INF_BLEKITNI\CODE\target\dist\sql\*"; DestDir: "{app}\sql"; Flags: ignoreversion recursesubdirs createallsubdirs

; Pliki zasobów
Source: "C:\Users\jakub\Pliki\INF_BLEKITNI\CODE\src\main\resources\*"; DestDir: "{app}\resources"; Flags: ignoreversion recursesubdirs createallsubdirs skipifsourcedoesntexist
Source: "C:\Users\jakub\Pliki\INF_BLEKITNI\CODE\src\main\resources\images\*"; DestDir: "{app}\resources\images"; Flags: ignoreversion recursesubdirs createallsubdirs skipifsourcedoesntexist
Source: "C:\Users\jakub\Pliki\INF_BLEKITNI\CODE\src\main\resources\templates\*"; DestDir: "{app}\resources\templates"; Flags: ignoreversion recursesubdirs createallsubdirs skipifsourcedoesntexist

[Icons]
Name: "{autoprograms}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; IconFilename: "{app}\stonka.ico"
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; IconFilename: "{app}\stonka.ico"; Tasks: desktopicon

[Run]
; Domyślna konfiguracja, jeśli nie istnieje
Filename: "{cmd}"; Parameters: "/c if not exist ""{app}\config\database.properties"" echo db.host=localhost> ""{app}\config\database.properties"""; Flags: runhidden

; Uruchomienie po instalacji
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent

[Code]
var
  DBConfigPage: TInputQueryWizardPage;
  EmailConfigPage: TInputQueryWizardPage;

// Przygotowanie instalacji
procedure InitializeWizard;
begin
  // Strona konfiguracji bazy danych
  DBConfigPage := CreateInputQueryPage(wpSelectTasks,
    'Konfiguracja połączenia z bazą danych', 
    'Wprowadź dane połączenia z bazą MySQL',
    'Te ustawienia zostaną zapisane w pliku database.properties.');
    
  // Dodanie pól formularza do konfiguracji bazy danych
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
  
  // Tworzenie strony konfiguracji email
  EmailConfigPage := CreateInputQueryPage(DBConfigPage.ID,
    'Konfiguracja wysyłania wiadomości email',
    'Wprowadź dane konta email używanego do wysyłania powiadomień',
    'Te ustawienia zostaną zapisane w pliku email.properties.');

  // Dodanie pól formularza do konfiguracji email
  EmailConfigPage.Add('Adres email:', False);
  EmailConfigPage.Add('Hasło do konta email:', True); // True oznacza pole hasła

  // Domyślne wartości
  EmailConfigPage.Values[0] := 'powiadomienia@twojafirma.pl';
  EmailConfigPage.Values[1] := '';
end;

// Walidacja konfiguracji
function NextButtonClick(CurPageID: Integer): Boolean;
begin
  Result := True;
  
  // Walidacja konfiguracji DB
  if (CurPageID = DBConfigPage.ID) then
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
  
  // Walidacja konfiguracji email
  if (CurPageID = EmailConfigPage.ID) then
  begin
    // Sprawdź czy wszystkie pola są wypełnione
    if (Length(Trim(EmailConfigPage.Values[0])) = 0) or
       (Length(Trim(EmailConfigPage.Values[1])) = 0) then
    begin
      MsgBox('Adres email i hasło muszą być wypełnione.', mbError, MB_OK);
      Result := False;
      Exit;
    end;
    
    // Walidacja formatu adresu email
    if (Pos('@', EmailConfigPage.Values[0]) <= 1) or
       (Pos('.', EmailConfigPage.Values[0]) <= 3) or
       (Pos('.', EmailConfigPage.Values[0]) = Length(EmailConfigPage.Values[0])) then
    begin
      MsgBox('Podany adres email ma nieprawidłowy format.', mbError, MB_OK);
      Result := False;
      Exit;
    end;
  end;
end;

// Akcje po instalacji
procedure CurStepChanged(CurStep: TSetupStep);
var
  Host, Port, DBName, User, Password: String;
  EmailAddr, EmailPass: String;
begin
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
      
    // Zapisz konfigurację email
    EmailAddr := Trim(EmailConfigPage.Values[0]);
    EmailPass := Trim(EmailConfigPage.Values[1]);
    
    // Zapisz do pliku email.properties
    SaveStringToFile(ExpandConstant('{app}\config\email.properties'),
      'email.address=' + EmailAddr + #13#10 +
      'email.password=' + EmailPass + #13#10,
      False);
      
    // Stwórz też plik PASS.txt dla kompatybilności wstecznej
    SaveStringToFile(ExpandConstant('{app}\PASS.txt'),
      EmailAddr + #13#10 +
      EmailPass + #13#10,
      False);
      
    // Informacja dla użytkownika
    MsgBox('Konfiguracja bazy danych i email została zapisana. Aplikacja Stonka jest gotowa do użycia.', mbInformation, MB_OK);
  end;
end;