#include "GameApp.hpp"

#include <iostream>
using namespace std;

#include "../header.h"

#include "wx/xrc/xmlres.h"

#include "wxstatfuncs.hpp"

bool GameApp::OnInit()
{
	cout << "Loading..." << endl;
	SetAppName(wxT(APP_BIN_NAME));
	
	cout << "          XML Data... " << endl;
	wxXmlResource::Get()->InitAllHandlers();
	wxXmlResource::Get()->Load(wxString(statfuncs::locateGameResource("splashscreen2.xrc").c_str(), wxConvUTF8));
	cout << "          * Done!" << endl;
	
	wxInitAllImageHandlers();
	GameFrame *frame = new GameFrame(_(APP_NAME), wxDefaultPosition, wxSize(-1, -1));
	
	frame->Show(true);
	SetTopWindow(frame);
	return true;
}
