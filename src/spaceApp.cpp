#include "spaceApp.hpp"

#include "spaceFrame.hpp"
#include <wx/xrc/xmlres.h>

#include <iostream>
using namespace std;

#include "misc/data.hpp"

#include <stdlib.h>

IMPLEMENT_APP(SpaceApp)

bool SpaceApp::OnInit()
{
	setenv("UBUNTU_MENUPROXY", "0", 1); // Ubuntu 10.10 fix
	if(!Misc::Data::initialise())
	{
		wxMessageDialog(NULL, _("Could not find application data,\npossibly because application was installed incorrectly?"), _("Error finding data"), wxOK | wxICON_EXCLAMATION).ShowModal();
		return false;
	}
	//SetAppName(_T("SpaceGame Editor"));
	if(!wxApp::OnInit())
		return false;

//	wxXmlResource::Get()->InitAllHandlers();
	wxInitAllImageHandlers();
	SpaceFrame *frame = new SpaceFrame;
	frame->Show(true);
	SetTopWindow(frame);
	return true;
}
