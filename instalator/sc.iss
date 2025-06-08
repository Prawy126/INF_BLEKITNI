; Skrypt instalacyjny dla aplikacji Stonka
; Wygenerowany: 2025-06-08

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
; Komentuje te pliki, jeśli nie istnieją usuń średniki jeśli masz te pliki
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
Name: "{app}\logs"; Permissions: users-full; Check: not IsInstallModeAdvanced

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
Source: "C:\Users\jakub\Pliki\GIT\INF_BLEKITNI\target\dist\configure_database.bat"; DestDir: "{app}"; Flags: ignoreversion
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

[Icons]
Name: "{autoprograms}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; IconFilename: "{app}\stonka.ico"
Name: "{autoprograms}\{#MyAppName}\Konfiguracja bazy danych"; Filename: "{app}\configure_database.bat"
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; IconFilename: "{app}\stonka.ico"; Tasks: desktopicon

[Run]
; Domyślna konfiguracja, jeśli nie istnieje
Filename: "{cmd}"; Parameters: "/c if not exist ""{app}\config\config.properties"" echo db.host=localhost> ""{app}\config\config.properties"""; Flags: runhidden

; Uruchomienie po instalacji
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent
Filename: "{app}\configure_database.bat"; Description: "Skonfiguruj połączenie z bazą danych"; Flags: postinstall skipifsilent

[Code]
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
end;

// Uruchomienie konfiguracji bazy danych po instalacji
procedure CurStepChanged(CurStep: TSetupStep);
var
  ResultCode: Integer;
begin
  if CurStep = ssPostInstall then
  begin
    if MsgBox('Czy chcesz teraz skonfigurować połączenie z bazą danych?', mbConfirmation, MB_YESNO) = IDYES then
    begin
      Exec(ExpandConstant('{app}\configure_database.bat'), '', ExpandConstant('{app}'), SW_SHOW, ewWaitUntilTerminated, ResultCode);
    end;
  end;
end;