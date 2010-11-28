#include"spaceFrame.hpp"

BEGIN_EVENT_TABLE(SpaceFrame, wxFrame)
	EVT_MENU(SpaceFrame::ID_File_Quit, SpaceFrame::OnQuit)
	EVT_MENU(SpaceFrame::ID_Help_About, SpaceFrame::OnAbout)
END_EVENT_TABLE()

#include <iostream>
using namespace std;

#include "misc/data.hpp"

#ifdef __WXMSW__
#define _FRAME_ICON "icon.xpm"
#else
#define _FRAME_ICON "iconlarge.xpm"
#endif

SpaceFrame::SpaceFrame()
	: wxFrame(NULL, -1, _("SpaceGame Editor"), wxDefaultPosition, wxSize(640, 480))
{
	SetIcon(wxIcon(wxString(Misc::Data::getFilePath(_FRAME_ICON).c_str(), wxConvUTF8), wxBITMAP_TYPE_XPM));

	menuFile = new wxMenu;
	menuAbout = new wxMenu;

	cout << "Hello!" << endl;
	menuFile->Append(ID_File_Quit, _("E&xit"));
	menuAbout->Append(ID_Help_About, _("&About"));

	menuBar = new wxMenuBar;
	menuBar->Append(menuFile, _("&File"));
	menuBar->Append(menuAbout, _("&Help"));

//	SetMenuBar(menuBar);
	CreateStatusBar();
	SetStatusText(_("SpaceGame Editor"));

	hcontainer = new wxBoxSizer(wxHORIZONTAL);
	spacePanel = new SpacePanel(this, lmanager);
	hcontainer->Add(spacePanel, 1, wxEXPAND);

	SetSizer(hcontainer);
	SetAutoLayout(true);
}

void SpaceFrame::OnQuit(wxCommandEvent& event)
{
	Close(TRUE);
}

void SpaceFrame::OnAbout(wxCommandEvent& event)
{
	wxMessageBox(wxT("About!"));
}
