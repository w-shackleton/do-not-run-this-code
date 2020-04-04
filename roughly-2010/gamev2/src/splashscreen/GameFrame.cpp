#include "GameFrame.hpp"
#include <iostream>
using namespace std;

#include <wx/xrc/xmlres.h>

#include "wxstatfuncs.hpp"
#include "main.hpp"
#include "PrefDialog.hpp"

BEGIN_EVENT_TABLE(GameFrame, wxFrame)
	EVT_BUTTON(XRCID("buttonQuit"), GameFrame::onButtonQuitPressed)
	EVT_BUTTON(XRCID("buttonStart"), GameFrame::onButtonStartPressed)
	EVT_BUTTON(XRCID("buttonOptions"), GameFrame::onButtonOptionsPressed)
END_EVENT_TABLE()

GameFrame::GameFrame(const wxString& title, const wxPoint& pos, const wxSize& size)
: wxFrame( NULL, -1, title, pos, size, wxMINIMIZE_BOX | wxSYSTEM_MENU | wxCAPTION | wxCLOSE_BOX | wxCLIP_CHILDREN)
{
	cout << "          Splash screen... " << endl;
	splashPanel = wxXmlResource::Get()->LoadPanel(this, wxT("splashPanel"));
	cout << "          * Done!" << endl;
	
	cout << "          Splash image... " << endl;
	splashLogo = XRCCTRL(*this, "splashLogo", wxStaticBitmap);
	splashLogo->SetBitmap(wxBitmap(wxString(statfuncs::locateGameResource("pixmaps/logo.png").c_str(), wxConvUTF8), wxBITMAP_TYPE_PNG));
	cout << "          * Done!" << endl;
	
	Fit();
	
	cout << "Loaded." << endl;
}

GameFrame::~GameFrame()
{
	
}

void GameFrame::onButtonStartPressed(wxCommandEvent& WXUNUSED(event))
{
	splashReason = REASON_START;
	Close(TRUE);
}
void GameFrame::onButtonOptionsPressed(wxCommandEvent& WXUNUSED(event))
{
	cout << "Options" << endl;
	PrefDialog prefs(_("Preferences"));
	prefs.ShowModal();
}
void GameFrame::onButtonQuitPressed(wxCommandEvent& WXUNUSED(event))
{
	splashReason = REASON_QUIT;
	Close(TRUE);
}

