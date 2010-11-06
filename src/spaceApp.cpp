#include "spaceApp.hpp"

#include "spaceFrame.hpp"
#include <wx/xrc/xmlres.h>

#include <iostream>
using namespace std;

#include "misc/data.hpp"

IMPLEMENT_APP(SpaceApp)

bool SpaceApp::OnInit()
{
	if(!Misc::Data::initialise())
		return false;
	//SetAppName(_T("SpaceGame Editor"));
	if(!wxApp::OnInit())
		return false;

	wxXmlResource::Get()->InitAllHandlers();
	wxInitAllImageHandlers();
	SpaceFrame *frame = new SpaceFrame;
	frame->Show(true);
	SetTopWindow(frame);
	return true;
}
