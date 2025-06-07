; Skrypt instalacyjny dla aplikacji Stonka
; Wygenerowany: 2025-06-07

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

[Icons]
Name: "{autoprograms}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; IconFilename: "{app}\stonka.ico"
Name: "{autoprograms}\{#MyAppName}\Konfiguracja bazy danych"; Filename: "{app}\configure_database.bat"
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; IconFilename: "{app}\stonka.ico"; Tasks: desktopicon

[Run]
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

// Ostrzeżenie, jeśli Java nie jest zainstalowana
function InitializeSetup(): Boolean;
begin
  Result := True;
  if not IsJavaInstalled then begin
    if MsgBox('Aplikacja Stonka wymaga Java 22 lub nowszej do działania. Nie wykryto Javy na tym komputerze. Czy chcesz kontynuować instalację?', mbConfirmation, MB_YESNO) = IDNO then
      Result := False;
  end;
end;

// Uruchomienie konfiguracji bazy danych po instalacji
procedure CurStepChanged(CurStep: TSetupStep);
var
  ResultCode: Integer; // Dodana deklaracja zmiennej
begin
  if CurStep = ssPostInstall then begin
    if MsgBox('Czy chcesz teraz skonfigurować połączenie z bazą danych?', mbConfirmation, MB_YESNO) = IDYES then begin
      Exec(ExpandConstant('{app}\configure_database.bat'), '', ExpandConstant('{app}'), SW_SHOW, ewWaitUntilTerminated, ResultCode);
    end;
  end;
end;