#include "spaceApp.hpp"

#include "spaceFrame.hpp"
#include <wx/xrc/xmlres.h>
#include <wx/log.h>

#include <iostream>
using namespace std;

#include "misc/data.hpp"

#include <stdlib.h>

IMPLEMENT_APP(SpaceApp)

bool SpaceApp::OnInit()
{
#ifndef __WXMSW__
	setenv("UBUNTU_MENUPROXY", "0", 1); // Ubuntu 10.10 fix
#endif

	if(!wxApp::OnInit())
		return false;
	wxLog *logger = new wxLogStream(&cerr);
	wxLog::SetActiveTarget(logger);
	SetAppName(_T("spaceedit"));
//	wxXmlResource::Get()->InitAllHandlers();
	wxInitAllImageHandlers();

	if(!Misc::Data::initialise())
	{
		wxMessageDialog(NULL, _("Could not find application data,\npossibly because application was installed incorrectly?"), _("Error finding data"), wxOK | wxICON_EXCLAMATION).ShowModal();
		return false;
	}
	SpaceFrame *frame = new SpaceFrame;
	frame->Show(true);
	SetTopWindow(frame);
	return true;
}
