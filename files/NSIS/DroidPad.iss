; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)

AppId={{046B525C-FEC6-42A2-A637-53CC2F58100E}
AppName=DroidPad
AppVerName=DroidPad 1.2
AppPublisher=Digitalsquid
AppPublisherURL=http://digitalsquid.co.uk/droidpad/
AppSupportURL=http://digitalsquid.co.uk/droidpad/
AppUpdatesURL=http://digitalsquid.co.uk/droidpad/
DefaultDirName={pf}\DroidPad
DefaultGroupName=DroidPad
OutputBaseFilename=DroidPad_setup
SetupIconFile=..\installIcon.ico
Compression=lzma
SolidCompression=true
InfoBeforeFile=Info.rtf
RestartIfNeededByRun=false
VersionInfoVersion=1.1
VersionInfoCompany=Digitalsquid.co.uk
VersionInfoDescription=DroidPad
VersionInfoCopyright=Copyright 2010 Will Shackleton
VersionInfoProductName=DroidPad
VersionInfoProductVersion=1.1
MinVersion=0,5.01.2600
AppVersion=1.0

[Languages]
Name: en; MessagesFile: compiler:Default.isl

[Tasks]
Name: desktopicon; Description: {cm:CreateDesktopIcon}; GroupDescription: {cm:AdditionalIcons}
Name: quicklaunchicon; Description: {cm:CreateQuickLaunchIcon}; GroupDescription: {cm:AdditionalIcons}; Flags: unchecked

[Files]
Source: ..\..\bin\Release\DroidPad.exe; DestDir: {app}; Flags: ignoreversion
Source: ..\..\bin\Release\DroidPad.exe.config; DestDir: {app}; Flags: ignoreversion
Source: ..\..\bin\Release\files\*; DestDir: {app}\files; Flags: ignoreversion recursesubdirs createallsubdirs
; NOTE: Don't use "Flags: ignoreversion" on any shared system files
Source: ..\iconred.ico; DestDir: {app}

[Icons]
Name: {group}\DroidPad; Filename: {app}\DroidPad.exe
Name: {group}\Setup DroidPad; Filename: {app}\DroidPad.exe; Parameters: setup
;Name: {group}\{cm:ProgramOnTheWeb,DroidPad}; Filename: http://digitalsquid.co.uk/droidpad/
Name: {group}\{cm:UninstallProgram,DroidPad}; Filename: {uninstallexe}; IconFilename: {app}\iconred.ico
Name: {commondesktop}\DroidPad; Filename: {app}\DroidPad.exe; Tasks: desktopicon
Name: {userappdata}\Microsoft\Internet Explorer\Quick Launch\DroidPad; Filename: {app}\DroidPad.exe; Tasks: quicklaunchicon

[Run]
Filename: {app}\DroidPad.exe; Parameters: autosetup; StatusMsg: Starting DroidPad setup...

[UninstallDelete]
Name: {app}/DroidPad_update.exe; Type: files; Tasks: ; Languages: 
Name: {app}/setupCompleted.conf; Type: files
[UninstallRun]
Filename: {app}\DroidPad.exe; Parameters: uinstproc
[CustomMessages]
InstallDotNet=You must install .NET Framework 3.5 to use DroidPad. Click OK to download the .NET Framework
[Code]
const
  dotnet35URL = 'http://download.microsoft.com/download/7/0/3/703455ee-a747-4cc8-bd3e-98a615c3aedb/dotNetFx35setup.exe';

function InitializeSetup(): Boolean;
var
  msgRes : integer;
  errCode : integer;

begin
  Result := true;
  // Check for required dotnetfx 3.5 installation
  if (not RegKeyExists(HKLM, 'SOFTWARE\Microsoft\NET Framework Setup\NDP\v3.5')) then begin
    msgRes := MsgBox(CustomMessage('InstallDotNet'), mbError, MB_OKCANCEL);
    if(msgRes = 1) then begin
      ShellExec('Open', dotnet35URL, '', '', SW_SHOW, ewNoWait, errCode);
    end;
    Abort();
  end;
end;
