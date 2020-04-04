#include "splashscreen/main.hpp"
#include "engine/EngineMain.hpp"

#include <iostream>
using namespace std;

int main (int argc, char **argv)
{
	splashMain(argc, argv);
	switch(splashReason) // This switch checks which button was pressed in wxWidgets
	{
	case REASON_START:
		cout << "Splash screen finished, starting game..." << endl;
		EngineRun();
		break;
	case REASON_QUIT:
		cout << "Quitting..." << endl;
		break;
	}
}
