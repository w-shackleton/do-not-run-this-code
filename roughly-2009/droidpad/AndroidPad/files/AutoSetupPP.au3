AutoItSetOption("TrayIconHide",1)

$handle = ""
While $handle = ""
$handle = WinGetHandle("PPJoy Joystick and Gamepad configuration utility   v0.84.6.000", "")
WEnd
WinWaitActive($handle)
ControlClick($handle, "", "[CLASS:Button; INSTANCE:4]")
Sleep(500)

$handle = ""
While $handle = ""
$handle = WinGetHandle("Configure new controller", "")
WEnd
WinWaitActive($handle)
ControlClick($handle, "", "[CLASS:ComboBox; INSTANCE:1]")
Sleep(500)
Send("{DOWN}",0)
Sleep(500)
ControlClick($handle, "", "[CLASS:ComboBox; INSTANCE:1]")
Sleep(500)

ControlClick($handle, "", "[CLASS:Button; INSTANCE:1]")
Sleep(500)

$handle = ""
While $handle = ""
$handle = WinGetHandle("PPJoy Joystick and Gamepad configuration utility   v0.84.6.000", "")
WEnd
WinActivate($handle)
ControlClick($handle, "", "[CLASS:Button; INSTANCE:1]")