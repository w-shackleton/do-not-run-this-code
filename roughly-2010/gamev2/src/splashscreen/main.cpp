#include "main.hpp"

#include "GameApp.hpp"

// Taken from wx/app.h:687
IMPLEMENT_APP_NO_MAIN(GameApp)
IMPLEMENT_WX_THEME_SUPPORT

#include <iostream>
using namespace std;

int splashMain(int argc, char **argv)
{
	return wxEntry(argc, argv);
}

int splashReason = REASON_QUIT;
